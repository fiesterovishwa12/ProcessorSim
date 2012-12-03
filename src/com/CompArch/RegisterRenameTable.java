package com.CompArch;

public class RegisterRenameTable {
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
		for (int i = 0; i < available.length; i++)
		{
			if (available[i]) 
			{
				rename[in] = i;
				available[i] = false;
				return i;
			}
		}
		return -1;
	}
	
	int getReg (int in)
	{
		if( rename[in] >= 0)
			return rename[in];
		// otherwise it is a new register and so is assigned as such
		return newReg(in);
	}
	
	void free (int r)
	{
		available[r] = true;
	}
}
