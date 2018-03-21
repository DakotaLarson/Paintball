package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GameTimer {
	
	private static Random random = new Random();
	private static int timer = 0;
	private static String lastArena = null;
	private static String nextArena = null;
	private static String currentArena = null;
	private static int matchTime = 0;
	
	public static void startCountdown(Main main){
		if(TimerInterrupter.isStopped()) return;
		Utils.setGameStatus(GameStatus.COUNTING_DOWN);
		String msg = MessageHandler.getMessage("countdownStarted");
		for(UUID id : Utils.getPlayers()){
			Player player = Bukkit.getPlayer(id);
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabCounting"));
			Bukkit.getPlayer(id).sendMessage(msg);
		}			
		timer = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
			int time = Main.countdownTime;
			boolean send = false;
			@Override
			public void run() {
				if(time <= 5 && time > 0 || time == 10 || (time % 15 == 0 && time <= 60) || time % 60 == 0){
					send = true;
				}
				if(time >= 0){
					for(UUID id : Utils.getPlayers()){
						Player player = Bukkit.getPlayer(id);
						player.setLevel(time);
						player.setExp((float) time/Main.countdownTime);
						if(send){
							int minutes = time / 60;
							int seconds = time - 60 * minutes;
							String minString = null;
							String secString = null;
							if(minutes < 10){
								minString = "0" + minutes;
							}else{
								minString = "" + minutes;
							}if(seconds < 10){
								secString = "0" + seconds;
							}else{
								secString = "" + seconds;
							}
							player.sendMessage(MessageHandler.getMessage("timeRemaining", "%minutes%", minString, "%seconds%", secString));
							Utils.playTimerPling(player);
						}
					}
					send = false;
					if(time == 5){
						if(Main.nextArena != null){
							nextArena = Main.nextArena;
							VoteHandler.setTempDisabled(true);
							Main.nextArena = null;
						}else{
							if(Main.enableVoting){
								nextArena = VoteHandler.getNextArena();
								if(nextArena == null){
									nextArena = getNextArena(lastArena);
									for(UUID id : Utils.getPlayers()){
										Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("noVotes"));
									}
								}
							}else{
								nextArena = getNextArena(lastArena);
							}
						}
						if(nextArena == null){
							nextArena = getNextArena(lastArena);
						}
						if(nextArena == null){
							for(UUID id : Utils.getPlayers()){
								Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("noArenasEnabled"));
							}
							TimerInterrupter.stop(true, Bukkit.getConsoleSender(), main);
						}else{
							for(UUID id : Utils.getPlayers()){
								Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("nextArena", "%arena%", nextArena));
							}
						}
					}
					else if(time == 0){
						Bukkit.getScheduler().cancelTask(timer);
						timer = 0;
						startInitialization(main);
						nextArena = null;
					}
					time --;
				}
			}
		}, 0l, 20l).getTaskId();
	}
	private static String getNextArena(String lastArena){
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas");
		if(section == null) return null;
		Set<String> allArenas = section.getKeys(false);
		List<String> arenas = new ArrayList<String>();
		for(String s : allArenas){
			if(Configuration.getArenaConfig().getBoolean("arenas." + s + ".enabled")){
				arenas.add(s);
			}
		}
		if(arenas.size() == 0){
			return null;
		}else if(arenas.size() == 1){
			return arenas.get(0);
		}else{
			if(lastArena != null && arenas.contains(lastArena)){
				arenas.remove(lastArena);
			}
			return arenas.get(random.nextInt(arenas.size()));
		}
	}
	private static void startInitialization(Main main){
		currentArena = nextArena;
		nextArena = null;
		lastArena = null;
		Utils.setGameStatus(GameStatus.INITIALIZING);
		createArena(currentArena);
		VoteHandler.createVotingList(currentArena);
		long time = 20l;
		for(UUID id : Utils.getPlayers()){
			Player player = Bukkit.getPlayer(id);
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabInit"));
			player.sendMessage(MessageHandler.getMessage("teleStart"));
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					InventoryEditor.handleMatchTeleport(player);
					player.teleport(Arena.getLobbySpawn());
				}
			}, time);
			time += 5;
		}
		int delay = (Utils.getPlayers().size() + 1) * 5;
		timer = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
			int time = Main.initializationTime;
			boolean send = false;
			@Override
			public void run() {
				if(time <= 5 && time > 0 || time == 10 || (time % 15 == 0 && time <= 60) || time % 60 == 0){
					send = true;
				}
				if(time >= 0){
					if(time == Main.initializationTime){
						ScoreboardHandler.handleMatchInitialization();
						for(UUID id : Utils.getPlayers()){
							Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("beginInit"));
						}
					}
					int minutes = time / 60;
					int seconds = time - 60 * minutes;
					String minString = null;
					String secString = null;
					if(minutes < 10){
						minString = "0" + minutes;
					}else{
						minString = "" + minutes;
					}if(seconds < 10){
						secString = "0" + seconds;
					}else{
						secString = "" + seconds;
					}
					String title = MessageHandler.getMessage("timeRemaining", "%minutes%", minString, "%seconds%", secString);
					for(UUID id : Utils.getPlayers()){
						Player player = Bukkit.getPlayer(id);
						player.setLevel(time);
						player.setExp((float) time/Main.initializationTime);
						if(send){
							player.sendMessage(title);
							Utils.playTimerPling(player);
						}
					}
					send = false;
					if(time == 0){
						Bukkit.getScheduler().cancelTask(timer);
						timer = 0;
						startMatch(main);
					}
					time --;
				}
			}	
		}, delay, 20l).getTaskId();
	}
	private static void startMatch(Main main){
		Utils.setGameStatus(GameStatus.RUNNING);
		for(UUID id : Utils.getPlayers()){
			Player player = Bukkit.getPlayer(id);
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabRun"));
			if(Utils.getRed().contains(id) == false && Utils.getBlue().contains(id) == false){
				if(Utils.getRed().size() > Utils.getBlue().size()){
					Utils.getBlue().add(id);
					player.sendMessage(MessageHandler.getMessage("autoJoinBlue"));
				}else if(Utils.getBlue().size() > Utils.getRed().size()){
					Utils.getRed().add(id);
					player.sendMessage(MessageHandler.getMessage("autoJoinRed"));
				}else{
					if(random.nextBoolean()){
						Utils.getBlue().add(id);
						player.sendMessage(MessageHandler.getMessage("autoJoinBlue"));
					}else{
						Utils.getRed().add(id);
						player.sendMessage(MessageHandler.getMessage("autoJoinRed"));
					}
				}
			}
		}
		DeathManager.startDeathBox(main, Arena.getGameMode());
		ScoreboardHandler.handleMatchStart();
		startKitTasks(main);
		Match.createMatch(Arena.getGameMode(), Utils.getRed(), Utils.getBlue(), Arena.getTitle());
		boolean isNormal = false;
		List<Location> spawns = getSpawns(Arena.getTitle(), "red");
		if(spawns == null || spawns.isEmpty()){
			isNormal = true;
			spawns = Arena.getRedSpawns();
		}
		for(UUID id : Utils.getRed()){
			Player player = Bukkit.getPlayer(id);
			player.sendMessage(MessageHandler.getMessage("matchBegins"));
			player.setLevel(0);
			player.setExp(0f);
			InventoryEditor.handleMatchStart(player, 0);
			player.teleport(spawns.get(random.nextInt(spawns.size())));
			if(!isNormal){
				player.sendMessage(MessageHandler.getMessage("spawnedAtInitial"));
			}
		}
		spawns = getSpawns(Arena.getTitle(), "blue");
		if(spawns == null || spawns.isEmpty()){
			isNormal = true;
			spawns = Arena.getBlueSpawns();
		}else{
			isNormal = false;
		}
		for(UUID id : Utils.getBlue()){
			Player player = Bukkit.getPlayer(id);
			player.sendMessage(MessageHandler.getMessage("matchBegins"));
			player.setLevel(0);
			player.setExp(0f);
			InventoryEditor.handleMatchStart(player, 1);
			player.teleport(spawns.get(random.nextInt(spawns.size())));
			if(!isNormal){
				player.sendMessage(MessageHandler.getMessage("spawnedAtInitial"));
			}
		}
		matchTime = 0;
		if(Arena.getGameMode() == GameMode.TDM){
			matchTime = Main.tdmTime;
		}else if(Arena.getGameMode() == GameMode.CTF){
			matchTime = Main.ctfTime;
		}else if(Arena.getGameMode() == GameMode.RTF){
			matchTime = Main.rtfTime;
		}else if(Arena.getGameMode() == GameMode.ELM){
			matchTime = Main.elmTime;
		}
		timer = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
			boolean send = false;
			@Override
			public void run() {
				if((matchTime <= 5 && matchTime > 0) || matchTime == 10 || (matchTime % 15 == 0 && matchTime <= 60) || matchTime % 30 == 0){
					send = true;
				}
				if(matchTime >= 0){
					ScoreboardHandler.handleTimeChange(matchTime);
					if(send){
						int minutes = matchTime / 60;
						int seconds = matchTime - 60 * minutes;
						String minString = null;
						String secString = null;
						if(minutes < 10){
							minString = "0" + minutes;
						}else{
							minString = "" + minutes;
						}if(seconds < 10){
							secString = "0" + seconds;
						}else{
							secString = "" + seconds;
						}
						for(UUID id : Utils.getPlayers()){
							Player player = Bukkit.getPlayer(id);
							player.sendMessage(MessageHandler.getMessage("timeRemaining", "%minutes%", minString, "%seconds%", secString));
							Utils.playTimerPling(player);
						}
					}
					send = false;
					if(matchTime == 0){
						Bukkit.getScheduler().cancelTask(timer);
						timer = 0;
						endMatch(main, false);
					}
					matchTime --;
				}
			}
		}, 0l, 20l).getTaskId();
	}
	private static void endMatch(Main main, boolean isImmediate){
		lastArena = currentArena;
		currentArena = null;
		Utils.setGameStatus(GameStatus.FINALIZING);
		DeathManager.handleMatchEnd();
		Match.handleMatchEnd(main, isImmediate);
	}
	private static void createArena(String title){
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas." + title);
		World world = Bukkit.getWorld(section.getString("world"));
		List<Location> redLocations = new ArrayList<Location>();
		List<Location> blueLocations = new ArrayList<Location>();
		for(String string : section.getStringList("spawns.red")){
			String[] args = string.split(" : ");
			double x = Double.parseDouble(args[0]);
			double y = Double.parseDouble(args[1]);
			double z = Double.parseDouble(args[2]);
			float yaw = Float.parseFloat(args[3]);
			float pitch = Float.parseFloat(args[4]);
			Location loc = new Location(world, x, y, z, yaw, pitch);
			redLocations.add(loc);
		}
		for(String string : section.getStringList("spawns.blue")){
			String[] args = string.split(" : ");
			double x = Double.parseDouble(args[0]);
			double y = Double.parseDouble(args[1]);
			double z = Double.parseDouble(args[2]);
			float yaw = Float.parseFloat(args[3]);
			float pitch = Float.parseFloat(args[4]);
			Location loc = new Location(world, x, y, z, yaw, pitch);
			blueLocations.add(loc);
		}
		double x = section.getDouble("lobby.x");
		double y = section.getDouble("lobby.y");
		double z = section.getDouble("lobby.z");
		float yaw = (float) section.getDouble("lobby.yaw");
		float pitch = (float) section.getDouble("lobby.pitch");
		Location lobbySpawn = new Location(world, x, y, z, yaw, pitch);
		x = section.getDouble("death.x");
		y = section.getDouble("death.y");
		z = section.getDouble("death.z");
		yaw = (float) section.getDouble("death.yaw");
		pitch = (float) section.getDouble("death.pitch");
		Location deathSpawn = new Location(world, x, y, z, yaw, pitch);
		if(section.getString("gamemode").equalsIgnoreCase("TDM")){
			Arena.initializeTDMArena(lobbySpawn, deathSpawn, title, redLocations, blueLocations);
			return;
		}else if(section.getString("gamemode").equalsIgnoreCase("ELM")){
			Arena.initializeELMArena(lobbySpawn, deathSpawn, title, redLocations, blueLocations);
			return;
		}
		x = section.getInt("flag.red.x");
		y = section.getInt("flag.red.y");
		z = section.getInt("flag.red.z");
		Location redFlagLoc = new Location(world, x, y, z);
		x = section.getInt("flag.blue.x");
		y = section.getInt("flag.blue.y");
		z = section.getInt("flag.blue.z");
		Location blueFlagLoc = new Location(world, x, y, z);
		if(section.getString("gamemode").equalsIgnoreCase("CTF")){
			Arena.initializeCTFArena(deathSpawn, lobbySpawn, redFlagLoc, blueFlagLoc, title, redLocations, blueLocations);
		}else{
			x = section.getInt("flag.neutral.x");
			y = section.getInt("flag.neutral.y");
			z = section.getInt("flag.neutral.z");
			Location neutralLoc = new Location(world, x, y, z);
			Arena.initializeRTFArena(deathSpawn, lobbySpawn, neutralLoc, redFlagLoc, blueFlagLoc, title, redLocations, blueLocations);
		}
	}
	public static int getTimeLeft(){
		return matchTime;
	}
	private static void startKitTasks(Main main){
		SniperKit.init(main);
		SprayerKit.init(main);
	}
	public static void forceEndTimer(int status, Main main, boolean isPlayerCount){
		if(status == 0){
			Bukkit.getScheduler().cancelTask(timer);
			timer = 0;
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabHalt"));
				if(isPlayerCount){
					player.sendMessage(MessageHandler.getMessage("cancelInitPlayers"));
				}else{
					player.sendMessage(MessageHandler.getMessage("cancelInit"));
				}
				player.setLevel(0);
				player.setExp(1f);
			}
			Utils.setGameStatus(GameStatus.NOT_STARTED);
		}else if(status == 1){
			Bukkit.getScheduler().cancelTask(timer);
			timer = 0;
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				if(isPlayerCount){
					player.sendMessage(MessageHandler.getMessage("cancelInitPlayers"));
				}else{
					player.sendMessage(MessageHandler.getMessage("cancelInit"));
				}
				player.teleport(Utils.getLobbySpawn());
				player.setLevel(0);
				player.setExp(1f);
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				InventoryEditor.handleForceEnd(player);
			}
			Utils.setGameStatus(GameStatus.NOT_STARTED);
		}else if(status == 2){
			Bukkit.getScheduler().cancelTask(timer);
			timer = 0;
			endMatch(main, !isPlayerCount);
		}
	}
	private static List<Location> getSpawns(String arena, String team){
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas." + arena);
		World world = Bukkit.getWorld(section.getString("world"));
		List<Location> locations = new ArrayList<Location>();
		for(String string : section.getStringList("initial." + team)){
			String[] args = string.split(" : ");
			double x = Double.parseDouble(args[0]);
			double y = Double.parseDouble(args[1]);
			double z = Double.parseDouble(args[2]);
			float yaw = Float.parseFloat(args[3]);
			float pitch = Float.parseFloat(args[4]);
			Location loc = new Location(world, x, y, z, yaw, pitch);
			locations.add(loc);
		}
		return locations;
	}
}
