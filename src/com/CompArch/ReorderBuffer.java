package com.CompArch;

public class ReorderBuffer {
	private int instruct[];
	private int dest[];
	private int result[];
	private boolean valid[];
	private int size;
	
	ReorderBuffer (int length)
	{
		size = length;
		instruct = new int [length];
		dest = new int [length];
		result = new int [length];
		valid = new boolean [length];
		for (int i = 0; i < length; i++)
			valid[i] = false;
	}
}
