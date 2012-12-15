package com.CompArch;

import java.util.LinkedList;

// A branch controlling unit

public class BranchController {
	
static class BranchRecord{
		int id;
		int [] instruct;
		boolean taken;
		int PC;
		int branch;
		
		public BranchRecord(int in, int [] instruction, boolean take, Simulator sim) {
			id = in;
			instruct = instruction;
			taken = take;
			PC = sim.PC;
			branch = sim.branch;
		}


		public String toString() {
			return "ID: " + id + ", Instruction: " + instruct[0] + " "
					+ instruct[1] + " " + instruct[2] + " " + instruct[3] 
							+ ", Taken: " + taken + ", PC: " + PC + " Branch: " + branch + "\n";
		}
	}
	
	boolean tagged = false;

	LinkedList<BranchRecord> buffer = new LinkedList<BranchRecord>();

	int nextID = 0;

	
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
			
			// 
			if (buffer.size() > 0)
			{
				if (buffer.getLast().PC != sim.PC)
					tagged = false;
			}
			else
				tagged = false;
				
			if (!tagged )
			{
				buffer.add(new BranchRecord(nextID, renamed,sim.bp.branches(sim.PC, instruct),sim));
				tagged = true;
				sim.branch = nextID;
				nextID++;
			}
			if (sim.bp.branches(sim.PC, instruct))
			{
				// Run unconditionally
				//out = renamed[3];
			}

			return false;
		}
		/*System.out.println("After: " + renamed[0] + " " +
				renamed[1] + " " + renamed[2] + 
				" " + renamed[3]);*/
		//System.out.println("GARBAL: " + renamed[0]);
		// Calculate the result based on the command number
		switch (renamed[0]) {
		case 17:
			/*System.out.println("BEQ " + renamed[1] + " " + renamed[2]);
			System.out.println("BEQ " + sim.regFile.get(renamed[1]) 
					+ " " + sim.regFile.get(renamed[2]) );*/
			if (sim.regFile.get(renamed[1]) == sim.regFile.get(renamed[2])) {
				free = false;
				out = renamed[3];
			}
			break;
		case 18:
			/*System.out.println("BNEQ " + renamed[1] + " " + renamed[2]);
			System.out.println("BNEQ " + sim.regFile.get(renamed[1]) 
					+ " " + sim.regFile.get(renamed[2]) );
			System.out.println("DEPENDS: " + sim.regFile.isFree(instruct[1])  
					+ sim.regFile.isFree(instruct[2]));*/
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
	
	public void run ()
	{
		if (free)
			return;
		sim.PC += out;
		out = 0;
		free = true;
	}
	
	public void tick ()
	{
		// Check the current buffer
		if (buffer.size() == 0)
			return;
		
		BranchRecord rec = buffer.getFirst();
		
		if (!dependency(rec.instruct))
		{
			if (rec.taken == didRun(rec.instruct))
			{
				System.out.println("GOT IT RIGHT!!!!");
			}
			else
			{
				System.out.println("GOT IT WRONG!!!!");
				/*sim.flush(rec.id);
				sim.PC = rec.PC;
				if (didRun(rec.instruct))
					sim.PC += rec.instruct[3];*/
			}
			sim.branch = rec.branch;
			System.out.println(buffer);
			buffer.remove(0);
		}
	}
	
	void flush (int id)
	{
		int i = 0;
		while (i < buffer.size())
		{
			if (buffer.get(i).branch == id)
			{
				sim.flush(buffer.get(i).id);
				buffer.remove(i);
			}
			else
				i++;
		}
	}
	
	boolean didRun(int instruct[])
	{
		if ((instruct[0] == 17 && sim.regFile.get(instruct[1]) == sim.regFile.get(instruct[2])) || 
				(instruct[0] == 18 && sim.regFile.get(instruct[1]) != sim.regFile.get(instruct[2])))
		{
			return true;
		}
		return false;
	}

}
