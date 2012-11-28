package com.CompArch;

public class ReservationStation {

	Simulator sim;
	IAU iau;
	
	int depth;
	
	// Location in the 
	int next;
	int total;
	
	// Instruction memory section
	public int[][]instructBuffer;
	
	public boolean isFree ()
	{
		if (total > 0)
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
		iau = new IAU(s);
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
					instructBuffer[next][3]);
			next++;
			next = next % depth;
			total--;
		}
			
	}
	
	
}
