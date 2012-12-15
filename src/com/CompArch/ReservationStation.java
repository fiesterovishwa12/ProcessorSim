package com.CompArch;

public class ReservationStation {

	private Simulator sim;
	ExecutionUnit eu;
	
	private int depth;
	
	// Location in the 
	private int next;
	private int total;
	
	// Instruction memory section
	private int[][] instructBuffer;
	
	// If the instruction is dependent on a branch predict give id, else -1
	private int[] branch;
	
	// Location of each instruction in the reorder buffer
	private int robLoc[];
	
	// Whether inputs are available
	private boolean[][] available;
	
	public boolean isFree ()
	{
		if (total > 0 || !eu.isFree())
			return false;
		else
			return true;
	}
	
	public boolean canWrite()
	{
		return total < depth;
	}

	public ReservationStation (Simulator s, int size, ExecutionUnit e)
	{
		depth = size;
		next = 0;
		total = 0;
		instructBuffer = new int[size][4];
		branch = new int[size];
		robLoc = new int[size];
		eu = e;
		available = new boolean[size][2];
		
		for (int i = 0; i < size; i++) {
			available[i][0] = false;
			available[i][1] = false;
			branch[i] = -1;
		}
		sim = s;
	}
	
	// Takes instruction, returns true if added to buffer, false if buffer full
	public boolean receive (int[] instruct, int br)
	{
		// Check to see if can add instruction to the buffer
		
		if (total >= depth || instruct[0] == 0)
		{
			return false;
		}
		
		// Memory access forbidden while predicting
		//if (instruct[0] < 3 && br != -1) 
			//return false;
		
		/*System.out.println("Before: " + instruct[0] + " " + instruct[1] 
				+ " " + instruct[2] + " " + instruct[3]);*/
		
		total++;
		
		// Where to add instruction in buffer
		int dest = (next + total - 1) % depth;
		
		// If it is an overwrite 
		boolean isOverwrite = instruct[0] == 1;

		// If its opcode is an immediate operator
		boolean isIm = (instruct[0] == 3 || instruct[0] == 9 
				|| instruct[0] == 11 || instruct[0] == 16);

		isOverwrite = isOverwrite || (isIm
				&& instruct[1] != instruct[2]);

		isOverwrite = isOverwrite || (!isIm
				&& instruct[0] > 2 && instruct[0] < 17 
				&& (instruct[1] == instruct[2] ||
				instruct[1] == instruct[3]));		
		
		// If the operation is self writing
		boolean isSelfWrite = instruct[1] == instruct[2];
		isSelfWrite = isSelfWrite || (!isIm && instruct[1] 
				== instruct[3] && instruct[0] > 2 && instruct[0] < 17 );
		
		int overWrite = -1;
		
		//int new1 = instruct[1];
		
		int out[] = sim.regRename(instruct);
		
		
		if (isSelfWrite  || (isOverwrite && sim.rrt.assigned(instruct[1])))
		{
			overWrite = out[1];
			out[1] = sim.rrt.newReg(instruct[1]);
		}
		
		//System.out.println("New val:" + out[0] + " " + out[1] + " " + out[2] + " " + out[3]);

		
		if (out[0] < 17)
		{
			sim.regFile.issue(out[1]);
		}
		
		// Write to instruction buffer
		instructBuffer[dest] = out;
		
		// Store what branch
		branch[dest] = br;
		
		// Add instruction to the reorder buffer
		robLoc[dest] = sim.rob.insert(out, overWrite,br);
		
		//printContents();

		return true;
	}
	
	void printContents()
	{
		System.out.println("RESERVATION STATION " + total);

		int toPrint = next;

		for (int i = 0; i < total; i++)
		{
			System.out.println(instructBuffer[toPrint][0] + " " + instructBuffer[toPrint][1]
					+ " " + instructBuffer[toPrint][2] + " " + instructBuffer[toPrint][3]);
			toPrint++;
			if (toPrint >= depth)
				toPrint = 0;
		}

		System.out.println("-----");
	}
	
	public void tick ()
	{
		for (int i = 0; i<instructBuffer.length; i++)
		{
			//System.out.println(instructBuffer[i][0]);
		}
		this.dispatch();
		eu.tick();
	}
	
	// adds an instruction to the buffer
	void add (int [] instruct)
	{
		
	}
	
	// removes instruction at position pos
	void remove (int pos)
	{
		int c = pos + next;
		if (c >= depth)
			c = 0;
		for (int i = pos; i < total; i++)
		{
			int d = c + 1;
			if (d >= depth)
				d = 0;
			instructBuffer[c]=instructBuffer[d];
			// Location of each instruction in the reorder buffer
			robLoc[c] = robLoc[d];
			
			available[c] = available[d];
			
			c = d;
		}
		total--;
	}
	
	boolean dependency (int in)
	{
		boolean depends = false;

		int [] instruct = instructBuffer[in];
		// immediate operators
		boolean isIm = (instruct[0] == 3 || instruct[0] == 9 
				|| instruct[0] == 11 || instruct[0] == 16);

		if (!sim.regFile.isFree(instruct[2]))
			depends = true;

		if (!isIm && instruct[0] > 2 && instruct[0] < 17 )
			if (!sim.regFile.isFree(instruct[3]))
				depends = true;
		
		return depends;
	}
	
	void dispatch ()
	{
		if (!eu.isFree() || total == 0)
			return;
		
		int p = next;
		
		for (int i = 0; i<1; i++)
		{
			p = next + i;
			if (p >= depth)
				p = 0;
				
			if (!dependency(p))
			{
				eu.read(instructBuffer[next][0], instructBuffer[next][1], instructBuffer[next][2], 
						instructBuffer[next][3], robLoc[next], -1);
				remove(next);
				
				break;
			}
		}
	}

	// Takes a branch value, flushes any value within it
	public void flush (int br)
	{
		// Flush the EU
		eu.flush(br);
		// Remove records from buffer
		int i = 0;
		int j = 0;
		int rm = 0;
		while (i < total)
		{
			int pos = next + i;
			pos = pos % total;
			System.out.println(instructBuffer[pos][0]);
			boolean clearing = (branch[pos] == br);
			if (clearing)
			{
				j++;
				//total--;
				System.out.println("PURGING");
				rm++;
			}
			int pos2 = next + j;
			pos2 = pos2 % total;

			instructBuffer[pos] = instructBuffer[pos2];
			branch[pos] = branch[pos2];
			robLoc[pos] = robLoc[pos2];
			available[pos] = available[pos2];

			if (!clearing)
			{
				i++;
				j++;
			}

		}
		total -= rm;
	}
	
	// Takes a branch value, confirms any value within it
		public void confirm (int br, int newID)
		{
			// Flush the EU
			eu.confirm(br, newID);
			// Update records in buffer
			int rm = 0;
			for (int i = 0; i < branch.length; i++)
			{
				if (branch[i] == br);
					branch[i] = newID;
			}
		}

	
}
