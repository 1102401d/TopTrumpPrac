import java.util.Scanner;

public class HumanPlayer extends Player{

	//prompts player to select an attribute
	//takes a scanner as an argument so that the main scanner can be reused
	//having multiple scanners open at once seems to cause errors
	public String playerSelectAttribute(Scanner scanner)
	{
		String att = "";
		String[] atts = cardInPlay.getAttributes();
		boolean valid = false;
		while(!valid)
		{
			//checks that the input is one of the card's attribute names
			System.out.println("select attribute");
			att = scanner.next();
			for(String s : atts)
			{
				if(att.equals(s))
					valid = true;
			}
		}		
		//returns the attribute as a String
		return att;
	}
	
}