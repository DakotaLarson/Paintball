package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RankHandler extends BukkitCommand implements Listener{

	private static Map<String, Integer> ranks = new LinkedHashMap<String, Integer>();
	private static Map<UUID, String> userRanks = new HashMap<UUID, String>();
	private static Map<UUID, String> nextRanks = new HashMap<UUID, String>();
	
	public RankHandler(String name){
		super(name);
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.rank")  || sender.hasPermission("paintball.general") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if(args.length == 0){
			if(sender instanceof Player){
				Player player = (Player) sender;
				player.sendMessage(MessageHandler.getMessage("rankInfoTitle"));
				player.sendMessage(MessageHandler.getMessage("useRankList"));
				MessageHandler.getMessage("currentRank", "%rank%", userRanks.get(player.getUniqueId()));
				if(nextRanks.containsKey(player.getUniqueId())){
					String nextRank = nextRanks.get(player.getUniqueId());
					player.sendMessage(MessageHandler.getMessage("nextRank", "%rank%", nextRank));
					player.sendMessage(MessageHandler.getMessage("nextRankUnlock", "%points%", "" + ranks.get(nextRank)));
				}else{
					player.sendMessage(MessageHandler.getMessage("atHighestRank"));
				}
			}else{
				sender.sendMessage(MessageHandler.getMessage("useRankCommand"));
			}
		}else if(args.length == 1){
			if(args[0].equalsIgnoreCase("list")){
				sender.sendMessage(MessageHandler.getMessage("listOfRanks"));
				for(String rank : ranks.keySet()){
					sender.sendMessage(MessageHandler.getMessage("rankList", "%rank%", rank, "%points%", "" + ranks.get(rank)));
				}
			}else{
				String name = args[0];
				TaskHandler.runTask(new Runnable() {
					@Override
					public void run() {
						if(MySQL.hasValidConnection() == false){
							MySQL.connectToDatabase();
						}
						if(MySQL.isInDatabase(name, true)){
							String uuid = MySQL.getIDFromName(name);
							int points = MySQL.getInt(uuid, "points");
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("playerRankInfo", "%player%", name));
									sender.sendMessage(MessageHandler.getMessage("userRankList"));
									sender.sendMessage(MessageHandler.getMessage("playerCurrentRank", "%player%", name, "%rank%", getRankForPlayer(points)));
									String nextRank = getNextRank(points);
									if(nextRank != null){
										sender.sendMessage(MessageHandler.getMessage("playerNextRank", "%player%", name, "%rank%", nextRank));
										sender.sendMessage(MessageHandler.getMessage("playerNextRankUnlock", "%point%", "" + ranks.get(nextRank)));
									}else{
										sender.sendMessage(MessageHandler.getMessage("playerAtHighest", "%player%", name));
									}
								}
							});
						}else{
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									sender.sendMessage(MessageHandler.getMessage("notInDatabase", "%name%", name));
								}
							});
						}
					}
				});
			}
		}else{
			sender.sendMessage(MessageHandler.getMessage("useRankCommand"));
		}
		return false;
	}
	
	public static void loadRanks(ConfigurationSection section){
		if(section == null) return;
		ConfigurationSection titleSection = section.getConfigurationSection("titles");
		if(titleSection == null) return;
		Set<String> titles = titleSection.getKeys(false);
		int rankTotal = 0;
		for(String title : titles){
			String text = titleSection.getString(title + ".textColor");
			if(text == null){
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning " + title);
				continue;
			}
			String finalTitle = text + title;
			finalTitle = ChatColor.translateAlternateColorCodes('&', finalTitle);
			int points = titleSection.getInt(title + ".pointsNeeded", -1);
			if(points < 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning points of " + title);
				continue;
			}else if(ranks.values().contains(points)){
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There is more than one rank with a point value of " + points);
				continue;
			}
			ranks.put(finalTitle, points);
			rankTotal ++;
		}
		Main.main.getLogger().info("registered " + rankTotal + " ranks!");
	}
	public static void handleLeave(Player player){
		userRanks.remove(player.getUniqueId());
		nextRanks.remove(player.getUniqueId());
	}
	public static void handleJoin(Player player, int points){
		String rank = getRankForPlayer(points);
		if(rank == null){
			player.sendMessage(MessageHandler.getMessage("noRank"));
		}else{
			player.sendMessage(MessageHandler.getMessage("currentRank", "%rank%", rank));
			userRanks.put(player.getUniqueId(), rank);
		}
		String nextRank = getNextRank(points);
		if(nextRank != null){
			nextRanks.put(player.getUniqueId(), nextRank);
		}
		setArmor(player);
	}
	private static String getRankForPlayer(int points){
		int rankPoints = -1;
		String currentRank = null;
		for(String rank : ranks.keySet()){
			int i = ranks.get(rank);
			if(points >= i){
				if(i > rankPoints){
					rankPoints = i;
					currentRank = rank;
				}
			}
		}
		return currentRank;
	}
	private static String getNextRank(int points){
		int neededPoints = -1;
		String nextRank = null;
		for(String rank : ranks.keySet()){
			int rankPoints = ranks.get(rank);
			if(rankPoints > points){
				if(neededPoints <= 0){
					neededPoints = rankPoints;
					nextRank = rank;
				}else if((neededPoints - points) > (rankPoints - points)){
					neededPoints = rankPoints;
					nextRank = rank;
				}
			}
		}
		return nextRank;
	}
	public static void checkRank(Player player, int points){
		if(userRanks.containsKey(player.getUniqueId()) == false){
			userRanks.put(player.getUniqueId(), getRankForPlayer(points));
		}
		if(nextRanks.containsKey(player.getUniqueId())){
			if(points >= ranks.get(nextRanks.get(player.getUniqueId()))){
				player.sendMessage(MessageHandler.getMessage("rankedUp"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
				userRanks.put(player.getUniqueId(), nextRanks.get(player.getUniqueId()));
				String nextRank = getNextRank(points);
				if(nextRank != null){
					nextRanks.put(player.getUniqueId(), nextRank);
					player.sendMessage(MessageHandler.getMessage("nextRank", "%rank%", nextRank));
					player.sendMessage(MessageHandler.getMessage("nextRankUnlock", "%points%", "" + ranks.get(nextRank)));
				}else{
					player.sendMessage(MessageHandler.getMessage("atHighestRank"));
					nextRanks.remove(player.getUniqueId());
				}
				setArmor(player);
				runCommands(player, userRanks.get(player.getUniqueId()));
			}else if(points < ranks.get(userRanks.get(player.getUniqueId()))){
				player.sendMessage(MessageHandler.getMessage("rankedDown"));
				userRanks.put(player.getUniqueId(), getRankForPlayer(points));
				String nextRank = getNextRank(points);
				nextRanks.put(player.getUniqueId(), nextRank);
				
			}
		}else if(points < ranks.get(userRanks.get(player.getUniqueId()))){
			player.sendMessage(MessageHandler.getMessage("rankedDown"));
			userRanks.put(player.getUniqueId(), getRankForPlayer(points));
			String nextRank = getNextRank(points);
			nextRanks.put(player.getUniqueId(), nextRank);
			player.sendMessage(MessageHandler.getMessage("nextRank", "%rank%", nextRank));
			player.sendMessage(MessageHandler.getMessage("nextRankUnlock", "%points%", "" + ranks.get(nextRank)));
		}
	}private static void runCommands(Player player, String rank){
		rank = ChatColor.stripColor(rank.toLowerCase());
		List<String> commands = Configuration.getConfig().getStringList("ranks.titles." + rank + ".commandsToRun");
		for(String command : commands){
			if(command.equalsIgnoreCase("none")) continue;
			command = command.replaceAll("%name%", player.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}
	public static void setArmor(Player player){
		if(Main.enableRanks == false){
			player.getInventory().setHelmet(null);
			return;
		}
		String rank = userRanks.get(player.getUniqueId());
		if(rank == null){
			player.sendMessage("Your rank is null. Please report this.");
			player.getInventory().setHelmet(null);	
		}
		String rankTitle = rank;
		rank = ChatColor.stripColor(rank);
		int red = Configuration.getConfig().getInt("ranks.titles." + rank + ".color.red");
		int green = Configuration.getConfig().getInt("ranks.titles." + rank + ".color.green");
		int blue = Configuration.getConfig().getInt("ranks.titles." + rank + ".color.blue");
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
		meta.setColor(Color.fromRGB(red, green, blue));
		meta.setDisplayName(rankTitle);
		helmet.setItemMeta(meta);
		player.getInventory().setHelmet(helmet);
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){ 						
		if(Main.enableRanks && Main.rankPrefix){
			Player player = event.getPlayer();
			if(Utils.getPlayers().contains(player.getUniqueId()) || Utils.getAsyncPlayers().contains(player.getUniqueId())){
				if(userRanks.containsKey(player.getUniqueId())){
					String rank = userRanks.get(player.getUniqueId());
					if(Main.colorText){
						if(Utils.getGameStatus() == GameStatus.RUNNING){
							if(Utils.getRed().contains(player.getUniqueId())){
								event.setFormat(Main.rankPrefixColor + "[" + ChatColor.RESET + rank + Main.rankPrefixColor + "] " + 
										ChatColor.RESET + "<" + player.getDisplayName() + ChatColor.RESET + "> " + ChatColor.RED + event.getMessage());
								return;
							}else if(Utils.getBlue().contains(player.getUniqueId())){
								event.setFormat(Main.rankPrefixColor + "[" + ChatColor.RESET + rank + Main.rankPrefixColor + "] " + 
										ChatColor.RESET + "<" + player.getDisplayName() + ChatColor.RESET + "> " + ChatColor.BLUE + event.getMessage());
								return;
							}
						}
					}
					event.setFormat(Main.rankPrefixColor + "[" + ChatColor.RESET + rank + Main.rankPrefixColor + "] " + 
							ChatColor.RESET + "<" + player.getDisplayName() + ChatColor.RESET + "> " + event.getMessage());
					return;
				}else{
					if(Main.colorText){
						if(Utils.getGameStatus() == GameStatus.RUNNING){
							if(Utils.getRed().contains(player.getUniqueId())){
								event.setFormat(ChatColor.RESET + "<" + player.getDisplayName() + ChatColor.RESET + "> " + ChatColor.RED + event.getMessage());
								return;
							}else if(Utils.getBlue().contains(player.getUniqueId())){
								event.setFormat(ChatColor.RESET + "<" + player.getDisplayName() + ChatColor.RESET + "> " + ChatColor.BLUE + event.getMessage());
								return;
							}
						}
					}
				}
			}
		}
	}
	public static int getRanksSize(){
		return ranks.size();
	}
	public static int getUserRanksSize(){
		return userRanks.size();
	}
	public static int getNextRanksSize(){
		return nextRanks.size();
	}
}
