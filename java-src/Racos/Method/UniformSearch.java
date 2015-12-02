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
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 */
package Racos.Method;

import Racos.Componet.*;
import Racos.ObjectiveFunction.*;
import Racos.Tools.RandomOperator;

public class UniformSearch extends BaseParameters{
	
	private Instance Optimal;
	private Task task;
	private Dimension dimension;
	
	public UniformSearch(Task t){
		dimension = t.getDim();
		task = t;
	}
	
	protected Instance RandomInstance(){
		Instance ins = new Instance(dimension);
		RandomOperator ro = new RandomOperator();//tool of randomizing
		for(int i=0; i<dimension.getSize(); i++){
			
			if(dimension.getType(i)){//if i-th dimension type is continue
				ins.setFeature(i, ro.getDouble(dimension.getRegion(i)[0], dimension.getRegion(i)[1]));
			}else{//if i-th dimension type is discrete
				ins.setFeature(i, ro.getInteger((int)dimension.getRegion(i)[0], (int)dimension.getRegion(i)[1]));
			}
			
		}
		return ins;
	}
	
	public Instance getOptimal(){
		return Optimal;
	}
	
	public void run(){
		Instance ins = null;
		ins = RandomInstance();
		ins.setValue(task.getValue(ins));
		Optimal = ins.CopyInstance();
		for(int i=0; i<SampleSize*MaxIteration-1; i++){
			ins = RandomInstance();
			ins.setValue(task.getValue(ins));
			if(ins.getValue()<Optimal.getValue()){
				Optimal = ins.CopyInstance();
			}
		}
		return ;
	}
	
	

}
