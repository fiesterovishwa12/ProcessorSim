package com.CompArch;

// Saturating counter with 4 states
public class SaturatingCounter {
	/** State of counter, initial being 0 **/
	private int state;
	
	public SaturatingCounter() {
		state = 0;
	}
	
	boolean branches ()	{
		if (state == 0)
			return false;
		else if (state >= 3)
			return false;
		else
			return true;
	}
	
	void update (boolean branched)
	{
		if (branched)
		{
			if (state == 0)
				state = 3;
			state--;
		}
		else
		{
			if (state == 0)
				state = 2;
			state++;
		}
		
		if (state < 1)
			state = 1;
		
		if (state > 3)
			state = 3;
	}
}
