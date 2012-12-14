package com.CompArch;

// A branch controlling unit

public class BranchController {
	
	Simulator sim;
	
	int out;
	
	int cycles;
	
	// If Branch Controller is available
	boolean free;
	
	BranchController (Simulator s)
	{
		sim = s;
		free = true;
	}

	// Carry out the given command
	public void read (int[] instruct)
	{
		if (!free) {
			System.out.println("Branch Controller: Cannot execute instruction" + instruct + " "
					+ instruct[1] + " " + instruct[2] + " " + instruct[3] + ", busy");
			return;
		}
		/*System.out.println("Before: " + instruct[0] + " " +
				instruct[1] + " " + instruct[2] + 
				" " + instruct[3]);*/
		int[] renamed = sim.regRename(instruct);
		/*System.out.println("After: " + renamed[0] + " " +
				renamed[1] + " " + renamed[2] + 
				" " + renamed[3]);*/
		
		// Calculate the result based on the command number
		switch (renamed[0]) {
		case 17:
			if (sim.regFile.get(renamed[1]) == sim.regFile.get(renamed[2])) {
				free = false;
				out = renamed[3];
			}
			break;
		case 18:
			if (sim.regFile.get(renamed[1]) != sim.regFile.get(renamed[2])) {
				free = false;
				out = renamed[3];
			}
			break;
		case 19:
			free = false;
			out = renamed[1];
			break;
		default:
			System.out.println("Branch Controller: Invalid instruction: " + renamed[0]);
			free = true;
			break;
		}
	}
	
	public void tick ()
	{
		if (free)
			return;
		sim.PC += out;
		out = 0;
		free = true;
	}

}
