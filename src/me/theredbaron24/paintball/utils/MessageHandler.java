package me.theredbaron24.paintball.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

public class MessageHandler {

	private static Map<String, String> messages = new HashMap<String, String>();
	
	private static FileConfiguration loadResource() {
		File folder = Main.main.getDataFolder();
		if (folder.exists() == false)
			folder.mkdir();
		File file = new File(folder, "messages.yml");
		try {
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			InputStream in = Main.main.getResource("messages.yml"); 
			OutputStream out = new FileOutputStream(file);
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config;
	}
	public static void loadMessages(){
		FileConfiguration config = loadResource();
		String fileTitle = Configuration.getConfig().getString("general.messageFileName", "messages");
		if(fileTitle.equalsIgnoreCase("messages") || fileTitle.equalsIgnoreCase("messages.yml")){
			Main.main.getLogger().info("loading default message file...");
			Set<String> keys = config.getKeys(false);
			for(String title : keys){
				messages.put(title, ChatColor.translateAlternateColorCodes('&', config.getString(title, "Message not found: " + title)));
			}
			Main.main.getLogger().info("Successfully loaded " + messages.size() + " messages!");
		}else{
			if(fileTitle.endsWith(".yml") == false){
				fileTitle += ".yml";
			}
			FileConfiguration customConfig = null;
			try{
				customConfig = YamlConfiguration.loadConfiguration(new File(Main.main.getDataFolder(), fileTitle));
			}catch(Exception e){
				Main.main.getLogger().warning("Unable to load message file: " + fileTitle);
				Main.main.getLogger().info("loading default message file...");
				Set<String> keys = config.getKeys(false);
				for(String title : keys){
					messages.put(title, ChatColor.translateAlternateColorCodes('&', config.getString(title, "Message not found: " + title)));
				}
				Main.main.getLogger().info("Successfully loaded " + messages.size() + " messages!");
				return;
			}
			Set<String> keys = customConfig.getKeys(false);
			Set<String> generalKeys = config.getKeys(false);
			generalKeys.removeAll(keys);
			if(generalKeys.isEmpty()){
				Main.main.getLogger().info("Loading custom message file: " + fileTitle + "...");
				keys = customConfig.getKeys(false);
				for(String title : config.getKeys(false)){
					messages.put(title, ChatColor.translateAlternateColorCodes('&', customConfig.getString(title, "Message not found: " + title)));
				}
				Main.main.getLogger().info("Successfully loaded " + messages.size() + " messages!");
			}else{
				Main.main.getLogger().warning("Custom config file " + fileTitle + " was missing " + generalKeys.size() + " messages.");
				Main.main.getLogger().info("loading default message file...");
				for(String title : config.getKeys(false)){
					messages.put(title, ChatColor.translateAlternateColorCodes('&', config.getString(title, "Message not found: " + title)));
				}
				Main.main.getLogger().info("Successfully loaded " + messages.size() + " messages!");
			}
		}
	}
	public static String getMessage(String title, String... strings){
		String message = messages.get(title);
		if(message == null || message.equals("")){
			Bukkit.getConsoleSender().sendMessage(title + " does not exist!");
			message = "Message not found! " + title;
		}
		if(strings == null){
			return message;
		}else{
			if(strings.length % 2 != 0){
				Bukkit.getConsoleSender().sendMessage(title + " has invalid argument length!");
			}
			String finalMessage = message;
			for(int i = 0; i < strings.length; i +=2){
				if(i < strings.length -1){
					finalMessage = finalMessage.replaceAll(strings[i], strings[i + 1]);
				}
			}
			return finalMessage;
		}
	}
}
