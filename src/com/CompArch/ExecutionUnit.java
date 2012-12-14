package com.CompArch;

// General execution unit, can be BC, IAU, Memory Manager
public abstract class ExecutionUnit {
	Simulator sim;
	
	/** If execution unit 
	 * is free to accept another instruction*/
	abstract boolean isFree();

	/** Read in an instruction to be executed */
	//public abstract void read (int instruct, int r1, int r2, int r3, int d);
	public abstract void read (int instruct, int r1, int r2, int r3, int d, int br);

	/** Flushes an instruction if it is an particular branch operation */
	public abstract void flush(int br);

	public abstract void print();

	/** Move the internal state of the eu forward one clock cycle */
	public abstract void tick();

	ExecutionUnit(Simulator s) {
		sim = s;
	}

}
