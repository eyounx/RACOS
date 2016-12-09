'''
RACOS is an algorithm for solving derivative-free non-convex optimization problems
In this class, there are three RACOS methods aimed for continuous, discrete and mixed optimization

Author:
    Yi-Qi Hu

Time:
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

from Components import Instance
from Components import Dimension
from Tools import RandomOperator

class RacosOptimization:

    def __init__(self, dim):

        self.__Pop = []             # population set
        self.__PosPop = []          # positive sample set
        self.__Optimal = []         # the best sample so far
        self.__NextPop = []         # the next population set
        self.__region = []          # the region of model
        self.__label = []           # the random label, if true random in this dimension
        self.__SampleSize = 0       # the instance number of sampling in an iteration
        self.__MaxIteration = 0     # the number of iterations
        self.__Budget = 0           # budget of evaluation
        self.__PositiveNum = 0      # the set size of PosPop
        self.__RandProbability = 0  # the probability of sample in model
        self.__UncertainBits = 0    # the dimension size that is sampled randomly
        self.__OnlineSwitch = False
        self.__dimension = dim

        for i in range(dim.getSize()):
            reg = []
            reg.append(0)
            reg.append(0)
            self.__region.append(reg)
            self.__label.append(True)

        self.__ro = RandomOperator()
        return


    def OnlineTurnOn(self):
        self.__OnlineSwitch = True

    def OnlineTurnOff(self):
        self.__OnlineSwitch = False

    def Clear(self):
        self.__Pop = []
        self.__PosPop = []
        self.__Optimal = []
        self.__NextPop = []
        return

    # Parameters setting
    def setParameters(self, ss, mt, pn, rp, ub):
        self.__SampleSize = ss
        if self.__OnlineSwitch is False:
            self.__MaxIteration = mt
        else:
            self.__Budget = mt
        self.__PositiveNum = pn
        self.__RandProbability = rp
        self.__UncertainBits = ub
        return
    # -------------------------------------------------------
    # some test function
    def ShowPop(self, fea):
        print '----Pop----'
        for i in range(self.__SampleSize):
            if fea is True:
                print self.__Pop[i].getFeatures(), ':', self.__Pop[i].getFitness()
            else:
                print 'fitness:', self.__Pop[i].getFitness()
        return

    def ShowNextPop(self, fea):
        print '----NextPop----'
        for i in range(self.__SampleSize):
            if fea is True:
                print self.__NextPop[i].getFeatures(), ':', self.__NextPop[i].getFitness()
            else:
                print 'fitness:', self.__NextPop[i].getFitness()
        return

    def ShowPosPop(self, fea):
        print '----PosPop----'
        for i in range(self.__PositiveNum):
            if fea is True:
                print self.__PosPop[i].getFeatures(), ':', self.__PosPop[i].getFitness()
            else:
                print 'fitness:', self.__PosPop[i].getFitness()
        return

    def ShowRegion(self):
        print '----Region----'
        for i in range(self.__dimension.getSize):
            print 'dimension',i,'[', self.__region[i][0],',',self.__region[i][1],']'
        return

    def ShowLabel(self):
        print self.__label
        return
    # test function end
    # ----------------------------------------------------------------

    # Return optimal
    def getOptimal(self):
        return self.__Optimal


    # Generate an instance randomly
    def RandomInstance(self, dim, region, label):
        inst = Instance(dim)
        for i in range(dim.getSize()):
            if label[i] is True:
                if dim.getType(i) is True:
                    inst.setFeature(i, self.__ro.getUniformDouble(region[i][0], region[i][1]))
                else:
                    inst.setFeature(i, self.__ro.getUniformInteger(region[i][0], region[i][1]))
        return inst

    # generate an instance randomly
    def PosRandomInstance(self, dim, region, label, pos):
        ins = Instance(dim)
        for i in range(dim.getSize()):
            if label[i] is False:
                if dim.getType(i) is True:
                    ins.setFeature(i, self.__ro.getUniformDouble(region[i][0], region[i][1]))
                else:
                    ins.setFeature(i, self.__ro.getUniformInteger(region[i][0], region[i][1]))
            else:
                ins.setFeature(i, pos.getFeature(i))
        return ins

    # reset model
    def ResetModel(self):
        for i in range(self.__dimension.getSize()):
            self.__region[i][0] = self.__dimension.getRegion(i)[0]
            self.__region[i][1] = self.__dimension.getRegion(i)[1]
            self.__label[i] = True
        return

    # If an instance exists in list which is as same as ins, return True
    def InstanceInList(self, ins, list, end):
        for i in range(len(list)):
            if i == end:
                break
            if ins.Equal(list[i]) == True:
                return True
        return False

    # Initialize Pop, PosPop and Optimal
    def Initialize(self, func):
        temp = []

        # sample in original region under uniform distribution
        self.ResetModel()

        for i in range(self.__SampleSize+self.__PositiveNum):
            ins = []
            while(True):
                ins = self.RandomInstance(self.__dimension, self.__region, self.__label)
                if self.InstanceInList(ins, temp, i) is False:
                    break
            ins.setFitness(func(ins.getFeatures()))
            temp.append(ins)
        # sorted by fitness
        temp.sort(key=lambda instance: instance.getFitness())
        # initialize PosPop and Pop
        i = 0
        while(i<self.__PositiveNum):
            self.__PosPop.append(temp[i])
            i += 1
        while(i<self.__PositiveNum+self.__SampleSize):
            self.__Pop.append(temp[i])
            i += 1
        # initialize optimal
        self.__Optimal = self.__PosPop[0].CopyInstance()
        return

    # Generate model for sample next instance
    def ContinueShrinkModel(self, ins):
        ins_left = self.__SampleSize
        while(ins_left > 0):
            ChosenNeg = self.__ro.getUniformInteger(0, ins_left-1)
            ChosenDim = self.__ro.getUniformInteger(0, self.__dimension.getSize()-1)
            #shrinking
            if (ins.getFeature(ChosenDim) < self.__Pop[ChosenNeg].getFeature(ChosenDim)):
                btemp = self.__ro.getUniformDouble(ins.getFeature(ChosenDim), self.__Pop[ChosenNeg].getFeature(ChosenDim))
                if(btemp < self.__region[ChosenDim][1]):
                    self.__region[ChosenDim][1] = btemp
                    i = 0
                    while(i < ins_left):
                        if self.__Pop[i].getFeature(ChosenDim) >= btemp:
                            ins_left = ins_left - 1
                            itemp = self.__Pop[i]
                            self.__Pop[i] = self.__Pop[ins_left]
                            self.__Pop[ins_left] = itemp
                        else:
                            i += 1
            else:
                btemp = self.__ro.getUniformDouble(self.__Pop[ChosenNeg].getFeature(ChosenDim), ins.getFeature(ChosenDim))
                if (btemp > self.__region[ChosenDim][0]):
                    self.__region[ChosenDim][0] = btemp
                    i = 0
                    while(i < ins_left):
                        if self.__Pop[i].getFeature(ChosenDim) <= btemp:
                            ins_left = ins_left - 1
                            itemp = self.__Pop[i]
                            self.__Pop[i] = self.__Pop[ins_left]
                            self.__Pop[ins_left] = itemp
                        else:
                            i += 1

        return

    # Set uncertain bits
    def setUncertainBits(self):
        temp = []
        for i in range(self.__dimension.getSize()):
            temp.append(i)
        for i in range(self.__UncertainBits):
            index = self.__ro.getUniformInteger(0, self.__dimension.getSize()-i-1)
            self.__label[temp[index]] = False
            temp.remove(temp[index])
        return

    # Update PosPop list according to new Pop list generated latterly
    def UpdatePosPop(self):
        for i in range(self.__SampleSize):
            j = 0
            while(j<self.__PositiveNum):
                if(self.__Pop[i].getFitness()<self.__PosPop[j].getFitness()):
                    break
                else:
                    j += 1
            if(j < self.__PositiveNum):
                temp = self.__Pop[i]
                self.__Pop[i] = self.__PosPop[self.__PositiveNum-1]
                k = self.__PositiveNum-1
                while(k > j):
                    self.__PosPop[k] = self.__PosPop[k-1]
                    k -= 1
                self.__PosPop[j] = temp
        return

    def OnlineUpdate(self, ins):
        j = 0
        while (j < self.__PositiveNum):
            if (ins.getFitness() < self.__PosPop[j].getFitness()):
                break
            else:
                j += 1
        if (j < self.__PositiveNum):
            temp = ins
            ins = self.__PosPop[self.__PositiveNum - 1]
            k = self.__PositiveNum - 1
            while (k > j):
                self.__PosPop[k] = self.__PosPop[k - 1]
                k -= 1
            self.__PosPop[j] = temp

        j = 0
        while (j < self.__SampleSize):
            if (ins.getFitness() < self.__Pop[j].getFitness()):
                break
            else:
                j += 1
        if (j < self.__SampleSize):
            temp = ins
            ins = self.__Pop[self.__SampleSize - 1]
            k = self.__SampleSize - 1
            while (k > j):
                self.__Pop[k] = self.__Pop[k - 1]
                k -= 1
            self.__Pop[j] = temp


    # Update Optimal
    def UpdateOptimal(self):
        if(self.__Optimal.getFitness() > self.__PosPop[0].getFitness()):
            self.__Optimal = self.__PosPop[0].CopyInstance()
        return

    # If instances in Pop list are not in model, return True
    def Distinguish(self):
        for i in range(self.__SampleSize):
            j = 0
            while(j < self.__dimension.getSize()):
                if (self.__Pop[i].getFeature(j) > self.__region[j][0]) and (self.__Pop[i].getFeature(j) < self.__region[j][1]):
                    j += 1
                else:
                    break
            if (j == self.__dimension.getSize()):
                return False
        return True

    '''
    Racos for continue optimization
    param:
        func: objective function name
        ss:   sample size
        mt:   max iteration size
        pn:   positive instance size
        rp:   the probability of sampling in model randomly
        ub:   uncertain bits
    '''
    def ContinueOpt(self, func, ss, mt, pn, rp, ub):

        self.Clear()
        self.setParameters(ss, mt, pn, rp, ub)
        self.ResetModel()
        self.Initialize(func)

        if self.__OnlineSwitch is False:
            # no online style
            for itera in range(self.__MaxIteration-1):

                self.__NextPop = []
                for sam in range(self.__SampleSize):
                    while(True):
                        self.ResetModel()
                        ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum-1)
                        Gsample = self.__ro.getUniformDouble(0, 1)
                        if(Gsample <= self.__RandProbability):
                            self.ContinueShrinkModel(self.__PosPop[ChosenPos])
                            self.setUncertainBits()
                        ins = self.PosRandomInstance(self.__dimension, self.__region, self.__label, self.__PosPop[ChosenPos])
                        if((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (self.InstanceInList(ins, self.__NextPop, sam) is False)):
                            ins.setFitness(func(ins.getFeatures()))
                            break
                    self.__NextPop.append(ins)
                self.__Pop = []
                for i in range(self.__SampleSize):
                    self.__Pop.append(self.__NextPop[i])

                self.UpdatePosPop()
                self.UpdateOptimal()
        else:
            #Online style
            BudCount = self.__SampleSize+self.__PositiveNum
            while BudCount < self.__Budget:
        #        print BudCount, self.__Optimal.getFitness()
                BudCount += 1
                while (True):
                    self.ResetModel()
                    ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum - 1)
                    Gsample = self.__ro.getUniformDouble(0, 1)
                    if (Gsample <= self.__RandProbability):
                        self.ContinueShrinkModel(self.__PosPop[ChosenPos])
                        self.setUncertainBits()
                    ins = self.PosRandomInstance(self.__dimension, self.__region, self.__label,
                                                 self.__PosPop[ChosenPos])
                    if ((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (
                        self.InstanceInList(ins, self.__Pop, self.__SampleSize) is False)):
                        ins.setFitness(func(ins.getFeatures()))
                        break

                self.OnlineUpdate(ins)
                self.UpdateOptimal()


        return

    # Distinguish function for discrete optimization
    def DiscreteDistinguish(self, ins, ChosenDim):

        if len(ChosenDim) is 0:
            return 0

        for i in range(self.__SampleSize):
            j = 0
            while j < len(ChosenDim):
                if ins.getFeature(ChosenDim[j]) != self.__Pop[i].getFeature(ChosenDim[j]):
                    break
                j = j+1
            if j == len(ChosenDim):
                return 0
        return 1

    # PosRandomInstance function for discrete optimization
    def PosRandomDiscreteInstance(self, PosIns, dim, label):
        ins = Instance(dim)

        for i in range(dim.getSize()):
            if label[i] is True:
                ins.setFeature(i, PosIns.getFeature(i))
            else:
                ins.setFeature(i, self.__ro.getUniformInteger(dim.getRegion(i)[0], dim.getRegion(i)[1]))
        return ins

    # ShringModel function for discrete
    def DiscreteShrinkModel(self, ins, dim):


        NonChosenDim = []
        for i in range(dim.getSize()):
            NonChosenDim.append(i)
        ChosenDim = []

        while self.DiscreteDistinguish(ins, ChosenDim) == 0:
            tempDim = NonChosenDim[self.__ro.getUniformInteger(0, len(NonChosenDim)-1)]
            ChosenDim.append(tempDim)
            NonChosenDim.remove(tempDim)

        while len(NonChosenDim) > self.__UncertainBits:
            tempDim = NonChosenDim[self.__ro.getUniformInteger(0, len(NonChosenDim) - 1)]
            ChosenDim.append(tempDim)
            NonChosenDim.remove(tempDim)

        return NonChosenDim


    '''
    RACOS for discrete optimization
    param:
        func: objective function name
        ss:   sample size
        mt:   max iteration size
        pn:   positive instance size
        rp:   the probability of sampling in model randomly
        ub:   uncertain bits
    '''
    def DiscreteOpt(self, func, ss, mt, pn, rp, ub):
        self.Clear()
        self.setParameters(ss, mt, pn, rp, ub)
        self.ResetModel()
        self.Initialize(func)

        if self.__OnlineSwitch is False:
            for itera in range(self.__MaxIteration - 1):

                self.__NextPop = []

                for sam in range(self.__SampleSize):
                    while (True):
                        self.ResetModel()
                        ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum - 1)
                        Gsample = self.__ro.getUniformDouble(0, 1)
                        if (Gsample <= self.__RandProbability):
                            NonChosenDim = self.DiscreteShrinkModel(self.__PosPop[ChosenPos], self.__dimension)
                            for i in range(len(NonChosenDim)):
                                self.__label[NonChosenDim[i]] = False
                        ins = self.PosRandomDiscreteInstance(self.__PosPop[ChosenPos], self.__dimension, self.__label)
                        if ((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (
                            self.InstanceInList(ins, self.__NextPop, sam) is False)):
                            ins.setFitness(func(ins.getFeatures()))
                            break
                    self.__NextPop.append(ins)
                self.__Pop = []
                for i in range(self.__SampleSize):
                    self.__Pop.append(self.__NextPop[i])

                self.UpdatePosPop()
                self.UpdateOptimal()
        else:
            BudCount = self.__SampleSize + self.__PositiveNum
            while BudCount < self.__Budget:
                print BudCount, self.__Optimal.getFitness()
                BudCount += 1
                while (True):
                    self.ResetModel()
                    ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum - 1)
                    Gsample = self.__ro.getUniformDouble(0, 1)
                    if (Gsample <= self.__RandProbability):
                        NonChosenDim = self.DiscreteShrinkModel(self.__PosPop[ChosenPos], self.__dimension)
                        for i in range(len(NonChosenDim)):
                            self.__label[NonChosenDim[i]] = False
                    ins = self.PosRandomDiscreteInstance(self.__PosPop[ChosenPos], self.__dimension, self.__label)
                    if ((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (
                                self.InstanceInList(ins, self.__Pop, self.__SampleSize) is False)):
                        ins.setFitness(func(ins.getFeatures()))
                        break

                self.OnlineUpdate(ins)
                self.UpdateOptimal()
        return

    # Distinguish function for mixed optimization
    def MixDistinguish(self, ins):
        for i in range(self.__SampleSize):
            j = 0
            while j < self.__dimension.getSize():
                if self.__dimension.getType(j) is True:
                    if self.__Pop[i].getFeature(j) < self.__region[j][0] or self.__Pop[i].getFeature(j) > self.__region[j][1]:
                        break
                else:
                    if self.__label[j] is False and ins.getFeature(j) != self.__Pop[i].getFeature(j):
                        break
                j += 1
            if j >= self.__dimension.getSize():
                return False
        return True

    # PosRandomInstance for mixed optimization
    def PosRandomMixInstance(self, PosIns, dim, regi, lab):
        ins = Instance(dim)
        for i in range(dim.getSize()):
            if lab[i] is False:
                ins.setFeature(i, PosIns.getFeature(i))
            else:
                if dim.getType(i) is True: #continue
                    ins.setFeature(i, self.__ro.getUniformDouble(regi[i][0], regi[i][1]))
                else: #discrete
                    ins.setFeature(i, self.__ro.getUniformInteger(dim.getRegion(i)[0], dim.getRegion(i)[1]))
        return ins

    # ShrinkModel function for mixed optimization
    def MixShrinkModel(self, ins):

        ChosenDim = []
        NonChosenDim = []
        for i in range(self.__dimension.getSize()):
            NonChosenDim.append(i)

        count = 0
        while self.MixDistinguish(ins) is False:
            TempDim = NonChosenDim[self.__ro.getUniformInteger(0, len(NonChosenDim)-1)]
            ChosenNeg = self.__ro.getUniformInteger(0, self.__SampleSize-1)
            if self.__dimension.getType(TempDim) is True:  #continue
                if ins.getFeature(TempDim) < self.__Pop[ChosenNeg].getFeature(TempDim):
                    btemp = self.__ro.getUniformDouble(ins.getFeature(TempDim), self.__Pop[ChosenNeg].getFeature(TempDim))
                    if btemp < self.__region[TempDim][1]:
                        self.__region[TempDim][1] = btemp
                else:
                    btemp = self.__ro.getUniformDouble(self.__Pop[ChosenNeg].getFeature(TempDim), ins.getFeature(TempDim))
                    if btemp > self.__region[TempDim][0]:
                        self.__region[TempDim][0] = btemp
            else:
                ChosenDim.append(TempDim)
                NonChosenDim.remove(TempDim)
                self.__label[TempDim] = False

            count += 1

        while len(NonChosenDim) > self.__UncertainBits:
            TempDim = NonChosenDim[self.__ro.getUniformInteger(0, len(NonChosenDim) - 1)]
            ChosenDim.append(TempDim)
            NonChosenDim.remove(TempDim)
            self.__label[TempDim] = False

        return

    '''
    RACOS for mixed optimization
    param:
        func: objective function name
        ss:   sample size
        mt:   max iteration size
        pn:   positive instance size
        rp:   the probability of sampling in model randomly
        ub:   uncertain bits
    '''
    def MixOpt(self, func, ss, mt, pn, rp, ub):
        self.Clear()
        self.setParameters(ss, mt, pn, rp, ub)
        self.ResetModel()
        self.Initialize(func)

        if self.__OnlineSwitch is False:

            for itera in range(self.__MaxIteration - 1):

                self.__NextPop = []

                # self.ShowPosPop(True)
                # self.ShowPop(True)

                for sam in range(self.__SampleSize):
                    while (True):
                        self.ResetModel()
                        ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum - 1)
                        Gsample = self.__ro.getUniformDouble(0, 1)
                        if (Gsample <= self.__RandProbability):
                            # print 'begin shrinking!'
                            self.MixShrinkModel(self.__PosPop[ChosenPos])
                        ins = self.PosRandomMixInstance(self.__PosPop[ChosenPos], self.__dimension, self.__region,
                                                        self.__label)
                        if ((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (
                                    self.InstanceInList(ins, self.__NextPop, sam) is False)):
                            ins.setFitness(func(ins.getFeatures()))
                            break
                    self.__NextPop.append(ins)
                self.__Pop = []
                for i in range(self.__SampleSize):
                    self.__Pop.append(self.__NextPop[i])

                self.UpdatePosPop()
                self.UpdateOptimal()
        else:
            #online style
            BudCount = self.__SampleSize + self.__PositiveNum
            while BudCount < self.__Budget:
                print BudCount, self.__Optimal.getFitness()
                BudCount += 1
                while (True):
                    self.ResetModel()
                    ChosenPos = self.__ro.getUniformInteger(0, self.__PositiveNum - 1)
                    Gsample = self.__ro.getUniformDouble(0, 1)
                    if (Gsample <= self.__RandProbability):
                        # print 'begin shrinking!'
                        self.MixShrinkModel(self.__PosPop[ChosenPos])
                    ins = self.PosRandomMixInstance(self.__PosPop[ChosenPos], self.__dimension, self.__region,
                                                    self.__label)
                    if ((self.InstanceInList(ins, self.__PosPop, self.__PositiveNum) is False) and (
                                self.InstanceInList(ins, self.__Pop, self.__SampleSize) is False)):
                        ins.setFitness(func(ins.getFeatures()))
                        break
                self.OnlineUpdate(ins)
                self.UpdateOptimal()

        return