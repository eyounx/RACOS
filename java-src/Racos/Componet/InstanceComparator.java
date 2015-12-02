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
 * Class InstanceComparator
 * @author Yi-Qi Hu
 * @time 2015.11.14
 * @version 2.0
 * this class override function compare in class Comparator, is used for Instance array sort
 */
package Racos.Componet;

import java.util.*;

public class InstanceComparator implements Comparator{

	@Override
	public int compare(Object arg0, Object arg1) {
		Instance ins1 = (Instance)arg0;
		Instance ins2 = (Instance)arg1;
		
		if(ins1.getValue()<ins2.getValue()){
			return -1;
		}else{
			if(ins1.getValue()==ins2.getValue()){
				return 0;
			}else{
				return 1;
			}
		}
	}

}
