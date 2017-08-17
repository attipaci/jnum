package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.util.BufferedDataOutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class HeaderTest.
 */
public class HeaderTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			HeaderTest test = new HeaderTest();
			test.addKeys();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Adds the keys.
	 *
	 * @throws FitsException the fits exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public void addKeys() throws IOException, FitsException, FileNotFoundException {
		float[][] data = new float[10][10];
		
		BasicHDU<?> hdu = (BasicHDU<?>) Fits.makeHDU(data);
		
		Header h = hdu.getHeader();
		
		h.addValue("BOOL1", true, "no. 1.");
		h.addValue("FLOAT2", 1.0, "no. 2.");
		h.addValue("STRING3", "hello", "no. 3.");
		h.addValue("INT4", 4, "no. 4.");
		
		h.addValue("BOOL5", false, "no. 5.");
		h.addValue("FLOAT6", 2.0, "no. 6.");
		h.addValue("STRING7", "hello", "no. 7.");
		h.addValue("INT8", 6, "no. 8.");
	
		h.addValue("FLOAT2", 2.0, "no. 2. (override)");
		
		h.addValue("BOOL9", true, "no. 9.");
		h.addValue("FLOAT10", 3.0, "no. 10.");
		h.addValue("STRING11", "hello", "no. 11.");
		h.addValue("INT12", 8, "no. 12.");
		
		h.insertComment("Block4");
		
		h.addValue("BOOL13", false, "no. 13.");
		h.addValue("FLOAT14", 4.0, "no. 14.");
		h.addValue("STRING15", "hello", "no. 15.");
		h.addValue("INT16", 10, "no. 16.");

		
		Fits fits = new Fits();
		fits.addHDU(hdu);
			
		fits.write(new BufferedDataOutputStream(new FileOutputStream("test.fits")));

		fits.close();
	}
	
}
