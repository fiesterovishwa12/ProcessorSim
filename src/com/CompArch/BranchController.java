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
	public void read (int instruct, int r1, int r2, int r3)
	{
		if (!free) {
			System.out.println("Branch Controller: Cannot execute instruction" + instruct + " "
					+ r1 + " " + r2 + " " + r3 + ", busy");
			return;
		}
		
		// Calculate the result based on the command number
		switch (instruct) {
		case 17:
			if (sim.reg[r1] == sim.reg[r2]) {
				free = false;
				out = sim.PC + r3;
			}
			break;
		case 18:
			if (sim.reg[r1] != sim.reg[r2]) {
				free = false;
				out = sim.PC + r3;
			}
			break;
		case 19:
			free = false;
			out = sim.PC + r1;
			break;
		default:
			System.out.println("Branch Controller: Invalid instruction: " + instruct);
			free = true;
			break;
		}
	}
	
	public void tick ()
	{
		if (free)
			return;
		sim.PC = out;
		free = true;
	}

}
