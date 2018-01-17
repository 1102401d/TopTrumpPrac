import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class DataBaseCon
{
	//public DataBaseCon()
	//{
		//connect();
		//int games = numGames();
		//System.out.print(games);
		//close();
		
	//}
	
	public static void gameInfo(String winName, int countRounds, int[] roundWins)
	{
		connect();
		Statement stmt = null;
		String query = "INSERT into toptrumps.gamedata (gamewinner, numberrounds, humanroundwins, ai1wins,ai2wins,ai3wins,ai4wins)"
				+ "Values('"+winName+"', "+countRounds+", "+roundWins[0]+", "+roundWins[1]+", "+roundWins[2]+", "+roundWins[3]+", "+roundWins[4]+");";
		try 
		{
			stmt = connection.createStatement();
			 stmt.executeUpdate(query);

		}
		catch (SQLException e )
		{
			e.printStackTrace();
			System.err.println("error executing query " + query);
		}
		
		
		
		
		//close();
		//System.out.print(winName + countRounds + Arrays.toString(roundWins));
	}
	public static Connection connection =null;
	private static void connect()
	{
		String dbname ="m_17_1102401d";
		String username = "m_17_1102401d";
		String password = "1102401d";


		try 
		{
			connection =
					DriverManager.getConnection("jdbc:postgresql://yacata.dcs.gla.ac.uk:5432/" + 
							dbname,username, password);
		}
		catch (SQLException e) 
		{
			System.err.println("Connection Failed!");
			e.printStackTrace();
			return;
		}
		if (connection != null)
		{
			System.out.println("Connection successful");
		}
		else 
		{
			System.err.println("Failed to make connection!");
		}
	}

	//method to close connection
		private static void close()
		{
			try 
			{
				connection.close();
				System.out.println("Connection closed");
			}
			catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Connection could not be closed – SQL exception");
			}
		}
		
		public static int numGames()
		{
			int gamesPlayed = 0;
			Statement stmt = null;
			String query = "SELECT COUNT (gamenumber) FROM toptrumps.gamedata;";
			try 
			{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				//the next method of ResultSet allows you to iterate through the results
				if (rs.next())
				{
					//count the number enrolled in a class
					gamesPlayed =rs.getInt("count");				
				}

			}
			catch (SQLException e )
			{
				e.printStackTrace();
				System.err.println("error executing query " + query);
			}
			System.out.println("Games: "+gamesPlayed);
			return gamesPlayed;
		}
		
		public static int humanWins()
		{
			int wins = 0;
			Statement stmt = null;
			String query = "SELECT COUNT(gamenumber) FROM toptrumps.gamedata WHERE gamewinner = 'human';";
			try 
			{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				//the next method of ResultSet allows you to iterate through the results
				if (rs.next())
				{
					//count the number enrolled in a class
					wins =rs.getInt("count");				
				}

			}
			catch (SQLException e )
			{
				e.printStackTrace();
				System.err.println("error executing query " + query);
			}
			System.out.println("human wins; "+wins);
			return wins;
		}
		
		
		public static int aIWins()
		{
			int wins = 0;
			Statement stmt = null;
			String query = "SELECT COUNT(gamenumber) FROM toptrumps.gamedata WHERE gamewinner = 'ai player1' "
					+ "OR gamewinner = 'ai player2' OR gamewinner = 'ai player3' OR gamewinner = 'ai player4';";
			try 
			{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				//the next method of ResultSet allows you to iterate through the results
				if (rs.next())
				{
					//count the number enrolled in a class
					wins =rs.getInt("count");				
				}

			}
			catch (SQLException e )
			{
				e.printStackTrace();
				System.err.println("error executing query " + query);
			}
			System.out.println("ai wins:"+wins);
			return wins;
		}
		
		public static double avgDraws()
		{
			double avg=0.0;
			Statement stmt = null;
			String query = "SELECT AVG(numberrounds) FROM toptrumps.gamedata;";
			try 
			{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				//the next method of ResultSet allows you to iterate through the results
				if (rs.next())
				{
					//count the number enrolled in a class
					avg =rs.getDouble("avg");				
				}

			}
			catch (SQLException e )
			{
				e.printStackTrace();
				System.err.println("error executing query " + query);
			}
			System.out.println("avg draws;" +avg);
			return avg;
					
		}
		
		public static int maxDraws()
		{
			int max = 0;
			Statement stmt = null;
			String query = "SELECT MAX(numberrounds) FROM toptrumps.gamedata;";
			try 
			{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				//the next method of ResultSet allows you to iterate through the results
				if (rs.next())
				{
					//count the number enrolled in a class
					max =rs.getInt("max");				
				}

			}
			catch (SQLException e )
			{
				e.printStackTrace();
				System.err.println("error executing query " + query);
			}
			System.out.println("max draws:"+max);
			close();
			return max;
		}
		
}
