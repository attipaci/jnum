package jnum.devel.sourcecounts;


import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.Complex;
import jnum.math.Vector2D;

import java.io.*;
import java.util.*;

import crush.CRUSH;
import crush.astro.AstroMap;
import jnum.data.Histogram;
import jnum.data.fitting.DownhillSimplex;
import jnum.data.fitting.Parameter;
import jnum.data.fitting.Parametric;
import jnum.fft.DoubleFFT;
import nom.tam.fits.*;

public class SourceCounts {
    
    String type = "power";
    double maxFlux;
    int bins = 50;
    double resolution = 0.25;
    int oversampling = 1;	
    double background = Double.NaN;

    double dBG = 0.0;
    boolean fixedNoise = false;
    boolean bounded = true;
    int mapPixels;
    double mapArea, mapRMS;
    int minTestSources = 100;

    public SourceCounts(AstroMap map) {
       
        map.undoFilterCorrect();

        mapPixels = map.countPoints();
        mapArea = mapPixels * map.getPixelArea();

        System.err.println("Map area is " + Util.e3.format(mapArea/Unit.deg2) + " deg2.");


        map.reweight(true);
        noiseScaling = 1.0;

        double[][] rms = map.getRMS();
        double sumw = 0.0;
        int n= 0;
        
        for(int i=0; i<map.sizeX(); i++) for(int j=0; j<map.sizeY(); j++) if(map.isUnflagged(i, j)) {
            sumw += 1.0 / (rms[i][j] * rms[i][j]);
            n++;
        }
        
        
        mapRMS = Math.sqrt(n/sumw);
        maxFlux = map.getS2NImage().getMax() * mapRMS;
    }

    public static void main(String args[]) {

        try {
            AstroMap map = new AstroMap();
            map.read(args[args.length-1]);
            
            SourceCounts sc = new SourceCounts(map);

            for(int i=0; i<args.length-1; i++) sc.option(args[i]);

            sc.fit();
        }
        catch(Exception e) { e.printStackTrace(); }

    }

    public boolean option(String line) {
        if(line.charAt(0) != '-') return false;

        StringTokenizer tokens = new StringTokenizer(line.substring(1), "=:");
        String key = tokens.nextToken();
        String value = tokens.hasMoreTokens() ? tokens.nextToken() : "";

        if(key.equalsIgnoreCase("type")) {
            type = value;
        }
        else if(key.equalsIgnoreCase("max")) {
            maxFlux = Double.parseDouble(value) * Unit.Jy;
        }
        else if(key.equalsIgnoreCase("bins")) {
            bins = Integer.parseInt(value);
        }
        else if(key.equalsIgnoreCase("resolution")) {
            resolution = Double.parseDouble(value);
        }
        else if(key.equalsIgnoreCase("jackknife")) {
            try {
                jackknife = new AstroMap();
                jackknife.read(value);
                jackknife.setWeight(map.getWeights());
                jackknife.setWeightScale(map.getWeightScale());
                jackknife.setFlag(map.getFlag());
                noiseScaling = 1.0 / Math.sqrt(jackknife.getChi2(true));
                System.err.println("Jackknife noise: " + Util.f3.format(noiseScaling) + "x ");
                fixedNoise = true;
            }
            catch(Exception e) { e.printStackTrace(); }
        }
        else if(key.equalsIgnoreCase("noiseScale")) {
            System.err.println("Fixed noise scaling: " + Util.f3.format(noiseScaling) + "x ");
            noiseScaling = Double.parseDouble(value);
            fixedNoise = true;
        }
        else if(key.equalsIgnoreCase("background")) {
            StringTokenizer values = new StringTokenizer(value, "+-:;,()");
            background = Double.parseDouble(values.nextToken()) * Unit.Jy;
            dBG = values.hasMoreTokens() ? Double.parseDouble(values.nextToken()) * Unit.Jy : 0.1 * background;
            System.err.println("Background: " + Util.e3.format(background/Unit.Jy) + " +- " + Util.e3.format(dBG/Unit.Jy) + " Jy/deg2");
        }
        else if(key.equalsIgnoreCase("nobounds")) {
            bounded = false;
        }	
        else if(key.equalsIgnoreCase("oversample")) {
            oversampling = Integer.parseInt(value);
        }	
        else if(key.equalsIgnoreCase("sim")) {
            System.err.println("simulating!!!");
            try { simulate(value); }
            catch(Exception e) { e.printStackTrace(); }
            System.exit(0);
        }

        return true;
    }


    public void simulate(String fileName) throws IOException, FitsException, HeaderCardException {
        // Load the the counts data into fluxes and counts...
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String record = null;
        Vector<Vector2D> entries = new Vector<Vector2D>();

        while((record = in.readLine()) != null) if(record.length() > 0) if(record.charAt(0) != '#') {
            StringTokenizer columns = new StringTokenizer(record);
            Vector2D entry = new Vector2D();
            entry.setX(Double.parseDouble(columns.nextToken()) * Unit.Jy);
            entry.setY(Double.parseDouble(columns.nextToken()));
            entries.add(entry);
        }
        
        in.close();

        double[] fluxes = new double[entries.size()];
        double[] counts = new double[entries.size()];
        for(int i=0; i<entries.size(); i++) {
            Vector2D entry = entries.get(i);
            fluxes[i] = entry.x();
            counts[i] = entry.y();				
        }

        // Simulate the data
        AstroMap sim = getSimulated(fluxes, counts);

        // Write out the simulated
        sim.fileName = "sim.fits";
        sim.write();
    }

    public void fit() {

        RegularFit model;
        if(type.equalsIgnoreCase("power")) model = new PowerLawFit();
        else if(type.equalsIgnoreCase("broken")) model = new BrokenPowerLawFit();
        else if(type.equalsIgnoreCase("barger")) model = new BargerFit();
        else if(type.equalsIgnoreCase("exponential")) model = new ExponentialFit();
        else if(type.equalsIgnoreCase("schechter")) model = new SchechterFit();
        else if(type.equalsIgnoreCase("individual")) model = new IndividualFit();
        else throw new IllegalArgumentException("Uknown function type: " + type);

        System.err.println(" Function : " + model.getClass().getSimpleName());
        System.err.println(" Flux Bins: " + bins);
        System.err.println(" Max Flux : " + maxFlux / Unit.Jy + " Jy");
        System.err.println(" S/N bin  : " + resolution);
        System.err.println(" Oversamp.: " + oversampling);

        model.init(getMapHistogram(resolution), maxFlux, bins);
        model.verbose = false;

        model.fitAll();
        if(fixedNoise) model.fixedNoise();
        //if(!Double.isNaN(map.extFilterFWHM)) model.noOffset();

        model.minimize(3);

        //model.writeCounts(System.out);
        /*
		try { model.writeTemplates(); }
		catch(IOException e) {}
         */

        try { model.writeSourceFluxDistribution(); }
        catch(IOException e) { e.printStackTrace(); }

        System.err.println("Map Chi = " + Util.f5.format(model.parameter[model.getNoiseScaleIndex()]));
        if(Double.isNaN(map.getExtFilterFWHM())) {
            System.err.println("S2N Offset = " + Util.e3.format(model.parameter[model.getOffsetIndex()]));
            System.err.println("Map Level = " + Util.e3.format(model.backGround / map.getUnit().value()) + " " + map.getUnit().name());
        }
        System.err.println("Background = " + Util.f3.format(model.getBackground(model.parameter) / Unit.Jy) + " Jy/deg2.");
        System.err.println("Model Chi2 = " + Util.f3.format(Math.sqrt(model.getReducedChi2())));

    }




    /*
	public Histogram getNoiseHistogram(double resolution, double offset) {	
		Histogram histogram = new Histogram(resolution);

		int N = (int)Math.ceil(8.0 / resolution);

		double lower = -(N+0.5) * resolution - offset;
		double below = ConfidenceCalculator.getOutsideProbability(-lower);

		for(int bin=-N; bin<=N; bin++) {
			double upper = lower + resolution;
			double inclusive = upper > 0.0 ? 
					1.0 - ConfidenceCalculator.getOutsideProbability(upper) 
					: ConfidenceCalculator.getOutsideProbability(-upper);

			double diff = inclusive - below;
			histogram.bins.put(bin, new Counter(diff));	

			lower = upper;
			below = inclusive;
		}

		return histogram;
	}
     */

 

 
  
    
}