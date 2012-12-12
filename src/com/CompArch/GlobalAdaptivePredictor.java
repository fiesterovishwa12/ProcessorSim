package com.CompArch;

public class GlobalAdaptivePredictor extends BranchPredictor {

	boolean history[];
	
	GlobalAdaptivePredictor(Simulator s) {
		super(s);
	}

	@Override
	boolean branches(int[] instruct) {
		return false;
	}

	@Override
	void tick() {
		
	}

	@Override
	void parseInstruct(int index, int[] instruct) {
		
	}

	@Override
	void parseBranch(int index, boolean branched) {
		// TODO Auto-generated method stub
	}
	
	/** sets the length of the predictor's history, wipes existing history 
	 ** WARNING need to store 2^length values**/
	public void setHistory (int length)
	{
		history = new boolean[length];
	}
	
}
