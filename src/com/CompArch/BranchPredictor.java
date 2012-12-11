package com.CompArch;

public abstract class BranchPredictor {
	Simulator sim;
	
	abstract boolean branches(int[] instruct);
	
	BranchPredictor(Simulator s){
		sim = s;
	}
}
