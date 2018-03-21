package me.theredbaron24.paintball.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.ScoreboardHandler;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class TeamSelectionCommand extends BukkitCommand{

	private static Set<UUID> cooldown = new HashSet<UUID>();

	public TeamSelectionCommand(String name){
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			Player player = (Player) sender;
			if(Main.selectTeamPerm == false || player.hasPermission("paintball.selectteam") || player.isOp()){
				if(Utils.getGameStatus() != GameStatus.INITIALIZING){
					sender.sendMessage(MessageHandler.getMessage("teamSelectNoTime"));
				}else if(args.length != 1){
					sender.sendMessage(MessageHandler.getMessage("teamUseCorrect"));
				}else{
					String team = args[0];
					if(team.equalsIgnoreCase("red")){
						if(Utils.getPlayers().size() / 2 <= Utils.getRed().size()){
							sender.sendMessage(MessageHandler.getMessage("teamSelectRedFull"));
						}else{
							if(cooldown.contains(player.getUniqueId())){
								sender.sendMessage(MessageHandler.getMessage("teamSelectCool"));
								return false;
							}else{
								cooldown.add(player.getUniqueId());
								Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
									@Override
									public void run() {
										cooldown.remove(player.getUniqueId());
									}
								}, 20l);
							}
							if(Utils.getBlue().contains(player.getUniqueId())){
								Utils.getBlue().remove(player.getUniqueId());
							}
							Utils.getRed().add(player.getUniqueId());
							sender.sendMessage(MessageHandler.getMessage("teamSelectJoinedRed"));
							ScoreboardHandler.handleTeamSelection(player);
						}
					}else if(team.equalsIgnoreCase("blue")){
						if(cooldown.contains(player.getUniqueId())){
							sender.sendMessage(MessageHandler.getMessage("teamSelectCool"));
							return false;
						}else{
							cooldown.add(player.getUniqueId());
							Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
								@Override
								public void run() {
									cooldown.remove(player.getUniqueId());
								}
							}, 20l);
						}
						if(Utils.getPlayers().size() / 2 <= Utils.getBlue().size()){
							sender.sendMessage(MessageHandler.getMessage("teamSelectBlueFull"));
						}else{
							if(Utils.getRed().contains(player.getUniqueId())){
								Utils.getRed().remove(player.getUniqueId());
							}
							Utils.getBlue().add(player.getUniqueId());
							sender.sendMessage(MessageHandler.getMessage("teamSelectJoinedBlue"));
							ScoreboardHandler.handleTeamSelection(player);
						}
					}else if(team.equalsIgnoreCase("random")){
						if(cooldown.contains(player.getUniqueId())){
							sender.sendMessage(MessageHandler.getMessage("teamSelectCool"));
							return false;
						}else{
							cooldown.add(player.getUniqueId());
							Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
								@Override
								public void run() {
									cooldown.remove(player.getUniqueId());
								}
							}, 20l);
						}
						if(Utils.getRed().contains(player.getUniqueId()) == false && Utils.getBlue().contains(player.getUniqueId()) == false){
							sender.sendMessage(MessageHandler.getMessage("teamSelectAlreadyRand"));
						}else{
							if(Utils.getBlue().contains(player.getUniqueId())){
								Utils.getBlue().remove(player.getUniqueId());
							}if(Utils.getRed().contains(player.getUniqueId())){
								Utils.getRed().remove(player.getUniqueId());
							}
							sender.sendMessage(MessageHandler.getMessage("teamSelectRand"));
							ScoreboardHandler.handleTeamSelection(player);
						}
					}else{
						sender.sendMessage(MessageHandler.getMessage("teamSelectNoRec"));
					}
				}
			}else{
				sender.sendMessage(MessageHandler.getMessage("noPermission"));
			}
		}
		return false;
	}
	
	public static void handleWoolSelection(Player player){
		if(Utils.getGameStatus() == GameStatus.INITIALIZING == false) return;
		if(player.getInventory().getItemInMainHand().getDurability() == (short) 14){
			if(Main.cmdPrefixes){
				if(Utils.getRed().contains(player.getUniqueId())){
					player.performCommand("pb team random");
				}else{
					player.performCommand("pb team red");
				}
			}else{
				if(Utils.getRed().contains(player.getUniqueId())){
					player.performCommand("team random");
				}else{
					player.performCommand("team red");
				}
			}
		}else{
			if(Main.cmdPrefixes){
				if(Utils.getBlue().contains(player.getUniqueId())){
					player.performCommand("pb team random");
				}else{
					player.performCommand("pb team blue");
				}
			}else{
				if(Utils.getBlue().contains(player.getUniqueId())){
					player.performCommand("team random");
				}else{
					player.performCommand("team blue");
				}
			}
		}	
	}
	public static int getSize(){
		return cooldown.size();
	}
}
