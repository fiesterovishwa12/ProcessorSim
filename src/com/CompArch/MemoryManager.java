package com.CompArch;

public class MemoryManager extends ExecutionUnit{
	
	// Output of operation
	private int out;
	
	// Destination of output
	private int dest;
	
	// Cycles remaining for current instruction
	int cycles;
	
	// If ALU is available
	boolean free;

	MemoryManager(Simulator s) {
		super(s);
	}

	@Override
	boolean isFree() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void read(int instruct, int r1, int r2, int r3, int d, int br) {
		dest = d;
		int[] in = {instruct,r1,r2,r3};
		mem(in);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
	
	void mem(int[] instruct)
	{
		
		int val = 0;
		// Increment the clock for the memory access (cost - 1)
		sim.cycleTotal += 3;
		if (instruct[0] == 1){
			val = sim.dataMem[sim.regFile.get(instruct[2]) + instruct[3]];
		}
		else if (instruct[0] == 2){
			val =  sim.regFile.get(instruct[1]);
			sim.dataMem[sim.regFile.get(instruct[2]) + instruct[3]] = sim.regFile.get(instruct[1]);
			// Increment max mem
			if (sim.regFile.get(instruct[2]) + instruct[3] > sim.maxMem)
				sim.maxMem = sim.regFile.get(instruct[2]) + instruct[3];
		}
		sim.rob.setResult(dest, val);
		
	}

	@Override
	public void flush(int br) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}	

}
