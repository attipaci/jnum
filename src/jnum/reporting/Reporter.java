/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.reporting;


/**
 * 
 * Reporter is a convenient way of handling messages from your Java programs and methods. They should be preferred
 * to <code>System.out.println()</code> or <code>System.err.println()</code> statements, as 
 * you can always change how they are consumed (e.g. whether they are printed to {@link System#out}, or to 
 * {@link System#err}, or to some other {@link java.io.PrintStream}, or to a graphical GUI), or
 * choose which messages are kept and which are suppressed).
 * 
 * There is a certain similarity to Java's built-in {@link java.util.logging.Logger} class, but offering a somewhat 
 * different (better) set of features.
 * 
 */
public abstract class Reporter {
    
    /** A string identifier by which this reporter can be referred to */
    private String id;

    public Reporter(String id) {
        this.id = id;
    }

    public final String getID() { return id; }
    
    
    /**
     * Processes an informational message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). 
     * The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void info(Object owner, String message);
   
    /**
     * Processes a notification message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). 
     * The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * For example, a program may send a notification when it created a file, or connected to a network resource.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body of the notification
     */
    public abstract void notify(Object owner, String message);
    
    /**
     * Processes a debugging message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). 
     * The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * Debugging messages should normally be suppressed (not recorded to displayed to the user), except if the
     * program is being run in debugging mode.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The debugging information.
     */
    public abstract void debug(Object owner, String message);
    
    /**
     * Processes a warning message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * Warnings are meant to report something that did not go as planned, but which do not present a critical
     * issue. They are often used to report certain types of Exceptions, although they are not necessarily linked
     * to an {@link Exception}.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The warning message body.
     * 
     * @see #warning(Object, Exception)
     * @see #warning(Object, Exception, boolean)
     */
    public abstract void warning(Object owner, String message);
    
    /**
     * Processes an error message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * Like warnings, but indicating a more critical issue or error. The user may want to abort whatever process produced
     * the error, and exit the program or start fresh. Also like warnings, errors are often, but not always, linked
     * to an {@link Exception} and/or {@link Error}.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The warning message body.
     * 
     * @see #error(Object, Throwable)
     * @see #error(Object, Throwable, boolean)
     */
    public abstract void error(Object owner, String message);
    

    /**
     * Processes a trace that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * A trace may be generated when an {@link Exception} or a {@link Error} is encountered in a program, 
     * or they may be generated for
     * monitoring or debugging purposes. They may often follow after {@link #warning(Object, String)} or 
     * {@link #error(Object, String)} messages to provide
     * more detail about the particulars of the given error/warning.
     * 
     * @param e     The {@link Throwable} object, such as an {@link Exception} or {@link Error} whose trace is to be reported.
     * 
     * @see #warning(Object, Exception)
     * @see #error(Object, Throwable)
     */
    public abstract void trace(Throwable e);
    

    /**
     * Processes an error linked to a {@link Throwable} (e.g. a Java {@link Exception} or {@link Error}). 
     * The default implementation here is to report the included message as an {@link #error(Object, String)}, 
     * and then provide a {@link #trace(Throwable)} if debug is set to true.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param e         The {@link Throwable} object, such as an {@link Exception} or {@link Error}, that 
     *                  contains the relevant information about the error.
     * @param debug     Set to <code>true</code> if a trace should be reported for this error via a {@link #trace(Throwable)} call.
     * 
     * @see #error(Object, Throwable, boolean)
     * @see #error(Object, String)
     */
    public void error(Object owner, Throwable e, boolean debug) {
        error(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    /**
     * Same as {@link #error(Object, Throwable, boolean)}, but with the trace reporting enabled by default.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param e         The {@link Throwable} object, such as an {@link Exception} or {@link Error}, that contains the relevant information about the error.
     *
     * @see #error(Object, Throwable, boolean)
     * @see #error(Object, String)
     */
    public void error(Object owner, Throwable e) {
        error(owner, e, true);
    }
    
    /**
     * Similar to {@link #error(Object, Throwable, boolean)} but for reporting a warning.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param e         The {@link Throwable} object, such as an {@link Exception}, that contains the relevant information about the error.
     * @param debug     Set to <code>true</code> if a trace should be reported for this warning via a {@link #trace(Throwable)} call.
     * 
     * @see #warning(Object, Exception)
     * @see #warning(Object, String)
     */
    public void warning(Object owner, Exception e, boolean debug) {
        warning(owner, e.getMessage());
        if(debug) trace(e);
    }
    
    /**
     * Similar to {@link #error(Object, Throwable)}, but for warning. The trace reporting is enabled by default. It simply calls
     * <code>warning(owner, e, true)</code>.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param e         The {@link Throwable} object, such as an {@link Exception} or {@link Error}, that contains the relevant information about the error.
     *
     * @see #warning(Object, Exception, boolean)
     * @see #warning(Object, String)
     */
    public void warning(Object owner, Exception e) {
        warning(owner, e, false);
    }

    /**
     * Processes a status message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * A status message might for example report what stage or phase a program is currently in.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void status(Object owner, String message);
    
    /**
     * Processes a result that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * A result might be generated from a calculation, or may report the parameters of a fit to the data.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void result(Object owner, String message);
    
    
    /**
     * Processes a detail message that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * Details are like informational messages, but with lesser importance. Generally speaking details should be
     * pretty safe to ignore, unless you want to scrutinize the details of what's happening. 
     * They are meant as a way to provide more verbose information about what is happening.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void detail(Object owner, String message);
    
    
    /**
     * Processes a value or values that were generated that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * For example, values may be reporting some input parameters, or values parsed from an input file. They may come
     * with extra text that defines their meaning and context.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void values(Object owner, String message); 
    
    /**
     * Processes a suggestion that is associated with the given object instance or class. (I.e. owner may
     * be an instance of <code>Foo</code>, or it may be a class such as <code>Foo.class</code>). The owner is typically the object or class that
     * generated the message, but it does not have to be so. Sometimes you may want to assign a message to a particular
     * object or class that has something to do with the given message, even if they did not themselves produce that
     * message.
     * 
     * Suggestions are often generated following a {@link #warning(Object, String)} or {@link #error(Object, String)} call, 
     * and may provide information on what you may do to correct or work around some issue.
     * 
     * @param owner     The {@link Object} or {@link Class} to which this message belongs
     * @param message   The message body.
     */
    public abstract void suggest(Object owner, String message);
    
}
