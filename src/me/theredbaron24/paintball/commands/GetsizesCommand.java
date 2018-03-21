package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.kits.BlasterKit;
import me.theredbaron24.paintball.kits.BlinderKit;
import me.theredbaron24.paintball.kits.BlockerKit;
import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.JuggernautKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.ReloaderKit;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.main.ActionMenuHandler;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.CTFFlagHandler;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.RankHandler;
import me.theredbaron24.paintball.main.ReloadHandler;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class GetsizesCommand extends BukkitCommand{

	public GetsizesCommand(String name) {
		super(name);
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player && (sender.hasPermission("paintball.admin") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			sender.sendMessage("If any of the following values are rather large, please report this.");
			sender.sendMessage("Players: " + Utils.getPlayers().size());
			sender.sendMessage("Playerstoadd: " + Utils.getPlayersToAdd().size());
			sender.sendMessage("Async: " + Utils.getAsyncPlayers().size());
			sender.sendMessage("Blue: " + Utils.getBlue().size());
			sender.sendMessage("Red: " + Utils.getRed().size());
			sender.sendMessage("Sniper: " + SniperKit.getSize());
			sender.sendMessage("Sprayer: " + SprayerKit.getSize());
			sender.sendMessage("Blaster: " + BlasterKit.getSize());
			sender.sendMessage("Blinder: " + BlinderKit.getSize());
			sender.sendMessage("Blocker: " + BlockerKit.getSize());
			sender.sendMessage("Demo: " + Main.mines.size());
			sender.sendMessage("Jugg: " + JuggernautKit.getSize());
			sender.sendMessage("Reloader: " + ReloaderKit.getSize());
			sender.sendMessage("Kit: " + Kit.getKits().size());
			sender.sendMessage("Kit Changes: " + Kit.getChangesSize());
			sender.sendMessage("ConfMap: " + ActionMenuHandler.getSize());
			sender.sendMessage("Ignored: " + DeathMessageHandler.getSize());
			sender.sendMessage("TeamCool: " + TeamSelectionCommand.getSize());
			sender.sendMessage("Red Spawns: " + Arena.getRedSpawns().size());
			sender.sendMessage("Blue Spawns: " + Arena.getBlueSpawns().size());
			sender.sendMessage("Dead Players: " + DeathManager.getPlayersSize());
			sender.sendMessage("Player Times: " + DeathManager.getPlayerTimesSize());
			sender.sendMessage("Hits: " + DeathManager.getHitsSize());
			sender.sendMessage("Respawning: " + DeathManager.getRespawningSize());
			sender.sendMessage("Spawn Protected: " + DeathManager.getProtectedSize());
			sender.sendMessage("Flag FW: " + CTFFlagHandler.getFWTasks().size());
			sender.sendMessage("Ranks: " + RankHandler.getRanksSize());
			sender.sendMessage("User Ranks: " + RankHandler.getUserRanksSize());
			sender.sendMessage("Next Ranks: " + RankHandler.getNextRanksSize());
			sender.sendMessage("Reloaders: " + ReloadHandler.getSize());
			sender.sendMessage("Votable Arenas: " + VoteHandler.getVotableArenas().size());
			sender.sendMessage("User Votes: " + VoteHandler.getUserVotes().size());
			sender.sendMessage("Vote Counts: " + VoteHandler.getVoteCounts().size());
			sender.sendMessage("Bouncer Ball: " + BouncerKit.getBallCount());
			sender.sendMessage("Bouncer Bounces: " + BouncerKit.getBouncesSize());	
		}
		return false;
	}
}
