'''
Some necessary classes were implemented in this file

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

#Instance class, each sample is an instance
class Instance:

    __feature = []        #feature value in each dimension
    __fitness = 0         #fitness of objective function under those features

    def __init__(self, dim):
        self.__feature = []
        for i in range(dim.getSize()):
            self.__feature.append(0)
        self.__fitness = 0
        self.__dimension = dim

    #return feature value in index-th dimension
    def getFeature(self, index):
        return self.__feature[index]

    #return features of all dimensions
    def getFeatures(self):
        return self.__feature

    #set feature value in index-th dimension
    def setFeature(self, index, v):
        self.__feature[index] = v

    #set features of all dimension
    def setFeatures(self, v):
        self.__feature = v

    #return fitness under those features
    def getFitness(self):
        return self.__fitness

    #set fitness
    def setFitness(self, fit):
        self.__fitness = fit

    #
    def Equal(self, ins):
        if len(self.__feature) != len(ins.__feature):
            return False
        for i in range(len(self.__feature)):
            if self.__feature[i] != ins.__feature[i]:
                return False
        return True

    #copy this instance
    def CopyInstance(self):
        copy = Instance(self.__dimension)
        for i in range(len(self.__feature)):
            copy.setFeature(i, self.__feature[i])
        copy.setFitness(self.__fitness)
        return copy


#Dimension class
#dimension message
class Dimension:

    __size = 0       #dimension size
    __region = []    #lower and upper bound in each dimension
    __type = []      #the type of each dimension

    def __init__(self):
        return

    def setDimensionSize(self, s):
        self.__size = s
        self.__region = []
        self.__type = []

        for i in range(s):
            ori_reg = []
            ori_reg.append(0)
            ori_reg.append(0)
            self.__region.append(ori_reg)
            self.__type.append(True)
        return

    #set index-th dimension region
    def setRegion(self, index, reg, ty):
        self.__region[index][0] = reg[0]
        self.__region[index][1] = reg[1]
        self.__type[index] = ty
        return

    def setRegions(self, regs, tys):
        for i in range(self.__size):
            self.__region[i][0] = regs[i][0]
            self.__region[i][1] = regs[i][1]
            self.__type[i] = tys[i]
        return

    def getSize(self):
        return self.__size

    def getRegion(self, index):
        return self.__region[index]

    def getRegions(self):
        return self.__region

    def getType(self, index):
        return self.__type[index]