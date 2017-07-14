/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila[AT]sigmyne.com>.
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
/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.parallel;

import jnum.data.WeightedPoint;
import jnum.math.Additive;

// TODO: Auto-generated Javadoc
/**
 * The Class Summation.
 *
 * @param <ReturnType> the generic type
 */
public class Summation<ReturnType extends Additive<ReturnType>> extends ParallelReduction<ReturnType> {

	
	/* (non-Javadoc)
	 * @see jnum.parallel.ParallelReduction#getResult()
	 */
	@Override
	public ReturnType getResult() {
		ReturnType sum = null;
		for(ParallelTask<ReturnType> task : getParallel().getWorkers()) {
			ReturnType local = task.getLocalResult();
			if(sum == null) sum = local;
			else sum.add(local);
		}
		return sum;
	}
	
	/**
	 * The Class WeightedSum.
	 *
	 * @param <ReturnType> the generic type
	 */
	public static class WeightedSum<ReturnType extends WeightedPoint> extends ParallelReduction<ReturnType> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public ReturnType getResult() {
			ReturnType sum = null;
			for(ParallelTask<ReturnType> task : getParallel().getWorkers()) {
				ReturnType local = task.getLocalResult();
				if(sum == null) sum = local;
				else {
					sum.add(local.value());
					sum.addWeight(local.weight());
				}
			}
			return sum;
		}
	}
	
	/**
	 * The Class IntValue.
	 */
	public static class IntValue extends ParallelReduction<Integer> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public Integer getResult() {
			int sum = 0;
			for(ParallelTask<Integer> task : getParallel().getWorkers()) sum += task.getLocalResult();
			return sum;
		}
	}
	
	/**
	 * The Class FloatValue.
	 */
	public static class FloatValue extends ParallelReduction<Float> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public Float getResult() {
			float sum = 0.0F;
			for(ParallelTask<Float> task : getParallel().getWorkers()) sum += task.getLocalResult();
			return sum;
		}
	}
	
	/**
	 * The Class DoubleValue.
	 */
	public static class DoubleValue extends ParallelReduction<Double> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public Double getResult() {
			double sum = 0.0;
			for(ParallelTask<Double> task : getParallel().getWorkers()) sum += task.getLocalResult();
			return sum;
		}
	}
	
	/**
	 * The Class IntArray.
	 */
	public static class IntArray extends ParallelReduction<int[]> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public int[] getResult() {
			int[] sum = null;
			for(ParallelTask<int[]> task : getParallel().getWorkers()) {
				int[] local = task.getLocalResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}
	
	/**
	 * The Class FloatArray.
	 */
	public static class FloatArray extends ParallelReduction<float[]> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public float[] getResult() {
			float[] sum = null;
			for(ParallelTask<float[]> task : getParallel().getWorkers()) {
				float[] local = task.getLocalResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}
	
	/**
	 * The Class DoubleArray.
	 */
	public static class DoubleArray extends ParallelReduction<double[]> {
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ParallelReduction#getResult()
		 */
		@Override
		public double[] getResult() {
			double[] sum = null;
			for(ParallelTask<double[]> task : getParallel().getWorkers()) {
				double[] local = task.getLocalResult();
				if(sum == null) sum = local;
				else for(int k=sum.length; --k >= 0; ) sum[k] += local[k];
			}
			return sum;
		}
	}

	
}
