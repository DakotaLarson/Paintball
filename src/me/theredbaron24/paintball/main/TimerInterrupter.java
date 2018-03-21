package me.theredbaron24.paintball.main;

import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TimerInterrupter {

	private static boolean stopped = false;
	
	public static void stop(boolean isImmediate, CommandSender sender, Main main){
		if(stopped){
			sender.sendMessage(MessageHandler.getMessage("alreadyHalted"));
		}else{
			stopped = true;
			if(Utils.getGameStatus() == GameStatus.NOT_STARTED){
				Bukkit.broadcastMessage(MessageHandler.getMessage("gameHalted"));
			}else if(Utils.getGameStatus() == GameStatus.FINALIZING){
				if(isImmediate){
					if(Match.endFinalization(main)){
						sender.sendMessage(MessageHandler.getMessage("finalizationHalted"));
					}else{
						sender.sendMessage(MessageHandler.getMessage("finalHaltDelay"));
					}
				}else{
					sender.sendMessage(MessageHandler.getMessage("noMoreStart"));
				}
			}else{
				if(isImmediate){
					if(Utils.getGameStatus() == GameStatus.INITIALIZING){
						GameTimer.forceEndTimer(1, main, false);
					}else if(Utils.getGameStatus() == GameStatus.COUNTING_DOWN){
						GameTimer.forceEndTimer(0, main, false);
					}else if(Utils.getGameStatus() == GameStatus.RUNNING){
						GameTimer.forceEndTimer(2, main, false);
					}			
				}else{
					if(Utils.getGameStatus() == GameStatus.INITIALIZING){
						GameTimer.forceEndTimer(1, main, false);
					}else if(Utils.getGameStatus() == GameStatus.COUNTING_DOWN){
						GameTimer.forceEndTimer(0, main, false);
					}else if(Utils.getGameStatus() == GameStatus.RUNNING){
						GameTimer.forceEndTimer(2, main, false);
					}
					Bukkit.broadcastMessage(MessageHandler.getMessage("matchStartsOnEnable"));
				}
			}
		}
	}
	public static void start(String arena, boolean isImmediate, CommandSender sender, Main main){
		if(stopped){
			stopped = false;
			Bukkit.broadcastMessage(MessageHandler.getMessage("resumeMatches"));
			if(arena != null){
				if(Configuration.getArenaConfig().get("arenas." + arena) != null){
					if(Configuration.getArenaConfig().getBoolean("arenas." + arena + ".enabled")){
						Main.nextArena = arena;
						sender.sendMessage(MessageHandler.getMessage("arenaSetNext", "%arena%", arena));
					}else{
						sender.sendMessage(MessageHandler.getMessage("arenaNotSetNext"));
					}
				}else{
					sender.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
				}
			}
			if(Utils.getPlayers().size() >= Main.minPlayers){
				GameTimer.startCountdown(main);
			}
		}else{
			sender.sendMessage(MessageHandler.getMessage("matchesAleadyRunning"));
		}
	}
	
	public static boolean isStopped(){
		return stopped;
	}
}
