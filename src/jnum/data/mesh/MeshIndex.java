package jnum.data.mesh;

// TODO: Auto-generated Javadoc
/**
 * The Class MeshIndex.
 */
public class MeshIndex {
    
    /** The value. */
    private int value;
    
    /** The size. */
    private int size;
    
    /** The child. */
    private MeshIndex child;
    
    /** The parent. */
    private MeshIndex parent;
    

    /**
     * Instantiates a new mesh index.
     *
     * @param index the index
     * @param size the size
     */
    public MeshIndex(int[] index, int[] size) {
        this(index, size, 0);
    }
    
    /**
     * Instantiates a new mesh index.
     *
     * @param index the index
     * @param size the size
     * @param depth the depth
     */
    private MeshIndex(int[] index, int[] size, int depth) {
        value = index[depth];
        this.size = size[depth];
        if(depth < index.length) {
            child = new MeshIndex(index, size, depth+1);
            child.parent = this;
        }
    }
    
    /**
     * Sets the index.
     *
     * @param index the new index
     */
    public final void setIndex(int[] index) { setIndex(index, 0); }
    
    /**
     * Sets the index.
     *
     * @param index the index
     * @param depth the depth
     */
    private void setIndex(int[] index, int depth) {
        value = index[depth];
        if(child != null) child.value = index[depth];
    }
    
    /**
     * Gets the bottom index.
     *
     * @return the bottom index
     */
    public MeshIndex getBottomIndex() {
        return child == null ? this : child.getBottomIndex();
    }
    
    /**
     * Size.
     *
     * @return the int
     */
    public int size() {
        return child == null ? 1 : 1 + child.size();
    }
    
    /**
     * Checks for next.
     *
     * @return true, if successful
     */
    public boolean hasNext() {
        if(hasNextLocal()) return true;
        if(child != null) return child.hasNextLocal();
        return false;
    }
    
    /**
     * Next.
     */
    public void next() {
        if(child != null) child.next();
        else incrementLocal();
    }
   
    /**
     * Checks for next local.
     *
     * @return true, if successful
     */
    private final boolean hasNextLocal() {
        return (value+1) < size;
    }
    
    /**
     * Increment local.
     */
    private final void incrementLocal() {
        if(value++ < size) return;
        value = 0;
        if(parent != null) parent.incrementLocal();
    }
}
