//package sctrain;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

public class sccompare {
	static HashMap <Integer, String> givenAnswers = new HashMap <Integer, String>();
	static HashMap <Integer, String> actualAnswers = new HashMap <Integer, String>();
	public static void main(String[] args) {
		try {
			Scanner checkAnswers = new Scanner(new FileReader(args[4]));
			while (checkAnswers.hasNextLine()) {
				Scanner line = new Scanner(checkAnswers.nextLine());
				Integer key = Integer.parseInt(line.next());
				String value = line.next();
				givenAnswers.put(key, value);
			}
			
			Scanner correctAnswers = new Scanner(new FileReader(args[5]));
			while (correctAnswers.hasNextLine()) {
				Scanner line = new Scanner(correctAnswers.nextLine());
				Integer key = Integer.parseInt(line.next());
				String value = line.next();
				actualAnswers.put(key, value);
			}
			
			int correctCount = 0;
			int totalCount = 0;
			Iterator itCheckAns = givenAnswers.entrySet().iterator();
			Iterator itActualAns = actualAnswers.entrySet().iterator();
		    while (itCheckAns.hasNext()) {
		        HashMap.Entry pairCheckAns = (HashMap.Entry)itCheckAns.next();
		        HashMap.Entry pairActualAns = (HashMap.Entry)itActualAns.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		    	if (pairCheckAns.getValue().equals(pairActualAns.getValue()))
		    		correctCount++;
		        totalCount++;
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		    //System.out.printf("%d out of %d correct\n", correctCount, totalCount);
			correctAnswers.close();
			checkAnswers.close();
		} catch (Exception e) {
			//System.err.format("Exception occurred trying to read '%s'.", args[0]);
		    e.printStackTrace();
		}
		
		
	}
}
