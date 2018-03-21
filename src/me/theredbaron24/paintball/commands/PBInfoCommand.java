package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.main.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class PBInfoCommand extends BukkitCommand{
	
	public PBInfoCommand(String name){
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		sender.sendMessage("" + ChatColor.DARK_GREEN + ChatColor.BOLD + "-------" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Ultimate Paintball" + ChatColor.DARK_GREEN + ChatColor.BOLD + "-------");
		sender.sendMessage(ChatColor.DARK_AQUA + "Author: " + ChatColor.GOLD + "THEREDBARON24");
		sender.sendMessage(ChatColor.DARK_AQUA + "Version: " + ChatColor.GOLD + Main.version);
		sender.sendMessage(ChatColor.DARK_AQUA + "Page: " + ChatColor.GOLD + "http://tinyurl.com/ultimatepb");
		sender.sendMessage("" + ChatColor.DARK_GREEN + ChatColor.BOLD + "----------------------------");
		return false;
	}

}
