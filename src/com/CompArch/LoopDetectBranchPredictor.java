package com.CompArch;

// Identifies loops by the incrementing that occurs before the jump

public class LoopDetectBranchPredictor extends BranchPredictor{

	int count; // Cycles since a value was last incremented
	int lag;
	
	LoopDetectBranchPredictor(Simulator s) {
		super(s);
	}

	@Override
	boolean branches(int index, int[] instruct) {
		return count > 0;
	}

	@Override
	void tick() {
	}
	
	public void setLag(int l)
	{
		lag = l;
	}
	
	
	/* Returns true if an instruction is an increment. 
	** Defined as an add or addi where destination register
	** is one of the parameters*/
	private boolean isIncrement (int[] instruct)
	{
		if (instruct[0] != 3 && instruct[0] != 4)
			return false;
		else if (instruct[1] == instruct[2])
			return true;
		else if (instruct[0] == 4 && instruct[1] == instruct[3])
			return true;
		
		return false;
	}

	@Override
	void parseInstruct(int index, int[] instruct) {
		
		lag--;
		
		if (isIncrement(instruct))
			count = lag; 
	}

	@Override
	void parseBranch(int index, boolean branched) {
		// TODO Auto-generated method stub
		
	}

}
