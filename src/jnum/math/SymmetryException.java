package jnum.math;

/**
 * The operation cannot be performed because the object does not have the required symmetry for it.
 */
public class SymmetryException extends ArithmeticException {

    /**
     * 
     */
    private static final long serialVersionUID = 922570926857533786L;
    
    /**
     * The default exception message string.
     * 
     */
    private static String defaultMessage = "Object does not have the required symmetry.";
    
    /**
     * Instantiates a new symmetry exception.
     * 
     */
    public SymmetryException() {
        this(defaultMessage);
    }

    /**
     * Instantiates a new symmetry exception with a specific mesdsage providing extra details.
     * 
     * @param message   the message string for the new exception.
     */
    public SymmetryException(String message) {
        super(message);
    }
 
}
