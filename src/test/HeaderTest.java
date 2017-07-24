package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.BinaryTableHDU;
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
	public void addKeys() throws FitsException, FileNotFoundException {
		float[][] data = new float[10][10];
		
		BasicHDU hdu = (BasicHDU) Fits.makeHDU(data);
		
		Header header = hdu.getHeader();
		c.add(new HeaderCard"BOOL1", true, "no. 1.");
		c.add(new HeaderCard"FLOAT2", 1.0, "no. 2.");
		c.add(new HeaderCard"STRING3", "hello", "no. 3.");
		c.add(new HeaderCard"INT4", 4, "no. 4.");
		
		c.add(new HeaderCard"BOOL5", false, "no. 5.");
		c.add(new HeaderCard"FLOAT6", 2.0, "no. 6.");
		c.add(new HeaderCard"STRING7", "hello", "no. 7.");
		c.add(new HeaderCard"INT8", 6, "no. 8.");
	
		c.add(new HeaderCard"FLOAT2", 2.0, "no. 2. (override)");
		
		c.add(new HeaderCard"BOOL9", true, "no. 9.");
		c.add(new HeaderCard"FLOAT10", 3.0, "no. 10.");
		c.add(new HeaderCard"STRING11", "hello", "no. 11.");
		c.add(new HeaderCard"INT12", 8, "no. 12.");
		
		header.insertComment("Block4");
		
		c.add(new HeaderCard"BOOL13", false, "no. 13.");
		c.add(new HeaderCard"FLOAT14", 4.0, "no. 14.");
		c.add(new HeaderCard"STRING15", "hello", "no. 15.");
		c.add(new HeaderCard"INT16", 10, "no. 16.");

		
		Fits fits = new Fits();
		fits.addHDU(hdu);
		
		BufferedDataOutputStream file = new BufferedDataOutputStream(new FileOutputStream("test.fits"));
		
		fits.write(file);		
	}
	
}
