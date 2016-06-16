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
Description: some basic classes used in Racos
**************************************************/

#include<iostream>
using namespace std;

class Dimension{
private:
	int size;             //dimension size
	double **region;      //feasible region for each dimension
	bool *type;           //type of each dimension, true means continuous, false means discrete

public:
	Dimension();
	Dimension(const Dimension& d);
	~Dimension();
	void setSize(int s);                                         //set dimension size
	void setDimensions(double, double, bool);                    //set all dimensions
	void setDimension(int, double, double, bool);                //set index-th dimenison
	int getSize();                                               //return dimension size
	double getLowBound(int);                                     //return lower bound of index-th dimension 
	double getUpBound(int);                                      //return upper bound of index-th dimension
	bool getType(int);                                           //return the type of index-th dimension

};

class Instance {
private:
	int size;			//feature size
	double *features;	//features
	double fitness;		//objective function value under this features setting

public:
	Instance(int);					//constructor
	Instance();
	Instance(const Instance&);		//copy constructor
	~Instance();
	void setFeatures(double[]);		//set all features
	void setFeature(int, double);	//set feature
	void setFitness(double);		//set objective function value
	void setSize(int);				//set feature size;
	double getFeature(int);			//return the index-th feature
	double getFitness();			//return objective function value
	int getSize();					//return the number of features
	void CopyInsatnce(Instance&);	//copy the objective instance
	bool Equal(Instance&);			//compare two instance, if they are same, return true
	void PrintIns();
};


//class Dimension

/*
 *Summary: constructor without parameters
 *Parameters: null
 *Return: null
 */
Dimension::Dimension() {
	region = NULL;
	type = NULL;
}

/*
 *Summary: copy constructor 
 *Parameters: copied class
 *Return: class Dimension
 */
Dimension::Dimension(const Dimension& d) {
	size = d.size;
	region = new double*[size];
	type = new bool[size];
	for (int i = 0; i < size; i++) {
		region[i] = d.region[i];
	}
	for (int i = 0; i < size; i++) {
		type[i] = d.type[i];
	}
}

/*
 *Summary: destructor
 *Parameters: null
 *Return: null
 */
Dimension::~Dimension() {
	for (int i = 0; i < size; i++) {
		delete[] region[i];
	}
	delete[] type;
}

/*
 *Summary: set dimension size
 *Parameters: 
 *	s: dimension size
 *Return: null
 */
void Dimension::setSize(int s) {
	size = s;
	region = new double*[size];
	for (int i = 0; i < size; i++) {
		region[i] = new double[2];
	}
	type = new bool[size];
	return;
}

/*
 *Summary: set region and type for all dimensions
 *Parameters:
 *	l: lower bound
 *	u: upper bound
 *	t: type
 *Return: null
 */
void Dimension::setDimensions(double l, double u, bool t) {
	for (int i = 0; i < size; i++) {
		region[i][0] = l;
		region[i][1] = u;
		type[i] = t;
	}
	return;
}

/*
 *Summary: set region and type for index-th dimension
 *Parameters:
 *	index: the index of setted dimension
 *	l: lower bound
 *	u: upper bound
 *	t: type
 *Return: null
 */
void Dimension::setDimension(int index, double l, double u, bool t) {
	region[index][0] = l;
	region[index][1] = u;
	type[index] = t;
	return;
}


/*
 *Summary: get dimension size
 *Parameters: null
 *Return: size
 */
int Dimension::getSize() {
	return size;
}


/*
 *Summary: get index-th dimension lower bound
 *Parameters: 
 *	index: index of dimension
 *Return: lower bound
 */
double Dimension::getLowBound(int index) {
	return region[index][0];
}

/*
 *Summary: get index-th dimension upper bound
 *Parameters:
 *	index: index of dimension
 *Return: upper bound
 */
double Dimension::getUpBound(int index) {
	return region[index][1];
}

/*
 *Summary: get index-th dimension type
 *Parameters:
 *	index: index of dimension
 *Return: type
 */
bool Dimension::getType(int index) {
	return type[index];
}


//class Instance

/*
 *Summary: constructor with parameters
 *Parameters:
 *	s: feature size
 *Return: null
 */
Instance::Instance(int s) {
	size = s;
	features = new double[size];
	fitness = 0;
	return;
}

/*
 *Summary: constructor without parameters
 *Parameters: null
 *Return: null
 */
Instance::Instance() {
	size = 1;
	features = new double[size];
	fitness = 0;
	return;
}

/*
 *Summary: copy constructor
 *Parameters: another instance
 *Return: null
 */
Instance::Instance(const Instance& ins) {
	size = ins.size;
	features = new double[size];
	for (int i = 0; i < size; i++) {
		features[i] = ins.features[i];
	}
	fitness = ins.fitness;
}

/*
 *Summary: destructor
 *Parameters: null
 *Return: null
 */
Instance::~Instance() {
	delete [] features;
}

/*
 *Summary: set features using an array
 *Parameters: 
 *	fea: a double array
 *Return: null
 */
void Instance::setFeatures(double fea[]) {
	for (int i = 0; i < size; i++) {
		features[i] = fea[i];
	}
	return;
}

/*
 *Summary: set index-dimension feature
 *Parameters:
 *	index: index
 *	fea: feature value
 *Return: null
 */
void Instance::setFeature(int index, double fea) {
	features[index] = fea;
	return;
}

/*
 *Summary: set fitness according to features
 *Parameters:
 *	fit: fitness value
 *Return: null
 */
void Instance::setFitness(double fit) {
	fitness = fit;
	return;
}

/*
 *Summary: set dimension size for instance
 *Parameters:
 *	s: dimension size
 *Return: null
 */
void Instance::setSize(int s) {
	size = s;
	features = new double[size];
	for (int i = 0; i < size; i++) {
		features[i] = 0;
	}
	fitness = 0;
	return;
}

/*
 *Summary: get index-dimensional feature value
 *Parameters:
 *	index: index
 *Return: index-dimensional feature value
 */
double Instance::getFeature(int index) {
	return features[index];
}

/*
 *Summary: get fitness value
 *Parameters: null
 *Return: fitness value
 */
double Instance::getFitness() {
	return fitness;
}

/*
 *Summary: get dimension size of this instance
 *Parameters: null
 *Return: dimension size
 */
int Instance::getSize() {
	return size;
}

/*
 *Summary: copy instance
 *Parameters: 
 *	ins: copied instance
 *Return: null
 */
void Instance::CopyInsatnce(Instance& ins) {
	setSize(ins.getSize());
	for (int i = 0; i < size; i++) {
		features[i] =  ins.getFeature(i);
	}
	fitness = ins.getFitness();
	return;
}

/*
 *Summary: compare this instance with another one
 *Parameters:
 *	ins: another instance
 *Return: if same return true, otherwise return false
 */
bool Instance::Equal(Instance& ins) {
	for (int i = 0; i < size; i++) {
		if (features[i] != ins.getFeature(i)) {
			return false;
		}
	}
	return true;
}

/*
 *Summary: print this instance
 *Parameters: null
 *Return: null
 */
void Instance::PrintIns() {
	cout << "fit:" << fitness;
	for (int i = 0; i < size; i++) {
		cout << ", " << features[i];
	}
	cout << endl;
	return;
}
