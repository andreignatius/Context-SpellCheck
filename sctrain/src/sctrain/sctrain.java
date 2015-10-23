//package sctrain;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

public class sctrain {
	
	static final Integer iterations = 10000;
	static final Double learningRate = 1.0 / iterations;
	static String firstStr;
	static String secondStr;
	static Vector < Vector < String >> sentenceCollection = new Vector < Vector < String>>();
	static HashMap <String, Double> wordWeightSet = new HashMap <String, Double>();
	static Vector < String > correctWord = new Vector < String >();
	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		try {
			firstStr = args[0];
			secondStr = args[1];
			Scanner sc = new Scanner(new FileReader(args[2]));
			String[] token;
			Vector < Vector <String>> allSurroundingWords = new Vector < Vector <String>>();
			Vector < Vector <String>> allSurroundingWords_SW = new Vector < Vector <String>>();
			Vector <String> surroundingWords = new Vector <String>();
			Vector <String> surroundingWords_SW = new Vector <String>();
			while(sc.hasNextLine()) {
				Scanner line = new Scanner(sc.nextLine());
				while (line.hasNext()) {
					token = line.next().replaceAll("[^a-zA-Z\\>>\\<< ]", "").toLowerCase().split("\\s+");
					for (int i = 0; i < token.length; i++) {
						if (!isStopWord(token[i])) {
							if (!token[i].trim().isEmpty()) {
								//System.out.println(token[i]);
								surroundingWords.add(token[i]);
							}
						}
						if(!token[i].trim().isEmpty()) {
							surroundingWords_SW.add(token[i]);
						}
					}
					
				}
				allSurroundingWords.add(surroundingWords);
				allSurroundingWords_SW.add(surroundingWords_SW);
				surroundingWords = new Vector <String>();
				surroundingWords_SW = new Vector <String>();
			}
			for (int i = 0; i < allSurroundingWords.size(); i++) {
				Vector <String> sentence = new Vector <String>();
				Vector <String> sentence_SW = new Vector <String>();
				Iterator<String> it = allSurroundingWords.get(i).iterator();
				Iterator<String> it_SW = allSurroundingWords_SW.get(i).iterator();
				while (it.hasNext()) {
			        String str = it.next();
			        String str_SW = it_SW.next();
			        sentence.add(str);
			        sentence_SW.add(str_SW);
			    }
				getCorrectWord(sentence);
				Vector <String> addToSentence = getCollocations(sentence_SW);
				for (int j = 0; j < sentence.size(); j++) {
					addUniqueElement(wordWeightSet, sentence.get(j));
				}
				for (int j = 0; j < addToSentence.size(); j++) {
					sentence.add(addToSentence.get(j));
				}
				sentenceCollection.add(sentence);
			}
			
			trainingModel();
			
			
			PrintWriter writer = new PrintWriter(args[3]);
			Iterator it = wordWeightSet.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry pair = (HashMap.Entry)it.next();
		        writer.println(pair.getKey() + " = " + pair.getValue());
		    }
		    
			writer.close();
			sc.close();
			final long endTime = System.currentTimeMillis();
			//System.out.println("Total execution time: " + (endTime - startTime) + "ms");
		} catch (Exception e) {
			//System.err.format("Exception occurred trying to read '%s'.", args[2]);
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
	
	public static void getCorrectWord(Vector <String> sentence) {
		for (int i = 1; i < sentence.size(); i++) {
			if (sentence.get(i-1).equals(">>")) {
				correctWord.add(sentence.get(i));
				return;
			}
		}
	}
	public static Vector <String> getCollocations(Vector <String> sentence) {
		Vector <String> addToSentence = new Vector <String>();
		for (int i = 1; i < sentence.size(); i++) {
			if (sentence.get(i-1).equals(">>")) {
				//correctWord.add(sentence.get(i));
				for (int j = -4; j <= 4; j++) {
					if (i + j > 0 && i + j + 1 < i - 1) {
						String concatenateStr = sentence.get(i+j) + sentence.get(i+j+1);
						addUniqueElement(wordWeightSet, concatenateStr);
						addToSentence.add(concatenateStr);
					} else if (i + j > i + 1 && i + j + 1 < sentence.size()) {
						String concatenateStr = sentence.get(i+j) + sentence.get(i+j+1);
						addUniqueElement(wordWeightSet, concatenateStr);
						addToSentence.add(concatenateStr);
					}
				}
			}
		}
		return addToSentence;
	}
	
	public static void addUniqueElement(HashMap <String, Double> tokenWeightPair, String candidate) {
		if(!tokenWeightPair.containsKey(candidate))
			tokenWeightPair.put(candidate, 0.0);
	}
	
	public static void trainingModel() {
		
		for(int it = 0; it < iterations; it++) {
			//if (it % 200 == 0) System.out.println(it);
			for (int i = 0; i  < sentenceCollection.size(); i++) {
				double z = 0;
				for (int j = 0; j < sentenceCollection.get(i).size(); j++) {
					z += wordWeightSet.get(sentenceCollection.get(i).get(j));
				}
				for (int j = 0; j < sentenceCollection.get(i).size(); j++) {
					int expectedWord = (correctWord.get(i).equals(firstStr))? 0 : 1;
					
					Double weight_k0 = wordWeightSet.get(sentenceCollection.get(i).get(j));
					Double weight_k1 = weight_k0 + learningRate * (expectedWord - ( 1 / (1 + Math.exp(-z))));

					wordWeightSet.put(sentenceCollection.get(i).get(j), weight_k1);
					
				}
			}
		}
	}
	
}

class TokenWeightPair<String, Double> {         
    public String t;
    public Double u;
    public Integer v;

    public TokenWeightPair(String t, Double u) {         
        this.t= t;
        this.u= u;
        this.v = 1;
    }
    
    public boolean equals(TokenWeightPair<String, Double> o) {
    	return this.t.equals(o.getToken());
    }
    
    public void setWeight(Double u) { this.u = u;}
    public void addOne()	{this.v++;}
    
    public String getToken() { return t; }
    public Double getWeight(){ return u; }
    public Integer getNum() { return v; }
 }