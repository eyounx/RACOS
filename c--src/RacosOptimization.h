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
Description: Racos
**************************************************/

#include <iostream>
#include "Components.h"
#include "Tools.h"
using namespace std;

class Racos {

private:
	//parameters
	int SampleSize;			//number of instances in each iteration
	int MaxIteration;		//iteration size
	int Budget;				//budget of evaluation
	int PositiveNum;		//number of positive instance 
	double Probability;		//the probability of sampling in hyposith
	int Uncertainbits;		//number of uncertainbits
	bool OnlineSwitch;		//online version switch

	//algorithm
	RandomOperator ro;				//random tool
	double(*task)(Instance& ins);	//task
	Dimension dimension;			//dimension information
	Instance *Population;			//sample set
	int *Pop;						//negative sample index set
	int *NextPop;					//sample index set in next iteration
	int *PosPop;					//positive sample index set
	Instance Optimal;				//best sample so far
	double **region;				//region in model
	bool *label;					//random label in model

protected:
	void RandomInstance(Instance*);
	void RandomInstance(Instance*, Instance&);
	void Initialize();
	void ResetModel();
	void ContinueShrinkModel(int);
	void DiscreteShrinkModel(int);
	void MixedShrinkModel(int);
	bool DiscreteDistinguish(int);
	bool MixedDistinguish(int, bool);
	void setRandomBit();
	bool InstanceInModel(Instance);
	bool notExistInstance(Instance&, int);
	bool notExistInstanceInInit(Instance&, int);
	bool notExistInstanceForOnline(Instance&);
	void UpdatePosPop();
	void UpdateOptimal();
	int OnlineUpdate(int);

public:
	Racos(Dimension&, double(*)(Instance&));
	~Racos();
	void setParameters(int, int, int, double, int);
	void setDimension(Dimension);
	void setTask(double(*)(Instance&));
	Instance Continue();
	Instance Discrete();
	Instance Mix();
	void OnlineTurnOn();
	void OnlineTurnOff();
};

/*
 *Summary: constructor
 *Parameters:
 *	dim: dimensiom
 *	t: point of task function
 *Return: null
 */
Racos::Racos(Dimension& dim, double(*t)(Instance& ins)) {
	dimension.setSize(dim.getSize());
	for (int i = 0; i < dimension.getSize(); i++) {
		dimension.setDimension(i, dim.getLowBound(i), dim.getUpBound(i), dim.getType(i));
	}
	task = t;
	region = new double*[dimension.getSize()];
	label = new bool[dimension.getSize()];
	for (int i = 0; i < dimension.getSize(); i++) {
		region[i] = new double[2];
		region[i][0] = 0;
		region[i][1] = 1;
		label[i] = false;
	}
	ro = RandomOperator();
	OnlineSwitch = false;
}

/*
 *Summary: distructor without parameters
 *Parameters: null
 *Return: null
 */
Racos::~Racos(){
	delete[] Population;
	delete[] Pop;
	delete[] PosPop;
	delete[] NextPop;
	delete[] label;
	for (int i = 0; i < dimension.getSize(); i++)
		delete[] region[i];
	delete region;
	return;
}

/*
 *Summary: parameters setting
 *Parameters:
 *	ss: sample size
 *	mi:	iteration size in normal version, budget in online version
 *	pn: positive size
 *	pro: probability
 *	ub: dimension number obtained randomly
 *Return: null
 */
void Racos::setParameters(int ss, int mi, int pn, double pro, int ub) {
	SampleSize = ss;
	if (OnlineSwitch)
		Budget = mi;
	else
		MaxIteration = mi;
	PositiveNum = pn;
	Probability = pro;
	Uncertainbits = ub;
}

/*
 *Summary: set dimension
 *Parameters:
 *	dim: dimension
 *Return: null
 */
void Racos::setDimension(Dimension dim) {
	dimension = dim;
	region = new double*[dimension.getSize()];
	label = new bool[dimension.getSize()];
	for (int i = 0; i < dimension.getSize(); i++) {
		region[i] = new double[2];
		region[i][0] = 0;
		region[i][1] = 1;
		label[i] = false;
	}
	return;
}

/*
 *Summary: set task
 *Parameters: 
 *	t: new task
 *Return: null
 */
void Racos::setTask(double(*t)(Instance& ins)) {
	task = t;
	return;
}

/*
 *Summary: generate a sample in initialization
 *Parameters:
 *	ins: sample
 *Return: null
 */
void Racos::RandomInstance(Instance *ins) {
	ins->setSize(dimension.getSize());
	for (int i = 0; i < ins->getSize(); i++) {
		if (dimension.getType(i))
			ins->setFeature(i, ro.getDouble(dimension.getLowBound(i), dimension.getUpBound(i)));
		else
			ins->setFeature(i, ro.getInteger((int)dimension.getLowBound(i), (int)dimension.getUpBound(i)));
	}
	ins->setFitness(0);
	return;
}
/*
 *Summary: generate a sample from model
 *Parameters:
 *	ins: sample
 *	pos: model
 *Return: null
 */
void Racos::RandomInstance(Instance *ins, Instance& pos) {
	for (int i = 0; i < ins->getSize(); i++) {
		if (label[i])
			ins->setFeature(i, pos.getFeature(i));
		else
			if (dimension.getType(i))
				ins->setFeature(i, ro.getDouble(region[i][0], region[i][1]));
			else
				ins->setFeature(i, ro.getInteger((int)dimension.getLowBound(i), (int)dimension.getUpBound(i)));
	}
	ins->setFitness(0);
	return;
}

/*
 *Summary: inialize population etc.
 *Parameters: null
 *Return: null
 */
void Racos::Initialize() {

	Population = new Instance[PositiveNum + SampleSize + SampleSize];	//Population saves all instances used in Racos, include PosPop, Pop, NextPop;
	PosPop = new int[PositiveNum];										//PopPop saves the index of positive instance in Population
	Pop = new int[SampleSize];											//Pop saves the index of negative instance in Population
	NextPop = new int[SampleSize];										//NextPop saves the index of new sampling instance in Population

	//sample Pop set and PosPop and evaluate those samples
	for (int i = 0; i < PositiveNum + SampleSize; i++) {
		bool resample = true;
		while (resample) {
			RandomInstance(&Population[i]);
			//avoid same samples in Population
			if (notExistInstanceInInit(Population[i], i)) {
				Population[i].setFitness(task(Population[i]));
				resample = false;
			}		
		}	
	}

	//sample NextPop set but not need to evaluate
	for (int i = 0; i < SampleSize; i++)
		RandomInstance(&Population[i + PositiveNum + SampleSize]);

	int *mark = new int[PositiveNum + SampleSize];
	for (int i = 0; i < PositiveNum + SampleSize; i++)
		mark[i] = i;

	//sorting
	int po_count = PositiveNum + SampleSize;
	while (po_count > 0) {
		int best_index = 0;
		for (int i = 0; i < po_count; i++) {
			if (Population[mark[best_index]].getFitness()>Population[mark[i]].getFitness())
				best_index = i;
		}
		po_count--;
		int tempi = mark[best_index];
		mark[best_index] = mark[po_count];	
		mark[po_count] = tempi;
	}

	//initialize PosPop
	for (int i = 0; i < PositiveNum; i++) {
		PosPop[i] = mark[PositiveNum + SampleSize - i - 1];
	}

	//initialize Pop
	for (int i = 0; i < SampleSize; i++)
		Pop[i] = mark[SampleSize - i - 1];

	//initializa NextPop
	for (int i = 0; i < SampleSize; i++)
		NextPop[i] = i + PositiveNum + SampleSize;

	//inialize optimal
	Optimal = Instance(dimension.getSize());
	Optimal.CopyInsatnce(Population[PosPop[0]]);

	return;
}

/*
 *Summary: reset model
 *Parameters: null
 *Return: null
 */
void Racos::ResetModel() {
	for (int i = 0; i < dimension.getSize(); i++) {
		region[i][0] = dimension.getLowBound(i);
		region[i][1] = dimension.getUpBound(i);
		label[i] = false;
	}
	return;
}

/*
 *Summary: generate model for continuous optimizaition
 *Parameters:
 *	pos_index: the index of positive sample
 *Return: null
 */
void Racos::ContinueShrinkModel(int pos_index) {
	int choosen_dim;
	int choosen_neg;
	double cut;

	int ins_left = SampleSize;
	while (ins_left > 0) {
		choosen_dim = ro.getInteger(0, dimension.getSize() - 1);
		choosen_neg = ro.getInteger(0, ins_left - 1);
		
		//shrinking model
		if (Population[PosPop[pos_index]].getFeature(choosen_dim) < Population[Pop[choosen_neg]].getFeature(choosen_dim)) {
			cut = ro.getDouble(Population[PosPop[pos_index]].getFeature(choosen_dim), Population[Pop[choosen_neg]].getFeature(choosen_dim));
			if (cut < region[choosen_dim][1]) {
				region[choosen_dim][1] = cut;
				int i = 0;
				while (i < ins_left) {
					if (Population[Pop[i]].getFeature(choosen_dim) >= cut) {
						ins_left--;
						int temp_index = Pop[i];
						Pop[i] = Pop[ins_left];
						Pop[ins_left] = temp_index;
					}
					else {
						i++;
					}
				}
			}
		}
		else {
			cut = ro.getDouble(Population[Pop[choosen_neg]].getFeature(choosen_dim), Population[PosPop[pos_index]].getFeature(choosen_dim));
			if (cut > region[choosen_dim][0]) {
				region[choosen_dim][0] = cut;
				int i = 0;
				while (i < ins_left) {
					if (Population[Pop[i]].getFeature(choosen_dim) <= cut) {
						ins_left--;
						int temp_index = Pop[i];
						Pop[i] = Pop[ins_left];
						Pop[ins_left] = temp_index;
					}
					else {
						i++;
					}
				}
			}
		}
	}
	return;
}

/*
 *Summary: generate model for discrete optimizaition
 *Parameters:
 *	pos_index: the index of positive sample
 *Return: null
 */
void Racos::DiscreteShrinkModel(int pos_index) {

	int* mark = new int[dimension.getSize()];

	for (int i = 0; i < dimension.getSize(); i++)
		mark[i] = i;

	int mark_left = dimension.getSize();

	while (!DiscreteDistinguish(pos_index)) {
		int temp_index = ro.getInteger(0, mark_left - 1);
		label[mark[temp_index]] = true;
		mark_left--;
		mark[temp_index] = mark[mark_left];
	}

	return ;
}

/*
 *Summary: generate model for mixed optimizaition
 *Parameters:
 *	pos_index: the index of positive sample
 *Return: null
 */
void Racos::MixedShrinkModel(int pos_index) {

	int rand_dim;
	int chosen_neg;
	double cut;

	int* ChosenDim;
	int dim_left;

	ChosenDim = new int[dimension.getSize()];
	for (int i = 0; i < dimension.getSize(); i++)
		ChosenDim[i] = i;
	dim_left = dimension.getSize();

	bool ori = true;
	while (!MixedDistinguish(pos_index, ori)) {
		ori = false;
		rand_dim = ro.getInteger(0, dim_left - 1);
		//continue
		if (dimension.getType(ChosenDim[rand_dim])) {
			chosen_neg = ro.getInteger(0, SampleSize - 1);
			if (Population[PosPop[pos_index]].getFeature(ChosenDim[rand_dim]) < Population[Pop[chosen_neg]].getFeature(ChosenDim[rand_dim])) {
				cut = ro.getDouble(Population[PosPop[pos_index]].getFeature(ChosenDim[rand_dim]), Population[Pop[chosen_neg]].getFeature(ChosenDim[rand_dim]));
				if (cut < region[ChosenDim[rand_dim]][1])
					region[ChosenDim[rand_dim]][1] = cut;
			}
			else {
				cut = ro.getDouble(Population[Pop[chosen_neg]].getFeature(ChosenDim[rand_dim]), Population[PosPop[pos_index]].getFeature(ChosenDim[rand_dim]));
				if (cut > region[ChosenDim[rand_dim]][0])
					region[ChosenDim[rand_dim]][0] = cut;
			}
		}
		else {//discrete
			label[ChosenDim[rand_dim]] = true;
			dim_left--;
			ChosenDim[rand_dim] = ChosenDim[dim_left];
		}
	}

	return;

}

/*
 *Summary: judge whether negative samples exist in model for discrete optimization
 *Parameters:
 *	pos_index: the index of positive sample
 *Return: null
 */
bool Racos::DiscreteDistinguish(int pos_index) {
	int i, j;
	for (i = 0; i < dimension.getSize(); i++) {
		if (label[i])
			break;
	}
	if (i == dimension.getSize())
		return false;

	for (i = 0; i < SampleSize; i++) {
		for (j = 0; j < dimension.getSize(); j++) {
			if (label[j] && Population[PosPop[pos_index]].getFeature(j) != Population[Pop[i]].getFeature(j))
				break;
		}
		if (j == dimension.getSize())
			return false;
	}
	return true;
}

/*
 *Summary: judge whether negative samples exist in model for mixed optimization
 *Parameters:
 *	pos_index: the index of positive sample
 *Return: null
 */
bool Racos::MixedDistinguish(int pos_index, bool ori) {

	if (ori)
		return false;

	for (int i = 0; i < SampleSize; i++) {
		int j;
		for (j = 0; j < dimension.getSize(); j++) {
			if (dimension.getType(j)) {
				if ((Population[Pop[i]].getFeature(j) < region[j][0]) || (Population[Pop[i]].getFeature(j) > region[j][1]))
					break;
			}
			else {
				if (label[j] && (Population[PosPop[pos_index]].getFeature(j) != Population[Pop[i]].getFeature(j)))
					break;
			}
		}
		if (j == dimension.getSize())
			return false;
	}

	return true;

}

/*
 *Summary: choose dimensions that should get randomly
 *Parameters: null
 *Return: null
 */
void Racos::setRandomBit() {
	int count = 0;
	for (int i = 0; i < dimension.getSize(); i++) {
		if (!label[i])
			count++;
	}
	int *mark = new int[count];
	int j = 0;
	for (int i = 0; i < dimension.getSize(); i++) {
		if (!label[i]) {
			mark[j] = i;
			j++;
		}
	}
	int next_index;
	while (count > Uncertainbits) {
		next_index = ro.getInteger(0, count - 1);
		label[mark[next_index]] = true;
		mark[next_index] = mark[count - 1];
		count--;
	}
	delete[] mark;
	return; 
}

/*
*Summary: judge whether samples exist in model
*Parameters:
*	ins: a sample
*Return: if this is no one return false
*/
bool Racos::InstanceInModel(Instance ins) {
	for (int i = 0; i < dimension.getSize(); i++) {
		if (ins.getFeature(i) < region[i][0] || ins.getFeature(i) > region[i][1])
			return false;
	}
	return true;
}

/*
 *Summary: judge whether a sample exists in population which is as same as this one in initialization
 *Parameters:
 *	ins: a sample
 *	end: end index
 *Return: if this is no one return false
 */
bool Racos::notExistInstanceInInit(Instance& ins, int end) {
	for (int i = 0; i < end; i++) {
		if (Population[i].Equal(ins))
			return false;
	}
	return true;
}

/*
 *Summary: judge whether a sample exists in population which is as same as this one in sampling
 *Parameters:
 *	ins: a sample
 *	end: end index
 *Return: if this is no one return false
 */
bool Racos::notExistInstance(Instance& ins, int end) {
	for (int i = 0; i < PositiveNum; i++) {
		if (Population[PosPop[i]].Equal(ins))
			return false;
	}
	for (int i = 0; i < SampleSize; i++) {
		if (Population[Pop[i]].Equal(ins))
			return false;
	}
	for (int i = 0; i < end; i++) {
		if (Population[NextPop[i]].Equal(ins))
			return false;
	}
	return true;
}

/*
 *Summary: judge whether a sample exists in population which is as same as this one for online version
 *Parameters:
 *	ins: a sample
 *Return: if this is no one return false
 */
bool Racos::notExistInstanceForOnline(Instance& ins) {
	for (int i = 0; i < PositiveNum; i++) {
		if (Population[PosPop[i]].Equal(ins))
			return false;
	}
	for (int i = 0; i < SampleSize; i++) {
		if (Population[Pop[i]].Equal(ins))
			return false;
	}
	return true;
}

/*
 *Summary: update PosPop set according to samples
 *Parameters: null
 *Return: null
 */
void Racos::UpdatePosPop() {
	for (int i = 0; i < SampleSize; i++) {
		int j;
		for (j = 0; j < PositiveNum; j++) {
			if (Population[PosPop[j]].getFitness()>Population[Pop[i]].getFitness())
				break;
		}
		int temp_ins;
		if (j < PositiveNum) {
			temp_ins = PosPop[PositiveNum - 1];
			for (int k = PositiveNum - 1; k > j; k--) {
				PosPop[k] = PosPop[k - 1];
			}
			PosPop[j] = Pop[i];
			Pop[i] = temp_ins;
		}
	}
	return;
}

/*
 *Summary: update Optimal
 *Parameters: null
 *Return: null
 */
void Racos::UpdateOptimal() {
	if (Optimal.getFitness() > Population[PosPop[0]].getFitness())
		Optimal.CopyInsatnce(Population[PosPop[0]]);
	return;
}

/*
 *Summary: Racos algorithm for continuous optimization
 *Parameters: null
 *Return: best sample so far
 */
Instance Racos::Continue() {
	int choosen_pos;
	double global_sample;
	bool resample;

	ResetModel();

	Initialize();

	if (!OnlineSwitch) {
		for (int itera = 0; itera < MaxIteration; itera++) {
			cout << "iteration" << itera << ":" << Optimal.getFitness() << endl;
			for (int sam = 0; sam < SampleSize; sam++) {
				resample = true;
				while (resample) {
					ResetModel();
					choosen_pos = ro.getInteger(0, PositiveNum - 1);
					global_sample = ro.getDouble(0, 1);
					if (global_sample < Probability) {
						ContinueShrinkModel(choosen_pos);
						setRandomBit();
					}
					RandomInstance(&Population[NextPop[sam]], Population[PosPop[choosen_pos]]);
					if (notExistInstance(Population[NextPop[sam]], sam)) {
						Population[NextPop[sam]].setFitness(task(Population[NextPop[sam]]));
						resample = false;
					}
				}
			}
			for (int i = 0; i < SampleSize; i++) {
				int temp_index;
				temp_index = Pop[i];
				Pop[i] = NextPop[i];
				NextPop[i] = temp_index;
			}

			UpdatePosPop();
			UpdateOptimal();
		}
	}
	else {
		int bud_count = SampleSize + PositiveNum;
		int new_index = NextPop[0];
		for (; bud_count < Budget; bud_count++) {

//			cout << "budget:" << bud_count << ":" << Optimal.getFitness() << endl;

			resample = true;
			while (resample) {
				ResetModel();
				choosen_pos = ro.getInteger(0, PositiveNum - 1);
				global_sample = ro.getDouble(0, 1);
				if (global_sample < Probability) {
					ContinueShrinkModel(choosen_pos);
					setRandomBit();
				}
				RandomInstance(&Population[new_index], Population[PosPop[choosen_pos]]);
				if (notExistInstanceForOnline(Population[new_index])) {
					Population[new_index].setFitness(task(Population[new_index]));
					resample = false;
				}
			}
			new_index = OnlineUpdate(new_index);
			UpdateOptimal();


		}
	}
	return Optimal;
}

/*
 *Summary: Racos algorithm for discrete optimization
 *Parameters: null
 *Return: best sample so far
 */
Instance Racos::Discrete() {

	int choosen_pos;
	double global_sample;
	bool resample;

	ResetModel();

	Initialize();

	if (!OnlineSwitch) {

		for (int itera = 0; itera < MaxIteration; itera++) {
			cout << "iteration" << itera << ":" << Optimal.getFitness() << endl;
			for (int sam = 0; sam < SampleSize; sam++) {
				resample = true;
				while (resample) {
					ResetModel();
					choosen_pos = ro.getInteger(0, PositiveNum - 1);
					global_sample = ro.getDouble(0, 1);
					if (global_sample < Probability) {
						DiscreteShrinkModel(choosen_pos);
						setRandomBit();
					}
					RandomInstance(&Population[NextPop[sam]], Population[PosPop[choosen_pos]]);
					if (notExistInstance(Population[NextPop[sam]], sam)) {
						Population[NextPop[sam]].setFitness(task(Population[NextPop[sam]]));
						resample = false;
					}
				}
			}
			for (int i = 0; i < SampleSize; i++) {
				int temp_index;
				temp_index = Pop[i];
				Pop[i] = NextPop[i];
				NextPop[i] = temp_index;
			}

			UpdatePosPop();
			UpdateOptimal();
		}
	}
	else {
		int bud_count = SampleSize + PositiveNum;
		int new_index = NextPop[0];
		for (; bud_count < Budget; bud_count++) {

//			cout << "budget:" << bud_count << ":" << Optimal.getFitness() << endl;

			resample = true;
			while (resample) {
				ResetModel();
				choosen_pos = ro.getInteger(0, PositiveNum - 1);
				global_sample = ro.getDouble(0, 1);
				if (global_sample < Probability) {
					DiscreteShrinkModel(choosen_pos);
					setRandomBit();
				}
				RandomInstance(&Population[new_index], Population[PosPop[choosen_pos]]);
				if (notExistInstanceForOnline(Population[new_index])) {
					Population[new_index].setFitness(task(Population[new_index]));
					resample = false;
				}
			}
			new_index = OnlineUpdate(new_index);
			UpdateOptimal();
		}
	}

	return Optimal;

}

/*
 *Summary: Racos algorithm for mixed optimization
 *Parameters: null
 *Return: best sample so far
 */
Instance Racos::Mix() {

	int choosen_pos;
	double global_sample;
	bool resample;

	ResetModel();

	Initialize();

	if (!OnlineSwitch) {

		for (int itera = 0; itera < MaxIteration; itera++) {

//			cout << "iteration" << itera << ":" << Optimal.getFitness() << endl;

			for (int sam = 0; sam < SampleSize; sam++) {
				resample = true;
				while (resample) {
					ResetModel();
					choosen_pos = ro.getInteger(0, PositiveNum - 1);
					global_sample = ro.getDouble(0, 1);
					if (global_sample < Probability) {
						MixedShrinkModel(choosen_pos);
						setRandomBit();
					}
					RandomInstance(&Population[NextPop[sam]], Population[PosPop[choosen_pos]]);
					if (notExistInstance(Population[NextPop[sam]], sam)) {
						Population[NextPop[sam]].setFitness(task(Population[NextPop[sam]]));
						resample = false;
					}
				}
			}
			for (int i = 0; i < SampleSize; i++) {
				int temp_index;
				temp_index = Pop[i];
				Pop[i] = NextPop[i];
				NextPop[i] = temp_index;
			}
			UpdatePosPop();
			UpdateOptimal();
		}
	}
	else {
		int bud_count = SampleSize + PositiveNum;
		int new_index = NextPop[0];
		for (; bud_count < Budget; bud_count++) {
			cout << "budget:" << bud_count << ":" << Optimal.getFitness() << endl;
			resample = true;
			while (resample) {
				ResetModel();
				choosen_pos = ro.getInteger(0, PositiveNum - 1);
				global_sample = ro.getDouble(0, 1);
				if (global_sample < Probability) {
					MixedShrinkModel(choosen_pos);
					setRandomBit();
				}
				RandomInstance(&Population[new_index], Population[PosPop[choosen_pos]]);
				if (notExistInstanceForOnline(Population[new_index])) {
					Population[new_index].setFitness(task(Population[new_index]));
					resample = false;
				}
			}

			new_index = OnlineUpdate(new_index);

			UpdateOptimal();


		}
	}

	return Optimal;
}

/* 
 *Summary: turn on online switch
 *Parameters: null
 *Return: null
 */
void Racos::OnlineTurnOn() {
	OnlineSwitch = true;
	return;
}

/*
 *Summary: turn off online switch
 *Parameters: null
 *Return: null
 */
void Racos::OnlineTurnOff() {
	OnlineSwitch = false;
	return;
}

/*
 *Summary: update PosPop and Pop set for online version
 *Parameters:
 *	new_index: the index of new sample
 *Return: null
 */
int Racos::OnlineUpdate(int new_index) {
	int i, j;
	for (i = 0; i < PositiveNum; i++)
		if (Population[new_index].getFitness() < Population[PosPop[i]].getFitness())
			break;
	if (i < PositiveNum) {
		int temp = new_index;
		new_index = PosPop[PositiveNum - 1];
		for (int k = PositiveNum - 1; k > i; k--) {
			PosPop[k] = PosPop[k - 1];
		}
		PosPop[i] = temp;
	}

	int worst_pop = 0;
	for (i = 1; i < SampleSize; i++)
		if (Population[Pop[worst_pop]].getFitness() < Population[Pop[i]].getFitness())
			worst_pop = i;
	if (Population[new_index].getFitness() < Population[Pop[worst_pop]].getFitness()) {
		int temp = new_index;
		new_index = Pop[worst_pop];
		Pop[worst_pop] = temp;
	}
	return new_index;
}

