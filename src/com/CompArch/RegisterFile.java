package com.CompArch;

/* Register file, implements scoreboarding but not register renaming */

public class RegisterFile {
	// Register array
	int[] reg;
	int maxReg = 0;
	
	// Scoreboard for registers
	private boolean[] scoreboard;
	
	public RegisterFile(int size) 
	{
		reg = new int[size];
		scoreboard = new boolean[size];
		for (int i = 0; i < size; i++)
			scoreboard[i] = true;
	}
	
	// Set the register at index i to val
	public void set(int i, int val)
	{
		System.out.println("Freeing  " + i);
		scoreboard[i] = true;
		reg[i] = val;
		if (maxReg < i)
			maxReg = i;
	}
	
	public void issue(int i)
	{
		System.out.println("Locking " + i);
		scoreboard[i] = false;
	}
	
	public int get(int i)
	{
		return reg[i];
	}
	
	public boolean isFree(int i)
	{
		return scoreboard[i];
	}
	
	void printReg () {
		for (int i = 0; i <= maxReg; i++)
		{
			System.out.println(reg[i]);
		}
	}
	
}
