package jnum.data.cube2;

import jnum.data.image.Index2D;

public class Index2D1 extends Index2D {
    /**
     * 
     */
    private static final long serialVersionUID = -4733216886398716571L;
    int k;
    

    @Override
    public Index2D1 copy() { return (Index2D1) super.copy(); } 
    

    @Override
    public int hashCode() {
        return super.hashCode() ^ k;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        if(!(o instanceof Index2D1)) return false;
        
        Index2D1 index = (Index2D1) o;
        if(index.k != k) return false;
       
        return super.equals(o);
    }
    
    public int k() { return k; }
    
    public void setK(int value) { k = value; }

    @Override
    public String toString() {
        return super.toString() + "," + k;
    }
}
