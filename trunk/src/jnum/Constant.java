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
// Copyright (c) 2007 Attila Kovacs 

package jnum;

// TODO: Auto-generated Javadoc
// TODO Convert to Enum?
/**
 * The Class Constant.
 */
public class Constant {

    // Mathematical Constants
    /** The Constant PI. */
    public final static double Pi = Math.PI;
    
    /** The Constant twoPi. */
    public final static double twoPi = 2.0 * Pi;
    
    /** The Constant fourPi. */
    public final static double fourPi = 4.0 * Pi;
    
    
    /** The Constant iPi. */
    public final static double iPi = 1.0 / Pi;
    
    /** The Constant iTwoPi. */
    public final static double iTwoPi = 1.0 / twoPi;
    
    /** The Constant iFourPi. */
    public final static double iFourPi = 1.0 / fourPi;
    
    /** The Constant rightAngle. */
    public final static double rightAngle = 0.5 * Pi;
    
    /** The Constant sqrtPi. */
    public final static double sqrtPi = Math.sqrt(Pi);
    
    /** The Constant sqrtTwoPi. */
    public final static double sqrtTwoPi = Math.sqrt(twoPi);
    
    /** The Constant isqrtPi. */
    public final static double isqrtPi = 1.0 / sqrtPi;
    
    /** The Constant isqrtTwoPi. */
    public final static double isqrtTwoPi = 1.0 / sqrtTwoPi;
    
    

    
    /** The Constant sqrt2. */
    public final static double sqrt2 = Math.sqrt(2.0);
    
    /** The Constant sqrt3. */
    public final static double sqrt3 = Math.sqrt(3.0);
    
    /** The Constant sqrt5. */
    public final static double sqrt5 = Math.sqrt(5.0);
    
    /** The Constant isqrt2. */
    public final static double isqrt2 = 1.0 / sqrt2;
    
    /** The Constant isqrt3. */
    public final static double isqrt3 = 1.0 / sqrt2;
    
    /** The Constant isqrt5. */
    public final static double isqrt5 = 1.0 / sqrt2;
    
    /** The Constant goldenRatio. */
    public final static double goldenRatio = 0.5 * (1.0 + sqrt5);
    
    /** The Constant euler. */
    public final static double euler = 0.577215664901533;
	
    /** The Constant log2. */
    public final static double log2 = Math.log(2.0);
	
	/** The Constant log10. */
	public final static double log10 = Math.log(10.0);
	
    /** The Constant logPi. */
    public final static double logPi = Math.log(Math.PI);
    
    /** The Constant logTwoPi. */
    public final static double logTwoPi = log2 + logPi;
	
	/** The Constant ilog2. */
	public final static double ilog2 = 1.0 / log2;
	
	/** The Constant ilog10. */
	public final static double ilog10 = 1.0 / log10;
	
	
	/** The Constant sigmasInFWHM. */
	public final static double sigmasInFWHM = 2.0 * Math.sqrt(2.0 * log2);

    
    // Physical Constants
    /** The Constant Planck. */
    public final static double Planck = 6.626076e-34 * Unit.J * Unit.s;
    
    /** The Constant h. */
    public final static double h = Planck;
    
    /** The Constant h2. */
    public final static double h2 = h * h;
    
    /** The Constant h3. */
    public final static double h3 = h2 * h;
    
    /** The Constant h4. */
    public final static double h4 = h3 * h;
    
    /** The Constant h5. */
    public final static double h5 = h4 * h;
    
    /** The Constant h6. */
    public final static double h6 = h5 * h;

    /** The Constant Dirac. */
    public final static double Dirac = h / twoPi;
    
    /** The Constant hbar. */
    public final static double hbar = Dirac;
    
    /** The Constant hbar2. */
    public final static double hbar2 = hbar * hbar;
    
    /** The Constant hbar3. */
    public final static double hbar3 = hbar2 * hbar;
    
    /** The Constant hbar4. */
    public final static double hbar4 = hbar3 * hbar;
    
    /** The Constant hbar5. */
    public final static double hbar5 = hbar4 * hbar;
    
    /** The Constant hbar6. */
    public final static double hbar6 = hbar5 * hbar;
    
    /** The Constant c. */
    public final static double c = 299792458.0 * Unit.m/Unit.s;
    
    /** The Constant c2. */
    public final static double c2 = c * c;
    
    /** The Constant c3. */
    public final static double c3 = c2 * c;
    
    /** The Constant c4. */
    public final static double c4 = c3 * c;
    
    /** The Constant c5. */
    public final static double c5 = c4 * c;
    
    /** The Constant c6. */
    public final static double c6 = c5 * c;

    /** The Constant e. */
    public final static double e = 1.6022e-19 * Unit.C;
    
    /** The Constant Qe. */
    public final static double Qe = e;
    
    /** The Constant qe. */
    public final static double qe = e;
    
    /** The Constant Q_e. */
    public final static double Q_e = e;
    
    /** The Constant q_e. */
    public final static double q_e= e;
    
    /** The Constant e2. */
    public final static double e2 = e * e;
    
    /** The Constant e3. */
    public final static double e3 = e2 * e;
    
    /** The Constant e4. */
    public final static double e4 = e3 * e;
    
    /** The Constant e5. */
    public final static double e5 = e4 * e;
    
    /** The Constant e6. */
    public final static double e6 = e5 * e;

    /** The Constant Boltzmann. */
    public final static double Boltzmann = 1.380658e-23 * Unit.J/Unit.K;
    
    /** The Constant k. */
    public final static double k = Boltzmann;
    
    /** The Constant kB. */
    public final static double kB = k;
    
    /** The Constant k_B. */
    public final static double k_B = k;

    /** The Constant mu0. */
    public final static double mu0 = 4.0e-7 * Pi * Unit.H / Unit.m;
    
    /** The Constant mu_0. */
    public final static double mu_0 = 4.0e-7 * Pi * Unit.H / Unit.m;
    
    /** The Constant u0. */
    public final static double u0 = mu0;
    
    /** The Constant u_0. */
    public final static double u_0 = mu0;
    
    /** The Constant epsilon0. */
    public final static double epsilon0 = 1.0 / (u0 * c2);
    
    /** The Constant epsilon_0. */
    public final static double epsilon_0 = epsilon0;
    
    /** The Constant eps0. */
    public final static double eps0 = epsilon0;
    
    /** The Constant eps_0. */
    public final static double eps_0 = epsilon0;


    /** The Constant me. */
    public final static double me = 9.1093897e-31 * Unit.kg;
    
    /** The Constant m_e. */
    public final static double m_e = me;

    /** The Constant mp. */
    public final static double mp = 1.6726231e-27 * Unit.kg;
    
    /** The Constant m_p. */
    public final static double m_p = mp;
    
    /** The Constant Avogadro. */
    public final static double Avogadro = 6.0221367e23 / Unit.mol;
    
    /** The Constant NA. */
    public final static double NA = Avogadro;
    
    /** The Constant N_A. */
    public final static double N_A = NA;
    
    /** The Constant L. */
    public final static double L = NA;

    /** The Constant Loschmidt. */
    public final static double Loschmidt = 2.686763e25 / Unit.m3;
    
    /** The Constant NL. */
    public final static double NL = Loschmidt;
    
    /** The Constant N_L. */
    public final static double N_L = NL;
    
    /** The Constant n0. */
    public final static double n0 = NL;
    
    /** The Constant n_0. */
    public final static double n_0 = NL;

    /** The Constant Rgas. */
    public final static double Rgas = NA * k;

    /** The Constant Faraday. */
    public final static double Faraday = NA * e;
    
    /** The Constant F. */
    public final static double F = Faraday;

    /** The Constant StefanBoltzmann. */
    public final static double StefanBoltzmann = 6.67051e-8 * Unit.W / Unit.m2 / Unit.K4;
    
    /** The Constant sigma. */
    public final static double sigma = StefanBoltzmann;

    /** The Constant fineStructure. */
    public final static double fineStructure = e2 / (4.0 * eps0 * h * c);
    
    /** The Constant alpha. */
    public final static double alpha = fineStructure;

    /** The Constant Rydberg. */
    public final static double Rydberg = me * e4 / (8.0 * eps0 * eps0 * h3 * c);
    
    /** The Constant R. */
    public final static double R = Rydberg;
    
    /** The Constant RH. */
    public final static double RH = mp / (me + mp) * Rydberg;

    /** The Constant G. */
    public final static double G = 6.67259e-11 * Unit.N * Unit.m2 / Unit.kg2;
    
    /** The Constant g. */
    public final static double g = 9.80665 * Unit.m / Unit.s2;


    /** 0C in kelvins */
    public final static double zeroCelsius = 273.15 * Unit.K;
    
    // some constants of questionable accuracy

    /** The h0. */
    public static double h0 = 0.71;
    
    /** The h_0. */
    public static double h_0 = h0;
    
    /** The Hubble. */
    public static double Hubble = 100.0 * h0 * Unit.km / Unit.s / Unit.Mpc;
    
    /** The H0. */
    public static double H0 = Hubble;
    
    /** The H_0. */
    public static double H_0 = Hubble;
    
    /** The Omega. */
    public static double Omega = 1.0;
    
    /** The Omega_ m. */
    public static double Omega_M = 0.27;
    
    /** The Omega_ lambda. */
    public static double Omega_Lambda = 0.73;

    

}


