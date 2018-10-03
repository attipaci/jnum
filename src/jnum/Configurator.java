/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
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


public class Configurator implements Serializable, Cloneable, Copiable<Configurator>, FitsHeaderEditing {


    private static final long serialVersionUID = 5040150005828567005L;

    private Configurator root;

    private String value;
    public int serialNo;

    public boolean isEnabled = false;
    public boolean isLocked = false;
    public boolean wasUsed = false;


    public Hashtable<String, Configurator> branches = new Hashtable<String, Configurator>();
    public Hashtable<String, Vector<String>> conditionals = new Hashtable<String, Vector<String>>();

    private static int counter = 0;	

    public static boolean silent = false;
    public static boolean verbose = false;
    public static boolean details = false;



    public Configurator() { root = this; }


    public Configurator(Configurator root) { this.root = root; }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Configurator clone() {
        try { return (Configurator) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }


    @Override
    @SuppressWarnings("unchecked")
    public Configurator copy() {
        Configurator copy = clone();

        copy.branches = new Hashtable<String, Configurator>();
        for(String key : branches.keySet()) copy.branches.put(key, branches.get(key).copy());

        copy.conditionals = new Hashtable<String, Vector<String>>();
        for(String key : conditionals.keySet()) copy.conditionals.put(key, (Vector<String>) conditionals.get(key).clone());

        return copy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof Configurator)) return false;
        Configurator c = (Configurator) o;
        return Util.equals(c.value, value);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }


    public boolean is(String value) {
        return value.equalsIgnoreCase(this.value);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if(value == null) return "<none>";
        return value;		
    }


    public List<String> parseAll(List<String> lines) {
        ArrayList<String> exceptions = new ArrayList<String>();

        for(String line : lines) {
            try { parse(line); }
            catch(LockedException e) { exceptions.add(e.getMessage()); }
        }

        return exceptions.isEmpty() ? null : exceptions;
    }


    public void parseSilent(String line) {
        try { parse(line); }
        catch(LockedException e) {}
    }


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
            Configurator alias = getExact("alias." + branchName);
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


    public String getProperty(String name) {
        return containsKey(name) ? get(name).getValue() : null;		
    }


    public void processSilent(String key, String argument) {
        try { process(key, argument); }
        catch(LockedException e) {}
    }


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
            catch(IOException e) { Util.warning(this, "Configuration file '" + argument + "' no found."); }
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
                for(String name : new ArrayList<String>(branches.keySet())) process(name + key.substring(1), argument);				
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
        setCondition(key, argument);
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
        if(isSatisfied(condition)) parseSilent(setting); 

        else {
            Vector<String> list = conditionals.containsKey(condition) ? conditionals.get(condition) : new Vector<String>();
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
            if(isConfigured(conditionKey)) if(get(conditionKey).is(pair.nextToken())) return true;
        }
        else if(isConfigured(condition.toLowerCase())) return true;

        return false;
    }


    private List<String> getList(String argument) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(argument, " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken());
        return list;
    }



    public void setCondition(String key, String value) {
        setCondition(key);
        setCondition(key + "?" + value.toLowerCase());
    }


    public void setCondition(String expression) {
        if(!conditionals.containsKey(expression)) return;

        if(details) Util.detail(this, "[c] " + expression + " > " + conditionals.get(expression));
        parseAll(conditionals.get(expression));
    }


    public void forgetSilent(String arg) {
        try { forget(arg); }
        catch(LockedException e) {}
    }


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
            for(String name : new ArrayList<String>(branches.keySet())) forget(name + getRemainder(arg, 1));
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
            for(String name : new ArrayList<String>(branches.keySet())) recall(name + getRemainder(arg, 1));
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
                    setCondition(arg, option.value);
                }
            }
        }
    }


    public void remove(String arg) throws LockedException {
        String branchName = getBranchName(arg);

        if(branchName.equals("*")) {
            for(String name : new ArrayList<String>(branches.keySet())) remove(name + getRemainder(arg, 1));
        }
        else if(branchName.startsWith("[") && branchName.endsWith("]")) {
            branchName = branchName.substring(1, branchName.length()-1).trim();
            for(String condition : new ArrayList<String>(conditionals.keySet())) if(condition.startsWith(branchName)) conditionals.remove(condition);
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
            for(String name : new ArrayList<String>(branches.keySet())) purge(name + getRemainder(arg, 1));
        }
        else if(branchName.startsWith("[") && branchName.endsWith("]")) {
            branchName = branchName.substring(1, branchName.length()-1).trim();
            for(String condition : new ArrayList<String>(conditionals.keySet())) if(condition.startsWith(branchName)) conditionals.remove(condition);
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
            else for(String name : new ArrayList<String>(branches.keySet())) restore(name + getRemainder(arg, 1));
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


    public Configurator get(String key) {
        String branchName = getBranchName(key);
        if(branchName.length() == key.length()) return branches.get(unaliasedKey(key));
        else if(branches.containsKey(branchName)) return branches.get(branchName).get(getRemainder(key, branchName.length() + 1));
        else return null;
    }


    public Configurator getExact(String key) {
        String branchName = getBranchName(key);
        if(branchName.length() == key.length()) return branches.get(key);
        else if(branches.containsKey(branchName)) return branches.get(branchName).get(getRemainder(key, branchName.length() + 1));
        else return null;		
    }


    public boolean containsKey(String key) {
        String branchName = getBranchName(key);
        String unaliased = unaliasedKey(branchName);
        if(!branches.containsKey(unaliased)) return false;
        if(key.length() == branchName.length()) return true;
        return branches.get(unaliased).containsKey(getRemainder(key, branchName.length() + 1));
    }


    public boolean containsExact(String key) {
        String branchName = getBranchName(key);
        if(!branches.containsKey(branchName)) return false;
        if(key.length() == branchName.length()) return true;
        return branches.get(branchName).containsExact(getRemainder(key, branchName.length() + 1));
    }


    public boolean isConfigured(String key) {
        if(!containsKey(key)) return false;
        Configurator option = get(key);
        if(!option.isEnabled) return false;
        option.wasUsed = true;
        return option.value != null;
    }	


    public void mapValueTo(String branchName) throws LockedException {
        if(value != null) if(value.length() > 0) {
            if(containsKey(branchName)) {
                Configurator branch = get(branchName);
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
                Configurator option = get(key);
                Configurator other = options.get(key);
                if(option.isEnabled && !other.isEnabled) option.isEnabled = false;
                else if(!option.value.equals(other.value)) option.isEnabled = false;
            }
        }
    }

    // TODO Difference conditionals and blacklists too...
    public Configurator difference(Configurator options) {
        Configurator difference = new Configurator(root);

        for(String key : getKeys(false)) {
            if(!options.containsKey(key)) difference.parseSilent(key + " " + get(key).value);
            else {
                Configurator option = get(key);
                Configurator other = options.get(key);

                if(option.isEnabled && !other.isEnabled) difference.parseSilent(key + " " + get(key).value);
                else if(!option.value.equals(other.value)) difference.parseSilent(key + " " + get(key).value);
            }
        }
        return difference;
    }


    public String getValue() {
        return root.resolve(resolve(getRawValue(), "{?", "}"), "{?", "}"); 
    }


    public String getRawValue() {
        return value;
    }


    public double getDouble() {
        return Double.parseDouble(getValue());
    }


    public float getFloat() {
        return Float.parseFloat(getValue());
    }


    public int getInt() {
        return Integer.decode(getValue());
    }


    public boolean getBoolean() {
        return Util.parseBoolean(getValue());
    }


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


    public String getPath() {
        return Util.getSystemPath(getValue());
    }


    public Range getRange() {
        return Range.from(getValue());		
    }

    public Range getRange(double scaling) {
        Range r = getRange();
        r.scale(scaling);
        return r;
    }


    public Range getRange(boolean isNonNegative) {
        return Range.from(getValue(), isNonNegative);     
    }

    public Range getRange(boolean isNonNegative, double scaling) {
        Range r = getRange(isNonNegative);
        r.scale(scaling);
        return r;
    }


    public Range2D getRange2D() {
        return Range2D.from(getValue());      
    }

    public Range2D getRange2D(double scaling) {
        Range2D r = getRange2D();
        r.scale(scaling);
        return r;
    }


    public Vector2D getVector2D() {
        return new Vector2D(getValue());		
    }

    public Vector2D getVector2D(double scaling) {
        Vector2D v = getVector2D();
        v.scale(scaling);
        return v;
    }

    public Vector2D getDimension2D() {
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,:xX");
        Vector2D v = new Vector2D();
        v.setX(Double.parseDouble(tokens.nextToken()));
        v.setY(tokens.hasMoreTokens() ? Double.parseDouble(tokens.nextToken()) : v.x());
        return v;
    }

    public Vector2D getDimension2D(double scaling) {
        Vector2D v = getDimension2D();
        v.scale(scaling);
        return v;
    }

    public Vector2D getDimension2D(Coordinate2D scaling) {
        Vector2D v = getDimension2D();
        v.multiplyByComponents(scaling);
        return v;
    }
    

    public List<String> getList() {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken());
        return list;
    }


    public List<String> getLowerCaseList() {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(getValue(), " \t,");
        while(tokens.hasMoreTokens()) list.add(tokens.nextToken().toLowerCase());
        return list;		
    }


    public List<Double> getDoubles() {
        List<String> list = getList();
        ArrayList<Double> doubles = new ArrayList<Double>(list.size());	
        for(String entry : list) {
            try { doubles.add(Double.parseDouble(entry)); }
            catch(NumberFormatException e) { doubles.add(Double.NaN); }
        }
        return doubles;
    }


    public List<Float> getFloats() {
        List<String> list = getList();
        ArrayList<Float> floats = new ArrayList<Float>(list.size());	
        for(String entry : list) {
            try { floats.add(Float.parseFloat(entry)); }
            catch(NumberFormatException e) { floats.add(Float.NaN); }
        }
        return floats;
    }

    // Also takes ranges...
    public List<Integer> getIntegers() {
        List<String> list = getList();
        ArrayList<Integer> ints = new ArrayList<Integer>(list.size());	
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
        ArrayList<String> keys = new ArrayList<String>();
        for(String branchName : branches.keySet()) {
            Configurator option = branches.get(branchName);	
            if(option.isEnabled) keys.add(branchName);
            else if(includeBlacklisted) if(option.isBlacklisted()) keys.add(branchName);
            for(String key : option.getKeys(includeBlacklisted)) keys.add(branchName + "." + key);			
        }	
        return keys;
    }


    public List<String> getForgottenKeys() {
        ArrayList<String> keys = new ArrayList<String>();
        for(String branchName : branches.keySet()) {
            Configurator option = branches.get(branchName);
            if(!option.isEnabled) if(option.value != null) if(option.value.length() > 0) keys.add(branchName);
            for(String key : option.getForgottenKeys()) keys.add(branchName + "." + key);			
        }		
        return keys;		
    }


    public List<String> getBlacklist() {
        ArrayList<String> keys = new ArrayList<String>();
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
        ArrayList<String> forKey = new ArrayList<String>();

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
        Hashtable<String, Vector<String>> conditions = new Hashtable<String, Vector<String>>();
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
                int i1 = get(key1).serialNo;
                int i2 = get(key2).serialNo;
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

            Configurator option = get(key);
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
            String value = get(key).value;
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
        ArrayList<String> conditionKeys = new ArrayList<String>(conditions.keySet());	
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
                public boolean parse(String line) { parseSilent(line); return true; }
            }.read(configFile);
        }
        else throw new FileNotFoundException(fileName);
    }


    @Override
    public void editHeader(Header header) throws HeaderCardException {

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);

        // Add all active configuration keys...
        for(String key : getAlphabeticalKeys(false)) {
            Configurator option = get(key);
            if(option.isEnabled) FitsToolkit.addLongHierarchKey(c, key, option.value);
        }

        // Add all the conditionals...
        Hashtable<String, Vector<String>> conditions = getConditions(true);
        ArrayList<String> conditionKeys = new ArrayList<String>(conditions.keySet());	
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


