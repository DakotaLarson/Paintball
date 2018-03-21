package me.theredbaron24.paintball.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

public class Configuration {

	private static Main main = null;
	private static FileConfiguration config = null;
	private static File configFile = null;
	private static FileConfiguration arenaConfig = null;
	private static File arenaFile = null;
	private static FileConfiguration messageConfig = null;
	private static File messageFile = null;
	private static FileConfiguration cpConfig = null;
	private static File cpFile = null;
	
	public static void init(Main main){
		Configuration.main = main;
		configFile = loadResource(main, "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		main.saveDefaultConfig();
		reloadArenaConfig();
	}
	
	/*public Configuration(Main main){
		this.main = main;
	}*/
	
	public static FileConfiguration getConfig(){
		if(config == null){
			reloadConfig();
		}
		return config;
	}
	
	public static void reloadConfig(){
		if (configFile == null) {
			configFile = new File(main.getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public static void saveConfig(){
		if (config == null || configFile == null) {
			return;
		}
		try {
			config.save(configFile);
		} catch (IOException e) {
			main.getLogger().severe("Could not save config");
		}
	}
	
	public static FileConfiguration getArenaConfig() {
		if (arenaConfig == null) {
			reloadArenaConfig();
		}
		return arenaConfig;
	}
	
	public static void reloadArenaConfig() {
		if (arenaFile == null) {
			arenaFile = new File(main.getDataFolder(), "ArenaConfig.yml");
		}
		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
	}

	public static void saveArenaConfig() {
		if (arenaConfig == null || arenaFile == null) {
			return;
		}
		try {
			getArenaConfig().save(arenaFile);
		} catch (IOException e) {
			main.getLogger().severe("Could not save arena config");
		}
	}
	
	public static FileConfiguration getMessageConfig() {
		if (messageConfig == null) {
			reloadMessageConfig();
		}
		return messageConfig;
	}
	
	public static void reloadMessageConfig() {
		if (messageFile == null) {
			messageFile = new File(main.getDataFolder(), "Messages.yml");
		}
		messageConfig = YamlConfiguration.loadConfiguration(messageFile);
	}

	public void saveMessageConfig() {
		if (messageConfig == null || messageConfig == null) {
			return;
		}
		try {
			getArenaConfig().save(messageFile);
		} catch (IOException e) {
			main.getLogger().severe("Could not save message config");
		}
	}
	
	public static FileConfiguration getCPConfig() {
		if (cpConfig == null) {
			reloadCPConfig();
		}
		return cpConfig;
	}
	
	public static void reloadCPConfig() {
		if (cpFile == null) {
			cpFile = new File(main.getDataFolder(), "ControlPointConfig.yml");
		}
		cpConfig = YamlConfiguration.loadConfiguration(cpFile);
	}

	public static void saveCPConfig() {
		if (cpConfig == null || cpFile == null) {
			return;
		}
		try {
			getCPConfig().save(cpFile);
		} catch (IOException e) {
			main.getLogger().severe("Could not save cp config");
		}
	}
	
	private static File loadResource(Main main, String resource) {
		File folder = main.getDataFolder();
		if (folder.exists() == false)
			folder.mkdir();
		File file = new File(folder, resource);
		try {
			if(file.exists() == false){
				file.createNewFile();
				InputStream in = main.getResource(resource); 
				OutputStream out = new FileOutputStream(file);
				ByteStreams.copy(in, out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
