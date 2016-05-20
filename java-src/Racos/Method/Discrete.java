/**
 * Class Discrete
 * @author Yi-Qi Hu
 * @time 2015.11.14
 * @version 2.0
 * Discrete class is the method of Racos in Discrete feasible region
 */


package Racos.Method;

import Racos.Componet.*;
import Racos.ObjectiveFunction.*;
import Racos.Tools.*;

public class Discrete extends BaseParameters{
	
	private Task task;                   //the task that algorithm will optimize       
	private Dimension dimension;         //the task dimension message that algorithm optimize
	private Instance[] Pop;              //the instance set that algorithm will handle with
	private Instance[] NextPop;          //the instance set that algorithm will generate using instance set Pop
	private Instance[] PosPop;           //the instance set with best objective function value
	private Instance Optimal;            //an instance with the best objective function value
	private Model model;                 //the model of generating next instance
	private RandomOperator ro;
	
	private class Model{                 //the model of generating next instance
		
		public boolean[] label;  //if label[i] is false, the corresponding dimension should be randomized from region
		
		public Model(int size){
			label = new boolean[size];
			for(int i=0; i<size; i++){
				label[i] = false;
			}
		}
		
		public void PrintLabel(){
			for(int i=0; i<label.length; i++){
				if(!label[i]){
					System.out.print(i+" ");
				}
			}
			System.out.println();
		}
	}
	
	
	/**
	 * constructors function
	 * user have to constructs class Continue with a class which implements interface Task
	 * 
	 * @param ta  the class which implements interface Task
	 */
	public Discrete(Task ta){
		task = ta;
		dimension = ta.getDim();
		ro = new RandomOperator();
	}
	
	//the next several functions are prepared for testing
		public void PrintPop(){
			System.out.println("Pop set:");
			if(Pop.length==0){
				System.out.println("Pop set is null!");
			}
			for(int i=0; i<Pop.length; i++){
				Pop[i].PrintInstance();
			}
		}
		public void PrintPosPop(){
			System.out.println("PosPop set:");
			if(PosPop.length==0){
				System.out.println("PosPop set is null!");
			}
			for(int i=0; i<PosPop.length; i++){
				PosPop[i].PrintInstance();
			}
		}
		public void PrintNextPop(){
			System.out.println("NextPop set:");
			if(NextPop.length==0){
				System.out.println("NextPop set is null!");
			}
			for(int i=0; i<NextPop.length; i++){
				NextPop[i].PrintInstance();
			}
		}
		public void PrintOptimal(){
			System.out.println("Optimal:");
			Optimal.PrintInstance();
		}
		
		/**
		 * 
		 * @return the optimal that algorithm found
		 */
		public Instance getOptimal(){
			return Optimal;
		}
		
		/**
		 * RandomInstance without parameter
		 * 
		 * @return an Instance, each feature in this instance is a random number from original feasible region
		 */
		protected Instance RandomInstance(){
			Instance ins = new Instance(dimension);
			for(int i=0; i<dimension.getSize(); i++){
				
				if(dimension.getType(i)){//if i-th dimension type is continue
					ins.setFeature(i, ro.getDouble(dimension.getRegion(i)[0], dimension.getRegion(i)[1]));
				}else{//if i-th dimension type is discrete
					ins.setFeature(i, ro.getInteger((int)dimension.getRegion(i)[0], (int)dimension.getRegion(i)[1]));
				}
				
			}
			return ins;
		}
		
		/**
		 * RandomInstance with parameter
		 * 
		 * @param model, the feasible region
		 * @return an Instance, each feature in this instance is a random number from a feasible region named model
		 */
		protected Instance RandomInstance(Instance pos){
			Instance ins = new Instance(dimension);
//			model.PrintLabel();
			for(int i=0; i<dimension.getSize(); i++){
				
				if(dimension.getType(i)){//if i-th dimension type is continue
		/*			if(model.label[i]){//according to fixed dimension, valuate using corresponding value in pos
						ins.setFeature(i, pos.getFeature(i));
					}else{//according to not fixed dimension, random in region
//						System.out.println("["+model.region[i][0]+", "+model.region[i][1]+"]");
						ins.setFeature(i, ro.getDouble(model.region[i][0], model.region[i][1]));
					}*/
				}else{//if i-th dimension type is discrete
					if(model.label[i]){//according to fixed dimension, valuate using corresponding value in pos
						ins.setFeature(i, pos.getFeature(i));
					}else{//according to not fixed dimension, random in region
						ins.setFeature(i, ro.getInteger((int)dimension.getRegion(i)[0], (int)dimension.getRegion(i)[1]));
					}
				}
				
			}
			return ins;
		}
		
		
		/**
		 * if ins exist in Pop, return false. designed for initialize()
		 * 
		 * @param until 
		 * @param ins
		 * @return
		 */
		protected boolean notExistInPop(int until, Instance ins, Instance[] temp){
			int i,j;
			for(i=0;i<until;i++){
				for(j=0; j<dimension.getSize(); j++){
					if(ins.getFeature(j)!=temp[i].getFeature(j)){
						break;
					}
				}
				if(j==dimension.getSize()){//exist
					return false;
				}
			}
			return true;
		}
		
		/**
		 * initialize Pop, NextPop, PosPop and Optimal
		 * 
		 */
		@SuppressWarnings("unchecked")
		protected void Initialize(){
			
			Instance[] temp = new Instance[SampleSize+PositiveNum];
			boolean exist = true;
			
			Pop = new Instance[SampleSize];
			
			//sample Sample+PositiveNum instances and add them into temp
			for(int i=0; i<SampleSize+PositiveNum; i++){
				exist = true;
				while (exist) {//if new sample exist in old set, resample
					temp[i] = RandomInstance();
					if (notExistInPop(i, temp[i], temp)) {
						exist = false;
					}
				}
				temp[i].setValue(task.getValue(temp[i]));
			}	
					
			//sort Pop according to objective function value
			InstanceComparator comparator = new InstanceComparator();
			java.util.Arrays.sort(temp,comparator);
			
			//initialize Optimal
			Optimal = temp[0].CopyInstance();
			
			//after sorting, the beginning several instances in temp are used for initializing PosPop
			PosPop = new Instance[PositiveNum];
			for(int i=0; i<PositiveNum; i++){
				PosPop[i] = temp[i];
			}
			
			Pop = new Instance[SampleSize];
			for(int i=0; i<SampleSize; i++){
				Pop[i] = temp[i+PositiveNum];
			}
			
			//initialize NextPop
			NextPop = new Instance[SampleSize];
			
			model = new Model(dimension.getSize());
			
			return ;
			
		}
		
		/**
		 * 
		 * @return the model with original feasible region and all label is true
		 */
		protected void ResetModel(){
			for(int i=0; i<dimension.getSize(); i++){
				model.label[i] = false;
			}
			return ;
		}
		
		/**
		 * if each instance in Pop is not in model, return true; if exist more than one instance in Pop is in the model, return false
		 * 
		 * @param model
		 * @return true or false
		 */
		protected boolean Distinguish(Instance pos){
			int i,j;
			
			//if the model was not updated, return false
			for(i=0; i<dimension.getSize(); i++){
				if(model.label[i]){
					break;
				}
			}
			if(i==dimension.getSize()){
				return false;
			}
			
			//if the model was updated
			for(j=0; j<SampleSize; j++){
				
				//for each instance in Pop, if exist one feature in someone dimension that was labeled is different from pos, means that model distinguish this instance from pos 
				for(i=0; i<dimension.getSize(); i++){
					if(model.label[i]&&pos.getFeature(i)!=Pop[j].getFeature(i)){
						break;
					}
				}
				if(i==dimension.getSize()){//if exist one instance was not distinguished by model, return false
					return false;
				}
			}
			
			//if the model distinguish all instances in Pop from pos, return true
			return true;
		}
		
		/**
		 * shrink model for instance pos
		 * 
		 * @param pos
		 */
		protected void ShrinkModel(Instance pos){
			int LabelNum;
			int TempLab=0;
			int[] LabelMark;
			LabelMark=new int[dimension.getSize()];
			int labelMarkNum;
			
			LabelNum = 0;		
			labelMarkNum = dimension.getSize();
			for(int k=0; k<dimension.getSize(); k++){
				LabelMark[k] = k;
			}

			//shrink model by determining bits
			while (!Distinguish(pos)) {
				TempLab = ro.getInteger(0, labelMarkNum - 1);
				model.label[LabelMark[TempLab]] = true;
				LabelMark[TempLab] = LabelMark[labelMarkNum-1];
				labelMarkNum--;
				LabelNum++;
			}

			//sample without replacement to get uncertain bits
			while (dimension.getSize() - LabelNum > UncertainBits) {
				TempLab = ro.getInteger(0, labelMarkNum - 1);
				model.label[LabelMark[TempLab]] = true;
				LabelMark[TempLab] = LabelMark[labelMarkNum-1];
				labelMarkNum--;
				LabelNum++;
			}
			return ;
		}
		
		/**
		 * if ins exist in Pop, PosPop and NextPop, return false
		 * 
		 * @param until
		 * @param ins
		 * @return
		 */
		protected boolean notExistInNextPop(int until, Instance ins){
			int i,j;
			
			//in PosPop
			for(i=0; i<PositiveNum;i++){
				for(j=0; j<dimension.getSize(); j++){
					if(ins.getFeature(j)!=PosPop[i].getFeature(j)){
						break;
					}
				}
				if(j==dimension.getSize()){
					return false;
				}
			}
			
			//in Pop
			for(i=0;i<SampleSize;i++){
				for(j=0; j<dimension.getSize(); j++){
					if(ins.getFeature(j)!=Pop[i].getFeature(j)){
						break;
					}
				}
				if(j==dimension.getSize()){
					return false;
				}
			}
			
			//in NextPop
			for(i=0;i<until;i++){
				for(j=0; j<dimension.getSize(); j++){
					if(ins.getFeature(j)!=NextPop[i].getFeature(j)){
						break;
					}
				}
				if(j==dimension.getSize()){
					return false;
				}
			}
			return true;
		}
		
		/**
		 * update set PosPop using NextPop
		 * 
		 */
		protected void UpdatePosPop(){
			Instance temp = new Instance(dimension);
			int j;
			for(int i=0; i<this.SampleSize; i++){
				for(j=0; j<this.PositiveNum; j++){
					if(PosPop[j].getValue()>Pop[i].getValue()){//if an instance is better than instances in PosPop set
						break;
					}
				}
				if(j<this.PositiveNum){
					temp=Pop[i];
					Pop[i]=PosPop[this.PositiveNum-1];
					for(int k=this.PositiveNum-1; k>j;k--){//delete the worst instance in PosPop
						PosPop[k]=PosPop[k-1];
					}
					PosPop[j]=temp;//insert this instance in PosPop set 
				}
			}
			return ;
		}
		
		/**
		 * update optimal
		 */
		protected void UpdateOptimal(){
			if (Optimal.getValue() > PosPop[0].getValue()) {
				Optimal=PosPop[0];
	//			System.out.println("iteration:"+i+" value:"+Optimal.getValue());
	//			Optimal.PrintInstance();
			}
			return ;
		}
		
		/**
		 * after setting parameters of Racos, user call this function can obtain optimal
		 */
		public void run(){

			double GlobalSample=0;
			boolean reSample;
			int ChoosenPos;
			

			Initialize();//initialize Pop, PosPop and Optimal
			
			for (int i = 1; i < this.MaxIteration; i++) {//each iteration
				for (int j = 0; j < this.SampleSize; j++) {//sample instance				
					reSample = true;
					while (reSample) {
						
						ResetModel();//reset model
						ChoosenPos = ro.getInteger(0, this.PositiveNum - 1);//choose an instance randomly
						GlobalSample = ro.getDouble(0, 1);
						if (GlobalSample >= this.RandProbability) {//sample globally
							
						}else{		
						
							ShrinkModel(PosPop[ChoosenPos]);//get model by shrinking
							
						}
											
						NextPop[j] = RandomInstance(PosPop[ChoosenPos]);//sample

						if (notExistInNextPop(j, NextPop[j])) {//if the instance is unique
							NextPop[j].setValue(task.getValue(NextPop[j])); //query
							reSample = false;
						} else {
						}
					}
				}
				
				//copy NextPop to Pop
				for(int k=0; k<this.SampleSize; k++){
					Pop[k] = NextPop[k];
				}	
				
				//update PosPop according to new Pop
				UpdatePosPop();
				
				//get optimal
				UpdateOptimal();								
				
			}
			return ;
		}

}
