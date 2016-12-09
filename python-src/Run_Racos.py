'''
Demos for RACOS

Author:
    Yi-Qi Hu

time:
    2016.6.13
'''

'''
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 Copyright (C) 2015 Nanjing University, Nanjing, China
'''

from Racos import RacosOptimization
from Components import Dimension
from ObjectiveFunction import Sphere
from ObjectiveFunction import Ackley
from ObjectiveFunction import SetCover
from ObjectiveFunction import MixedFunction
import numpy as np

# parameters
SampleSize = 20             # the instance number of sampling in an iteration
MaxIteration = 100          # the number of iterations
Budget = 2000               # budget in online style
PositiveNum = 1             # the set size of PosPop
RandProbability = 0.99      # the probability of sample in model
UncertainBits = 1           # the dimension size that is sampled randomly


def ResultAnalysis(res, top):
    res.sort()
    top_k = []
    for i in range(top):
        top_k.append(res[i])
    mean_r = np.mean(top_k)
    std_r = np.std(top_k)
    print mean_r, '#', std_r
    return

# continuous optimization
if True:

    # dimension setting
    repeat = 15
    results = []
    DimSize = 100
    regs = []
    regs.append(0.0)
    regs.append(1.0)

    dim = Dimension()
    dim.setDimensionSize(DimSize)
    for i in range(DimSize):
        dim.setRegion(i, regs, True)

    for i in range(repeat):
        print i, ':--------------------------------------------------------------'
        racos = RacosOptimization(dim)

        # call online version RACOS
        # racos.OnlineTurnOn()
        # racos.ContinueOpt(Ackley, SampleSize, Budget, PositiveNum, RandProbability, UncertainBits)

        racos.ContinueOpt(Ackley, SampleSize, MaxIteration, PositiveNum, RandProbability, UncertainBits)

        # print racos.getOptimal().getFeatures()
        print racos.getOptimal().getFitness()
        results.append(racos.getOptimal().getFitness())

    print '======================================================================'
    ResultAnalysis(results, 5)


# discrete optimization
if False:

    # dimension setting
    DimSize = 20
    regs = []
    regs.append(0)
    regs.append(1)

    dim = Dimension()
    dim.setDimensionSize(DimSize)
    for i in range(DimSize):
        dim.setRegion(i, regs, False)

    racos = RacosOptimization(dim)

    # call online version RACOS
    #racos.OnlineTurnOn()
    #racos.DiscreteOpt(SetCover, SampleSize, Budget, PositiveNum, RandProbability, UncertainBits)

    racos.DiscreteOpt(SetCover, SampleSize, MaxIteration, PositiveNum, RandProbability, UncertainBits)

    print racos.getOptimal().getFeatures()
    print racos.getOptimal().getFitness()

# mixed optimization
if False:

    # dimension setting
    DimSize = 10
    regs1 = []
    regs1.append(-1)
    regs1.append(1)
    regs2 = []
    regs2.append(0)
    regs2.append(100)

    dim = Dimension()
    dim.setDimensionSize(DimSize)
    for i in range(DimSize):
        if i%2 == 0:
            dim.setRegion(i, regs1, True)
        else:
            dim.setRegion(i, regs2, False)

    racos = RacosOptimization(dim)

    # call online version RACOS
    #racos.OnlineTurnOn()
    #racos.MixOpt(MixedFunction, SampleSize, Budget, PositiveNum, RandProbability, UncertainBits)

    racos.MixOpt(MixedFunction, SampleSize, MaxIteration, PositiveNum, RandProbability, UncertainBits)

    print racos.getOptimal().getFeatures()
    print racos.getOptimal().getFitness()

