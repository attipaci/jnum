package jnum.devel.sourcecounts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import crush.CRUSH;
import crush.astro.AstroMap;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.Util;
import jnum.data.Histogram;
import jnum.data.fitting.Parameter;
import jnum.data.fitting.Parametric;
import jnum.data.image.Data2D;
import jnum.data.image.GridImage2D;
import jnum.data.image.GridMap2D;

public abstract class OldCountsModel implements Parametric<Double> { 
    private GridMap2D<?> map;
    
    private double mapArea;
    
    double s2nResolution, fluxResolution;
    int oversampling = 1;
  
    
    Histogram s2nHistogram;
    
    ProbabilityDistribution mapDistribution, noiseDistribution;
    ProbabilityDistribution modelDistribution, hiresModelDistribution;
    
    CharSpectrum modelSpectrum, noiseSpectrum;
    ComponentSpectrum[] componentSpectra, sourceSpectra, fluxSpectra;
   
    Parameter background;
    Parameter noiseScale;
    Parameter offset;
     
    
    //RandomGenerator random = new RandomGenerator2();
    private Random random = new Random();

    public OldCountsModel(GridMap2D<?> map, double s2nBinSize, int oversampling) {
        this.map = map;
        this.oversampling = oversampling;
        
        s2nHistogram = Histogram.createFrom(map.getS2N(), s2nBinSize);
        mapArea = map.getArea();
    }

    public double getMapArea() { return mapArea; }
     
    public void init(double[] fluxes) {      
        
       
        double maxDev = s2nHistogram.getMaxDev();
        System.err.println("Max histogram deviation is: " + maxDev);

        //int bins = 2 * ExtraMath.pow2ceil((int) Math.ceil(4.0 * maxDev / s2nResolution));

        double maxFlux = 0.0;
        for(int i=0; i<fluxes.length; i++) if(fluxes[i] > maxFlux) maxFlux = fluxes[i];
        fluxResolution = 8.0 * maxFlux / bins / oversampling;

        System.err.println("Using " + bins + " histogram bins.");

        s2nResolution = s2nHistogram.getResolution();
        mapDistribution = new ProbabilityDistribution(s2nHistogram, grid, 4.0);
        
        System.err.println("Using " + mapDistribution.size() + " histogram bins.");
        
        modelDistribution = new ProbabilityDistribution(mapDistribution.getRange(), mapDistribution.getResolution());
        noiseDistribution = new ProbabilityDistribution(mapDistribution.getRange(), mapDistribution.getResolution() / oversampling);
        hiresModelDistribution = new ProbabilityDistribution(mapDistribution.getRange(), mapDistribution.getResolution() / oversampling);
        

        CharSpectrum[][] temps = makeTemplates(fluxes, 1000, resolution / oversampling, oversampling * bins);
        templates = temps[0];
        fluxSpecs = temps[1];

        model = new CharSpectrum(new double[hiresModel.length]);
        model.noData();
        sources = new CharSpectrum[templates.length];
        for(int i=0; i<sources.length; i++) sources[i] = new CharSpectrum(model.size());
    }

    
    public void setNoiseScaling(AstroMap jackknife) {
        // TODO
    }
    
    public String getCoreFileName() {
        return CRUSH.workPath + File.separator + getClass().getSimpleName();
    }
    

    public CharSpectrum[][] makeTemplates(double[] fluxes, int N, double s2nResolution, int bins) {
        fluxResolution *= map.getUnit().value();

        CharSpectrum[][] templates = new CharSpectrum[2][fluxes.length];

        double[][] s2nCounts = new double[fluxes.length][bins];
        double[][] fluxCounts = new double[fluxes.length][bins];


        AstroMap model = (AstroMap) map.copy(false);

        // If large-scale-structure filtering is used, then
        // assume average rms & weight in flagged areas.
        // and remove flags for the simulation...
        /*
        double averms = map.getTypicalRMS();
        double avew = 1.0 / (map.weightFactor * averms * averms * map.pointsPerBeam);
        if(!Double.isNaN(map.extFilterFWHM)) for(int i=0; i<map.sizeX; i++) for(int j=0; j<model.sizeY; j++) if(map.flag[i][j] != 0) {
            model.weight[i][j] = avew;
            model.rms[i][j] = averms;
            model.flag[i][j] = 0;
        }
         */
        model.setVerbose(false);

        double imageFWHM = map.getUnderlyingBeam().getCircularEquivalentFWHM();
        double fullArea = (map.sizeX() * map.getResolution().x() + imageFWHM) * (map.sizeY() * map.getResolution().y() + imageFWHM);

        // To avoid excessive overlapping (unless counts warrant it) put one source for every 10 beams...
        final int n = (int)Math.ceil(0.3 * fullArea / model.getImageBeamArea());
        System.err.println("Creating models with " + N + "+ simulated sources in steps of " + n + " sources/map. ");    

        N = Math.max(n, N);
        //System.err.println("Creating models with >=" + (int)(N * fullArea/mapArea) + " simulated sources in steps of " + n + " sources/map. ");       



        int sources = 0;
        int negs = 0;
        int points = 0;

        while(sources < N) {    
            // Start with a clear map...
            model.noData();      
            model.undoFilterCorrect();
            
            model.clearRegions();
            for(int i=0; i<n; i++) createRandomSource(model, 1.0);          

            sources += n;

            // Insert unfiltered sources. Filter afterwards...
            model.addPointSources();

            // Filter if necessary...
            if(!Double.isNaN(map.getExtFilterFWHM())) {
                model.level(true);
                model.filterAbove(map.getExtFilterFWHM());
                // The filtering rescales the weight map. So reinstate the original weights.
                model.setWeight(map.getWeights());
                model.setWeightScale(map.getWeightScale());
            }

            model.level(true);
            double[][] s2n = model.getS2N();

            for(int i=0; i<model.sizeX(); i++) for(int j=0; j<model.sizeY(); j++) if(map.isUnflagged(i,j)) {

                for(int k=0; k<s2nCounts.length; k++, points++) {

                    // Bin the signal-to-noise values...
                    int bin = (int) Math.round(fluxes[k] * s2n[i][j] / s2nResolution);
                    if(bin > bins/2 || bin <= -bins/2) System.err.println("WARNING! S/N Data outside of binning range."); 
                    else {
                        if(bin < 0) bin += bins;
                        s2nCounts[k][bin]++;
                    }   

                    // Now bin the flux values...
                    bin = (int) Math.round(fluxes[k] * model.get(i, j) / fluxResolution);
                    if(bin > bins/2 || bin <= -bins/2) System.err.println("WARNING! Flux Data outside of binning range."); 
                    else {
                        if(bin < 0) {
                            bin += bins;
                            negs++;
                        }
                        fluxCounts[k][bin]++;
                    }           

                }
            }
        }

        System.err.println(" Sanity check: " + Util.f3.format(100.0 * negs / points) + "% negative fluxes.");

        //System.err.println(CharSpec.toString(fluxCounts[fluxCounts.length-1]));

        double areaFactor = getMapArea() / fullArea;

        for(int k=0; k<s2nCounts.length; k++) {
            CharSpectrum s2nSpec = new CharSpectrum(s2nCounts[k]);
            s2nSpec.flux = fluxes[k];
            s2nSpec.testSources = areaFactor * n;           
            templates[0][k] = s2nSpec;

            CharSpectrum fluxSpec = new CharSpectrum(fluxCounts[k]);        
            fluxSpec.flux = fluxes[k];
            fluxSpec.testSources = areaFactor * n;          
            templates[1][k] = fluxSpec;
        }

        return templates;
    }

    public void createRandomSource(AstroMap image, double flux) {
        double fwhm = image.getImageBeam().getCircularEquivalentFWHM() / Math.sqrt(image.getPixelArea());
        double i0 = (image.sizeX() + fwhm) * random.nextDouble() - fwhm/2.0;
        double j0 = (image.sizeY() + fwhm) * random.nextDouble() - fwhm/2.0;

        image.addRegion(image.dXofIndex(i0), image.dYofIndex(j0), image.getImageFWHM(), flux/Unit.jansky * image.janskyPerBeam.evaluate());
    }

    
    @Override
    public double minimize(int n) {
        System.err.println("Minimizing...");

        verbose = true;

        for(int i=0; i<3; i++) {
            if(i > 0) {
                applyOffset(getOffset());
                init(parameter);
            }
            super.minimize(n);  
            shrinkInitSize(0.3);
        }


        noiseScaling = parameter[getNoiseScaleIndex()];

        try { writeFit(new PrintStream(new FileOutputStream(getCoreFileName() + ".fit"))); }
        catch(IOException e) { e.printStackTrace(); }

        try { writeCounts(new PrintStream(new FileOutputStream(getCoreFileName() + ".cnt"))); }
        catch(IOException e) { e.printStackTrace(); }

        writeSimulated();

        /*
        double[][] C = getCovarianceMatrix(0.1);
        double[] sigma = new double[C.length];
        for(int i=0; i<C.length; i++) sigma[i] = Math.sqrt(Math.abs(C[i][i]));

        for(int i=0; i<C.length; i++) {
            for(int j=0; j<C.length; j++) {
                C[i][j] /= sigma[i] * sigma[j];
                System.err.print("\t" + Util.f2.format(C[i][j]));
            }
            System.err.println();
        }
         */

        return getChi2();
    }       

    public void setMapHistogram(Histogram mapHistogram) {
        mapDistribution = mapHistogram.toFFTArray(modelHist.length);
    }       

    public void setNoise(Histogram noiseHistogram) {
        setNoise(noiseHistogram.toFFTArray(modelHist.length));      
    }

    public void setNoise(double[] hist) {
        noise = new CharSpectrum(hist);
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public abstract double getCountsFor(double[] tryparm, int i);

    public abstract double getCountsErrFor(double[] tryparm, int i);

    public abstract double getdF(int i);

    public void fixedNoise() {
        fitList.remove(new Integer(getNoiseScaleIndex()));
    }

    private boolean fitOffset = true;       
    public void noOffset() {
        fitList.remove(new Integer(getOffsetIndex()));
        fitOffset = false;
    }

    @Override
    public void fitAll() {
        super.fitAll();
        fitOffset = true;
    }

    public void applyOffset(double s2nOffset) {
        double dBackGround = s2nOffset * mapRMS / Unit.Jy * map.getUnit().value();
        backGround += dBackGround;
        map.addValue(dBackGround);
        parameter[getOffsetIndex()] -= s2nOffset;
        if(saveparm != null) saveparm[getOffsetIndex()] -= s2nOffset;
    }

    public double getBackground(double[] tryparm) {
        double bg = 0.0;
        for(int i=0; i<templates.length; i++) bg += templates[i].flux * getCountsFor(tryparm, i);   
        bg *= Unit.deg2 / mapArea;
        //bg -= backGround / map.janskyPerBeam.value * Unit.Jy * Unit.deg2 / map.imageBeamArea;
        return bg;
    }

    // Assume 50 sources per sqdeg per mJy at 5 mJy...
    public double guessN(double S, double dF) { 
        return 50.0 * Math.pow(S/(5.0 * Unit.mJy), -4.0) * getMapArea() / Unit.deg2 * dF / Unit.mJy;
    }

    public double getReducedChi2() {
        return getChi2() / (s2nHistogram.size() - fitList.size());           
    }

    @Override
    public Double evaluate() {
        gaussianNoise(noiseScale.value(), offset.value(), noiseHist, resolution / oversampling);
        setNoise(noiseHist);


        model.copy(noise);

        double factor = 1.0;
        double chi2 = 0.0; 

        for(int i=0; i<templates.length; i++) {
            double N = getCountsFor(tryparm, i);
            if(N <= 0.0) factor *= 1.0 - N;
            else {
                templates[i].forSources(N, sources[i]);
                model.multiplyBy(sources[i]);
            }
        }
        model.toProbabilities(hiresModel);



        Arrays.fill(modelHist, 0.0);
        for(int i=0, j=0; i<modelHist.length; i++) for(int k=0; k<oversampling; k++, j++) 
            modelHist[i] += hiresModel[j]; 

        double norm = 0.0;
        for(int i=0; i<modelHist.length; i++) norm += modelHist[i];
        for(int i=0; i<modelHist.length; i++) modelHist[i] /= norm;

        for(int i=0; i<mapDistribution.length; i++) {
            modelHist[i] *= mapPixels;
            double var = modelHist[i] > 1e-6 ? modelHist[i] : 1e-6;
            double dev = mapDistribution[i] - modelHist[i];
            chi2 += dev * dev / var;
        }

        if(!Double.isNaN(background)) {
            double dev = (getBackground(tryparm) - background) / dBG;
            chi2 += dev * dev;
        }

        return factor * factor * chi2;
    }

    public void writeTemplates() throws IOException {       
        for(int i=0; i<templates.length; i++) {
            PrintWriter out = new PrintWriter(new FileOutputStream(CRUSH.workPath + File.separator + "template-" + (i+1) + ".dat"));
            double[] p = new double[mapDistribution.length];
            templates[i].toProbabilities(p);
            out.println(templates[i].toString(p));
            out.close();
        }
    }   


    public void writeSourceFluxDistribution() throws IOException {  
        CharSpectrum fluxSpec = fluxSpecs[0].copy();
        CharSpectrum temp = fluxSpecs[0].copy();

        fluxSpec.clear();

        for(int i=0; i<fluxSpecs.length; i++) {     
            fluxSpecs[i].forSources(Math.max(0.0, getCountsFor(parameter, i)), temp);
            fluxSpec.multiplyBy(temp);
        }

        String fileName = getCoreFileName() + ".dist";
        PrintWriter out = new PrintWriter(new FileOutputStream(fileName));
        double[] n = fluxSpec.toProbabilities();

        // Make sure it is a proper probability dsitribution with integral 1.0
        double renorm = 0.0;
        for(int i=0; i<n.length; i++) renorm += n[i];
        for(int i=0; i<n.length; i++) n[i] /= renorm;

        // TODO remove median from distribution...

        out.println("# Resolution = " + Util.e6.format(fluxResolution / Unit.Jy) + " Jy");
        //out.println("# Re-centered around Median of distribution...")
        out.println(fluxSpec.toString(n));
        out.close();
        //System.err.println("Written " + fileName);
    }   


    public void writeCounts(PrintStream out) {
        out.println("# flux\tparm\t\terr");

        double totalCounts = 0.0;

        for(int i=templates.length-1; i>=0; i--) {
            double binCounts = getCountsFor(parameter, i);
            out.println(Util.e3.format(templates[i].flux / Unit.Jy) + "\t" 
                    + Util.e3.format(binCounts) + "\t" 
                    + Util.e3.format(getCountsErrFor(parameter, i)) + "\t"
                    + Util.e3.format(totalCounts)
                    );
            totalCounts += binCounts;
        }
        out.flush();
    }

    public void writeFit(PrintStream out) {
        out.println("# data\tmodel\tdiff");
        int n = mapDistribution.length >> 1;
        for(int i=n+1; i<mapDistribution.length; i++) 
            out.println(Util.f2.format(resolution*(i - mapDistribution.length)) + 
                    "\t" + Util.e3.format(mapDistribution[i]) +
                    "\t" + Util.e3.format(modelHist[i]) +
                    "\t" + Util.e3.format(modelHist[i] - mapDistribution[i])
                    );
        for(int i=0; i<n; i++)
            out.println(Util.f2.format(resolution*(i)) + 
                    "\t" + Util.e3.format(mapDistribution[i]) +
                    "\t" + Util.e3.format(modelHist[i]) +
                    "\t" + Util.e3.format(modelHist[i] - mapDistribution[i])
                    );
        out.close();            
    }

    public void writeSimulated() {
        double[] fluxes = new double[templates.length];
        double[] counts = new double[templates.length];
        for(int i=0; i<templates.length; i++) {
            fluxes[i] = templates[i].flux;
            counts[i] = getCountsFor(i);                
        }

        SkyMap sim = getSimulated(fluxes, counts);

        // Write out the simulated
        sim.fileName = getCoreFileName() + ".sim.fits";
        try { sim.write(); }
        catch(Exception e) { e.printStackTrace(); }
    }

    public AstroMap getSimulated(double[] fluxes, double[] counts) {
        AstroMap sim = (AstroMap) map.copy(false);

        sim.noData();

        System.err.println("Creating simulated map. ");

        boolean wasVerbose = sim.isVerbose();
        sim.setVerbose(false);

        // Add unfiltered point sources...
        sim.undoFilterCorrect();

        int N = 0;

        double imageFWHM = map.getImageBeam().getCircularEquivalentFWHM();
        
        double fullArea = (map.sizeX() * map.getResolution().x() + imageFWHM) * (map.sizeY() * map.getResolution().y() + imageFWHM);

         
        // Insert the desired number of sources...
        for(int i=0; i<fluxes.length; i++) {
            System.err.print("#");
            System.err.flush();

            double peak = fluxes[i];
            double binCounts = counts[i] * fullArea / map.getArea();      

            for(int added=0; added < binCounts; added++, N++) {
                double x = (map.sizeX() + imageFWHM) * random.nextDouble() - 0.5 * imageFWHM;
                double y = (map.sizeY() + imageFWHM) * random.nextDouble() - 0.5 * imageFWHM;
                boolean addSource =  (added+1) < binCounts ? true : random.nextDouble() < binCounts - added;
                if(addSource) sim.addRegion(sim.dXofIndex(x), sim.dYofIndex(y), sim.getImageFWHM(), peak);
            }
            sim.addPointSources();
            sim.clearRegions();
        }

        System.err.println();
        System.err.println(N + " sources inserted.");
        sim.setVerbose(wasVerbose);

        // Write out a pure source map (unfiltered...);
        //sim.fileName = "sources.fits";
        //try { sim.write(); }
        //catch(Exception e) { e.printStackTrace(); }

        // If not using a jackknife then create a smoothed noise map...
        if(jackknife == null) {
            System.err.println("Adding simulated noise (" + noiseScale.value() + "x)...");
            AstroMap noise = (AstroMap) map.copy(false);
            noise.reset(true);
           
            for(int i=0; i<sim.sizeX(); i++) for(int j=0; j<sim.sizeY(); j++) if(noise.isUnflagged(i, j))
                noise.set(i, j, noiseScale.value() * random.nextGaussian() / Math.sqrt(noise.weightAt(i, j)));

            noise.smoothTo(map.getUnderlyingBeam().getCircularEquivalentFWHM());

            for(int i=0; i<sim.sizeX(); i++) for(int j=0; j<sim.sizeY(); j++) if(noise.isUnflagged(i,j)) 
                sim.set(i, j, sim.get(i, j) + noise.get(i, j));

        }

        // Filter like in map & jackknife...
        if(!Double.isNaN(map.getExtFilterFWHM())) {
            sim.level(true);
            sim.filterAbove(map.getExtFilterFWHM());

            // The filtering rescales the weight map. So reinstate the original weights.
            sim.setWeight(map.getWeights());
            sim.setWeightScale(map.getWeightScale());
        }

        // Adding jackknife noise...
        if(jackknife != null) {
            AstroMap noise = (AstroMap) sim.copy();
            jackknife.regridTo(noise);
            System.err.println("Adding jackknifed noise to map...");
            for(int i=0; i<sim.sizeX(); i++) for(int j=0; j<sim.sizeY(); j++) if(sim.isUnflagged(i,j))
                sim.set(i, j, sim.get(i, j) + noise.get(i, j));
        }

        sim.level(true);

        return sim;     
    }


    public Histogram getMapHistogram(double resolution) {
        System.err.println("Calculating map histogram.");

        Histogram histogram = new Histogram(resolution);

        double[][] s2n = map.getS2N();
        for(int i=0; i<map.sizeX(); i++) for(int j=0; j<map.sizeY(); j++) if(map.isUnflagged(i, j)) {
            histogram.add(s2n[i][j]);
        }

        return histogram;
    }


    public void gaussianNoise(double noiseScale, double offset, double[] noisehist, double resolution) {
        int N = Math.min(noisehist.length >> 1, (int)Math.ceil(8.0 / (noiseScale * resolution)));
        Arrays.fill(noisehist, 0.0);
        double dev = offset / noiseScale;
        noisehist[0] = Math.exp(-0.5*dev*dev);
        for(int i=1; i<=N; i++) {
            dev = (i * resolution + offset) / noiseScale;
            noisehist[i] = Math.exp(-0.5 * dev * dev);
            dev -= 2.0 * offset / noiseScale;
            noisehist[noisehist.length - i] = Math.exp(-0.5 * dev * dev);
        }

        //System.err.println(CharSpec.toString(noisehist));
    }

    
    
}


