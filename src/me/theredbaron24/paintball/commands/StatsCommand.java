package me.theredbaron24.paintball.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class StatsCommand extends BukkitCommand{

	public StatsCommand(String name){
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.stats") || sender.hasPermission("paintball.general") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if(args.length == 0){
			if(sender instanceof Player){
				TaskHandler.runTask(new Runnable() {
					@Override
					public void run() {
						List<String> stats = getStats(sender.getName(), Main.main);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(MessageHandler.getMessage("statsTitle"));
								for(String string : stats){
									sender.sendMessage(string);
								}
							}
						});
					}
				});
			}else{
				sender.sendMessage(MessageHandler.getMessage("statsGeneral"));
				sender.sendMessage(MessageHandler.getMessage("statsGeneral1"));
			}
		}else if(args.length == 1){
			if(args[0].equalsIgnoreCase("arena")){
				sender.sendMessage(MessageHandler.getMessage("useArenaStats"));
			}else if(args[0].equalsIgnoreCase("general")){
				int blueKills = Configuration.getArenaConfig().getInt("general.blueKills");
				int redKills = Configuration.getArenaConfig().getInt("general.redKills");
				int matches = Configuration.getArenaConfig().getInt("general.matches");
				int redShots = Configuration.getArenaConfig().getInt("general.redShots");
				int blueShots = Configuration.getArenaConfig().getInt("general.blueShots");
				int redWins = Configuration.getArenaConfig().getInt("general.redWins");
				int blueWins = Configuration.getArenaConfig().getInt("general.blueWins");
				int blueCaps = Configuration.getArenaConfig().getInt("general.blueCaps");
				int redCaps = Configuration.getArenaConfig().getInt("general.redCaps");
				int draws = Configuration.getArenaConfig().getInt("general.draws");
				sender.sendMessage(MessageHandler.getMessage("generalStatsTitle"));
				sender.sendMessage(MessageHandler.getMessage("generalStats1", "%totalShots%", "" + (redShots + blueShots), "%redShots%", "" + redShots, "%blueShots%", "" + blueShots));
				sender.sendMessage(MessageHandler.getMessage("generalStats2", "%totalKill%", "" + (redKills + blueKills), "%redKill%", "" + redKills, "%blueKill%", "" + blueKills));
				sender.sendMessage(MessageHandler.getMessage("generalStats3", "%totalCap%", "" + (redCaps + blueCaps), "%redCap%", "" + redCaps, "%blueCap%", "" + blueCaps));
				sender.sendMessage(MessageHandler.getMessage("generalStats4", "%totalVic", "" + (redWins + blueWins), "%redVic", "" + redWins, "%blueVic", "" + blueWins));
				sender.sendMessage(MessageHandler.getMessage("generalStats5", "%blueShots%", "" + draws));
				sender.sendMessage(MessageHandler.getMessage("generalStats6", "%blueShots%", "" + matches));
			}else{
				TaskHandler.runTask(new Runnable() {
					@Override
					public void run() {
						List<String> stats = getStats(args[0], Main.main);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								if(stats.isEmpty()){
									sender.sendMessage(MessageHandler.getMessage("noStats", "%player%", args[0]));
								}else{
									sender.sendMessage(MessageHandler.getMessage("playerStatsTitle", "%player%", args[0]));
									for(String string : stats){
										sender.sendMessage(string);
									}
								}
							}
						});
					}
				});
			}
		}else if(args.length == 2){
			if(args[0].equalsIgnoreCase("arena") == false){
				sender.sendMessage(MessageHandler.getMessage("syntaxNotValid"));
			}else{
				String arena = args[1];
				ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas." + arena);
				if(section == null){
					sender.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
				}else{
					sender.sendMessage(MessageHandler.getMessage("arenaStatsTitle", "%arena%", arena));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData", "%kills%", "" + section.getInt("redKills")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData1", "%kills%", "" + section.getInt("blueKills")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData2", "%wins%", "" + section.getInt("redWins")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData3", "%wins%", "" + section.getInt("blueWins")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData4", "%shots%", "" + section.getInt("redShots")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData5", "%shots%", "" + section.getInt("blueShots")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData6", "%captures%", "" + section.getInt("redCaps")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData7", "%captures%", "" + section.getInt("blueCaps")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData8", "%count%", "" + section.getInt("matchCount")));
					if(section.getBoolean("enabled")){
						if(Utils.getGameStatus() == GameStatus.RUNNING){
							if(Arena.getTitle() != null && Arena.getTitle().equals(arena)){
								sender.sendMessage(MessageHandler.getMessage("arenaStatsData9"));
							}else{
								sender.sendMessage(MessageHandler.getMessage("arenaStatsData10"));
							}
						}
					}else{
						sender.sendMessage(MessageHandler.getMessage("arenaStatsData11"));
					}
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData12", "%gamemode%", section.getString("gamemode", "Null")));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData13", "%count%", "" + section.getStringList("spawns.red").size()));
					sender.sendMessage(MessageHandler.getMessage("arenaStatsData14", "%count%", "" + section.getStringList("spawns.blue").size()));
				}
			}
		}
		return false;
	}
	private List<String> getStats(String name, Main main){
		List<String> data = new ArrayList<String>();
		if(MySQL.hasValidConnection() == false){
			MySQL.connectToDatabase();
		}
		if(MySQL.isInDatabase(name, true) == false) return data;
		String uuid = MySQL.getIDFromName(name);
		int points = MySQL.getInt(uuid, "points");
		int kills = MySQL.getInt(uuid, "kills");
		int deaths = MySQL.getInt(uuid, "deaths");
		int rounds = MySQL.getInt(uuid, "rounds");
		int victories = MySQL.getInt(uuid, "victories");
		int defeats = MySQL.getInt(uuid, "defeats");
		int draws = MySQL.getInt(uuid, "draws");
		int shots = MySQL.getInt(uuid, "shots");
		int captures = MySQL.getInt(uuid, "captures");
		int tokens = MySQL.getInt(uuid, "tokens");
		int currency = MySQL.getInt(uuid, "currency");
		String kit = MySQL.getString(uuid, "kit");
		int rank = MySQL.getRank(uuid);
		String kd = null;
		DecimalFormat format = new DecimalFormat("#.##");
		if(deaths == 0){
			kd = "1";
		}else{
			kd = format.format((double) kills/deaths);
		}
		data.add(MessageHandler.getMessage("playerStats", "%count%", "" + points));
		data.add(MessageHandler.getMessage("playerStats1", "%count%", "" + kills));
		data.add(MessageHandler.getMessage("playerStats2", "%count%", "" + deaths));
		data.add(MessageHandler.getMessage("playerStats3", "%kd%", "" + kd));
		data.add(MessageHandler.getMessage("playerStats4", "%count%", "" + rounds));
		data.add(MessageHandler.getMessage("playerStats5", "%count%", "" + victories));
		data.add(MessageHandler.getMessage("playerStats6", "%count%", "" + defeats));
		data.add(MessageHandler.getMessage("playerStats7", "%count%", "" + draws));
		data.add(MessageHandler.getMessage("playerStats8", "%count%", "" + shots));
		data.add(MessageHandler.getMessage("playerStats9", "%count%", "" + captures));
		data.add(MessageHandler.getMessage("playerStats10", "%count%", "" + tokens));
		data.add(MessageHandler.getMessage("playerStats11", "%count%", "" + currency));
		data.add(MessageHandler.getMessage("playerStats12", "%kit%", "" + kit));
		data.add(MessageHandler.getMessage("playerStats13", "%rank%", "" + rank));
		return data;
	}
}
