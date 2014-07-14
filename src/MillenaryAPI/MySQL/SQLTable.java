package MillenaryAPI.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class SQLTable {
	private String name;
	private MakerSQL makersql;
	
	public SQLTable (MakerSQL makersql, String name){
		if(makersql == null) return;
		this.name = name;
		this.makersql = makersql;
	}
	
	public SQLTable (MakerSQL makersql, String name, HashMap<String, String> columns){
		if(makersql == null) return;
		this.name = name;
		this.makersql = makersql;
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `" + name + "` (id int PRIMARY KEY AUTO_INCREMENT");
		Iterator<Entry<String, String>> i = columns.entrySet().iterator();
		while(i.hasNext()){
			Map.Entry<String, String> entry = (Entry<String, String>) i.next();
			sb.append(", " + (String) entry.getKey() + " " + (String) entry.getValue());
		}
		sb.append(");");
		String update = sb.toString();
		try {
			this.makersql.updateSQL(update);
		} catch (SQLException e) {
			this.makersql.printLevelLog(Level.SEVERE, "Não foi possível executar a SQLTable.");
			e.printStackTrace();
			return;
		}
	}
	
	public String getTableName(){
		return this.name;
	}
	
	public boolean containsColumn(String column){
		ResultSet result = null;
		try{
			result = this.makersql.querySQL("SHOW COLUMNS FROM " + this.name + ";");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while(result.next()){
				if(result.getString(column) != null) return true;
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean contains(String column, String value){
		ResultSet result = null;
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while(result.next()){
				if(result.getString(column) != null) return true;
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public int getInt(String column, String value, String row){
		int i = 0;
		try {
			ResultSet res = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
			if(res.next()) {
				if((Integer) res.getInt(row) != null) i = res.getInt(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public String getString(String column, String value, String row){
		try {
			ResultSet res = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
			if(res.next()) {
				if((String) res.getString(row) != null) return (String) res.getString(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Double getDouble(String column, String value, String row){
		Double i = 0D;
		try {
			ResultSet res = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
			if(res.next()) {
				if((Double) res.getDouble(row) != null) i = Double.valueOf(res.getDouble(row));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public Long getLong(String column, String value, String row){
		Long i = 0L;
		try {
			ResultSet res = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
			if(res.next()) {
				if((Long) res.getLong(row) != null) i = Long.valueOf(res.getLong(row));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, String>> openTable(){
		ResultSet result = null;
		ArrayList<HashMap<String, String>> table = new ArrayList<HashMap<String, String>>();
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + ";");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			int i = result.getMetaData().getColumnCount();
			HashMap<String, String> hashmap = new HashMap<String, String>();
			while (result.next()){
				for(int n = 1; n <= i; n++){
					String k = result.getMetaData().getColumnName(n);
					String v = result.getString(n);
					hashmap.put(k, v);
				}
				HashMap<String, String> clone = (HashMap<String, String>) hashmap.clone();
				clone.putAll(hashmap);
				table.add(clone);
				hashmap.clear();
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return table;
	}
	
	public Integer getRowId(String column, String value){
		ResultSet result = null;
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE " + column + " = '" + value + "';");
			if(result == null) return null;
			if(result.next()) return Integer.valueOf(result.getInt("id"));
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String getStringAt(Integer rowId, String column){
		ResultSet result = null;
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id = " + rowId + ";");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while (result.next()){
				if(result.getString(column) != null) return result.getString(column);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public int getIntAt(Integer rowId, String column){
		ResultSet result = null;
		int i = 0;
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id = " + rowId + ";");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while (result.next()){
				if((Integer)result.getInt(column) != null) i = result.getInt(column);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return i;
	}
	
	public Object getAt(Integer rowId, String column){
		ResultSet result = null;
		try{
			result = this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id = " + rowId + ";");
		}catch (SQLException e){
			e.printStackTrace();
		}
		try{
			while (result.next()){
				if(result.getObject(column) != null) return result.getObject(column);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void putAt(Integer rowId, String column, Object value){
		try{
			if(Integer.valueOf(this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id=" + rowId + ";").getInt("id")) != null){
				this.makersql.updateSQL("UPDATE " + this.name + " SET " + column + "='" + value + "' WHERE id=" + rowId + ";");
			}else{
				this.makersql.updateSQL("INSERT INTO " + this.name + " (" + column + ") VALUES ('" + value + "');");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void putStringAt(Integer rowId, String column, String value){
		try{
			if(Integer.valueOf(this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id=" + rowId + ";").getInt("id")) != null){
				this.makersql.updateSQL("UPDATE " + this.name + " SET " + column + "='" + value + "' WHERE id=" + rowId + ";");
			}else{
				this.makersql.updateSQL("INSERT INTO " + this.name + " (" + column + ") VALUES ('" + value + "');");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void putIntAt(Integer rowId, String column, Integer value){
		try{
			if(Integer.valueOf(this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id=" + rowId + ";").getInt("id")) != null){
				this.makersql.updateSQL("UPDATE " + this.name + " SET " + column + "='" + value + "' WHERE id=" + rowId + ";");
			}else{
				this.makersql.updateSQL("INSERT INTO " + this.name + " (" + column + ") VALUES ('" + value + "');");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void putDoubleAt(Integer rowId, String column, Double value){
		try{
			if(Integer.valueOf(this.makersql.querySQL("SELECT * FROM " + this.name + " WHERE id=" + rowId + ";").getInt("id")) != null){
				this.makersql.updateSQL("UPDATE " + this.name + " SET " + column + "='" + value + "' WHERE id=" + rowId + ";");
			}else{
				String s = "INSERT INTO " + this.name + " (" + column + ") VALUES (?);";
				PreparedStatement st = this.makersql.getConnection().prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
				st.setDouble(1, value);
				this.makersql.updateSQL(st);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void insertInto(String[] columns, String[] values){
		try{
			StringBuilder cb = new StringBuilder();
			StringBuilder vb = new StringBuilder();
			for(String c : columns){
				cb.append("," + c);
			}
			for(String v : values){
				vb.append("','" + v);
			}
			this.makersql.updateSQL("INSERT INTO " + this.name + " (" + cb.toString().substring(1) + ") VALUES ('" + vb.toString().substring(3) + "');");
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
}
