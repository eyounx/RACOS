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
 * @version 2.1 by Yang Yu: correct the optimal solution
 */
 
 package Racos.ObjectiveFunction;

import Racos.Componet.Dimension;
import Racos.Componet.Instance;

public class Ackley implements Task{
	
	private Dimension dim; //dimension
	private double opt[];  //the center of sphere
	
	public Ackley(int size){
		dim = new Dimension();
		dim.setSize(size);
		dim.setDimension(0, 1, true);
		opt = new double[size];
		for(int i=0; i<size; i++){
			opt[i] = 0.2;
		}
	}
	
	public Ackley(double opt[]){
    dim = new Dimension();
    dim.setSize(opt.length);
    dim.setDimension(0, 1, true);
    this.opt = new double[opt.length];
    System.arraycopy(opt, 0, this.opt, 0, opt.length);
  }

	@Override
	public double getValue(Instance ins) {
		// TODO Auto-generated method stub
		double[] v;
        double squaresum = 0;
        double cossum = 0;
        v = new double[ins.getFeature().length];
        for(int i=0; i<ins.getFeature().length; i++){
        	v[i] = ins.getFeature(i);
        }
        for(int i=0; i<ins.getFeature().length; i++){
            squaresum += (v[i]-opt[i])*(v[i]-opt[i]);
            cossum += Math.cos(Math.PI*2*(v[i]-opt[i]));
        }
        squaresum /= (double)v.length;
        cossum /= (double)v.length;
        double v1 = -0.2*Math.sqrt(squaresum);
        double sum = -20*Math.exp(v1)-Math.exp(cossum)+20+Math.E;
        return sum;

	}

	@Override
	public Dimension getDim() {
		// TODO Auto-generated method stub
		return dim;
	}

}
