package jnum.devel.sourcecounts;

import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;

class Barger extends RegularTemplate {

    @Override
    public void init(Histogram mapHistogram, double maxFlux, int bins) {
        super.init(mapHistogram, maxFlux, bins);

        double S0 = 2.0 * dF;
        double N0 = guessN(S0);

        double[] initparms = { N0, 2*dF, 3.0, noiseScaling, 0.0 };
        startSize = new double[] { 0.1*N0, 0.1*dF, 0.1, 0.1, 0.01 };
        init(initparms);
    }

    @Override
    public double getCountsFor(double[] tryparm, int i) {
        double N1 = tryparm[0];
        double S0 = tryparm[1];
        double p = tryparm[2];

        double F = templates[i].flux;

        return N1 / (1.0 + Math.pow(F/S0, p));
    }

    @Override
    public double getCountsErrFor(double[] tryparm, int i) { return getStandardError(0, 0.1*parameter[0]) * getCountsFor(tryparm, i); }

    @Override
    public double evaluate(double[] tryparm) {
        double S0 = tryparm[1];
        double chi2 = super.evaluate(tryparm);
        if(S0 < dF) return (1.0 + Math.pow(S0/dF - 1.0, 2.0)) * chi2;
        return chi2;
    }


    @Override
    public double minimize(int n) {
        double chi2 = super.minimize(n);

        double N = parameter[0]/dF * Unit.mJy * Unit.deg2 / mapArea;
        double dN = N/parameter[0] * getStandardError(0, 0.1*parameter[0]);
        double S0 = parameter[1] / Unit.Jy;
        double dS0 = getStandardError(1, 0.1*parameter[1]) / Unit.Jy;
        double p = parameter[2];
        double dp = getStandardError(2, 0.1);


        System.err.println("N0 = " + Util.e3.format(N) + " +- " + Util.e1.format(dN) + " #/mJy/deg2");
        System.err.println("S0 = " + Util.e3.format(S0) + " +- " + Util.e1.format(dS0) + " Jy");
        System.err.println("p = " + Util.f3.format(p) + " +- " + Util.f3.format(dp));

        return chi2;
    }

    @Override
    public int getNoiseScaleIndex() { return 3; }

    @Override
    public int getOffsetIndex() { return 4; }

}



