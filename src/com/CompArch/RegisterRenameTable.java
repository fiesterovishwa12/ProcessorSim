package com.CompArch;

public class RegisterRenameTable {
	// the physical register the given register will map to
	private int rename[];
	private boolean available[];
	
	public RegisterRenameTable(int size) {
		rename = new int[size];
		available = new boolean[size];
		for (int i = 0; i<size; i++)
		{
			rename[i] = -1;
			available[i] = true;
		}
	}
	
	// returns -1 if no registers are available for renaming
	int newReg (int in)
	{
		if (in < 0)
			return -1;
		
		for (int i = 0; i < available.length; i++)
		{
			if (available[i]) 
			{
				rename[in] = i;
				available[i] = false;
				System.out.println("to " + i);
				return i;
			}
		}
		
		return -1;
	}
	
	boolean assigned (int in)
	{
		return rename[in] >= 0;
	}
	
	int getReg (int in)
	{
		System.out.println("GETTING REG " + in);
		if( rename[in] >= 0)
			return rename[in];
		System.out.println("RENAME: " + in + " to " + rename[in]);
		// otherwise it is a new register and so is assigned as such
		System.out.println("NEW REG:" + in);
		return newReg(in);
	}
	
	void free (int r)
	{
		if (r >= 0)
			available[r] = true;
	}
}
