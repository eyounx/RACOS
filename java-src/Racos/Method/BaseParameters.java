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
	protected int MaxIteration;      //iteration size for batch-racos
	protected int Budget;   		 //sampling budget for sequential racos
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
	
	public void setBudget(int bud){
		Budget = bud;
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
