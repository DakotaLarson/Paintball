package me.theredbaron24.paintball.main;

import java.util.UUID;

import me.theredbaron24.paintball.kits.HealerKit;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class ScoreboardHandler {
	
	private static Scoreboard teamBoard = null;
	
	public static void handleMatchStart(){
		for(UUID id : Utils.getPlayers()){
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective stats = board.registerNewObjective("stats", "dummy");
			stats.setDisplaySlot(DisplaySlot.SIDEBAR);
			stats.getScore(MessageHandler.getMessage("playerShots")).setScore(0);
			stats.getScore(MessageHandler.getMessage("redTeamPoints")).setScore(0);
			stats.getScore(MessageHandler.getMessage("blueTeamPoints")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerKills")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerDeaths")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerHits")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerPoints")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerKS")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerCurr")).setScore(0);
			if(Arena.getGameMode() == GameMode.CTF){
				stats.setDisplayName(createTitleString(Main.ctfTime));
				stats.getScore(MessageHandler.getMessage("redTeamCaps")).setScore(0);
				stats.getScore(MessageHandler.getMessage("blueTeamCaps")).setScore(0);
				stats.getScore(MessageHandler.getMessage("playerCaps")).setScore(0);
				
			}else{
				stats.setDisplayName(createTitleString(Main.tdmTime));
				stats.getScore(MessageHandler.getMessage("redTeamKills")).setScore(0);
				stats.getScore(MessageHandler.getMessage("blueTeamKills")).setScore(0);
			}
			Objective health = board.registerNewObjective("health", "dummy");
			health.setDisplaySlot(DisplaySlot.BELOW_NAME);
			health.setDisplayName("/" + DeathManager.playerHits);
			Objective points = board.registerNewObjective("points", "dummy");
			points.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			Team red = board.registerNewTeam("red");
			red.setPrefix(Main.redTeamColor);
			Team blue = board.registerNewTeam("blue");
			blue.setPrefix(Main.blueTeamColor);
			Team dead = board.registerNewTeam("dead");
			dead.setPrefix(Main.deadTeamColor);
			dead.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			if(Main.hideNametagsFromOpponents){
				if(Main.hideNametagsFromTeam){
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
				}else{
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
				}
			}else{
				if(Main.hideNametagsFromTeam){
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
				}else{
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				}
			}
			for(UUID uuid: Utils.getPlayers()){
				String name = Bukkit.getPlayer(uuid).getName();
				health.getScore(name).setScore(DeathManager.playerHits);
				points.getScore(name).setScore(0);
				if(Utils.getRed().contains(uuid)){
					red.addEntry(name);
				}else{
					blue.addEntry(name);
				}
			}
			Bukkit.getPlayer(id).setScoreboard(board);
		}
	}
	public static void handleShot(Player player){
		Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerShots"));
		score.setScore(score.getScore() + 1);
		if(Utils.getRed().contains(player.getUniqueId())){
			Match.setRedShots(Match.getRedShots() + 1);
		}else{
			Match.setBlueShots(Match.getBlueShots() + 1);
		}
	}
	public static void handleHit(Player killer, Player hit){
		boolean contains = Utils.getRed().contains(killer.getUniqueId());
		if(contains){
			Match.setRedHits(Match.getRedHits() + 1);
		}else{
			Match.setBlueHits(Match.getBlueHits() + 1);
		}
		setPoints(contains, Main.hitPoints);
		Scoreboard board = killer.getScoreboard();
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(MessageHandler.getMessage("playerHits"));
		score.setScore(score.getScore() + 1);
		score = obj.getScore(MessageHandler.getMessage("playerPoints"));
		score.setScore(score.getScore() + Main.hitPoints);
		addToCurrency(killer, Main.hitCurr);
		adjustHealth(hit);
		adjustSidebar(contains, true, Main.hitPoints);
		adjustPlayerListPts(killer, Main.hitPoints);
	}
	/**
	 * 
	 * @param status 0 = grab, 1 = drop, 2 = cap, 3 = return, 4 = pick up
	 * @param player the player involved
	 */
	public static void handleFlag(int status, Player player){
		if(status == 0){
			addToCurrency(player, Main.flagGrabCurr);
			if(Main.flagGrabPoints == 0) return;
			if(Utils.getRed().contains(player.getUniqueId())){
				setPoints(true, Main.flagGrabPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagGrabPoints);
				adjustPlayerListPts(player, Main.flagGrabPoints);
				adjustSidebar(true, true, Main.flagGrabPoints);
			}else{
				setPoints(false, Main.flagGrabPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagGrabPoints);
				adjustPlayerListPts(player, Main.flagGrabPoints);
				adjustSidebar(false, true, Main.flagGrabPoints);
			}
		}else if(status == 1){
			addToCurrency(player, Main.flagDropCurr);
			if(Main.flagDropPoints == 0) return;
			if(Utils.getRed().contains(player.getUniqueId())){
				setPoints(true, Main.flagDropPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagDropPoints);
				adjustPlayerListPts(player, Main.flagDropPoints);
				adjustSidebar(true, true, Main.flagDropPoints);
			}else{
				setPoints(false, Main.flagDropPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagDropPoints);
				adjustPlayerListPts(player, Main.flagDropPoints);
				adjustSidebar(false, true, Main.flagDropPoints);
			}
		}else if(status == 2){
			addToCurrency(player, Main.flagCapCurr);
			if(Main.flagCapPoints == 0){
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerCaps"));
				score.setScore(score.getScore() + 1);
				if(Utils.getRed().contains(player.getUniqueId())){
					Match.setRedCaptures(Match.getRedCaptures() + 1);
					adjustSidebar(true, false, 1);
				}else{
					Match.setBlueCaptures(Match.getBlueCaptures() + 1);
					adjustSidebar(false, false, 1);
				}
				return;
			}
			if(Utils.getRed().contains(player.getUniqueId())){
				setPoints(true, Main.flagCapPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagCapPoints);
				score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerCaps"));
				score.setScore(score.getScore() + 1);
				adjustPlayerListPts(player, Main.flagCapPoints);
				adjustSidebar(true, true, Main.flagCapPoints);
				adjustSidebar(true, false, 1);
				Match.setRedCaptures(Match.getRedCaptures() + 1);
			}else{
				setPoints(false, Main.flagCapPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagCapPoints);
				score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerCaps"));
				score.setScore(score.getScore() + 1);
				adjustPlayerListPts(player, Main.flagCapPoints);
				adjustSidebar(false, true, Main.flagCapPoints);
				adjustSidebar(false, false, 1);
				Match.setBlueCaptures(Match.getBlueCaptures() + 1);
			}
		}else if(status == 3){
			addToCurrency(player, Main.flagRetCurr);
			if(Main.flagRetPoints == 0) return;
			if(Utils.getRed().contains(player.getUniqueId())){
				setPoints(true, Main.flagRetPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagRetPoints);
				adjustPlayerListPts(player, Main.flagRetPoints);
				adjustSidebar(true, true, Main.flagRetPoints);
			}else{
				setPoints(false, Main.flagRetPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagRetPoints);
				adjustPlayerListPts(player, Main.flagRetPoints);
				adjustSidebar(false, true, Main.flagRetPoints);
			}
		}else if(status == 4){
			addToCurrency(player, Main.flagPickCurr);
			if(Main.flagPickPoints == 0) return;
			if(Utils.getRed().contains(player.getUniqueId())){
				setPoints(true, Main.flagPickPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagPickPoints);
				adjustPlayerListPts(player, Main.flagPickPoints);
				adjustSidebar(true, true, Main.flagPickPoints);
			}else{
				setPoints(false, Main.flagPickPoints);
				Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + Main.flagPickPoints);
				adjustPlayerListPts(player, Main.flagPickPoints);
				adjustSidebar(false, true, Main.flagPickPoints);
			}
		}
	}
	public static void handleMatchInitialization(){
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective stats = board.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName(MessageHandler.getMessage("initBoardTitle"));
		stats.getScore(MessageHandler.getMessage("initBoardRed")).setScore(Utils.getRed().size());
		stats.getScore(MessageHandler.getMessage("initBoardBlue")).setScore(Utils.getBlue().size());
		stats.getScore(MessageHandler.getMessage("initBoardRand")).setScore(Utils.getPlayers().size() - (Utils.getRed().size() + Utils.getBlue().size()));
		teamBoard = board;
		for(UUID id : Utils.getPlayers()){
			Bukkit.getPlayer(id).setScoreboard(board);
		}
	}
	public static void handleTeamSelection(Player player){
		if(teamBoard == null) return;
		Objective obj = teamBoard.getObjective(DisplaySlot.SIDEBAR);
		obj.getScore(MessageHandler.getMessage("initBoardBlue")).setScore(Utils.getBlue().size());
		obj.getScore(MessageHandler.getMessage("initBoardRed")).setScore(Utils.getRed().size());
		int size = Utils.getPlayers().size() - (Utils.getRed().size() + Utils.getBlue().size());
		obj.getScore(MessageHandler.getMessage("initBoardRand")).setScore(size);
	}
	public static void handleJoin(Player player){
		if(Utils.getGameStatus() == GameStatus.RUNNING){
			DeathManager.addEntry(player.getUniqueId());
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective stats = board.registerNewObjective("stats", "dummy");
			stats.setDisplaySlot(DisplaySlot.SIDEBAR);
			stats.getScore(MessageHandler.getMessage("playerShots")).setScore(0);
			stats.getScore(MessageHandler.getMessage("redTeamPoints")).setScore(Match.getRedPoints());
			stats.getScore(MessageHandler.getMessage("blueTeamPoints")).setScore(Match.getBluePoints());
			stats.getScore(MessageHandler.getMessage("playerKills")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerDeaths")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerHits")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerPoints")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerKS")).setScore(0);
			stats.getScore(MessageHandler.getMessage("playerCurr")).setScore(0);
			if(Arena.getGameMode() == GameMode.CTF){
				stats.setDisplayName(createTitleString(Main.ctfTime));
				stats.getScore(MessageHandler.getMessage("redTeamCaps")).setScore(Match.getRedCaptures());
				stats.getScore(MessageHandler.getMessage("blueTeamCaps")).setScore(Match.getRedCaptures());
				stats.getScore(MessageHandler.getMessage("playerCaps")).setScore(0);
				
			}else{
				stats.setDisplayName(createTitleString(Main.tdmTime));
				stats.getScore(MessageHandler.getMessage("redTeamKills")).setScore(Match.getRedKills());
				stats.getScore(MessageHandler.getMessage("blueTeamKills")).setScore(Match.getBlueKills());
			}
			Objective health = board.registerNewObjective("health", "dummy");
			health.setDisplaySlot(DisplaySlot.BELOW_NAME);
			health.setDisplayName("/" + DeathManager.playerHits);
			Objective points = board.registerNewObjective("points", "dummy");
			points.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			Team red = board.registerNewTeam("red");
			red.setPrefix(Main.redTeamColor);
			Team blue = board.registerNewTeam("blue");
			blue.setPrefix(Main.blueTeamColor);
			Team dead = board.registerNewTeam("dead");
			dead.setPrefix(Main.deadTeamColor);
			dead.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			if(Main.hideNametagsFromOpponents){
				if(Main.hideNametagsFromTeam){
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
				}else{
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
				}
			}else{
				if(Main.hideNametagsFromTeam){
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
				}else{
					red.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
					blue.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				}
			}
			player.setScoreboard(board);
			for(UUID uuid: Utils.getPlayers()){
				Player p = Bukkit.getPlayer(uuid);
				String name = p.getName();
				health.getScore(name).setScore(DeathManager.getHits(Bukkit.getPlayer(uuid)));
				p.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(player.getName()).setScore(DeathManager.getHits(player));
				int pointCount = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints")).getScore();
				points.getScore(name).setScore(pointCount);
				p.getScoreboard().getObjective(DisplaySlot.PLAYER_LIST).getScore(player.getName()).setScore(0);
				if(DeathManager.isPlayerRespawning(p)){
					dead.addEntry(name);
				}else{
					if(Utils.getRed().contains(uuid)){
						red.addEntry(name);
					}else{
						blue.addEntry(name);
					}
				}
				if(Utils.getRed().contains(player.getUniqueId())){
					p.getScoreboard().getTeam("red").addEntry(player.getName());
				}else{
					p.getScoreboard().getTeam("blue").addEntry(player.getName());
				}
			}
		}else if(Utils.getGameStatus() == GameStatus.INITIALIZING){
			if(teamBoard != null){
				player.setScoreboard(teamBoard);
			}
		}
	}
	public static void handleTimeChange(int seconds){
		String title = createTitleString(seconds);
		if(title == null) return;
		for(UUID id : Utils.getPlayers()){
			Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
		}
	}
	public static void handleElm(Player player){
		if(Utils.getRed().contains(player.getUniqueId())){
			for(UUID id : Utils.getPlayers()){
				Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
				Team team = board.getTeam("red");
				team.removeEntry(player.getName());
				team = board.getTeam("dead");
				team.addEntry(player.getName());
			}
		}else{
			for(UUID id : Utils.getPlayers()){
				Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
				Team team = board.getTeam("blue");
				team.removeEntry(player.getName());
				team = board.getTeam("dead");
				team.addEntry(player.getName());
			}
		}
	}
	public static void handleKill(Player player, Player killer, boolean isFullPoints, boolean isVoid){
		if(isVoid){
			if(DeathManager.usingBox()){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getPlayers()){
						Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
						Team team = board.getTeam("red");
						team.removeEntry(player.getName());
						team = board.getTeam("dead");
						team.addEntry(player.getName());
					}
				}else{
					for(UUID id : Utils.getPlayers()){
						Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
						Team team = board.getTeam("blue");
						team.removeEntry(player.getName());
						team = board.getTeam("dead");
						team.addEntry(player.getName());
					}
				}
			}
			Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			Score score = obj.getScore(MessageHandler.getMessage("playerDeaths"));
			score.setScore(score.getScore() + 1);
		}else{
			int totalAdj = Main.killPoints;
			if(isFullPoints){
				totalAdj += (DeathManager.playerHits * Main.hitPoints);
			}
			int currAdj = Main.killCurr;
			if(isFullPoints){
				currAdj += (DeathManager.playerHits * Main.hitCurr);
			}
			addToCurrency(killer, currAdj);
			if(DeathManager.usingBox()){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getPlayers()){
						Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
						Team team = board.getTeam("red");
						team.removeEntry(player.getName());
						team = board.getTeam("dead");
						team.addEntry(player.getName());
					}
				}else{
					for(UUID id : Utils.getPlayers()){
						Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
						Team team = board.getTeam("blue");
						team.removeEntry(player.getName());
						team = board.getTeam("dead");
						team.addEntry(player.getName());
					}
				}
			}
			boolean isRed = Utils.getRed().contains(killer.getUniqueId());
			setPoints(isRed, totalAdj);
			if(isRed){
				Match.setRedKills(Match.getRedKills() + 1);
			}else{
				Match.setBlueKills(Match.getBlueKills() + 1);
			}
			adjustSidebar(isRed, true, totalAdj);
			if(Arena.getGameMode() == GameMode.TDM){
				adjustSidebar(Utils.getRed().contains(killer.getUniqueId()), false, 1);	
			}
			Objective obj = killer.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			Score score = obj.getScore(MessageHandler.getMessage("playerKills"));
			score.setScore(score.getScore() + 1); 
			score = obj.getScore(MessageHandler.getMessage("playerPoints"));
			score.setScore(score.getScore() + totalAdj);
			score = obj.getScore(MessageHandler.getMessage("playerKS"));
			int totalKills = score.getScore() + 1;
			score.setScore(totalKills);
			killer.setLevel(totalKills);
			adjustPlayerListPts(killer, totalAdj);
			obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			score = obj.getScore(MessageHandler.getMessage("playerDeaths"));
			score.setScore(score.getScore() + 1);
			if(isFullPoints){
				adjustHealth(player);
			}
		}
	}
	public static void handleRespawn(Player player){
		adjustHealth(player);
		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerKS")).setScore(0);
		if(DeathManager.usingBox()){
			if(Utils.getRed().contains(player.getUniqueId())){
				for(UUID id : Utils.getPlayers()){
					Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
					Team team = board.getTeam("dead");
					team.removeEntry(player.getName());
					team = board.getTeam("red");
					team.addEntry(player.getName());
				}
			}else{
				for(UUID id : Utils.getPlayers()){
					Scoreboard board = Bukkit.getPlayer(id).getScoreboard();
					Team team = board.getTeam("dead");
					team.removeEntry(player.getName());
					team = board.getTeam("blue");
					team.addEntry(player.getName());
				}
			}
		}
	}
	public static void handleCPTeam(boolean team, int points){
		setPoints(team, points);
		
	}
	public static void handleCPPlayer(Player player, int points, int currency){
		addToCurrency(player, currency);
		Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
		score.setScore(score.getScore() + points);
		adjustPlayerListPts(player, points);
		adjustSidebar(Utils.getRed().contains(player.getUniqueId()), true, points);
	}
	public static Scoreboard createVotingBoard(){
		if(Main.enableVoting == false) return null;
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective votes = board.registerNewObjective("votes", "dummy");
		votes.setDisplaySlot(DisplaySlot.SIDEBAR);
		votes.setDisplayName(MessageHandler.getMessage("votingBoardTitle"));
		for(String arena : VoteHandler.getVotableArenas()){
			int index = VoteHandler.getVotableArenas().indexOf(arena) + 1;
			int voteCount = VoteHandler.getVoteCounts().get(arena);
			String title = MessageHandler.getMessage("votingTitle", "%index%", "" + index, "%arena%", arena, "%mode%", VoteHandler.getGamemode(arena));
			if(title.length() > 40){
				title = title.substring(0, 40);
			}
			votes.getScore(title).setScore(voteCount);
		}
		Objective userVotes = board.registerNewObjective("userVotes", "dummy");
		userVotes.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for(UUID id : VoteHandler.getUserVotes().keySet()){
			Player p = Bukkit.getPlayer(id);
			if(p == null) continue;
			String name = p.getName();
			userVotes.getScore(name).setScore(VoteHandler.getVotableArenas().indexOf(VoteHandler.getUserVotes().get(id)) + 1);
		}
		return board;
	}
	public static void updateVotes(){
		Scoreboard board = VoteHandler.getVotingBoard();
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		for(String arena : VoteHandler.getVotableArenas()){
			int index = VoteHandler.getVotableArenas().indexOf(arena) + 1;
			int voteCount = VoteHandler.getVoteCounts().get(arena);
			String title = MessageHandler.getMessage("votingTitle", "%index%", "" + index, "%arena%", arena, "%mode%", VoteHandler.getGamemode(arena));
			if(title.length() > 40){
				title = title.substring(0, 40);
			}
			obj.getScore(title).setScore(voteCount);
		}
		Objective userVotes = board.getObjective(DisplaySlot.PLAYER_LIST);
		userVotes.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for(UUID id : VoteHandler.getUserVotes().keySet()){
			Player p = Bukkit.getPlayer(id);
			if(p == null) continue;
			String name = p.getName();
			userVotes.getScore(name).setScore(VoteHandler.getVotableArenas().indexOf(VoteHandler.getUserVotes().get(id)) + 1);
		}
	}
	private static String createTitleString(int time){
		int minutes = time / 60;
		int seconds = time - 60* minutes;
		String arenaTitle = Arena.getTitle();
		if(arenaTitle.length() > 11){
			arenaTitle = arenaTitle.substring(0, 11);
		}
		String finalString = null;
		String minuteString = null;
		String secondString = null;
		if(minutes > 9){
			minuteString = "" + minutes;
		}else{
			minuteString = "0" + minutes;
		}if(seconds > 9){
			secondString = "" + seconds;
		}else{
			secondString = "0" + seconds;
		}
		String timeString = null;
		if(time > 60){
			timeString = MessageHandler.getMessage("highTime", "%mins%", minuteString, "%secs%", secondString);
		}else if(time > 15){
			timeString = MessageHandler.getMessage("medTime", "%mins%", minuteString, "%secs%", secondString);
		}else{
			if(time == 0){
				timeString = "";
				finalString = MessageHandler.getMessage("scoreboardMatchComplete");
			}else{
				if(time % 2 == 0){
					timeString = MessageHandler.getMessage("lowTime1", "%mins%", minuteString, "%secs%", secondString);
				}else{
					timeString = MessageHandler.getMessage("lowTime2", "%mins%", minuteString, "%secs%", secondString);
				}
			}
		}
		if(finalString == null){
			finalString = MessageHandler.getMessage("mainBoardTitle", "%arena%", arenaTitle, "%mode%", Arena.getGameMode().name(), "%time%", timeString);
		}
		if(finalString.length() > 32){
			System.err.println("Title string too long for scoreboard. Please report.");
			return finalString.substring(0, 32);
		}
		return finalString;
	}
	public static void handleHeal(Player player, Player healer){
		if(player == null){
			adjustHealth(healer);
		}else{
			adjustHealth(player);
			if(HealerKit.getHealPoints() == 0){
				return;
			}else{
				adjustPlayerListPts(healer, HealerKit.getHealPoints());
				adjustSidebar(Utils.getRed().contains(healer.getUniqueId()), true, HealerKit.getHealPoints());
				Score score = healer.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerPoints"));
				score.setScore(score.getScore() + HealerKit.getHealPoints());
				addToCurrency(healer, HealerKit.getHealCurr());
			}
		}
	}
	private static void adjustPlayerListPts(Player player, int points){
		for(UUID id : Utils.getPlayers()){
			Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.PLAYER_LIST).getScore(player.getName());
			score.setScore(score.getScore() + points);
		}
	}
	private static void adjustHealth(Player player){
		for(UUID id : Utils.getPlayers()){
			Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(player.getName());
			score.setScore(DeathManager.getHits(player));
		}
	}
	private static void adjustSidebar(boolean team, boolean status, int points){
		if(team){
			if(status){
				for(UUID id : Utils.getPlayers()){
					Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("redTeamPoints"));
					score.setScore(score.getScore() + points);
				}
			}else{
				if(Arena.getGameMode() == GameMode.CTF){
					for(UUID id : Utils.getPlayers()){
						Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("redTeamCaps"));
						score.setScore(score.getScore() + points);
					}
				}else{
					for(UUID id : Utils.getPlayers()){
						Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("redTeamKills"));
						score.setScore(score.getScore() + points);
					}
				}
			}
		}else{
			if(status){
				for(UUID id : Utils.getPlayers()){
					Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("blueTeamPoints"));
					score.setScore(score.getScore() + points);
				}
			}else{
				if(Arena.getGameMode() == GameMode.CTF){
					for(UUID id : Utils.getPlayers()){
						Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("blueTeamCaps"));
						score.setScore(score.getScore() + points);
					}
				}else{
					for(UUID id : Utils.getPlayers()){
						Score score = Bukkit.getPlayer(id).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("blueTeamKills"));
						score.setScore(score.getScore() + points);
					}
				}
			}
		}
	}
	private static void setPoints(boolean teamIsRed, int points){
		if(teamIsRed){
			Match.setRedPoints(Match.getRedPoints() + points);
		}else{
			Match.setBluePoints(Match.getBluePoints() + points);
		}
	}
	private static void addToCurrency(Player player, int currency){
		if(currency == 0) return;
		Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(MessageHandler.getMessage("playerCurr"));
		score.setScore(score.getScore() + currency);
	}
}
