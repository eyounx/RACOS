package Test;

import Racos.Componet.*;
import Racos.Method.*;
import Racos.ObjectiveFunction.*;
import Racos.Tools.RandomOperator;

public class MyMain {

	public static void main(String[] args) {
		
		// parameters of Racos
		int samplesize = 100;       // parameter: the number of samples in each iteration
		int iteration = 10000;     // parameter: the number of iterations
		int positivenum = 1;       // parameter: the number of positive instances in each iteration
		double probability = 0.95; // parameter: the probability of sampling from the model
		int uncertainbit = 1;      // parameter: the number of samplable dimensions 
		
		// load data for optimimzation ramploss
		if(false){
			double c = 1.5;
			double s = 0.3;
			String training  = "C:\\Users\\Frank Hu\\Desktop\\ramplosstest.txt";
			String testing  = "C:\\Users\\Frank Hu\\Desktop\\ramplosstest.txt";
			RampLoss ramp = new RampLoss(c,s,training,testing);
			Instance ins = new Instance(ramp.getDim());
			double fe = 1;
			ins.setFeature(fe);
			ins.setValue(ramp.getValue(ins));
			System.out.println(ins.getValue());
		}
		
		// example for optimization in continuous solutions space
		if (true) {
			Instance ins = null;
			int repeat = 1;
			double[] result = new double[repeat];
			Task t = new Ackley(100);
			for (int i = 0; i < repeat; i++) {
				Continue con = new Continue(t);
				con.setSampleSize(samplesize);      // parameter: the number of samples in each iteration
				con.setMaxIteration(iteration);     // parameter: the number of iterations
				con.setPositiveNum(positivenum);    // parameter: the number of positive instances in each iteration
				con.setRandProbability(probability);// parameter: the probability of sampling from the model
				con.setUncertainBits(uncertainbit); // parameter: the number of samplable dimensions 
				con.run();                          // call Racos
				ins = con.getOptimal();             // obtain optimal
				result[i] = ins.getValue();
			}
			double sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + result[i];
			}
			double average = sum / repeat;
			sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + (result[i] - average) * (result[i] - average);
			}
			double variance = sum / repeat;
			System.out.println("Average:" + average + " Variance:" + variance);
		}
		
		// example for optimization in discrete solution space
		if(false){
			Instance ins = null;
			int repeat = 1;
			double[] result = new double[repeat];
			Task t = new SetCover(30, 20);
			// compare with random search
			{
				UniformSearch us = new UniformSearch(t);
				us.setSampleSize(samplesize);  // parameter: the number of samples in each iteration
				us.setMaxIteration(iteration); // parameter: the number of iterations
				// us.run();
				// ins = us.getOptimal();
				// System.out.println("Uniform Search:"+ins.getValue());
			}
			for (int i = 0; i < repeat; i++) {
				Discrete con = new Discrete(t);
				con.setSampleSize(samplesize);       // parameter: the number of samples in each iteration
				con.setMaxIteration(iteration);      // parameter: the number of iterations
				con.setPositiveNum(positivenum);     // parameter: the number of positive instances in each iteration
				con.setRandProbability(probability); // parameter: the probability of sampling from the model
				con.setUncertainBits(uncertainbit);  // parameter: the number of samplable dimensions 
				con.run();                           // call Racos
				ins = con.getOptimal();              // obtain optimal
				result[i] = ins.getValue();
			}
			double sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + result[i];
			}
			double average = sum / repeat;
			sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + (result[i] - average) * (result[i] - average);
			}
			double variance = sum / repeat;
			System.out.println("Average:" + average + " Variance:" + variance);
		}
		
		// example for optimization in mixed solutions space
		if(false){
			Instance ins = null;
			int repeat = 10;
			double[] result = new double[repeat];
			Task t = new MixedFunction(100);
			// us.run();
			// ins = us.getOptimal();
			// System.out.println("Uniform Search:"+ins.getValue());
			for (int i = 0; i < repeat; i++) {
				Mix con = new Mix(t);
				con.setSampleSize(samplesize);      // parameter: the number of samples in each iteration
				con.setMaxIteration(iteration);     // parameter: the number of iterations
				con.setPositiveNum(positivenum);    // parameter: the number of positive instances in each iteration
				con.setRandProbability(probability);// parameter: the probability of sampling from the model
				con.setUncertainBits(uncertainbit); // parameter: the number of samplable dimensions 
				con.run();                          // call Racos
				ins = con.getOptimal();             // obtain optimal
				result[i] = ins.getValue();
//				con.PrintTime();
			}
			double sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + result[i];
			}
			double average = sum / repeat;
			sum = 0;
			for (int i = 0; i < repeat; i++) {
				sum = sum + (result[i] - average) * (result[i] - average);
			}
			double variance = sum / repeat;
			System.out.println("Average:" + average + " Variance:" + variance);
			
		}


	}

}
