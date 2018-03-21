package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.main.Main;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class LeaveallCommand extends BukkitCommand{

public LeaveallCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Main.main.leaveCommand.execute(sender, commandLabel, args);
		return false;
}}
