package jnum.astro;

public final class ICRSEpoch extends CoordinateEpoch {


    /**
     * 
     */
    private static final long serialVersionUID = 4405473994187423557L;

    public ICRSEpoch() {
        super(2000.0);
    }

    
    @Override
    public double getJulianYear() {
        return 2000.0;
    }

    @Override
    public double getBesselianYear() {
        return CoordinateEpoch.J2000.getBesselianYear();
    }

    @Override
    public double getMJD() {
        return CoordinateEpoch.mjdJ2000;
    }

    @Override
    public String toString() {
        return "ICRS";
    }
    
    
}
