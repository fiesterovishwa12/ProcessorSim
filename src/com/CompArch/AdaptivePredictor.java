package com.CompArch;
import java.lang.Math;


public class AdaptivePredictor extends BranchPredictor {

	boolean history[];
	SaturatingCounter counters[];
	
	AdaptivePredictor(Simulator s) {
		super(s);
		setHistory(4);
	}

	@Override
	boolean branches(int index, int[] instruct) {
		int histNum = getHistNum();	
		return counters[histNum].branches();
	}

	@Override
	void tick() {
		
	}

	@Override
	void parseInstruct(int index, int[] instruct) {
		
	}

	@Override
	void parseBranch(int index, boolean branched) {
		
		// Update state machine
		int histNum = getHistNum();	
		counters[histNum].update(branched);
		
		// Update history
		for (int i = 0; i < history.length - 1; i++)
			history[i] = history[i + 1];

		history[history.length-1] = branched;
		
		
	}

	/* Based on http://eggeral.blogspot.co.uk/2008/04/converting-boolean-to-int-in-java.html
	 * Alexander Egger 2008
	**/
	int getHistNum()
	{
		int result = 0;
		for (int i = 0; i < history.length; i++)
		{
			int value = (history[history.length - i - 1] ? 1 : 0) << i;
			result = result | value;
		}
		return result;
	}

	
	
	/** sets the length of the predictor's history, wipes existing history 
	 ** WARNING need to store 2^length values**/
	public void setHistory (int length)
	{
		history = new boolean[length];
		for (int i = 0; i < length; i++)
			history[i] = false;
		
		int cntr = (int) Math.pow(2.0, length);
		counters = new SaturatingCounter[cntr];
		for (int i = 0; i < cntr; i++)
			counters[i] = new SaturatingCounter();
	}
	
}
