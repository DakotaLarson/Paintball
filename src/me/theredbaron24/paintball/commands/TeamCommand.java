package me.theredbaron24.paintball.commands;

import java.util.UUID;

import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class TeamCommand extends BukkitCommand{

	public TeamCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if((player.hasPermission("paintball.teamchat") || sender.hasPermission("paintball.general") || player.isOp()) == false){
				sender.sendMessage(MessageHandler.getMessage("noPermission"));
			}else if(Utils.getPlayers().contains(player.getUniqueId()) == false){
				sender.sendMessage(MessageHandler.getMessage("teamNotRunning"));
			}else if(Utils.getGameStatus() != GameStatus.RUNNING){
				sender.sendMessage(MessageHandler.getMessage("teamNotRunning"));
			}else if(args.length == 0){
				sender.sendMessage(MessageHandler.getMessage("teamAddMessage"));
			}else{
				if(Utils.getRed().contains(player.getUniqueId())){
					String text = "";
					for(int i = 0; i < args.length; i++){
						text += args[i] + " ";
					}
					text = text.substring(0, text.length() -1);
					String message = MessageHandler.getMessage("teamMessage", "%player%", Utils.getColoredName(player), "%massage%", text);
					for(UUID id : Utils.getRed()){
						Bukkit.getPlayer(id).sendMessage(message);
					}
				}else if(Utils.getBlue().contains(player.getUniqueId())){
					String text = "";
					for(int i = 0; i < args.length; i++){
						text += args[i] + " ";
					}
					text = text.substring(0, text.length() -1);
					String message = MessageHandler.getMessage("teamMessage", "%player%", Utils.getColoredName(player), "%massage%", text);
					for(UUID id : Utils.getBlue()){
						Bukkit.getPlayer(id).sendMessage(message);
					}
				}else{
					sender.sendMessage(MessageHandler.getMessage("teamNotFound"));
				}
			}
		}
		return false;
	}
}
