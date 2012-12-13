package com.CompArch;

public class ReservationStation {

	private Simulator sim;
	private ExecutionUnit eu;
	
	private int depth;
	
	// Location in the 
	private int next;
	private int total;
	
	// Instruction memory section
	private int[][] instructBuffer;
	
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
		robLoc = new int[size];
		eu = e;
		available = new boolean[size][2];
		for (int i = 0; i < size; i++) {
			available[i][0] = false;
			available[i][1] = false;
		}
		sim = s;
	}
	
	// Takes instruction, returns true if added to buffer, false if buffer full
	public boolean receive (int[] instruct)
	{
		if (total == depth)
		{
			return false;
		}
		
		total++;
		
		// Where to add instruction
		int dest = (next + total - 1) % depth;
		
		// If it is an overwrite 
		boolean isOverwrite = instruct[0] == 1;

		// immediate operators
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
		
		if (isOverwrite && sim.rrt.assigned(instruct[1]))
		{
				//System.out.println("REGISTER " + instruct[1]);
				overWrite = sim.rrt.getReg(instruct[1]);
				//System.out.println("OVERWRITING: " + overWrite);
				int new1 = sim.rrt.newReg(instruct[1]);
				//System.out.println("WITH: " + new1 + ":" + sim.rrt.getReg(instruct[1]));
		}
				
		int out[] = sim.regRename(instruct);
		
		if (isSelfWrite && !(isOverwrite && sim.rrt.assigned(instruct[1])))
		{
			overWrite = out[1];
			out[1] = sim.rrt.newReg(instruct[1]);
		}
		
		if (out[0] < 19)
		{
			System.out.println(out[0] + " " + out[1] + " " + out[2] + " " + out[3]);
			sim.regFile.issue(out[1]);
		}

		// Write to instruction buffer
		instructBuffer[dest] = out;
		
		// Add instruction to the reorder buffer
		robLoc[dest] = sim.rob.insert(out, overWrite);

		return true;
	}
	
	public void tick ()
	{
		for (int i = 0; i<instructBuffer.length; i++)
		{
			System.out.println(instructBuffer[i][0]);
		}
		this.dispatch();
		eu.tick();
	}
	
	void dispatch ()
	{
		boolean depends = false;

		int [] instruct = instructBuffer[next];
		// immediate operators
		boolean isIm = (instruct[0] == 3 || instruct[0] == 9 
				|| instruct[0] == 11 || instruct[0] == 16);
		
		if (!sim.regFile.isFree(instruct[2]))
		{
			depends = true;
			//System.out.println("Waiting on: " + instruct[1]);
		}

		if (!isIm && instruct[0] > 2 && instruct[0] < 17 )
			if (!sim.regFile.isFree(instruct[3]))
			{
				depends = true;
				//System.out.println("Waiting on: " + instruct[2]);
			}

		depends = false;

		// perform dependancy and eu availability checking, if ready then send
		/*System.out.println(instructBuffer[next][0] + " " + instructBuffer[next][1] + " " + 
				instructBuffer[next][2] + " " + instructBuffer[next][3]);*/
		
		/*
		if (!sim.regFile.isFree(instructBuffer[next][2]))
		{
			depends = true;
			System.out.println(instructBuffer[next][2] + " NOT FREE1");
		}
		int in = instructBuffer[next][0];
		if (in != 3 && in != 9 && in != 11 && in != 13 && in != 14 && in != 16)
		{
			if (!sim.regFile.isFree(instructBuffer[next][3]))
			{
				depends = true;
				System.out.println(instructBuffer[next][3] + " NOT FREE2");
			}
		}
		depends = false;*/
		
		if (eu.isFree() && total > 0 && !depends)
		{
			//System.out.println(instructBuffer[next][2] + " " + instructBuffer[next][3] + " FREE");
			/*System.out.println("WORKING: " + total);
			System.out.println("running: " + instructBuffer[next][0] + " " + instructBuffer[next][1]
					+ " " + instructBuffer[next][2] + " " + instructBuffer[next][3]);*/
			
			eu.read(instructBuffer[next][0], instructBuffer[next][1], instructBuffer[next][2], 
					instructBuffer[next][3], robLoc[next]);
			next++;
			next = next % depth;
			total--;
		}
		
		//System.out.println("---");
			
	}
	
	
}
