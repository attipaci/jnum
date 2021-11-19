package jnum.math;

import java.text.ParsePosition;
import java.util.stream.IntStream;

import jnum.ViewableAsDoubles;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedValues;
import jnum.fits.FitsToolkit;
import jnum.text.Parser;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public interface RealCoordinates extends Coordinates<Double>, IndexedValues<Index1D, Double>, RealComponents, ViewableAsDoubles, Parser, Inversion, ZeroValue {

    
    @Override
    public default void set(double... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, v[i]));
    }


    @Override
    public default void set(float... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, (double) v[i]));
    }    
    
    @Override
    public default void add(double... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) + v[i]));
    }


    @Override
    public default void add(float... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) + v[i]));
    }


    @Override
    public default void addScaled(double factor, double... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) + factor * v[i]));
    }


    @Override
    public default void addScaled(double factor, float... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) + factor * v[i]));
    }


    @Override
    public default void subtract(double... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) - v[i]));
    }


    @Override
    public default void subtract(float... v) {
        IntStream.range(0, Math.min(size(), v.length)).forEach(i -> setComponent(i, getComponent(i) - v[i]));
    }


    @Override
    public default void flip() {
        IntStream.range(0, size()).forEach(i -> setComponent(i, -getComponent(i)));
    }

    @Override
    public default void zero() {
        IntStream.range(0, size()).forEach(i -> setComponent(i, 0.0));
    }


    @Override
    public default boolean isNull() { 
        for(int i=size(); --i >= 0; ) if(getComponent(i) != 0.0) return false;
        return true;
    }   

    /**
     * Sets both coordinates to NaN.
     * 
     */
    public default void NaN() { 
        IntStream.range(0, size()).forEach(i -> setComponent(i, Double.NaN));
    }

    /**
     * Checks if either coordinate is NaN (invalid).
     * 
     * @return <code>true</code> if either coordinate is NaN (invalid). Otherwise <code>false</code>.
     */
    public default boolean isNaN() { 
        for(int i=size(); --i >= 0; ) if(Double.isNaN(getComponent(i))) return true;
        return false;
    }

    /**
     * Checks if either coordinate is infinite.
     * 
     * @return <code>true</code> if either coordinate is infinite. Otherwise <code>false</code>.
     */
    public default boolean isInfinite() { 
        for(int i=size(); --i >= 0; ) if(Double.isInfinite(getComponent(i))) return true;
        return false;
    }
    
    
    public default boolean isFinite() {
        if(isNaN()) return false;
        if(isInfinite()) return false;
        return true;
        
    }

    @Override
    public default Double copyOf(int i) { 
        return getComponent(i);
    }
    
    
    
   


    public default String toDefaultString() {
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        IntStream.range(0, size()).forEach(i -> buf.append(getComponent(i)));
        buf.append(')');
        return buf.toString();
    }



    @Override
    public default void clear(Index1D index) {
        set(index.i(), 0.0);
    }

    @Override
    public default void scale(Index1D index, double factor) {
        setComponent(index.i(), factor * getComponent(index.i()));
    }

    @Override
    public default void set(Index1D index, Number value) {
        setComponent(index.i(), value.doubleValue());
    }

    @Override
    public default void add(Index1D index, Number value) {
        setComponent(index.i(), getComponent(index.i()) + value.doubleValue());
    }

    @Override
    public default Class<? extends Number> getElementType() {
        return Double.class;
    }


    
    
    
    /**
     * Sets the coordinates to the weighted average of the current value of these coordinates
     * and the specified other coordinates.
     * 
     * @param w1        the weight to use for these coordinates
     * @param coord     the other coordinates
     * @param w2        the weight to use for the other coordinates
     */
    public default void weightedAverageWith(double w1, final RealCoordinates coord, double w2) {
        final double isumw = 1.0 / (w1 + w2);
        final double c1 = w1 * isumw;
        final double c2 = w2 * isumw;
        IntStream.range(0, Math.min(size(), coord.size())).forEach(i -> setComponent(i, c1 * getComponent(i) + c2 * coord.getComponent(i)));
    }
    
    
    /**
     * Parses the coordinates from a string representation of these.
     * 
     * @param spec      The string beginnign with a 2D coordinate description (normally
     *                  a pair of comma or space separated values, possibly enclosed in brackets.
     *                  (See {@link #parse(StringParser)} for more details on the accepted
     *                  string formats).
     * @throws NumberFormatException    if the string does not seem to begin with what could
     *                                  be used as coordinate components.
     * 
     * @see #parse(String, ParsePosition)
     * @see #parse(StringParser)
     */
    public default void parse(String spec) throws NumberFormatException {
        parse(spec, new ParsePosition(0));
    }


    @Override
    public default void parse(String text, ParsePosition pos) throws NumberFormatException {
        parse(new StringParser(text, pos));
    }

    /**
     * Parses text x,y 2D coordinate representations that are in the format(s) of:
     * 
     * <pre>
     * {@code
     *     x,y / x y
     * }
     * </pre>
     * 
     * or
     * 
     * <pre>
     * {@code
     *     (x,y) / (x y)
     * }
     * </pre>
     * 
     * More specifically, the x and y values may be separated either by comma(s) or white space(s) (including 
     * tabs line breaks, carriage returns), or a combination of both. The pair of values may be bracketed (or 
     * not). Any number of white spaces may exists between the elements (brackets and pair of values), or 
     * precede the text element. Thus, the following will parse as a proper x,y (1.0,-2.0) pair:
     * 
     * <pre>
     * {@code
     *     (  \t\n 1.0 ,,, \r , \t -2.0    \n  )
     * }
     * </pre>
     * 
     * @param parser                    The string parsing helper object.
     * @throws NumberFormatException    if the string does not seem to begin with what could
     *                                  be used as coordinate components.
     * 
     * @see #parse(String)
     * @see #parse(String, ParsePosition)
     */
    public default void parse(StringParser parser) throws NumberFormatException {
        boolean isBracketed = false;

        zero();

        parser.skipWhiteSpaces();

        if(parser.peek() == '(') {
            isBracketed = true;
            parser.skip(1);
            parser.skipWhiteSpaces();
        }
        
        for(int i=0; i<size(); i++) {
            String term = ",";
            if(isBracketed && i == size() - 1) term = ",)";
            parseComponent(i, parser.nextToken(term));
        }
    }
    
    public default void parseComponent(int index, String text) throws NumberFormatException {
        setComponent(index, Double.parseDouble(text));
    }
    
    @Override
    public default void createFromDoubles(Object array) throws IllegalArgumentException {
        if(!(array instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
        double[] components = (double[]) array;
        if(components.length < size()) throw new IllegalArgumentException("argument double[] array is to small.");
        IntStream.range(0, size()).forEach(i -> setComponent(i, components[i]));
    }


    @Override
    public default void viewAsDoubles(Object view) throws IllegalArgumentException {
        if(!(view instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
        double[] components = (double[]) view;
        if(components.length != size()) throw new IllegalArgumentException("argument double[] array is to small.");
        IntStream.range(0, size()).forEach(i -> components[i] = getComponent(i));
    }

    @Override
    public default double[] viewAsDoubles() {
        double[] components = new double[size()]; 
        viewAsDoubles(components);
        return components;
    }


    public default void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        for(int i=0; i<size(); i++) {
            String name = i < 6 ? "" + ('x' + i) : "t" + (i-6);
            c.add(new HeaderCard(keyStem + (i + 1) + alt, getComponent(i), "The reference " + name + " coordinate in SI units."));
        }
    }


    public default void parseHeader(Header header, String keyStem, String alt, RealCoordinates defaultValue) {
        for(int i=0; i<size(); i++)
            setComponent(i, header.getDoubleValue(keyStem + "1" + alt, defaultValue == null ? 0.0 : defaultValue.getComponent(i)));    
    }

    
    
}
