package com.CompArch;

public class ReorderBuffer {
	private int instruct[];
	private int dest[];
	private int result[];
	private boolean valid[];
	private int size;
	private int pos;
	private int total;
	private Simulator sim;

	ReorderBuffer (int length, Simulator s)
	{
		sim = s;
		pos = 0;
		total = 0;
		size = length;
		instruct = new int [length];
		dest = new int [length];
		result = new int [length];
		valid = new boolean [length];
		for (int i = 0; i < length; i++)
			valid[i] = false;
	}
	
	void setResult (int index, int val)
	{
		result[index] = val;
		valid[index] = true;
	}
	
	int insert (int[] instruction)
	{
		int writeTo = (pos + total) % size;
		instruct[writeTo] = instruction[0];
		
		// Dest if branch operation
		if (instruction[0] == 17 | instruction[0] == 18)
			dest[writeTo] = instruction[3];
		else
			dest[writeTo] = instruction[1];
		
		valid[writeTo] = false;
		
		total++;
		
		return writeTo;
	}
	
	void tick()
	{
		//Check if current instruction is valid, if so then write it and move on to the next
		if (valid[pos] && total > 0)
		{
			sim.regFile.set(dest[pos], result[pos]);
			total--;
			pos++;
		}
	}
}
