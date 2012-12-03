package com.CompArch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Simulator {
	
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
	
	// Reservation station array
	private ReservationStation rs[];
	
	// Branch controller of the processor
	private BranchController bc;
	
	// Reorder buffer
	public ReorderBuffer rob;
	
	// Register Renaming Table
	public RegisterRenameTable rrt;
	
	public static void main(String[] args) {
		System.out.println("Launching simulator");
		System.out.println("Running program");

		Simulator sim = new Simulator(100,100,200,1);

		File file = new File(args[0]);

		System.out.println("Assembling code");
		
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
	
	Simulator (int registers, int instructions, int dataSize, int rsNum){
		// Set up components
		rs = new ReservationStation[rsNum];
		for (int i = 0; i < rsNum; i++)
			rs[i] = new ReservationStation(this, 4);
		
		bc = new BranchController(this);
		
	
		// Set up registers
		PC = 0;
		instructMem = new int[instructions][4];
		dataMem = new int[dataSize];
		
		regFile = new RegisterFile(registers);
		
		//TODO make rrt smaller than registers
		rrt = new RegisterRenameTable(registers);
		
		rob = new ReorderBuffer(this, 4*rsNum);
	}
	
	// Tick the processor
	void tick () {
		cycleTotal++;
		for (int i = 0; i < rs.length; i++)
			rs[i].tick();
		bc.tick();
		rob.tick();
	}
	
	// Load values from file to instruction memory
	void loadInstruct (String file)	{
		System.out.println("Loading Instruction File: " + file);
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
		System.out.println("Loading Data File: " + file);
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
	
	int[] regRename (int instruction[])
	{
		/* Get the register values if needed*/
		int toReserve[] = new int[4];
		toReserve[0] = instruction[0];
		
		// Check if instruction is an overwrite
		boolean isWrite = instruction[0] == 1;
		boolean isWipe = instruction[0] == 5 && instruction[1] == instruction[2] 
				&& instruction[2] == instruction[3]; 
		
		int overWrite = -1;
		
		if (isWrite || isWipe)
		{
//TODO
		}
		
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
	
	// Fetch decode and execute an instruction, returns true if instruction executed, otherwise false
	boolean fetch(int[] instruct) {
		boolean result;
		
		// Log the maximum written to register
		if (instruct[1] > maxReg && instruct[0] != 0 && instruct[0] != 19)
			maxReg = instruct[1];
		
		// Memory load
		if (instruct[0] <= 2)
		{
			result = true;
			mem(instruct);
		}
		
		// IAU instructions - TODO currently all sent to one RS
		else if (instruct[0] <= 16) {
			//result = iau.free;
			//if (result)
				//iau.read(instruct[0], instruct[1], instruct[2], instruct[3]);
			result = rs[0].receive(instruct);
		}
		else {
			result = bc.free;
			if (result)
				bc.read(instruct[0], instruct[1], instruct[2], instruct[3]);
		}
		
		return result;
	}
	
	// Handles memory loads and writes
	void mem(int[] instruct)
	{
		// is op an overwrite?
		int overWrite = -1;
		if (instruct[0] == 1)
		{
			overWrite = rrt.getReg(instruct[1]);
			rrt.newReg(rrt.getReg(instruct[1]));
		}
		
		int robIndex = rob.insert(instruct, overWrite);
		
		// Increment the clock for the memory access (cost - 1)
		cycleTotal += 3;
		if (instruct[0] == 1){
			regFile.set(instruct[1], dataMem[regFile.get(instruct[2]) + instruct[3]]);
		}
		else if (instruct[0] == 2){
			dataMem[regFile.get(instruct[2]) + instruct[3]] = regFile.get(instruct[1]);
			// Increment max mem
			if (regFile.get(instruct[2]) + instruct[3] > maxMem)
				maxMem = regFile.get(instruct[2]) + instruct[3];
		}
		
		rob.setResult(robIndex, 0);
	}	
	
	// Are reservation stations free?
	
	boolean isRsFree()
	{
		boolean result = true;
		for (int i = 0; i < rs.length; i++)
		{
			if (!rs[i].isFree())
				result = false;
		}
		return result;
	}
	
	// Run the processor with the current instruction and memory content
	void run () {
		
		// check if any reservation stations are free
		boolean rsFree = isRsFree();
		
		while (instructMem[PC][0] != 0 || !rsFree /* !iau.free */|| !bc.free) {
			boolean next = false;
			if (rsFree/* iau.free*/ && bc.free)
				next = fetch(instructMem[PC]);
			tick();
			rsFree = isRsFree();
			if (next)
				PC++;
		}
		System.out.println("Halting " + rs[0].isFree());
	}
}
