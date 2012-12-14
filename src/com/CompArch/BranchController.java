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
	public boolean read (int[] instruct)
	{
		if (!free){ //|| !sim.isRsFree()) {
			System.out.println("Branch Controller: Cannot execute instruction " + instruct[0] + " "
					+ instruct[1] + " " + instruct[2] + " " + instruct[3] + ", busy");
			return false;
		}
		
		/*System.out.println("Before: " + instruct[0] + " " +
				instruct[1] + " " + instruct[2] + 
				" " + instruct[3]);*/
		int[] renamed = sim.regRename(instruct);
		if (dependency(renamed))
		{
			System.out.println("Branch Controller: Cannot execute instruction " + instruct[0] + " "
					+ instruct[1] + " " + instruct[2] + " " + instruct[3] + ", dependencies");
			System.out.println("SPECULATING " + sim.bp.branches(sim.PC, instruct));
			return false;
		}
		/*System.out.println("After: " + renamed[0] + " " +
				renamed[1] + " " + renamed[2] + 
				" " + renamed[3]);*/
		System.out.println("GARBAL: " + renamed[0]);
		// Calculate the result based on the command number
		switch (renamed[0]) {
		case 17:
			System.out.println("BEQ " + renamed[1] + " " + renamed[2]);
			System.out.println("BEQ " + sim.regFile.get(renamed[1]) 
					+ " " + sim.regFile.get(renamed[2]) );
			if (sim.regFile.get(renamed[1]) == sim.regFile.get(renamed[2])) {
				free = false;
				out = renamed[3];
			}
			break;
		case 18:
			System.out.println("BNEQ " + renamed[1] + " " + renamed[2]);
			System.out.println("BNEQ " + sim.regFile.get(renamed[1]) 
					+ " " + sim.regFile.get(renamed[2]) );
			System.out.println("DEPENDS: " + sim.regFile.isFree(instruct[1])  
					+ sim.regFile.isFree(instruct[2]));
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
		return true;
	}
	
	boolean dependency (int[] instruct)
	{
		// Jump
		if (instruct[0] == 19)
			return false;
		
		else if (!sim.regFile.isFree(instruct[1]) || !sim.regFile.isFree(instruct[2]))
			return true;
		
		return false;
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
