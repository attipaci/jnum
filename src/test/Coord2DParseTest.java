package test;

import jnum.astro.EquatorialCoordinates;
import jnum.math.SphericalCoordinates;

public class Coord2DParseTest {

    

    static String[] testCases = new String[] {
             "-155.5, 34.2", 
             "-155.5,  34.2 ",
             "130:11:43.4 -43:33:12.2",
             "130d11m43.4s, -43:33:12.2",
             "( 130d11m43.4s, -43:33:12.2)"
     };
    
    static String[] eqTestCases = new String[] {
            "-155.5, 34.2",
            "12:44:38.2 -36:52.48.4",
            "12:44:38.2 -36:52.48.4 (J2000.0)",
            "12:44:38.2 -36:52.48.4 J2000.0",
            "12:44:38.2 -36:52.48.4 1950.0",
            "EQ 12:44:38.2 -36:52.48.4",
            "eq 12:44:38.2 -36:52.48.4",
            "ga 122:44:38.2 -36:52.48.4",
            "sg 122:44:38.2 -36:52.48.4",
            "ec 122:44:38.2 -36:52.48.4",
            "ec 122:44:38.2 -36:52.48.4 (B1950.0)",
            "12h44m38.2s -36d52m48.4s"
    };
     
     public static void main(String[] args) {
         for(String input : testCases) {
             try { parse(input); }
             catch(Exception e) { e.printStackTrace(); }
         }
         
         System.out.println();
         
         for(String input : eqTestCases) {
             try { parseEq(input); }
             catch(Exception e) { e.printStackTrace(); }
         }
         
     }
     
  
     public static void parse(String input) throws Exception {
         SphericalCoordinates coords = new SphericalCoordinates();
         coords.parse(input);
         System.out.println(input + " --> " + coords.toString(1));
     }
     
     

     public static void parseEq(String input) throws Exception {
         EquatorialCoordinates coords = new EquatorialCoordinates(input);
         System.out.println(input + " --> " + coords.toString(1));
     }
     
    
}
