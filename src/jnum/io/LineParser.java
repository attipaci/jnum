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
    private String id;
    private ArrayList<Exception> parseExceptions = new ArrayList<Exception>();
   
    
    public void read(InputStream in) throws IOException {
        read(new BufferedReader(new InputStreamReader(in)));
    }
    
    @SuppressWarnings("resource")
    public void read(File file) throws IOException {
        read(new FileInputStream(file));
    }
    
    public void read(String spec) throws IOException {
        read(Util.getReader(spec));
    }
    
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
            catch(Exception e) { 
                Util.warning(this, id + ":" + lineNumber + "> " + e.getClass().getSimpleName() + ": " + e.getMessage());
                parseExceptions.add(new Exception("@" + lineNumber + "> " + e.getClass().getSimpleName() + ": " + e.getMessage(), e));
            }
        }
        in.close();
    }
    
    
    private String readLine(BufferedReader in) throws IOException {
        lineNumber++;
        String line = in.readLine(); 
        return line == null ? null : line.trim();
    }
    
    protected boolean isCommentChar(char c) {
        return c == '#';
    }
    
    protected boolean parseComment(String comment) throws Exception { return true; }
    
    protected abstract boolean parse(String line) throws Exception;
    
    protected final int getLineNumber() { return lineNumber; }
      
    public final int getLinesProcessed() { return lines; }
    
    public final int getCommentsProcessed() { return comments; }
    
    public final boolean hadExceptions() { return !parseExceptions.isEmpty(); }
    
    public final List<Exception> getParseExceptions() { return parseExceptions; }
       
}
