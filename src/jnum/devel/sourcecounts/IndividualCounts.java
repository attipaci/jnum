package jnum.devel.sourcecounts;

import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;

public class IndividualCounts extends RegularTemplate {
    int freeValues;


    @Override
    public void init(Histogram mapHistogram, double maxFlux, int bins) {    
        freeValues = bins;
        super.init(mapHistogram, maxFlux, bins);

        double[] initparms = new double[freeValues + 2];

        double N0 = guessN(5.0 * Unit.mJy);
        double p = 4.0;

        for(int i=0; i<freeValues; i++) initparms[i] = N0 * Math.pow(templates[i].flux / (5.0 * Unit.mJy), -p); 
        initparms[freeValues] = noiseScaling;
        initparms[freeValues + 1] = 0.0;
        init(initparms);

        startSize = new double[parameter.length];
        for(int i=0; i<freeValues; i++) startSize[i] = 0.1 * initparms[i];
        startSize[freeValues] = 0.1;
        startSize[freeValues + 1] = 0.1;
    }

    @Override
    public double minimize(int n) {
        double chi2 = super.minimize(n);

        double a = Unit.deg2 / mapArea * Unit.mJy / dF;

        for(int k=0; k<freeValues; k++) {
            System.err.println(Util.e3.format(templates[k].flux / Unit.Jy) + "\t"
                    + Util.e3.format(a * parameter[k]) + "  \t"
                    + Util.e3.format(a * getStandardError(k, 0.1*parameter[k]))
                    );
        }

        System.err.println();

        double[] integral = new double[freeValues];
        double[] dN = new double[freeValues];
        double sum = 0.0, var = 0.0;

        for(int k=freeValues-1; k>=0; k--) {
            sum += a * parameter[k];
            double rms = a * getStandardError(k, 0.1*parameter[k]);
            var += rms * rms;

            integral[k] = sum;
            dN[k] = Math.sqrt(var);
        }

        for(int k=0; k<freeValues; k++) {
            System.err.println(Util.e3.format((k+1)*dF / Unit.Jy) + "\t"
                    + Util.e3.format(integral[k]) + "  \t"
                    + Util.e3.format(dN[k])
                    );
        }

        System.err.println();

        return chi2;
    }

    @Override
    public double getCountsFor(double[] tryparm, int i) {       
        int k = i;
        return tryparm[k];
    }

    @Override
    public double getCountsErrFor(double[] tryparm, int i) { 
        return getStandardError(i, 0.1);
    }

    @Override
    public int getNoiseScaleIndex() { return freeValues; }

    @Override
    public int getOffsetIndex() { return freeValues+1; }

}



