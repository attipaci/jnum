package test;

import jnum.data.image.Flag2D;
import jnum.data.image.Observation2D;

public class Obs2DTest {

    public static void main(String[] args) {
        
        Observation2D map = new Observation2D(Float.class, Float.class, Flag2D.TYPE_INT);
        map.setSize(8, 5);
           
        map.accumulateAt(1, 2, 1.0, 1.0, 1.0, 1.0);
        map.endAccumulation();
        
        System.err.println("? 0,0: " + map.isValid(0,0));
        
        System.err.println("N = " + map.countPoints());
        
        try { map.writeFits("test.fits"); }
        catch(Exception e) { e.printStackTrace(); }
        
    }
    
}
