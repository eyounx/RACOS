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
Description: demos of RACOS optimization
**************************************************/

#include "stdafx.h"
#include <iostream>
#include "Tools.h"
#include "ObjectiveFunctions.h"
#include "RacosOptimization.h"

using namespace std;


int main()
{
	//parameters setting
	int samplesize = 5;				//sample size
	int iteration = 30;				//number of iteration
	int budget = 150;				//budget of evaluation used in online version
	int positivenum = 2;			//number of positive instance
	double probability = 0.95;		//probability of sampling in model
	int uncertainbit = 3;			//number of dimensions that are obtained randomly

	//continue optimization
	if (true) {
		int dimension_size = 10;
		double lower = -1;
		double upper = 1;

		//dimension setting
		Dimension dimension;
		dimension.setSize(dimension_size);
		dimension.setDimensions(lower, upper, true);

		Racos racos(dimension, &Sphere);	//initialize class Racos by dimension and task

		//online switch is off in default situation
		//turn on switch of online version optimization
//		racos.OnlineTurnOn();
//		racos.setParameters(samplesize, budget, positivenum, probability, uncertainbit);		//online version parameters setting
		racos.setParameters(samplesize, iteration, positivenum, probability, uncertainbit);		//parameters setting

		Instance optimal = racos.Continue();	//run Racos

		cout << "optimal:" << optimal.getFitness() << endl;
		optimal.PrintIns();
	}

	//discrete optimization
	if (false) {
		int dimension_size = 20;
		double lower = 0;
		double upper = 1;

		//dimension setting
		Dimension dimension;
		dimension.setSize(dimension_size);
		dimension.setDimensions(lower, upper, false);

		Racos racos(dimension, &SetCover);	//initialize class Racos by dimension and task

		//online switch is off in default situation
		//turn on switch of online version optimization
//		racos.OnlineTurnOn();
//		racos.setParameters(samplesize, budget, positivenum, probability, uncertainbit);		//online version parameters setting

		racos.setParameters(samplesize, iteration, positivenum, probability, uncertainbit);  //parameters setting

		Instance optimal = racos.Discrete();	//run Racos

		cout << "optimal:" << optimal.getFitness() << endl;
		optimal.PrintIns();
	}

	//mixed optimization
	if (false) {
		int dimension_size = 10;
		
		//dimension setting
		Dimension dimension;
		dimension.setSize(dimension_size);
		for (int i = 0; i < dimension_size; i++) {
			if (i % 2 == 0)
				dimension.setDimension(i, -1, 1, true);
			else
				dimension.setDimension(i, 0, 100, false);
		}

		Racos racos(dimension, &MixedFunction);	//initialize class Racos by dimension and task

		//online switch is off in default situation
		//turn on switch of online version optimization
//		racos.OnlineTurnOn();
//		racos.setParameters(samplesize, budget, positivenum, probability, uncertainbit);		//online version parameters setting

		racos.setParameters(samplesize, budget, positivenum, probability, uncertainbit);	//parameters setting

		Instance optimal = racos.Mix();	//run Racos

		cout << "optimal:" << optimal.getFitness() << endl;
		optimal.PrintIns();
	}
	

	system("pause");

    return 0;
}

