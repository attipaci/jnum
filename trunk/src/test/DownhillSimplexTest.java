package test;

import java.util.ArrayList;

import jnum.Util;
import jnum.data.fitting.ChiSquared;
import jnum.data.fitting.CorrelationMatrix;
import jnum.data.fitting.CovarianceMatrix;
import jnum.data.fitting.DownhillSimplex;
import jnum.data.fitting.Parameter;
import jnum.data.fitting.Parametric;
import jnum.data.fitting.RelationalConstraint;
import jnum.math.Range;

public class DownhillSimplexTest {

    public static void main(String[] args) {
     
        final Parameter a = new Parameter("a", 1.0, new Range(-2.0, 2.0), 0.1);
        final Parameter b = new Parameter("b", 1.0, 0.1);
        
        Parametric<Double> sum = new Parametric<Double>() {
            @Override
            public Double evaluate() { return a.value() + b.value(); }
        };
        
        RelationalConstraint c = new RelationalConstraint("sum", sum, RelationalConstraint.EQUALS, 6.0, 0.01);
        
        ChiSquared f = new ChiSquared() {
            @Override
            public Double evaluate() {
                return Math.pow(Math.abs(a.value()-3.0), 1.0) + Math.pow(Math.abs(b.value()-4.0), 0.5);
            }
        };
        
       
        ArrayList<Parameter> parms = new ArrayList<Parameter>();
        parms.add(a);
        parms.add(b);
    
        
        DownhillSimplex m = new DownhillSimplex(f, parms);
        //m.setVerbose(true);
        m.setPrecision(1e-12);
        m.addConstraint(c);
        m.minimize();
        m.print();
        
        CovarianceMatrix C = m.getCovarianceMatrix();
        
        System.err.println(C.toString(Util.e3));
        
        CorrelationMatrix R = C.getCorrelationMatrix();
        
        System.err.println(R.toString(Util.f3));
        
        System.err.println("Sigmas: " + R.sigmasToString());
        
    }
    
}
