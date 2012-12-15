package com.CompArch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.CompArch.BranchController.BranchRecord;

public class Simulator {
	
	BranchPredictor bp;
	
	// The id of the current branch being executed
	int branch = -1;
	
	// Used for output
	int maxReg = 0;
	int maxMem = 0;
	
	// Used for stats
	int cycleTotal = 0;
	
	// Program counter
	public int PC;
	
	// Instruction memory section
	public int[][]instructMem;
	
	// Data memory section
	public int[] dataMem;
	
	public RegisterFile regFile;
	
	// IAU Reservation station array
	private ReservationStation iauRS[];
	
	// The next IAU RS to use
	int nextIAU;
	
	// Memory Manager RS array
	private ReservationStation memManRS[];
	
	// Branch controller of the processor
	private BranchController bc;
	
	// Reorder buffer
	public ReorderBuffer rob;
	
	// Register Renaming Table
	public RegisterRenameTable rrt;
	
	public static void main(String[] args) {
		//System.out.println("Launching simulator");
		//System.out.println("Running program");

		Simulator sim = new Simulator(100,100,200,4);

		File file = new File(args[0]);

		//System.out.println("Assembling code");
		
		if (!file.isFile())
		{
			System.out.println("Code file did not exist");
			return;
		}
		else
		{
			assembler asm = new assembler();
			asm.read(file, "torun.out");
		}
		
		// Load instructions
		if (args.length > 0)
			sim.loadInstruct("torun.out");
		
		// If memory values given load them
		if (args.length == 2)
			sim.loadData(args[1]);
		
		//sim.testRegRename();
		
		//sim.testFlush();
		
		
		System.out.println("BEFORE:\n");
		
		System.out.println("INSTRUCTIONS\n---------");
		sim.printInstruct();
		System.out.println("---------");
		System.out.println("DATA\n---------");
		sim.printData();
		System.out.println("---------");
		
		sim.run();
		
		System.out.println("\nAFTER:\n");
		
		System.out.println("INSTRUCTIONS\n---------");
		sim.printInstruct();
		System.out.println("---------");
		System.out.println("REG\n---------");
		sim.printReg();		
		System.out.println("---------");
		System.out.println("DATA\n---------");
		sim.printData();
		System.out.println("---------");
		System.out.print("Total cycles: " + sim.cycleTotal);
	}
	
	void testRegRename()
	{
		// Test register renaming
		int i = 0;
		while (instructMem[i][0] > 0)
		{

			int [] instruct = instructMem[i];
			/*System.out.println("Before: " + instruct[0] + " " +
					instruct[1] + " " + instruct[2] + 
					" " + instruct[3]);*/

			// If it is an overwrite 

			boolean isOverwrite = instruct[0] == 1;

			// immediate operators
			boolean isIm = (instruct[0] == 3 || instruct[0] == 9 
					|| instruct[0] == 11 || instruct[0] == 16);

			isOverwrite = isOverwrite || (isIm
					&& instruct[1] == instruct[2]);

			isOverwrite = isOverwrite || (!isIm
					&& instruct[0] > 2 && instruct[0] < 19 
					&& (instruct[1] == instruct[2] ||
					instruct[1] == instruct[3]));

			System.out.println("Is an overwrite? " + isOverwrite);

			if (isOverwrite)
			{
				int overWrite = rrt.getReg(instruct[1]);
				rrt.newReg(rrt.getReg(instruct[1]));
			}
			int out[] = regRename(instruct);
			System.out.println("After:  " + out[0] + " " + out[1] + " " + out[2] + " " + out[3]);
			i++;
		}
	}
	
	Simulator (int registers, int instructions, int dataSize, int iauNum){
		// Set up components
		iauRS = new ReservationStation[iauNum];
		for (int i = 0; i < iauNum; i++)
			iauRS[i] = new ReservationStation(this, 4, new IAU(this));
		
		memManRS = new ReservationStation[1];
		for (int i = 0; i < memManRS.length; i++)
			memManRS[i] = new ReservationStation(this, 4, new MemoryManager(this));
		
		bc = new BranchController(this);
		
		// Set up branch predictor
		
		bp = new ForwardBackBranchPredictor(this);
		
		// Set up registers
		PC = 0;
		instructMem = new int[instructions][4];
		dataMem = new int[dataSize];
		
		regFile = new RegisterFile(registers);
		
		//TODO make rrt smaller than registers
		rrt = new RegisterRenameTable(registers);
		
		rob = new ReorderBuffer(this, 4*iauNum);
	}
	
	// Tick the processor
	void tick () {
		cycleTotal++;
		for (int i = 0; i < iauRS.length; i++)
		{
			//System.out.println("RS " + i);
			iauRS[i].tick();
		}
		
		for (int i = 0; i < memManRS.length; i++)
			memManRS[i].tick();
		
		bc.tick();
		rob.tick();
	}
	
	// Load values from file to instruction memory
	void loadInstruct (String file)	{
		//System.out.println("Loading Instruction File: " + file);
		try {
            BufferedReader br = new BufferedReader(new FileReader (file));
            String line = br.readLine();
            int i = 0;
            while (line != null) {
                String[] nums = line.split(" ");
                instructMem[i][0] = new Integer(nums[0]);
                instructMem[i][1] = new Integer(nums[1]);
                instructMem[i][2] = new Integer(nums[2]);
                instructMem[i][3] = new Integer(nums[3]);
                i++;
                line = br.readLine();
            }
        }
		catch (IOException e) {
            System.err.println("Error: " + e);
        }
	}
	
	// Load values from file to data memory
	void loadData (String file) {
		//System.out.println("Loading Data File: " + file);
		try {
            BufferedReader br = new BufferedReader(new FileReader (file));
            String line = br.readLine();
            int i = 0;
            while (line != null) {
                dataMem[i] = new Integer(line);
                i++;
                line = br.readLine();
            }
            maxMem = i-1;
        }
		catch (IOException e) {
            System.err.println("Error: " + e);
        }
	}
	
	void printData () {
		for (int i = 0; i <= maxMem; i++){
			System.out.println(i+": " + dataMem[i]);
		}
	}
	
	void printInstruct () {
		int i = 0;
		while (instructMem[i][0] != 0){
			System.out.println(i+": " + instructMem[i][0] + " " 
					+ instructMem[i][1] + " " + instructMem[i][2] + " " + instructMem[i][3]);
			i++;
		}
	}
	
	void printReg () {
		regFile.printReg();
		/*
		for (int i = 0; i <= maxReg; i++)
		{
			System.out.println(reg[i]);
		}
		*/
	}
	
	/* process an instruction so register values are renamed */
	int[] regRename (int instruction[])
	{
		if (instruction[0] == 19)
			return instruction;
	
		//if (true)
			//return instruction;
		int toReserve[] = new int[4];
		toReserve[0] = instruction[0];
		
		if (instruction[0] > 0 && instruction[0] < 19)
		{
			toReserve[1] = rrt.getReg(instruction[1]);
			toReserve[2] = rrt.getReg(instruction[2]);
		}
		
		boolean thirdReg = instruction[0] > 3 && instruction[0] < 9;
		thirdReg = thirdReg || instruction[0] == 10 || instruction[0] == 12  
				|| instruction[0] == 15; 
		
		if (thirdReg)
		{
			toReserve[3] = rrt.getReg(instruction[3]);
		}
		else
			toReserve[3] = instruction[3];
		
		return toReserve;
	}
	
	// Fetch decode and issue an instruction, returns true if instruction issued, otherwise false
	boolean fetch(int[] instruct) {
		boolean result;
		
		// HALT
		if (instruct[0] == 0)
			return false;
		
		// Log the maximum written to register
		if (instruct[1] > maxReg && instruct[0] > 0 && instruct[0] < 19)
			maxReg = instruct[1];
		
		// Memory load
		if (instruct[0] <= 2)
		{
			if (isRsFree())
				result = memManRS[0].receive(instruct,branch);
			else
				return false;
			//mem(instruct);
		}	
		
		
		// IAU instructions
		else if (instruct[0] <= 16) {
			
			// IAU OPERATIONS
			int to = getIAU();
			
			if (to == -1)
				return false;
			
			result = iauRS[getIAU()].receive(instruct,branch);
		}
		else {
			// Handle branch
			//System.out.println("Branching :" + bp.branches(PC, instruct));
			result = bc.read(instruct);
			if (result)
				bc.run();
		}
		
/*		if(result)
			System.out.println("Doing: " + instruct[0] + " " + instruct[1] 
					+ " " + instruct[2] + " " + instruct[3]);*/
		
		return result;
	}	
	
	int getIAU()
	{
		
		int result = -1;

		for (int i = 0; i < iauRS.length; i++)
		{
			
			if (iauRS[nextIAU].isFree())
			{
				result = nextIAU;
				break;
			}
			nextIAU++;
			if (nextIAU >= iauRS.length)
				nextIAU = 0;
		}
		return result;
	}
	
	// Are reservation stations free?
	
	boolean isRsFree()
	{
		boolean result = true;
		for (int i = 0; i < iauRS.length; i++)
		{
			if (!iauRS[i].isFree())
				result = false;
		}
		for (int i = 0; i < memManRS.length; i++)
		{
			if (!memManRS[i].isFree())
				result = false;
			//bc.tick();
		}
		return result;
	}
	
	// Run the processor with the current instruction and memory content
	void run () {
		
		// check if any reservation stations are free
		boolean rsFree = isRsFree();
		
		while (instructMem[PC][0] != 0 || !rsFree || !bc.isFree() || !rob.isFree()) {
			for (int i = 0; i < iauRS.length; i++)
			{
				boolean next = false;
				next = fetch(instructMem[PC]);
				//System.out.println("AT " + i);
				//boolean isIAU = (instructMem[PC][0] < 17 && instructMem[PC][0] > 2);
				if (next)
				{
					System.out.println("BR: " + branch);
					PC++;
				}
				else
				{
					//System.out.println("NO TO " + instructMem[PC][0]);
					break;
				}
				/*if (!isIAU)
				{
					System.out.println("NOT IAU");
					System.out.println("PC: " + PC);
					//break;
				}*/
			}
			tick();
			//System.out.println("PC: " + PC);
			rsFree = isRsFree();
		}
		//System.out.println("Halting " + iauRS[0].isFree());
		System.out.println("BC");
		System.out.println(bc.buffer);
	}

	int getNWay()
	{
		return iauRS.length;
	}
	
	void flush (int id)
	{
		System.out.println("FLUSHING: " + id);
		//cycleTotal++;
		for (int i = 0; i < iauRS.length; i++)
		{
			//System.out.println("RS " + i);
			iauRS[i].flush(id);
		}
		
		rob.flush(id);			
		
		bc.flush(id);
	}
	
	void confirm (int id, int newId)
	{
		System.out.println("Confirming: " + id);
		for (int i = 0; i < iauRS.length; i++)
		{
			//System.out.println("RS " + i);
			iauRS[i].confirm(id,newId);
		}
		
		rob.confirm(id,newId);			
		
		bc.confirm(id, newId);
	}

	void testFlush ()
	{

		System.out.println("TESTING THE FLUSH");
		int [] instruct = {1, 1, 1, 1};
		iauRS[0].receive(instruct, -1);
		instruct[0] = 2;
		iauRS[0].receive(instruct, -1);
		instruct[0] = 3;
		iauRS[0].receive(instruct, -1);
		instruct[0] = 4;
		iauRS[0].receive(instruct, -1);
		iauRS[0].printContents();

		iauRS[0].flush(1);
		System.out.println("FLUSHED:");
		iauRS[0].printContents();

		System.out.println("--------------------");

		iauRS[0].eu.read(3, 1, 1, 1, 1, 1);
		iauRS[0].eu.print();
		iauRS[0].flush(1);
		iauRS[0].eu.print();
		
        System.out.println("--------------------");

		int [] instruct2 = {1, 2, 1, 1};
		rob.insert(instruct2, -1,-1);
		instruct2[0] = 2;
		rob.insert(instruct2, -1,-1);
		instruct2[0] = 3;
		rob.insert(instruct2, -1,-1);
		instruct2[0] = 4;
		rob.insert(instruct2, -1,-1);
		rob.printBuffer();
		rob.flush(1);
		rob.printBuffer();
		
		branch = 7;
		int [] instruct3 = {1, 2, 1, 1};
		BranchRecord dor = new BranchRecord(0, instruct3.clone(), true,this);
		bc.buffer.add(dor);
		
		branch = -1;
		instruct3[0] = 2;
		dor = new BranchRecord(1, instruct3.clone(), true,this);
		bc.buffer.add(dor);
		
		branch = 0;
		instruct3[0] = 3;
		dor = new BranchRecord(2, instruct3.clone(), true,this);
		bc.buffer.add(dor);
		
		branch = -1;
		instruct3[0] = 4;
		dor = new BranchRecord(3, instruct3.clone(), true,this);
		bc.buffer.add(dor);
		
		System.out.println(bc.buffer);
		bc.flush(7);
		System.out.println(bc.buffer);

	}


}
