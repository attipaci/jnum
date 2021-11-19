package jnum.data;

public enum ComponentType {
    UNKNOWN(0, "Unknown"),
    SIGNAL(1, "Signal"),
    WEIGHT(2, "Weight"),
    EXPOSURE(3, "Exposure"),
    NOISE(4, "Noise"),
    VARIANCE(5, "Variance"),
    S2N(6, "S/N");
    
    private int bitMask;
    private String description;
    
    ComponentType(int bit, String desc) {
        this.bitMask = 1 << bit;
        this.description = desc;
    }
    
    public int mask() {
        return bitMask;
    }
    
    public String description() {
        return description;
    }

    public static ComponentType guessType(String text) {
        text = text.toLowerCase();     

        if(text.contains("weight")) return WEIGHT;
      
        // "Signal-to-noise" and variants...
        if(text.contains("to-noise")) return S2N;
        if(text.contains("to noise")) return S2N;
        if(text.contains("/noise")) return S2N;
        if(text.contains("/ noise")) return S2N;
       
        if(text.contains("noise")) return NOISE;               // noise weight -> weight
        if(text.contains("rms")) return NOISE;
        if(text.contains("error")) return NOISE;
        if(text.contains("uncertainty")) return NOISE;
        if(text.contains("sensitivity")) return NOISE;
        if(text.contains("depth")) return NOISE;
        if(text.contains("scatter")) return NOISE;
        if(text.contains("sigma")) return NOISE;

        if(text.contains("variance")) return VARIANCE;
        if(text.equals("var")) return VARIANCE;
        
        if(text.contains("s/n")) return S2N;
        if(text.contains("s2n")) return S2N;
        
        if(text.contains("coverage")) return EXPOSURE;         // depth coverage -> noise
        if(text.contains("time")) return EXPOSURE;
        if(text.contains("exposure")) return EXPOSURE;

        if(text.contains("signal")) return SIGNAL;
        if(text.contains("flux")) return SIGNAL;
        if(text.contains("intensity")) return SIGNAL;
        if(text.contains("brightness")) return SIGNAL; 

        return UNKNOWN;
    }
    
}
