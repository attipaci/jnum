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


public class Constant {

    // Mathematical Constants
    /** The Constant Pi. Same as {@link Math#PI}. */
    public final static double Pi = Math.PI;
    
    /** 2.0 * Pi */
    public final static double twoPi = 2.0 * Pi;
    
    /** 4.0 * Pi */
    public final static double fourPi = 4.0 * Pi;
    
    
    /** 1.0 / Pi */
    public final static double iPi = 1.0 / Pi;
    
    /** 1.0 / (2.0 * Pi) */
    public final static double iTwoPi = 1.0 / twoPi;
    
    /** 1.0 / (4.0 * Pi) */
    public final static double iFourPi = 1.0 / fourPi;
    
    
    /** Pi/2 */
    public final static double halfPi = 0.5 * Pi;
    
    /** Pi/3 */
    public final static double thirdPi = Pi / 3.0;
    
    /** Pi/4 */
    public final static double quarterPi = 0.25 * Pi;
    
    /** A right angle, i.e. 90 {@link Unit#deg}, same as Pi/2 */
    public final static double rightAngle = halfPi;
    
    
    /** sqrt(Pi) */
    public final static double sqrtPi = Math.sqrt(Pi);
    
    /** sqrt(2.0 * Pi) */
    public final static double sqrtTwoPi = Math.sqrt(twoPi);
    
    /** 1.0 / sqrt(Pi) */
    public final static double isqrtPi = 1.0 / sqrtPi;
    
    /** 1.0 / sqrt(2.0 * Pi) */
    public final static double isqrtTwoPi = 1.0 / sqrtTwoPi;
    
    

    
    /** sqrt(2.0) */
    public final static double sqrt2 = Math.sqrt(2.0);
    
    /** sqrt(3.0) */
    public final static double sqrt3 = Math.sqrt(3.0);
    
    /** sqrt(5.0) */
    public final static double sqrt5 = Math.sqrt(5.0);
    
    /** 1.0 / sqrt(2.0) */
    public final static double isqrt2 = 1.0 / sqrt2;
    
    /** 1.0 / sqrt(3.0) */
    public final static double isqrt3 = 1.0 / sqrt3;
    
    /** 1.0 / sqrt(5.0) */
    public final static double isqrt5 = 1.0 / sqrt5;
    
    /** The Golden Ratio, i.e. (1.0 + sqrt(5.0)) / 2.0. */
    public final static double goldenRatio = 0.5 * (1.0 + sqrt5);
    
    /** Euler's constant (0.577215664901533). */
    public final static double euler = 0.577215664901533;
	
    /** The natural log of 2, i.e. log(2.0). */
    public final static double log2 = Math.log(2.0);
	
	/** The natural log of 10, i.e. log(10.0). */
	public final static double log10 = Math.log(10.0);
	
    /** The natural log of Pi, i.e. log(Pi) */
    public final static double logPi = Math.log(Math.PI);
    
    /** log(2.0 * Pi). */
    public final static double logTwoPi = log2 + logPi;
	
	/** 1.0 / log(2.0) */
	public final static double ilog2 = 1.0 / log2;
	
	/** 1.0 / log(10.0) */
	public final static double ilog10 = 1.0 / log10;
	
	
	/** A Gaussian full-width half maximum (FWHM) expressed in  standard deviations (sigmas).  */
	public final static double sigmasInFWHM = 2.0 * Math.sqrt(2.0 * log2);

    
    // Physical Constants
	
    /** The Planck constant (6.626076e-34 Js). */
    public final static double Planck = 6.626076e-34 * Unit.J * Unit.s;
    
    /** The Planck constant by its usual notation: h. */
    public final static double h = Planck;
    
    /** Planck's constant squared */
    public final static double h2 = h * h;
    
    /** Planck's constant cubed */
    public final static double h3 = h2 * h;
    
    /** Planck's constant to the 4th power */
    public final static double h4 = h3 * h;
    
    /** Planck's constant to the 5th power */
    public final static double h5 = h4 * h;
    
    /** Planck's constant to the 6th power */
    public final static double h6 = h5 * h;

    /** Dirac's constant: h / (2.0 * Pi) */
    public final static double Dirac = h / twoPi;
    
    /** Dirac's constant by its usual notation: hbar */
    public final static double hbar = Dirac;
    
    /** Dirac's constant squared */
    public final static double hbar2 = hbar * hbar;
    
    /** Dirac's constant cubed */
    public final static double hbar3 = hbar2 * hbar;
    
    /** Dirac's constant to the 4th power */
    public final static double hbar4 = hbar3 * hbar;
    
    /** Dirac's constant to the 5th power */
    public final static double hbar5 = hbar4 * hbar;
    
    /** Dirac's constant to the 6th power */
    public final static double hbar6 = hbar5 * hbar;
    
    /** The speed of light (299,792,458 m/s) */
    public final static double speedOfLight = 299792458.0 * Unit.m/Unit.s;
    
    /** The speed of light by it's usual notation: c */
    public final static double c = speedOfLight;
    
    /** The speed of light squared */
    public final static double c2 = c * c;
    
    /** The speed of light cubed */
    public final static double c3 = c2 * c;
    
    /** The speed of light to the 4th power */
    public final static double c4 = c3 * c;
    
    /** The speed of light to the 5th power */
    public final static double c5 = c4 * c;
    
    /** The speed of light to the 6th power */
    public final static double c6 = c5 * c;

    /** The electron's charge (1.6022e-19 C) */
    public final static double electronCharge = 1.6022e-19 * Unit.C;
    
    /** The electron's charge (1.6022e-19 C) */
    public final static double e = electronCharge;
    
    /** The electron's charge by another notation */
    public final static double q_e = e;
    
    /** The electron's charge squared */
    public final static double e2 = e * e;
    
    /** The electron's charge cubed */
    public final static double e3 = e2 * e;
    
    /** The electron's charge to the 4th power */
    public final static double e4 = e3 * e;
    
    /** The electron's charge to the 5th power */
    public final static double e5 = e4 * e;
    
    /** The electron's charge to the 6th power */
    public final static double e6 = e5 * e;

    /** Boltzmann's constant (1.380658e-23 J/K). */
    public final static double Boltzmann = 1.380658e-23 * Unit.J/Unit.K;
    
    /** Boltzmann's constant (k = 1.380658e-23 J/K). */
    public final static double k = Boltzmann;
    
    /** Bolzmann's constant by another name */
    public final static double kB = k;
  
    /** The magnetic constant, a.k.a. vacumm permeability (4.0e-7 Pi H/m) */
    public final static double magnetic = 4.0e-7 * Pi * Unit.H / Unit.m;
    
    /** The magnetic constant / vacuum permeability by its usual notation: mu0 */
    public final static double mu0 = magnetic;
   

    /** The electric constant, a.k.a. vacuum permittivity: 1/(mu0 c^2). */
    public final static double electric = 1.0 / (mu0 * c2);
    
    /** The electric constant by its usual notation: epsilon0. */
    public final static double epsilon0 = electric;
   
    /** The electric constant by the shorthand of its usual notation: eps0. */
    public final static double eps0 = epsilon0;
    
    /** The electron's mass (9.1093897e-31 kg) */
    public final static double electronMass = 9.1093897e-31 * Unit.kg;
    
    /** The electron's mass by its usual notation: m_e. */
    public final static double m_e = electronMass;

    /** The protons' mass (1.6726231e-27 kg) */
    public final static double protonMass = 1.6726231e-27 * Unit.kg;
    
    /** The proton's mass by its usual notation: m_p */
    public final static double m_p = protonMass;
    
    /** Avogadro's number (6.0221367e23 / mol). */
    public final static double Avogadro = 6.0221367e23 / Unit.mol;
    
    /** Avogadro's number by its usual notation: N_A. */
    public final static double N_A = Avogadro;
    
    /** Avogadro's number by another notation. */
    public final static double L = Avogadro;

    /** Loschmidt constant (2.686763e25 / m^3). */
    public final static double Loschmidt = 2.686763e25 / Unit.m3;
    
    /** Loschmidt constant by its usual notation: N_L. */
    public final static double N_L = Loschmidt;
    
    /** Loschmidt constant by another notation */
    public final static double n0 = Loschmidt;

    /** The gas constant R */
    public final static double Rgas = N_A * k;

    /** Faraday's constant (N_A * e). */
    public final static double Faraday = N_A * e;
    
    /** Faraday's constant by its usual notation: F. */
    public final static double F = Faraday;

    /** Stefan-Boltzmann constant (6.67051e-8 W / m^2 / K^4). */
    public final static double StefanBoltzmann = 6.67051e-8 * Unit.W / Unit.m2 / Unit.K4;
    
    /** The Stefan-Bolzmann constant by its usual notation: sigma. */
    public final static double sigma = StefanBoltzmann;

    /** The fine structure constant: e^2 / (4 eps0 h c). */
    public final static double fineStructure = e2 / (4.0 * eps0 * h * c);
    
    /** The fine structure constant by its usual notation: alpha */
    public final static double alpha = fineStructure;

    /** Rydberg constant: m_e * e^4 / (8 eps0^2 h^3 c). */
    public final static double Rydberg = m_e * e4 / (8.0 * eps0 * eps0 * h3 * c);
    
    /** Rydberg constant by its usual notation: R */
    public final static double R = Rydberg;
    
    /** The Constant RH. */
    public final static double RH = m_p / (m_e + m_p) * Rydberg;

    /** The gravitational constant G (6.67259e-11 N m^2 / kg^2) */
    public final static double G = 6.67259e-11 * Unit.N * Unit.m2 / Unit.kg2;
    
    /** The typical gravitational acceleration on Earth's surface (g = 9.80665 m/s^2) */
    public final static double g = 9.80665 * Unit.m / Unit.s2;


    /** 0C in kelvins (273.15 K) */
    public final static double zeroCelsius = 273.15 * Unit.K;
    
    // some constants of questionable accuracy

    /** The h0. */
    public static double h0 = 0.71;
    
    /** The h_0. */
    public static double h_0 = h0;
    
    /** Hubble's constant. */
    public static double Hubble = 100.0 * h0 * Unit.km / Unit.s / Unit.Mpc;
    
    /** Hubble's constant by its usual notation: H0. */
    public static double H0 = Hubble;
    
    /** The H_0. */
    public static double H_0 = Hubble;
    
    /** The cosmological density relative to the critical density */
    public static double Omega = 1.0;
    
    /** The cosmological density of matter relative to critical density. */
    public static double Omega_M = 0.27;
    
    /** The cosmological density of dark matter relative to critical density */
    public static double Omega_Lambda = 0.73;

    

}


