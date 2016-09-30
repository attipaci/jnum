package test;

import jnum.text.TextWrapper;

public class WrappingTest {

    public static void main(String[] args) {
        TextWrapper wrapper = new TextWrapper(52);
        wrapper.setJustified(true);
        
        String text = "blah-blah-blah blah\n     blahblah blah-blahblah blah blah -blah-blahbla-blah" +
        "blahblah blah-blahblah blah blah blah-blahbla-blahblah -blah -blah-blahblah -blah -blah -blah-blahbla-blah" +
        "blahblah blah-blahblah blah blah blah-blahbla-blahblah blah blah-blahblah blah blah blah-blahbla-blah";        
        
        System.out.println(wrapper.wrap(text, "# TEST> ", 2));   
    }
    
}
