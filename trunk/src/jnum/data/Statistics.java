/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     jnum is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     jnum is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with jnum.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public final class Statistics {
    
  
    public static double median(double[] data) {
        return median(data, 0, data.length);
    }
    
    public static double median(double[] data, int fromIndex, int toIndex) {
        return Inplace.median(getSorter(data, fromIndex, toIndex));
    }
    
    public static float median(float[] data) {
        return median(data, 0, data.length);
    }
    
    public static float median(float[] data, int fromIndex, int toIndex) {
        float[] sorter = new float[toIndex - fromIndex];
        System.arraycopy(data, fromIndex, sorter, 0, sorter.length);
        return Inplace.median(sorter);
    }
    
    public double median(List<? extends Number> data, int fromIndex, int toIndex) {
        double[] values = new double[toIndex - fromIndex];
        int i=values.length;
        for(Number x : data) if(x != null) if(!Double.isNaN(x.doubleValue())) values[--i] = x.doubleValue();
        return Inplace.median(values, i, values.length);
    }
    
    public double median(Collection<? extends Number> data) {
        double[] values = new double[data.size()];
        int i=values.length;
        for(Number x : data) if(x != null) values[--i] = x.doubleValue();
        return Inplace.median(values, i, values.length);
    }
    
    public static WeightedPoint median(WeightedPoint[] data) {
        return median(data, 0, data.length);
    }
    
    public static WeightedPoint median(WeightedPoint[] data, int fromIndex, int toIndex) {
        return Inplace.median(getSorter(data, fromIndex, toIndex));
    }
    
    

    public static WeightedPoint median(double[] data, double[] weight) {
        return median(data, weight, 0, data.length);
    }
    
    public static WeightedPoint median(double[] data, double[] weight, int fromIndex, int toIndex) {
        return Inplace.median(getSorter(data, weight, fromIndex, toIndex));
    }
    
    
    
    public static WeightedPoint median(float[] data, float[] weight) {
        return median(data, weight, 0, data.length);
    }
    
    public static WeightedPoint median(float[] data, float[] weight, int fromIndex, int toIndex) {
        return Inplace.median(getSorter(data, weight, fromIndex, toIndex));
    }

   
    public static float mean(float[] data) {
        return mean(data, 0, data.length);
    }
    
    public static float mean(float[] data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n=0;
        while(--toIndex >= fromIndex) if(!Float.isNaN(data[toIndex])) {
            sum += data[toIndex];
            n++;
        }
        return (float) (sum / n);
    }
    
    
    public static double mean(double[] data) {
        return mean(data, 0, data.length);
    }
    
    public static double mean(double[] data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n=0;
        while(--toIndex >= fromIndex) if(!Double.isNaN(data[toIndex])) {
            sum += data[toIndex];
            n++;
        }
        return sum / n;
    }
    
    
    public static double mean(Iterable<? extends Number> data) {
        double sum = 0.0;
        int n=0;
        for(final Number x : data) if(x != null) if(!Double.isNaN(x.doubleValue())) {
            sum += x.doubleValue();
            n++;
        }
        return sum/n;
    }
    
    public static double mean(List<? extends Number> data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n=0;
        while(--toIndex >= fromIndex) {
            final Number x = data.get(toIndex);
            
            if(x == null) continue;
            if(Double.isNaN(x.doubleValue())) continue;
     
            sum += x.doubleValue();
            n++;
        }
        return sum / n;
    }
    
    
    public static WeightedPoint mean(WeightedPoint[] data) {
        return mean(data, 0, data.length);
    }
    
    public static WeightedPoint mean(WeightedPoint[] data, int fromIndex, int toIndex) {
        WeightedPoint result = new WeightedPoint();
        calcMean(data, fromIndex, toIndex, result);
        return result;
    }
  
    
    public static WeightedPoint weightedMean(List<? extends WeightedPoint> data, int fromIndex, int toIndex) {
        WeightedPoint result = new WeightedPoint();
        calcMean(data, fromIndex, toIndex, result);
        return result;
    }
   
    public static WeightedPoint weightedMean(Iterable<? extends WeightedPoint> data) {
        WeightedPoint result = new WeightedPoint();
        calcMean(data, result);
        return result;
    }
    
    
    public static void calcMean(WeightedPoint[] data, WeightedPoint result) {
        calcMean(data, 0, data.length, result);
    }

    public static void calcMean(WeightedPoint[] data, int fromIndex, int toIndex, WeightedPoint result) {
        result.noData();
        while(--toIndex >= fromIndex) {
            final WeightedPoint p = data[toIndex];
            if(p == null) continue;
            if(p.isNaN()) continue;

            result.add(p.weight() * p.value());
            result.addWeight(p.weight());
        }
        result.scaleValue(1.0 / result.weight());
    }
    
    public static void calcMean(Iterable<? extends WeightedPoint> data, WeightedPoint result) {
        result.noData();
        for(final WeightedPoint x : data) if(x != null) if(!x.isNaN()) {
            result.add(x.weight() * x.value());
            result.addWeight(x.weight());
        }
        result.scaleValue(1.0 / result.weight());
    }
    
    public static void calcMean(List<? extends WeightedPoint> data, int fromIndex, int toIndex, WeightedPoint result) {
        result.noData();
        while(--toIndex >= fromIndex) {
            final WeightedPoint x = data.get(toIndex);
            if(x == null) continue;
            if(x.isNaN()) continue;
       
            result.add(x.weight() * x.value());
            result.addWeight(x.weight());
        }
        result.scaleValue(1.0 / result.weight());
    }
    
    
    
    public static WeightedPoint mean(double[] data, double[] weight) {
        return mean(data, weight, 0, data.length);
    }
    
    public static WeightedPoint mean(double[] data, double[] weight, int fromIndex, int toIndex) {
        WeightedPoint result = new WeightedPoint();
        calcMean(data, weight, fromIndex, toIndex, result);
        return result;
    }
    
    public static void calcMean(double[] data, double[] weight, WeightedPoint result) {
        calcMean(data, weight, 0, data.length, result);
    }
    
    public static void calcMean(double[] data, double[] weight, int fromIndex, int toIndex, WeightedPoint result) {
        result.noData();
        
        while(--toIndex >= fromIndex) if(!Double.isNaN(data[toIndex])) {
            result.add(weight[toIndex] * data[toIndex]);
            result.addWeight(weight[toIndex]);
        }
        
        result.scaleValue(1.0 / result.weight());
    }
    
   
    
    public static WeightedPoint mean(float[] data, float[] weight) {
        return mean(data, weight, 0, data.length);
    }
    
    public static WeightedPoint mean(float[] data, float[] weight, int fromIndex, int toIndex) {
        WeightedPoint result = new WeightedPoint();
        calcMean(data, weight, fromIndex, toIndex, result);
        return result;
    }
    
    public static void calcMean(float[] data, float[] weight, WeightedPoint result) {
        calcMean(data, weight, 0, data.length, result);
    }
    
    public static void calcMean(float[] data, float[] weight, int fromIndex, int toIndex, WeightedPoint result) {
        result.noData();
        
        while(--toIndex >= fromIndex) if(!Float.isNaN(data[toIndex])) {
            result.add(weight[toIndex] * data[toIndex]);
            result.addWeight(weight[toIndex]);
        }
        
        result.scaleValue(1.0 / result.weight());
    }
    
    
    public static float rms(float[] data) {
        return rms(data, 0, data.length);
    }
    
    public static float rms(float[] data, int fromIndex, int toIndex) {
        return (float) Math.sqrt(variance(data, fromIndex, toIndex));
    }
    
    
    public static float variance(float[] data) {
        return variance(data, 0, data.length);
    }
    
    public static float variance(float[] data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n = 0;
        
        while(--toIndex >= fromIndex) {
            final float x = data[toIndex];
            if(Float.isNaN(x)) continue;
            sum += x * x;
            n++;
        }
        
        return (float) (sum / n);
    }
    
    
    
    public static double rms(double[] data) {
        return rms(data, 0, data.length);
    }
    
    public static double rms(double[] data, int fromIndex, int toIndex) {
        return Math.sqrt(variance(data, fromIndex, toIndex));
    }
    
    
    
    public static double variance(double[] data) {
        return variance(data, 0, data.length);
    }
    
    public static double variance(double[] data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n = 0;
        
        while(--toIndex >= fromIndex) {
            final double x = data[toIndex];
            if(Double.isNaN(x)) continue;    
            sum += x * x;
            n++;
        }
        
        return sum / n;
    }
    
    public static double rms(List<? extends Number> data, int fromIndex, int toIndex) {
        return Math.sqrt(variance(data, fromIndex, toIndex));
    }
    
    public static double variance(List<? extends Number> data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n = 0;
        
        while(--toIndex >= fromIndex) { 
            final Number x = data.get(toIndex);
            if(x == null) continue;
            if(Double.isNaN(x.doubleValue())) continue;         
            sum += x.doubleValue() * x.doubleValue();
            n++;
        }
        
        return sum / n;
    }
    
    public static double rms(Iterable<? extends Number> data) {
        return Math.sqrt(variance(data));
    }
    
    public static double variance(Iterable<? extends Number> data) {
        double sum = 0.0;
        int n = 0;
        
        for(final Number value : data) if(value != null) {
            final double x = value.doubleValue();
            if(Double.isNaN(x)) continue;
            sum += x * x;
            n++;
        }
        
        return sum / n;
    }
    
    
    public static double reducedChiSquared(WeightedPoint[] data, int fromIndex, int toIndex) {
        double sum = 0.0;
        int n = 0;
        
        while(--toIndex >= fromIndex) {
            final WeightedPoint p = data[toIndex];
            if(p == null) continue;
            if(p.isNaN()) continue;
        
            double dev = DataPoint.significanceOf(p);
            sum += dev * dev;
            n++;
        }
        
        return sum / n;
    }
    
    public static float select(float[] data, double fraction) {
        return select(data, fraction, 0, data.length);
    }
    
    public static float select(float[] data, double fraction, int fromIndex, int toIndex) {
        return Inplace.select(getSorter(data, fromIndex, toIndex), fraction);
    }
    
    public static double select(double[] data, double fraction) {
        return select(data, fraction, 0, data.length);
    }
   
    public static double select(double[] data, double fraction, int fromIndex, int toIndex) {
        return Inplace.select(getSorter(data, fromIndex, toIndex), fraction);
    }
    
    public static <T extends Comparable<? super T>> T select(List<T> data, double fraction) {
        return select(data, fraction, 0, data.size());
    }
    
    public static <T extends Comparable<? super T>> T select(List<T> data, double fraction, int fromIndex, int toIndex) {
        ArrayList<T> sorter = new ArrayList<T>(toIndex - fromIndex);
        while(fromIndex < toIndex) sorter.add(data.get(fromIndex++));
        return Inplace.select(sorter, fraction);
    }
    
    public static <T> T select(List<T> data, double fraction, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        ArrayList<T> sorter = new ArrayList<T>(toIndex - fromIndex);
        while(fromIndex < toIndex) sorter.add(data.get(fromIndex++));
        return Inplace.select(sorter, fraction, comparator);
    }
    
    
    private static float[] getSorter(float[] data, int fromIndex, int toIndex) {
        float[] sorter = new float[toIndex - fromIndex];
        System.arraycopy(data, fromIndex, sorter, 0, sorter.length);
        return sorter;
    }
    
    private static double[] getSorter(double[] data, int fromIndex, int toIndex) {
        double[] sorter = new double[toIndex - fromIndex];
        System.arraycopy(data, fromIndex, sorter, 0, sorter.length);
        return sorter;
    }
    
    private static WeightedPoint[] getSorter(WeightedPoint[] data, int fromIndex, int toIndex) {
        WeightedPoint[] sorter = new WeightedPoint[toIndex - fromIndex];
        System.arraycopy(data, fromIndex, sorter, 0, sorter.length);
        return sorter;
    }
    
    private static WeightedPoint[] getSorter(float[] data, float[] weight, int fromIndex, int toIndex) {
        WeightedPoint[] sorter = new WeightedPoint[toIndex - fromIndex];
        for(int to=sorter.length, from=toIndex; --toIndex >= fromIndex; ) sorter[--to] = new WeightedPoint(data[from], weight[from]);
        return sorter;
    }
    
    private static WeightedPoint[] getSorter(double[] data, double[] weight, int fromIndex, int toIndex) {
        WeightedPoint[] sorter = new WeightedPoint[toIndex - fromIndex];
        for(int to=sorter.length, from=toIndex; --toIndex >= fromIndex; ) sorter[--to] = new WeightedPoint(data[from], weight[from]);
        return sorter;
    }
   
    
 
 

    public static final class Inplace {


        public static double median(final double[] data) { return median(data, 0, data.length); }


        public static double median(final double[] data, final int fromIndex, int toIndex) {
            Arrays.sort(data, fromIndex, toIndex);
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --toIndex >= fromIndex; ) if(!Double.isNaN(data[toIndex])) break;
            toIndex++;
            
            int n = toIndex - fromIndex;
            return n % 2 == 0 ? 0.5 * (data[fromIndex + (n>>>1)-1] + data[fromIndex + (n>>>1)]) : data[fromIndex + ((n-1)>>>1)];
        }
        
        
        public static float median(final float[] data) { return median(data, 0, data.length); }


        public static float median(final float[] data, final int fromIndex, int toIndex) {
            Arrays.sort(data, fromIndex, toIndex);
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --toIndex >= fromIndex; ) if(!Float.isNaN(data[toIndex])) break;
            toIndex++;
            
            int n = toIndex - fromIndex;
            return n % 2 == 0 ? 0.5F * (data[fromIndex + (n>>>1)-1] + data[fromIndex + (n>>>1)]) : data[fromIndex + ((n-1)>>>1)];
        }
        
        public static <T extends Number> double median(List<T> data) {
            Comparator<T> c = new Comparator<T>() {

                @Override
                public int compare(T a, T b) {
                    return Double.compare(a.doubleValue(), b.doubleValue());
                }
                
            };
            
            Collections.sort(data, c);
            int n = data.size();
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --n >= 0; ) if(!Double.isNaN(data.get(n).doubleValue())) break;
            n++;
            
            return data.size() % 2 == 0 ? 0.5 * (data.get((n>>>1) - 1).doubleValue() + data.get(n>>>1).doubleValue()) : data.get((n-1)>>>1).doubleValue();
        }


        public static WeightedPoint median(final WeightedPoint[] data) { return median(data, 0, data.length); }
        

        public static void median(final WeightedPoint[] data, final WeightedPoint result) { median(data, 0, data.length, result); }


        public static WeightedPoint median(final WeightedPoint[] data, final int fromIndex, final int toIndex) {
            return smartMedian(data, fromIndex, toIndex, 1.0);      
        }
        

        public static void median(final WeightedPoint[] data, final int fromIndex, final int toIndex, final WeightedPoint result) {
            smartMedian(data, fromIndex, toIndex, 1.0, result);     
        }
        
      

        public static WeightedPoint smartMedian(final WeightedPoint[] data, final int fromIndex, final int toIndex, final double maxDependence) {
            final WeightedPoint result = new WeightedPoint();
            smartMedian(data, fromIndex, toIndex, maxDependence, result);
            return result;      
        }


        public static void smartMedian(final WeightedPoint[] data, final int from, int to, final double maxDependence, WeightedPoint result) {
            // If no data, then 
            if(to == from) {
                result.noData();
                return;
            }
            
            if(to - from == 1) {
                result.copy(data[from]);
                return;
            }
        
            Arrays.sort(data, from, to);
        
            // wt is the sum of all weights
            // wi is the integral sum including the current point.
            double wt = 0.0, wmax = 0.0;
            
            for(int i=to; --i >= from; ) {
                if(data[i].isNaN()) continue;
                final double w = data[i].weight();
              
                wt += w;
                if(w > wmax) wmax = w;
            }
        
            // If a single datum dominates, then return the weighted mean...
            if(wmax >= maxDependence * wt) {
                double sum=0.0, sumw=0.0;
                for(int i = to; --i >= from; ) {
                    if(data[i].isNaN()) continue;
                    final double w = data[i].weight();
                   
                    sum += w * data[i].value();
                    sumw += w;
                }
                result.setValue(sum/sumw);
                result.setWeight(sumw);
                return;
            }
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --to >= from; ) if(!data[to].isNaN()) break;
            to++;
          
            // If all weights are zero return the arithmetic median...
            // This should never happen, but just in case...
            if(wt == 0.0) {
                final int n = to - from;
                result.setValue(n % 2 == 0 ? 
                        0.5F * (data[from + n/2-1].value() + data[from + n/2].value()) 
                        : data[from + (n-1)/2].value());
                result.setWeight(0.0);
                return;
            }
        
        
            final double midw = 0.5 * wt; 
            int ig = from; 
            
            WeightedPoint last = WeightedPoint.NaN;
            WeightedPoint point = data[from];
        
            double wi = point.weight();
            
            while(wi < midw) if(data[++ig].weight() > 0.0) {
                last = point;
                point = data[ig];       
                wi += 0.5 * (last.weight() + point.weight());    
            }
            
            final double wplus = wi;
            final double wminus = wi - 0.5 * (last.weight() + point.weight());
            
            final double w1 = (wplus - midw) / (wplus + wminus);
            result.setValue(w1 * last.value() + (1.0-w1) * point.value());
            result.setWeight(wt);
        }

        public static double select(double[] data, double fraction) {
            return select(data, fraction, 0, data.length);
        }
        
        public static double select(double[] data, double fraction, int fromIndex, int toIndex) {
            Arrays.sort(data, fromIndex, toIndex);
            while(Double.isNaN(data[toIndex - 1]) && toIndex > fromIndex) toIndex--;
            return data[fromIndex + (int)Math.round(fraction * (toIndex - fromIndex - 1))];
        }

        public static float select(float[] data, double fraction) {
            return select(data, fraction, 0, data.length);
        }
        
        public static float select(float[] data, double fraction, int fromIndex, int toIndex) {
            Arrays.sort(data, fromIndex, toIndex);
            while(Float.isNaN(data[toIndex - 1]) && toIndex > fromIndex) toIndex--;
            return data[fromIndex + (int)Math.floor(fraction * (toIndex - fromIndex - 1))];
        }
        
        
        public static <T extends Comparable<? super T>> T select(T[] data, double fraction) {
            return select(data, fraction, 0, data.length);
        }
        
        public static <T extends Comparable<? super T>> T select(T[] data, double fraction, int fromIndex, int toIndex) {
            Arrays.sort(data, fromIndex, toIndex);
            return data[fromIndex + (int)Math.floor(fraction * (toIndex - fromIndex - 1))];
        }
        
        public static <T> T select(T[] data, double fraction, Comparator<? super T> comparator) {
            return select(data, fraction, 0, data.length, comparator);
        }
        
        public static <T> T select(T[] data, double fraction, int fromIndex, int toIndex, Comparator<? super T> comparator) {
            Arrays.sort(data, fromIndex, toIndex, comparator);
            return data[fromIndex + (int)Math.floor(fraction * (toIndex - fromIndex - 1))];
        }
        
        public static <T extends Comparable<? super T>> T select(List<T> data, double fraction) {
            Collections.sort(data);
            return data.get((int) Math.round(fraction * data.size()));
        }

        public static <T> T select(List<T> data, double fraction, Comparator<? super T> comparator) {
            Collections.sort(data, comparator);
            return data.get((int) Math.round(fraction * data.size()));
        }
        
        public static float robustMean(final float[] data, final double tails) {
            return robustMean(data, 0, data.length, tails);
        }
        

        public static float robustMean(final float[] data, int from, int to, final double tails) {
            Arrays.sort(data, from, to);
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --to >= from; ) if(!Float.isNaN(data[to])) break;
            to++;

            // Ignore the tails on both sides of the distribution...
            final int dn = (int) Math.round(tails * (to - from));
        
            to -= dn;
            from += dn;
            if(from >= to) return Float.NaN;

            // Average over the middle section of values...
            double sum = 0.0;
            for(int i = to; --i >= from; ) sum += data[i];
            return (float) (sum / (to - from));
        }
        

        public static double robustMean(final double[] data, final double tails) {
            return robustMean(data, 0, data.length, tails);
        }
        

        public static double robustMean(final double[] data, int from, int to, final double tails) {
            Arrays.sort(data, from, to);
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --to >= from; ) if(!Double.isNaN(data[to])) break;
            to++;
            
            // Ignore the tails on both sides of the distribution...
            final int dn = (int) Math.round(tails * (to - from));
        
            to -= dn;
            from += dn;
            if(from >= to) return Double.NaN;

            // Average over the middle section of values...
            double sum = 0.0;
            for(int i = to; --i >= from; ) sum += data[i];
            return sum / (to - from);
        }
        
 
        public static WeightedPoint robustMean(final WeightedPoint[] data, final double tails) {
            return robustMean(data, 0, data.length, tails);
        }
        

        public static void robustMean(final WeightedPoint[] data, final double tails, final WeightedPoint result) {
            robustMean(data, 0, data.length, tails, result);
        }
        

        public static WeightedPoint robustMean(final WeightedPoint[] data, int from, int to, final double tails) {
            WeightedPoint result = new WeightedPoint();
            robustMean(data, from, to, tails, result);
            return result;
        }
        
 
        public static void robustMean(final WeightedPoint[] data, int from, int to, final double tails, final WeightedPoint result) {
            if(from >= to) {
                result.noData();
                return;
            }
            
            if(to-from == 1) {
                result.copy(data[from]);
                return;
            }
            
            Arrays.sort(data, from, to);
            
            // NaN values go to the end. Skip these when calculating the median
            for(; --to >= from; ) if(!data[to].isNaN()) break;
            to++;
            
            // Ignore the tails on both sides of the distribution...
            final int dn = (int) Math.round(tails * (to - from));
        
            to -= dn;
            from += dn;
            if(from >= to) {
                result.noData();
                return;
            }

            // Average over the middle section of values...
            double sum = 0.0, sumw = 0.0;
            while(--to >= from) {
                final WeightedPoint point = data[to];
                if(point.isNaN()) continue; 
                sum += point.weight() * point.value();
                sumw += point.weight();
            }
            result.setValue(sum / sumw);
            result.setWeight(sumw);
        }

        
        
    }
    
    // median(x^2) = 0.454937 * sigma^2 
    public static final double medianNormalizedVariance = 0.454937;


    
}