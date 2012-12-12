package com.CompArch;
import java.io.*;
import java.util.*; 
import java.util.ArrayList; 

public class assembler
{
    int lineNum = 1;
    int docNum = 1;
    ArrayList<Integer> cmd = new ArrayList<Integer>();
    ArrayList<Integer> dest = new ArrayList<Integer>();
    ArrayList<Integer> in1 = new ArrayList<Integer>();
    ArrayList<Integer> in2 = new ArrayList<Integer>();
    HashMap<String, Integer> labels = new HashMap<String, Integer>();

    public static void main (String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Enter code file and destination");
            return;
        }
        
        File file = new File(args[0]);
        
        if (!file.isFile())
        {
            System.out.println("Code file did not exist");
        }
        else
        {
            assembler asm = new assembler();
            asm.read(file, args[1]);
        }
    }
    
    public void read(File file, String out)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader (file));
            String line = br.readLine();
            
            while (line != null) {
                lineIn1(line);
                line = br.readLine();
                docNum++;
            }
        }
        catch (IOException e) {
            System.err.println("Error: " + e);
        }

        lineNum = 1;
        docNum = 1;
        

        try {
            docNum = 1;
            //System.out.println("Writing to:" + out);
            BufferedReader br = new BufferedReader(new FileReader (file));
            String line = br.readLine();
            
            while (line != null) {
                lineIn2(line);
                line = br.readLine();
                docNum++;
            }
        }
        catch (IOException e) {
            System.err.println("Error: " + e);
        }

        try {
              // Create file 
              FileWriter fstream = new FileWriter(out);
              BufferedWriter outFile = new BufferedWriter(fstream);

              for (int i = 0; i < cmd.size(); i++)
                outFile.write(cmd.get(i) + " " + dest.get(i) + " " + in1.get(i) + " " + in2.get(i) + "\n");
              //Close the output stream
              outFile.close();
            }
        catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    // Just handles input of 
    private void lineIn1 (String line)
    {
        if (line.startsWith("//"))
            return;
        if (line.replace(" ", "").length() == 0)
            return;
        // Handle labels
        if (line.endsWith(":"))
        {
            // Store the label number
            String label = line.substring(0, line.length() - 1);
            if (labels.containsKey(label))
                System.err.println("DUPLICATE LABEL " + label + " AT " + docNum);
            else
            {
                labels.put(label, lineNum);
            }
            return;
        }
        lineNum++;
    }
       
    private void lineIn2 (String line)
    {
        if (line.startsWith("//"))
            return;
        if (line.replace(" ", "").length() == 0)
            return;
        // Handle labels
        if (line.endsWith(":"))
{
//System.out.print(line + " ");
            return;
}

        //System.out.println(lineNum + ": " + line);
        
        String[] input = line.split(" ");
        
        Integer c = getVal(input[0]);
        if (c < 0)
        {
            System.err.println("INVALID INPUT '" + input[0] + "' at line " + docNum);
        }
        Integer d = 0;
        Integer i1 = 0;
        Integer i2 = 0;
        if (input.length > 1 && isInteger(input[1]))
            d = new Integer(input[1]);
        if (input.length > 2 && isInteger(input[2]))
            i1 = new Integer(input[2]);
        if (input.length > 3 && isInteger(input[3]))
            i2 = new Integer(input[3]);

        // Handle breaks

        if (c == 17 || c == 18) {
            String label = input[3];
            int dest = labels.get(label);
            i2 = dest - lineNum - 1;
        }

        // Handle jumps
        
        if (c == 19) {
            String label = input[1];
            int dest = labels.get(label);
            d = dest - lineNum - 1;
        }

        cmd.add(c);
        dest.add(d);
        in1.add(i1);
        in2.add(i2);
        
        //System.out.println(c + " " + d  + " " + i1  + " " + i2);
        
        lineNum++;
    }
    
    public boolean isInteger( String input ) {
    try {
        Integer.parseInt( input );
        return true;
    }
    catch( Exception e ) {
        return false;
    }
}
    
    private Integer getVal (String cmd)
    {
        String in = cmd.toLowerCase();
        if(in.equals("halt"))
            return 0;
        if(in.equals("ld"))
            return 1;
        if(in.equals("write")) 
            return 2;
        if(in.equals("addim")) 
            return 3;
        if(in.equals("add"))
            return 4;
        if(in.equals("sub"))
            return 5;
        if(in.equals("mul"))
            return 6;
        if(in.equals("div"))
            return 7;
        if(in.equals("&"))
            return 8;
        if(in.equals("&im"))
            return 9;
        if(in.equals("or")) 
            return 10;
        if(in.equals("orim")) 
            return 11;
        if(in.equals("xor")) 
            return 12;
        if(in.equals(">>")) 
            return 13;
        if(in.equals("<<")) 
            return 14;
        if(in.equals("cmp")) 
            return 15;
        if(in.equals("cmpi")) 
            return 16;
        if(in.equals("beq")) 
            return 17;
        if(in.equals("bneq")) 
            return 18;
        if(in.equals("jmp")) 
            return 19;

        return -1;
    }
}
