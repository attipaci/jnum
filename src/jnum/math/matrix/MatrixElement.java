package jnum.math.matrix;

import jnum.data.image.Index2D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.math.Normalizable;

public abstract class MatrixElement<T> implements LinearAlgebra<T>, AbstractAlgebra<T>, Metric<T>, AbsoluteValue , Normalizable {

        public abstract MatrixElement<T> fresh();
    
        public abstract T value();
        
        public abstract T copyOfValue();
        
        public abstract void applyTo(int i, int j);
        
        public abstract MatrixElement<T> from(T value);
        
        public abstract MatrixElement<T> from(int i, int j);
        
        public final MatrixElement<T> from(Index2D index) {
            from(index.i(), index.j());
            return this;
        }
          
        public abstract MatrixElement<T> copy(int i, int j);
        
        public abstract MatrixElement<T> copy(T value);
        
        public final MatrixElement<T> copy(Index2D index) {
            copy(index.i(), index.j());
            return this;
        }
        
    }