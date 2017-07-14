package jnum.devel.sourcecounts;

import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;

class BrokenPowerLaw extends RegularTemplate {

    @Override
    public void init(Histogram mapHistogram, double maxFlux, int bins) {
        super.init(mapHistogram, maxFlux, bins);

        double S1 = 5.0 * Unit.mJy;
        double N1 = guessN(S1);

        double[] initparms = { N1, 2*dF, S1, -3.0, -5.0, noiseScaling, 0.0 };
        startSize = new double[] { 0.1*N1, 0.3*dF, dF, 0.03, 0.03, 0.1, 0.1 };
        init(initparms);
    }

    @Override
    public double getCountsFor(double[] tryparm, int i) {
        double N1 = tryparm[0];
        double S0 = bounded ? tryparm[1] : 0.0;
        double S1 = tryparm[2];
        double p1 = tryparm[3];
        double p2 = tryparm[4];

        double F = templates[i].flux;

        double bottom = (i+1) * dF;
        double top = bottom + dF;

        if(top < S1) return getFillFraction(i, S0, p1) * N1 * Math.pow(F/S1, p1);
        else if(bottom > S1) return getFillFraction(i, S0, p2) * N1 * Math.pow(F/S1, p2);
        else {
            double N2 = N1 * Math.pow(F/S1, p2);
            double dN = N1 * (Math.pow(bottom, p1+1.0) - Math.pow(bottom, p2+1.0) + Math.pow(top, p2+1.0) - Math.pow(top, p1+1.0)) / (Math.pow(bottom, p2+1.0) - Math.pow(top, p2+1.0));
            return getFillFraction(i, S0, 0.5*(p1+p2)) * (N2 - dN);
        }
    }

    @Override
    public double getCountsErrFor(double[] tryparm, int i) { return getStandardError(0, 0.1*parameter[0]) * getCountsFor(tryparm, i); }

    @Override
    public double evaluate(double[] tryparm) {
        double S0 = bounded ? tryparm[1] : 0.0;
        double p1 = tryparm[3];

        double chi2 = super.evaluate(tryparm);
        if(S0 < dF) return (1.0 + Math.pow((S0/dF-1.0), 2.0)) * chi2;
        if(p1 > 0.0) chi2 *= (1.0 + p1*p1);
        return chi2;
    }

    @Override
    public double getBackground(double[] tryparm) {
        if(bounded) {
            double N0 = tryparm[0];
            double S0 = bounded ? tryparm[1] : 0.0;
            double p = tryparm[3];

            double delta = S0 < dF ? Math.pow(dF, 2.0 + p) - Math.pow(S0, 2.0 + p) : 0.0;
            delta *= N0 * Math.pow(5.0 * Unit.mJy, -p) / (2.0 - p);
            return delta + super.getBackground(tryparm);
        }
        return super.getBackground(tryparm);
    }

    @Override
    public double minimize(int n) {
        double chi2 = super.minimize(n);

        double N = parameter[0]/dF * Unit.mJy * Unit.deg2 / mapArea;
        double dN = N/parameter[0] * getStandardError(0, 0.1*parameter[0]);
        double S0 = parameter[1] / Unit.Jy;
        double dS0 = getStandardError(1, 0.01*parameter[1]) / Unit.Jy;
        double S1 = parameter[2] / Unit.Jy;
        double dS1 = getStandardError(2, 0.01*parameter[2]) / Unit.Jy;
        double p1 = parameter[3];
        double dp1 = getStandardError(3, 0.001);
        double p2 = parameter[4];
        double dp2 = getStandardError(4, 0.001);


        System.err.println("N(S1) = " + Util.e3.format(N) + " +- " + Util.e1.format(dN) + " #/mJy/deg2");
        if(bounded) System.err.println("S0 = " + Util.e3.format(S0) + " +- " + Util.e1.format(dS0) + " Jy");
        System.err.println("S1 = " + Util.e3.format(S1) + " +- " + Util.e1.format(dS1) + " Jy");
        System.err.println("a1 = " + Util.f3.format(p1) + " +- " + Util.f3.format(dp1));
        System.err.println("a2 = " + Util.f3.format(p2) + " +- " + Util.f3.format(dp2));

        return chi2;
    }

    @Override
    public int getNoiseScaleIndex() { return 5; }

    @Override
    public int getOffsetIndex() { return 6; }

}



