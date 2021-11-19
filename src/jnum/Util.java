/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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
import java.util.Arrays;
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
 * @author Attila Kovacs
 */
public final class Util {

    /** The jnum package version */
    public static final String version = "0.40-a1";
    
    /** The jnum package sub-version, if any */
    public static final String revision = "devel.18";
    
    /** Copyright string for the jnum package */
    public static final String copyright = "(c)2021 Attila Kovacs"; 
    
    /** E-mail to include with copyright */
    public static final String copyrightEmail = "<attila[AT]sigmyne.com>"; 

    /** Set to <code>true</code> to get verbose output for debugging. Otherwise <code>false</code> */
    public static boolean debug = false;

    /** private constructor because we don't want to instantiate this class. */
    private Util() {}
    
    /**
     * Returns the full copyright information, including the email address.
     * 
     * @return  the copyright string.
     */
    public static String getCopyrightString() {
        return "Copyright " + copyright + " " + copyrightEmail;
    }
    
    /**
     * Returns an appropriate decimal formating class for representing values with a given significance,
     * but displaying at most 6 decimals places (or fewer, depending on the specified significance).  
     * Trailing zeroes as displayed as approrpriate.
     * 
     * @param significance      the typical S/N ratio (significance) of the values to represent.
     * @return  A number formating instance that will provide an approriate representation of values
     *          with the specified significance.
     *          
     * @see #getDecimalFormat(double, boolean)
     * @see #getDecimalFormat(double, int)
     * @see #getDecimalFormat(double, int, boolean)
     */
    public static NumberFormat getDecimalFormat(double significance) {
        return getDecimalFormat(significance, 6, true);
    }

    /**
     * Returns an appropriate decimal formating class for representing values with a given significance,
     * but displaying at most 6 decimals places (or fewer, depending on the specified significance).
     * This implementation allows to choose whether trailing zeroes are to be shown or not.
     * 
     * @param significance      the typical S/N ratio (significance) of the values to represent.
     * @param trailingZeroes    <code>true</code> if trailing zeroes should be printed as appropriate or,
     *                          <code>false</code> if all trailing zeroes can be omitted.
     * @return  A number formating instance that will provide an approriate representation of values
     *          with the specified significance.
     *          
     * @see #getDecimalFormat(double, int)
     * @see #getDecimalFormat(double, int, boolean)
     */
    public static NumberFormat getDecimalFormat(double significance, boolean trailingZeroes) {
        return getDecimalFormat(significance, 6, trailingZeroes);
    }

    /**
     * Returns an appropriate decimal formating class for representing values with a given significance,
     * but displaying at most the specified number of decimals places (or fewer, depending on the 
     * specified significance). 
     * 
     * @param significance      the typical S/N ratio (significance) of the values to represent.
     * @param maxDecimals       the maximum number of decimal places to use when formating values, regardless
     *                          of significance.
     *                          
     * @return  A number formating instance that will provide an approriate representation of values
     *          with the specified significance.
     *          
     * @see #getDecimalFormat(double, boolean)
     * @see #getDecimalFormat(double, int, boolean)
     */
    public static NumberFormat getDecimalFormat(double significance, int maxDecimals) {
        return getDecimalFormat(significance, maxDecimals, true);
    }

    /**
     * Returns an appropriate decimal formating class for representing values with a given significance,
     * but displaying at most the specificed number of decimals places (or fewer, depending on the 
     * specified significance). This implementation also allows to choose whether trailing zeroes are 
     * to be shown or not.
     * 
     * @param significance      the typical S/N ratio (significance) of the values to represent.
     * @param maxDecimals       the maximum number of decimal places to use when formating values, regardless
     *                          of significance.
     * @param trailingZeroes    <code>true</code> if trailing zeroes should be printed as appropriate or,
     *                          <code>false</code> if all trailing zeroes can be omitted.
     *                          
     * @return  A number formating instance that will provide an approriate representation of values
     *          with the specified significance.
     *          
     * @see #getDecimalFormat(double)
     * @see #getDecimalFormat(double, boolean)
     * @see #getDecimalFormat(double, int, boolean)
     */
    public static NumberFormat getDecimalFormat(double significance, int maxDecimals, boolean trailingZeroes) {
        if(Double.isNaN(significance)) return trailingZeroes ?  f1 : F1;
        if(significance == 0.0) return trailingZeroes ? f2 : F2;
        int figures = Math.min(maxDecimals, (int) Math.floor(Math.log10(Math.abs(significance))) + 2);
        figures = Math.max(1, figures);
        return trailingZeroes ? s[figures] : S[figures];
    }

    /**
     * Returns the time of day for a timestamp.
     * 
     * @param time      (s) Running seconds measured from a reference time that itself is assumed to coincide to the start of
     *                  a new day. E.g. UNIX time seconds (since 0 UT, 1 Jan 1970), or seconds since J2000 (12h TT, 1 Jan 2000).
     * @return          (s) The remainder of the input time with a 24-hour day, in the range of [0.0:86400.0).
     */
    public static final double timeOfDay(double time) {
        return time - Unit.day * Math.floor(time / Unit.day);
    }

    /**
     * Returns the time representation of an angle, where 2&pi; = 24 hours.
     * 
     * @param angle     (rad) an angle
     * @return          (s) The time-of-day equivalent for an angle, in the [0.0:86400.0) range.
     * 
     * @see #timeOfDay(double)
     * @see #angleOfTime(double)
     */
    public static final double timeOfAngle(final double angle) {
        return timeOfDay(angle / Unit.timeAngle);
    }

    /**
     * Returns the angle representation of time, where 24 hours = 2&pi;.
     * 
     * @param time      (s) a time
     * @return          (rad) an angle eqvilalent to time in the [-&pi;:&pi;] range
     * 
     * @see ExtraMath#standardAngle(double)
     * @see #timeOfAngle(double)
     */
    public static final double angleOfTime(final double time) {
        return ExtraMath.standardAngle(time * Unit.timeAngle);
    }

    /**
     * <p>
     * Parses the time from a HMS string representation, such a -2:04:12.334. This methods kes a very liberal approach with formating
     * requirements. It mainly only assumes time starts with an integer hour, which may be followed by an integer
     * minutes and possibly finish with floating point seconds. The three parts may be separated by spaces, tabs, or any of the 
     * characters commonly used to separated time components, such as colons. 
     * </p>
     * 
     * <p>
     * This approach can have pitfalls if the few base assumptions are not held. For example, the string "2m 43s" will
     * end up being parse as if it were 02:43:00, that is 2 hours and 43 minutes (reminder that the first integer must
     * be the hours!). But provided you follow the rules and give it a reasonable input, you should get the expected
     * results. 
     * </p>
     * 
     * @param HMS   a HMS representation of time, starting with the hours component.
     * @return      (s) the time in seconds corresponding to the input string.
     * 
     * @see #parseAngle(String)
     * @see jnum.text.TimeFormat
     * @see jnum.text.HourAngleFormat
     */
    public static double parseTime(String HMS) {
        int sign = 1;
        StringTokenizer tokens = new StringTokenizer(HMS, " \t:hmsHMS'\"" + Symbol.degree + Symbol.prime + Symbol.doublePrime);

        String leading = tokens.nextToken();
        if(leading.charAt(0) == '-') sign = -1;
  
        double time = Integer.parseInt(leading) * Unit.hour;
        if(sign < 0) time = -time;
  
        if(tokens.hasMoreTokens()) time += Integer.parseInt(tokens.nextToken()) * Unit.min;
        if(tokens.hasMoreTokens()) time += Double.parseDouble(tokens.nextToken()) * Unit.second;

        return sign * time;
    }

    /**
     * <p>
     * Parses an angle from a DMS string representation, such a -2:04:12.334. This methods kes a very liberal approach with formating
     * requirements. It mainly only assumes time starts with an integer degrrees, which may be followed by an integer
     * arc-minutes and possibly finish with floating point arc-seconds. The three parts may be separated by spaces, tabs, or any of the 
     * characters commonly used to separated angle components, such as colons. 
     * </p>
     * 
     * <p>
     * This approach can have pitfalls if the few base assumptions are not held. For example, the string "2m 43s" will
     * end up being parse as if it were 02:43:00, that is 2 degrees and 43 arc-minutes (reminder that the first integer must
     * be the degrees!). But provided you follow the rules and give it a reasonable input, you should get the expected
     * results. 
     * </p>
     * 
     * @param DMS   a DMS representation of an angle, starting with the degrees component.
     * @return      (rad) the angle corresponding to the input string.
     * 
     * @see #parseTime(String)
     * @see jnum.text.AngleFormat
     * @see jnum.text.HourAngleFormat
     */
    public static double parseAngle(String DMS) {
        int sign = 1;
        StringTokenizer tokens = new StringTokenizer(DMS, " \t:dmsDMS'\"" + Symbol.prime + Symbol.doublePrime);

        String leading = tokens.nextToken();
        if(leading.charAt(0) == '-') sign = -1;
        
        double angle = Integer.parseInt(leading) * Unit.degree;
        if(angle < 0) angle = -angle;
        
        if(tokens.hasMoreTokens()) angle += Integer.parseInt(tokens.nextToken()) * Unit.arcmin;
        if(tokens.hasMoreTokens()) angle += Double.parseDouble(tokens.nextToken()) * Unit.arcsec;

        return sign * angle;
    }
    
    /**
     * Returns the boolean value associated with the string argument. The
     * String <code>value</code> may be representing a boolean is several ways, with little or no sensitivity to
     * case (see {@link #parseBoolean(String)}, such as "true" or "False", "T" or "F", "yes" or "NO",
     * "Y", or "N", "ON", or "Off", "enabled" or "DISABLED", or "1" or "0".
     * 
     * @param value     a string representation of a boolean value.
     * @return          the Java <code>boolean</code> value represented by the string.
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

    public static void printContents(Object object) {
        // TODO implement with Properties...
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
     * Retuns a resolved path on this system in an OS-independent way, which may contain UNIX-style shorthands
     * to home directories, as well as references to shell environment variables.
     * 
     *  <ul>
     *  <li><b>File separators</b>: '/' can be used universally, regarless of OS convention.</li>
     *  <li><b>Home directories</b>: "~johndoe" can be used to signify tyhe home directory of user "johndoe", or
     *              and "~" (without username) signifies the the user's home directory (like in a UNIX shell).</li>
     *  <li><b>Shell environment variables</b>: "${VAR}" or "{$VAR"} can be used to refer to the value of the environment 
     *              variable VAR.</li>
     *   </ul>
     * 
     * @param spec      The path specification, which may include shorthands for home directories and
     *                  references to environment variables.
     * @return          The resolved path after the requisite substitutions are made.
     */
    public static String getSystemPath(String spec) {
        if(spec == null) return ".";
        if(spec.length() == 0) return ".";
        
        
        String homes = System.getProperty("user.home");

        // Make sure that UNIX-type pathnames are always accepted since these are default
        // to configuration files. This comes at the small price that pathnames are not
        // allowed to contain '/' characters other than path separators...
        if(!File.separator.equals("/")) spec = spec.replace('/', File.separator.charAt(0));
        homes = homes.substring(0, homes.lastIndexOf(File.separator) + 1);

        StringBuffer text = new StringBuffer();

        // See if it's a home directory specification starting with '~'...
        if(spec.charAt(0) == '~') {
            int userChars = spec.indexOf(File.separator) - 1;
            if(userChars < 0) userChars = spec.length() - 1;
            if(userChars == 0) text.append(System.getProperty("user.home"));
            else text.append(homes + spec.substring(1, 1 + userChars));
            spec = spec.substring(userChars + 1);
        }

        while(spec.contains("{$")) {
            int from = spec.indexOf("{$");
            int to = spec.indexOf("}");
            if(to < 0) return text + spec;

            text.append(spec.substring(0, from));
            text.append(System.getenv(spec.substring(from+2, to)));

            spec = spec.substring(to+1);
        }
        
        while(spec.contains("${")) {
            int from = spec.indexOf("${");
            int to = spec.indexOf("}");
            if(to < 0) return text + spec;

            text.append(spec.substring(0, from));
            text.append(System.getenv(spec.substring(from+2, to)));

            spec = spec.substring(to+1);
        }

        text.append(spec);

        return new String(text);

    }

    /**
     * Returns an unsigned <code>byte</code> values as a <code>short</code>. Java does not support unsigned 
     * integer types, but other languages (like C) often do. As such, we sometimes need to read binary
     * data streams from Java, which contain unsigned integer values. For example, the byte value 0xFF written
     * into a file as an unsigned integer is meant to be the value 255, but because Java always treats it as 
     * a signed value, it will read it as -1. The solution is to bump the integer type one up, s.t. the
     * signedness in unambigious. So by making 0xFF (<code>byte</code>) become 0x00FF (<code>short</code>) the value is properly
     * restored as the intended 255.
     * 
     * @param b     The byte value as a signed Java type.
     * @return      The unsigned value of the input byte as a <code>short</code>.
     * 
     * @see #unsigned(byte[])
     * @see #toUnsignedByte(short)
     */
    public static final short unsigned(final byte b) {
        return ((short)(b & 0xff));
    }	

    /**
     * Returns an unsigned 16-bit integer value as an <code>int</code>. Java does not support unsigned 
     * integer types, but other languages (like C) often do. As such, we sometimes need to read binary
     * data streams from Java, which contain unsigned integer values. For example, the byte value 0xFFFF written
     * into a file as an unsigned integer is meant to be the value 65535, but because Java always treats it as 
     * a signed value, it will read it as -1. The solution is to bump the integer type one up, s.t. the
     * signedness in unambigious. So by making 0xFFFF (<code>short</code>) become 0x0000FFFF (<code>int</code>) 
     * the value is properly restored as the intended 65535.
     * 
     * @param s     The 16-bit value as a signed Java type.
     * @return      The unsigned value of the input 16-bit integer as an <code>int</code>.
     * 
     * @see #unsigned(short[])
     * @see #toUnsignedShort(int)
     */
    public static final int unsigned(final short s) {
        return (s & 0xffff);
    }

    /**
     * Returns an unsigned 32-bit integer value as a <code>long</code>. Java does not support unsigned 
     * integer types, but other languages (like C) often do. As such, we sometimes need to read binary
     * data streams from Java, which contain unsigned integer values. For example, the byte value 0xFFFFFFFF written
     * into a file as an unsigned integer is meant to be the value 4294967295, but because Java always treats it as 
     * a signed value, it will read it as -1. The solution is to bump the integer type one up, s.t. the
     * signedness in unambigious. So by making 0xFFFFFFFF (<code>int</code>) become 0x00000000FFFFFFFF (<code>long</code>) 
     * the value is properly restored as the intended 4294967295.
     * 
     * @param i     The 32-bit value as a signed Java type.
     * @return      The unsigned value of the input 32-bit integer as a <code>long</code>.
     * 
     * @see #unsigned(int[])
     * @see #toUnsignedInt(long)
     */
    public static final long unsigned(final int i) {
        return (i & 0xffffffffL);
    }
    
    /**
     * Converts a unsigned 64-bit integer value to an essentially 63-bit Java <code>long</code>,
     * e.g. for writing to a binary stream in the range of 0 to {@link Long#MAX_VALUE}. 
     * 
     * 
     * @param l     The 64-bit value as a Java type.
     * @return      The unsigned 63-bit value as a <code>long</code>.
     * 
     * @throws ArithmeticException  if the input value cannot fit into 63-bits for unsigned representation
     *                              in Java.
     *                              
     */
    public static final long unsigned(final long l) throws ArithmeticException {
        if(l < 0) throw new ArithmeticException("value to large to be represneted in a signd Java type.");
        return l;
    }

    /** 
     * Converts an array of unsigned bytes, represented by a signed Java <code>byte[]</code> array,
     * into a signed Java <code>short[]</code> array. This may be useful for reading binary data that
     * was written from lnaguage with support for unsigned types.
     * 
     * @param b     The input array containing unsigned bytes.
     * @return      A new <code>short[]</code> array containing the unsigned values in the 0 to 255 range.
     * 
     * @see #unsigned(byte)
     */
    public static final short[] unsigned(final byte[] b) {
        final short[] s = new short[b.length];
        for(int i=0; i<b.length; i++) s[i] = unsigned(b[i]);    
        return s;
    }

    /** 
     * Converts an array of unsigned 16-bit integer values, represented by a signed Java <code>short[]</code> array,
     * into a signed Java <code>int[]</code> array. This may be useful for reading binary data that
     * was written from lnaguage with support for unsigned types.
     * 
     * @param s     The input array containing unsigned 16-bit integers.
     * @return      A new <code>int[]</code> array containing the unsigned values in the 0 to 65535 range.
     * 
     * @see #unsigned(short)
     */
    public static final int[] unsigned(final short[] s) {
        final int[] i = new int[s.length];
        for(int j=0; j<s.length; j++) i[j] = unsigned(s[j]);    
        return i;
    }


    /** 
     * Converts an array of unsigned 32-bit integer values, represented by a signed Java <code>int[]</code> array,
     * into a signed Java <code>long[]</code> array. This may be useful for reading binary data that
     * was written from lnaguage with support for unsigned types.
     * 
     * @param i     The input array containing unsigned 16-bit integers.
     * @return      A new <code>long[]</code> array containing the unsigned 32-bit integer values.
     * 
     * @see #unsigned(int)
     */
    public static final long[] unsigned(final int[] i) {
        final long[] l = new long[i.length];
        for(int j=0; j<i.length; j++) l[j] = unsigned(i[j]);    
        return l;
    }
    
    
    /**
     * Converts an unsigned byte value, represented in a Java <code>short</code>, to a signed
     * Java <code>byte</code>, e.g. for writing to a binary stream.
     * 
     * @param value     The value of the unsigned byte, represented as a Java <code>short</code>.
     * @return          The Java <code>byte</code> from the least significant byte of the input.
     * 
     * @see #unsigned(byte)
     */
    public static final byte toUnsignedByte(final short value) {
        return (byte)(value & 0xff);
    }

    /**
     * Converts an unsigned 16-bit value, represented in a Java <code>int</code>, to a signed
     * Java <code>short</code>, e.g. for writing to a binary stream.
     * 
     * @param value     The value of the unsigned 16-bit integer, represented as a Java <code>int</code>.
     * @return          The Java <code>short</code> from the least significant byte of the input.
     * 
     * @see #unsigned(short)
     */
    public static final short toUnsignedShort(final int value) {
        return (short)(value & 0xffff);
    }

    /**
     * Converts an unsigned 32-bit value, represented in a Java <code>long</code>, to a signed
     * Java <code>int</code>, e.g. for writing to a binary stream.
     * 
     * @param value     The value of the unsigned 32-bit integer, represented as a Java <code>long</code>.
     * @return          The Java <code>int</code> from the least significant byte of the input.
     * 
     * @see #unsigned(int)
     */
    public static final int toUnsignedInt(final long value) {
        return (int)(value & 0xffffffffL);
    }


    /**
     * A wrapper around {@link System#getProperty(String, String)}, with "n/a" as the default value.
     * 
     * @param name      Property name
     * @return          The existing value of that property, or else "n/a" if the propertry does not exist.
     * 
     * @see System#getProperty(String, String)
     */
    public static final String getProperty(String name) {
        return System.getProperty(name, "n/a");
    }

    /**
     * Returns a Java string from an escaped character sequence.
     * 
     * @param value     the escaped character sequence
     * @return          a new Java string containing the actual characters defined by the escaped sequences in the input.
     * @throws IllegalStateException    if the string contains illegal escape sequences.
     * 
     * @see #toEscapedString(String)
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
     * Returns an escapped character sequence from an a Java string.
     * 
     * @param value     a Java string.
     * @return          a new string with escaped character sequences.
     * 
     * @see #fromEscapedString(String)
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
     * Copies an array fully. Like {@link Arrays#copyOf(boolean[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(boolean[], int)
     */
    public static boolean[] copyOf(boolean[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }


    /**
     * Copies an array fully. Like {@link Arrays#copyOf(char[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(char[], int)
     */
    public static char[] copyOf(char[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }

    /**
     * Copies an array fully. Like {@link Arrays#copyOf(short[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(short[], int)
     */
    public static short[] copyOf(short[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }

    /**
     * Copies an array fully. Like {@link Arrays#copyOf(int[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(int[], int)
     */
    public static int[] copyOf(int[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }

    /**
     * Copies an array fully. Like {@link Arrays#copyOf(long[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(long[], int)
     */
    public static long[] copyOf(long[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }

    /**
     * Copies an array fully. Like {@link Arrays#copyOf(float[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(float[], int)
     */
    public static float[] copyOf(float[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
    }

    /**
     * Copies an array fully. Like {@link Arrays#copyOf(double[], int)} but always copying the entire array.
     * 
     * @param a     the array to copy
     * @return      a new array with the full copy of the input.
     * 
     * @see Arrays#copyOf(double[], int)
     */
    public static double[] copyOf(double[] a) {
        if(a == null) return null;
        return Arrays.copyOf(a,  a.length);
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
    
    /**
     * <p>
     * Checks if two floating point values are equal to each other within some relative precision.
     * They are considered equal if they are the exact same value, or else if the magnitude of the 
     * difference between the two values, divided by the larger magnitude of the inputs, is less than the 
     * specified precision. That is, the two values are equal if:
     * </p>
     * 
     * <pre>
     *   |a-b| / max(|a|, |b|) &lt; precision
     * </pre>
     * 
     *  
     * 
     * @param a             one of the floating-point values
     * @param b             the other floating-point value
     * @param precision     the relative precision for comparison. E.g. 1e-6 to compare to ~6 significant figures.
     * @return              <code>true</code> if the two values are equal within the specified relative
     *                      precision, otherwise <code>false</code>
     *                      
     * @see Util#fixedPrecisionEquals(double, double, double)
     */
    public static final boolean equals(final double a, final double b, final double precision) {
        if(a == b) return true;     
        return (Math.abs(a - b) / Math.max(Math.abs(a), Math.abs(b)) <= precision);
    }
    
    /**
     * <p>
     * Checks if two floating point values are equal to each other within some absolute precision.
     * They are considered equal if they are the exact same value, or else if the magnitude of the 
     * difference between the two values is less than the specified precision. That is, the two values
     * are equal if:
     * </p>
     * 
     * <pre>
     *   |a-b| &lt; precision
     * </pre>
     * 
     *  
     * 
     * @param a             one of the floating-point values
     * @param b             the other floating-point value
     * @param precision     the absolute precision for comparison.
     * @return              <code>true</code> if the two values are equal within the specified absolute
     *                      precision, otherwise <code>false</code>
     *                      
     * @see Util#equals(double, double, double)
     */
    public static final boolean fixedPrecisionEquals(final double a, final double b, final double precision) {
        if(a == b) return true;
        return (Math.abs(a - b) <= precision);     
    }
    
    /**
     * Checks equality of two objects, including checking for <code>null</code>. 
     * Either or both arguments may be <code>null</code>. If both arguments
     * are <code>null</code>, then they are considered equal.
     * 
     * @param a     One of the objects (may be <code>null</code>).
     * @param b     The other object (may be <code>null</code>).
     * @return      <code>true</code> if both arguments are <code>null</code>, or if the two objects equals
     *              under {@link Object#equals(Object)} (as in <code>a.equals(b)</code>). Otherwise
     *              <code>false</code>
     */ 
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

    
    public static final DecimalFormat f0 = new DecimalFormat("0");

    public static final DecimalFormat f1 = new DecimalFormat("0.0");

    public static final DecimalFormat f2 = new DecimalFormat("0.00");

    public static final DecimalFormat f3 = new DecimalFormat("0.000");

    public static final DecimalFormat f4 = new DecimalFormat("0.0000");

    public static final DecimalFormat f5 = new DecimalFormat("0.00000");

    public static final DecimalFormat f6 = new DecimalFormat("0.000000");

    public static final DecimalFormat f7 = new DecimalFormat("0.0000000");

    public static final DecimalFormat f8 = new DecimalFormat("0.00000000");

    public static final DecimalFormat f9 = new DecimalFormat("0.000000000");

    public static final DecimalFormat f10 = new DecimalFormat("0.0000000000");

    public static final DecimalFormat f11 = new DecimalFormat("0.00000000000");

    public static final DecimalFormat f12 = new DecimalFormat("0.000000000000");

    public static final DecimalFormat F0 = new DecimalFormat("0");

    public static final DecimalFormat F1 = new DecimalFormat("0.#");

    public static final DecimalFormat F2 = new DecimalFormat("0.##");

    public static final DecimalFormat F3 = new DecimalFormat("0.###");

    public static final DecimalFormat F4 = new DecimalFormat("0.####");

    public static final DecimalFormat F5 = new DecimalFormat("0.#####");

    public static final DecimalFormat F6 = new DecimalFormat("0.######");

    public static final DecimalFormat F7 = new DecimalFormat("0.#######");

    public static final DecimalFormat F8 = new DecimalFormat("0.########");

    public static final DecimalFormat F9 = new DecimalFormat("0.#########");
    
    public static final DecimalFormat F10 = new DecimalFormat("0.##########");

    public static final DecimalFormat F11 = new DecimalFormat("0.###########");

    public static final DecimalFormat F12 = new DecimalFormat("0.############");



    public static final DecimalFormat e0 = new DecimalFormat("0E0");

    public static final DecimalFormat e1 = new DecimalFormat("0.0E0");

    public static final DecimalFormat e2 = new DecimalFormat("0.00E0");

    public static final DecimalFormat e3 = new DecimalFormat("0.000E0");

    public static final DecimalFormat e4 = new DecimalFormat("0.0000E0");

    public static final DecimalFormat e5 = new DecimalFormat("0.00000E0");

    public static final DecimalFormat e6 = new DecimalFormat("0.000000E0");

    public static final DecimalFormat e7 = new DecimalFormat("0.0000000E0");

    public static final DecimalFormat e8 = new DecimalFormat("0.00000000E0");

    public static final DecimalFormat e9 = new DecimalFormat("0.000000000E0");

    public static final DecimalFormat e10 = new DecimalFormat("0.0000000000E0");

    public static final DecimalFormat e11 = new DecimalFormat("0.00000000000E0");

    public static final DecimalFormat e12 = new DecimalFormat("0.000000000000E0");



    public static final DecimalFormat E0 = new DecimalFormat("0E0");

    public static final DecimalFormat E1 = new DecimalFormat("0.#E0");

    public static final DecimalFormat E2 = new DecimalFormat("0.##E0");

    public static final DecimalFormat E3 = new DecimalFormat("0.###E0");

    public static final DecimalFormat E4 = new DecimalFormat("0.####E0");

    public static final DecimalFormat E5 = new DecimalFormat("0.#####E0");

    public static final DecimalFormat E6 = new DecimalFormat("0.######E0");

    public static final DecimalFormat E7 = new DecimalFormat("0.#######E0");

    public static final DecimalFormat E8 = new DecimalFormat("0.########E0");

    public static final DecimalFormat E9 = new DecimalFormat("0.#########E0");

    public static final DecimalFormat E10 = new DecimalFormat("0.##########E0");

    public static final DecimalFormat E11 = new DecimalFormat("0.###########E0");

    public static final DecimalFormat E12 = new DecimalFormat("0.############E0");



    public static final DecimalFormat d1 = new DecimalFormat("0");

    public static final DecimalFormat d2 = new DecimalFormat("00");

    public static final DecimalFormat d3 = new DecimalFormat("000");

    public static final DecimalFormat d4 = new DecimalFormat("0000");

    public static final DecimalFormat d5 = new DecimalFormat("00000");

    public static final DecimalFormat d6 = new DecimalFormat("000000");

    public static final DecimalFormat d7 = new DecimalFormat("0000000");

    public static final DecimalFormat d8 = new DecimalFormat("00000000");

    public static final DecimalFormat d9 = new DecimalFormat("000000000");

    public static final DecimalFormat d10 = new DecimalFormat("0000000000");

    public static final DecimalFormat d11 = new DecimalFormat("00000000000");

    public static final DecimalFormat d12 = new DecimalFormat("000000000000");




    public static final SignificantFiguresFormat s1 = new SignificantFiguresFormat(1);

    public static final SignificantFiguresFormat s2 = new SignificantFiguresFormat(2);

    public static final SignificantFiguresFormat s3 = new SignificantFiguresFormat(3);

    public static final SignificantFiguresFormat s4 = new SignificantFiguresFormat(4);

    public static final SignificantFiguresFormat s5 = new SignificantFiguresFormat(5);

    public static final SignificantFiguresFormat s6 = new SignificantFiguresFormat(6);

    public static final SignificantFiguresFormat s7 = new SignificantFiguresFormat(7);

    public static final SignificantFiguresFormat s8 = new SignificantFiguresFormat(8);

    public static final SignificantFiguresFormat s9 = new SignificantFiguresFormat(9);

    public static final SignificantFiguresFormat s10 = new SignificantFiguresFormat(10);

    public static final SignificantFiguresFormat s11 = new SignificantFiguresFormat(11);

    public static final SignificantFiguresFormat s12 = new SignificantFiguresFormat(12);


    
    public static final SignificantFiguresFormat S1 = new SignificantFiguresFormat(1, false);

    public static final SignificantFiguresFormat S2 = new SignificantFiguresFormat(2, false);

    public static final SignificantFiguresFormat S3 = new SignificantFiguresFormat(3, false);

    public static final SignificantFiguresFormat S4 = new SignificantFiguresFormat(4, false);

    public static final SignificantFiguresFormat S5 = new SignificantFiguresFormat(5, false);

    public static final SignificantFiguresFormat S6 = new SignificantFiguresFormat(6, false);

    public static final SignificantFiguresFormat S7 = new SignificantFiguresFormat(7, false);

    public static final SignificantFiguresFormat S8 = new SignificantFiguresFormat(8, false);

    public static final SignificantFiguresFormat S9 = new SignificantFiguresFormat(9, false);

    public static final SignificantFiguresFormat S10 = new SignificantFiguresFormat(10, false);

    public static final SignificantFiguresFormat S11 = new SignificantFiguresFormat(11, false);

    public static final SignificantFiguresFormat S12 = new SignificantFiguresFormat(12, false);



    public static final HourAngleFormat hf0 = new HourAngleFormat(0);

    public static final HourAngleFormat hf1 = new HourAngleFormat(1);

    public static final HourAngleFormat hf2 = new HourAngleFormat(2);

    public static final HourAngleFormat hf3 = new HourAngleFormat(3);

    public static final HourAngleFormat hf4 = new HourAngleFormat(4);

    public static final HourAngleFormat hf5 = new HourAngleFormat(5);

    public static final HourAngleFormat hf6 = new HourAngleFormat(6);

    public static final HourAngleFormat hf7 = new HourAngleFormat(7);

    public static final HourAngleFormat hf8 = new HourAngleFormat(8);

    public static final HourAngleFormat hf9 = new HourAngleFormat(9);

    public static final HourAngleFormat hf10 = new HourAngleFormat(10);

    public static final HourAngleFormat hf11 = new HourAngleFormat(11);

    public static final HourAngleFormat hf12 = new HourAngleFormat(12);





    public static final AngleFormat af0 = new AngleFormat(0);

    public static final AngleFormat af1 = new AngleFormat(1);

    public static final AngleFormat af2 = new AngleFormat(2);

    public static final AngleFormat af3 = new AngleFormat(3);

    public static final AngleFormat af4 = new AngleFormat(4);

    public static final AngleFormat af5 = new AngleFormat(5);

    public static final AngleFormat af6 = new AngleFormat(6);

    public static final AngleFormat af7 = new AngleFormat(7);

    public static final AngleFormat af8 = new AngleFormat(8);

    public static final AngleFormat af9 = new AngleFormat(9);

    public static final AngleFormat af10 = new AngleFormat(10);

    public static final AngleFormat af11 = new AngleFormat(11);

    public static final AngleFormat af12 = new AngleFormat(12);

    
    public static final AngleFormat Af0 = new AngleFormat(0, true);

    public static final AngleFormat Af1 = new AngleFormat(1, true);

    public static final AngleFormat Af2 = new AngleFormat(2, true);

    public static final AngleFormat Af3 = new AngleFormat(3, true);

    public static final AngleFormat Af4 = new AngleFormat(4, true);
    
    public static final AngleFormat Af5 = new AngleFormat(5, true);

    public static final AngleFormat Af6 = new AngleFormat(6, true);

    public static final AngleFormat Af7 = new AngleFormat(7, true);

    public static final AngleFormat Af8 = new AngleFormat(8, true);

    public static final AngleFormat Af9 = new AngleFormat(9, true);

    public static final AngleFormat Af10 = new AngleFormat(10, true);

    public static final AngleFormat Af11 = new AngleFormat(11, true);

    public static final AngleFormat Af12 = new AngleFormat(12, true);


    public static final TimeFormat tf0 = new TimeFormat(0);

    public static final TimeFormat tf1 = new TimeFormat(1);

    public static final TimeFormat tf2 = new TimeFormat(2);

    public static final TimeFormat tf3 = new TimeFormat(3);

    public static final TimeFormat tf4 = new TimeFormat(4);

    public static final TimeFormat tf5 = new TimeFormat(5);

    public static final TimeFormat tf6 = new TimeFormat(6);

    public static final TimeFormat tf7 = new TimeFormat(7);

    public static final TimeFormat tf8 = new TimeFormat(8);

    public static final TimeFormat tf9 = new TimeFormat(9);

    public static final TimeFormat tf10 = new TimeFormat(10);

    public static final TimeFormat tf11 = new TimeFormat(11);

    public static final TimeFormat tf12 = new TimeFormat(12);
    

    public static final HourAngleFormat haf0 = new HourAngleFormat(0);

    public static final HourAngleFormat haf1 = new HourAngleFormat(1);

    public static final HourAngleFormat haf2 = new HourAngleFormat(2);

    public static final HourAngleFormat haf3 = new HourAngleFormat(3);

    public static final HourAngleFormat haf4 = new HourAngleFormat(4);

    public static final HourAngleFormat haf5 = new HourAngleFormat(5);

    public static final HourAngleFormat haf6 = new HourAngleFormat(6);

    public static final HourAngleFormat haf7 = new HourAngleFormat(7);

    public static final HourAngleFormat haf8 = new HourAngleFormat(8);

    public static final HourAngleFormat haf9 = new HourAngleFormat(9);

    public static final HourAngleFormat haf10 = new HourAngleFormat(10);

    public static final HourAngleFormat haf11 = new HourAngleFormat(11);

    public static final HourAngleFormat haf12 = new HourAngleFormat(12);


    public static final DecimalFormat[] 
            e = { e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12 },
            E = { E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12 },
            f = { f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12 },
            F = { F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12 },
            d = { null, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12 };


    public static final SignificantFiguresFormat[] 
            s = { null, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12 },
            S = { null, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12 };

    public static final HourAngleFormat[] hf = { hf0, hf1, hf2, hf3, hf4, hf5, hf6, hf7, hf8, hf9, hf10, hf11, hf12 };

    public static final AngleFormat[] af = { af0, af1, af2, af3, af4, af5, af6, af7, af8, af9, af10, af11, af12 };

    public static final AngleFormat[] Af = { Af0, Af1, Af2, Af3, Af4, Af5, Af6, Af7, Af8, Af9, Af10, Af11, Af12 };
    
    public static final TimeFormat[] tf = { tf0, tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12 };

    public static final HourAngleFormat[] haf = { haf0, haf1, haf2, haf3, haf4, haf5, haf6, haf7, haf8, haf9, haf10, haf11, haf12 };
    
}


