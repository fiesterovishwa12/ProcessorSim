package com.CompArch;

public class ReservationStation {

	private Simulator sim;
	private IAU iau;
	
	private int depth;
	
	// Location in the 
	private int next;
	private int total;
	
	// Instruction memory section
	private int[][]instructBuffer;
	
	// Location of each instruction in the reorder buffer
	private int robLoc[];
	
	public boolean isFree ()
	{
		if (total > 0 || !iau.free)
			return false;
		else
			return true;
	}

	public ReservationStation (Simulator s, int size)
	{
		depth = size;
		next = 0;
		total = 0;
		instructBuffer = new int[size][4];
		robLoc = new int[size];
		iau = new IAU(s);
		sim = s;
	}
	
	// Takes instruction, returns true if added to buffer, false if buffer full
	public boolean receive (int[] instruction)
	{
		if (total == depth)
		{
			return false;
		}
		
		total++;
		
		int dest = (next + total - 1) % depth;
		/*System.out.println("--");
		System.out.println(next + " " + total + " " + depth);
		System.out.println((next + total - 1) % depth);
		System.out.println("--");*/
		instructBuffer[dest] = instruction;
		
		robLoc[dest] = sim.rob.insert(instruction);
		
		return true;
	}
	
	public void tick ()
	{
		this.dispatch();
		iau.tick();
	}
	
	void dispatch ()
	{
		// perform dependancy and IAU availability checking, if ready then send
		
		if (iau.free && total > 0)
		{
			/*System.out.println("WORKING: " + total);
			System.out.println("running: " + instructBuffer[next][0] + " " + instructBuffer[next][1]
					+ " " + instructBuffer[next][2] + " " + instructBuffer[next][3]);*/
			
			iau.read(instructBuffer[next][0], instructBuffer[next][1], instructBuffer[next][2], 
					instructBuffer[next][3], robLoc[next]);
			next++;
			next = next % depth;
			total--;
		}
			
	}
	
	
}
