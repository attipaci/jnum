package test;

import jnum.Symbol;
import jnum.text.AngleFormat;

public class AngleFormatTest {

   static String[] testCases = new String[] {
            "-155.5", 
            "134.2, ",
            "130:11:43.4",
            "130d11m43.4s",
            "130" + Symbol.degree + "11'43.4\"",
            "130 11 43.4",
            "130d  11    43.4",
            "  130: 11",
            "11m43.4s",
            "  134.2"
            
    };
    
    public static void main(String[] args) {
        for(String input : testCases) {
            try { parse(input); }
            catch(Exception e) { e.printStackTrace(); }
        }
        
    }
    
 
    public static void parse(String input) throws Exception {
        double angle = new AngleFormat().parse(input).doubleValue();
        System.out.println(input + " --> " + new AngleFormat(1).format(angle));
    }
    
}
