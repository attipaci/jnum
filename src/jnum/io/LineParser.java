/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jnum.Util;


public abstract class LineParser {

    private int lines = 0, comments = 0;

    private int lineNumber = 0;

    private ArrayList<Exception> parseExceptions = new ArrayList<>();
   
    
    /**
     * Read all available bytes from the specified InputStream, parsing the content line-by-line until
     * the end of stream (EOF) is reached.
     *
     * @param in the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void read(InputStream in) throws IOException {
        read(new BufferedReader(new InputStreamReader(in)));
    }
    
    /**
     * Read the specified file fully, parsing its content line-by-line.
     *
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("resource")
    public void read(File file) throws IOException {
        read(new FileInputStream(file));
    }
    
    /**
     * Read the file or online resource, specified by the descriptor, line-by-line. 
     *
     * @param descriptor the file name, including path as neccessary, or URL specification to
     *        an online resource.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void read(String descriptor) throws IOException {
        try(BufferedReader in = Util.getReader(descriptor)) { read(in); }
    }
    
    /**
     * Read the specified BufferedReader input fully, line-by-line, until the end-of-file (EOF).
     *
     * @param in the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void read(BufferedReader in) throws IOException {
        String line = null;
        
        while((line = readLine(in)) != null) if(line.length() > 0) {
            try { 
                if(isCommentChar(line.charAt(0))) {
                    String comment = line.substring(1).trim();
                    if(comment.length() > 0) if(parseComment(comment)) comments++;
                }
                else if(parse(line)) lines++;
            }
            catch(Exception e) { handleParseException(e); }
        }
        in.close();
    }
    
    /**
     * Handle parse exceptions.
     *
     * @param e the exception that occurred during parsing.
     */
    protected void handleParseException(Exception e) {
        Util.warning(this, "L" + lineNumber + "> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        parseExceptions.add(new Exception("@" + lineNumber + "> " + e.getClass().getSimpleName() + ": " + e.getMessage(), e));
    }
    
    /**
     * Read a single line from the underlying BufferedReader input.
     *
     * @param in the input
     * @return the next line from the input
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String readLine(BufferedReader in) throws IOException {
        lineNumber++;
        String line = in.readLine(); 
        return line == null ? null : line.trim();
    }
    
    /**
     * Checks if the specified character is a comment marker.
     *
     * @param c the character
     * @return true, if c is a comment marker.
     */
    protected boolean isCommentChar(char c) {
        return c == '#';
    }
    
    /**
     * Parses a comment line.
     *
     * @param comment the comment line to parse (with the leading comment marker, such as {@link #clone()}, removed).
     * @return true, if the content was successfully parsed (that is information from it was retrieved), false if the
     *    line contained incomplete or no information for successful parsing.
     * @throws Exception any exception encountered during a failed parse attempt.
     */
    protected boolean parseComment(String comment) throws Exception { return true; }
    
    /**
     * Parses an uncommented line from the input. 
     *
     * @param line the next uncommented line from the input
     * @return true, if the parsing was successful (that is information from the line was retrieved), false if the 
     *    line contained incomplete or no information for successful parsing.
     * @throws Exception any exception encountered during a failed parse attempt.
     */
    protected abstract boolean parse(String line) throws Exception;
    
    /**
     * Gets the line number of the last line read from the input.
     *
     * @return the current line number for processing.
     */
    protected final int getLineNumber() { return lineNumber; }
      
    /**
     * Gets the total number of uncommented input lines processed successfully from the input.
     *
     * @return the number of uncommented lines processed
     */
    public final int getLinesProcessed() { return lines; }
    
    /**
     * Gets the total number of commented lines processed successfully from the input.
     *
     * @return the number of comment lines processed
     */
    public final int getCommentsProcessed() { return comments; }
    
    /**
     * Check whether any exceptions occurred during the parsing of the input.
     *
     * @return true, if the parsing encountered any exceptions during parsing.
     */
    public final boolean hadExceptions() { return !parseExceptions.isEmpty(); }
    
    /**
     * Retrieve the list of exceptions that occurred while the input was processed.
     *
     * @return the list the exceptions during parsing.
     */
    public final List<Exception> getParseExceptions() { return parseExceptions; }
       
}
