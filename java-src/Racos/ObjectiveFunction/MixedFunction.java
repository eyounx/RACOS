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
 
package Racos.ObjectiveFunction;

import Racos.Componet.*;

public class MixedFunction implements Task{
	private Dimension dim;
	
	public MixedFunction(int size){
		dim = new Dimension();
		dim.setSize(size);
		for(int i=0; i<size ;i++){
			
			if(i%2==1){
				dim.setDimension(i, 0, 1, true);
			}else{
				dim.setDimension(i, 0, 100, false);			
			}
		}
		
	}
	
	public double getValue(Instance ins){
		double v = 0;
		int size = ins.getFeature().length;
		for(int i=0; i<size; i++){
			v = v+ins.getFeature(i);
		}
		return v;
	}

	public Dimension getDim() {
		return dim;
	}

}
