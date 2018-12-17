/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.math.specialfunctions;

import jnum.Function;


public final class Bessel {


	/**
	 * Bessel J0 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double J0(final double x) {
		final double ax = Math.abs(x);

		if(ax < 8.0) {
			final double x2=x*x;
			final double a=57568490574.0+x2*(-13362590354.0+x2*(651619640.7+x2*(-11214424.18+x2*(77392.33017+x2*(-184.9052456)))));
			final double b=57568490411.0+x2*(1029532985.0+x2*(9494680.718+x2*(59272.64853+x2*(267.8532712+x2*1.0))));
			return a/b;
		} 

		final double ix=8.0/ax;
		final double ix2=ix*ix;
		final double ax1=ax-0.785398164;
		final double a=1.0+ix2*(-0.1098628627e-2+ix2*(0.2734510407e-4+ix2*(-0.2073370639e-5+ix2*0.2093887211e-6)));
		final double b = -0.1562499995e-1+ix2*(0.1430488765e-3+ix2*(-0.6911147651e-5+ix2*(0.7621095161e-6-ix2*0.934935152e-7)));	
		return Math.sqrt(0.636619772/ax)*(a*Math.cos(ax1)-ix*b*Math.sin(ax1));
	}


	/**
	 * Bessel J1 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double J1(final double x) {
		final double ax = Math.abs(x);

		if(ax < 8.0) {
			final double x2=x*x;
			final double a=x*(72362614232.0+x2*(-7895059235.0+x2*(242396853.1+x2*(-2972611.439+x2*(15704.48260+x2*(-30.16036606))))));
			final double b=144725228442.0+x2*(2300535178.0+x2*(18583304.74+x2*(99447.43394+x2*(376.9991397+x2*1.0))));
			return a/b;
		}
		final double iax=8.0/ax;
		final double iax2=iax*iax;
		final double ax1=ax-2.356194491;
		final double a=1.0+iax2*(0.183105e-2+iax2*(-0.3516396496e-4+iax2*(0.2457520174e-5+iax2*(-0.240337019e-6))));
		final double b=0.04687499995+iax2*(-0.2002690873e-3+iax2*(0.8449199096e-5+iax2*(-0.88228987e-6+iax2*0.105787412e-6)));
		final double value = Math.sqrt(0.636619772/ax)*(a*Math.cos(ax1)-iax*b*Math.sin(ax1));
		return x < 0.0 ? -value : value;
	}


	/**
	 * The Bessel J_n function class.
	 */
	public static class J implements Function<Double, Double> {

		/** The order. */
		private int order;

		/**
		 * Instantiates a new j.
		 *
		 * @param order the order
		 */
		public J(int order) { this.order = order; }

		/**
		 * Gets the order.
		 *
		 * @return the order
		 */
		public int getOrder() { return order; }

		/* (non-Javadoc)
		 * @see jnum.math.Function#valueAt(java.lang.Object)
		 */
		@Override
		public Double valueAt(Double x) {
			return J(order, x);
		}
	}

	/**
	 * Bessel J_n function. Adapted from Numerical Recipes in C.
	 *
	 * @param n the n
	 * @param x the x
	 * @return the double
	 */
	public static double J(int n, double x) {

		if(n < 2) {
			if(n < 0) throw new IllegalArgumentException("No BesselJ of negative kind.");
			if(n == 0) return J0(x);
			return J1(x);
		}
		final double ax = Math.abs(x);

		if(ax == 0.0) return 0.0;

		double value;
		final double tox = 2.0 / ax;

		if(ax > n) {
			double bjm = J0(ax);
			double bj = J1(ax);
			for(int j = 1; j < n; j++) {
				final double bjp = j * tox * bj - bjm;
				bjm = bj;
				bj = bjp;
			}
			value = bj;
		} 
		else {
			final int m = 2 * ((n + (int) Math.sqrt(ACC * n)) / 2);
			boolean jsum = false;
			double bjp = value = 0.0;
			double sum = 0.0;
			double bj = 1.0;
			for(int j = m; j > 0; j--) {
				double bjm = j * tox * bj - bjp;
				bjp = bj;
				bj = bjm;
				if(Math.abs(bj) > BIG_VALUE) {
					bj *= BIG_INVERSE;
					bjp *= BIG_INVERSE;
					value *= BIG_INVERSE;
					sum *= BIG_INVERSE;
				}
				if(jsum) sum += bj;
				jsum = !jsum;
				if(j == n) value = bjp;
			}
			sum = 2.0 * sum - bj;
			value /= sum;
		}
		return x < 0.0 && ((n & 1) != 0) ? -value : value;
	}


	/**
	 * Bessel Y0 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double Y0(final double x) {
		if(x < 8.0) {
			final double x2=x*x;
			final double a=-2957821389.0+x2*(7062834065.0+x2*(-512359803.6+x2*(10879881.29+x2*(-86327.92757+x2*228.4622733))));
			final double b=40076544269.0+x2*(745249964.8+x2*(7189466.438+x2*(47447.26470+x2*(226.1030244+x2*1.0))));
			return (a/b)+0.636619772*J0(x)*Math.log(x);
		} 

		final double ix=8.0/x;
		final double ix2=ix*ix;
		final double x1=x-0.785398164;
		final double a=1.0+ix2*(-0.1098628627e-2+ix2*(0.2734510407e-4+ix2*(-0.2073370639e-5+ix2*0.2093887211e-6)));
		final double b = -0.1562499995e-1+ix2*(0.1430488765e-3+ix2*(-0.6911147651e-5+ix2*(0.7621095161e-6+ix2*(-0.934945152e-7))));
		return Math.sqrt(0.636619772/x)*(a*Math.sin(x1)+ix*b*Math.cos(x1));
	}



	/**
	 * Bessel Y1 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double Y1(final double x) {
		if(x < 8.0) {
			final double x2=x*x;
			final double a=x*(-0.4900604943e13+x2*(0.1275274390e13+x2*(-0.5153438139e11+x2*(0.7349264551e9+x2*(-0.4237922726e7+x2*0.8511937935e4)))));
			final double b=0.2499580570e14+x2*(0.4244419664e12+x2*(0.3733650367e10+x2*(0.2245904002e8+x2*(0.1020426050e6+x2*(0.3549632885e3+x2)))));
			return (a/b)+0.636619772*(J1(x)*Math.log(x)-1.0/x);
		} 

		final double ix=8.0/x;
		final double ix2=ix*ix;
		final double x1=x-2.356194491;
		final double a=1.0+ix2*(0.183105e-2+ix2*(-0.3516396496e-4+ix2*(0.2457520174e-5+ix2*(-0.240337019e-6))));
		final double b=0.04687499995+ix2*(-0.2002690873e-3+ix2*(0.8449199096e-5+ix2*(-0.88228987e-6+ix2*0.105787412e-6)));
		return Math.sqrt(0.636619772/x)*(a*Math.sin(x1)+ix*b*Math.cos(x1));
	}


	/**
	 * Bessel Y_n function. Adapted from Numerical Recipes in C.
	 *
	 * @param n the n
	 * @param x the x
	 * @return the double
	 */
	public static double Y(int n, double x) {	
		if(n < 2) {
			if(n < 0) throw new IllegalArgumentException("No BesselY of negative kind.");
			if(n == 0) return Y0(x);
			return Y1(x);
		}

		final double tox=2.0/x;
		double by=Y1(x);
		double bym=Y0(x);
		for (int j=1;j<n;j++) {
			double byp=j*tox*by-bym;
			bym=by;
			by=byp;
		}
		return by;
	}

	/**
	 * Bessel Y_n function class.
	 */
	public static class Y implements Function<Double, Double> {

		/** The order. */
		private int order;

		/**
		 * Instantiates a new y.
		 *
		 * @param order the order
		 */
		public Y(int order) { this.order = order; }

		/**
		 * Gets the order.
		 *
		 * @return the order
		 */
		public int getOrder() { return order; }

		/* (non-Javadoc)
		 * @see jnum.math.Function#valueAt(java.lang.Object)
		 */
		@Override
		public Double valueAt(Double x) {
			return Y(order, x);
		}
	}


	/**
	 * Bessel I0 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double I0(double x) {
		final double ax = Math.abs(x);
		if (ax < 3.75) {
			double y=x/3.75;
			y*=y;
			return 1.0+y*(3.5156229+y*(3.0899424+y*(1.2067492+y*(0.2659732+y*(0.360768e-1+y*0.45813e-2)))));
		} 
		
		final double y=3.75/ax;
		return Math.exp(ax)/Math.sqrt(ax)
		        *(0.39894228+y*(0.1328592e-1+y*(0.225319e-2+y*(-0.157565e-2+y*(0.916281e-2+y*(-0.2057706e-1+y*(0.2635537e-1+y*(-0.1647633e-1+y*0.392377e-2))))))));
	}


	/**
	 * Bessel I1 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double I1(double x) {
		final double ax = Math.abs(x);
		double value;

		if (ax < 3.75) {
			double y=x/3.75;
			y*=y;
			value = ax*(0.5+y*(0.87890594+y*(0.51498869+y*(0.15084934+y*(0.2658733e-1+y*(0.301532e-2+y*0.32411e-3))))));
		} 
		else {
			final double y=3.75/ax;
			value=0.2282967e-1+y*(-0.2895312e-1+y*(0.1787654e-1-y*0.420059e-2));
			value=0.39894228+y*(-0.3988024e-1+y*(-0.362018e-2+y*(0.163801e-2+y*(-0.1031555e-1+y*value))));
			value *= Math.exp(ax)/Math.sqrt(ax);
			return value;
		}
		return x < 0.0 ? -value : value;
	}



	/**
	 * Bessel I_n function. Adapted from Numerical Recipes in C.
	 *
	 * @param n the n
	 * @param x the x
	 * @return the double
	 */
	public static double I(int n, double x) {
		if(n < 2) {
			if(n < 0) throw new IllegalArgumentException("No BesselI of negative kind.");
			if(n == 0) return I0(x);
			return I1(x);
		}
		if (x == 0.0) return 0.0;

		final double tox = 2.0/Math.abs(x);
		double bip=0.0;
		double value=bip;
		double bi=1.0;
		for(int j=2*(n+(int)Math.sqrt(ACC*n)); j>0; j--) {
			final double bim=bip+j*tox*bi;
			bip=bi;
			bi=bim;
			if (Math.abs(bi) > BIG_VALUE) {
				value *= BIG_INVERSE;
				bi *= BIG_INVERSE;
				bip *= BIG_INVERSE;
			}
			if (j == n) value=bip;
		}
		value *= I0(x)/bi;
		return x < 0.0 && ((n & 1) > 0) ? -value : value;
	}

	/**
	 * The Bessel I_n function class.
	 */
	public static class I implements Function<Double, Double> {

		/** The order. */
		private int order;

		/**
		 * Instantiates a new i.
		 *
		 * @param order the order
		 */
		public I(int order) { this.order = order; }

		/**
		 * Gets the order.
		 *
		 * @return the order
		 */
		public int getOrder() { return order; }

		/* (non-Javadoc)
		 * @see jnum.math.Function#valueAt(java.lang.Object)
		 */
		@Override
		public Double valueAt(Double x) {
			return I(order, x);
		}
	}

	/**
	 * Bessel K0 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double K0(double x) {
		if (x <= 2.0) {
			double y = x*x/4.0;
			return -Math.log(x/2.0) * I0(x) - 0.57721566 
					+ y*(0.42278420+y*(0.23069756+y*(0.3488590e-1+y*(0.262698e-2+y*(0.10750e-3+y*0.74e-5)))));
		} 

		double y = 2.0/x;
		return Math.exp(-x)/Math.sqrt(x)
				* (1.25331414+y*(-0.7832358e-1+y*(0.2189568e-1+y*(-0.1062446e-1+y*(0.587872e-2+y*(-0.251540e-2+y*0.53208e-3))))));

	}




	/**
	 * Bessel K1 function. Adapted from Numerical Recipes in C.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double K1(double x) {
		if (x <= 2.0) {
			final double y= 0.25*x*x;
			return Math.log(0.5*x)*I1(x) + 
					(1.0+y*(0.15443144+y*(-0.67278579+y*(-0.18156897+y*(-0.1919402e-1+y*(-0.110404e-2+y*(-0.4686e-4))))))) / x;
		} 

		final double y=2.0/x;
		return Math.exp(-x)/Math.sqrt(x)
				* (1.25331414+y*(0.23498619+y*(-0.3655620e-1+y*(0.1504268e-1+y*(-0.780353e-2+y*(0.325614e-2+y*(-0.68245e-3)))))));	
	}



	/**
	 * Bessel K_n function. Adapted from Numerical Recipes in C.
	 *
	 * @param n the n
	 * @param x the x
	 * @return the double
	 */
	public static double K(int n, double x) {
		if(n < 2) {
			if(n < 0) throw new IllegalArgumentException("No BesselK of negative kind.");
			if(n == 0) return K0(x);
			return K1(x);
		}
		final double tox = 2.0/x;
		double bkm = K0(x);
		double bk = K1(x);
		for (int j=1;j<n;j++) {
			final double bkp = bkm+j*tox*bk;
			bkm = bk;
			bk = bkp;
		}
		return bk;
	}

	/**
	 * Bessel K_n function class.
	 */
	public static class K implements Function<Double, Double> {

		/** The order. */
		private int order;

		/**
		 * Instantiates a new k.
		 *
		 * @param order the order
		 */
		public K(int order) { this.order = order; }

		/**
		 * Gets the order.
		 *
		 * @return the order
		 */
		public int getOrder() { return order; }

		/* (non-Javadoc)
		 * @see jnum.math.Function#valueAt(java.lang.Object)
		 */
		@Override
		public Double valueAt(Double x) {
			return K(order, x);
		}
	}

	/** The Constant ACC. */
	private static final double ACC = 40.0;

	/** The Constant BIGNO. */
	private static final double BIG_VALUE = 1.0e10;

	/** The Constant BIGNI. */
	private static final double BIG_INVERSE = 1.0e-10;

}
