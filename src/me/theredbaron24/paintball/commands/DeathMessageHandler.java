package me.theredbaron24.paintball.commands;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class DeathMessageHandler extends BukkitCommand{
	
	public DeathMessageHandler(String name){
		super(name);
	}

	private static Set<UUID> ignored = new HashSet<UUID>();
	private static Random random = new Random();
	private static DecimalFormat format = new DecimalFormat("#.##");

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.toggle") || sender.hasPermission("paintball.general") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if(ignored.contains(player.getUniqueId())){
				ignored.remove(player.getUniqueId());
				sender.sendMessage(MessageHandler.getMessage("deathMessageToggle"));
			}else{
				ignored.add(player.getUniqueId());
				sender.sendMessage(MessageHandler.getMessage("deathMessageToggle1"));
				 
			}
		}
		return false;
	}
	public static void removePlayer(Player player){
		if(ignored.contains(player.getUniqueId())){
			ignored.remove(player.getUniqueId());
		}
	}
	public static void sendDeathMessage(Player killed, Player killer, String data, Main main){
		if(killer != null){
			NMS.sendActionBar(killed, MessageHandler.getMessage("deathActionBar", "%killer%", Utils.getBoldedColoredName(killer)), 5, 30, 5);
			NMS.sendActionBar(killer, MessageHandler.getMessage("killActionBar", "%killed%", Utils.getBoldedColoredName(killed)), 5, 30, 5);
		}
		List<String> messages = null;
		if(data == null){
			messages = main.getConfig().getStringList("deathMessages.standard");
		}else{
			messages = main.getConfig().getStringList("deathMessages." + data.toLowerCase());			
		}
		String message = null;
		if(messages.isEmpty()){
			message = "%killer% &efragged %killed%";
		}else{
			message = messages.get(random.nextInt(messages.size()));
		}
		if(data != null && data.equalsIgnoreCase("void")){
			message = message.replaceAll("%killed%", Utils.getColoredName(killed));
		}else{
			message = message.replaceAll("%killer%", Utils.getColoredName(killer)).replaceAll("%killed%", Utils.getColoredName(killed));
		}
		if(data != null && data.equalsIgnoreCase("sniper")){
			String distance = format.format(killer.getLocation().distance(killed.getLocation()));
			message = message.replaceAll("%distance%", distance);
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		for(UUID id: Utils.getPlayers()){
			if(ignored.contains(id) == false){
				Bukkit.getPlayer(id).sendMessage(message);
			}
		}
	}
	public static int getSize(){
		return ignored.size();
	}
}
