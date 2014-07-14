package MillenaryAPI.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

/**
 * @author Arthur Bergamaschi (ArthurMaker)
 */
public class MakerSQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;
	private Connection connection;
	
	public MakerSQL(Plugin plugin, String hostname, String port, String username, String password, String database){
		super(plugin);
		this.hostname = hostname;
		this.port = port;
		this.user = username;
		this.password = password;
		this.database = database;
		this.plugin.getLogger().info("[" +  this.hostname + ":" + this.port + "] Connecting to database...");
								   //"[" +  this.hostname + ":" + this.port + "] Estabelecendo conexão com o banco de dados..."
		this.connection = this.openConnection();
	}
	
	private Connection openConnection(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.password);
		}catch (SQLException e){
			this.plugin.getLogger().log(Level.SEVERE, "It was not possible to connect to the MySQL server! Returned error: " + e.getMessage());
													  //"Não foi possível conectar ao servidor MySQL! Causa: " + e.getMessage()
		}catch (ClassNotFoundException e){
			this.plugin.getLogger().log(Level.SEVERE, "JDBC was not found!");
													//"JDBC não encontrado!"
		}
		return this.connection;
	}
	
	public SQLTable createTableConnection(String tablename){
		return new SQLTable(this, tablename);
	}
	
	public SQLTable createTable(String tablename, HashMap<String, String> columns){
		return new SQLTable(this, tablename, columns);
	}
	
	public boolean containsTable(String tablename){
		ResultSet result = null;
		try{
			result = this.querySQL("SHOW TABLES LIKE '" + tablename + "';");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while(result.next()) if(result.getString(1).equalsIgnoreCase(tablename)) return true;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void printLevelLog(Level level, String s){
		this.plugin.getLogger().log(level, s);
	}
	
	public void printLog(String s){
		this.plugin.getLogger().info(s);
	}
	
	public ResultSet querySQL(String query) throws SQLException {
		Connection c = this.getConnection();
		ResultSet result = null;
		try{
			c.setAutoCommit(false);
			result = c.prepareStatement(query).executeQuery();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					this.plugin.getLogger().log(Level.SEVERE, "Não foi possível executar a query.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			c.setAutoCommit(true);
		}
		return result;
	}
	
	public void updateSQL(String update) throws SQLException {
		Connection c = this.getConnection();
		PreparedStatement s = null;
		try{
			c.setAutoCommit(false);
			s = c.prepareStatement(update);
			s.execute();
			c.commit();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					this.plugin.getLogger().log(Level.SEVERE, "Não foi possível executar o update.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			if(s != null) s.close();
			c.setAutoCommit(false);
		}
		this.closeConnection();
	}
	
	public void updateSQL(PreparedStatement preparedstatement) throws SQLException {
		Connection c = this.getConnection();
		try{
			c.setAutoCommit(false);
			preparedstatement.execute();
			c.commit();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					this.plugin.getLogger().log(Level.SEVERE, "Não foi possível executar o update.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			if(preparedstatement != null) preparedstatement.close();
			c.setAutoCommit(false);
		}
		this.closeConnection();
	}
	
	public Connection getConnection(){
		try{
			if((this.connection == null) || (!this.connection.isValid(10))) this.connection = this.openConnection();
		}catch (SQLException e){
			e.printStackTrace();
		}
		return this.connection;
	}
	
	public void closeConnection(){
		if(this.connection == null){
			this.plugin.getLogger().log(Level.SEVERE, "Não há conexões para encerrar!");
		}else{
			try{
				this.connection.close();
				this.connection = null;
			}catch (SQLException e){
				this.plugin.getLogger().log(Level.SEVERE, "Ocorreu um erro ao encerrar a conexão ao banco de dados!");
				e.printStackTrace();
			}
		}
	}
	
}
