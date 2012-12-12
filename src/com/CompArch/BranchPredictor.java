package com.CompArch;

public abstract class BranchPredictor {
	Simulator sim;
	
	abstract boolean branches(int[] instruct);
	
	abstract void tick();
	
	/** Takes an instruction and its index **/
	abstract void parseInstruct (int index, int [] instruct);
	
	abstract void parseBranch (int index, boolean branched);
	
	BranchPredictor(Simulator s){
		sim = s;
	}
}
