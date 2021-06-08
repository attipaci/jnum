package jnum.astro;

import jnum.math.Vector2D;

public class ApparentEpoch extends JulianEpoch {
    /**
     * 
     */
    private static final long serialVersionUID = -5617616639784105380L;
   
    private Vector2D wobble;
    private double dUT1;
    
    public ApparentEpoch(double epoch, double dUT1, double xp, double yp) {
        super(epoch);
        wobble = new Vector2D(xp, yp);
        this.dUT1 = dUT1;
    }    
    
    public Vector2D getWobble() {
        return wobble;
    }

}
