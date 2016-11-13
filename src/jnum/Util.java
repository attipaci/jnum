/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
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


// TODO: Auto-generated Javadoc
/**
 * The Class Util.
 */
public final class Util {

    /** The Constant version. */
    public final static String version = "0.10-2";

    /** The Constant revision. */
    public final static String revision = "devel.2";

    /** The debug. */
    public static boolean debug = false;

    /**
     * Gets the decimal format.
     *
     * @param significance the significance
     * @return the decimal format
     */
    public static NumberFormat getDecimalFormat(double significance) {
        return getDecimalFormat(significance, 6, true);
    }

    /**
     * Gets the decimal format.
     *
     * @param significance the significance
     * @param trailingZeroes the trailing zeroes
     * @return the decimal format
     */
    public static NumberFormat getDecimalFormat(double significance, boolean trailingZeroes) {
        return getDecimalFormat(significance, 6, trailingZeroes);
    }

    /**
     * Gets the decimal format.
     *
     * @param significance the significance
     * @param maxDecimals the max decimals
     * @return the decimal format
     */
    public static NumberFormat getDecimalFormat(double significance, int maxDecimals) {
        return getDecimalFormat(significance, maxDecimals, true);
    }

    /**
     * Gets the decimal format.
     *
     * @param significance the significance
     * @param maxDecimals the max decimals
     * @param trailingZeroes the trailing zeroes
     * @return the decimal format
     */
    public static NumberFormat getDecimalFormat(double significance, int maxDecimals, boolean trailingZeroes) {
        if(Double.isNaN(significance)) return trailingZeroes ?  f1 : F1;
        if(significance == 0.0) return trailingZeroes ? f2 : F2;
        int figures = Math.min(maxDecimals, (int) Math.floor(Math.log10(Math.abs(significance))) + 2);
        figures = Math.max(1, figures);
        return trailingZeroes ? s[figures] : S[figures];
    }

    /**
     * Return the time of day for a given time value.
     *
     * @param time the time
     * @return time betweem 0-24h.
     */
    public final static double timeOfDay(double time) {
        time = Math.IEEEremainder(time, Unit.day);
        if(time < 0.0) time += Unit.day;
        return time;
    }

    /**
     * Return the equivalent time value for angle. Useful for converting angles to right-ascention or hour-angle
     *
     * @param angle the angle
     * @return a time.
     * @see angleOfTime
     */
    public final static double timeOfAngle(final double angle) {
        return timeOfDay(angle * Unit.second/Unit.secondAngle);
    }

    /**
     * Return the equivalent angle value for a time value. Useful for converting right-ascention or hour-angle to radians.
     *
     * @param time the time
     * @return an angle.
     * @see timeOfAngle
     */
    public final static double angleOfTime(final double time) {
        return ExtraMath.standardAngle(time * Unit.secondAngle/Unit.second);
    }

    /**
     * Construct an angle from degree, arc-minute and arc-second values.
     *
     * @param D the d
     * @param M the m
     * @param S the s
     * @return an angle.
     * @see DMS
     */    
    public static double DMS(final int D, final int M, final double S) {
        return D*Unit.degree + M*Unit.arcmin + S*Unit.arcsec;
    }

    /**
     * Construct an time from hour, minute and second values.
     *
     * @param H the h
     * @param M the m
     * @param S the s
     * @return an angle.
     * @see HMS
     */ 

    public static double HMS(final int H, final int M, final double S) {
        return H*Unit.hour + M * Unit.minute + S * Unit.second;
    } 

    /**
     * Hms.
     *
     * @param time the time
     * @return the string
     */
    public static String HMS(final double time) { return HMS(time, f[3]); }

    /**
     * Hms.
     *
     * @param time the time
     * @param precision the precision
     * @return the string
     */
    public static String HMS(final double time, final int precision) { return HMS(time, f[precision]); }

    /**
     * Hms.
     *
     * @param time the time
     * @param df the df
     * @return the string
     */
    public static String HMS(double time, DecimalFormat df) {
        if(time < 0.0) return("-" + HMS(-time, df));

        time /= Unit.hour; int hour = (int)time; time -= hour;
        time *= 60.0; int min = (int)time; time -= min;

        return d2.format(hour) + ":" + d2.format(min) + ":" + df.format(60.0 * time);
    }

    /**
     * Short hms.
     *
     * @param time the time
     * @param df the df
     * @return the string
     */
    public static String shortHMS(double time, DecimalFormat df) {
        if(time < 0.0) return("-" + shortHMS(-time, df));

        time /= Unit.hour; int hour = (int)time; time -= hour;
        time *= 60.0; int min = (int)time; time -= min;

        return (hour > 0 ? d2.format(hour) + ":" : "") + (min > 0 ? d2.format(min) + ":" : "") + df.format(60.0 * time);
    }



    /**
     * Dms.
     *
     * @param angle the angle
     * @return the string
     */
    public static String DMS(double angle) { return DMS(angle, f[3]); }

    /**
     * Dms.
     *
     * @param angle the angle
     * @param precision the precision
     * @return the string
     */
    public static String DMS(double angle, int precision) { return DMS(angle, f[precision]); }

    /**
     * Dms.
     *
     * @param angle the angle
     * @param df the df
     * @return the string
     */
    public static String DMS(double angle, DecimalFormat df) {
        if(angle < 0.0) return("-" + DMS(-angle, df));

        angle /= Unit.degree; int d = (int)angle; angle -= d;
        angle *= 60.0; int m = (int)angle; angle -= m;

        return d2.format(d) + ":" + d2.format(m) + ":" + df.format(60.0 * angle);
    }

    /**
     * Short dms.
     *
     * @param angle the angle
     * @param df the df
     * @return the string
     */
    public static String shortDMS(double angle, DecimalFormat df) {
        if(angle < 0.0) return("-" + shortDMS(-angle, df));

        angle /= Unit.degree; int d = (int)angle; angle -= d;
        angle *= 60.0; int m = (int)angle; angle -= m;

        return (d > 0 ? d2.format(d) + ":" : "") + (m > 0 ? d2.format(m) + ":" : "") + df.format(60.0 * angle);
    }


    /**
     * Parses the time.
     *
     * @param HMS the hms
     * @return the double
     */
    public static double parseTime(String HMS) {
        StringTokenizer tokens = new StringTokenizer(HMS, " \t\n\r:hmsHMS");

        double time = Integer.parseInt(tokens.nextToken()) * Unit.hour;
        time += Integer.parseInt(tokens.nextToken()) * Unit.min;
        time += Double.parseDouble(tokens.nextToken()) * Unit.second;

        return time;
    }

    /**
     * Parses the angle.
     *
     * @param DMS the dms
     * @return the double
     */
    public static double parseAngle(String DMS) {
        StringTokenizer tokens = new StringTokenizer(DMS, "- \t\n\r:dmsDMS");

        double angle = Integer.parseInt(tokens.nextToken()) * Unit.degree;
        angle += Integer.parseInt(tokens.nextToken()) * Unit.arcmin;
        angle += Double.parseDouble(tokens.nextToken()) * Unit.arcsec;

        if(DMS.indexOf('-') >= 0) angle *= -1;

        return angle;
    }

    /**
     * Parses the boolean.
     *
     * @param value the value
     * @return true, if successful
     * @throws NumberFormatException the number format exception
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
    /**
     * Prints the contents.
     *
     * @param object the object
     */
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






    /**
     * Gets the system path.
     *
     * @param spec the spec
     * @return the system path
     */
    public static String getSystemPath(String spec) {
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

    /**
     * Unsigned.
     *
     * @param b the b
     * @return the short
     */
    public final static short unsigned(final byte b) {
        return ((short)(b & 0xff));
    }	


    /**
     * Unsigned.
     *
     * @param s the s
     * @return the int
     */
    public final static int unsigned(final short s) {
        return (s & 0xffff);
    }


    /**
     * Unsigned.
     *
     * @param i the i
     * @return the long
     */
    public final static long unsigned(final int i) {
        return (i & 0xffffffffL);
    }


    /**
     * Unsigned byte.
     *
     * @param value the value
     * @return the byte
     */
    public final static byte unsignedByte(final short value) {
        return (byte)(value & 0xff);
    }

    /**
     * Unsigned short.
     *
     * @param value the value
     * @return the short
     */
    public final static short unsignedShort(final int value) {
        return (short)(value & 0xffff);
    }

    /**
     * Unsigned int.
     *
     * @param value the value
     * @return the int
     */
    public final static int unsignedInt(final long value) {
        return (int)(value & 0xffffffffL);
    }

    /**
     * Pseudo unsigned.
     *
     * @param l the l
     * @return the long
     */
    public final static long pseudoUnsigned(final long l) {
        return l < 0L ? Long.MAX_VALUE : l;
    }

    /**
     * Unsigned.
     *
     * @param b the b
     * @return the short[]
     */
    public final static short[] unsigned(final byte[] b) {
        final short[] s = new short[b.length];
        for(int i=0; i<b.length; i++) s[i] = unsigned(b[i]);	
        return s;
    }

    /**
     * Unsigned.
     *
     * @param s the s
     * @return the int[]
     */
    public final static int[] unsigned(final short[] s) {
        final int[] i = new int[s.length];
        for(int j=0; j<s.length; j++) i[j] = unsigned(s[j]);	
        return i;
    }

    /**
     * Unsigned.
     *
     * @param i the i
     * @return the long[]
     */
    public final static long[] unsigned(final int[] i) {
        final long[] l = new long[i.length];
        for(int j=0; j<i.length; j++) l[j] = unsigned(i[j]);	
        return l;
    }

    /**
     * Pseudo unsigned.
     *
     * @param l the l
     */
    public final static void pseudoUnsigned(final long[] l) {
        for(int j=0; j<l.length; j++) l[j] = pseudoUnsigned(l[j]);	
    }


    /**
     * Equals.
     *
     * @param a the a
     * @param b the b
     * @param precision the precision
     * @return true, if successful
     */
    public final static boolean equals(final double a, final double b, final double precision) {
        if(b == 0.0) {
            if(Math.abs(a) > precision) return false; 
        }
        else if(Math.abs(a / b - 1.0) > precision) return false;
        return true;
    }

    /**
     * Gets the property.
     *
     * @param name the name
     * @return the property
     */
    public final static String getProperty(String name) {
        String value = System.getProperty(name);
        return value == null ? "n/a" : value;
    }

    /**
     * Default format.
     *
     * @param value the value
     * @param f the f
     * @return the string
     */
    public static String defaultFormat(double value, NumberFormat f) {
        return f == null ? Double.toString(value) : f.format(value);
    }

    /**
     * From escaped string.
     *
     * @param value the value
     * @return the string
     * @throws IllegalStateException the illegal state exception
     */
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

    /**
     * To escaped string.
     *
     * @param value the value
     * @return the string
     */
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

    /**
     * Check if the argument is a white-space character.
     *
     * @param c the c
     * @return true if c is a white-space character
     */
    public static boolean isWhiteSpace(char c) {
        switch(c) {
        case ' ':
        case '\t':
        case '\r':
        case '\n': return true;
        default: return false;		
        }

    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the boolean[]
     */
    public static boolean[] copyOf(boolean[] a) {
        if(a == null) return null;
        boolean[] b = new boolean[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the char[]
     */
    public static char[] copyOf(char[] a) {
        if(a == null) return null;
        char[] b = new char[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the short[]
     */
    public static short[] copyOf(short[] a) {
        if(a == null) return null;
        short[] b = new short[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the int[]
     */
    public static int[] copyOf(int[] a) {
        if(a == null) return null;
        int[] b = new int[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the long[]
     */
    public static long[] copyOf(long[] a) {
        if(a == null) return null;
        long[] b = new long[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the float[]
     */
    public static float[] copyOf(float[] a) {
        if(a == null) return null;
        float[] b = new float[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Copy of.
     *
     * @param a the a
     * @return the double[]
     */
    public static double[] copyOf(double[] a) {
        if(a == null) return null;
        double[] b = new double[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    /**
     * Return the full version information for the utility classes.
     *
     * @return the full version
     */
    public static String getFullVersion() {
        if(revision == null) return version;
        if(revision.length() == 0) return version;
        return version + " (" + revision + ")";
    }


    /**
     * Gets the reader.
     *
     * @param fileName the file name
     * @return the reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
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

    /**
     * Gets the file reader.
     *
     * @param fileName the file name
     * @return the file reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static BufferedReader getFileReader(String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
    }

    /**
     * Gets the URL reader.
     *
     * @param address the address
     * @return the URL reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static BufferedReader getURLReader(String address) throws IOException {
        URL versionURL = new URL(address);
        URLConnection connection = versionURL.openConnection();

        connection.setConnectTimeout(3000);
        connection.setReadTimeout(2000);
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));

    }

    
    /**
     * Equals.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    public static boolean equals(Object a, Object b) {
        if(a == null) return b == null;
        if(b == null) return a == null;	
        return a.equals(b);
    }
   
    /**
     * Generates a String with the desired number of white space characters.
     *
     * @param n the number of white space characters
     * @return the string consisting of the specified number of white spaces alone. 
     */
    public static String spaces(int n) {
        if(n < 1) return "";

        StringBuffer buf = new StringBuffer(n);
        for(int i=n; --i >= 0; ) buf.append(" ");
        return new String(buf);
    }

    /**
     * Info.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void info(Object owner, String message) { reporter.info(owner, message); }

    /**
     * Notify.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void notify(Object owner, String message) { reporter.notify(owner, message); }

    /**
     * Debug.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void debug(Object owner, String message) { reporter.debug(owner, message); }

    /**
     * Warning.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void warning(Object owner, String message) { reporter.warning(owner, message); }

    /**
     * Warning.
     *
     * @param owner the owner
     * @param e the e
     * @param debug the debug
     */
    public static void warning(Object owner, Exception e, boolean debug) { reporter.warning(owner, e, debug); }

    /**
     * Warning.
     *
     * @param owner the owner
     * @param e the e
     */
    public static void warning(Object owner, Exception e) { reporter.warning(owner, e, debug); }

    /**
     * Error.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void error(Object owner, String message) { reporter.error(owner, message); }

    /**
     * Error.
     *
     * @param owner the owner
     * @param e the e
     * @param debug the debug
     */
    public static void error(Object owner, Throwable e, boolean debug) { reporter.error(owner, e, debug); }

    /**
     * Error.
     *
     * @param owner the owner
     * @param e the e
     */
    public static void error(Object owner, Throwable e) { reporter.error(owner, e); }

    /**
     * Status.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void status(Object owner, String message) { reporter.status(owner, message); }

    /**
     * Result.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void result(Object owner, String message) { reporter.result(owner, message); }

    /**
     * Detail.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void detail(Object owner, String message) { reporter.detail(owner, message); }

    /**
     * Values.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void values(Object owner, String message) { reporter.values(owner, message); }

    /**
     * Suggest.
     *
     * @param owner the owner
     * @param message the message
     */
    public static void suggest(Object owner, String message) { reporter.suggest(owner, message); }

    /**
     * Trace.
     *
     * @param e the e
     */
    public static void trace(Throwable e) { reporter.trace(e); }

    
    /**
     * Gets the default reporter.
     *
     * @return the default reporter
     */
    public static Reporter getDefaultReporter() { return reporter; }

    /**
     * Sets the reporter.
     *
     * @param r the new reporter
     */
    public static void setReporter(Reporter r) { reporter = r; }

    /**
     * Sets the default reporter.
     */
    public static void setDefaultReporter() { setReporter(defaultReporter); }

    /** The Constant defaultReporter. */
    public static final Reporter defaultReporter = new ConsoleReporter("jnum-default");

    /** The reporter. */
    public static Reporter reporter = defaultReporter;

    /** The Constant f0. */
    public final static DecimalFormat f0 = new DecimalFormat("0");

    /** The Constant f1. */
    public final static DecimalFormat f1 = new DecimalFormat("0.0");

    /** The Constant f2. */
    public final static DecimalFormat f2 = new DecimalFormat("0.00");

    /** The Constant f3. */
    public final static DecimalFormat f3 = new DecimalFormat("0.000");

    /** The Constant f4. */
    public final static DecimalFormat f4 = new DecimalFormat("0.0000");

    /** The Constant f5. */
    public final static DecimalFormat f5 = new DecimalFormat("0.00000");

    /** The Constant f6. */
    public final static DecimalFormat f6 = new DecimalFormat("0.000000");

    /** The Constant f7. */
    public final static DecimalFormat f7 = new DecimalFormat("0.0000000");

    /** The Constant f8. */
    public final static DecimalFormat f8 = new DecimalFormat("0.00000000");

    /** The Constant f9. */
    public final static DecimalFormat f9 = new DecimalFormat("0.000000000");

    /** The Constant f10. */
    public final static DecimalFormat f10 = new DecimalFormat("0.0000000000");

    /** The Constant f11. */
    public final static DecimalFormat f11 = new DecimalFormat("0.00000000000");

    /** The Constant f12. */
    public final static DecimalFormat f12 = new DecimalFormat("0.000000000000");

    /** The Constant F0. */
    public final static DecimalFormat F0 = new DecimalFormat("0");

    /** The Constant F1. */
    public final static DecimalFormat F1 = new DecimalFormat("0.#");

    /** The Constant F2. */
    public final static DecimalFormat F2 = new DecimalFormat("0.##");

    /** The Constant F3. */
    public final static DecimalFormat F3 = new DecimalFormat("0.###");

    /** The Constant F4. */
    public final static DecimalFormat F4 = new DecimalFormat("0.####");

    /** The Constant F5. */
    public final static DecimalFormat F5 = new DecimalFormat("0.#####");

    /** The Constant F6. */
    public final static DecimalFormat F6 = new DecimalFormat("0.######");

    /** The Constant F7. */
    public final static DecimalFormat F7 = new DecimalFormat("0.#######");

    /** The Constant F8. */
    public final static DecimalFormat F8 = new DecimalFormat("0.########");

    /** The Constant F9. */
    public final static DecimalFormat F9 = new DecimalFormat("0.#########");

    /** The Constant F10. */
    public final static DecimalFormat F10 = new DecimalFormat("0.##########");

    /** The Constant F11. */
    public final static DecimalFormat F11 = new DecimalFormat("0.###########");

    /** The Constant F12. */
    public final static DecimalFormat F12 = new DecimalFormat("0.############");



    /** The Constant e0. */
    public final static DecimalFormat e0 = new DecimalFormat("0E0");

    /** The Constant e1. */
    public final static DecimalFormat e1 = new DecimalFormat("0.0E0");

    /** The Constant e2. */
    public final static DecimalFormat e2 = new DecimalFormat("0.00E0");

    /** The Constant e3. */
    public final static DecimalFormat e3 = new DecimalFormat("0.000E0");

    /** The Constant e4. */
    public final static DecimalFormat e4 = new DecimalFormat("0.0000E0");

    /** The Constant e5. */
    public final static DecimalFormat e5 = new DecimalFormat("0.00000E0");

    /** The Constant e6. */
    public final static DecimalFormat e6 = new DecimalFormat("0.000000E0");

    /** The Constant e7. */
    public final static DecimalFormat e7 = new DecimalFormat("0.0000000E0");

    /** The Constant e8. */
    public final static DecimalFormat e8 = new DecimalFormat("0.00000000E0");

    /** The Constant e9. */
    public final static DecimalFormat e9 = new DecimalFormat("0.000000000E0");

    /** The Constant e10. */
    public final static DecimalFormat e10 = new DecimalFormat("0.0000000000E0");

    /** The Constant e11. */
    public final static DecimalFormat e11 = new DecimalFormat("0.00000000000E0");

    /** The Constant e12. */
    public final static DecimalFormat e12 = new DecimalFormat("0.000000000000E0");



    /** The Constant E0. */
    public final static DecimalFormat E0 = new DecimalFormat("0E0");

    /** The Constant E1. */
    public final static DecimalFormat E1 = new DecimalFormat("0.#E0");

    /** The Constant E2. */
    public final static DecimalFormat E2 = new DecimalFormat("0.##E0");

    /** The Constant E3. */
    public final static DecimalFormat E3 = new DecimalFormat("0.###E0");

    /** The Constant E4. */
    public final static DecimalFormat E4 = new DecimalFormat("0.####E0");

    /** The Constant E5. */
    public final static DecimalFormat E5 = new DecimalFormat("0.#####E0");

    /** The Constant E6. */
    public final static DecimalFormat E6 = new DecimalFormat("0.######E0");

    /** The Constant E7. */
    public final static DecimalFormat E7 = new DecimalFormat("0.#######E0");

    /** The Constant E8. */
    public final static DecimalFormat E8 = new DecimalFormat("0.########E0");

    /** The Constant E9. */
    public final static DecimalFormat E9 = new DecimalFormat("0.#########E0");

    /** The Constant E10. */
    public final static DecimalFormat E10 = new DecimalFormat("0.##########E0");

    /** The Constant E11. */
    public final static DecimalFormat E11 = new DecimalFormat("0.###########E0");

    /** The Constant E12. */
    public final static DecimalFormat E12 = new DecimalFormat("0.############E0");



    /** The Constant d1. */
    public final static DecimalFormat d1 = new DecimalFormat("0");

    /** The Constant d2. */
    public final static DecimalFormat d2 = new DecimalFormat("00");

    /** The Constant d3. */
    public final static DecimalFormat d3 = new DecimalFormat("000");

    /** The Constant d4. */
    public final static DecimalFormat d4 = new DecimalFormat("0000");

    /** The Constant d5. */
    public final static DecimalFormat d5 = new DecimalFormat("00000");

    /** The Constant d6. */
    public final static DecimalFormat d6 = new DecimalFormat("000000");

    /** The Constant d7. */
    public final static DecimalFormat d7 = new DecimalFormat("0000000");

    /** The Constant d8. */
    public final static DecimalFormat d8 = new DecimalFormat("00000000");

    /** The Constant d9. */
    public final static DecimalFormat d9 = new DecimalFormat("000000000");

    /** The Constant d10. */
    public final static DecimalFormat d10 = new DecimalFormat("0000000000");

    /** The Constant d11. */
    public final static DecimalFormat d11 = new DecimalFormat("00000000000");

    /** The Constant d12. */
    public final static DecimalFormat d12 = new DecimalFormat("000000000000");




    /** The Constant s1. */
    public final static SignificantFiguresFormat s1 = new SignificantFiguresFormat(1);

    /** The Constant s2. */
    public final static SignificantFiguresFormat s2 = new SignificantFiguresFormat(2);

    /** The Constant s3. */
    public final static SignificantFiguresFormat s3 = new SignificantFiguresFormat(3);

    /** The Constant s4. */
    public final static SignificantFiguresFormat s4 = new SignificantFiguresFormat(4);

    /** The Constant s5. */
    public final static SignificantFiguresFormat s5 = new SignificantFiguresFormat(5);

    /** The Constant s6. */
    public final static SignificantFiguresFormat s6 = new SignificantFiguresFormat(6);

    /** The Constant s7. */
    public final static SignificantFiguresFormat s7 = new SignificantFiguresFormat(7);

    /** The Constant s8. */
    public final static SignificantFiguresFormat s8 = new SignificantFiguresFormat(8);

    /** The Constant s9. */
    public final static SignificantFiguresFormat s9 = new SignificantFiguresFormat(9);

    /** The Constant s10. */
    public final static SignificantFiguresFormat s10 = new SignificantFiguresFormat(10);

    /** The Constant s11. */
    public final static SignificantFiguresFormat s11 = new SignificantFiguresFormat(11);

    /** The Constant s12. */
    public final static SignificantFiguresFormat s12 = new SignificantFiguresFormat(12);



    /** The Constant S1. */
    public final static SignificantFiguresFormat S1 = new SignificantFiguresFormat(1, false);

    /** The Constant S2. */
    public final static SignificantFiguresFormat S2 = new SignificantFiguresFormat(2, false);

    /** The Constant S3. */
    public final static SignificantFiguresFormat S3 = new SignificantFiguresFormat(3, false);

    /** The Constant S4. */
    public final static SignificantFiguresFormat S4 = new SignificantFiguresFormat(4, false);

    /** The Constant S5. */
    public final static SignificantFiguresFormat S5 = new SignificantFiguresFormat(5, false);

    /** The Constant S6. */
    public final static SignificantFiguresFormat S6 = new SignificantFiguresFormat(6, false);

    /** The Constant S7. */
    public final static SignificantFiguresFormat S7 = new SignificantFiguresFormat(7, false);

    /** The Constant S8. */
    public final static SignificantFiguresFormat S8 = new SignificantFiguresFormat(8, false);

    /** The Constant S9. */
    public final static SignificantFiguresFormat S9 = new SignificantFiguresFormat(9, false);

    /** The Constant S10. */
    public final static SignificantFiguresFormat S10 = new SignificantFiguresFormat(10, false);

    /**  The Constant S11. */
    public final static SignificantFiguresFormat S11 = new SignificantFiguresFormat(11, false);

    /** The Constant S12. */
    public final static SignificantFiguresFormat S12 = new SignificantFiguresFormat(12, false);



    /** The Constant hf0. */
    public final static HourAngleFormat hf0 = new HourAngleFormat(0);

    /** The Constant hf1. */
    public final static HourAngleFormat hf1 = new HourAngleFormat(1);

    /** The Constant hf2. */
    public final static HourAngleFormat hf2 = new HourAngleFormat(2);

    /** The Constant hf3. */
    public final static HourAngleFormat hf3 = new HourAngleFormat(3);

    /** The Constant hf4. */
    public final static HourAngleFormat hf4 = new HourAngleFormat(4);

    /** The Constant hf5. */
    public final static HourAngleFormat hf5 = new HourAngleFormat(5);

    /** The Constant hf6. */
    public final static HourAngleFormat hf6 = new HourAngleFormat(6);

    /** The Constant hf7. */
    public final static HourAngleFormat hf7 = new HourAngleFormat(7);

    /** The Constant hf8. */
    public final static HourAngleFormat hf8 = new HourAngleFormat(8);

    /** The Constant hf9. */
    public final static HourAngleFormat hf9 = new HourAngleFormat(9);

    /** The Constant hf10. */
    public final static HourAngleFormat hf10 = new HourAngleFormat(10);

    /** The Constant hf11. */
    public final static HourAngleFormat hf11 = new HourAngleFormat(11);

    /** The Constant hf12. */
    public final static HourAngleFormat hf12 = new HourAngleFormat(12);









    /** The Constant af0. */
    public final static AngleFormat af0 = new AngleFormat(0);

    /** The Constant af1. */
    public final static AngleFormat af1 = new AngleFormat(1);

    /** The Constant af2. */
    public final static AngleFormat af2 = new AngleFormat(2);

    /** The Constant af3. */
    public final static AngleFormat af3 = new AngleFormat(3);

    /** The Constant af4. */
    public final static AngleFormat af4 = new AngleFormat(4);

    /** The Constant af5. */
    public final static AngleFormat af5 = new AngleFormat(5);

    /** The Constant af6. */
    public final static AngleFormat af6 = new AngleFormat(6);

    /** The Constant af7. */
    public final static AngleFormat af7 = new AngleFormat(7);

    /** The Constant af8. */
    public final static AngleFormat af8 = new AngleFormat(8);

    /** The Constant af9. */
    public final static AngleFormat af9 = new AngleFormat(9);

    /** The Constant af10. */
    public final static AngleFormat af10 = new AngleFormat(10);

    /** The Constant af11. */
    public final static AngleFormat af11 = new AngleFormat(11);

    /** The Constant af12. */
    public final static AngleFormat af12 = new AngleFormat(12);



    /** The Constant tf0. */
    public final static TimeFormat tf0 = new TimeFormat(0);

    /** The Constant tf1. */
    public final static TimeFormat tf1 = new TimeFormat(1);

    /** The Constant tf2. */
    public final static TimeFormat tf2 = new TimeFormat(2);

    /** The Constant tf3. */
    public final static TimeFormat tf3 = new TimeFormat(3);

    /** The Constant tf4. */
    public final static TimeFormat tf4 = new TimeFormat(4);

    /** The Constant tf5. */
    public final static TimeFormat tf5 = new TimeFormat(5);

    /** The Constant tf6. */
    public final static TimeFormat tf6 = new TimeFormat(6);

    /** The Constant tf7. */
    public final static TimeFormat tf7 = new TimeFormat(7);

    /** The Constant tf8. */
    public final static TimeFormat tf8 = new TimeFormat(8);

    /** The Constant tf9. */
    public final static TimeFormat tf9 = new TimeFormat(9);

    /** The Constant tf10. */
    public final static TimeFormat tf10 = new TimeFormat(10);

    /** The Constant tf11. */
    public final static TimeFormat tf11 = new TimeFormat(11);

    /** The Constant tf12. */
    public final static TimeFormat tf12 = new TimeFormat(12);


    /** The Constant d. */
    public final static DecimalFormat[] 
            e = { e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12 },
            E = { E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12 },
            f = { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 },
            F = { F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12 },
            d = { null, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12 };

    /** The Constant S. */
    public final static SignificantFiguresFormat[] 
            s = { null, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12 },
            S = { null, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12 }
    ;

    /** The Constant hf. */
    public final static HourAngleFormat[] hf = { hf0, hf1, hf2, hf3, hf4, hf5, hf6, hf7, hf8, hf9, hf10, hf11, hf12 };

    /** The Constant af. */
    public final static AngleFormat[] af = { af0, af1, af2, af3, af4, af5, af6, af7, af8, af9, af10, af11, af12 };

    /** The Constant tf. */
    public final static TimeFormat[] tf = { tf0, tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12 };


}


