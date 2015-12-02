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
 * The Normalized cut method is one of methods of spectral clustering
 * @author Yi-Qi Hu
 * @time 2015.11.20
 * @version 2.0
 */
package Racos.ObjectiveFunction;

import java.util.ArrayList;

import javax.swing.Spring;

import Racos.Componet.*;
import Racos.Tools.*;

public class NormalizedCut implements Task{
	
	private Dimension dim;
	private double[][] vertex;   //data set, each line is a instance
	private double sigma;        //hyper-parameter
	private int v_size;          //instance size
	private int vd_size;         //dimension size
	private int c_size;          //category size
	private int[] c_result;      //category label
	private String train;
	private String test;
	private class EachClass{     //intermediate class, record statistical result
		public int c_num;        //the instance size according to a certain category
		public int[] C;          //the sequence of this category
		public int count;
		public double dis;       //the distance in the same category
		
		public EachClass(){
			this.c_num = 0;
			this.C = null;
			this.count=0;
		}
	}
	
	public NormalizedCut(double sig, String tra, String tes){
		FileOperator fo = new FileOperator();
		sigma = sig;
		train = tra;
		test = tes;
		ArrayList<String> al = fo.FileReader(train);
		getData(al);
	}
	
	public void setSigma(double s){
		this.sigma = s;
	}
	
	/**
	 * get data from file, initialize some parameters according to file, and generate dimension class
	 * @param al
	 */
	protected void getData(ArrayList<String> al){
		String stral = (String)al.get(0);
		this.c_size = Integer.parseInt(stral);
		stral = (String)al.get(1);
		String[] num;
		num = stral.split(",");
		this.vertex = new double[al.size()-1][num.length-1];
		this.c_result = new int[al.size()-1];
		for(int i=1; i<al.size(); i++){
			stral = (String)al.get(i);
			num = stral.split(",");
			for(int j=0; j<num.length-1; j++){
		    	this.vertex[i-1][j] = Double.parseDouble(num[j]);
			}
			this.c_result[i-1] = Integer.parseInt(num[num.length-1]);
		}
		this.v_size = this.vertex.length;
		this.vd_size = this.vertex[0].length;
		this.dim = new Dimension();
		this.dim.setSize(this.v_size);
		this.dim.setDimension(0, this.c_size-1, false);
		return ;
	}
	
	/**
	 * data analysis according to class
	 * 
	 * @param s class imformation
	 * @return the results of analysis in class EachClass[]
	 */
	public EachClass[] AnalysisOfClass(Instance s){//count statistics of each class
		EachClass[] imf_c = new EachClass[this.c_size];
		for(int i=0; i<imf_c.length; i++){
			imf_c[i] = new EachClass();
		}
		for(int i=0; i<this.v_size; i++){
			imf_c[(int) s.getFeature(i)].c_num++;
		}
		for(int i=0; i<this.c_size; i++){
			imf_c[i].C = new int[imf_c[i].c_num];
		}
		for(int i=0; i<this.v_size; i++){
			imf_c[(int) s.getFeature(i)].C[imf_c[(int) s.getFeature(i)].count] = i;
			imf_c[(int) s.getFeature(i)].count++;
		}
		return imf_c;
	}
	
	/**
	 * 
	 * 
	 * @param best the sample, the result of classification
	 * @return error rate
	 */
	public double ErrorTest(Instance best){
		double rate;
		int count = 0;
		for(int i=0; i<best.getFeature().length; i++){
			if(best.getFeature(i)!=c_result[i]){
				count++;
			}
		}
		rate = ((double)count)/best.getFeature().length;
		return rate;
	}
	
	/**
	 * Calculate the distance^2 between vertex[i] and vertex[j]
	 * 
	 * @param i vertex[i]
	 * @param j vertex[j]
	 * @return distance
	 */
	public double distance(int i,int j){
		double dis = 0;
		for(int m=0; m<this.vd_size; m++){
			dis = dis + (this.vertex[i][m]-this.vertex[j][m])*(this.vertex[i][m]-this.vertex[j][m]);
		}
		return dis;
	}
	
	
	/**
	 * Calculate the weight between vertex[i] and vertex[j]
	 * 
	 * @param i vertex[i]
	 * @param j vertex[j]
	 * @return the weight
	 */
	public double weight(int i,int j){
		double wei = 0;
		wei = Math.pow(Math.E, -(distance(i,j)/(this.sigma*this.sigma)));
		return wei;
	}
	
	/**
	 * calculate inner-object distance
	 * @param ec
	 */
	protected void dis_in_same_class(EachClass[] ec){
		for(int i=0; i<ec.length; i++){
			if(ec[i].c_num!=0){
				ec[i].dis = ec[i].c_num;
			}else{
				ec[i].dis = 1;
			}
		}
		return ;
	}
	
	protected double wei_bet_diff_class(EachClass[] ec,int num){
		double temp = 0;
		int j;
		for(int i=0; i<ec[num].c_num; i++){
			for(j=0; j<num; j++){
				for(int k=0; k<ec[j].c_num; k++){
					temp = temp + weight(ec[num].C[i],ec[j].C[k]);
				}
			}
			j++;
			for( ; j<this.c_size; j++){
				for(int k=0; k<ec[j].c_num; k++){
					temp = temp + weight(ec[num].C[i],ec[j].C[k]);
				}
			}
		}
		return temp;
	}

	@Override
	public double getValue(Instance ins) {
		// TODO Auto-generated method stub
		EachClass[] imf_c;
		double RatioCut = 0;
		imf_c = AnalysisOfClass(ins);
		this.dis_in_same_class(imf_c);
		for(int i=0; i<this.c_size; i++){
			RatioCut = RatioCut + (1/imf_c[i].dis)*this.wei_bet_diff_class(imf_c, i);
		}
		return RatioCut;
	}

	@Override
	public Dimension getDim() {
		// TODO Auto-generated method stub
		return dim;
	}

}
