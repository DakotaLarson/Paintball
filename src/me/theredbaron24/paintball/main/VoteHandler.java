package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.utils.Chat;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class VoteHandler {

	private static Map<String, Integer> voteCounts = new HashMap<String, Integer>();
	private static Map<UUID, String> userVotes = new HashMap<UUID, String>();
	private static List<String> votableArenas = new ArrayList<String>();
	
	private static Inventory inv = Bukkit.createInventory(null, 9, MessageHandler.getMessage("votingInvTitle"));
	
	private static Scoreboard board = null;
	
	private static Random random = new Random();
	
	private static boolean momentarilyDisabled = false;
	
	public static boolean createVotingList(String lastArena){
		momentarilyDisabled = false;
		voteCounts.clear();
		userVotes.clear();
		votableArenas.clear();
		inv.clear();
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas");
		if(section == null) return false;
		Set<String> allArenas = section.getKeys(false);
		List<String> arenas = new ArrayList<String>();
		for(String s : allArenas){
			if(Configuration.getArenaConfig().getBoolean("arenas." + s + ".enabled")){
				if(lastArena == null || lastArena.equals(s) == false){
					arenas.add(s);
				}
			}
		}
		int amountNeeded = Main.votingArenaCount;
		if(lastArena == null){
			amountNeeded ++;
		}
		if(arenas.size() < amountNeeded){
			return false;
		}else{
			if(lastArena == null){
				amountNeeded--;
			}
			ItemStack item = new ItemStack(Material.WOOL);
			ItemMeta meta = item.getItemMeta();
			for(int i = 1; i <= amountNeeded; i ++){
				String arena = arenas.get(random.nextInt(arenas.size()));
				voteCounts.put(arena, 0);
				votableArenas.add(arena);
				if(Main.useVotingBlock){
					meta.setDisplayName(MessageHandler.getMessage("blockTitle", "%index%", "" + votableArenas.size(), "%arena%", arena));
					List<String> lore = new ArrayList<String>();
					lore.add(MessageHandler.getMessage("blockLore", "%mode%", getGamemode(arena)));
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
				arenas.remove(arena);
			}
			if(Main.votingRandomArena){
				voteCounts.put("Random", 0);
				votableArenas.add("Random");
				if(Main.useVotingBlock){
					meta.setDisplayName(MessageHandler.getMessage("blockTitle", "%index%", "" + votableArenas.size(), "%arena%", MessageHandler.getMessage("randomTitle")));
					List<String> lore = new ArrayList<String>();
					lore.add(MessageHandler.getMessage("blockLore", "%mode%", "???"));
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
			}	
			board = ScoreboardHandler.createVotingBoard();
			return true;
		}
	}
	public static String getNextArena(){
		momentarilyDisabled = true;
		if(voteCounts.isEmpty()) return null;
		int max = 0;
		String next = null;
		for(String arena : voteCounts.keySet()){
			int votes = voteCounts.get(arena);
			if(votes > max){
				next = arena;
				max = votes;
			}else if(votes == max && max != 0){
				if(random.nextBoolean()){
					next = arena;
				}
			}
		}
		if(next != null && next.equalsIgnoreCase("Random")){
			Set<String> allArenas = Configuration.getArenaConfig().getConfigurationSection("arenas").getKeys(false);
			if(allArenas == null) return null;
			List<String> arenas = new ArrayList<String>();
			for(String s : allArenas){
				if(Configuration.getArenaConfig().getBoolean("arenas." + s + ".enabled")){
					if(votableArenas.contains(s) == false){
						arenas.add(s);
					}
				}
			}
			return arenas.get(random.nextInt(arenas.size()));
		}
		return next;
	}
	public static void handleLeave(Player player){
		if(board != null){
			if(board.getObjective(DisplaySlot.PLAYER_LIST).getScore(player.getName()).getScore() != 0){
				board.getObjective(DisplaySlot.PLAYER_LIST).getScore(player.getName()).setScore(0);
			}
		}
		UUID id = player.getUniqueId();
		if(userVotes.containsKey(id)){
			String arena = userVotes.get(id);
			userVotes.remove(id);
			voteCounts.put(arena, voteCounts.get(arena) -1);
			ScoreboardHandler.updateVotes();
		}
	}
	public static void vote(Player player, String arg){
		if(votableArenas.size() == 0){
			player.sendMessage(MessageHandler.getMessage("cannotVote"));
			return;
		}
		String arena = null;
		if(Utils.isDigit(arg) && inRange(Integer.parseInt(arg))){
			arena = votableArenas.get(Integer.parseInt(arg) -1);
		}
		if(arena == null){
			arena = arg;
		}
		if(votableArenas.contains(arena)){
			if(userVotes.containsKey(player.getUniqueId())){
				String oldArena = userVotes.get(player.getUniqueId());
				voteCounts.put(oldArena, voteCounts.get(oldArena) -1);
			}
			userVotes.put(player.getUniqueId(), arena);
			voteCounts.put(arena, voteCounts.get(arena) + 1);
			player.sendMessage(MessageHandler.getMessage("successfulVote", "%arena%", arena));
			ScoreboardHandler.updateVotes();
		}else{
			player.sendMessage(MessageHandler.getMessage("notValidArena", "%arena%", arena));
		}
	}
	public static boolean isTempDisabled(){
		return momentarilyDisabled;
	}
	public static void setTempDisabled(boolean disabled){
		momentarilyDisabled = disabled;
	}
	public static void sendVoteMessages(Player player){
		if(momentarilyDisabled){
			player.sendMessage(MessageHandler.getMessage("cannotVote"));
		}else{
			player.sendMessage(MessageHandler.getMessage("voteForTheFollowing"));
			for(String arena : votableArenas){
				int index = votableArenas.indexOf(arena) + 1;
				if(Main.useClickable){
					if(Main.cmdPrefixes){
						new Chat(MessageHandler.getMessage("voteText", "%index%", "" + index, "%arena%", arena, "%mode%", getGamemode(arena)))
						.hover(MessageHandler.getMessage("voteHoverText", "%arena%", arena)).command("/pb vote " + index).send(player);
					}else{
						new Chat(MessageHandler.getMessage("voteText", "%index%", "" + index, "%arena%", arena, "%mode%", getGamemode(arena)))
						.hover(MessageHandler.getMessage("voteHoverText", "%arena%", arena)).command("/vote " + index).send(player);
					}
				}else{
					player.sendMessage(MessageHandler.getMessage("voteText", "%index%", "" + index, "%arena%", arena, "%mode%", getGamemode(arena)));
				}
			}
			player.sendMessage(MessageHandler.getMessage("useVoteCmd"));
		}
	}
	private static boolean inRange(int i){
		if(i < 1) return false;
		int amountNeeded = Main.votingArenaCount;
		if(Main.votingRandomArena == true){
			amountNeeded ++;
		}
		if(i > amountNeeded) return false;
		return true;
	}
	public static Map<String, Integer> getVoteCounts(){
		return voteCounts;
	}
	public static Map<UUID, String> getUserVotes(){
		return userVotes;
	}
	public static List<String> getVotableArenas(){
		return votableArenas;
	}
	public static Scoreboard getVotingBoard(){
		return board;
	}
	public static void setVotingBoard(Player player){
		if(board != null){
			player.setScoreboard(board);
		}else{
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
	public static void openVotingInv(Player player){
		if(Main.useVotingBlock){
			player.openInventory(inv);
		}
	}
	public static void handleVotingSelection(Player player, int slot){
		if(Main.useVotingBlock == false) return;
		slot ++;
		if(Main.cmdPrefixes){
			player.performCommand("pb vote " + slot);

		}else{
			player.performCommand("vote " + slot);
		}
	}
	public static ItemStack getVotingBlock(){
		ItemStack item = new ItemStack(Material.WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("mainBlockTitle"));
		item.setItemMeta(meta);
		return item;
	}
	public static String getGamemode(String arena){
		return Configuration.getArenaConfig().getString("arenas." + arena + ".gamemode", "???");
	}
}
