package com.CompArch;

public class RegisterRenameTable {
	private int rename[];
	private boolean available[];
	
	public RegisterRenameTable(int size) {
		rename = new int[size];
		available = new boolean[size];
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
		return rename[in];
	}
	
	void free (int r)
	{
		available[r] = true;
	}
}
