package com.elizalde.simulacion.virtual;
import io.github.pixee.security.BoundedLineReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Vector;
public class MemoriaSuspendida {
	private Formatter memoriaSuspendida;
	File fileTest;
	String file;
	FileReader fileReader;
	FileWriter fileWriter;
	String currentProcess;
	public int processCounter = 0;
	public MemoriaSuspendida()
	{
		 fileTest = 
				new File("MemoriaSuspendida.txt");
		
		if(!fileTest.exists())
		{
			System.out.println("Creating File");
			
			try {
				memoriaSuspendida = new Formatter("MemoriaSuspendida.txt");
				System.out.println("CreatedFile");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.print("Constructor");
		}
		
	}
	public void addProcess(Vector<String> process)
	{
		processCounter++;
		
		try {
			
			/*fileReader = new FileReader(fileTest);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			int linesCounter = 0;
			while((currentProcess = bufferedReader.readLine()) != null)
					{
						linesCounter++;
					}
					*/
			fileWriter = new FileWriter(fileTest,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(process.get(0) +" "
					+process.get(1) + " "
					+process.get(2) + " "
					+process.get(3) + " "
					+process.get(4) + " "
					+process.get(5) + " "
					+process.get(6) + " "
					+process.get(7) + " "
					+process.get(8) + " "
					+process.get(9) + " "
					+process.get(10)+ " "
					+process.get(11)+ " "
					+process.get(12)+ " "
					+'\n');
			
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	public Vector<String>  getProcess()
	{
		processCounter--;
		Vector<String> fileProcess = new Vector<String>();
		try {
			fileReader = new FileReader(fileTest);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			currentProcess = BoundedLineReader.readLine(bufferedReader, 5_000_000);
			if(currentProcess != null)
				{
				if(currentProcess.length() > 10)
				{
					String str = "";
					for(int i = 0; i < currentProcess.length();i++)
					{
						
						if(currentProcess.charAt(i)!= ' ')
						{
							str += currentProcess.charAt(i);
						}
						else
						{
							fileProcess.add(str);
							str = "";
						}
					}
				}
				
				String allProcesses = "";
				currentProcess = "";
				while((currentProcess = BoundedLineReader.readLine(bufferedReader, 5_000_000)) != null)
				{
					allProcesses += currentProcess + '\n';
					
				}	
					fileWriter = new FileWriter(fileTest);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(allProcesses);
					bufferedWriter.close();
				
				System.out.println("Get Function From Memory Class All Processes: " + allProcesses);
			}
			else bufferedReader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("Process from File: " + fileProcess);
		return fileProcess;
	}
	public void addFirst(Vector<String> process)
	{
		processCounter++;
		System.out.println("Adding to First");
		try {
			fileReader = new FileReader(fileTest);
		
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String allProcesses = "";
		while((currentProcess = BoundedLineReader.readLine(bufferedReader, 5_000_000)) != null)
		{
			allProcesses += currentProcess +'\n';
		}
		
		String firstProcess = "";
		firstProcess += process.get(0) + " ";
		firstProcess += process.get(1) + " ";
		firstProcess += process.get(2) + " ";
		firstProcess += process.get(3) + " ";
		firstProcess += process.get(4) + " ";
		firstProcess += process.get(5) + " ";
		firstProcess += process.get(6) + " ";
		firstProcess += process.get(7) + " ";
		firstProcess += process.get(8) + " ";
		firstProcess += process.get(9) + " ";
		firstProcess += process.get(10) + " ";
		firstProcess += process.get(11) + " ";
		firstProcess += process.get(12) + " \n";
		
		firstProcess += allProcesses;
		
		fileWriter = new FileWriter(fileTest);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(firstProcess);
		bufferedWriter.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
