package me.theredbaron24.paintball.commands;

import java.util.UUID;

import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.CTFFlagHandler;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.GameTimer;
import me.theredbaron24.paintball.main.InventoryEditor;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.RTFFlagHandler;
import me.theredbaron24.paintball.main.RankHandler;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.main.TokenHandler;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class LeaveCommand extends BukkitCommand implements Listener{

	public LeaveCommand(String name){
		super(name);
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent event){
		LeaveCommand.leave(event.getPlayer(), true, Main.main);

	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if(label.equalsIgnoreCase("leave")){
				if((sender.hasPermission("paintball.leave") || sender.hasPermission("paintball.general") || sender.isOp()) == false){
					sender.sendMessage(MessageHandler.getMessage("noPermission"));
					return false;
				}else{
					leave(player, false, Main.main);
					return true;
				}
			}else{
				if(player.hasPermission("paintball.leaveall") == false){
					sender.sendMessage(MessageHandler.getMessage("noPermission"));
				}else{
					if(Utils.getPlayers().contains(player.getUniqueId())){
						leave(player, false, Main.main);
					}	
					if(Utils.getAsyncPlayers().contains(player.getUniqueId())){
						Utils.getAsyncPlayers().remove(player.getUniqueId());
					}else{
						sender.sendMessage(MessageHandler.getMessage("alreadyNoIntegrated"));
						return false;
					}
					player.getInventory().clear();
					Bukkit.getScheduler().runTask(Main.main, new Runnable() {
						@Override
						public void run() {
							player.teleport(Utils.getLobbySpawn());
						}
					});
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					sender.sendMessage(MessageHandler.getMessage("noLongerRegistered"));
				}
			}
		}
		return false;
	}

	public static void leave(Player player, boolean isQuit, Main main){
		if(Utils.getPlayers().contains(player.getUniqueId()) || Utils.getAsyncPlayers().contains(player.getUniqueId()) || Utils.getPlayersToAdd().contains(player.getUniqueId())){
			Utils.getPlayers().remove(player.getUniqueId());
			Utils.getRed().remove(player.getUniqueId());
			Utils.getBlue().remove(player.getUniqueId());
			if(isQuit){
				Utils.getAsyncPlayers().remove(player.getUniqueId());
			}else{
				Utils.getAsyncPlayers().add(player.getUniqueId());
			}
			Utils.getPlayersToAdd().remove(player.getUniqueId());
			Kit.handleLeave(player, main);
			VoteHandler.handleLeave(player);
			RankHandler.handleLeave(player);
			DeathMessageHandler.removePlayer(player);
			DeathManager.handleLeave(player);
			checkPlayerCount(main);
			TokenHandler.handleLeave(player);
			Utils.removeArmor(player);
			NMS.sendTabTitle(player, null, null);
			if(Utils.getGameStatus() == GameStatus.RUNNING){
				saveStats(player, main);
			}else if(Utils.getGameStatus() == GameStatus.INITIALIZING){
				for(UUID id : Utils.getPlayers()){
					Objective obj = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR);
					obj.getScore(MessageHandler.getMessage("votingBoardBlue")).setScore(Utils.getBlue().size());
					obj.getScore(MessageHandler.getMessage("votingBoardRed")).setScore(Utils.getRed().size());
					int size = Utils.getPlayers().size() - (Utils.getRed().size() + Utils.getBlue().size());
					obj.getScore(MessageHandler.getMessage("votingBoardRand")).setScore(size);
				}
			}
			InventoryEditor.handleMatchEnd(player); 
			if(isQuit == false){
				player.teleport(Utils.getLobbySpawn());
			}
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			player.sendMessage(MessageHandler.getMessage("leftPaintball"));
		}else{
			player.sendMessage(MessageHandler.getMessage("alreadyNoIntegrated"));
		}
	}
	private static void saveStats(Player player, Main main){
		Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		if(obj == null) return;
		int points = obj.getScore(MessageHandler.getMessage("playerPoints")).getScore();
		int currency = obj.getScore(MessageHandler.getMessage("playerCurr")).getScore();
		int kills = obj.getScore(MessageHandler.getMessage("playerKills")).getScore();
		int deaths = obj.getScore(MessageHandler.getMessage("playerDeaths")).getScore();
		int shots = obj.getScore(MessageHandler.getMessage("playerShots")).getScore();
		int captures = obj.getScore(MessageHandler.getMessage("playerCaps")).getScore();
		String id = player.getUniqueId().toString();
		boolean b = Arena.getGameMode() == GameMode.CTF || Arena.getGameMode() == GameMode.RTF;
		if(b){
			if(Arena.getGameMode() == GameMode.CTF){
				CTFFlagHandler.dropFlag(player, main);
			}else if(Arena.getGameMode() == GameMode.RTF){
				RTFFlagHandler.dropFlag(player, main);
			}
		}
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(MySQL.hasValidConnection() == false){
					MySQL.connectToDatabase();
				}
				MySQL.addInt(id, "points", points);
				MySQL.addInt(id, "currency", currency);
				MySQL.addInt(id, "kills", kills);
				MySQL.addInt(id, "deaths", deaths);
				MySQL.addInt(id, "shots", shots);
				if(b){
					MySQL.addInt(id, "captures", captures);
				}
			}
		});
	}
	private static void checkPlayerCount(Main main){
		if(Utils.getPlayers().size() < Main.minPlayers){
			if(Utils.getGameStatus() == GameStatus.INITIALIZING){
				GameTimer.forceEndTimer(1, main, true);
			}else if(Utils.getGameStatus() == GameStatus.COUNTING_DOWN){
				GameTimer.forceEndTimer(0, main, true);
			}else if(Utils.getGameStatus() == GameStatus.RUNNING){
				GameTimer.forceEndTimer(2, main, true);
			}
		}else if(Utils.getGameStatus() == GameStatus.RUNNING){
			if(Utils.getRed().size() == 0 || Utils.getBlue().size() == 0){
				GameTimer.forceEndTimer(2, main, true);
			}
		}
	}
}
