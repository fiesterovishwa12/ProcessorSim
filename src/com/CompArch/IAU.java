package com.CompArch;

// An integer arithmetic unit

public class IAU {
	
	// Output of calculation
	private int out;
	
	// Destination of output
	private int dest;
	
	Simulator sim;
	
	// Cycles remaining for current instruction
	int cycles;
	
	// If ALU is available
	boolean free;
	
	IAU (Simulator s)
	{
		sim = s;
		out = 0;
		free = true;
		cycles = 0;
	}
	
	// Calculate the given command
	public void read (int instruct, int r1, int r2, int r3)
	{
		if (!free) {
			System.out.println("Cannot execute instruction" + instruct + " "
					+ r1 + " " + r2 + " " + r3 + ", busy");
			return;
		}
		dest = r1;
		free = false;
		// Calculate the result based on the command number
		switch (instruct) {
		case 3:
			out = sim.reg[r2] + r3;
			cycles = 1;
			break;
		case 4:
			out = sim.reg[r2] + sim.reg[r3];
			cycles = 1;
			break;
		case 5:
			out = sim.reg[r2] - sim.reg[r3];
			cycles = 1;
			break;
		case 6:
			out = sim.reg[r2] * sim.reg[r3];
			cycles = 1;
			break;
		case 7:
			out = sim.reg[r2] / sim.reg[r3];
			cycles = 10;
			break;
		case 8:
			out = sim.reg[r2] & sim.reg[r3];
			cycles = 1;
			break;
		case 9:
			out = sim.reg[r2] & r3;
			cycles = 1;
			break;
		case 10:
			out = sim.reg[r2] | sim.reg[r3];
			cycles = 1;
			break;
		case 11:
			out = sim.reg[r2] | r3;
			cycles = 1;
			break;
		case 12:
			out = sim.reg[r2] ^ sim.reg[r3];
			cycles = 1;
			break;
		case 13:
			out = sim.reg[r2] >> r3;
			cycles = 1;
			break;
		case 14:
			out = sim.reg[r2] << r3;
			cycles = 1;
			break;
		case 15:
			if (sim.reg[r2] == sim.reg[r3])
				out = 0;
			else if (sim.reg[r2] > sim.reg[r3])
				out = 1;
			else
				out = -1;
			cycles = 1;
			break;
		case 16:
			if (sim.reg[r2] == r3)
				out = 0;
			else if (sim.reg[r2] > r3)
				out = 1;
			else
				out = -1;
			cycles = 1;
			System.out.println("cmpi " + sim.reg[r2] + " " + sim.reg[r3]);
			System.out.println(out);
			break;
		default:
			System.out.println("Invalid instruction: " + instruct);
			free = true;
			break;
		}
		
	}
	
	public void tick ()
	{
		if (free)
		{
			return;
		}
		
		cycles--;
		
		if (cycles == 0) {
			sim.reg[dest] = out;
			free = true;
		}
	}
}
