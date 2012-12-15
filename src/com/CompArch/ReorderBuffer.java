package com.CompArch;

public class ReorderBuffer {
	private int instruct[];
	private int dest[];
	private int result[];
	private boolean valid[];
	private int branch[];
	
	// Logs if instruction is overwriting a particular register's value (for register renaming)
	private int overWrite[];
	private int size;
	private int head;
	private int tail;
	private Simulator sim;

	ReorderBuffer (Simulator s, int length)
	{
		sim = s;
		head = 0;
		tail = 0;
		size = length;
		instruct = new int [length];
		dest = new int [length];
		result = new int [length];
		valid = new boolean [length];
		overWrite = new int [length];
		branch = new int [length];
		for (int i = 0; i < length; i++)
		{
			valid[i] = false;
			overWrite[i] = -1;
		}
	}
	
	boolean isFree()
	{
		return head == tail;
	}
	
	void printBuffer ()
	{
		System.out.println("REORDER BUFFER");
		int pos = tail;
		while (pos != head)
		{
			System.out.println(instruct[pos] + " " + dest[pos] + " " + result[pos]);
			pos++;
			if (pos >= size)
				pos = 0;
		}
	}
	
	void setResult (int index, int val)
	{
		result[index] = val;
		valid[index] = true;
	}
	
	/** insert an instruction into the reorder buffer, along with the register it is overwriting */
	int insert (int[] instruction, int over, int br)
	{
		instruct[head] = instruction[0];
		
		overWrite[head] = over;
		
		branch[head] = br;
		
		// Dest if branch operation
		if (instruction[0] == 17 | instruction[0] == 18)
			dest[head] = instruction[3];
		else
			dest[head] = instruction[1];
				
		int result = head;
		
		head++;
		
		if (head >= size)
			head = 0;
		
		return result;
	}
	
	void tick()
	{
		for (int i = 0; i < sim.getNWay(); i++)
		{
			//Check if current instruction is valid, if so then write it and move on to the next
			if (valid[tail])
			{
				sim.regFile.set(dest[tail], result[tail]);
				//System.out.println("WROTE TO: " + dest[tail] + " : " + result[tail]);
				//System.out.println("FREEING: " + overWrite[tail]);
				sim.rrt.free(overWrite[tail]);
				valid[tail] = false;
				tail++;
				if (tail >= size)
					tail = 0;
			}
			else
				break;
		}
	}

	// Takes a branch value, flushes any value within it
	void flush (int br)
	{
		int pos = tail;
		int pos2 = pos;
		while (pos != head)
		{
			pos = pos % instruct.length;
			pos2 = pos2 % instruct.length;
			boolean over = branch[pos] == br;
			if (over)
			{
				pos2++;
				pos2 = pos2 % instruct.length;
				head--;
				head = head % instruct.length;
			}
			instruct[pos] = instruct[pos2];
			dest[pos] = dest[pos2];
			result[pos] = result[pos2];
			valid[pos] = valid[pos2];
			overWrite[pos] = overWrite[pos2];
			branch[pos] = branch[pos2];
			if (!over)
			{
				pos++;
				pos2++;
			}
		}
	}

	public void confirm(int id, int newId) {
		for (int i = 0; i < branch.length; i++)
			if (branch[i] == id)
				branch[i] = newId;
	}

	
}
