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
 * Class Dimension
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * There are some message and operations in this class
 */

package Racos.Componet;

public class Dimension {
	
	private int size;          // the dimension size
	private double[][] region; //the feasible region in each dimension, region[][0] is lower bound, region[][1] is upper bound
	private boolean[] type;    //the type in each dimension, true means continue, false means discrete

	public Dimension(){
		size = 1;
		region = new double[1][2];
		type = new boolean[1];
		setDimension(0,1,true);
		
	}

	/**
	 * setting dimension size
	 * @param s
	 */
	public void setSize(int s){
		size = s;
		region = new double[size][2];
		type = new boolean[size];
	}
	
	 /**
	  * setting dimension, in this case, each dimension has the same setting
	  * 
	  * @param lower, the region's lower bound
	  * @param upper, the region's upper bound
	  * @param t, the dimension's type
	  */
	public void setDimension(double lower, double upper, boolean t){
		for(int i=0; i<size; i++){
			region[i][0] = lower;
			region[i][1] = upper;
			type[i] = t;
		}
		return ;
	}
	
	/**
	  * setting the index-th dimension only
	  * 
	  * @param index, the index-th dimension
	  * @param lower, the region's lower bound
	  * @param upper, the region's upper bound
	  * @param t, the dimension's type
	  */
	public void setDimension(int index, double lower, double upper, boolean t){
		region[index][0] = lower;
		region[index][1] = upper;
		type[index] = t;
		return ;
	}
	
	/**
	 * 
	 * 
	 * @return dimension size
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * return feasible region in one dimension
	 * 
	 * @param index, which dimension
	 * @return the feasible region in index-th dimension
	 */
	public double[] getRegion(int index){
		return region[index];
	}
	
	/**
	 * return the type
	 * 
	 * @param index, which dimension
	 * @return the type of index-th dimension
	 */
	public boolean getType(int index){
		return type[index];
	}
	
	public void PrintDim(){
		for(int i=0; i<size; i++){
			System.out.print("["+region[i][0]+","+region[i][1]+"] ");
		}
		System.out.println();
	}
	
	
}
