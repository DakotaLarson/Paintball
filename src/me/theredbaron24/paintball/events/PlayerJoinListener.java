package me.theredbaron24.paintball.events;

import java.util.Random;
import java.util.UUID;

import me.theredbaron24.paintball.commands.LeaveCommand;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.GameTimer;
import me.theredbaron24.paintball.main.InventoryEditor;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.Match;
import me.theredbaron24.paintball.main.RankHandler;
import me.theredbaron24.paintball.main.ScoreboardHandler;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.main.TimerInterrupter;
import me.theredbaron24.paintball.main.TokenHandler;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	
	private static Random random = new Random();
	private static Main main = null;
	public PlayerJoinListener(Main main){
		PlayerJoinListener.main = main;
	}

	@EventHandler()
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(Main.autoJoin && Main.pbWorld == false){
			join(player, main, true);
		}else if(Main.pbWorld){
			if(player.getLocation().getWorld().getName().equals(main.getWorld().getName())){
				join(player, main, true);
			}
		}
	}
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event){
		if(Main.pbWorld){
			Player player = event.getPlayer();
			if(event.getPlayer().getWorld().getName().equals(main.getWorld().getName())){
				join(player, main, true);	
			}else if(event.getFrom().getName().equals(main.getWorld().getName())){
				LeaveCommand.leave(player, true, main);
			}
		}
	}
	public static void join(Player player, Main main, boolean isEvent){
		if(Main.maxPlayers > 0){
			if(Utils.getPlayers().size() + Utils.getAsyncPlayers().size() + Utils.getPlayersToAdd().size() >= Main.maxPlayers){
				if((player.hasPermission("paintball.joinfull") || player.isOp()) == false){
					player.sendMessage(MessageHandler.getMessage("noJoinGame"));
					return;
				}	
			}
		}
		if(Utils.getPlayers().contains(player.getUniqueId())){
			player.sendMessage(MessageHandler.getMessage("alreadyIntegrated"));
			return;
		}
		if(Utils.getLobbySpawn() == null){
			player.sendMessage(MessageHandler.getMessage("noLobbySpawn"));
			return;
		}else{
			Bukkit.getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.teleport(Utils.getLobbySpawn());
				}
			});
		}
		if(TimerInterrupter.isStopped()){
			player.sendMessage(MessageHandler.getMessage("matchesCurrHalted"));
		}
		if(Utils.getGameStatus() != GameStatus.FINALIZING){
			Utils.getPlayers().add(player.getUniqueId());
		}
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20d);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(1f);
		Utils.removeArmor(player);
		TokenHandler.handleJoin(player);
		if(Utils.getGameStatus() == GameStatus.NOT_STARTED || Utils.getGameStatus() == GameStatus.COUNTING_DOWN || Utils.getGameStatus() == GameStatus.FINALIZING){
			InventoryEditor.handlePlayerJoin(player, 0, main);
			VoteHandler.setVotingBoard(player);
			if(Utils.getGameStatus() == GameStatus.NOT_STARTED){
				if(Utils.getPlayers().size() >= Main.minPlayers){
					GameTimer.startCountdown(main);
				}else{
					player.sendMessage(MessageHandler.getMessage("matchBeginsOnceMore", "%diff%", "" + (Main.minPlayers - Utils.getPlayers().size())));
					NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabNoStart"));
				}
			}else if(Utils.getGameStatus() == GameStatus.COUNTING_DOWN){
				player.sendMessage(MessageHandler.getMessage("matchesBeginSoon"));
				NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabCounting"));
			}else if(Utils.getGameStatus() == GameStatus.FINALIZING){
				Utils.getPlayersToAdd().add(player.getUniqueId());
				player.sendMessage(MessageHandler.getMessage("matchFinalTime", "%arena%", Arena.getTitle()));
				NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabFinal"));
			}
		}else if(Utils.getGameStatus() == GameStatus.INITIALIZING){
			InventoryEditor.handlePlayerJoin(player, 0, main);
			Bukkit.getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.teleport(Arena.getLobbySpawn());
				}
			});
			player.sendMessage(MessageHandler.getMessage("matchAlreadyInit", "%arena%", Arena.getTitle()));
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabInit"));
		}else{
			player.sendMessage(MessageHandler.getMessage("matchAlreadyRunning", "%arena%", Arena.getTitle()));
			NMS.sendTabTitle(player, MessageHandler.getMessage("nmsTabTitle"), MessageHandler.getMessage("nmsTabRun"));
			if(Utils.getBlue().size() > Utils.getRed().size()){
				Utils.getRed().add(player.getUniqueId());
			}else if(Utils.getRed().size() > Utils.getBlue().size()){
				Utils.getBlue().add(player.getUniqueId());
			}else{
				if(Match.getRedPoints() > Match.getBluePoints()){
					Utils.getBlue().add(player.getUniqueId());
				}else if(Match.getBluePoints() > Match.getRedPoints()){
					Utils.getRed().add(player.getUniqueId());
				}else{
					if(random.nextBoolean()){
						Utils.getRed().add(player.getUniqueId());
					}else{
						Utils.getBlue().add(player.getUniqueId());
					}
				}
			}
		}
		ScoreboardHandler.handleJoin(player);
		if(isEvent){
			updateDatabase(player, main);
		}else{
			updateKit(player, main);
		}
	}
	private static void joinLate(Player player){
		if(Utils.getGameStatus() != GameStatus.RUNNING) return;
		if(Utils.getRed().contains(player.getUniqueId())){
			InventoryEditor.handlePlayerJoin(player, 0, main);
			moveLatePlayer(player, 0);
		}else{
			InventoryEditor.handlePlayerJoin(player, 1, main);
			moveLatePlayer(player, 1);
		}	
	}
	private static void moveLatePlayer(Player player, int team){
		if(team == 0){
			Location loc = Arena.getRedSpawns().get(random.nextInt(Arena.getRedSpawns().size()));
			Bukkit.getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.teleport(loc);
				}
			});
		}else{
			Location loc = Arena.getBlueSpawns().get(random.nextInt(Arena.getBlueSpawns().size()));
			Bukkit.getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.teleport(loc);
				}
			});
		}
	}
	private static void updateDatabase(final Player player, Main main){
		String name = player.getName();
		String id = player.getUniqueId().toString();
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(!MySQL.hasValidConnection()){
					MySQL.connectToDatabase();
				}
				if(!MySQL.isInDatabase(id, false)){
					if(MySQL.addPlayerToDatabase(id, name)){
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.sendMessage(MessageHandler.getMessage("regisSuccess"));
							}
						});
					}else{
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.sendMessage(MessageHandler.getMessage("regisNoSuccess"));
							}
						});		
					}
				}else{
					String nameInDb = MySQL.getString(id, "name");
					if(!nameInDb.equals(name)){
						MySQL.setString(id, "name", name);
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.sendMessage(MessageHandler.getMessage("regisNameChange"));
							}
						});
					}
				}
				String kit = MySQL.getString(id, "kit");
				int points = MySQL.getInt(id, "points");
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						RankHandler.handleJoin(player, points);
						if(kit.toLowerCase().equals("none")){
							Kit.getKits().put(UUID.fromString(id), KitType.valueOf(kit.toUpperCase()));
							player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", MessageHandler.getMessage("noKit")));
							joinLate(player);
							return;
						}
						if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled") == false){
							Kit.getKits().put(UUID.fromString(id), KitType.NONE);
							player.sendMessage(MessageHandler.getMessage("kitRegisReset"));
							joinLate(player);
							return;
						}else if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".permissionRequired")){
							if(!player.hasPermission("paintball.kit." + kit.toLowerCase())){
								Kit.getKits().put(UUID.fromString(id), KitType.NONE);
								player.sendMessage(MessageHandler.getMessage("kitRegisNoPerm"));
								joinLate(player);
								return;
							}
						}
						Kit.getKits().put(UUID.fromString(id), KitType.valueOf(kit.toUpperCase()));
						String kitTitle = kit;
						String initChar = kitTitle.substring(0, 1);
						kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
						player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", kitTitle));
						joinLate(player);
					}
				});	
			}
		});
	}
	private static void updateKit(Player player, Main main){
		UUID uuid = player.getUniqueId();
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(MySQL.hasValidConnection() == false){
					MySQL.connectToDatabase();
				}
				String kit = MySQL.getString(uuid.toString(), "kit");
				int points = MySQL.getInt(uuid.toString(), "points");
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						RankHandler.handleJoin(player, points);
						if(kit.toLowerCase().equals("none")){
							Kit.getKits().put(uuid, KitType.valueOf(kit.toUpperCase()));
							player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", MessageHandler.getMessage("noKit")));
							joinLate(player);
							return;
						}
						if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled") == false){
							Kit.getKits().put(uuid, KitType.NONE);
							player.sendMessage(MessageHandler.getMessage("kitRegisReset"));
							joinLate(player);
							return;
						}else if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".permissionRequired")){
							if(player.hasPermission("paintball.kit." + kit.toLowerCase()) == false){
								Kit.getKits().put(uuid, KitType.NONE);
								player.sendMessage(MessageHandler.getMessage("kitRegisNoPerm"));
								joinLate(player);
								return;
							}
						}
						Kit.getKits().put(uuid, KitType.valueOf(kit.toUpperCase()));
						String kitTitle = kit;
						String initChar = kitTitle.substring(0, 1);
						kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
						player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", kitTitle));
						joinLate(player);
					}
				});
			}
		});
	}
}
