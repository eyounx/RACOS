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
 * Sphere function
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * Sphere function is the square of 2-norm of feature-center
 */
package Racos.ObjectiveFunction;

import Racos.Componet.Dimension;
import Racos.Componet.Instance;

public class Sphere implements Task{
	
	private Dimension dim; //dimension
	private double opt[];  //the center of sphere
	
	public Sphere(int size){
		dim = new Dimension();
		dim.setSize(size);
		dim.setDimension(0, 1, true);
		opt = new double[size];
		for(int i=0; i<size; i++){
			opt[i] = 0.2;
		}
	}

	public Sphere(double opt[]){
    dim = new Dimension();
    dim.setSize(opt.length);
    dim.setDimension(0, 1, true);
    this.opt = new double[opt.length];
    System.arraycopy(opt, 0, this.opt, 0, opt.length);
  }

	@Override
	public double getValue(Instance ins) {
		// TODO Auto-generated method stub
		double sum = 0;
        double v=0;
        for(int i=0; i<dim.getSize(); i++){//calculate sphere value
            v=ins.getFeature(i)-opt[i];
            sum += v*v;
        }
        return sum;
	}

	@Override
	public Dimension getDim() {
		// TODO Auto-generated method stub
		return dim;
	}
	
	

}
