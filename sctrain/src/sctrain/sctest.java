//package sctrain;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

public class sctest {
	static final Double learningRate = 1.0;
	static final Integer iterations = 10000;
	static String firstStr;
	static String secondStr;
	static Vector < Vector < TokenWeightPair <String, Double>>> collocationCollection = new Vector < Vector < TokenWeightPair <String, Double>>>();
	static Vector < Vector < String >> sentenceCollection = new Vector < Vector < String>>();
	static HashMap <String, Double> wordWeightSet = new HashMap <String, Double>();
	static Vector < String > correctWord = new Vector < String >();
	public static void main(String[] args) {
		try {
			firstStr = args[0];
			secondStr = args[1];
			Scanner modelWeights = new Scanner(new FileReader(args[3]));
			while (modelWeights.hasNextLine()) {
				Scanner line = new Scanner(modelWeights.nextLine());
				String key = line.next();
				line.next();
				Double value = Double.parseDouble(line.next());
				wordWeightSet.put(key, value);
			}
			Iterator it = wordWeightSet.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry pair = (HashMap.Entry)it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		    
			Scanner sc = new Scanner(new FileReader(args[2]));
			String[] token;
			Vector < Vector <String>> allSurroundingWords = new Vector < Vector <String>>();
			Vector <String> surroundingWords = new Vector <String>();
			while(sc.hasNextLine()) {
				Scanner line = new Scanner(sc.nextLine());
				surroundingWords.add(line.next());
				while (line.hasNext()) {
					//surroundingWords.add(line.next());
					token = line.next().replaceAll("[^a-zA-Z\\>>\\<< ]", "").toLowerCase().split("\\s+");
					for (int i = 0; i < token.length; i++) {
						if (!isStopWord(token[i])) {
							if (!token[i].trim().isEmpty()) {
								//System.out.println(token[i]);
								surroundingWords.add(token[i]);
							}
						}
					}
					
				}
				allSurroundingWords.add(surroundingWords);
				surroundingWords = new Vector <String>();
			}
			int noCorrectWord = 0;
			for (int i = 0; i < allSurroundingWords.size(); i++) {
				Vector <String> sentence = new Vector <String>();
				for (Iterator<String> itr = allSurroundingWords.get(i).iterator(); itr.hasNext(); ) {
			        String str = itr.next();
			        sentence.add(str);
			        //System.out.printf("%s ", str);
			    }
				//System.out.println();
				
					for (int j = 0; j < sentence.size(); j++) {
						addUniqueElement(wordWeightSet, sentence.get(j));
					}
					sentenceCollection.add(sentence);
			}
			
			
			Vector <String> myAnswers = answeringMachine();
			PrintWriter writer = new PrintWriter(args[4]);
			//System.out.println("got my answers");
			for (int i = 0; i < myAnswers.size(); i++) {
				//System.out.printf("%04d\t%s\n", Integer.parseInt(allSurroundingWords.get(i).get(0)), myAnswers.get(i));
				writer.printf("%04d\t%s\n", Integer.parseInt(allSurroundingWords.get(i).get(0)), myAnswers.get(i));
			}
			writer.close();
			sc.close();
			modelWeights.close();
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", args[2]);
		    e.printStackTrace();
		}
		
		
	}
	
	public static boolean isStopWord(String token) {
		try {
			Scanner sc = new Scanner(new FileReader("stopwd.txt"));
			while (sc.hasNext()) {
				String str = sc.next();
				if (str.equals(token)) {
					sc.close();
					return true;
				}
			}
			sc.close();
			
		} catch (Exception e) {
			//System.out.println("cannot read stopwd");
		}
		return false;
	}
	
	public static boolean isPunctuation(String token) {
		return Pattern.matches("\\p{Punct}", token);
	}
	
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
	public static void addUniqueElement(HashMap <String, Double> tokenWeightPair, String candidate) {
		if(!tokenWeightPair.containsKey(candidate))
			tokenWeightPair.put(candidate, 0.0);
	}
	
	public static Vector <String> answeringMachine() {
		Vector <String> myAnswers = new Vector <String>();
		for (int i = 0; i  < sentenceCollection.size(); i++) {
			
			double z = 0;
			for (int j = 0; j < sentenceCollection.get(i).size(); j++) {
				z += wordWeightSet.get(sentenceCollection.get(i).get(j));
			}
			double logisticVal = 1 / (1 + Math.exp(-z));
			if(logisticVal < 0.5) {
				//choose first keyword
				myAnswers.add(firstStr);
				//System.out.printf("%d chose first word\n", i);
			} else if(logisticVal > 0.5) {
				// choose second keyword
				myAnswers.add(secondStr);
				//System.out.printf("%d chose second word\n", i);
			} else {
				//System.out.println("HAve to check model " + logisticVal);
			}
			//System.out.println();
		}
		return myAnswers;
	}
	
}

