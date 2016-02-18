/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.parallel;

import jnum.Parallel;
import jnum.data.WeightedPoint;
import jnum.math.Additive;

public class Summation<ReturnType extends Additive<ReturnType>> extends ParallelReduction<ReturnType> {

	
	@Override
	public ReturnType getResult() {
		ReturnType sum = null;
		for(Parallel<ReturnType> task : getParallel().getWorkers()) {
			ReturnType local = task.getPartialResult();
			if(sum == null) sum = local;
			else sum.add(local);
		}
		return sum;
	}
	
	public static class WeightedSum<ReturnType extends WeightedPoint> extends ParallelReduction<ReturnType> {
		@Override
		public ReturnType getResult() {
			ReturnType sum = null;
			for(Parallel<ReturnType> task : getParallel().getWorkers()) {
				ReturnType local = task.getPartialResult();
				if(sum == null) sum = local;
				else {
					sum.add(local.value());
					sum.addWeight(local.weight());
				}
			}
			return sum;
		}
	}
	
	public static class IntValue extends ParallelReduction<Integer> {
		@Override
		public Integer getResult() {
			int sum = 0;
			for(Parallel<Integer> task : getParallel().getWorkers()) sum += task.getPartialResult();
			return sum;
		}
	}
	
	public static class FloatValue extends ParallelReduction<Float> {
		@Override
		public Float getResult() {
			float sum = 0.0F;
			for(Parallel<Float> task : getParallel().getWorkers()) sum += task.getPartialResult();
			return sum;
		}
	}
	
	public static class DoubleValue extends ParallelReduction<Double> {
		@Override
		public Double getResult() {
			double sum = 0.0;
			for(Parallel<Double> task : getParallel().getWorkers()) sum += task.getPartialResult();
			return sum;
		}
	}
	
	public static class IntArray extends ParallelReduction<int[]> {
		@Override
		public int[] getResult() {
			int[] sum = null;
			for(Parallel<int[]> task : getParallel().getWorkers()) {
				int[] local = task.getPartialResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}
	
	public static class FloatArray extends ParallelReduction<float[]> {
		@Override
		public float[] getResult() {
			float[] sum = null;
			for(Parallel<float[]> task : getParallel().getWorkers()) {
				float[] local = task.getPartialResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}
	
	public static class DoubleArray extends ParallelReduction<double[]> {
		@Override
		public double[] getResult() {
			double[] sum = null;
			for(Parallel<double[]> task : getParallel().getWorkers()) {
				double[] local = task.getPartialResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}

	
}