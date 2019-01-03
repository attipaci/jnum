/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.reporting.ConsoleReporter;
import jnum.reporting.Reporter;
import jnum.text.AngleFormat;
import jnum.text.HourAngleFormat;
import jnum.text.SignificantFiguresFormat;
import jnum.text.TimeFormat;


/**
 * A collection of static utility funtions to use anywhere and everywhere.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 */
public final class Util {


    public final static String version = "0.23-2";
    
    public final static String revision = "devel.1";
    
    public final static String copyright = "(c)2018 Attila Kovacs"; 
    
    public final static String copyrightEmail = "<attila[AT]sigmyne.com>"; 

 
    public static boolean debug = false;

    
    public static String getCopyrightString() {
        return "Copyright " + copyright + " " + copyrightEmail;
    }
    

    public static NumberFormat getDecimalFormat(double significance) {
        return getDecimalFormat(significance, 6, true);
    }


    public static NumberFormat getDecimalFormat(double significance, boolean trailingZeroes) {
        return getDecimalFormat(significance, 6, trailingZeroes);
    }


    public static NumberFormat getDecimalFormat(double significance, int maxDecimals) {
        return getDecimalFormat(significance, maxDecimals, true);
    }


    public static NumberFormat getDecimalFormat(double significance, int maxDecimals, boolean trailingZeroes) {
        if(Double.isNaN(significance)) return trailingZeroes ?  f1 : F1;
        if(significance == 0.0) return trailingZeroes ? f2 : F2;
        int figures = Math.min(maxDecimals, (int) Math.floor(Math.log10(Math.abs(significance))) + 2);
        figures = Math.max(1, figures);
        return trailingZeroes ? s[figures] : S[figures];
    }


    public final static double timeOfDay(double time) {
        time = Math.IEEEremainder(time, Unit.day);
        if(time < 0.0) time += Unit.day;
        return time;
    }


    public final static double timeOfAngle(final double angle) {
        return timeOfDay(angle * Unit.second/Unit.secondAngle);
    }


    public final static double angleOfTime(final double time) {
        return ExtraMath.standardAngle(time * Unit.secondAngle/Unit.second);
    }

    /**
     * Construct an angle from degree, arc-minute and arc-second values.
     *
     * @param D     degrees
     * @param M     arcminutes
     * @param S     arcseconds
     * @return      The angle (radians) constructed from the supplied components.
     * 
     * @see Util#DMS(double)
     */    
    public static double DMS(final int D, final int M, final double S) {
        return D*Unit.degree + M*Unit.arcmin + S*Unit.arcsec;
    }

    /**
     * Construct an time from hour, minute and second values.
     *
     * @param H     hours
     * @param M     minutes
     * @param S     seconds
     * @return      The time (seconds) constructed fromn the supplied components
     * 
     * @see #HMS(double)
     */ 

    public static double HMS(final int H, final int M, final double S) {
        return H*Unit.hour + M * Unit.minute + S * Unit.second;
    } 

    public static String HMS(final double time) { return HMS(time, f[3]); }


    public static String HMS(final double time, final int precision) { return HMS(time, f[precision]); }

    public static String HMS(double time, DecimalFormat df) {
        if(time < 0.0) return("-" + HMS(-time, df));

        time /= Unit.hour; int hour = (int)time; time -= hour;
        time *= 60.0; int min = (int)time; time -= min;

        return d2.format(hour) + ":" + d2.format(min) + ":" + df.format(60.0 * time);
    }

    public static String shortHMS(double time, DecimalFormat df) {
        if(time < 0.0) return("-" + shortHMS(-time, df));

        time /= Unit.hour; int hour = (int)time; time -= hour;
        time *= 60.0; int min = (int)time; time -= min;

        return (hour > 0 ? d2.format(hour) + ":" : "") + (min > 0 ? d2.format(min) + ":" : "") + df.format(60.0 * time);
    }


    public static String DMS(double angle) { return DMS(angle, f[3]); }

    public static String DMS(double angle, int precision) { return DMS(angle, f[precision]); }

    public static String DMS(double angle, DecimalFormat df) {
        if(angle < 0.0) return("-" + DMS(-angle, df));

        angle /= Unit.degree; int d = (int)angle; angle -= d;
        angle *= 60.0; int m = (int)angle; angle -= m;

        return d2.format(d) + ":" + d2.format(m) + ":" + df.format(60.0 * angle);
    }

    public static String shortDMS(double angle, DecimalFormat df) {
        if(angle < 0.0) return("-" + shortDMS(-angle, df));

        angle /= Unit.degree; int d = (int)angle; angle -= d;
        angle *= 60.0; int m = (int)angle; angle -= m;

        return (d > 0 ? d2.format(d) + ":" : "") + (m > 0 ? d2.format(m) + ":" : "") + df.format(60.0 * angle);
    }


    public static double parseTime(String HMS) {
        StringTokenizer tokens = new StringTokenizer(HMS, " \t\n\r:hmsHMS");

        double time = Integer.parseInt(tokens.nextToken()) * Unit.hour;
        time += Integer.parseInt(tokens.nextToken()) * Unit.min;
        time += Double.parseDouble(tokens.nextToken()) * Unit.second;

        return time;
    }

    public static double parseAngle(String DMS) {
        StringTokenizer tokens = new StringTokenizer(DMS, "- \t\n\r:dmsDMS");

        double angle = Integer.parseInt(tokens.nextToken()) * Unit.degree;
        angle += Integer.parseInt(tokens.nextToken()) * Unit.arcmin;
        angle += Double.parseDouble(tokens.nextToken()) * Unit.arcsec;

        if(DMS.indexOf('-') >= 0) angle *= -1;

        return angle;
    }
    
    /**
     * Returns the boolean value associated with the string argument. The
     * String <code>value</code> may be representing a boolean is several ways, with little or no sensitivity to
     * case (see {@link #parseBoolean(String)}, such as "true" or "False", "T" or "F", "yes" or "NO",
     * "Y", or "N", "ON", or "Off", "enabled" or "DISABLED", or "1" or "0".
     * 
     * @return  The boolean value represented by the <code>value</code> argument.
     * @throws NumberFormatException    If the value could not be parsed as a boolean value.
     * 
     */
    public static boolean parseBoolean(final String value) throws NumberFormatException {
        if(value.length() < 1) throw new NumberFormatException(" ERROR! Empty string for boolean value.");

        final char c = value.charAt(0);
        // 1/0
        if(value.length() == 1) {
            switch(c) {
            case '1' : 
            case 't' :
            case 'T' :
            case 'y' :
            case 'Y' :
                return true;
            case '0' :
            case 'f' :
            case 'F' :
            case 'n' :
            case 'N' :
                return false;
            default : throw new NumberFormatException(" ERROR! Character does not define boolean: " + c);
            }
        }

        switch(c) {
        case 'y' :
        case 'Y' :
            // Yes/No
            if(value.equalsIgnoreCase("yes")) return true;
            break;
        case 'n' :
        case 'N' :
            if(value.equalsIgnoreCase("no")) return false;
            break;
        case 't' :
        case 'T' :
            // True/False
            if(value.equalsIgnoreCase("true")) return true;
            break;
        case 'f' :
        case 'F' :
            if(value.equalsIgnoreCase("false")) return false;
            break;
        case 'o' :
        case 'O' :
            // On/Off
            if(value.equalsIgnoreCase("on")) return true;
            if(value.equalsIgnoreCase("off")) return false;
            break;
        case 'e' :
            if(value.equalsIgnoreCase("enable")) return true;
            if(value.equalsIgnoreCase("enabled")) return true;
            break;
        case 'd' :
            if(value.equalsIgnoreCase("disable")) return true;
            if(value.equalsIgnoreCase("disabled")) return true;
            break;
        }


        throw new NumberFormatException(" ERROR! Illegal Boolean value: " + value);
    }

    // implement with Properties...
    public static void printContents(Object object) {
        System.out.println("Contents of " + object.getClass().getName() + ":");
        System.out.println("-------------------------------------------------------------");

        Field[] field = object.getClass().getFields();
        for(int i=0; i<field.length; i++) {
            try {
                Object value = field[i].get(object);
                System.out.println("  " + field[i].getName() + " = " + (value == null ? "null" : value.toString())); 
            }
            catch(IllegalAccessException e) {}
        }

        System.out.println("-------------------------------------------------------------");
    }


    public static String getSystemPath(String spec) {
        if(spec == null) return ".";
        if(spec.length() == 0) return ".";
        
        
        String homes = System.getProperty("user.home");

        // Make sure that UNIX-type pathnames are always accepted since these are default
        // to configuration files. This comes at the small price that pathnames are not
        // allowed to contain '/' characters other than path separators...
        if(!File.separator.equals("/")) spec = spec.replace('/', File.separator.charAt(0));
        homes = homes.substring(0, homes.lastIndexOf(File.separator) + 1);

        String text = "";

        // See if it's a home directory specification starting with '~'...
        if(spec.charAt(0) == '~') {
            int userChars = spec.indexOf(File.separator) - 1;
            if(userChars < 0) userChars = spec.length() - 1;
            if(userChars == 0) text = System.getProperty("user.home");
            else text += homes + spec.substring(1, 1 + userChars);
            spec = spec.substring(userChars + 1);
        }

        while(spec.contains("{$")) {
            int from = spec.indexOf("{$");
            int to = spec.indexOf("}");
            if(to < 0) return text + spec;

            text += spec.substring(0, from);
            text += System.getenv(spec.substring(from+2, to));

            spec = spec.substring(to+1);
        }

        text += spec;

        return text;

    }

    public final static short unsigned(final byte b) {
        return ((short)(b & 0xff));
    }	


    public final static int unsigned(final short s) {
        return (s & 0xffff);
    }


    public final static long unsigned(final int i) {
        return (i & 0xffffffffL);
    }


    public final static byte unsignedByte(final short value) {
        return (byte)(value & 0xff);
    }


    public final static short unsignedShort(final int value) {
        return (short)(value & 0xffff);
    }


    public final static int unsignedInt(final long value) {
        return (int)(value & 0xffffffffL);
    }


    public final static long pseudoUnsigned(final long l) {
        return l < 0L ? Long.MAX_VALUE : l;
    }


    public final static short[] unsigned(final byte[] b) {
        final short[] s = new short[b.length];
        for(int i=0; i<b.length; i++) s[i] = unsigned(b[i]);	
        return s;
    }


    public final static int[] unsigned(final short[] s) {
        final int[] i = new int[s.length];
        for(int j=0; j<s.length; j++) i[j] = unsigned(s[j]);	
        return i;
    }


    public final static long[] unsigned(final int[] i) {
        final long[] l = new long[i.length];
        for(int j=0; j<i.length; j++) l[j] = unsigned(i[j]);	
        return l;
    }


    public final static void pseudoUnsigned(final long[] l) {
        for(int j=0; j<l.length; j++) l[j] = pseudoUnsigned(l[j]);	
    }


    public final static String getProperty(String name) {
        String value = System.getProperty(name);
        return value == null ? "n/a" : value;
    }


    public static String fromEscapedString(String value) throws IllegalStateException {
        StringBuffer buffer = new StringBuffer(value.length());
        for(int i=0; i<value.length(); i++) {
            final char c = value.charAt(i);
            if(c == '\\') {
                if(++i == value.length()) throw new IllegalStateException("Illegal escape character at the end of string.");
                switch(value.charAt(i)) {
                case '\\' : buffer.append('\\'); break;
                case 't' : buffer.append('\t'); break;
                case 'n' : buffer.append('\n'); break;
                case 'r' : buffer.append('\r'); break;
                case 'b' : buffer.append('\b'); break;
                case '"' : buffer.append('\"'); break;
                case '\'' : buffer.append('\''); break;
                default : throw new IllegalStateException("Illegal escape sequence '\\" + value.charAt(i) + "'.");
                }
            }
            else buffer.append(c);
        }

        return new String(buffer);
    }

    public static String toEscapedString(String value) {
        StringBuffer buffer = new StringBuffer(2*value.length());
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            switch(c) {
            case '\\' : buffer.append("\\\\"); break;
            case '\t' : buffer.append("\\t"); break;
            case '\n' : buffer.append("\\n"); break;
            case '\r' : buffer.append("\\r"); break;
            case '\b' : buffer.append("\\b"); break;
            case '\"' : buffer.append("\\\""); break;
            case '\'' : buffer.append("\\\'"); break;
            default : buffer.append(c);
            }
        }

        return new String(buffer);
    }


    public static boolean isWhiteSpace(char c) {
        switch(c) {
        case ' ':
        case '\t':
        case '\r':
        case '\n': return true;
        default: return false;		
        }

    }

    public static boolean[] copyOf(boolean[] a) {
        if(a == null) return null;
        boolean[] b = new boolean[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static char[] copyOf(char[] a) {
        if(a == null) return null;
        char[] b = new char[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static short[] copyOf(short[] a) {
        if(a == null) return null;
        short[] b = new short[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static int[] copyOf(int[] a) {
        if(a == null) return null;
        int[] b = new int[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static long[] copyOf(long[] a) {
        if(a == null) return null;
        long[] b = new long[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static float[] copyOf(float[] a) {
        if(a == null) return null;
        float[] b = new float[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static double[] copyOf(double[] a) {
        if(a == null) return null;
        double[] b = new double[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }


    public static String getFullVersion() {
        if(revision == null) return version;
        if(revision.length() == 0) return version;
        return version + " (" + revision + ")";
    }


    @SuppressWarnings("resource")
    public static BufferedReader getReader(String fileName) throws IOException {
        BufferedReader in = null;

        try { in = getFileReader(fileName); }
        catch(IOException e) {		
            if(!fileName.contains("://")) fileName = "http://" + fileName;
            in = getURLReader(fileName); 
        }
        if(in == null) throw new FileNotFoundException(fileName);

        return in;
    }


    public static BufferedReader getFileReader(String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
    }


    public static BufferedReader getURLReader(String address) throws IOException {
        URL versionURL = new URL(address);
        URLConnection connection = versionURL.openConnection();

        connection.setConnectTimeout(3000);
        connection.setReadTimeout(2000);
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));

    }
    
    
    public final static boolean equals(final double a, final double b, final double precision) {
        if(a == b) return true;
        if(Math.getExponent(a) == 0) return (Math.abs(a - b) <= precision);     
        return (Math.abs(a - b) / Math.max(Math.abs(a), Math.abs(b)) <= precision);
    }
    
   
    public final static boolean fixedPrecisionEquals(final double a, final double b, final double precision) {
        if(a == b) return true;
        return (Math.abs(a - b) <= precision);     
    }
    
    
    public static boolean equals(Object a, Object b) {
        if(a == null) return b == null;
        if(b == null) return false;
        return a.equals(b);
    }
   
    /**
     * Generates a String with the desired number of white space characters.
     *
     * @param n         the number of white space characters
     * @return          the string consisting of the specified number of white spaces alone. 
     */
    public static String spaces(int n) {
        if(n < 1) return "";

        StringBuffer buf = new StringBuffer(n);
        for(int i=n; --i >= 0; ) buf.append(" ");
        return new String(buf);
    }
    
    public static String getWhiteSpaceChars() { return " \t\r\n"; }
    
  

    public static void info(Object owner, String message) { reporter.info(owner, message); }


    public static void notify(Object owner, String message) { reporter.notify(owner, message); }

    public static void debug(Object owner, String message) { reporter.debug(owner, message); }

    public static void warning(Object owner, String message) { reporter.warning(owner, message); }

    public static void warning(Object owner, Exception e, boolean debug) { reporter.warning(owner, e, debug); }

    public static void warning(Object owner, Exception e) { reporter.warning(owner, e, debug); }

    public static void error(Object owner, String message) { reporter.error(owner, message); }

    public static void error(Object owner, Throwable e, boolean debug) { reporter.error(owner, e, debug); }

    public static void error(Object owner, Throwable e) { reporter.error(owner, e); }

    public static void status(Object owner, String message) { reporter.status(owner, message); }

    public static void result(Object owner, String message) { reporter.result(owner, message); }

    public static void detail(Object owner, String message) { reporter.detail(owner, message); }

    public static void values(Object owner, String message) { reporter.values(owner, message); }

    public static void suggest(Object owner, String message) { reporter.suggest(owner, message); }

    public static void trace(Throwable e) { reporter.trace(e); }

    public static Reporter getDefaultReporter() { return reporter; }

    public static void setReporter(Reporter r) { reporter = r; }

    public static void setDefaultReporter() { setReporter(defaultReporter); }


    public static final Reporter defaultReporter = new ConsoleReporter("jnum-default");

    public static Reporter reporter = defaultReporter;

    
    public final static DecimalFormat f0 = new DecimalFormat("0");

    public final static DecimalFormat f1 = new DecimalFormat("0.0");

    public final static DecimalFormat f2 = new DecimalFormat("0.00");

    public final static DecimalFormat f3 = new DecimalFormat("0.000");

    public final static DecimalFormat f4 = new DecimalFormat("0.0000");

    public final static DecimalFormat f5 = new DecimalFormat("0.00000");

    public final static DecimalFormat f6 = new DecimalFormat("0.000000");

    public final static DecimalFormat f7 = new DecimalFormat("0.0000000");

    public final static DecimalFormat f8 = new DecimalFormat("0.00000000");

    public final static DecimalFormat f9 = new DecimalFormat("0.000000000");

    public final static DecimalFormat f10 = new DecimalFormat("0.0000000000");

    public final static DecimalFormat f11 = new DecimalFormat("0.00000000000");

    public final static DecimalFormat f12 = new DecimalFormat("0.000000000000");

    public final static DecimalFormat F0 = new DecimalFormat("0");

    public final static DecimalFormat F1 = new DecimalFormat("0.#");

    public final static DecimalFormat F2 = new DecimalFormat("0.##");

    public final static DecimalFormat F3 = new DecimalFormat("0.###");

    public final static DecimalFormat F4 = new DecimalFormat("0.####");

    public final static DecimalFormat F5 = new DecimalFormat("0.#####");

    public final static DecimalFormat F6 = new DecimalFormat("0.######");

    public final static DecimalFormat F7 = new DecimalFormat("0.#######");

    public final static DecimalFormat F8 = new DecimalFormat("0.########");

    public final static DecimalFormat F9 = new DecimalFormat("0.#########");
    
    public final static DecimalFormat F10 = new DecimalFormat("0.##########");

    public final static DecimalFormat F11 = new DecimalFormat("0.###########");

    public final static DecimalFormat F12 = new DecimalFormat("0.############");



    public final static DecimalFormat e0 = new DecimalFormat("0E0");

    public final static DecimalFormat e1 = new DecimalFormat("0.0E0");

    public final static DecimalFormat e2 = new DecimalFormat("0.00E0");

    public final static DecimalFormat e3 = new DecimalFormat("0.000E0");

    public final static DecimalFormat e4 = new DecimalFormat("0.0000E0");

    public final static DecimalFormat e5 = new DecimalFormat("0.00000E0");

    public final static DecimalFormat e6 = new DecimalFormat("0.000000E0");

    public final static DecimalFormat e7 = new DecimalFormat("0.0000000E0");

    public final static DecimalFormat e8 = new DecimalFormat("0.00000000E0");

    public final static DecimalFormat e9 = new DecimalFormat("0.000000000E0");

    public final static DecimalFormat e10 = new DecimalFormat("0.0000000000E0");

    public final static DecimalFormat e11 = new DecimalFormat("0.00000000000E0");

    public final static DecimalFormat e12 = new DecimalFormat("0.000000000000E0");



    public final static DecimalFormat E0 = new DecimalFormat("0E0");

    public final static DecimalFormat E1 = new DecimalFormat("0.#E0");

    public final static DecimalFormat E2 = new DecimalFormat("0.##E0");

    public final static DecimalFormat E3 = new DecimalFormat("0.###E0");

    public final static DecimalFormat E4 = new DecimalFormat("0.####E0");

    public final static DecimalFormat E5 = new DecimalFormat("0.#####E0");

    public final static DecimalFormat E6 = new DecimalFormat("0.######E0");

    public final static DecimalFormat E7 = new DecimalFormat("0.#######E0");

    public final static DecimalFormat E8 = new DecimalFormat("0.########E0");

    public final static DecimalFormat E9 = new DecimalFormat("0.#########E0");

    public final static DecimalFormat E10 = new DecimalFormat("0.##########E0");

    public final static DecimalFormat E11 = new DecimalFormat("0.###########E0");

    public final static DecimalFormat E12 = new DecimalFormat("0.############E0");



    public final static DecimalFormat d1 = new DecimalFormat("0");

    public final static DecimalFormat d2 = new DecimalFormat("00");

    public final static DecimalFormat d3 = new DecimalFormat("000");

    public final static DecimalFormat d4 = new DecimalFormat("0000");

    public final static DecimalFormat d5 = new DecimalFormat("00000");

    public final static DecimalFormat d6 = new DecimalFormat("000000");

    public final static DecimalFormat d7 = new DecimalFormat("0000000");

    public final static DecimalFormat d8 = new DecimalFormat("00000000");

    public final static DecimalFormat d9 = new DecimalFormat("000000000");

    public final static DecimalFormat d10 = new DecimalFormat("0000000000");

    public final static DecimalFormat d11 = new DecimalFormat("00000000000");

    public final static DecimalFormat d12 = new DecimalFormat("000000000000");




    public final static SignificantFiguresFormat s1 = new SignificantFiguresFormat(1);

    public final static SignificantFiguresFormat s2 = new SignificantFiguresFormat(2);

    public final static SignificantFiguresFormat s3 = new SignificantFiguresFormat(3);

    public final static SignificantFiguresFormat s4 = new SignificantFiguresFormat(4);

    public final static SignificantFiguresFormat s5 = new SignificantFiguresFormat(5);

    public final static SignificantFiguresFormat s6 = new SignificantFiguresFormat(6);

    public final static SignificantFiguresFormat s7 = new SignificantFiguresFormat(7);

    public final static SignificantFiguresFormat s8 = new SignificantFiguresFormat(8);

    public final static SignificantFiguresFormat s9 = new SignificantFiguresFormat(9);

    public final static SignificantFiguresFormat s10 = new SignificantFiguresFormat(10);

    public final static SignificantFiguresFormat s11 = new SignificantFiguresFormat(11);

    public final static SignificantFiguresFormat s12 = new SignificantFiguresFormat(12);


    
    public final static SignificantFiguresFormat S1 = new SignificantFiguresFormat(1, false);

    public final static SignificantFiguresFormat S2 = new SignificantFiguresFormat(2, false);

    public final static SignificantFiguresFormat S3 = new SignificantFiguresFormat(3, false);

    public final static SignificantFiguresFormat S4 = new SignificantFiguresFormat(4, false);

    public final static SignificantFiguresFormat S5 = new SignificantFiguresFormat(5, false);

    public final static SignificantFiguresFormat S6 = new SignificantFiguresFormat(6, false);

    public final static SignificantFiguresFormat S7 = new SignificantFiguresFormat(7, false);

    public final static SignificantFiguresFormat S8 = new SignificantFiguresFormat(8, false);

    public final static SignificantFiguresFormat S9 = new SignificantFiguresFormat(9, false);

    public final static SignificantFiguresFormat S10 = new SignificantFiguresFormat(10, false);

    public final static SignificantFiguresFormat S11 = new SignificantFiguresFormat(11, false);

    public final static SignificantFiguresFormat S12 = new SignificantFiguresFormat(12, false);



    public final static HourAngleFormat hf0 = new HourAngleFormat(0);

    public final static HourAngleFormat hf1 = new HourAngleFormat(1);

    public final static HourAngleFormat hf2 = new HourAngleFormat(2);

    public final static HourAngleFormat hf3 = new HourAngleFormat(3);

    public final static HourAngleFormat hf4 = new HourAngleFormat(4);

    public final static HourAngleFormat hf5 = new HourAngleFormat(5);

    public final static HourAngleFormat hf6 = new HourAngleFormat(6);

    public final static HourAngleFormat hf7 = new HourAngleFormat(7);

    public final static HourAngleFormat hf8 = new HourAngleFormat(8);

    public final static HourAngleFormat hf9 = new HourAngleFormat(9);

    public final static HourAngleFormat hf10 = new HourAngleFormat(10);

    public final static HourAngleFormat hf11 = new HourAngleFormat(11);

    public final static HourAngleFormat hf12 = new HourAngleFormat(12);





    public final static AngleFormat af0 = new AngleFormat(0);

    public final static AngleFormat af1 = new AngleFormat(1);

    public final static AngleFormat af2 = new AngleFormat(2);

    public final static AngleFormat af3 = new AngleFormat(3);

    public final static AngleFormat af4 = new AngleFormat(4);

    public final static AngleFormat af5 = new AngleFormat(5);

    public final static AngleFormat af6 = new AngleFormat(6);

    public final static AngleFormat af7 = new AngleFormat(7);

    public final static AngleFormat af8 = new AngleFormat(8);

    public final static AngleFormat af9 = new AngleFormat(9);

    public final static AngleFormat af10 = new AngleFormat(10);

    public final static AngleFormat af11 = new AngleFormat(11);

    public final static AngleFormat af12 = new AngleFormat(12);



    public final static TimeFormat tf0 = new TimeFormat(0);

    public final static TimeFormat tf1 = new TimeFormat(1);

    public final static TimeFormat tf2 = new TimeFormat(2);

    public final static TimeFormat tf3 = new TimeFormat(3);

    public final static TimeFormat tf4 = new TimeFormat(4);

    public final static TimeFormat tf5 = new TimeFormat(5);

    public final static TimeFormat tf6 = new TimeFormat(6);

    public final static TimeFormat tf7 = new TimeFormat(7);

    public final static TimeFormat tf8 = new TimeFormat(8);

    public final static TimeFormat tf9 = new TimeFormat(9);

    public final static TimeFormat tf10 = new TimeFormat(10);

    public final static TimeFormat tf11 = new TimeFormat(11);

    public final static TimeFormat tf12 = new TimeFormat(12);



    public final static DecimalFormat[] 
            e = { e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12 },
            E = { E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12 },
            f = { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 },
            F = { F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12 },
            d = { null, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12 };


    public final static SignificantFiguresFormat[] 
            s = { null, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12 },
            S = { null, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12 };

    public final static HourAngleFormat[] hf = { hf0, hf1, hf2, hf3, hf4, hf5, hf6, hf7, hf8, hf9, hf10, hf11, hf12 };

    public final static AngleFormat[] af = { af0, af1, af2, af3, af4, af5, af6, af7, af8, af9, af10, af11, af12 };

    public final static TimeFormat[] tf = { tf0, tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12 };

    
}


