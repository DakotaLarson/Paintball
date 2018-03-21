package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.MessageHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class VoteCommand extends BukkitCommand{
	
	public VoteCommand(String name){
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(Main.enableVoting == false){
			sender.sendMessage(MessageHandler.getMessage("votingDisabled"));
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if(Main.votingPermission){
				if((player.hasPermission("paintball.vote") || sender.hasPermission("paintball.general") || player.isOp()) == false){
					sender.sendMessage(MessageHandler.getMessage("noPermission"));
					return false;
				}
			}
			if(VoteHandler.isTempDisabled()){
				sender.sendMessage(MessageHandler.getMessage("votingTempDis"));
				return false;
			}
			if(args.length == 0){
				VoteHandler.sendVoteMessages(player);
			}else if(args.length == 1){
				VoteHandler.vote(player, args[0]);
			}else{
				sender.sendMessage(MessageHandler.getMessage("votingUseCmd"));
			}
		}
		return false;
	}
}
