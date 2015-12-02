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
 
 /**
 * Ramp loss is a kind of loss function for SVM
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * the optimization objective is the weights in SVM
 */
package Racos.ObjectiveFunction;

import Racos.Tools.*;
import java.util.ArrayList;
import Racos.Componet.*;

public class RampLoss implements Task{
	
	private Dimension dim;  //dimension
	private double s;       //hyper-parameter
	private double c;       //hyper-parameter
	private int dim_size;   //dimension size
	private double[][] L;   //training or testing data, include label
	private String test;    //test file name
	
	/**
	 * constructor with parameter s and c
	 * 
	 * @param i_s, parameter
	 * @param i_c, parameter
	 * @param train, train file path
	 * @param te, test file path
	 */
	public RampLoss(double i_s,double i_c,String train, String te){
		FileOperator fo = new FileOperator();
		ArrayList<String> al = null;
		test = te;
		al = fo.FileReader(train);
		L = getData(al);
		dim = new Dimension();
		dim.setSize(dim_size+1);
		dim.setDimension(-20, 20, true);
		s = i_s;
		c = i_c;
	}
	
	/**
	 * indicator function
	 * 
	 * @param a
	 * @return
	 */
	public double sign(double a){
		if(a>=0){
			return 1;
		}else{
			return -1;
		}
	}
	
	/**
	 * test function
	 * 
	 * @param best
	 * @return accuracy in test data
	 */
	public double Validation(Instance best){
		double rate;
		double[][] data = null;
		double sum;
		double label;
		double right = 0;
		FileOperator fo = new FileOperator();
		ArrayList<String> al = fo.FileReader(test);
		data = getData(al);
		for(int i=0; i<data.length; i++){//W*X+b
			sum = 0;
			for(int j=0; j<data[0].length-1; j++){
				sum += best.getFeature(j)*data[i][j];
			}
			sum += best.getFeature(data[0].length-1);
			label = sign(sum);
			if(label == data[i][data[0].length-1]){
				right++;
			}
		}
		rate = right/data.length;
		return rate;
	}
	
	/**
	 * 
	 * @param sa, weight
	 * @return 2norm of sa
	 */
	public double get_distance(Instance sa){
		double sum = 0;
		for(int i=0; i<dim.getSize()-1; i++){
			sum = sum+sa.getFeature(i)*sa.getFeature(i);
		}
		return sum;
	}
	
	/**
	 * f(xj)
	 * @param sa
	 * @param j
	 * @return
	 */
	public double get_fx(Instance sa,int j){
		double sum = 0;
		for(int i=0; i<sa.getFeature().length-1; i++){
			sum = sum+sa.getFeature(i)*L[j][i];
		}
		sum = sum+sa.getFeature(sa.getFeature().length-1);
		return sum;
	}
	
	public double get_H(double ylfx,double st){
		double temp;
		temp = st - ylfx;
		if(0>temp){
			return 0;
		}else{
			return temp;
		}
	}
	
	/**
	 * obtain data from ArrayList, the data format: the data is n*m matrix, each line is a instance.
	 * In each line, the first m-1 variables are features, the last one is label(1/-1)
	 * 
	 * @param al
	 * @return
	 */
	protected double[][] getData(ArrayList<String> al){
		String str;
		String[] num;
		double[][] data = null;
		str = al.get(0);
		num = str.split(",");
		dim_size = num.length-1;
		data = new double[al.size()][this.dim_size+1];
		for(int i=0; i<al.size();i++){
			str = al.get(i);
			num = str.split(",");
			for(int j=0; j<num.length; j++){
				data[i][j] = Double.parseDouble(num[j]);
			}
		}
		return data;
		
	}

	@Override
	public double getValue(Instance ins) {
		// TODO Auto-generated method stub
		double dis;
		double H1=0;
		double Hs=0;
		double fx;
		double value;
		for(int i=0; i<L.length; i++){
			fx = get_fx(ins,i);
			H1 = H1+get_H(L[i][L[0].length-1]*fx,1);
			Hs = Hs+get_H(L[i][L[0].length-1]*fx,s);			
		}
		dis = get_distance(ins);
		value = dis/2+c*H1-c*Hs;
		return value;
	}

	@Override
	public Dimension getDim() {
		// TODO Auto-generated method stub
		return dim;
	}

}
