package jnum.devel.sourcecounts;

import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;

class ExponentialCounts extends RegularTemplate {

    @Override
    public void init(Histogram mapHistogram, double maxFlux, int bins) {
        super.init(mapHistogram, maxFlux, bins);    

        double S1 = 0.2*maxFlux;
        double N0 = guessN(S1);

        double[] initparms = { N0, S1, noiseScaling, 0.0 };
        startSize = new double[] { 0.1*N0, dF, 0.1, 0.1 };
        init(initparms);
    }


    @Override
    public double getCountsFor(double[] tryparm, int i) {
        double N0 = tryparm[0];
        double S1 = tryparm[1];
        double F = templates[i].flux;

        return N0 * Math.exp(-F/S1);
    }

    @Override
    public double getCountsErrFor(double[] tryparm, int i) { return getStandardError(0, 0.1*parameter[0]) * getCountsFor(tryparm, i); }

    @Override
    public double evaluate(double[] tryparm) {
        double S0 = tryparm[1];
        double chi2 = super.evaluate(tryparm);
        if(S0 < 0.0) return (1.0 + Math.pow(S0 / dF, 2.0)) * chi2;
        return chi2;
    }

    @Override
    public double minimize(int n) {
        double chi2 = super.minimize(n);

        double N = parameter[0]/dF * Unit.mJy * Unit.deg2 / mapArea;
        double dN = N/parameter[0] * getStandardError(0, 0.01*parameter[0]);
        double S1 = parameter[1] / Unit.Jy;
        double dS1 = getStandardError(1, 0.01 * parameter[1]) / Unit.Jy;

        System.err.println("dN(S1) = " + Util.e3.format(N) + " +- " + Util.e1.format(dN) + " #/mJy/deg2");
        System.err.println("S1 = " + Util.e3.format(S1) + " +- " + Util.e1.format(dS1) + " Jy");

        return chi2;
    }


    @Override
    public int getNoiseScaleIndex() { return 2; }

    @Override
    public int getOffsetIndex() { return 3; }


}



