package jnum.data.image;

import jnum.math.SphericalCoordinates;

public class SkyGrid extends SphericalGrid {
    /**
     * 
     */
    private static final long serialVersionUID = 4221564275364638142L;

    
    public SkyGrid() {
        super();
    }

    public SkyGrid(SphericalCoordinates reference) {
        super(reference);
    }
    
    /**
     * Sky mpas are generally as we see looking out, not in, so it's mirrored w.r.t. the usual globe-like
     * spherical projection.
     */
    @Override
    public boolean isReverseX() { return !super.isReverseX(); }
}
