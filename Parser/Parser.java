
/* Predictive top down Parser for Parsing grammar of subset of C programming language 
 Author: Maryam Bashir
 Date: 1st April 2015
 */


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;


public class Parser {

	public HashMap<String,HashMap<String,String>>  parseTable = new HashMap<String,HashMap<String,String>>();  
	public HashMap<String,String[]>  grammarMap = new HashMap<String,String[]>();
	String startSymbol = "Program";
	String [] inputTokens;


	// This function reads input tokens from a file and stores it in a global string array called inputTokens
	@SuppressWarnings("deprecation")
	void readInput(File f)
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			int count=0;
			String s="";
			while (dis.available() != 0) {
				s += dis.readLine()+" ";



			}
			s+="$";  // Insert $ at end of input for matching wit $ in symbol in stack
			inputTokens = s.split(" ");
			for(int j=0;j<inputTokens.length;j++)
			{
				inputTokens[j]  = inputTokens[j].replaceAll("\\u00a0", "");
			}


			fis.close();
			bis.close();
			dis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// This function reads parse table from a file. The productions should be tab separated 
	//		and each symbol within a production should be space separated		
	void readParseTable(File f)
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			int line = 0; 
			String [] tokens = {};
			while (dis.available() != 0) {
				String s = dis.readLine();
				line ++;
				String [] str = s.split("\t");
				if(line == 1)
				{
					tokens = new String[str.length-1]; 
					for(int i=1;i<str.length;i++)
					{
						tokens[i-1] = str[i];
					}
				}
				else
				{
					String nonTerminal = str[0].replaceAll("\\u00a0", "");
					HashMap production = new HashMap();
					for(int i=1;i<str.length;i++)
					{
						production.put(tokens[i-1],str[i]);
					}

					for(int i=str.length-1;i<tokens.length;i++)
					{
						production.put(tokens[i],"");
					}

					parseTable.put(nonTerminal,production);
				}

			}



			fis.close();
			bis.close();
			dis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// This function reads grammar from a file
	@SuppressWarnings("deprecation")
	void readGrammar(File f)
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			while (dis.available() != 0) {
				String s = dis.readLine();
				if(s.length() > 0)
				{
					String [] str = s.split("\\.");

					String num =  str[0];
					String [] grammar = str[1].split("-->");
					String [] rhs = grammar[1].split("\\|");


						String [] production = rhs[0].split(" ");
						for(int j=0;j<production.length;j++)
						{
							production[j]  = production[j].replaceAll("\\u00a0", "");
						}
						grammarMap.put(num,production);
				}

			}

			fis.close();
			bis.close();
			dis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	boolean isTerminal(String sym) {

		if(Character.isUpperCase(sym.charAt(0))) {
			return false;
		} else {
			System.out.println(sym.charAt(0));
			return true;
		}	
	}
	
	static boolean isIdentifier(String str) {
	     if (str.length() == 0 || !Character.isJavaIdentifierStart(str.charAt(0))) {
	        return false;
	      }
	     else if(Set.of("int","float","void","return","break","continue","if","while","else","and","or","main","$").contains(str)) {
	    	 return false;
	     }
	      for (int i = 1; i < str.length(); i++) {
	         if (!Character.isJavaIdentifierPart(str.charAt(i))) {
	            return false;
	         }
	      }
	      return true;
	}
	private Pattern pattern = Pattern.compile("[+-]?(\\d+([.]\\d*)?([eE][+-]?\\d+)?|[.]\\d+([eE][+-]?\\d+)?)");

	public boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return pattern.matcher(strNum).matches();
	}
	
	void lexemeAnalasic() {
		System.out.println("Before: ");
		for(int i=0; i< inputTokens.length;i++) {
			System.out.print(inputTokens[i]+" / ");
		}
		System.out.println();
		for(int i=0; i< inputTokens.length;i++) {
//			System.out.println("check: "+i+inputTokens[i]);
			if(isIdentifier(inputTokens[i])) {
//				System.out.println("check iden: "+inputTokens[i]);
//				System.out.println(i+" is "+inputTokens[i]);
				inputTokens[i]="identifier";
				
			}
			else if(isNumeric(inputTokens[i])) {
//				System.out.println("check num: "+inputTokens[i]);
//				System.out.println(i+" is "+inputTokens[i]);
				inputTokens[i]="num";
				
			}
			else if(Set.of("==","<=",">=","<",">","!=").contains(inputTokens[i])) {
				inputTokens[i]="relOp";
//				System.out.println(i+" is "+inputTokens[i]);
				
			}
			else if(Set.of("+").contains(inputTokens[i])) {
				inputTokens[i]="addOp";
//				System.out.println(i+" is "+inputTokens[i]);
				
			}
		}
		System.out.println("After: ");
		for(int i=0; i< inputTokens.length;i++) {
			
			System.out.print(inputTokens[i]+" / ");
		}
		
	}
	void parse() {
		Stack<String> stack = new Stack();
		stack.push("$");
		stack.push(startSymbol);
//		System.out.println(inputTokens.length);
		int i = 0;
		
		while((!stack.isEmpty() && !stack.peek().equals("$"))) {
			System.out.println("\n i: " + i);
			String stackPeek = stack.peek().toString();
			
			String inputPeek = inputTokens[i].replace("\t", "").replace("\s", "").replace(" ", "");;
			
			System.out.println("stackpeek: " + stackPeek);
			System.out.println("inputpeek: " + inputPeek);
			
			if(stackPeek.equals(inputPeek)) {
				System.out.println("equal input");
				stack.pop();
				i++;
			} else if (isTerminal(stackPeek)) {
				System.out.println("terminal");
				i++;
				stack.pop();
			} else if (checkExistInParseTable(stackPeek, inputPeek) != null) {
				System.out.println("parsing successfully");
				
				String temp = checkExistInParseTable(stackPeek, inputPeek);
				System.out.println("temp: " + temp);
				
				if(temp.equals("epsilon")) {
					stack.pop();
				} else {
					String[] grammar = readGrammarFollowKey(temp).split("!");
					stack.pop();
					System.out.println("grammar: " + grammar.length);
					System.out.print(stackPeek + " ->");
					for(int j = grammar.length - 1; j > 0 ; j --) {
						if(!grammar[j].isEmpty()) {
							System.out.print(" " + grammar[j]);
							stack.push(grammar[j].replace("\t", "").replace("\s", "").replace(" ", ""));
						}
					}
					System.out.println();
				}
			} else if ((checkExistInParseTable(stackPeek, inputPeek) == null)){
				System.out.println("error no in table parsing: ");
				break;
			}
				
		} 
		if(!(stack.peek().toString().equals(inputTokens[i]))){
			System.out.println(stack.peek().toString());
			System.out.println(inputTokens[i]);
			System.out.println("error");
		}else System.out.println("PARSE SUCCESSFULLY");
	}

	private String readGrammarFollowKey(String temp) {
		Iterator<Entry<String, String[]>> iterator = grammarMap.entrySet().iterator();
		
		String rtn = null;
		
		while(iterator.hasNext()) {
			Entry<String, String[]> entry = iterator.next();
			
			if(temp.equals(entry.getKey())) {
				//System.out.println("getkey grammar: " + entry.getKey());
				rtn = ConvertStringArrayToString(entry.getValue());
			}
		}
		//8.	FunDeclList --> FunDecl  FunDeclList`
		//FunDecl  FunDeclList`
		return rtn;
	}
	
	private String ConvertStringArrayToString(String[] strArr) {
		StringBuilder sb = new StringBuilder();
		for (String str : strArr)
			sb.append(str).append("!");
		String test = sb.substring(0, sb.length()-1);
		return test;
	}	
	
	
	private boolean checkExistInParseTable(String keyToBeChecked) {
		Iterator<Entry<String, HashMap<String, String>>>
        iterator = parseTable.entrySet().iterator();

		boolean rtn = false;
		
	    while (iterator.hasNext()) {
	
	        Entry<String, HashMap<String, String>> entry = iterator.next();
	        //System.out.println("entry getValue: " + entry.getValue());
	        
	        if (!keyToBeChecked.equals(entry.getKey())) {
	            HashMap<String, String> entryGetValue = entry.getValue();
	            Iterator<Entry<String, String>> iterator2 = entryGetValue.entrySet().iterator();
	            
	            while(iterator2.hasNext()) {
	            	Entry<String, String> entry2 = iterator2.next();
	            	
	            	if(keyToBeChecked.equals(entry2.getKey())) {
	            		return true;
	            	} 
	            }
	        } else {
	        	return true;
	        }
	    }
	    return rtn;
	}
	
	private String checkExistInParseTable(String keyToBeChecked, String value) {
        Iterator<Entry<String, HashMap<String, String>>>
        iterator = parseTable.entrySet().iterator();

        String rtn = null;
	
	    while (iterator.hasNext()) {
	
	        Entry<String, HashMap<String, String>> entry = iterator.next();
	        //System.out.println("entry getValue: " + entry.getValue());
	        
	        if (keyToBeChecked.equals(entry.getKey())) {
	            HashMap<String, String> entryGetValue = entry.getValue();
	            Iterator<Entry<String, String>> iterator2 = entryGetValue.entrySet().iterator();
	            
	            while(iterator2.hasNext()) {
	            	Entry<String, String> entry2 = iterator2.next();
	            	
	            	if(value.equals(entry2.getKey())) {
	            		if(entry2.getValue().length() != 0) {
	            			//System.out.println("value getkey: " + entry2.getKey() + " | get value : " + entry2.getValue() );
	            			return entry2.getValue();
	            		} 
	            	}
	            }
	        }
	    }
	    return rtn;
	}

	




	public static void main(String[] args) {
		try{

			Parser parser = new Parser();
			parser.readInput(new File("input.txt"));
			parser.lexemeAnalasic();
			parser.readParseTable(new File("parseTableCformatted.txt"));
			parser.readGrammar(new File("grammarC++.txt"));
			parser.parse();

			//System.out.println("check: " + parser.checkExistInParseTable("VarList", "sum"));
			//System.out.println("check: " + parser.checkExistInParseTable("sum"));
//			System.out.println("0".chars().allMatch( Character::isDigit ));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
