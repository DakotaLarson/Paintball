package me.theredbaron24.paintball.main;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.DemoKit;
import me.theredbaron24.paintball.kits.JuggernautKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.ReloaderKit;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class Match{

	private static int redPoints = 0;
	private static int bluePoints = 0;
	private static GameMode mode = null;
	private static int redKills = 0;
	private static int blueKills = 0;
	private static int redCaptures = 0;
	private static int blueCaptures = 0;
	private static int redShots = 0;
	private static int blueShots = 0;
	private static int redHits = 0;
	private static int blueHits = 0;
	private static String arena = null;
	private static int task = 0;
	
	public static void createMatch(GameMode mode, Set<UUID> redTeam, Set<UUID> blueTeam, String arena){
		Match.mode = mode;
		Match.arena = arena;
		redPoints = 0;
		bluePoints = 0;
		redKills = 0;
		blueKills = 0;
		redCaptures = 0;
		blueCaptures = 0;
		redShots = 0;
		blueShots = 0;
		redHits = 0;
		blueHits = 0;
	}
	
	public static void handleMatchEnd(Main main, boolean isImmediate){
		int gameStatus = -1;
		DecimalFormat format = new DecimalFormat("#.##");
		if(redPoints > bluePoints){
			gameStatus = 1;
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.sendMessage(MessageHandler.getMessage("redTeamWon"));
				player.sendMessage(MessageHandler.getMessage("statsCalcShortly"));
			}
		}else if(bluePoints > redPoints){
			gameStatus = 2;
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.sendMessage(MessageHandler.getMessage("blueTeamWon"));
				player.sendMessage(MessageHandler.getMessage("statsCalcShortly"));
			}	
		}else{
			gameStatus = 0;
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.sendMessage(MessageHandler.getMessage("noWinner"));
				player.sendMessage(MessageHandler.getMessage("statsCalcShortly"));
			}
		}
		World world = main.getWorld();
		for(Entity e : world.getEntitiesByClasses(Snowball.class, Egg.class, EnderPearl.class, Fireball.class)){
			e.remove();
		}
		if(Arena.getGameMode() == GameMode.CTF){
			CTFFlagHandler.resetFlags(true);
		}else if(Arena.getGameMode() == GameMode.RTF){
			RTFFlagHandler.resetFlags(true);
		}
		DemoKit.removeMines(main);
		SprayerKit.endTask();
		SniperKit.endTask();
		BouncerKit.handleMatchEnd();
		ReloaderKit.handleMatchEnd();
		JuggernautKit.handleMatchEnd();
		ReloadHandler.handleMatchEnd();
		Set<String> saves = new HashSet<String>();
		for(UUID id : Utils.getPlayers()){
			Player player = Bukkit.getPlayer(id);
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabFinal"));
			try{
				player.getScoreboard().getTeam("red").setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				player.getScoreboard().getTeam("blue").setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(MessageHandler.getMessage("scoreboardMatchComplete"));
			}catch(Exception e){
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + player.getName() + " did not have the correct teams on their scoreboard.");
			}
			if(player.getWalkSpeed() != 0.2f){
				player.setWalkSpeed(0.2f);
			}
			Kit.changeKit(player, main);
			PlayerInventory inv = player.getInventory();
			ItemStack item1 = inv.getItem(5);
			ItemStack item2 = inv.getItem(6);
			ItemStack item3 = inv.getItem(7);
			ItemStack[] armor = inv.getArmorContents();
			inv.clear();
			inv.setItem(5, item1);
			inv.setItem(6, item2);
			inv.setItem(7, item3);
			if(!isImmediate){
				inv.setArmorContents(armor);
			}
			Objective stats = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			player.sendMessage(MessageHandler.getMessage("matchStatsTitle"));
			player.sendMessage(MessageHandler.getMessage("matchStats", "%total%", "" + (redPoints + bluePoints), "%red%", "" + redPoints, "%blue%", "" + bluePoints));
			player.sendMessage(MessageHandler.getMessage("matchStats1", "%total%", "" + (redHits + blueHits), "%red%", "" + redHits, "%blue%", "" + blueHits));
			player.sendMessage(MessageHandler.getMessage("matchStats2", "%total%", "" + (redKills + blueKills), "%red%", "" + redKills, "%blue%", "" + blueKills));
			player.sendMessage(MessageHandler.getMessage("matchStats3", "%total%", "" + (redShots + blueShots), "%red%", "" + redShots, "%blue%", "" + blueShots));
			if(mode == GameMode.CTF){
				player.sendMessage(MessageHandler.getMessage("matchStats4", "%total%", "" + (redCaptures + blueCaptures), "%red%", "" + redCaptures, "%blue%", "" + blueCaptures));
			}
			int playerPoints = stats.getScore(MessageHandler.getMessage("playerPoints")).getScore();
			int playerHits = stats.getScore(MessageHandler.getMessage("playerHits")).getScore();
			int playerKills = stats.getScore(MessageHandler.getMessage("playerKills")).getScore();
			int playerDeaths = stats.getScore(MessageHandler.getMessage("playerDeaths")).getScore();
			int playerShots = stats.getScore(MessageHandler.getMessage("playerShots")).getScore();
			int playerCurrency = stats.getScore(MessageHandler.getMessage("playerCurr")).getScore();
			int playerCaptures = getCaptures(player);
			playerPoints += Main.matchPartPoints;
			playerCurrency += Main.matchPartCurr;
			if(redPoints > bluePoints){
				if(Utils.getRed().contains(player.getUniqueId())){
					playerPoints += Main.matchWinPoints;
					playerCurrency += Main.matchWinCurr;
				}
			}else if(bluePoints > redPoints){
				if(Utils.getBlue().contains(player.getUniqueId())){
					playerPoints += Main.matchWinPoints;
					playerCurrency += Main.matchWinCurr;
				}
			}else{
				playerPoints += Main.matchWinPoints;
				playerCurrency += Main.matchWinCurr;
			}
			stats.getScore(MessageHandler.getMessage("playerPoints")).setScore(playerPoints);
			stats.getScore(MessageHandler.getMessage("playerCurr")).setScore(playerCurrency);
			prepareSaves(saves, player.getUniqueId().toString(), gameStatus, playerPoints, playerKills, playerDeaths, playerShots, playerCurrency, playerCaptures, mode);
			final int finalCurr = playerCurrency;
			final int finalPts = playerPoints;
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					if(player.isOnline() == false) return;
					player.sendMessage(MessageHandler.getMessage("personalStatsTitle"));
					player.sendMessage(MessageHandler.getMessage("personalStats", "%count%", "" + finalPts));
					player.sendMessage(MessageHandler.getMessage("personalStats1", "%count%", "" + playerHits));
					player.sendMessage(MessageHandler.getMessage("personalStats2", "%count%", "" + playerKills));
					player.sendMessage(MessageHandler.getMessage("personalStats3", "%count%", "" + playerDeaths));
					player.sendMessage(MessageHandler.getMessage("personalStats4", "%count%", "" + playerShots));
					player.sendMessage(MessageHandler.getMessage("personalStats5", "%count%", "" + finalCurr));
					String kd = null;
					if(playerDeaths == 0){
						kd = "1";
					}else{
						kd = format.format((double) playerKills/playerDeaths);
					}
					player.sendMessage(MessageHandler.getMessage("personalStats6", "%count%", kd));
					if(mode == GameMode.CTF || mode == GameMode.RTF){
						player.sendMessage(MessageHandler.getMessage("personalStats7", "%count%", "" + playerCaptures));
					}
				}
			}, 100l);
			
		}
		saveData(saves, main);
		if(redPoints > bluePoints){
			int difference = redPoints - bluePoints;
			for(UUID id : Utils.getRed()){
				Player player = Bukkit.getPlayer(id);
				int points = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints")).getScore();
				player.sendMessage(MessageHandler.getMessage("teamWonMatch"));
				if(points > difference){
					player.sendMessage(MessageHandler.getMessage("helpedSecureVic"));
				}
			}
			for(UUID id : Utils.getBlue()){
				Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("teamLostMatch"));
			}
		}else if(bluePoints > redPoints){
			int difference = bluePoints - redPoints;
			for(UUID id : Utils.getBlue()){
				Player player = Bukkit.getPlayer(id);
				int points = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints")).getScore();
				player.sendMessage(MessageHandler.getMessage("teamWonMatch"));
				if(points > difference){
					player.sendMessage(MessageHandler.getMessage("helpedSecureVic"));
				}
			}
			for(UUID id : Utils.getRed()){
				Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("teamLostMatch"));
			}
		}else{
			for(UUID id : Utils.getPlayers()){
				Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("noTeamWonMatch"));

			}
		}
		if(isImmediate){
			initFinalActions(main, false);
		}else{
			task = Bukkit.getScheduler().runTaskTimer(main, new Runnable(){
				int time = 30;
				@Override
				public void run() {
					if(time >= 5){
						if(redPoints > bluePoints){
							for(UUID id : Utils.getRed()){
								Location location = Bukkit.getPlayer(id).getEyeLocation().add(0, 1, 0);
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								if(location.getWorld().getTime() > 13000){
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
								}else{
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.BLACK).build());
								}
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}else if(bluePoints > redPoints){
							for(UUID id : Utils.getBlue()){
								Location location = Bukkit.getPlayer(id).getEyeLocation().add(0, 1, 0);
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								if(location.getWorld().getTime() > 13000){
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.WHITE).build());
								}else{
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.BLACK).build());
								}
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}else{
							for(UUID id : Utils.getPlayers()){
								Location location = Bukkit.getPlayer(id).getEyeLocation().add(0, 1, 0);
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								if(location.getWorld().getTime() > 13000){
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.GREEN).withColor(Color.WHITE).build());
								}else{
									meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.GREEN).withColor(Color.BLACK).build());
								}
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}
					}else{
						initFinalActions(main, true);
						Bukkit.getScheduler().cancelTask(task);
						task = 0;
					}
				time --;
				}
					
			}, 20l, 10l).getTaskId();
		}
		saveMatchData(main);
	}
	private static void saveMatchData(Main main){
		int prevRedKills = Configuration.getArenaConfig().getInt("arenas." + arena + ".redKills");
		int prevRedShots = Configuration.getArenaConfig().getInt("arenas." + arena + ".redShots");
		int prevRedWins = Configuration.getArenaConfig().getInt("arenas." + arena + ".redWins");
		int prevRedCaps = Configuration.getArenaConfig().getInt("arenas." + arena + ".redCaps");
		int prevBlueKills = Configuration.getArenaConfig().getInt("arenas." + arena + ".blueKills");
		int prevBlueShots = Configuration.getArenaConfig().getInt("arenas." + arena + ".blueShots");
		int prevBlueWins = Configuration.getArenaConfig().getInt("arenas." + arena + ".blueWins");
		int prevBlueCaps = Configuration.getArenaConfig().getInt("arenas." + arena + ".blueCaps");
		int prevMatchCount = Configuration.getArenaConfig().getInt("arenas." + arena + ".matchCount");
		int prevMatchDraws = Configuration.getArenaConfig().getInt("arenas." + arena + ".draws");
		Configuration.getArenaConfig().set("arenas." + arena + ".redKills", prevRedKills + redKills);
		Configuration.getArenaConfig().set("arenas." + arena + ".redShots", prevRedShots + redShots);
		Configuration.getArenaConfig().set("arenas." + arena + ".redCaps", prevRedCaps + redCaptures);
		Configuration.getArenaConfig().set("arenas." + arena + ".blueKills", prevBlueKills + blueKills);
		Configuration.getArenaConfig().set("arenas." + arena + ".blueShots", prevBlueShots + blueShots);
		Configuration.getArenaConfig().set("arenas." + arena + ".blueCaps", prevBlueCaps + blueCaptures);
		Configuration.getArenaConfig().set("arenas." + arena + ".matchCount", prevMatchCount + 1);
		int prevTotalBlueKills = Configuration.getArenaConfig().getInt("general.blueKills");
		int prevTotalRedKills = Configuration.getArenaConfig().getInt("general.redKills");
		int prevTotalMatches = Configuration.getArenaConfig().getInt("general.matches");
		int prevTotalRedShots = Configuration.getArenaConfig().getInt("general.redShots");
		int prevTotalBlueShots = Configuration.getArenaConfig().getInt("general.blueShots");
		int prevTotalRedWins = Configuration.getArenaConfig().getInt("general.redWins");
		int prevTotalBlueWins = Configuration.getArenaConfig().getInt("general.blueWins");
		int prevTotalBlueCaps = Configuration.getArenaConfig().getInt("general.blueCaps");
		int prevTotalRedCaps = Configuration.getArenaConfig().getInt("general.redCaps");
		int prevTotalDraws = Configuration.getArenaConfig().getInt("general.draws");
		Configuration.getArenaConfig().set("general.blueKills", prevTotalBlueKills + blueKills);
		Configuration.getArenaConfig().set("general.redKills", prevTotalRedKills + redKills);
		Configuration.getArenaConfig().set("general.blueShots", prevTotalBlueShots + blueShots);
		Configuration.getArenaConfig().set("general.redShots", prevTotalRedShots + redShots);
		if(redPoints > bluePoints){
			Configuration.getArenaConfig().set("general.redWins", prevTotalRedWins + 1);
			Configuration.getArenaConfig().set("arenas." + arena + ".redWins", prevRedWins + 1);
		}else if(bluePoints > redPoints){
			Configuration.getArenaConfig().set("general.blueWins", prevTotalBlueWins + 1);
			Configuration.getArenaConfig().set("arenas." + arena + ".blueWins", prevBlueWins + 1);
		}else{
			Configuration.getArenaConfig().set("general.draws", prevTotalDraws + 1);
			Configuration.getArenaConfig().set("arenas." + arena + ".draws", prevMatchDraws + 1);
		}
		Configuration.getArenaConfig().set("general.blueCaps", prevTotalBlueCaps + blueCaptures);
		Configuration.getArenaConfig().set("general.redCaps", prevTotalRedCaps + redCaptures);
		Configuration.getArenaConfig().set("general.matches", prevTotalMatches + 1);
		Configuration.saveArenaConfig();
	}
	private static void initFinalActions(Main main, boolean withDelay){
		long delay = 0;
		Utils.getRed().clear();
		Utils.getBlue().clear();	
		for(UUID id : Utils.getPlayers()){
			Player player = Bukkit.getPlayer(id);
			InventoryEditor.handleMatchEnd(player);
			Utils.removeArmor(player);
			if(withDelay){
				Bukkit.getScheduler().runTaskLater(main, new Runnable(){
					@Override
					public void run() {
						VoteHandler.setVotingBoard(player);
						player.teleport(Utils.getLobbySpawn());
					}
				}, delay);
				delay += 5;
			}else{
				VoteHandler.setVotingBoard(player);
				player.teleport(Utils.getLobbySpawn());
			}
			
		}
		if(Utils.getPlayersToAdd().isEmpty() == false){
			Utils.getPlayers().addAll(Utils.getPlayersToAdd());
			Utils.getPlayersToAdd().clear();
		}
		int timeDelay = (Utils.getPlayers().size() + 1) * 5;
		if(withDelay){
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					if (Utils.getPlayers().size() >= Main.minPlayers) {
						GameTimer.startCountdown(main);
					} else {
						Utils.setGameStatus(GameStatus.NOT_STARTED);
						for (UUID id : Utils.getPlayers()) {
							Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("matchBeginsOnceMore", "%diff%", "" + (Main.minPlayers - Utils.getPlayers().size())));
							NMS.sendTabTitle(Bukkit.getPlayer(id), MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabNoStart"));
						}
					}
				}
			}, timeDelay);
		}else{
			if (Utils.getPlayers().size() >= Main.minPlayers) {
				GameTimer.startCountdown(main);
			} else {
				Utils.setGameStatus(GameStatus.NOT_STARTED);
				for (UUID id : Utils.getPlayers()) {
					Player player = Bukkit.getPlayer(id);
					Bukkit.getPlayer(id).sendMessage(MessageHandler.getMessage("matchBeginsOnceMore", "%diff%", "" + (Main.minPlayers - Utils.getPlayers().size())));
					NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabNoStart"));
				}
			}
		}
	}
	/**
	 * 
	 * @param status 0=tie, 1=redWin, 2= blueWin
	 */
	private static void prepareSaves(Set<String> data, String uuid, int status, int points, int kills, int deaths, int shots, int currency, int captures, GameMode mode){
		data.add(uuid + " : " + "points" + " : " + points);
		data.add(uuid + " : " + "kills" + " : " + kills);
		data.add(uuid + " : " + "deaths" + " : " + deaths);
		data.add(uuid + " : " + "shots" + " : " + shots);
		data.add(uuid + " : " + "currency" + " : " + currency);
		if(mode == GameMode.CTF && captures > 0){
			data.add(uuid + " : " + "captures" + " : " + captures);
		}
		data.add(uuid + " : " + "rounds" + " : " + 1);
		if(status == 0){
			data.add(uuid + " : " + "draws" + " : " + 1);
		}else if(status == 1){
			if(Utils.getRed().contains(UUID.fromString(uuid))){
				data.add(uuid + " : " + "victories" + " : " + 1);
			}else{
				data.add(uuid + " : " + "defeats" + " : " + 1);
			}
		}else if(status == 2){
			if(Utils.getBlue().contains(UUID.fromString(uuid))){
				data.add(uuid + " : " + "victories" + " : " + 1);
			}else{
				data.add(uuid + " : " + "defeats" + " : " + 1);
			}
		}
	}
	private static void saveData(Set<String> data, Main main){
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(MySQL.hasValidConnection() == false){
					MySQL.connectToDatabase();
				}
				for(String string : data){
					String[] args = string.split(" : ");
					String uuid = args[0];
					String column = args[1];
					int addition = Integer.parseInt(args[2]);
					MySQL.addInt(uuid, column, addition);
				}
			}
		});
	}
	private static int getCaptures(Player player){
		if(mode == GameMode.CTF || mode == GameMode.RTF){
			return player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerCaps")).getScore();
		}else{
			return -1;
		}
	}
	public static boolean endFinalization(Main main){
		if(task != 0){
			Bukkit.getScheduler().cancelTask(task);
			task = 0;
			initFinalActions(main, false);
			return true;
		}else{
			return false;
		}
	}

	public static int getRedPoints() {
		return redPoints;
	}

	public static int getBluePoints() {
		return bluePoints;
	}

	public static GameMode getMode() {
		return mode;
	}

	public static int getRedKills() {
		return redKills;
	}

	public static int getBlueKills() {
		return blueKills;
	}

	public static int getRedCaptures() {
		return redCaptures;
	}

	public static int getBlueCaptures() {
		return blueCaptures;
	}

	public static void setRedPoints(int redPoints) {
		Match.redPoints = redPoints;
	}

	public static void setBluePoints(int bluePoints) {
		Match.bluePoints = bluePoints;
	}

	public static void setRedKills(int redKills) {
		Match.redKills = redKills;
		if(Main.maxTdmKills > 0 && Arena.getGameMode() == GameMode.TDM && Match.redKills >= Main.maxTdmKills){
			GameTimer.forceEndTimer(2, Main.main, true);
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					String msg = MessageHandler.getMessage("maxKillsMet");
					for(UUID id : Utils.getPlayers()){
						Bukkit.getPlayer(id).sendMessage(msg);
					}
				}
			}, 5l);
		}
	}

	public static void setBlueKills(int blueKills) {
		Match.blueKills = blueKills;
		if(Main.maxTdmKills > 0 && Arena.getGameMode() == GameMode.TDM && Match.blueKills >= Main.maxTdmKills){
			GameTimer.forceEndTimer(2, Main.main, true);
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					String msg = MessageHandler.getMessage("maxKillsMet");
					for(UUID id : Utils.getPlayers()){
						Bukkit.getPlayer(id).sendMessage(msg);
					}
				}
			}, 5l);
		}
	}

	public static void setRedCaptures(int redCaptures) {
		Match.redCaptures = redCaptures;
		if(Arena.getGameMode() == GameMode.CTF && Match.redCaptures >= Main.maxCtfCaps && Main.maxCtfCaps > 0){
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					GameTimer.forceEndTimer(2, Main.main, true);
					Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
						@Override
						public void run() {
							String msg = MessageHandler.getMessage("maxCapturesMet");
							for(UUID id : Utils.getPlayers()){
								Bukkit.getPlayer(id).sendMessage(msg);
							}
						}
					}, 5l);
				}
			}, 5l);
			
		}else if(Arena.getGameMode() == GameMode.RTF && Match.redCaptures >= Main.maxRtfCaps && Main.maxRtfCaps > 0){
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					GameTimer.forceEndTimer(2, Main.main, true);
					Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
						@Override
						public void run() {
							String msg = MessageHandler.getMessage("maxCapturesMet");
							for(UUID id : Utils.getPlayers()){
								Bukkit.getPlayer(id).sendMessage(msg);
							}
						}
					}, 5l);
				}
			}, 5l);
		}
	}

	public static void setBlueCaptures(int blueCaptures) {
		Match.blueCaptures = blueCaptures;
		if(Arena.getGameMode() == GameMode.CTF && Match.blueCaptures >= Main.maxCtfCaps && Main.maxCtfCaps > 0){
			GameTimer.forceEndTimer(2, Main.main, true);
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					String msg = MessageHandler.getMessage("maxCapturesMet");
					for(UUID id : Utils.getPlayers()){
						Bukkit.getPlayer(id).sendMessage(msg);
					}
				}
			}, 5l);
		}else if(Arena.getGameMode() == GameMode.RTF && Match.blueCaptures >= Main.maxRtfCaps && Main.maxRtfCaps > 0){
			GameTimer.forceEndTimer(2, Main.main, true);
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					String msg = MessageHandler.getMessage("maxCapturesMet");
					for(UUID id : Utils.getPlayers()){
						Bukkit.getPlayer(id).sendMessage(msg);
					}
				}
			}, 5l);
		}
	}
	
	public static String getArena(){
		return arena;
	}
	
	public static void setRedShots(int shots){
		Match.redShots = shots;
	}
	
	public static void setBlueShots(int shots){
		Match.blueShots = shots;
	}
	
	public static int getRedShots(){
		return Match.redShots;
	}
	
	public static int getBlueShots(){
		return Match.blueShots;
	}
	
	public static void setRedHits(int hits){
		Match.redHits = hits;
	}
	
	public static void setBlueHits(int hits){
		Match.blueHits = hits;
	}
	
	public static int getRedHits(){
		return Match.redHits;
	}
	
	public static int getBlueHits(){
		return Match.blueHits;
	}
}
