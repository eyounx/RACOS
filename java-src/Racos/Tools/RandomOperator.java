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
 * Class Random
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * This class implements some operations for generating numbers randomly
 */
package Racos.Tools;

import java.util.Random;

public class RandomOperator {
	
	Random random;
	
	public RandomOperator(){
		random = new Random();
	}
	
	/**
	 * get an integer in [lower,upper] under uniform distribution
	 * 
	 * @param lower 
	 * @param upper
	 * @return an integer within [lower,upper]
	 */
	public int getInteger(int lower, int upper){
		int b = upper-lower;
		return lower+random.nextInt(b+1);
	}
	
	/**
	 * get a double in [lower,upper] under uniform distribution
	 * 
	 * @param lower
	 * @param upper
	 * @return a double within [lower,upper]
	 */
	public double getDouble(double lower, double upper){
		double b = upper - lower;
		return lower+random.nextDouble()*b;
	}
	
	/**
	 * get a random number under standard normal distribution
	 * 
	 * @return a double number
	 */
	public double getGaussian(){
		Random random = new Random();
		return random.nextGaussian();
	}
	
	/**
	 * get a random number under normal distribution with mean and variance
	 * 
	 * @param mean
	 * @param variance
	 * @return a double number
	 */
	public double getGaussion(double mean, double variance){
		Random random = new Random();
		return Math.sqrt(variance)*random.nextGaussian()+mean;
	}

}
