/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum;

/**
 * Phyisical and mathematical constants. All constants bear values in standard SI units. 
 * 
 * @author Attila Kovacs
 *
 */
public final class Constant {

    private Constant() {}
    
    // Mathematical Constants
    /** The Constant &pi;. Same as {@link Math#PI}. */
    public static final double Pi = Math.PI;
    
    /** 2 &pi; */
    public static final double twoPi = 2.0 * Pi;
    
    /** 4 &pi; */
    public static final double fourPi = 4.0 * Pi;
    
    
    /** 1 / &pi; */
    public static final double iPi = 1.0 / Pi;
    
    /** 1/ (2 &pi;) */
    public static final double iTwoPi = 1.0 / twoPi;
    
    /** 1 / (4 &pi;) */
    public static final double iFourPi = 1.0 / fourPi;
    
    
    /** &pi;/2 */
    public static final double halfPi = 0.5 * Pi;
    
    /** &pi;/3 */
    public static final double thirdPi = Pi / 3.0;
    
    /** &pi;/4 */
    public static final double quarterPi = 0.25 * Pi;
    
    /** A right angle, that is 90&deg;, or &pi;/2 */
    public static final double rightAngle = halfPi;
    
    
    /** &radic;&pi; */
    public static final double sqrtPi = Math.sqrt(Pi);
    
    /** &radic;2&pi; */
    public static final double sqrtTwoPi = Math.sqrt(twoPi);
    
    /** 1 / &radic;&pi; */
    public static final double isqrtPi = 1.0 / sqrtPi;
    
    /** 1 / &radic;2&pi; */
    public static final double isqrtTwoPi = 1.0 / sqrtTwoPi;
    
    

    
    /** &radic;2 */
    public static final double sqrt2 = Math.sqrt(2.0);
    
    /** &radic;3 */
    public static final double sqrt3 = Math.sqrt(3.0);
    
    /** &radic;5 */
    public static final double sqrt5 = Math.sqrt(5.0);
    
    /** 1 / &radic;2 */
    public static final double isqrt2 = 1.0 / sqrt2;
    
    /** 1 / &radic;3 */
    public static final double isqrt3 = 1.0 / sqrt3;
    
    /** 1 / &radic;5 */
    public static final double isqrt5 = 1.0 / sqrt5;
    
    /** The Golden Ratio, i.e. (1 + &radic;5) / 2. */
    public static final double goldenRatio = 0.5 * (1.0 + sqrt5);
    
    /** Euler's constant (0.577215664901533). */
    public static final double euler = 0.577215664901533;
	
    /** The natural log of 2, i.e. log(2). */
    public static final double log2 = Math.log(2.0);
	
	/** The natural log of 10, i.e. log(10). */
	public static final double log10 = Math.log(10.0);
	
    /** The natural log of Pi, i.e. log(&pi;) */
    public static final double logPi = Math.log(Math.PI);
    
    /** log(2&pi;). */
    public static final double logTwoPi = log2 + logPi;
	
	/** 1 / log(2) */
	public static final double ilog2 = 1.0 / log2;
	
	/** 1 / log(10) */
	public static final double ilog10 = 1.0 / log10;
	
	
	/** A Gaussian full-width half maximum (FWHM) expressed in  standard deviations (sigmas).  */
	public static final double sigmasInFWHM = 2.0 * Math.sqrt(2.0 * log2);

    
    // Physical Constants
	
    /** The Planck constant (6.626076e-34 Js). */
    public static final double Planck = 6.626076e-34 * Unit.J * Unit.s;
    
    /** The Planck constant by its usual notation: h. */
    public static final double h = Planck;
    
    /** Planck's constant squared */
    public static final double h2 = h * h;
    
    /** Planck's constant cubed */
    public static final double h3 = h2 * h;
    
    /** Planck's constant to the 4th power */
    public static final double h4 = h3 * h;
    
    /** Planck's constant to the 5th power */
    public static final double h5 = h4 * h;
    
    /** Planck's constant to the 6th power */
    public static final double h6 = h5 * h;

    /** Dirac's constant: h / 2&pi; */
    public static final double Dirac = h / twoPi;
    
    /** Dirac's constant: h / 2&pi; */
    public static final double hbar = Dirac;
    
    /** Dirac's constant squared */
    public static final double hbar2 = hbar * hbar;
    
    /** Dirac's constant cubed */
    public static final double hbar3 = hbar2 * hbar;
    
    /** Dirac's constant to the 4th power */
    public static final double hbar4 = hbar3 * hbar;
    
    /** Dirac's constant to the 5th power */
    public static final double hbar5 = hbar4 * hbar;
    
    /** Dirac's constant to the 6th power */
    public static final double hbar6 = hbar5 * hbar;
    
    /** The speed of light (299,792,458 m/s) */
    public static final double speedOfLight = 299792458.0 * Unit.m/Unit.s;
    
    /** The speed of light by it's usual notation: c */
    public static final double c = speedOfLight;
    
    /** The speed of light squared */
    public static final double c2 = c * c;
    
    /** The speed of light cubed */
    public static final double c3 = c2 * c;
    
    /** The speed of light to the 4th power */
    public static final double c4 = c3 * c;
    
    /** The speed of light to the 5th power */
    public static final double c5 = c4 * c;
    
    /** The speed of light to the 6th power */
    public static final double c6 = c5 * c;

    /** The electron's charge (1.6022e-19 C) */
    public static final double electronCharge = 1.6022e-19 * Unit.C;
    
    /** The electron's charge (1.6022e-19 C) */
    public static final double e = electronCharge;
    
    /** The electron's charge by another notation */
    public static final double q_e = e;
    
    /** The electron's charge squared */
    public static final double e2 = e * e;
    
    /** The electron's charge cubed */
    public static final double e3 = e2 * e;
    
    /** The electron's charge to the 4th power */
    public static final double e4 = e3 * e;
    
    /** The electron's charge to the 5th power */
    public static final double e5 = e4 * e;
    
    /** The electron's charge to the 6th power */
    public static final double e6 = e5 * e;

    /** Boltzmann's constant (1.380658e-23 J/K). */
    public static final double Boltzmann = 1.380658e-23 * Unit.J/Unit.K;
    
    /** Boltzmann's constant (k<sub>B</sub> = 1.380658e-23 J/K). */
    public static final double k = Boltzmann;
    
    /** Bolzmann's constant by another name */
    public static final double kB = k;
  
    /** The magnetic constant, a.k.a. vacumm permeability (4.0e-7 &pi; H/m) */
    public static final double magnetic = 4.0e-7 * Pi * Unit.H / Unit.m;
    
    /** The magnetic constant / vacuum permeability by its usual notation: &mu;<sub>0</sub> */
    public static final double mu0 = magnetic;
   

    /** The electric constant, a.k.a. vacuum permittivity: 1/(&mu;<sub>0</sub> c<sup>2</sup>). */
    public static final double electric = 1.0 / (mu0 * c2);
    
    /** The electric constant by its usual notation: &epsilon;<sub>0</sub>. */
    public static final double epsilon0 = electric;
   
    /** The electric constant by the shorthand of its usual notation: &epsilon;<sub>0</sub>. */
    public static final double eps0 = epsilon0;
    
    /** The electron's mass (9.1093897e-31 kg) */
    public static final double electronMass = 9.1093897e-31 * Unit.kg;
    
    /** The electron's mass by its usual notation: m<sub>e</sub>. */
    public static final double m_e = electronMass;

    /** The protons' mass (1.6726231e-27 kg) */
    public static final double protonMass = 1.6726231e-27 * Unit.kg;
    
    /** The proton's mass by its usual notation: m<sub>p</sub> */
    public static final double m_p = protonMass;
    
    /** Avogadro's number (6.0221367e23 / mol). */
    public static final double Avogadro = 6.0221367e23 / Unit.mol;
    
    /** Avogadro's number by its usual notation: N<sub>A</sub>. */
    public static final double N_A = Avogadro;
    
    /** Avogadro's number by another notation. */
    public static final double L = Avogadro;

    /** Loschmidt constant (2.686763e25 / m<sup>3</sup>). */
    public static final double Loschmidt = 2.686763e25 / Unit.m3;
    
    /** Loschmidt constant by its usual notation: N<sub>L</sub>. */
    public static final double N_L = Loschmidt;
    
    /** Loschmidt constant by another notation */
    public static final double n0 = Loschmidt;

    /** The gas constant R */
    public static final double Rgas = N_A * k;

    /** Faraday's constant (N<sub>A</sub> e). */
    public static final double Faraday = N_A * e;
    
    /** Faraday's constant by its usual notation: F. */
    public static final double F = Faraday;

    /** Stefan-Boltzmann constant (6.67051e-8 W / m<sup>2</sup> / K<sup>4</sup>). */
    public static final double StefanBoltzmann = 6.67051e-8 * Unit.W / Unit.m2 / Unit.K4;
    
    /** The Stefan-Bolzmann constant by its usual notation: &sigma;. */
    public static final double sigma = StefanBoltzmann;

    /** The fine structure constant: e<sup>2</sup> / (4 &epsilon;<sub>0</sub> h c). */
    public static final double fineStructure = e2 / (4.0 * eps0 * h * c);
    
    /** The fine structure constant by its usual notation: &alpha; */
    public static final double alpha = fineStructure;

    /** Rydberg constant: m<sub>e</sub> e<sup>4</sup> / (8 eps<sub>0</sub><sup>2</sup> h<sup>3</sup> c). */
    public static final double Rydberg = m_e * e4 / (8.0 * eps0 * eps0 * h3 * c);
    
    /** Rydberg constant by its usual notation: R */
    public static final double R = Rydberg;
    
    /** The Constant R<sub>H</sub>. */
    public static final double RH = m_p / (m_e + m_p) * Rydberg;

    /** The gravitational constant G (6.67259e-11 N m<sup>2</sup> / kg<sup>2</sup>) */
    public static final double G = 6.67259e-11 * Unit.N * Unit.m2 / Unit.kg2;
    
    /** The typical gravitational acceleration on Earth's surface (g = 9.80665 m/s<sup>2</sup>) */
    public static final double g = 9.80665 * Unit.m / Unit.s2;


    /** 0C in kelvins (273.15 K) */
    public static final double zeroCelsius = 273.15 * Unit.K;
    
    // some constants of questionable accuracy

    /** The h<sub>0</sub>. */
    public static double h0 = 0.71;
    
    /** Hubble's constant. */
    public static double Hubble = 100.0 * h0 * Unit.km / Unit.s / Unit.Mpc;
    
    /** Hubble's constant by its usual notation: H<sub>0</sub>. */
    public static double H0 = Hubble;
   
    
    /** The cosmological density relative to the critical density: &Omega; */
    public static double Omega = 1.0;
    
    /** The cosmological density of matter relative to critical density: &Omega;<sub>M</sub> */
    public static double Omega_M = 0.27;
    
    /** The cosmological density of dark matter relative to critical density: &Omega;<sub>&Lambda;</sub> */
    public static double Omega_Lambda = 0.73;

    

}


