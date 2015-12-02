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

import Racos.Componet.*;
import Racos.Tools.*;
public class SetCover implements Task{
	private Dimension dim;
	private double weight[];
	private double allWeight;
	private int sub[][];
	private int subNum;
	
	public SetCover(int dim_size,int subset_size){
		double temp;
		subNum = subset_size;
		dim = new Dimension();
		dim.setSize(subset_size);
		dim.setDimension(0, 1, false);
		weight = new double[subset_size];
		sub = new int [subset_size][dim_size];
		RandomOperator rnd = new RandomOperator();
		RandomOperator rnd1 = new RandomOperator();
		allWeight = 0;
		for(int i=0; i<subset_size; i++){
			weight[i] = rnd.getDouble(0, 1);
			allWeight = allWeight + weight[i];
		}
		System.out.println("allW:"+allWeight);
		for(int i=0; i<subset_size; i++){
			for(int j=0; j<dim_size; j++){
				temp = rnd1.getDouble(0, 1);
				if(temp<0.3){//generate a subset with the probability 0.3
					sub[i][j] = 1;
				}
			}
		}
//		showWeight();
//		showSubSet();
	}
	public void showWeight(){
		for(int i=0; i<subNum; i++){
			System.out.print("dim"+i+":"+weight[i]+" ");
		}
		System.out.println("");
	}
	public void showSubSet(){
		for(int i=0; i<subNum; i++){
			System.out.println("sub"+i);
			for(int j=0; j<dim.getSize(); j++){
				System.out.print(sub[i][j]+" ");
			}
			System.out.println("");
		}
	}
	public int getSubNum(){
		return subNum;
	}
	public boolean fullSetCover(Instance ins){
		boolean temp[] = new boolean[dim.getSize()];
		for(int i=0; i<dim.getSize(); i++){
			temp[i] = false;
		}
		for(int i=0; i<subNum; i++){
			if (ins.getFeature(i) == 1) {
				for (int j = 0; j < dim.getSize(); j++) {
					if (!temp[j] && sub[i][j] == 1) {
						temp[j] = true;
					}
				}
			}
		}
		for(int i=0; i<dim.getSize(); i++){
			if(!temp[i]){
				return false;
			}
		}
		return true;
	}
	public double getValue(Instance ins){
		double v;
		v = 0;
		for(int i=0; i<subNum; i++){
			v = v+weight[i]*ins.getFeature(i);
		}
		if(fullSetCover(ins)){
			return v;
		}else{
		//	System.out.println("not full cover");
			return v+allWeight;
		}
	}
	public Dimension getDim() {
		// TODO Auto-generated method stub
		return this.dim;
	}

}

