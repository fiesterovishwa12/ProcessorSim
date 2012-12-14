package com.CompArch;

// Predicts all jump forwards will not happen, and all jumps back will

public class ForwardBackBranchPredictor extends BranchPredictor{

	ForwardBackBranchPredictor(Simulator s) {
		super(s);
	}

	@Override
	boolean branches(int index, int[] instruct) {
		int val = 1;
		if (instruct[0] == 17 || instruct[0] == 18)
			val = instruct[3];
		else if (instruct[0] == 19)
			val = instruct[1];
		
		if (val >= 0)
			return false;
		else
			return true;
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

}
