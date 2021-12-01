package test;

import jnum.data.ComplexView;
import jnum.data.image.Image2D;
import jnum.data.index.Index2D;
import jnum.util.ArrayUtil;

public class DataFFTTest {

        public static void main(String[] args) {
            
            Image2D i = Image2D.createType(Double.class, 4, 3);
            i.set(0, 0, 1.0);
            System.out.println(ArrayUtil.toString(i.getCore()));
            
            ComplexView<Index2D> s = i.getForwardFFT();

            System.out.println(ArrayUtil.toString(s.getData().getCore()));
            
            Image2D i2 = (Image2D) s.getBackFFT(i.getSize(), false);
            System.out.println(ArrayUtil.toString(i2.getCore()));
        }
    
}
