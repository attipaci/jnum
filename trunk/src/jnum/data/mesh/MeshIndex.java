package jnum.data.mesh;

public class MeshIndex {
    private int value;
    private int size;
    private MeshIndex child;
    private MeshIndex parent;
    

    public MeshIndex(int[] index, int[] size) {
        this(index, size, 0);
    }
    
    private MeshIndex(int[] index, int[] size, int depth) {
        value = index[depth];
        this.size = size[depth];
        if(depth < index.length) {
            child = new MeshIndex(index, size, depth+1);
            child.parent = this;
        }
    }
    
    public final void setIndex(int[] index) { setIndex(index, 0); }
    
    private void setIndex(int[] index, int depth) {
        value = index[depth];
        if(child != null) child.value = index[depth];
    }
    
    public MeshIndex getBottomIndex() {
        return child == null ? this : child.getBottomIndex();
    }
    
    public int size() {
        return child == null ? 1 : 1 + child.size();
    }
    
    public boolean hasNext() {
        if(hasNextLocal()) return true;
        if(child != null) return child.hasNextLocal();
        return false;
    }
    
    public void next() {
        if(child != null) child.next();
        else incrementLocal();
    }
   
    private final boolean hasNextLocal() {
        return (value+1) < size;
    }
    
    private final void incrementLocal() {
        if(value++ < size) return;
        value = 0;
        if(parent != null) parent.incrementLocal();
    }
}
