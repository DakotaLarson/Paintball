package me.theredbaron24.paintball.commands;

import java.util.List;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.utils.Chat;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class BestCommand extends BukkitCommand{
	
	public BestCommand(String name){
		super(name);
	}
	private static int page = 0;
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.best") || sender.hasPermission("paintball.general") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if(args.length == 0){
			TaskHandler.runTask(new Runnable() {
				@Override
				public void run() {
					if(MySQL.hasValidConnection() == false){
						MySQL.connectToDatabase();
					}
					if(Main.useClickable && sender instanceof Player){
						Player player = (Player) sender;
						List<Chat> data = MySQL.getBestPageWithChat(1);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(MessageHandler.getMessage("rankedPageTitle", "%low%", "" + 1, "%high%", "" + 10));
								for(Chat s : data){
									s.send(player);
								}
							}
						});
					}else{
						List<String> data = MySQL.getBestPage(1);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(MessageHandler.getMessage("rankedPageTitle", "%low%", "" + 1, "%high%", "" + 10));
								for(String s : data){
									sender.sendMessage(s);
								}
							}
						});
					}
				}
			});
		}else if(args.length == 1){
			page = 0;
			try{
				page = Integer.parseInt(args[0]);
			}catch(NumberFormatException e){
				sender.sendMessage(MessageHandler.getMessage("useNumber"));
				return false;
			}
			page = Math.max(page, 1);
			int number = page -1;
			number = number * 10;
			final int min = number + 1;
			final int max = min + 9;
			TaskHandler.runTask(new Runnable() {
				@Override
				public void run() {
					if(MySQL.hasValidConnection() == false){
						MySQL.connectToDatabase();
					}
					if(Main.useClickable && sender instanceof Player){
						Player player = (Player) sender;
						List<Chat> data = MySQL.getBestPageWithChat(1);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(MessageHandler.getMessage("rankedPageTitle", "%low%", "" + min, "%high%", "" + max));
								for(Chat s : data){
									s.send(player);
								}
							}
						});
					}else{
						List<String> data = MySQL.getBestPage(page);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(MessageHandler.getMessage("rankedPageTitle", "%low%", "" + min, "%high%", "" + max));
								for(String s : data){
									sender.sendMessage(s);
								}
							}
						});
					}
				}
			});
		}else{
			sender.sendMessage(MessageHandler.getMessage("useFewerArgs"));
		}
		return false;
	}
}
