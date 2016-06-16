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
Description: some tools used in Racos
**************************************************/

#include<stdio.h>
#include<stdlib.h>
#include<time.h>

class RandomOperator {
public:
	RandomOperator();
	double getDouble(double, double);	//random a double in [l,u]
	int getInteger(int, int);			//random a integer in [l,u]
};

/*
 *Summary: constructor
 *Parameters: null
 *Return: null
 */
RandomOperator::RandomOperator() {
	srand((int)time(0));
}

/*
 *Summary: get double randomly
 *Parameters:
 *	l: lower bound
 *	u: upper bound
 *Return: a double number
 */
double RandomOperator::getDouble(double l, double u) {
	double ran = ((double)rand() / RAND_MAX)*(u - l) + l;
	return ran;
}

/*
 *Summary: get integer randomly
 *Parameters: 
 *	l: lower bound
 *	u: upper bound
 *Return: an integer
 */
int RandomOperator::getInteger(int l, int u) {
	int ran = rand() % (u - l + 1) + l;
	return ran;
}