package jnum.devel.sourcecounts;

import jnum.data.Histogram;

public abstract class RegularTemplate extends OldCountsModel {
    public double dF;   

    public void init(Histogram mapHistogram, double maxFlux, int bins) {    
        double[] fluxes = new double[bins];
        dF = maxFlux / bins;
        double flux = dF;
        for(int i=0; i<fluxes.length; i++, flux+=dF) fluxes[i] = Math.sqrt(flux * (flux+dF)); 
        super.init(mapHistogram, fluxes);
    }

    @Override
    public double getdF(int i) { 
        return dF;
    }

    public double guessN(double S) {
        return super.guessN(S, dF);
    }

    public double getFillFraction(int bin, double S0, double index) {
        double bottom = dF * (bin+1);
        double top = bottom + dF;

        if(S0 < bottom) return 1.0;
        else if(S0 > top) return 0.0;
        else return (Math.pow(S0, index+1.0) - Math.pow(top, index+1.0)) / (Math.pow(bottom, index+1.0) - Math.pow(top, index+1.0));
    }

}