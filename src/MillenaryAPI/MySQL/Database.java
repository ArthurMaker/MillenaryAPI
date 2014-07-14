package MillenaryAPI.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

public abstract class Database {
	protected Plugin plugin;
	protected Database (Plugin plugin){
		this.plugin = plugin;
	}
	public abstract ResultSet querySQL(String s) throws SQLException;
	public abstract void updateSQL(String s) throws SQLException;
	public abstract void updateSQL(PreparedStatement preparedstatement) throws SQLException;
	public abstract Connection getConnection();
	public abstract void closeConnection();
}