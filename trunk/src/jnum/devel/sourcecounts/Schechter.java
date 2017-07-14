package jnum.devel.sourcecounts;

import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;

class Schechter extends RegularTemplate {

    @Override
    public void init(Histogram mapHistogram, double maxFlux, int bins) {
        super.init(mapHistogram, maxFlux, bins);    

        double S1 = 0.2*maxFlux;
        double N0 = guessN(S1);

        double[] initparms = { N0, 2.0 * dF, S1, -3.0, noiseScaling, 0.0 };
        startSize = new double[] { 0.1*N0, 0.1*dF, 0.3*dF, 0.1, 0.1, 0.1 };
        init(initparms);
    }


    @Override
    public double getCountsFor(double[] tryparm, int i) {
        double N0 = tryparm[0];
        double S0 = bounded ? tryparm[1] : 0.0;
        double S1 = tryparm[2];
        double p = tryparm[3];
        double F = templates[i].flux;

        return getFillFraction(i, S0, p-F/S1) * N0 * Math.pow(F/S1, p) * Math.exp(-F/S1);
    }

    @Override
    public double getCountsErrFor(double[] tryparm, int i) { return getStandardError(0, 0.1*parameter[0]) * getCountsFor(tryparm, i); }

    @Override
    public double evaluate(double[] tryparm) {
        double S0 = tryparm[1];
        double chi2 = super.evaluate(tryparm);
        if(S0 < dF) return (1.0 + Math.pow((S0/dF-1.0), 2.0)) * chi2;
        return chi2;
    }

    @Override
    public double getBackground(double[] tryparm) {
        if(bounded) {
            double N0 = tryparm[0];
            double S0 = tryparm[1];
            double S1 = tryparm[2];
            double p = tryparm[3];
            double delta = S0 < dF ? Math.pow(dF, 2.0 + p) - Math.pow(S0, 2.0 + p) : 0.0;
            delta *= N0 * Math.pow(S1, -p) / (2.0 - p);
            return delta + super.getBackground(tryparm);
        }
        return super.getBackground(tryparm);
    }

    @Override
    public double minimize(int n) {
        double chi2 = super.minimize(n);

        double N = parameter[0]/dF * Unit.mJy * Unit.deg2 / mapArea;
        double dN = N/parameter[0] * getStandardError(0, 0.01*parameter[0]);
        double S0 = parameter[1] / Unit.Jy;
        double dS0 = getStandardError(1, 0.1*parameter[1]) / Unit.Jy;
        double S1 = parameter[2] / Unit.Jy;
        double dS1 = getStandardError(2, 0.01*parameter[2]) / Unit.Jy;
        double alpha = parameter[3];
        double dalpha = getStandardError(3, 0.001);


        System.err.println("N' = " + Util.e3.format(N) + " +- " + Util.e1.format(dN) + " #/mJy/deg2");
        if(bounded) System.err.println("S0 = " + Util.e3.format(S0) + " +- " + Util.e1.format(dS0) + " Jy");
        System.err.println("S1 = " + Util.e3.format(S1) + " +- " + Util.e1.format(dS1) + " Jy");
        System.err.println("p = " + Util.f3.format(alpha) + " +- " + Util.f3.format(dalpha));

        return chi2;
    }


    @Override
    public int getNoiseScaleIndex() { return 4; }

    @Override
    public int getOffsetIndex() { return 5; }


}


