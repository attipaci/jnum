package jnum.math;

public class SymmetryException extends ArithmeticException {

    /**
     * 
     */
    private static final long serialVersionUID = 922570926857533786L;

    private static String defaultMessage = "Object does not have the required symmetry.";
    
    public SymmetryException() {
        this(defaultMessage);
    }

    public SymmetryException(String s) {
        super(s);
    }

    
    
}
