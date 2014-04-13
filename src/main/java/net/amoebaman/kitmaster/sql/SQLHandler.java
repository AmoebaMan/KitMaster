package net.amoebaman.kitmaster.sql;

import java.sql.*;

import net.amoebaman.kitmaster.KitMaster;
import net.amoebaman.kitmaster.handlers.KitHandler;
import net.amoebaman.kitmaster.objects.Kit;

public class SQLHandler {
	
	/** The SQLHandler's connection to its SQL server */
	private Connection connection = null;
	
	/**
	 * Constructs an SQLHandler for an SQL server with its URL and connection credentials
	 * @param url the URL of the SQL server
	 * @param username the username to connect with
	 * @param password the password for the given username
	 */
	public SQLHandler(String url, String username, String password){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + url + "/", username, password);
			connection.setClientInfo("autoReconnect", "true");
		}
		catch(Exception e){ e.printStackTrace(); }
		
		if(isConnected()){
			KitMaster.logger().info("Successfully connected to MySQL server");
			executeCommand(SQLQueries.CREATE_DATABASE);
			executeCommand(SQLQueries.USE_DATABASE);
			executeCommand(SQLQueries.CREATE_HISTORY_TABLE);
			executeCommand(SQLQueries.CREATE_SIGN_TABLE);
			executeCommand(SQLQueries.CREATE_TIMESTAMP_TABLE);
			
			for(Kit kit : KitHandler.getKits())
				executeCommand(SQLQueries.ADD_KIT_TO_TIMESTAMP_TABLE.replace(SQLQueries.KIT_MACRO, kit.name));
		}
		else
			KitMaster.logger().warning("Failed to connect to MySQL server");
	}
	
	/**
	 * Checks if this SQLHandler is successfully connected to an SQL server
	 * @return true if this SQLHandler is connected to an SQL server
	 */
	public boolean isConnected(){
		try { return connection != null && connection.isValid(3); }
        catch (SQLException sqle) { return false; }
	}
	
	/**
	 * Sends a command statement to the SQL server to be executed
	 * @param statement the command to execute
	 */
	public void executeCommand(String command){
		executeCommand(command, false);
	}
	
	/**
	 * Sends a command statement to the SQL server to be executed
	 * @param statement the command to execute
	 * @param suppressErrors whether or not to prevent any errors that may occur from being printed in the console
	 */
	public void executeCommand(String command, boolean suppressErrors){
		if(isConnected()){
			try{ connection.prepareStatement(command).executeUpdate(); }
			catch(SQLException sqle){
				if(!suppressErrors && !sqle.getMessage().toLowerCase().contains("duplicate column"))
					sqle.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends a query statement to the SQL server to be answered
	 * @param query the query to make
	 * @return a ResultSet containing the results of the query
	 */
	public ResultSet executeQuery(String query){
		if(isConnected()){
			try{ return connection.prepareStatement(query).executeQuery(); }
			catch(SQLException sqle){
				sqle.printStackTrace();
				return null;
			}
		}
		else
			return null;
	}
	
	public <T> T getFirstResult(ResultSet set, String column, Class<T> type){
		try{
			if(!set.first())
				return null;
			else
				return type.cast(set.getObject(column));
		}
		catch(Exception e){ return null; }
	}
	
}
