# JNUM

Author: Attila Kovacs <attila[AT]sigmyne.com>


---------------------------------------------------------------------------


## Introduction

**jnum** is a set of numerical Java classes, with an astronomy focus. It has
started life inside the **crush** data reduction code 
(https://github.com/attipaci/crush), but was split from it a while back.

**jnum** is designed to be super-fast, while also being very easy to use.
(It's floating point FFTs on 1M points beat the C FFTW handily in my tests).

**jnum** is not (yet) a fully stable code. It's API still continues to evolve
(driven by [crush](https://github.com/attipaci/crush)), although there are 
signs of stabilization in many of the Java subpackages within. Also, some parts
have clearly received more attention through the years as others. The javadoc 
markup is far from complete at this point, although it is steadily improving
and classes, methods and arguments are generally named in a self-explanatory 
manner. 

If you are willing to put up with occasional bugs and API changes, it might 
just make your scientific Java coding easier.

The **jnum** package is dependent on the 
[nom.tam.fits](https://github.com/nom-tam-fits/nom-tam-fits) packages for
dealing with FITS files, also on GitHub 
(https://github.com/nom-tam-fits/nom-tam-fits). 

Here is some more detail on the different parts of the package, by the 
current level of functionality they offer.

### Mutable object classes

__jnum__ is meant to be at once simple to use, powerful, and fast. These 
goals are sometimes at odds with one another, but trying to reconcile them 
as best as possible determines some of the particular choices that are made
throughout the library.

One such choice is the use of __mutable data__ classes nearly everywhere 
in the library. Why? It is because object instantiation is a well-known 
bottleneck of Java (and other high-level languages). Even with the significant 
improvements Java has made over the years to mitigate the cost of object
creation, the creation of object classes don't come close to the speed of 
using primitives as transient objects. Therefore, __jnum__ is designed to re-use 
objects whenever possible, and hence opt for mutable data types, in general. 

The choice of mutable data types goes against the current trend of functional 
programming and preference immutable data classes, but it is a choice made 
for providing vastly superior performance consistently. The downside is that 
mutable objects allow for more programming mistakes than immutable ones, since 
the state of an object used in one part of the code might be affected by 
operations performed in entirely different parts of the code. Thus, the price
of performance is some burden on the programmer, who needs to be a little
more careful as a result.

One common pattern in __jnum__ is for functions to return their result in
a caller supplied mutable object, rather than a return value. Such constructs
are common in low-level languages, like C, but foreign to Java. Nevertheless,
__jnum__ opts for using these constructs in order to reduce creating
transient objects.
 

### What's solid

These are the parts that are pretty established, generally well-tested, and
least likely to have major API changes...

 * Astronomical coordinate systems and conversions between them, including 
   precession (both FK4/FK5 and IAU2000), IAU2000A/R06 nutation, annual and
   diurnal aberration correction, gravitational deflection by the Sun,
   polar-wobble correction, and FITS WCS support (`jnum.astro`).

 * Spherical projections and FITS WCS support for these (`jnum.projection`)

 * Blazing fast, simple, and parallel FFTs -- 1D or multi-dimensional, real
   and complex (`jnum.fft`), and window functions (`jnum.data.WindowFunction`)

 * Generic data cubes (e.g. 1D-3D) via `jnum.data.Data`, with powerful
   overlay support (i.e. views).

 * 1D (`jnum.data.samples`), 2D (`jnum.data.image`), and 3D (`jnum.data.cube`,
   and `jnum.data.cube2`) data objects, supporting FITS I/O, 
   manipulation (e.g. arithmetic, resampling, smoothing, deconvolution) and 
   overlays (`jnum.data.*.overlay`)

 * Complex arithmetic (`jnum.math.Complex`)

 * Matrices and vectors (real, complex, and generic types), with inversion,
   eigen-system finding, and SVD (for real-valued `jnum.math.matrix.Matrix`
   classes). Efficient implementation for diagonal matrices. Support for
   heterogeneous dot products among matrices of different classes. The
   `jnum.math.matrix` package is also almost fully documented in Javadoc.

 * Values with weights and uncertainties (`jnum.data.WeightedPoint`,
   `jnum.data.DataPoint`, and `jnum.data.WeightedComplex`)

 * Special functions, e.g. Bessel functions, error function etc. 
   (`jnum.math.ExtraMath` and `jnum.math.specialfunctions`)

 * Parallel processing framework (`jnum.parallel`).

 * A powerful text-based hierarchical configuration engine for programs
   (`jnum.Configurator`). This is what CRUSH uses, see `README.syntax` in
   the CRUSH repo to see what it can do...

 * Parsing and formatting of time and angles (`jnum.text`)



### What works OK, but could be improved...

Here are the bits that do the job, have been tested quite well, but could
use additional functionality, including potential rethinking parts of the 
API...


 * Support for physical units (`jnum.Unit`).

 * Physical constants (`jnum.Constant`).

 * Common UTF symbols for math & physics (`jnum.Symbol`)

 * Astronomical time measures (except UT1) and leap seconds (`jnum.astro`)

 * Statistics on data (`jnum.data.Statistics`, `jnum.data.Data`, and
   `jnum.data.LinearRegression`)

 * Support for generic multidimensional arrays and iterators on them
   (`jnum.mesh` and `jnum.data.ArrayUtil`)

 * Interpolation (`jnum.data.interpolator`)

 * Dirfile I/O (`jnum.io.dirfile`)

 * Easy-to-use custom messaging support (`jnum.reporting`)

 * A generic framework for bitwise flag management (`jnum.util.Flag`)



### Where there is a fair bit of room for improvement...

Here are the parts that may undergo substantial development, which may 
also change the API significantly.

 * Fitting of data (`jnum.data.fitting`). Currently, only a downhill simplex
   method is implemented, although there is a decent generic framework that
   it is built on...



### What doesn't quite work (yet)...

Finally, the parts that are heavily under construction at this point...

 * Plotting capabilities (`jnum.plot`)

-----------------------------------------------------------------------------

Copyright (C) 2021 Attila Kovacs <attila[AT]sigmyne.com>
