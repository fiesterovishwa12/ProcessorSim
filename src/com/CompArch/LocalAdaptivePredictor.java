package com.CompArch;

import java.util.*;


public class LocalAdaptivePredictor extends BranchPredictor{

	// Limited number of PC points
	// Done by which was accessed last?
	// FORGIVE ME FOR THIS HACK
	
	private HashMap <Integer,AdaptivePredictor> predictors = new HashMap <Integer,AdaptivePredictor>();
	private int limit;
	private int removeQueue[];
	
	LocalAdaptivePredictor(Simulator s) {
		super(s);
		setLimit(4);
	}
	
	public void setLimit(int l)
	{
		limit = l;
		removeQueue = new int[limit];
		for (int i = 0; i<0; i++)
		{
			removeQueue[i] = -1;
		}
	}


	@Override
	boolean branches(int index, int[] instruct) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void parseInstruct(int index, int[] instruct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void parseBranch(int index, boolean branched) {
		if (!predictors.containsKey(index))
		{
			AdaptivePredictor ap = new AdaptivePredictor(sim);
			ap.parseBranch(index, branched);
			addAP(index, ap);
		}
		else
			
		predictors.get(index);
	}
	
	// Add an ap for a branch instruction
	void addAP(int index, AdaptivePredictor ap)
	{
		predictors.put(index, ap);
		
		// Remove last ap if limit exceeded
		int toGo = removeQueue[limit - 1];
		
		if (predictors.containsKey(toGo))
			predictors.remove(toGo);
		
		int item = 0;
		// Increment the remove queue
		for (int i = 0; i<limit - 1; i++)
		{
			int temp = removeQueue[i+1];
			removeQueue[i+1] = item;
			item = temp;
		}
		
		removeQueue[0] = index;
	}
	
	void shiftAP (int index)
	{
		int item = -1;
		// Increment the remove queue
		for (int i = 0; i<limit - 1; i++)
		{
			if (item == index)
				break;
			int temp = removeQueue[i+1];
			removeQueue[i+1] = item;
			item = temp;
		}
		removeQueue[0] = index;
		
	}

}
