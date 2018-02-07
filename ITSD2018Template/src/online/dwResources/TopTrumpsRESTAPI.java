package online.dwResources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import online.configuration.TopTrumpsJSONConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import commandline.AIPlayer;
import commandline.Card;
import commandline.DataBaseCon;
import commandline.HumanPlayer;
import commandline.Player;
import commandline.TopTrumpsCLIApplication;

@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/**
 * This is a Dropwizard Resource that specifies what to provide when a user
 * requests a particular URL. In this case, the URLs are associated to the
 * different REST API methods that you will need to expose the game commands
 * to the Web page.
 * 
 * Below are provided some sample methods that illustrate how to create
 * REST API methods in Dropwizard. You will need to replace these with
 * methods that allow a TopTrumps game to be controled from a Web page.
 */
public class TopTrumpsRESTAPI {

	/** the current round */
	private static int round;
	/** maintains a count of the draws in a single game */
	private static int draws;

	private static ArrayList<Player> allPlayers;
	private static ArrayList<Player> players;
	private static ArrayList<Card> deck, communalDeck;
	/** A Jackson Object writer. It allows us to turn Java objects
	 * into JSON strings easily. */
	ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();

	/**
	 * Contructor method for the REST API. This is called first. It provides
	 * a TopTrumpsJSONConfiguration from which you can get the location of
	 * the deck file and the number of AI players.
	 * @param conf
	 */
	public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf) {
		// ----------------------------------------------------
		// Add relevant initalization here
		// ----------------------------------------------------
	}

	// ----------------------------------------------------
	// Add relevant API methods here
	// ----------------------------------------------------

	@GET
	@Path("/helloJSONList")
	/**
	 * Here is an example of a simple REST get request that returns a String.
	 * We also illustrate here how we can convert Java objects to JSON strings.
	 * @return - List of words as JSON
	 * @throws IOException
	 */
	public String helloJSONList() throws IOException {

		List<String> listOfWords = new ArrayList<String>();
		listOfWords.add("Hello");
		listOfWords.add("World!");

		// We can turn arbatory Java objects directly into JSON strings using
		// Jackson seralization, assuming that the Java objects are not too complex.
		String listAsJSONString = oWriter.writeValueAsString(listOfWords);

		return listAsJSONString;
	}

	@GET
	@Path("/playGame")
	public ArrayList<Card> playGame(@QueryParam("numPlayers") int numPlayers) 
	{
		
		deck = buildDeck();
		Collections.shuffle(deck);
		//create a new empty ArrayList for the communal deck

		communalDeck = new ArrayList<Card>();
		// set up the players and divide deck between them

		players = setUpPlayers(numPlayers);
		//create a second array of players for reference to players after game ends
		allPlayers = new ArrayList<Player>(players);
		divideDeck(numPlayers);
		round=0;
		draws=0;
		int startPlayer = new Random().nextInt(numPlayers);
		
		Player currentPlayer = players.get(startPlayer);
		
	
		round++;
		ArrayList<Card> cardsInPlay = playNextHand();
		
		return cardsInPlay;
	}
	
	@GET
	@Path("/quitGame")
	public String quitGame() 
	{
		String quitMessage = "You have ended the game.";
		return quitMessage;
	}

	//Will be called from playGame(), rather than being called directly from GameScreen
	//@GET
	//@Path("/buildDeck")
	public ArrayList<Card> buildDeck() {
		//reads in the deck from the txt file
		ArrayList<Card> deck = new ArrayList<Card>();
		try
		{
			//I had to give the absolute path for this to work in command prompt
			// eg "C:\\Users\\Roddy\\workspace\\Trumps\\StarCitizenDeck.txt"
			BufferedReader reader = new BufferedReader(new FileReader("StarCitizenDeck.txt"));

			String line = reader.readLine();
			//skip the first line because it is just the names of attributes
			//will maybe read this line in and change card constructor
			//so decks with more/less/different attributes can be used
			line = reader.readLine();
			while (line != null)
			{
				//read each line in and create new card
				Card card = new Card(line);
				deck.add(card);
				line = reader.readLine();
			}
			reader.close();
			//return the built deck as and arraylist of cards
			//String deckString = oWriter.writeValueAsString(deck);
			return deck;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	//@GET
	//@Path("/setUpPlayers")
	public ArrayList<Player> setUpPlayers(int numPlayers) 
	{
		ArrayList<Player> players = new ArrayList<Player>();

		Player roddy = new HumanPlayer();
		players.add(roddy);

		//int opponents = numPlayers - 1;

		for (int i = 1; i <= numPlayers-1; i++) 
		{
			String name = String.format("AI Player %d", i);
			AIPlayer ai = new AIPlayer(name);
			players.add(ai);
		}

		return players;
	}
	
	private static void divideDeck(int numPlayers) {
		//sequentially divides the deck among players
		//not the most efficient way of doing it but it works and was easy to implement
		int i = 0;
		for (Card card : deck)
		{
			players.get(i % numPlayers).addCardToDeck(card);
			i++;
		}
	}
	
	
	public static ArrayList<Card> playNextHand() {
		ArrayList<Card> cards = new ArrayList<Card>();
		for (Player p : players)
		{
			cards.add(p.playCard());
		}

		if (players.get(0) instanceof HumanPlayer)
		{
			System.out.println("Your Card:");
			players.get(0).printCardInPlay();
		}

		return cards;
	}
	
	
	@GET
	@Path("/aiChoice")
	public String aiChoice(@QueryParam("aiCard") Card aiCard) {
		String[] atts = aiCard.getAttributes();
		int[] values = {aiCard.getSize(), aiCard.getSpeed(), aiCard.getRange(), aiCard.getFirepower(), aiCard.getCargo()};
		String att = "";

		int max = 0;
		for(int i = 0; i < 5; i++)
		{
			int value = values[i];
			if(value > max)
			{
				max = value;
				att = atts[i];
			}
		}
		return att;
	}
	//Method to divide the deck based on number of players
	//@GET
	//@Path("/splitDeck")
	//public void splitDeck(@QueryParam("numPlayers") int numPlayers) 
	//{

	//}
	@GET
	@Path("/getStats")
	public String getStats() throws IOException {

		List<String> stats = new ArrayList<String>();

		DataBaseCon.connect();
		int numGames = DataBaseCon.numGames();
		int aiWins = DataBaseCon.aiWins();
		int humWins = DataBaseCon.humanWins();
		int maxRounds = DataBaseCon.maxRounds();
		double avgDraws = DataBaseCon.avgDraws();
		String num = Integer.toString(numGames);
		String ai = Integer.toString(aiWins);
		String human = Integer.toString(humWins);
		String max = Integer.toString(maxRounds);
		String draw = Double.toString(avgDraws);
		DataBaseCon.close();
		stats.add(num);
		stats.add(ai);
		stats.add(human);
		stats.add(max);
		stats.add(draw);

		String statsAsJSONString = oWriter.writeValueAsString(stats);
		return statsAsJSONString;

	}

	@GET
	@Path("/helloWord")
	/**
	 * Here is an example of how to read parameters provided in an HTML Get request.
	 * @param Word - A word
	 * @return - A String
	 * @throws IOException
	 */
	public String helloWord(@QueryParam("Word") String Word) throws IOException {
		return "Hello "+Word;
	}

}