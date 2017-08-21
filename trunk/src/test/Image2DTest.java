package test;

import jnum.Util;
import jnum.data.image.FlatGrid2D;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Image2D;
import jnum.data.image.Values2D;
import jnum.data.image.overlay.Referenced2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.parallel.ParallelTask;

public class Image2DTest {

    public static void main(String[] args) {
        
        Image2D image = Image2D.createType(Double.class);  
        if(image == null) {
            System.err.println("null image!");
            System.exit(1);
        }
      
        image.setExecutor(ParallelTask.newDefaultParallelExecutor(4));   
        image.setSize(4, 3);
        
        image.set(2, 0, 20.0);
        image.set(1, 2, 12.0);
       
        print("original", image);
        
        Image2D resampled = Image2D.createType(Float.class, 7, 4);
        resampled.resampleFrom(image);
        
        print("resampled", resampled);
        
        Image2D backsampled = image.copy(false);
        backsampled.resampleFrom(resampled);
        
        print("backsampled", backsampled);
        
        Gaussian2D psf = new Gaussian2D(1.0);
        Referenced2D beam = psf.getBeam(new FlatGrid2D());
        
        print("smoothing beam", beam);
        
        image.smooth(beam);
        
        print("smoothed", image);
        
        Image2D cleaned = image.clean(beam, 0.1, 0.01);
        
        print("cleaned", cleaned);
        
        Viewport2D view = new Viewport2D(image, 1, 1, 3, 3);
        print("view", view);
        
        //image.setSize(150,  150);
        
        image.fill(2.0);
        print("filled", image);
        
        image.validate();
        print("validated", image);
        
        image.getExecutor().shutdown();
        
        System.err.println("Done.");
        
    }
    
    
    public static void print(String label, Values2D values) {
        System.out.println(label + " " + values.sizeX() + "x" + values.sizeY() + ":");
        
        for(int j=0; j<values.sizeY(); j++) {
            for(int i=0; i<values.sizeX(); i++) System.out.print(Util.S3.format(values.get(i, j)) + "\t");
            System.out.println();
        }
        
        System.out.println();
        
    }
}
