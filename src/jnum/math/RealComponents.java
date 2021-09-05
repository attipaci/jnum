package jnum.math;

/**
 * An interface for vector types that have real-valued components, defining component-wise operations.
 * 
 * @author Attila Kovacs
 *
 */
public interface RealComponents {
   
    /**
     * Sets components. The implementing class may have differing number of components,
     * and accordingly, the set values may be truncated or padded with zeroes as apprpriate.
     * 
     * @param v     vector components to set.
     */
    public void set(double... v);

    /**
     * Sets components. The implementing class may have differing number of components,
     * and accordingly, the set values may be truncated or padded with zeroes as apprpriate.
     * 
     * @param v     vector components to set.
     */
    public void set(float... v);
    
    /**
     * Adds components from another vector (possibly of different dimensions)
     * 
     * @param v     vector components to add.
     * @see #subtract(double...)
     * @see #addScaled(double, double...)
     */
    public void add(double... v);

    /**
     * Adds components from another vector (possibly of different dimensions)
     * 
     * @param v     vector components to add.
     * @see #subtract(float...)
     * @see #addScaled(double, float...)
     */
    public void add(float... v);
    
    /**
     * Adds scaled components from another vector (possibly of different dimensions)
     * 
     * @param factor    scaling factor for additive components
     * @param v         vector components to add.
     * @see #add(double...)
     */
    public void addScaled(double factor, double... v);
    
    /**
     * Adds scaled components from another vector (possibly of different dimensions)
     * 
     * @param factor    scaling factor for additive components
     * @param v         vector components to add.
     * @see #add(float...)
     */
    public void addScaled(double factor, float... v);
    
    /**
     * Subtracts components from another vector (possibly of different dimensions)
     * 
     * @param v     vector components to add.
     * @see #add(double...)
     */
    public void subtract(double... v);
    
    /**
     * Subtracts components from another vector (possibly of different dimensions)
     * 
     * @param v     vector components to add.
     * @see #add(float...)
     */
    public void subtract(float... v);
}
