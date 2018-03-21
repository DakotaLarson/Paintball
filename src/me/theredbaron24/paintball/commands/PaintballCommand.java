package me.theredbaron24.paintball.commands;

import java.util.Arrays;

import me.theredbaron24.paintball.main.RankHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class PaintballCommand extends BukkitCommand{

	public PaintballCommand(String name) {
		super(name);
	}
	private static ArenaCommand arenaCommand = null;
	private static AdminCommand adminCommand = null;
	private static TeamCommand teamCommand = null;
	private static LeaveCommand leaveCommand = null;
	private static JoinCommand joinCommand = null;
	private static StatsCommand statsCommand = null;
	private static VoteCommand voteCommand = null;
	private static DeathMessageHandler deathMessageHandler = null;
	private static BestCommand bestCommand = null;
	private static KitCommand kitCommand = null;
	private static GetsizesCommand getsizesCommand = null;
	private static RankHandler rankHandler = null;
	private static PBInfoCommand pbInfoCommand = null;
	private static TeamSelectionCommand teamSelectionCommand = null;
	private static LeaveallCommand leaveallCommand = null;
	private static ControlPointCommand cpCommand = null;
	

	public static void init(ArenaCommand arenaCommand, AdminCommand adminCommand, TeamCommand teamCommand, LeaveCommand leaveCommand, JoinCommand joinCommand, StatsCommand statsCommand,
			VoteCommand voteCommand, DeathMessageHandler deathMessageHandler, BestCommand bestCommand, KitCommand kitCommand, GetsizesCommand getsizesCommand, RankHandler rankHandler,
			PBInfoCommand pbInfoCommand, TeamSelectionCommand teamSelectionCommand, LeaveallCommand leaveallCommand, ControlPointCommand cpCommand) {
		PaintballCommand.arenaCommand = arenaCommand;
		PaintballCommand.adminCommand = adminCommand;
		PaintballCommand.teamCommand = teamCommand;
		PaintballCommand.leaveCommand = leaveCommand;
		PaintballCommand.joinCommand = joinCommand;
		PaintballCommand.statsCommand = statsCommand;
		PaintballCommand.voteCommand = voteCommand;
		PaintballCommand.deathMessageHandler = deathMessageHandler;
		PaintballCommand.bestCommand = bestCommand;
		PaintballCommand.kitCommand = kitCommand;
		PaintballCommand.getsizesCommand = getsizesCommand;
		PaintballCommand.rankHandler = rankHandler;
		PaintballCommand.pbInfoCommand = pbInfoCommand;
		PaintballCommand.teamSelectionCommand = teamSelectionCommand;
		PaintballCommand.leaveallCommand = leaveallCommand;
		PaintballCommand.cpCommand = cpCommand;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(args.length == 0){
			sender.sendMessage("Messages will be added soon.");
		}else{
			String[] finalArray = {};
			if(args.length >= 2){
				finalArray = Arrays.copyOfRange(args, 1, args.length);
			}
			switch(args[0].toLowerCase()){
				case "arena":
					arenaCommand.execute(sender, commandLabel, finalArray);
					break;
				case "admin":
					adminCommand.execute(sender, commandLabel, finalArray);
					break;
				case "team":
					teamSelectionCommand.execute(sender, commandLabel, finalArray);
					break;
				case "leave":
					leaveCommand.execute(sender, commandLabel, finalArray);
					break;
				case "leaveall":
					leaveallCommand.execute(sender, commandLabel, finalArray);
					break;
				case "join":
					joinCommand.execute(sender, commandLabel, finalArray);
					break;
				case "stats":
					statsCommand.execute(sender, commandLabel, finalArray);
					break;
				case "vote":
					voteCommand.execute(sender, commandLabel, finalArray);
					break;
				case "toggle":
					deathMessageHandler.execute(sender, commandLabel, finalArray);
					break;
				case "best":
					bestCommand.execute(sender, commandLabel, finalArray);
					break;
				case "kit":
					kitCommand.execute(sender, commandLabel, finalArray);
					break;
				case "getsizes":
					getsizesCommand.execute(sender, commandLabel, finalArray);
					break;
				case "rank":
					rankHandler.execute(sender, commandLabel, finalArray);
					break;
				case "t":
					teamCommand.execute(sender, commandLabel, finalArray);
					break;
				case "pbinfo":
					pbInfoCommand.execute(sender, commandLabel, finalArray);
					break;
				case "cp":
					cpCommand.execute(sender, commandLabel, finalArray);
				default:
					sender.sendMessage("Your command was not recognized!");
					Bukkit.getConsoleSender().sendMessage(commandLabel + " was not recognized!");
					break;
			}
		}
		return false;
	}
}
