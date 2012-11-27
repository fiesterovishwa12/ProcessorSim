package com.CompArch;

public class ReservationStation {

	Simulator sim;
	IAU iau;
	
	int depth;
	
	// Location in the 
	int next;
	int total;
	
	// Instruction memory section
	public int[][]instructBuffer;

	public ReservationStation (Simulator s, int size)
	{
		depth = size;
		next = 0;
		total = 0;
		instructBuffer = new int[size][4];
		iau = new IAU(s);
	}
	
	// Takes instruction, returns true if added to buffer, false if buffer full
	public boolean receive (int[] instruction)
	{
		if (total == depth)
			return false;
		
		int dest = next + total;
		
		return true;
	}
	
	void dispatch ()
	{
		// perform dependancy and IAU availability checking, if ready then send
	}
	
	
}
