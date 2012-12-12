package com.CompArch;
import java.util.*;

public class NeuralPredictor extends BranchPredictor{

	private float Vt[];
	private float Vnt[];
	private float Hg[];
	private float a;
	
	HashMap<Integer, Boolean>predictions = new HashMap<Integer, Boolean>();
	
	NeuralPredictor(Simulator s) {
		super(s);
		setLength(16);
		setA(0.2f);
	}
	
	public void setLength(int length)
	{
		Hg = new float[16];
		Vt = new float[16];
		Vnt = new float[16];
		
		// Initialise the vectors
		for (int i = 0; i < Hg.length; i++)
		{
			Vt[i] = 1;
			Vnt[i] = 0;
		}
	}
	
	public void setA(float in)
	{
		a = in;
	}

	@Override
	boolean branches(int index, int[] instruct) {
		// Get hamming distances
		
		boolean result;
		
		float hamNt = Hamming(Vnt, Hg);
		
		float hamT = Hamming(Vt, Hg);
		
		result = (hamT >= hamNt);
		
		predictions.put(index, result);
		
		return result;
	}

	@Override
	void tick() {
	}

	@Override
	void parseInstruct(int index, int[] instruct) {
	}

	@Override
	void parseBranch(int index, boolean branched) {
		for (int i = Hg.length - 1; i> 0; i--)
		{
			Hg[i] = Hg [i-1];
		}
		
		if (branched)
			Hg[0] = 1;
		else
			Hg[0] = 1;
		
		// Check prediction and adjust appropriately
		boolean predict = predictions.get(index);
		
	}
	
	float [] newVal (boolean correct, float[] Vw)
	{
		float Vw2[] = new float[Vw.length];

		int flip = 1;

		if (!correct)
			flip = -1;

		for (int i = 0; i < Vw.length; i++)
		{
			Vw2[i] = Vw[i] + flip*a*(Hg[i] - Vw[i]);
		}

		return Vw2;
	}
	
	float Hamming(float[] a, float[]b)
	{
		float result = 0;
		
		for (int i = 0; i < a.length; i++)
			result += Math.pow(a[i]-b[i],2);
			
		return result;
	}

}
