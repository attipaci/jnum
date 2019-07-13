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
markup is at best spotty at this point, although classes, methods and arguments
are generally named in a self-explanatory manner. 

There is more work to be done for sure. But, if you are willing to put up with 
occasional bugs and API changes, it might just make your scientific Java 
coding a breeze.

The **jnum** package is dependent on the 
[nom.tam.fits](https://github.com/nom-tam-fits/nom-tam-fits) packages for
dealing with FITS files, also on GitHub 
(https://github.com/nom-tam-fits/nom-tam-fits). 

Here is some more detail on the different parts of the package, by the 
current level of functionality they offer.


### What's solid

These are the parts that are pretty established, generally well-tested, and
least likely to have major API changes...

 * Astronomical coordinate systems and conversions between them, including 
   precession and FITS WCS support (`jnum.astro`).

 * Spherical projections and FITS WCS support for these (`jnum.projection`)

 * Blazing fast, simple, and parallel FFTs -- 1D or multi-dimensional, real
   and complex (`jnum.fft`), and window functions (`jnum.data.WindowFunction`)

 * Generic data cubes (e.g. 1D-3D) via `jnum.data.Data`

 * 1D (`jnum.data.samples`), 2D (`jnum.data.image`), and 3D (`jnum.data.cube`,
   and `jnum.data.cube2`) data objects, supporting FITS I/O, 
   manipulation (e.g. arithmetic, resampling, smoothing, deconvolution) and 
   overlays (`jnum.data.*.overlay`)

 * Complex arithmetic (`jnum.math.Complex`)

 * Values with weights and uncertainties (`jnum.data.WeightedPoint` and
   `jnum.data.DataPoint`)

 * Special functions, e.g. Bessel functions, error function etc. 
   (`jnum.math.ExtraMath` and `jnum.math.specialfunctions`)

 * Parallel processing framework (`jnum.parallel`).

 * A powerful text-based hierarchical configuration engine for programs
   (`jnum.Configurator`). This is what CRUSH uses, see `README.syntax` in
   the CRUSH repo to see what it can do...

 * Parsing and formatting of time and angles (`jnum.text`)



### What works OK, but could be improved

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


 * Matrices by `jnum.math.matrix` (real and complex), with algebra and 
   inversions and decompositions (but no eigenvalues or eigenvectors yet).

 * Fitting of data (`jnum.data.fitting`). Currently, only a downhill simplex
   method is implemented, although there is a decent generic framework that
   it is built on...



### What doesn't quite work (yet)...

Finally, the parts that are heavily under construction at this point...

 * Plotting capabilities (`jnum.plot`)

 * Symbolic math (`jnum.devel.symbolic`). It may be abandoned in favor
   of a dependence on an external package that would provide that 
   functionality

-----------------------------------------------------------------------------

Copyright (C) 2017 Attila Kovacs <attila[AT]sigmyne.com>
