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
 * Class Instance
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * this class is a storage of each sample
 */
package Racos.Componet;

import Racos.Componet.*;

public class Instance {
	
	private double[] feature;//the value in each dimension
	private double value;	 //the objective function value with the feature
		
	/**
	 * constructor with parameter class Dimension
	 * 
	 * @param dim, the massage of dimension
	 */
	public Instance(Dimension dim){
		feature = new double[dim.getSize()];
		value = 0;
	}
	
	/**
	 * constructor with parameter dimensionsize
	 * user can construct class instance with dimension size
	 * 
	 * @param dimensionsize
	 */
	public Instance(int dimensionsize){
		feature = new double[dimensionsize];
		value = 0;
	}
	
	/**
	 * get feature value in one dimension
	 * 
	 * @param index
	 * @return the index-th dimension's feature value
	 */
	public double getFeature(int index){
		return feature[index];
	}
	
	/**
	 * function getFeature without parameter
	 * 
	 * @return all feature values
	 */
	public double[] getFeature(){
		return feature;
	}
	
	/**
	 * without index, in this case, each feature has the same value
	 * 
	 * @param fea, the feature value
	 */
	public void setFeature(double fea){
		for(int i=0; i<feature.length; i++){
			feature[i] = fea;
		}
		return ;
	}
	
	/**
	 * with index parameter, in this case, setting index-th feature value only
	 * 
	 * @param index
	 * @param fea, the feature value
	 */
	public void setFeature(int index, double fea){
		feature[index] = fea;
	}
	
	/**
	 * 
	 * 
	 * @return the objective function value in this feature
	 */
	public double getValue(){
		return value;
	}
	
	/**
	 * setting the objective function value in this feature
	 * 
	 * @param val, the objective function value
	 */
	public void setValue(double val){
		value = val;
	}
	
	/**
	 * get a copy of this instance
	 * 
	 * @return
	 */
	public Instance CopyInstance(){
		Instance copy = new Instance(feature.length);
		for(int i=0; i<feature.length; i++){
			copy.setFeature(i, feature[i]);
		}
		copy.setValue(value);
		return copy;		
	}
	
	/**
	 * if exist one feature in this instance is different from corresponding feature in ins, return false
	 * 
	 * @param ins
	 * @return
	 */
	public boolean Equal(Instance ins){
		for(int i=0; i<feature.length; i++){
			if(feature[i]!=ins.getFeature(i)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * show instance
	 */
	public void PrintInstance(){
		for(int i=0; i<feature.length; i++){
			System.out.print(feature[i]+" ");
		}/**/
		System.out.println(":"+value);
	}

}
