package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.events.PlayerJoinListener;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class JoinCommand extends BukkitCommand{

	public JoinCommand(String name) {
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if(player.hasPermission("paintball.join") == false){
				player.sendMessage(MessageHandler.getMessage("noPermission"));
			}else{
				PlayerJoinListener.join(player, Main.main, false);
			}
		}
		return false;
	}
}
