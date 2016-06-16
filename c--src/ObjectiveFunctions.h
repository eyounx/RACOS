#pragma once
/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2015 Nanjing University, Nanjing, China
 */
/*************************************************
Copyright:
Author: Yi-Qi Hu
Date: 2016-6-15
Description: some objective functions are implemented in this file
**************************************************/

#include <iostream>
#include <stdlib.h>
#include "Components.h"

/*
 *Summary: Sphere function for continuous optimization
 *Parameters:
 *	ins: an sample
 *Return: fitness value
 */
double Sphere(Instance& ins) {
	double sum = 0;
	double fea;
	int size = ins.getSize();
	for (int i = 0; i < size; i++) {
		fea = ins.getFeature(i) - 0.2;
		sum += fea*fea;
	}
	return sum;
}

/*
 *Summary: Set cover problem for discrete optimization
 *Parameters:
 *	ins: an sample
 *Return: fitness value
 */
double SetCover(Instance& ins) {
	double weight[20] = { 0.8356,0.5495,0.4444,0.7269,0.9960,0.6633,0.5062,0.8429,0.1293,0.7355,0.7979,0.2814,0.7962,0.1754,0.0267,0.9862,0.1786,0.5884,0.6289,0.3008 };
	int subset[20][30] = { { 0,1,0,0,0,1,0,1,0,0,1,1,0,0,1,1,1,0,1,0,0,1,1,0,1,0,0,1,0,0 },
						   { 0,0,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,0,0,0,1,0,1,0,1,1,1,1,0,0 },
						   { 1,0,1,0,0,0,1,0,1,1,0,0,1,0,0,0,0,1,1,1,1,0,1,1,1,1,1,0,0,0 },
						   { 0,0,1,1,0,1,1,1,0,0,1,1,0,0,1,1,1,1,1,0,0,1,0,0,1,0,0,0,1,0 },
						   { 1,1,1,0,1,1,0,0,0,0,1,0,0,0,0,1,0,1,1,1,1,0,0,1,0,0,1,1,1,1 },
						   { 0,0,1,1,0,1,1,1,0,0,1,1,1,1,1,1,1,0,1,1,1,0,0,1,0,0,0,0,0,0 },
						   { 0,1,0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,1,0,1,1,1,1,0,0 },
						   { 0,0,1,0,0,0,0,1,1,0,1,0,0,1,1,1,1,1,0,1,0,1,1,0,1,1,1,0,0,0 },
						   { 0,0,1,1,0,1,0,1,0,1,1,0,1,1,1,0,0,1,0,0,1,1,0,1,0,0,0,0,1,0 },
						   { 0,1,1,1,0,0,1,0,1,0,1,0,1,1,1,0,1,0,0,0,1,1,0,0,0,1,1,0,0,1 },
						   { 0,0,1,1,1,0,1,1,0,0,1,1,1,1,1,0,0,0,1,1,0,0,0,1,0,1,0,1,0,0 },
						   { 0,0,1,0,0,1,0,0,0,0,1,1,0,1,1,1,0,0,1,1,0,1,1,1,1,0,0,0,1,1 },
						   { 1,0,0,0,1,1,0,1,1,1,1,0,1,0,0,1,0,1,1,1,0,0,1,1,0,0,0,1,1,1 },
						   { 1,0,0,1,0,1,1,1,1,1,1,1,1,1,0,0,1,0,0,1,1,1,1,0,1,0,1,0,0,1 },
						   { 0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,1,0,1,0,0,0,0,0,0,1,0,0,1,0,1 },
						   { 1,0,0,0,1,0,0,1,0,0,0,1,1,1,1,0,1,0,1,1,0,1,0,0,0,1,0,1,1,0 },
						   { 1,0,0,0,1,0,0,1,0,1,0,0,1,0,1,1,1,1,1,1,0,1,0,1,0,0,0,1,0,1 },
						   { 0,1,1,0,1,1,1,1,0,1,0,1,0,0,0,0,0,1,1,0,1,1,1,1,1,0,0,0,0,1 },
						   { 0,1,1,0,1,1,0,0,0,1,1,0,1,1,0,0,1,1,0,0,0,0,1,0,0,0,0,1,1,0 },
						   { 0,0,1,1,1,1,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,1,0,0,0,1,0,0,1,1 } };
	double allweight = 0;
	for (int i = 0; i < 20; i++) {
		allweight += weight[i];
	}

	double countw = 0;

	int dims[30] = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };

	for (int i = 0; i < ins.getSize(); i++) {
		if (ins.getFeature(i) == 1) {
			countw += weight[i];
			for (int j = 0; j < 30; j++){
				if (subset[i][j] == 1) {
					dims[j] = 1;
				}
			}
		}
	}

	bool full = true;

	for (int i = 0; i < 30; i++) {
		if (dims[i] == 0) {
			full = false;
			break;
		}
	}

	if (full)
		return countw;
	else 
		return allweight + countw;
}

/*
 *Summary: a function for discrete optimization
 *Parameters:
 *	ins: an sample
 *Return: fitness value
 */
double MixedFunction(Instance& ins) {
	double sum = 0;
	for (int i = 0; i < ins.getSize(); i++) {
		sum += ins.getFeature(i)*ins.getFeature(i);
	}
	return sum;
}