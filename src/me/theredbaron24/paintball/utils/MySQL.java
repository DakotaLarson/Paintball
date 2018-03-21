package me.theredbaron24.paintball.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.RankHandler;
import me.theredbaron24.paintball.main.TokenHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySQL {
	
	private static Connection connection = null;
	private static String host = null;
	private static int port = 3306;
	private static String database = null;
	private static String username = null;
	private static String password = null;
	private static String[] tables = {"uuid", "name", "kit", "points", "kills", "deaths", "rounds", "victories", "defeats", "draws", "shots", "captures", "tokens", "currency"};


	public synchronized static boolean connectToDatabase(String host, int port, String database, String username, String password) {
		MySQL.host = host;
		MySQL.port = port;
		MySQL.database = database;
		MySQL.username = username;
		MySQL.password = password;
		
		try {
			String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
			connection = DriverManager.getConnection(connectionString, username, password);
			return true;
		} catch (SQLException e) {
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
			return false;
		}
	}
	
	public synchronized static boolean connectToDatabase() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
			return true;
		} catch (SQLException e) {
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
			return false;
		}
	}
	public synchronized static boolean hasValidConnection(){
		try{
			if(connection == null || connection.isClosed()){
				return false;
			}else{
				return connection.isValid(3);
			}
		}catch(SQLException e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return false;
		
	}
	
	public synchronized static boolean createTable(){
		try {
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS paintball (uuid varchar(64),name varchar(64),kit varchar(64),points int,kills int,deaths int,"
					+ "rounds int,victories int, defeats int,draws int,shots int,captures int,tokens int, currency int);");
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e) {
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
			return false;
		}
	}
	
	public synchronized static boolean isInDatabase(String uuid, boolean isName){
		if(isName == false){
			boolean contains = false;
			try{
				PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM `paintball` WHERE uuid=?;");
				statement.setString(1, uuid);
				ResultSet set = statement.executeQuery();
				contains = set.next();
				statement.close();
			}catch(Exception e){
				if(!Main.sqlDebug){
					Main.main.getLogger().warning(e.getMessage());
				}else{
					e.printStackTrace();
				}
			}
			return contains;
		}else{
			boolean contains = false;
			try{
				PreparedStatement statement = connection.prepareStatement("SELECT name FROM `paintball` WHERE name=?;");
				statement.setString(1, uuid);
			    ResultSet set = statement.executeQuery();
				contains = set.next();
				statement.close();
			}catch(Exception e){
				if(!Main.sqlDebug){
					Main.main.getLogger().warning(e.getMessage());
				}else{
					e.printStackTrace();
				}
			}
			return contains;
		}
	}
	public synchronized static boolean addPlayerToDatabase(String uuid, String name){
		try{
			PreparedStatement statement = connection.prepareStatement("INSERT INTO `paintball` values(?, ?, 'none', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);");
			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.execute();
			statement.close();
			return true;
		}catch(SQLException e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public synchronized static void closeConnection(){
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				if(!Main.sqlDebug){
					Main.main.getLogger().warning(e.getMessage());
				}else{
					e.printStackTrace();
				}
			}
		}
	}
	public synchronized static String getString(String uuid, String title){
		if(isTable(title) == false) return null;
		String s = null;
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT `"+ title + "` FROM `paintball` WHERE uuid=?;");
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			set.next();
			int index = set.findColumn(title);
			s = set.getString(index);
			statement.close();
		}catch(Exception e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return s;
	}
	public synchronized static int getInt(String uuid, String title){
		if(isTable(title) == false) return 0;
		int i = 0;
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT `"+ title + "` FROM `paintball` WHERE uuid=?;");
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			set.next();
			int index = set.findColumn(title);
			i = set.getInt(index);
			statement.close();
		}catch(Exception e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return i;
	}
	public synchronized static void setString(String uuid, String title, String string){
		if(isTable(title) == false) return;
		try{
			PreparedStatement update = connection.prepareStatement("UPDATE `paintball` SET `"+ title + "` = ? WHERE uuid=?;");
			update.setString(1, string);
			update.setString(2, uuid);
			update.executeUpdate();
			update.close();
		}catch(SQLException e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
	}
	public synchronized static void setInt(String uuid, String title, int i){
		if(isTable(title) == false) return;
		try{
			PreparedStatement update = connection.prepareStatement("UPDATE `paintball` SET `"+ title + "` = ? WHERE uuid=?;");
			update.setInt(1, i);
			update.setString(2, uuid);
			update.executeUpdate();
			update.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		if(title.equals("points")){
			int total = getInt(uuid, title);
			Bukkit.getScheduler().runTask(Main.main, new Runnable() {
				@Override
				public void run() {
					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if(player != null && player.isOnline()){
						RankHandler.checkRank(player, total);
					}
				}
			});
		}else if(title.equals("tokens")){
			int total = getInt(uuid, title);
			Bukkit.getScheduler().runTask(Main.main, new Runnable() {
				@Override
				public void run() {
					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if(player != null && player.isOnline()){
						TokenHandler.getTokens().put(UUID.fromString(uuid), total);
					}
				}
			});
		}
	}
	public synchronized static void addInt(String uuid, String title, int i){
		if(isTable(title) == false) return;
		int num = getInt(uuid, title);
		num += i;
		setInt(uuid, title, num);
	}
	public synchronized static String getIDFromName(String name){
		String s = null;
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM `paintball` WHERE name=?;");
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			set.next();
			int index = set.findColumn("uuid");
			s = set.getString(index);
			statement.close();
		}catch(Exception e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return s;
	}
	public synchronized static int getRank(String uuid){
		int rank = 0;
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM `paintball` WHERE points > (SELECT points from `paintball` WHERE uuid=?);");
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set != null && set.next()) {
				rank = set.getInt(1) + 1;
			}
			set.close();
		}catch(Exception e){
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return rank;
	}
	public synchronized static List<String> getBestPage(int page){
		page --;
		page = page * 10;
		List<String> data = new ArrayList<String>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT name FROM `paintball` ORDER BY points DESC LIMIT ?, 10;");
			statement.setInt(1, page);
			final ResultSet set = statement.executeQuery();
			int row = page;
			while(set.next() == true){
				row ++;
				String s = set.getString(1);
				String uuid = getIDFromName(s);
				data.add(MessageHandler.getMessage("bestPageInfo", "%rank%", "" + row, "%name%", s, "%points%", "" + getInt(uuid, "points")));
			}
			statement.close();
		}catch (SQLException e) {
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return data;
	}
	public synchronized static List<Chat> getBestPageWithChat(int page){
		page --;
		page = page * 10;
		List<Chat> data = new ArrayList<Chat>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT name FROM `paintball` ORDER BY points DESC LIMIT ? , 10;");
			statement.setInt(1, page);
			final ResultSet set = statement.executeQuery();
			int row = page;
			while(set.next() == true){
				row ++;
				String s = set.getString(1);
				String uuid = getIDFromName(s);
				if(Main.cmdPrefixes){
					data.add(new Chat(MessageHandler.getMessage("bestPageInfo", "%rank%", "" + row, "%name%", s, "%points%", "" + getInt(uuid, "points")))
					.hover(MessageHandler.getMessage("bestPageClick", "%player%", s)).command("/pb stats " + s));	
				}else{
					data.add(new Chat(MessageHandler.getMessage("bestPageInfo", "%rank%", "" + row, "%name%", s, "%points%", "" + getInt(uuid, "points")))
					.hover(MessageHandler.getMessage("bestPageClick", "%player%", s)).command("/stats " + s));
				}
				
			}
			statement.close();
		}catch (SQLException e) {
			if(!Main.sqlDebug){
				Main.main.getLogger().warning(e.getMessage());
			}else{
				e.printStackTrace();
			}
		}
		return data;
	}
	private static boolean isTable(String string){
		for(String table : tables){
			if(string.equals(table)) return true;
		}
		return false;
	}
}
