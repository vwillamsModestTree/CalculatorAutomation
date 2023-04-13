package stepdefinitions;

import java.io.File; 
import java.io.FileNotFoundException; 
import java.util.Scanner; 

public class CurrentStepsGenerator {

	// Pass the step definition file as an argument 
	public static void main(String[] args) {
		String stepDefinitionFileName = System.getProperty("user.dir") + "\\src\\main\\java\\stepdefinitions\\"
				+ args[0];
		try {
			File myObj = new File(stepDefinitionFileName);
			Scanner myReader = new Scanner(myObj);
			String rawLineData ="";
			String lineDataNoSpaces="";
			int count = 0;
			while (myReader.hasNextLine()) {
				rawLineData = myReader.nextLine();
				if(rawLineData.length() != 0) {
					lineDataNoSpaces = rawLineData.trim();
					if(lineDataNoSpaces.length() != 0) {
						if(lineDataNoSpaces.charAt(0) == '@') {
							count++;
							if (count == 1) {
								System.out.println("The available step definitions are:\n");
							}
							lineDataNoSpaces = lineDataNoSpaces.replaceAll("@.*\\(\"", "");
							lineDataNoSpaces = lineDataNoSpaces.replaceAll("\"\\)", "");
							System.out.println(lineDataNoSpaces);
						}
					}
				}
			}
			System.out.println("\nThere is a total of " + count + " step definitions.");
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
