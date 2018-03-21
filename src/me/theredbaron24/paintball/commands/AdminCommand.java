package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.main.TimerInterrupter;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class AdminCommand extends BukkitCommand{

	public AdminCommand(String name){
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if((sender.hasPermission("paintball.admin") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		if(args.length == 0){
			sender.sendMessage(MessageHandler.getMessage("adminCommandList1"));
			sender.sendMessage(MessageHandler.getMessage("adminCommandList2"));
			sender.sendMessage(MessageHandler.getMessage("adminCommandList3"));
			sender.sendMessage(MessageHandler.getMessage("adminCommandList4"));
			sender.sendMessage(MessageHandler.getMessage("adminCommandList5"));
			sender.sendMessage(MessageHandler.getMessage("adminCommandList6"));

		}else if(args.length == 1){
			if(args[0].equalsIgnoreCase("spawn")){
				if(player == null){
					sender.sendMessage(MessageHandler.getMessage("noPermission"));
				}else{
					setLobby(player);
				}
			}else if(args[0].equalsIgnoreCase("stop")){
				TimerInterrupter.stop(false, sender, Main.main);
			}else if(args[0].equalsIgnoreCase("start")){
				TimerInterrupter.start(null, false, sender, Main.main);
			}else if(args[0].equalsIgnoreCase("reload")){
				reload();
			}else{
				sender.sendMessage(MessageHandler.getMessage("syntaxNotValid"));
			}
		}else if(args.length == 2){
			if(args[0].equalsIgnoreCase("start")){
				TimerInterrupter.start(args[1], false, sender, Main.main);
			}else if(args[0].equalsIgnoreCase("stop")){
				if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
					TimerInterrupter.stop(Boolean.parseBoolean(args[1]), sender, Main.main);
				}else{
					sender.sendMessage(MessageHandler.getMessage("invalidBool"));
				}
			}
			else{
				sender.sendMessage(MessageHandler.getMessage("syntaxNotValid"));
			}
		}else if(args.length == 3){
			if(args[0].equalsIgnoreCase("start")){
				if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")){
					TimerInterrupter.stop(Boolean.parseBoolean(args[2]), sender, Main.main);
				}else{
					sender.sendMessage(MessageHandler.getMessage("invalidBool"));
				}
				TimerInterrupter.start(args[1], Boolean.parseBoolean(args[2]), sender, Main.main);
			}else{
				sender.sendMessage(MessageHandler.getMessage("syntaxNotValid"));
			}
		}else if(args.length == 4){
			if(args[0].equalsIgnoreCase("add")){
				int i = 0;
				try{
					i = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(MessageHandler.getMessage("noPointCount"));
					return false;
				}
				addStats(args[1], i, args[3], sender);
			}else if(args[0].equalsIgnoreCase("set")){
				int i = 0;
				try{
					i = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(MessageHandler.getMessage("noPointCount"));
					return false;
				}
				setStats(args[1], i, args[3], sender);
			}
		}else{
			sender.sendMessage(MessageHandler.getMessage("syntaxNotValid"));
		}
		return false;
	}
	
	private void setLobby(Player player){
		Location location = player.getLocation();
		Configuration.getArenaConfig().set("spawn.x", location.getX());
		Configuration.getArenaConfig().set("spawn.y", location.getY());
		Configuration.getArenaConfig().set("spawn.z", location.getZ());
		Configuration.getArenaConfig().set("spawn.yaw", location.getYaw());
		Configuration.getArenaConfig().set("spawn.pitch", location.getPitch());
		Configuration.getArenaConfig().set("spawn.world", location.getWorld().getName());
		Configuration.saveArenaConfig();
		Utils.setLobbySpawn(location);
		player.sendMessage(MessageHandler.getMessage("mainLobbySet"));
	}
	private void reload(){
		Bukkit.getPluginManager().disablePlugin(Main.main);
		Bukkit.getPluginManager().enablePlugin(Main.main);
		for(Player online : Bukkit.getOnlinePlayers()){
			online.sendMessage(MessageHandler.getMessage("pluginReloaded"));
		}
	}
	private void addStats(String type, int points, String name, CommandSender sender){
		String[] types = {"points", "kills", "deaths", "rounds", "victories", "defeats", "draws", "shots", "captures", "currency", "tokens"};
		for(String s : types){
			if(s.equals(type.toLowerCase())){
				TaskHandler.runTask(new Runnable() {
					@Override
					public void run() {
						if(MySQL.hasValidConnection() == false){
							MySQL.connectToDatabase();
						}
						if(MySQL.isInDatabase(name, true)){
							MySQL.addInt(MySQL.getIDFromName(name), type.toLowerCase(), points);
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("adminAdd", "%points%", "" + points, "%type%", type, "%name%", name));
								}
							});
						}else{
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("playerNotInDatabase", "%name%", name));
								}
							});
						}
					}
				});
				return;
			}
		}
		sender.sendMessage(MessageHandler.getMessage("adminTypeNotSupported", "%type%", type));
		String typeStr = "";
		for(String s : types){
			typeStr += s + ", ";
		}
		typeStr = typeStr.substring(0, typeStr.length() -2);
		sender.sendMessage(MessageHandler.getMessage("adminUseDiffOptions", "%options%", typeStr));
	}
	private void setStats(String type, int points, String name, CommandSender sender){
		String[] types = {"points", "kills", "deaths", "rounds", "victories", "defeats", "draws", "shots", "captures", "currency", "tokens"};
		for(String s : types){
			if(s.equals(type.toLowerCase())){
				TaskHandler.runTask(new Runnable() {
					@Override
					public void run() {
						if(MySQL.hasValidConnection() == false){
							MySQL.connectToDatabase();
						}
						if(MySQL.isInDatabase(name, true)){
							MySQL.setInt(MySQL.getIDFromName(name), type.toLowerCase(), points);
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("adminSet", "%points%", "" + points, "%type%", type, "%name%", name));
								}
							});
						}else{
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("playerNotInDatabase", "%name%", name));
								}
							});
						}
					}
				});
				return;
			}
		}
		sender.sendMessage(MessageHandler.getMessage("adminTypeNotSupported", "%type%", type));
		String typeStr = "";
		for(String s : types){
			typeStr += s + ", ";
		}
		typeStr = typeStr.substring(0, typeStr.length() -2);
		sender.sendMessage(MessageHandler.getMessage("adminUseDiffOptions", "%options%", typeStr));
	}
	
}
