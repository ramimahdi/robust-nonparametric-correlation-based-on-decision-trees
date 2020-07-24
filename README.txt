BY: Rami Mahdi (ramimahdi@gmail.com)
Date: 01/06/2020

This package is an implementation the non-parametric dependence testing (or correlation) method described in the paper:
"A Nonparametric Test of Dependence Based on an Ensemble of Decision Trees"

See UCorrRandomForrest_Test.java, for an easy example of how to compute uCorr.

The main implementation class UCorrRandomForrest.java offers threes method to computing the coefficient.
- UCorr(data) that uses all default parameters, which were used in experiments in the paper.
- UCorr(data, params) that uses customized params to compute the coefficient. This might be useful to advanced users.
- uCorr(data, arrays of params) that uses a grid search as described in the paper to make parameters selection. This
  might be useful for researchers and/or advanced users.

These methods return UCorrResult Object which includes:
- corrCoefficient
- correspondingNullDistSTD
- pValue

Users may also use RandomTrainTestSampler.java to visualize the partitioning trees as presented in the paper.

This implementation was developed and tested using standard Java 11.