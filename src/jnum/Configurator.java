/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     jnum is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     jnum is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with jnum.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/


package jnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsToolkit;
import jnum.io.LineParser;
import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Range2D;
import jnum.math.Vector2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

/**
 * 
 * A configuration engine for programs that supports hierarchical configurations and conditional settings.
 * 
 * 
 * Specific configuration options are nodes/endpoints on an option tree. Each node/endpoint can have a string value,
 * and may have further branches of related sub-options stemming from it. It also can host its own list of conditional
 * settings.
 * 
 * 
 * See README.syntax in CRUSH (or on the CRUSH website) for details on the option syntax, and features. 
 *
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class Configurator implements Serializable, Cloneable, Copiable<Configurator>, FitsHeaderEditing {


    private static final long serialVersionUID = 5040150005828567005L;

    private Configurator root;

    private String value;
    public int serialNo;

    public boolean isEnabled = false;
    public boolean isLocked = false;
    public boolean wasUsed = false;


    public Hashtable<String, Configurator> branches = new Hashtable<>();
    public Hashtable<String, Vector<String>> conditionals = new Hashtable<>();

    private static int counter = 0;	

    public static boolean silent = false;
    public static boolean verbose = false;
    public static boolean details = false;


    public Configurator() { root = this; }


    public Configurator(Configurator root) { this.root = root; }

    @Override
    public Configurator clone() {
        try { return (Configurator) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }


    @Override
    @SuppressWarnings("unchecked")
    public Configurator copy() {
        Configurator copy = clone();

        copy.branches = new Hashtable<>();
        for(String key : branches.keySet()) copy.branches.put(key, branches.get(key).copy());

        copy.conditionals = new Hashtable<>();
        for(String key : conditionals.keySet()) copy.conditionals.put(key, (Vector<String>) conditionals.get(key).clone());

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof Configurator)) return false;
        Configurator c = (Configurator) o;
        return Util.equals(c.value, value);
    }

    /**
     * Resets this configuration branch back to its default state: disabled, unlocked, no value set, no branches, no conditionals. 
     */
    public void clear() {
        branches.clear();
        conditionals.clear();
        value = null;
        serialNo = 0;
        isEnabled = isLocked = wasUsed = false;
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }


    /**
     * Checks if the option is enabled and it's value matches the argument in a case insensitive comparison.
     * 
     * 
     * @param value     The string value to be checked for.
     * @return          <code>true</code> if the option is enabled and its value matches the argument in a case-insensitive comparison.
     *                  Otherwise, <code>false</code>.
     *                  
     * @see #hasOption(String)
     */
    public boolean is(String value) {
        if(!isEnabled) return false;
        return value.equalsIgnoreCase(this.value);
    }

    @Override
    public String toString() {
        if(value == null) return "<none>";
        return value;		
    }

    /**
     * Parses a list or confifuration entries (lines) in order.
     * 
     * 
     * @param lines     The ordered list of configuration lines
     * @return          A list of parse exceptions that occurred along the way.
     * 
     * @see #parse(String)
     */
    public List<Exception> parseAll(List<String> lines) {
        ArrayList<Exception> exceptions = new ArrayList<>();

        for(String line : lines) {
            try { parse(line); }
            catch(Exception e) { exceptions.add(e); }
        }

        return exceptions.isEmpty() ? null : exceptions;
    }

    
    /**
     * Sets an option that is encapsulated by the corresponding single line of configuration entry such as would be
     * found in a configuration file.
     * 
     * 
     * It works just like {@link #parse(String)} but instead of throwing a {@link LockedException}, it simply returns 
     * <code>true</code> or <code>false</code> depending whether the option was successfully set, or else
     * blocked by an existing lock.
     * 
     * The argument is expected to be a &lt;key&gt;[=][&lt;value&gt;] pair with the separator being any number of '=' characters or
     * white spaces following the the &lt;key&gt; argument. E.g.
     * 
     * <pre> 
     *  key1=2.0
     *  key1.subkey = A string value
     *  key2 1,2,A,B
     * </pre>
     *
     * 
     * A conditional construct (or a chain of conditionals) may precede a single key/value setting. E.g.:
     * 
     * <pre>
     *   [condition] key1 = 3.0
     *   [cond1] key2.[cond2] mypath = /home/johndoe
     * </pre>
     * 
     * 
     * @param line  The option specification as a &lt;key&gt;[=][&lt;value&gt;]. E.g. "faint" or "datapath=/home/data/".
     * @return      <code>true</code> if the option was successfully applied, or <code>false</code> if an existing lock on 
     *              &lt;key&gt; prevented setting a new value.
     *      
     * @see #parse(String)
     */
    public boolean setOption(String line) {
        try { parse(line); }
        catch(LockedException e) { return false; }
        return true;
    }


    /**
     * Parses a single configuration line/entry which as a <code>key[=]value</code> pair, possibly preceded by a conditional.
     * 
     * @param line              The configuration as a signle line of input, such as in a config file.
     * @throws LockedException  If the configuration could not be changes as requested due to an existing lock.
     * 
     * @see #setOption(String)
     */
    public void parse(String line) throws LockedException {
        Entry entry = new Entry(line);

        try { process(entry.key, entry.value); }
        catch(LockedException e) {}
    }


    private String unalias(String key) {
        String branchName = getBranchName(key);
        String unaliased = branchName;

        // Check if the requested key branch is aliased. If so, process as such...
        if(containsExact("alias." + branchName)) {
            Configurator alias = exactOption("alias." + branchName);
            if(alias.isEnabled) {
                unaliased = alias.value;
                if(details) Util.detail(this, "<a> '" + branchName + "' -> '" + unaliased + "'");
            }
        }

        if(key.length() != branchName.length()) unaliased += getRemainder(key, branchName.length());

        return unaliased;
    }


    private String unaliasedKey(String key) {
        key = unalias(key.toLowerCase());
        int pos = 0;
        for(; pos<key.length(); pos++) {
            switch(key.charAt(pos)) {
            case ' ':
            case '\t':
            case '=':
            case ':': return key.substring(0, pos);
            }
        }
        return key;
    }


    private String resolve(String argument, String marker, String endmarker) {
        int last = 0;

        // If these is nothing to resolve, just return the argument as is...
        if(!argument.contains(marker)) return argument;

        // Now for the hard part...
        StringBuffer resolved = new StringBuffer();

        for(;;) {
            if(last >= argument.length()) break;
            int i = argument.indexOf(marker, last);
            if(i < 0) {
                resolved.append(argument, last, argument.length());
                break;
            }				
            resolved.append(argument, last, i);

            int from = i + marker.length();
            int to = argument.indexOf(endmarker, from);

            if(to < 0) {
                resolved.append(argument, last, argument.length());
                break;
            }
            else if (to > from) {
                String key = argument.substring(from, to);   
                String property = getProperty(key, marker);

                if(property != null) resolved.append(property);
                else resolved.append(argument, i, to + endmarker.length());
            }
            last = to + endmarker.length();			
        }

        return new String(resolved);
    }


    private String getProperty(String name, String marker) {
        if(marker.charAt(0) != '{') return null;
        char c = marker.charAt(1);

        switch(c) {
        case '?' :
        case '&' :
            return getProperty(name.toLowerCase());
        case '@' :
            return System.getenv(name);
        case '#' :
            return System.getProperty(name);
        default : return null;
        }
    }

    /**
     * Similar to <code>option(key).getValue()</code> except that it will not result in a {@link NullPointerException} if the
     * named option does not exists in the branches of this <code>Configurator</code> object. Instead, it will simply return
     * <code>null</code> for names not (yet) defined.
     * 
     * 
     * @param key   The option key (or alias) under the configuration branch represented by this object whose value to retrieve. 
     * @return      The value of the specified option under this configuration branch, or <code>null</code> if the
     *              specified key has not been set, or is not enabled, or has no value associated to it.
     *              
     * @see #getValue()
     */
    public String getProperty(String key) {
        return hasOption(key) ? option(key).getValue() : null;		
    }

    /**
     * Same as {@link #process(String, String)} except that it does not throw a {@link LockedException} but rather it
     * returns a <code>boolean</code> indicator whether or not the arguments were successfully processed.
     * 
     * @param key       The option key to set.
     * @param argument  The value associated with the above key.
     * 
     * @return          <code>true</code> if the option was successfully set (added or replaced), or <code>false</code>
     *                  if the option was not set due to an existing lock.
     *                  
     * @see #process(String, String)
     */
    public boolean processSilent(String key, String argument) {
        try { process(key, argument); }
        catch(LockedException e) { return false; }
        return true;
    }


    /**
     * Adds or replaces a key/value pair in this configuration branch.
     * 
     * @param key       The option key to set.
     * @param argument  The value associated with the above key.
     * 
     * @throws LockedException      If the specified key was locked preventing changes to it.               
     *                  
     * @see #processSilent(String, String)
     */
    public void process(String key, String argument) throws LockedException {	
        String substitute = unalias(key);

        if(!key.equals(substitute)) {
            key = new StringTokenizer(substitute.toLowerCase(), " \t=:").nextToken();
            if(substitute.length() > key.length()) argument = substitute.substring(key.length()+1) + argument;
            // TODO uncomment to support compound aliasing...
            // process(key, argument);
            // return;
        }

        argument = resolve(argument, "{&", "}"); // Resolve static references
        argument = resolve(argument, "{@", "}"); // Resolve environment variables.
        argument = resolve(argument, "{#", "}"); // Resolve Java properties.

        if(key.equals("forget")) for(String name : getList(argument)) forget(name);
        else if(key.equals("recall")) for(String name : getList(argument)) recall(name);
        else if(key.equals("enable")) for(String name : getList(argument)) forget(name);
        else if(key.equals("disable")) for(String name : getList(argument)) recall(name);
        else if(key.equals("blacklist")) {
            if(argument.length() == 0) pollBlacklist(null);
            for(String name : getList(argument)) blacklist(name);
        }
        else if(key.equals("whitelist")) for(String name : getList(argument)) whitelist(name);
        else if(key.equals("remove")) for(String name : getList(argument)) remove(name);
        else if(key.equals("restore")) for(String name : getList(argument)) restore(name);
        else if(key.equals("replace")) for(String name : getList(argument)) restore(name);
        else if(key.equals("config")) {
            try { readConfig(Util.getSystemPath(argument)); }
            catch(IOException e) { Util.warning(this, "Configuration file '" + argument + "' not found."); }
        }
        else if(key.equals("poll")) {
            poll(argument.length() > 0 ? unaliasedKey(argument) : null);
        }
        else if(key.equals("conditions")) {
            pollConditions(argument.length() > 0 ? argument : null);
        }
        else if(key.equals("echo")) {
            System.out.println(resolve(argument, "{?", "}"));
        }
        else {
            String branchName = getBranchName(key);
            if(details) Util.detail(this, "<.> " + branchName);

            if(branchName.equals("*")) {
                for(String name : new ArrayList<>(branches.keySet())) process(name + key.substring(1), argument);				
            }	
            else if(branchName.startsWith("[")) {
                String condition = branchName.substring(1, branchName.indexOf(']'));
                String setting = key.substring(condition.length() + 2).trim() + " " + argument;
                addCondition(condition, setting);
            }
            else if(branchName.equals("lock")) lock(argument);
            else if(branchName.equals("relock")) relock(argument);
            else if(branchName.equals("unlock")) unlock();
            else set(branchName, key, argument);
        }
    }


    private void set(String branchName, String key, String argument) throws LockedException {
        activateCondition(key, argument);
        Configurator branch = branches.containsKey(branchName) ? branches.get(branchName) : new Configurator(root);
        if(key.length() == branchName.length()) {
            if(branch.isLocked) throw new LockedException("Cannot change option '" + key + "'");
            if(details) Util.detail(this, "<=> " + argument);
            branch.value = argument;
            branch.isEnabled = true;
            branch.serialNo = counter++; // Update the serial index for the given key...
        }
        else branch.process(getRemainder(key, branchName.length() + 1), argument);
        branches.put(branchName, branch);		
    }


    private void addCondition(String condition, String setting) {
        if(isSatisfied(condition)) setOption(setting); 

        else {
            Vector<String> list = conditionals.containsKey(condition) ? conditionals.get(condition) : new Vector<>();
            list.add(setting.trim());

            // Remove leading spaces and replace assignments and other spaces with a single ?
            StringBuffer canonized = new StringBuffer(condition.length());
            boolean leadingSpace = true;
            boolean substituted = false;

            for(int i=0; i<condition.length(); i++) {
                char c = condition.charAt(i);

                if(c == ' ' || c == '\t') {
                    if(leadingSpace) continue;

                    if(!substituted) canonized.append('?');
                    substituted = true;
                }		
                else if(c == '=') {
                    if(!substituted) canonized.append('?');
                    substituted = true;
                }
                else {
                    canonized.append(c);
                    substituted = false;
                }

                leadingSpace = false;
            }

            conditionals.put(new String(canonized), list);
        }
    }


    private boolean isSatisfied(String condition) {
        // If the conditional key is already defined, then simply parse the argument of the condition
        if(condition.contains("?")) {
            StringTokenizer pair = new StringTokenizer(condition, "?");
            String conditionKey = pair.nextToken().toLowerCase();
            if(hasOption(conditionKey)) if(option(conditionKey).is(pair.nextToken())) return true;
        }
        else if(hasOption(condition.toLowerCase())) return true;

        return false;
    }


    private List<String> getList(String argument) {
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(argument, " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken());
        return list;
    }


    /**
     * Activates a condition <code>key</code> with the associated <code>value</code>. As a result conditional settings
     * on both <code>[key]</code> or <code>[key?value]</code> will be activated. (The former is a condition on the
     * existence of <code>key</code>, while the second is a condition restricted to <code>key</code> being set to
     * a specific <code>value</code>.
     * 
     * 
     * @param key       The condition key that is activated
     * @param value     The value with which <code>key</code> is activated.
     * 
     * @see #activateCondition(String)
     */
    private void activateCondition(String key, String value) {
        activateCondition(key);
        activateCondition(key + "?" + value.toLowerCase());
    }

    /**
     * Activates a condition. Any conditional settings defined so far will be activated if the body of their
     * conditionals (the String literal between the square brackets) is matched to the <code>expression</code> argument.
     * The match has to be exact (case sensitive) for an option to be activated as a result.
     * 
     * @param expression    The literal conditional expression that has to be matched for an exiting conditional
     *                      option to get activated.
     */
    public void activateCondition(String expression) {
        if(!conditionals.containsKey(expression)) return;

        if(details) Util.detail(this, "[c] " + expression + " > " + conditionals.get(expression));
        parseAll(conditionals.get(expression));
    }

    /**
     * Like {@link #forget(String)} but without throwing a {@link LockedException}. Instead, it returns a
     * <code>boolean</code> to indicate whether the operation was successful or not.
     * 
     * @param key   The option <code>key</code> under this configuration branch to unset.
     * 
     * @return      <code>true</code> if the specified option was successfully unset, or <code>false</code> if
     *              an existing lock prevented the operation.
     *              
     * @see #forget(String)
     */
    public boolean forgetSilent(String key) {
        try { forget(key); }
        catch(LockedException e) { return false; }
        return true;
    }

    /**
     * Forgets (unsets) the specified option under this configuration branch. The forgotten options can be
     * recalled to its prior state as long as no new value is set for it.
     * 
     * @param arg   The option <code>key</code> under this configuration branch to unset.
     *              
     * @see #forgetSilent(String)
     * @see #recall(String)
     */
    public void forget(String arg) throws LockedException {

        if(arg.equals("blacklist")) {
            List<String> blacklist = getBlacklist();
            for(String key : blacklist) whitelist(key);
            return;
        }

        else if(arg.equals("conditions")) {
            conditionals.clear();
            for(String name : branches.keySet()) branches.get(name).forget(arg);
            return;
        }

        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            for(String name : new ArrayList<>(branches.keySet())) forget(name + getRemainder(arg, 1));
        }	
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) forget(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.forget(getRemainder(arg, branchName.length() + 1));
                else {
                    if(branch.isLocked) throw new LockedException("Cannot forget option '" + branch + "'");
                    branch.isEnabled = false;
                }
            }
        }
    }

    
    public void recall(String arg) throws LockedException {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            for(String name : new ArrayList<>(branches.keySet())) recall(name + getRemainder(arg, 1));
        }	
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) recall(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.forget(getRemainder(arg, branchName.length() + 1));
                else {
                    Configurator option = branches.get(key);
                    if(option.isLocked) throw new LockedException("Cannot recall option '" + key + "'");
                    option.isEnabled = true;
                    option.serialNo = counter++;
                    activateCondition(arg, option.value);
                }
            }
        }
    }


    public void remove(String arg) throws LockedException {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            for(String name : new ArrayList<>(branches.keySet())) remove(name + getRemainder(arg, 1));
        }
        else if(branchName.startsWith("[") && branchName.endsWith("]")) {
            branchName = branchName.substring(1, branchName.length()-1).trim();
            for(String condition : new ArrayList<>(conditionals.keySet())) if(condition.startsWith(branchName)) conditionals.remove(condition);
        }
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) remove(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.remove(getRemainder(arg, branchName.length() + 1));
                // Do not remove the removed key itself...
                else if(key.equals("removed")) return;
                else {
                    if(branch.isLocked) throw new LockedException("Cannot remove branch '" + key + "'");
                    if(details) Util.detail(this, "<rm> " + key); 
                    getRemoved().branches.put(key, branches.remove(key));
                }
            }
        }
    }


    public void purge(String arg) {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            for(String name : new ArrayList<>(branches.keySet())) purge(name + getRemainder(arg, 1));
        }
        else if(branchName.startsWith("[") && branchName.endsWith("]")) {
            branchName = branchName.substring(1, branchName.length()-1).trim();
            for(String condition : new ArrayList<>(conditionals.keySet())) if(condition.startsWith(branchName)) conditionals.remove(condition);
        }
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) purge(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.purge(getRemainder(arg, branchName.length() + 1));
                else {
                    if(details) Util.detail(this, "<pg> " + key); 
                    branches.remove(key);
                }
            }
        }

    }


    public Configurator getRemoved() {
        if(!branches.containsKey("removed")) branches.put("removed", new Configurator(root));
        return branches.get("removed");
    }



    public void restore(String arg) {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            if(arg.length() == 1) for(String name : getRemoved().branches.keySet()) restore(name);
            else for(String name : new ArrayList<>(branches.keySet())) restore(name + getRemainder(arg, 1));
        }
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) restore(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.restore(getRemainder(arg, branchName.length() + 1));
                else {
                    Hashtable<String, Configurator> removedBranches = getRemoved().branches;

                    if(!removedBranches.containsKey(key)) return;			
                    if(details) Util.detail(this, "<r> " + key);

                    Configurator removedBranch = removedBranches.remove(key);			
                    branches.put(key, removedBranch);

                    if(removedBranches.isEmpty()) branches.remove("removed");

                    if(branch.isBlacklisted()) {
                        try { removedBranch.blacklist(); }
                        catch(LockedException e) { 
                            // TODO 
                        } 
                    }
                }
            }
        }
    }



    public void blacklist(String arg) throws LockedException {		
        String branchName = getBranchName(arg);
        String key = unaliasedKey(branchName);

        if(key.contains(".")) blacklist(key + getRemainder(arg, branchName.length()));
        else {
            if(!branches.containsKey(key)) branches.put(key, new Configurator(root));
            Configurator branch = branches.get(key);
            if(arg.length() != branchName.length()) branch.blacklist(getRemainder(arg, branchName.length() + 1));
            else {
                if(details) Util.detail(this, "<b> " + key);
                branch.blacklist();
            }
        }
    }	


    public void whitelist(String arg) throws LockedException {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) 
            for(String branch : branches.keySet()) {
                try { whitelist(branch + getRemainder(arg, 1)); }
                catch(LockedException e) {
                    // TODO
                }
            }
        else {
            String key = unaliasedKey(branchName);
            if(key.contains(".")) whitelist(key + getRemainder(arg, branchName.length()));
            else if(branches.containsKey(key)) { 
                Configurator branch = branches.get(key);
                if(arg.length() != branchName.length()) branch.whitelist(getRemainder(arg, branchName.length() + 1));
                else {
                    if(details) Util.detail(this, "<w> " + key);
                    branch.whitelist();
                }
            }
        }
    }


    public boolean isBlacklisted() {
        return isLocked & !isEnabled;
    }


    public void blacklist() throws LockedException {
        if(isLocked) if(!isBlacklisted()) throw new LockedException("Cannot blacklist locked option.");
        isEnabled = false;
        isLocked = true;
    }


    public void whitelist() throws LockedException {
        if(isLocked) if(!isBlacklisted()) throw new LockedException("Cannot whitelist locked option.");
        isLocked = false;
    }


    public void relock(String argument) {
        if(!isBlacklisted()) {
            value = argument;
            isLocked = true;
        }
    }


    public void lock(String argument) {
        if(isBlacklisted()) return;
        if(!argument.isEmpty()) if(!isLocked) value = argument;
        isLocked = true;
    }


    public void unlock() {
        if(!isBlacklisted()) isLocked = false;
    }


    public boolean isBlacklisted(String arg) {
        String branchName = getBranchName(arg);
        String key = unaliasedKey(branchName);
        if(key.contains(".")) return isBlacklisted(key + getRemainder(arg, branchName.length()));
        else if(branches.containsKey(key)) { 
            Configurator branch = branches.get(key);
            if(arg.length() != branchName.length()) return branch.isBlacklisted(getRemainder(arg, branchName.length() + 1));
            return branch.isBlacklisted();
        }
        else return false;
    }

    // Looks for first period outside of square brackets (used for conditions)...
    private String getBranchName(String key) {
        int i=0;
        int open = 0;
        for(; i<key.length(); i++) {
            char c = key.charAt(i);
            if(c == '[') open++;
            else if(c == ']') open--;
            else if(open == 0 && c == '.') break;
        }

        if(i < key.length()) return key.substring(0, i);
        return key;
    }


    private String getRemainder(String key, int from) {
        if(key.length() <= from) return "";
        return key.substring(from);	
    }

    /**
     * Returns the Configurator brack for a given CRUSH option, which may be an alias. 
     * 
     * @param key  The option name or alias...     
     * @return  The {@link jnum.Configurator} option branch for the specified name argument. 
     * 
     * @see #exactOption(String)
     * @see #hasOption(String)
     * @see #containsKey(String)
     */
    public Configurator option(String key) {
        String branchName = getBranchName(key);
        if(branchName.length() == key.length()) return branches.get(unaliasedKey(key));
        else if(branches.containsKey(branchName)) return branches.get(branchName).option(getRemainder(key, branchName.length() + 1));
        else return null;
    }


    /**
     * Returns the Configurator branch for a given option.
     * 
     * @param key  The exact option key name, NOT an alias.
     * @return  The {@link jnum.Configurator} option branch for the specified name argument. 
     * 
     * @see #option(String)
     * @see #containsExact(String)
     */
    public Configurator exactOption(String key) {
        String branchName = getBranchName(key);
        if(branchName.length() == key.length()) return branches.get(key);
        else if(branches.containsKey(branchName)) return branches.get(branchName).option(getRemainder(key, branchName.length() + 1));
        else return null;		
    }

    /**
     * Checks if the specified configuration option or alias exists under this configuration branch. It does not check whether or
     * not the option is enabled. It merely checks for existence, even if in a disabled state.
     * 
     * 
     * If you also want to check to see if the option is <i>enabled</i> beyond just <i>existence</i>, you should
     * use {@link #hasOption(String)} instead.
     *
     * @param key   The option key or alias to look for in this configuration branch.
     * @return      <code>true</code> if the key exists (even if disabled!), or <code>false</code> otherwise.
     * 
     * @see #containsExact(String)
     * @see #hasOption(String)
     * 
     */
    public boolean containsKey(String key) {
        String branchName = getBranchName(key);
        String unaliased = unaliasedKey(branchName);
        if(!branches.containsKey(unaliased)) return false;
        if(key.length() == branchName.length()) return true;
        return branches.get(unaliased).containsKey(getRemainder(key, branchName.length() + 1));
    }

    /**
     * Checks if the specified configuration option (not alias!) exists under this configuration branch. 
     * It does not check whether or not the option is enabled. It merely checks for existence, even if in a disabled state.
     * 
     * 
     * If you also want to check to see if the option is <i>enabled</i> beyond just <i>existence</i>, you should
     * use {@link #hasOption(String)} instead.
     *
     * @param key   The exact option key (not alias!) to look for in this configuration branch.
     * @return      <code>true</code> if the key exists (even if disabled!), or <code>false</code> otherwise.
     * 
     * @see #containsKey(String)
     * @see #hasOption(String)
     * 
     */
    public boolean containsExact(String key) {
        String branchName = getBranchName(key);
        if(!branches.containsKey(branchName)) return false;
        if(key.length() == branchName.length()) return true;
        return branches.get(branchName).containsExact(getRemainder(key, branchName.length() + 1));
    }


    /**
     * Checks if a specific option key was set and is active in this condifuration branch. 
     * Similar to {@link #containsKey(String)} but checks that the option is <i>enabled</i> as well as <i>existence</i>.
     * 
     * 
     * @param key  The option <code>key</code> or alias to check under this configuration branch.
     * @return      <code>true</code> if the option <i>exists</i> and is <i>enabled</i>, or <code>false</code> otherwise.
     * 
     * @see #containsKey(String)
     * @see #containsExact(String)
     * @see #option(String)
     * @see #exactOption(String)
     * 
     */
    public boolean hasOption(String key) {
        if(!containsKey(key)) return false;
        Configurator option = option(key);
        if(!option.isEnabled) return false;
        option.wasUsed = true;
        return option.value != null;
    }	


    public void mapValueTo(String branchName) throws LockedException {
        if(value != null) if(value.length() > 0) {
            if(containsKey(branchName)) {
                Configurator branch = option(branchName);
                if(branch.isLocked) {
                    value = null;	// clear the value...
                    throw new LockedException("Cannot map value to '" + branchName + "'");
                }
                branch.value = value;
            }
            else processSilent(branchName, value);

            value = null;	// clear the value...
        }
        value = "";
    }


    public void intersect(Configurator options) {
        for(String key : getKeys(false)) {
            if(!options.containsKey(key)) purge(key);
            else {
                Configurator option = option(key);
                Configurator other = options.option(key);
                if(option.isEnabled && !other.isEnabled) option.isEnabled = false;
                else if(!option.value.equals(other.value)) option.isEnabled = false;
            }
        }
    }

    // TODO Difference conditionals and blacklists too...
    public Configurator difference(Configurator options) {
        Configurator difference = new Configurator(root);

        for(String key : getKeys(false)) {
            if(!options.containsKey(key)) difference.setOption(key + " " + option(key).value);
            else {
                Configurator option = option(key);
                Configurator other = options.option(key);

                if(option.isEnabled && !other.isEnabled) difference.setOption(key + " " + option(key).value);
                else if(!option.value.equals(other.value)) difference.setOption(key + " " + option(key).value);
            }
        }
        return difference;
    }


    /**
     * Returns the literal value for this configuration point.
     * 
     * @return  The literal {@link String} value associated with this configuration point.
     * 
     * @see #getValue()
     */
    public String getLiteral() {
        return value;
    }
    
    /**
     * Returns the resolved value for this configuration point. Dynamic references to other options are resolved
     * at call time, and are substituted as literal. 
     * 
     * 
     * Suppose this configuration point is set to the literal value: "<code>/home/{?user}/data</code>", and
     * <code>user</code> is set to "<code>johndoe</code>" when this method is called. Accordingly, the
     * call will make the requested substitution and return:
     * 
     * <pre>
     *   /home/johndoe/data
     * </pre>
     * 
     * 
     * @return  The value associated to this configuration point, with dynamic references to other options
     *          resolved and substituted as needed.
     *          
     * @see #getLiteral()
     * @see #getBoolean()
     * @see #getSign()
     * @see #getInt()
     * @see #getDouble()
     * @see #getFloat()
     * @see #getList()
     * @see #getIntegers()
     * @see #getDoubles()
     * @see #getFloats()
     * @see #getVector2D()
     * @see #getDimension2D()
     * @see #getRange()
     * @see #getRange2D()
     * @see #getPath()
     */
    public String getValue() {
        return root.resolve(resolve(getLiteral(), "{?", "}"), "{?", "}"); 
    }

    /**
     * Returns the value associated with this configuration point as a double-precision number.
     * 
     * @return                          The double-precision value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a double-precision number value.
     * 
     * @see #getValue()
     * @see #getFloat()
     * @see #getInt()
     * @see #getBoolean()
     * @see #getSign()
     * @see #getDoubles()
     * @see #getVector2D()
     * @see #getRange()
     * @see #getRange2D()
     * 
     */
    public double getDouble() throws NumberFormatException {
        return Double.parseDouble(getValue());
    }


    /**
     * Returns the value associated with this configuration point as a single-precision floating point number.
     * 
     * @return                          The single-precision value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a single-precision number value.
     * 
     * @see #getValue()
     * @see #getDouble()
     * @see #getInt()
     * @see #getBoolean()
     * @see #getSign()
     * @see #getFloats()
     * @see #getVector2D()
     * @see #getRange()
     * @see #getRange2D()
     * 
     */
    public float getFloat() throws NumberFormatException {
        return Float.parseFloat(getValue());
    }


    /**
     * Returns the value associated with this configuration point as an integer value. That parse rules of 
     * {@link Integer#decode(String)} are applied. I.e., the underlying string  value may be decimal (e.g. 1234), 
     * hex (e.g. 0xa0c7), binary (0b1001101), or octal (0777). 
     * 
     * 
     * A note of warning: you should avoid decimals starting with 0 (e.g. 0123 for decimal 123) as they will 
     * be interpreted as octal values.
     * 
     * @return                          The integer value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as an integer number value.
     * 
     * @see #getValue()
     * @see #getDouble()
     * @see #getFloat()
     * @see #getBoolean()
     * @see #getSign()
     * @see #getIntegers()
     * @see #getVector2D()
     * @see #getRange()
     * @see #getRange2D()
     * 
     */
    public int getInt() throws NumberFormatException {
        return Integer.decode(getValue());
    }


    /**
     * Returns the value associated with this configuration point as a <code>boolean</code> value.
     * See {@link Util#parseBoolean(String)} for acceptable string representations that can be
     * intrerpreted as appropriate <code>boolean</code> values.
     *  
     *  The
     * underlying String may be representing a boolean is several ways, with little or no sensitivity to
     * case (see {@link Util#parseBoolean(String)}, such as "true" or "False", "T" or "F", "yes" or "NO",
     * "Y", or "N", "ON", or "Off", "enabled" or "DISABLED", or "1" or "0".
     * 
     * @return                          The boolean value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a boolean value.
     * 
     * @see #getValue()
     * @see #getSign()
     * @see #getDouble()
     * @see #getFloat()
     * @see #getInt()
     * @see #getVector2D()
     * @see #getRange()
     * @see #getRange2D()
     * 
     */
    public boolean getBoolean() throws NumberFormatException {
        return Util.parseBoolean(getValue());
    }

    /**
     * Returns the value associated with this configuration point as a sign (1 or -1, 0). The
     * underlying String may be representing the sign in different ways:
     * 
     * 
     * <b>positive</b>: "+", "pos", "positive", "plus", "1", "325", "0.0333", "1.406e-27" <br>
     * <b>negative</b>: "-", "neg", "negative", "minus", "-1", "-325", "-0.0333", "-1.406e-27" <br>
     * <b>indeterminate</b>: "*", "any", "0", "0.0" <br>
     * 
     * 
     * @return                          1 for positive, -1 for negative and 0 for indeterminate.
     * @throws NumberFormatException    If the value could not be interpreted as a sign.
     * 
     * @see #getValue()
     * @see #getSign()
     * @see #getDouble()
     * @see #getFloat()
     * @see #getInt()
     * @see #getVector2D()
     * @see #getRange()
     * @see #getRange2D()
     * 
     */
    public int getSign() {
        String value = getValue().toLowerCase();
        if(value.equals("+")) return 1;
        else if(value.equals("-")) return -1;
        else if(value.equals("*")) return 0;
        else if(value.equals("pos")) return 1;
        else if(value.equals("neg")) return -1;
        else if(value.equals("positive")) return 1;
        else if(value.equals("negative")) return -1;
        else if(value.equals("plus")) return 1;
        else if(value.equals("minus")) return -1;
        else if(value.equals("any")) return 0;
        return Double.compare(Double.parseDouble(value), 0.0);
    }


    /**
     * Returns the path associated to this configuration point as a {@link String}. The underlying specification may
     * contain shorthands (such as "~" for the user's home folder), references to other configuration values (static
     * or dynamic), to environment variables, or Java properties. Both "/" and the Windows style "\" can be used
     * as a path separator to allow platform-independent parsing. 
     * 
     * 
     * See {@link #getValue()} for how references are
     * resolved, or {@link Util#getSystemPath(String)} for additional information on how paths are parsed.
     * 
     * 
     * @return  The full proper path constructed from the value of this configuration point as a String.
     * 
     * @see #getValue()
     * @see Util#getSystemPath(String)
     */
    public String getPath() {
        return Util.getSystemPath(getValue());
    }


    /**
     * Returns the value associated with this configuration point as a real-valued range.
     * 
     * @return                          The real-valued range represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as real-valued range value.
     * 
     * @see Range
     * @see #getValue()
     * @see #getRange(double)
     * @see #getRange(boolean)
     * @see #getRange(boolean, double)
     * 
     */
    public Range getRange() throws NumberFormatException {
        return Range.from(getValue());		
    }

    /**
     * Returns the value associated with this configuration point as a real-valued range scaled by the
     * specified scaling factor argument.
     * 
     * 
     * @param scaling                   The factor by which the range shall be scaled (e.g. a {@link Unit} cast) before return.
     * @return                          The real-valued range represented by this configuration point, scaled by the argument.
     * @throws NumberFormatException    If the value could not be parsed as real-valued range value.
     * 
     * @see Range
     * @see #getValue()
     * @see #getRange(boolean)
     * @see #getRange(boolean, double)
     * 
     */
    public Range getRange(double scaling) throws NumberFormatException {
        Range r = getRange();
        r.scale(scaling);
        return r;
    }

    

    /**
     * Returns the value associated with this configuration point as a real-valued range, with the argument
     * specifying the signedness of the expected range. If <code>isNonNegative</code> is <code>true</code> the
     * any dashes '-' are interpreted as min/max separators rather than negative signs. If <code>isNonNegative</code>
     * is <code>false</code> then only colon(s) ':' can separate the min/max limits.
     * 
     * @param isNonNegative             whether the parsed range is expected to be a strictly non-negative range.
     * @return                          The real-valued range represented by this configuration point, scaled by the argument.
     * @throws NumberFormatException    If the value could not be parsed as real-valued range.
     * 
     * @see Range
     * @see #getValue()
     * @see #getRange(double)
     * @see #getRange(boolean, double)
     * 
     */
    public Range getRange(boolean isNonNegative) throws NumberFormatException {
        return Range.from(getValue(), isNonNegative);     
    }

    
    /**
     * Returns the value associated with this configuration point as a real-valued range, with the first argument
     * specifying the signedness of the expected range, and the second argument specifying a scaling factor, such
     * as a unit conversion.
     * 
     * 
     * @param isNonNegative     whether the parsed range is expected to be a strictly non-negative range.
     * @param scaling           The factor by which the range shall be scaled (e.g. a {@link Unit} cast) before return.
     * @return                  The real-valued range represented by this configuration point, scaled by the argument.
     * 
     * @see Range
     * @see #getValue()
     * @see #getRange()
     * @see #getRange(boolean)
     * @see #getRange(double)
     */
    public Range getRange(boolean isNonNegative, double scaling) throws NumberFormatException {
        Range r = getRange(isNonNegative);
        r.scale(scaling);
        return r;
    }


    /**
     * Returns the value associated with this configuration point as a 2D range of values.
     * 
     * @return                          The 2D range represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a 2D range.
     * 
     * @see Range2D
     * @see #getValue()
     * @see #getRange2D(double)
     * 
     */
    public Range2D getRange2D() throws NumberFormatException {
        return Range2D.from(getValue());      
    }

    /**
     * Returns the value associated with this configuration point as a 2D range of values, scaled by the
     * specified factor (such as a Unit conversion).
     * 
     * @param scaling                   The factor by which the 2D range shall be scaled (e.g. a {@link Unit} cast) before return.
     * @return                          The 2D range represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as 2D range range.
     * 
     * @see Range2D
     * @see #getValue()
     * @see #getRange2D()
     * 
     */
    public Range2D getRange2D(double scaling) throws NumberFormatException {
        Range2D r = getRange2D();
        r.scale(scaling);
        return r;
    }

    /**
     * Returns the value associated with this configuration point as a 2D vector.
     * 
     * @return                          The 2D vector represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as 2D vector.
     * 
     * @see Vector2D
     * @see #getValue()
     * @see #getVector2D(double)
     * 
     */
    public Vector2D getVector2D() {
        return new Vector2D(getValue());		
    }

    /**
     * Returns the value associated with this configuration point as a 2D vector, scaled by the the argument 
     * (e.g. for a unit conversion).
     * 
     * @param scaling                   The factor by which the 2D vector shall be scaled (e.g. a {@link Unit} cast) before return.
     * @return                          The 2D vector represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as 2D vector.
     * 
     * @see Vector2D
     * @see #getValue()
     * @see #getVector2D()
     * 
     */
    public Vector2D getVector2D(double scaling) {
        Vector2D v = getVector2D();
        v.scale(scaling);
        return v;
    }

    
    /**
     * Returns the value associated with this configuration point as a 2D dimension. It is similar to {@link getVector2D()} except 
     * with slightly different parsing rules, accepting 'x' or 'X' as separator (as well as commas or spaces), e.g. "142.1x87.3".
     * 
     * @return                          The 2D dimension represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as 2D dimension.
     * 
     * @see Vector2D
     * @see #getVector2D(double)
     * @see #getDimension2D(double)
     * @see #getDimension2D(Coordinate2D)
     * 
     */
    public Vector2D getDimension2D() {
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,:xX");
        Vector2D v = new Vector2D();
        v.setX(Double.parseDouble(tokens.nextToken()));
        v.setY(tokens.hasMoreTokens() ? Double.parseDouble(tokens.nextToken()) : v.x());
        return v;
    }

    /**
     * Returns the value associated with this configuration point as a 2D dimension, scaled by the argument (e.g. for a unit 
     * conversion). It is similar to {@link getVector2D(double)} except with slightly different parsing rules, accepting 
     * 'x' or 'X' as separator (as well as commas or spaces), e.g. "142.1x87.3".
     * 
     * @param scaling                   The factor by which the 2D dimension shall be scaled (e.g. a {@link Unit} cast) before return.
     * @return                          The 2D dimension represented by this configuration point, scaled by the argument.
     * @throws NumberFormatException    If the value could not be parsed as 2D dimension.
     * 
     * @see Vector2D
     * @see #getVector2D(double)
     * @see #getDimension2D()
     * @see #getDimension2D(Coordinate2D)
     * 
     */
    public Vector2D getDimension2D(double scaling) {
        Vector2D v = getDimension2D();
        v.scale(scaling);
        return v;
    }

    

    /**
     * Returns the value associated with this configuration point as a 2D dimension, scaled component-wise by the 2D
     * argument. The underlying parsing is similar to that of {@link getVector2D(double)}, but also accepting 
     * 'x' or 'X' as separator (as well as commas or spaces), e.g. "142.1x87.3".
     * 
     * @param scaling                   The 2D component-wise scalars to apply before return.
     * @return                          The 2D dimension represented by this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as 2D dimension.
     * 
     * @see Vector2D
     * @see #getDouble()
     * @see #getDimension2D()
     * @see #getDimension2D(double)
     * 
     */
    public Vector2D getDimension2D(Coordinate2D scaling) {
        Vector2D v = getDimension2D();
        v.multiplyByComponentsOf(scaling);
        return v;
    }
    
    /**
     * Returns the value associated with this configuration points as a list of words that were separated by spaces, 
     * commas, and/or tabs.
     * 
     * @return      The value at this configuration point, split into word list
     * 
     * @see #getValue()
     * @see #getLowerCaseList()
     * @see #getDoubles()
     * @see #getFloats()
     * @see #getIntegers()
     * 
     */
    public List<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken());
        return list;
    }

    
    /**
     * Returns the value associated with this configuration points as a list of lower-case words that were separated by spaces, 
     * commas, and/or tabs.
     * 
     * @return      The value at this configuration point, split into word list in lower-case.
     * 
     * @see #getValue()
     * @see #getList()
     * @see #getDoubles()
     * @see #getFloats()
     * @see #getIntegers()
     * 
     */
    public List<String> getLowerCaseList() {
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken().toLowerCase());
        return list;		
    }


    /**
     * Returns the value associated with this configuration points as a list of double-precision values that were separated by spaces, 
     * commas, and/or tabs.
     * 
     * @return                          The list of double-precision values represented by the string value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a list of double-precision values.
     * 
     * @see #getValue()
     * @see #getList()
     * @see #getFloats()
     * @see #getIntegers()
     * 
     */
    public List<Double> getDoubles() throws NumberFormatException {
        List<String> list = getList();
        ArrayList<Double> doubles = new ArrayList<>(list.size());	
        for(String entry : list) {
            try { doubles.add(Double.parseDouble(entry)); }
            catch(NumberFormatException e) { doubles.add(Double.NaN); }
        }
        return doubles;
    }


    /**
     * Returns the value associated with this configuration points as a list of single-precision floating point values that 
     * were separated by spaces, commas, and/or tabs.
     * 
     * @return                          The list of single-precision values represented by the string value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a list of floating point values.
     * 
     * @see #getValue()
     * @see #getList()
     * @see #getDoubles()
     * @see #getIntegers()
     * 
     */
    public List<Float> getFloats() {
        List<String> list = getList();
        ArrayList<Float> floats = new ArrayList<>(list.size());	
        for(String entry : list) {
            try { floats.add(Float.parseFloat(entry)); }
            catch(NumberFormatException e) { floats.add(Float.NaN); }
        }
        return floats;
    }

    /**
     * Returns the value associated with this configuration points as a list of integer values that were separated by spaces, 
     * commas, and/or tabs. The parse rules of {@link Integer#decode(String)} are applied.
     * 
     * @return                          The list of integer values represented by the string value at this configuration point.
     * @throws NumberFormatException    If the value could not be parsed as a list of integer values.
     * 
     * @see #getValue()
     * @see #getList()
     * @see #getDoubles()
     * @see #getFloats()
     * 
     */
    public List<Integer> getIntegers() {
        List<String> list = getList();
        ArrayList<Integer> ints = new ArrayList<>(list.size());	
        for(String entry : list) {
            try { ints.add(Integer.decode(entry)); }
            catch(NumberFormatException e) {
                Range range = Range.from(entry, true);
                if(Double.isInfinite(range.min()) || Double.isInfinite(range.max())) throw e;
                int from = (int)Math.ceil(range.min());
                int to = (int)Math.floor(range.max());
                for(int i=from; i<=to; i++) ints.add(i);	
            }
        }
        return ints;
    }

    
    public List<String> getKeys(boolean includeBlacklisted) {
        ArrayList<String> keys = new ArrayList<>();
        for(String branchName : branches.keySet()) {
            Configurator option = branches.get(branchName);	
            if(option.isEnabled) keys.add(branchName);
            else if(includeBlacklisted) if(option.isBlacklisted()) keys.add(branchName);
            for(String key : option.getKeys(includeBlacklisted)) keys.add(branchName + "." + key);			
        }	
        return keys;
    }


    public List<String> getForgottenKeys() {
        ArrayList<String> keys = new ArrayList<>();
        for(String branchName : branches.keySet()) {
            Configurator option = branches.get(branchName);
            if(!option.isEnabled) if(option.value != null) if(option.value.length() > 0) keys.add(branchName);
            for(String key : option.getForgottenKeys()) keys.add(branchName + "." + key);			
        }		
        return keys;
    }


    public List<String> getBlacklist() {
        ArrayList<String> keys = new ArrayList<>();
        for(String branchName : branches.keySet()) {
            Configurator option = branches.get(branchName);	
            if(option.isBlacklisted()) keys.add(branchName);
            for(String key : option.getBlacklist()) keys.add(branchName + "." + key);
        }	
        return keys;
    }


    public List<String> getConditionalListFor(String keyPattern) {

        if(keyPattern != null) {
            if(keyPattern.isEmpty()) keyPattern = null;
            else keyPattern = keyPattern.toLowerCase();
        }

        Hashtable<String, Vector<String>> conditions = getConditions(true);
        ArrayList<String> forKey = new ArrayList<>();

        for(String condition : conditions.keySet()) {
            Vector<String> settings = conditions.get(condition);
            StringBuffer selection = new StringBuffer();

            for(int i=0; i<settings.size(); i++) {
                String setting = settings.get(i).trim();

                if(keyPattern == null) {
                    selection.append((selection.length() > 0 ? "; " : "") + setting);
                    continue;
                }

                if(setting.startsWith(keyPattern)) selection.append((selection.length() > 0 ? "; " : "") + setting);
                else if(setting.contains(keyPattern)) {
                    Entry entry = new Entry(setting);
                    if(entry.isCommand()) {
                        StringTokenizer tokens = new StringTokenizer(entry.value, " \t,;=:");
                        while(tokens.hasMoreTokens()) {
                            String token = tokens.nextToken();
                            if(token.startsWith(keyPattern)) selection.append((selection.length() > 0 ? "; " : "") + entry.key + " " + token);
                        }
                    }
                }
            }

            if(selection.length() > 0) forKey.add(condition + " " + selection);
        }

        Collections.sort(forKey);

        return forKey;
    }



    public Hashtable<String, Vector<String>> getConditions(boolean isBracketed) {
        Hashtable<String, Vector<String>> conditions = new Hashtable<>();
        for(String key : conditionals.keySet()) {
            conditions.put(isBracketed ? "[" + key + "]" : key, conditionals.get(key));
        }

        for(String branchName : branches.keySet()) {
            Hashtable<String, Vector<String>> branchConditions = branches.get(branchName).getConditions(isBracketed);	
            if(!branchConditions.isEmpty())
                for(String key : branchConditions.keySet()) conditions.put(branchName + "." + key, branchConditions.get(key));			
        }	
        return conditions;
    }


    public List<String> getTimeOrderedKeys() {		
        List<String> keys = getKeys(false);	
        Collections.sort(keys,
                new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                int i1 = option(key1).serialNo;
                int i2 = option(key2).serialNo;
                if(i1 == i2) return 0;
                return i1 > i2 ? 1 : -1;
            }
        });
        return keys;
    }


    public List<String> getAlphabeticalKeys(boolean includeBlacklisted) {
        List<String> keys = getKeys(includeBlacklisted);
        Collections.sort(keys);
        return keys;
    }


    public void print(PrintStream out) {
        poll(null, out, "#");
    }


    public void poll(String pattern) {
        poll(pattern, System.out, "");
        pollForgotten(pattern, System.out, "");
        System.out.println();
    }


    public void poll(String pattern, PrintStream out, String prefix) {

        if(pattern != null) {
            pattern = pattern.toLowerCase();
            while(pattern.endsWith("*")) pattern = pattern.substring(0, pattern.length()-1);
        }

        out.println();

        if(pattern == null) out.println(prefix + " Current configuration is: ");
        else out.println(prefix + " Currently set keys starting with '" + pattern + "': ");

        out.println(prefix + " --------------------------------------------------------------------");

        for(String key : getAlphabeticalKeys(true)) {
            if(pattern != null) if(!key.startsWith(pattern)) continue;

            Configurator option = option(key);
            if(option.isBlacklisted()) {
                out.println("  [" + key + "] --- (blacklisted)");
            }
            else {
                out.print("   " + key);
                String value = option.getValue();
                if(value.length() > 0) out.print(" = " + value);
                if(option.isLocked) out.print(" (locked)");
                out.println();
            }
        }


        if(pattern != null) {
            List<String> conditions = getConditionalListFor(pattern);

            if(!conditions.isEmpty()) {
                out.println();
                out.println(prefix + " Conditional settings for '" + pattern + "': ");
                out.println(prefix + " --------------------------------------------------------------------");

                for(String condition : conditions) out.println("   " + condition);
            }
        }


        out.println(prefix + " --------------------------------------------------------------------");
    }


    public void pollForgotten(String pattern, PrintStream out, String prefix) {

        if(pattern != null) {
            pattern = pattern.toLowerCase();
            while(pattern.endsWith("*")) pattern = pattern.substring(0, pattern.length()-1);
        }

        List<String> list = getForgottenKeys();
        if(list.isEmpty()) return;

        out.println();

        if(pattern == null) out.println(prefix + " Recallable configuration keys are: ");
        else out.println(prefix + " Recallable keys starting with '" + pattern + "': ");

        out.println(prefix + " --------------------------------------------------------------------");


        Collections.sort(list);

        for(String key : list) {
            if(pattern != null) if(!key.startsWith(pattern)) continue;

            out.print("   (" + key);
            String value = option(key).value;
            if(value.length() > 0) out.print(" = " + value);
            out.print(")");
            if(isBlacklisted(key)) out.print(" --blacklisted--");
            out.println();
        }

        out.println(prefix + " --------------------------------------------------------------------");
    }


    public void pollBlacklist(String pattern) {
        pollBlacklist(pattern, System.out, "");
        System.out.println();
    }


    public void pollBlacklist(String pattern, PrintStream out, String prefix) {

        if(pattern != null) {
            pattern = pattern.toLowerCase();
            while(pattern.endsWith("*")) pattern = pattern.substring(0, pattern.length()-1);
        }

        out.println();

        if(pattern == null) out.println(prefix + " Blacklisted configuration keys are: ");
        else out.println(prefix + " Blacklisted keys starting with '" + pattern + "': ");

        out.println(prefix + " --------------------------------------------------------------------");

        List<String> list = getBlacklist();
        Collections.sort(list);

        for(String key : list) {
            if(pattern != null) if(!key.startsWith(pattern)) continue;
            out.println("   " + key);
        }

        out.println(prefix + " --------------------------------------------------------------------");
    }


    public void pollConditions(String pattern) {
        pollConditions(pattern, System.out, "");
        System.out.println();
    }


    public void pollConditions(String pattern, PrintStream out, String prefix) {

        if(pattern != null) {
            pattern = pattern.toLowerCase();
            while(pattern.endsWith("*")) pattern = pattern.substring(0, pattern.length()-1);
        }

        out.println();

        if(pattern == null) out.println(prefix + " Active conditions are: ");
        else out.println(prefix + " Active conditions starting with '" + pattern + "': ");

        // Add all the conditionals...
        Hashtable<String, Vector<String>> conditions = getConditions(true);
        ArrayList<String> conditionKeys = new ArrayList<>(conditions.keySet());	
        Collections.sort(conditionKeys);

        out.println(prefix + " --------------------------------------------------------------------");

        for(String key : conditionKeys) {
            if(pattern != null) if(!key.startsWith(pattern)) continue;

            StringBuilder values = new StringBuilder();
            for(String value : conditions.get(key)) {
                if(values.length() > 0) values.append(';');
                values.append(value);
            }
            out.println("   " + key + " " + new String(values));
        }	

        out.println(prefix + " --------------------------------------------------------------------");
    }


    public void readConfig(String fileName) throws IOException {
        File configFile = new File(fileName);
        if(configFile.exists()) {
            if(!silent) Util.info(this, "Loading configuration from " + fileName);

            new LineParser() {
                @Override
                public boolean parse(String line) { return setOption(line); }
            }.read(configFile);
        }
        else throw new FileNotFoundException(fileName);
    }


    @Override
    public void editHeader(Header header) throws HeaderCardException {

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);

        // Add all active configuration keys...
        for(String key : getAlphabeticalKeys(false)) {
            Configurator option = option(key);
            if(option.isEnabled) FitsToolkit.addLongHierarchKey(c, key, option.value);
        }

        // Add all the conditionals...
        Hashtable<String, Vector<String>> conditions = getConditions(true);
        ArrayList<String> conditionKeys = new ArrayList<>(conditions.keySet());	
        Collections.sort(conditionKeys);

        for(String condition : conditionKeys) {
            StringBuilder values = new StringBuilder();
            for(String value : conditions.get(condition)) {
                if(values.length() > 0) values.append(';');
                values.append(value);
            }
            FitsToolkit.addLongHierarchKey(c, condition, new String(values));
        }
    }	


    static class Locator {
        String fileName;
        int locationIndex;
        long lastModified;
    }


    static class Setting {
        String value;
        Locator locator;
    }


    static class Entry {

        String key;

        String value;		// TODO change to Setting...


        public Entry() {}


        public Entry(String key, String value) {
            this();
            this.key = key;
            this.value = value;
        }


        public Entry (String line) {
            this();
            parse(line);
        }


        public boolean isCommand() {
            key = key.toLowerCase();
            if(key.endsWith("forget")) return true;
            if(key.endsWith("recall")) return true;
            if(key.endsWith("remove")) return true;
            if(key.endsWith("restore")) return true;
            if(key.endsWith("replace")) return true;
            if(key.endsWith("blacklist")) return true;
            if(key.endsWith("whitelist")) return true;
            if(key.endsWith("lock")) return true;
            if(key.endsWith("unlock")) return true;
            return false;
        }


        public void parse(String line) {
            final StringBuffer keyBuffer = new StringBuffer();

            int openCurved = 0;
            int openCurly = 0;
            int openSquare = 0;

            line = line.trim();

            int index = 0;

            boolean foundSeparator = false;

            for(; index < line.length(); index++) {
                final char c = line.charAt(index);
                switch(c) {
                case '(' : openCurved++; break;
                case ')' : openCurved--; break;
                case '{' : openCurly++; break;
                case '}' : openCurly--; break;
                case '[' : openSquare++; break;
                case ']' : openSquare--; break;
                default :
                    if(c == ' ' || c == '\t' || c == ':' || c == '=') {
                        if(openCurved <= 0 && openCurly <= 0 && openSquare <= 0) {
                            foundSeparator = true;
                            key = new String(keyBuffer).toLowerCase();
                            break;
                        }
                    }	
                }
                if(foundSeparator) break;
                keyBuffer.append(c);
            }

            // If it's just a key without an argument, then return an entry with an empty argument...
            if(index == line.length()) {
                key = new String(keyBuffer).toLowerCase();
                value = "";
                return;
            }

            // Otherwise, skip trailing spaces and assigners after the key... 
            for(; index < line.length(); index++) {
                char c = line.charAt(index);
                if(c != ' ') if(c != '\t') if(c != '=') if(c != ':') break;
            }

            // The remaining is the 'raw' argument...
            value = line.substring(index).trim();

            // Remove quotes from around the argument
            if(value.length() != 0) {
                if(value.charAt(0) == '"' && value.charAt(value.length()-1) == '"')
                    value = value.substring(1, value.length() - 1);
                else if(value.charAt(0) == '\'' && value.charAt(value.length()-1) == '\'')
                    value = value.substring(1, value.length() - 1);	
            }
        }

    }


    public void processEnvironmentOption(String spec, String settings) {
        if(spec.length() == 0) return;

        StringTokenizer tokens = new StringTokenizer(spec, "?");
        String varName = tokens.nextToken().toUpperCase();

        if(varName.charAt(0) == '!') if(System.getenv(varName.substring(1)) == null) {
            parseAll(getList(settings));
            return;
        }

        if(!tokens.hasMoreTokens()) if(System.getenv(varName) != null) {
            parseAll(getList(settings));
            return;
        }

        String checkValue = spec.substring(varName.length()+1);
        if(checkValue.charAt(0) == '!') {
            if(!System.getenv(varName).equalsIgnoreCase(checkValue.substring(1))) parseAll(getList(settings));
            return;
        }

        if(System.getenv(varName).equalsIgnoreCase(checkValue)) parseAll(getList(settings));
    }

    public void processPropertyOption(String spec, String settings) {
        if(spec.length() == 0) return;

        StringTokenizer tokens = new StringTokenizer(spec, "?");
        String name = tokens.nextToken();
        String checkValue = spec.substring(name.length()+1);

        if(checkValue.charAt(0) == '!') {
            if(!System.getProperty(name).equalsIgnoreCase(checkValue.substring(1))) parseAll(getList(settings));
            return;
        }

        if(System.getProperty(name).equalsIgnoreCase(checkValue)) parseAll(getList(settings));

    }

}


