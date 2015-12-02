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
 * Class BaseParameters
 * @author Yi-Qi Hu
 * @time 2015.11.14
 * @version 2.0
 * There are some common parameters in each Racos method
 */

package Racos.Method;

public class BaseParameters {
	
	protected int SampleSize;        //sample size in each iteration
	protected int MaxIteration;      //iteration size
	protected int PositiveNum;       //the number of samples in each iteration that Racos determine as good samples
	protected double RandProbability;//the probability that Racos sample in the region that algorithm learned
	protected int UncertainBits;     //the number of dimension in which algorithm will randomize
	
	public BaseParameters(){
	}
	
	//next some functions with set beginning are setting function of parameters above
	
	public void setSampleSize(int size){
		SampleSize = size;
	}
	
	public void setMaxIteration(int max){
		MaxIteration = max;
	}
	
	public void setPositiveNum(int num){
		PositiveNum = num;
	}
	
	public void setRandProbability(double pro){
		RandProbability = pro;
	}
	
	public void setUncertainBits(int unc){
		UncertainBits = unc;
	}

}
