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
		header.addValue("BOOL1", true, "no. 1.");
		header.addValue("FLOAT2", 1.0, "no. 2.");
		header.addValue("STRING3", "hello", "no. 3.");
		header.addValue("INT4", 4, "no. 4.");
		
		header.addValue("BOOL5", false, "no. 5.");
		header.addValue("FLOAT6", 2.0, "no. 6.");
		header.addValue("STRING7", "hello", "no. 7.");
		header.addValue("INT8", 6, "no. 8.");
	
		header.addValue("FLOAT2", 2.0, "no. 2. (override)");
		
		header.addValue("BOOL9", true, "no. 9.");
		header.addValue("FLOAT10", 3.0, "no. 10.");
		header.addValue("STRING11", "hello", "no. 11.");
		header.addValue("INT12", 8, "no. 12.");
		
		header.insertComment("Block4");
		
		header.addValue("BOOL13", false, "no. 13.");
		header.addValue("FLOAT14", 4.0, "no. 14.");
		header.addValue("STRING15", "hello", "no. 15.");
		header.addValue("INT16", 10, "no. 16.");

		
		Fits fits = new Fits();
		fits.addHDU(hdu);
		
		BufferedDataOutputStream file = new BufferedDataOutputStream(new FileOutputStream("test.fits"));
		
		fits.write(file);		
	}
	
}
