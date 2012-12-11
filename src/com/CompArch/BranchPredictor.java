package com.CompArch;

public abstract class BranchPredictor {
	Simulator sim;
	
	abstract boolean branches(int[] instruct);
	
	abstract void tick();
	
	abstract void parseInstruct (int [] instruct);
	
	BranchPredictor(Simulator s){
		sim = s;
	}
}
