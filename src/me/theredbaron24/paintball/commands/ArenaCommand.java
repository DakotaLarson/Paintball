package me.theredbaron24.paintball.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.Match;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.MessageHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ArenaCommand extends BukkitCommand{

	public ArenaCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.arena") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(MessageHandler.getMessage("noUseCommand"));
		} else {
			Player player = (Player) sender;
			if (args.length == 0) {
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList1"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList2"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList3"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList4"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList5"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList6"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList7"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList8"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList9"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList10"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList11"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList12"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList13"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList14"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList15"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList16"));
				sender.sendMessage(MessageHandler.getMessage("arenaCommandList17"));
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list") == false) {
					sender.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
				} else {
					listArenas(player);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("next")) {
					setNextArena(player, args[1]);
				} else if (args[0].equalsIgnoreCase("create")) {
					createArena(player, args[1]);
				} else if (args[0].equalsIgnoreCase("remove")) {
					removeArena(player, args[1]);
				} else if (args[0].equalsIgnoreCase("lobby")) {
					setLobbyOrDeath(player, args[1], true);
				} else if (args[0].equalsIgnoreCase("death")) {
					setLobbyOrDeath(player, args[1], false);
				} else if (args[0].equalsIgnoreCase("enable")) {
					enableOrDisableArena(player, args[1], true);
				} else if (args[0].equalsIgnoreCase("disable")) {
					enableOrDisableArena(player, args[1], false);
				} else if (args[0].equalsIgnoreCase("tp")) {
					teleToArena(player, args[1]);
				}else{
					sender.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("spawn")) {
					setSpawn(player, args[2], args[1]);
				} else if (args[0].equalsIgnoreCase("flag")) {
					setFlag(player, args[2], args[1]);
				} else if (args[0].equalsIgnoreCase("gamemode")) {
					setGamemode(player, args[1], args[2]);
				}else if (args[0].equalsIgnoreCase("clear")){
					clearSpawns(player, args[1], args[2]);
				} else if (args[0].equalsIgnoreCase("initial")){
					createInitialSpawns(player, args[2], args[1], false);
				} else {
					sender.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
				}
			}else if(args.length == 4){
				if(args[0].equalsIgnoreCase("initial") && args[3].equalsIgnoreCase("remove")){
					createInitialSpawns(player, args[2], args[1], true);
				}else{
					sender.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
				}
			} else {
				sender.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
			}
		}
		return false;
	}

	private void listArenas(Player player) {
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas");
		if(section == null){
			player.sendMessage(MessageHandler.getMessage("arenaCommandNotUnderstood"));
			return;
		}
		Set<String> arenas = section.getKeys(false);
		if(arenas == null || arenas.isEmpty()){
			player.sendMessage(MessageHandler.getMessage("noArenasCreated"));
			return;
		}
		player.sendMessage(MessageHandler.getMessage("createdArenasTitle"));
		for (String arena : arenas) {
			if (Configuration.getArenaConfig().getBoolean("arenas." + arena + ".enabled")) {
				player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + arena);
			} else {
				player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GRAY + arena);
			}
		}
	}

	private void createArena(Player player, String arena) {
		if (Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) != null) {
			player.sendMessage(MessageHandler.getMessage("arenaExists", "%arena%", arena));
		}else{
			try{
				Integer.parseInt(arena);
				player.sendMessage(MessageHandler.getMessage("arenaNameInt"));
			}catch(NumberFormatException e){
				if(arena.equalsIgnoreCase("random")){
					player.sendMessage(MessageHandler.getMessage("arenaNameRand"));
				}
				Configuration.getArenaConfig().set("arenas." + arena + ".redShots", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".redWins", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".redKills", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".redCaps", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".blueShots", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".blueWins", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".blueKills", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".blueCaps", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".matchCount", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".draws", 0);
				Configuration.getArenaConfig().set("arenas." + arena + ".enabled", false);
				Configuration.getArenaConfig().set("arenas." + arena + ".world", player.getLocation().getWorld().getName());
				player.sendMessage(MessageHandler.getMessage("arenaCreated", "%arena%", arena));
				
			}
		}
	}

	private void removeArena(Player player, String arena) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
		}else{
			if(isEnabled(arena)){
				player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
				return;
			}
			Configuration.getArenaConfig().set("arenas." + arena, null);
			Configuration.saveArenaConfig();
			player.sendMessage(MessageHandler.getMessage("arenaRemoved", "%arena%", arena));
		}
	}

	private void setGamemode(Player player, String gamemode, String arena) {
		String finalGamemode = gamemode.toUpperCase();
		if(finalGamemode.equals("TDM") || finalGamemode.equals("CTF") || finalGamemode.equals("RTF") || finalGamemode.equals("ELM")){
			if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
				player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			}else{
				if(isEnabled(arena)){
					player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
					return;
				}
				List<String> issues = new ArrayList<String>();
				issues.addAll(hasGeneralNecessities(arena));
				issues.addAll(hasTeamSpawns(arena));
				if(finalGamemode.equals("CTF") || finalGamemode.equals("RTF")){
					issues.addAll(hasTeamFlags(arena, finalGamemode.equals("CTF")));
				}
				if(issues.isEmpty()){
					Configuration.getArenaConfig().set("arenas." + arena + ".gamemode", finalGamemode);
					player.sendMessage(MessageHandler.getMessage("arenaGamemodeSet", "%arena%", arena, "%gamemode%", finalGamemode));
				}else{
					player.sendMessage(MessageHandler.getMessage("arenaCorrectIssues"));
					for(String s : issues){
						player.sendMessage(ChatColor.YELLOW + "- " + s);
					}
				}
			}
		}else{
			player.sendMessage(MessageHandler.getMessage("arenaGamemodeNoValid", "%gamemode%", gamemode));
			player.sendMessage(MessageHandler.getMessage("arenaGamemodeNoValid2"));
		}
	}

	private void setLobbyOrDeath(Player player, String arena, boolean isLobby) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}
		if(isEnabled(arena)){
			player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
			return;
		}
		Location location = player.getLocation();
		if(isLobby){
			Configuration.getArenaConfig().set("arenas." + arena + ".lobby.x", location.getX());
			Configuration.getArenaConfig().set("arenas." + arena + ".lobby.y", location.getY());
			Configuration.getArenaConfig().set("arenas." + arena + ".lobby.z", location.getZ());
			Configuration.getArenaConfig().set("arenas." + arena + ".lobby.yaw", location.getYaw());
			Configuration.getArenaConfig().set("arenas." + arena + ".lobby.pitch", location.getPitch());
			player.sendMessage(MessageHandler.getMessage("arenaLobbySpawnSet", "%arena%", arena));
		}else{
			Configuration.getArenaConfig().set("arenas." + arena + ".death.x", location.getX());
			Configuration.getArenaConfig().set("arenas." + arena + ".death.y", location.getY());
			Configuration.getArenaConfig().set("arenas." + arena + ".death.z", location.getZ());
			Configuration.getArenaConfig().set("arenas." + arena + ".death.yaw", location.getYaw());
			Configuration.getArenaConfig().set("arenas." + arena + ".death.pitch", location.getPitch());
			player.sendMessage(MessageHandler.getMessage("arenaDeathSpawnSet", "%arena%", arena));
		}
	}

	private void enableOrDisableArena(Player player, String arena, boolean enable) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}if(enable){
			if(Configuration.getArenaConfig().getBoolean("arenas." + arena + ".enabled")){
				player.sendMessage(MessageHandler.getMessage("arenaAlreadyEnabled", "%arena%", arena));
			}else{
				if(hasGamemode(arena)){
					Configuration.getArenaConfig().set("arenas." + arena + ".enabled", true);
					Configuration.saveArenaConfig();
					player.sendMessage(MessageHandler.getMessage("arenaEnabled", "%arena%", arena));
				}else{
					player.sendMessage(MessageHandler.getMessage("arenaSetGamemodeFirst", "%arena%", arena));
				}
			}
		}else{
			if(Match.getArena() != null && Match.getArena().equals(arena) || VoteHandler.getVotableArenas().contains(arena)){
				player.sendMessage(MessageHandler.getMessage("arenaNoDisable"));
			}else{
				Configuration.getArenaConfig().set("arenas." + arena + ".enabled", false);
				player.sendMessage(MessageHandler.getMessage("arenaDisabled", "%arena%", arena));
			}
		}
	}

	private void setNextArena(Player player, String arena) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
		}
		else if(Configuration.getArenaConfig().getBoolean("arenas." + arena + ".enabled")){
			Main.nextArena = arena;
			player.sendMessage(MessageHandler.getMessage("arenaSetNext", "%arena%", arena));
		}
	}
	
	private void teleToArena(Player player, String arena){
		ConfigurationSection section = Configuration.getArenaConfig().getConfigurationSection("arenas." + arena + ".lobby");
		if(section == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoTeleport"));
		}else{
			World world = Bukkit.getWorld(Configuration.getArenaConfig().getString("arenas." + arena + ".world"));
			double x = section.getDouble("x");
			double y = section.getDouble("y");
			double z = section.getDouble("z");
			float yaw = (float) section.getDouble("yaw");
			float pitch = (float) section.getDouble("pitch");
			player.teleport(new Location(world, x, y, z, yaw, pitch));
			player.sendMessage(MessageHandler.getMessage("arenaTeleport", "%arena%", arena));
		}
	}

	private void setSpawn(Player player, String arena, String team) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}
		if(isEnabled(arena)){
			player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
			return;
		}
		team = team.toLowerCase();
		if(team.equals("red") || team.equals("blue")){
			Location loc = player.getLocation();
			String LocationString = loc.getX() + " : " + loc.getY() + " : " + loc.getZ() + " : " + loc.getYaw() + " : " + loc.getPitch();
			List<String> locations = Configuration.getArenaConfig().getStringList("arenas." + arena + ".spawns." + team);
			locations.add(LocationString);
			Configuration.getArenaConfig().set("arenas." + arena + ".spawns." + team, locations);
			if(team.equals("red")){
				player.sendMessage(MessageHandler.getMessage("arenaRedSpawnSet", "%arena%", arena));
			}else{
				player.sendMessage(MessageHandler.getMessage("arenaBlueSpawnSet", "%arena%", arena));
			}
		}else{
			player.sendMessage(MessageHandler.getMessage("arenaNoValidTeam", "%team%", team));
		}
	}
	private void setFlag(Player player, String arena, String team) {
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}
		if(isEnabled(arena)){
			player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
			return;
		}
		Set<Material> materials = null;
		Block block = player.getTargetBlock(materials, 20);
		if(block.getType() != Material.WOOL){
			player.sendMessage(MessageHandler.getMessage("arenaLookAtWool"));
		}else{
			team = team.toLowerCase();
			if(team.equals("red") || team.equals("blue") || team.equals("neutral")){
				Configuration.getArenaConfig().set("arenas." + arena + ".flag." + team + ".x", block.getLocation().getBlockX());
				Configuration.getArenaConfig().set("arenas." + arena + ".flag." + team + ".y", block.getLocation().getBlockY());
				Configuration.getArenaConfig().set("arenas." + arena + ".flag." + team + ".z", block.getLocation().getBlockZ());
				player.sendMessage(MessageHandler.getMessage("arenaFlagSet", "%arena%", arena, "%team%", team));
			}else{
				player.sendMessage(MessageHandler.getMessage("arenaTeamNotValid", "%team%", team));
			}
		}
	}
	private void clearSpawns(Player player, String team, String arena){
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}
		if(isEnabled(arena)){
			player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
			return;
		}
		if(team.equalsIgnoreCase("red") || team.equalsIgnoreCase("blue")){
			Configuration.getArenaConfig().set("arenas." + arena + ".spawns." + team.toLowerCase(), null);
			Configuration.getArenaConfig().set("arenas." + arena + ".enabled", false);
			Configuration.saveArenaConfig();
			player.sendMessage(MessageHandler.getMessage("arenaSpawnsRemoved", "%arena%", arena, "%team%", team));
		}else{
			player.sendMessage(MessageHandler.getMessage("arenaNoValidTeam", "%team%", team));
		}
	}
	private void createInitialSpawns(Player player, String arena, String team, boolean clear){
		if(Configuration.getArenaConfig().getConfigurationSection("arenas." + arena) == null){
			player.sendMessage(MessageHandler.getMessage("arenaNoExist", "%arena%", arena));
			return;
		}
		if(isEnabled(arena)){
			player.sendMessage(MessageHandler.getMessage("arenaNotDisabled", "%arena%", arena));
			return;
		}
		String finalTeam = team.toLowerCase();
		if((finalTeam.equals("red") || finalTeam.equals("blue")) == false){
			player.sendMessage(MessageHandler.getMessage("initialBadTeam", "%team%", team));
			return;
		}
		if(clear){
			if(Configuration.getArenaConfig().get("arenas." + arena + ".initial." + finalTeam) == null){
				player.sendMessage(MessageHandler.getMessage("noInitialSpawns"));
			}else{
				Configuration.getArenaConfig().set("arenas." + arena + ".intial." + finalTeam, null);
				Configuration.saveArenaConfig();
				player.sendMessage(MessageHandler.getMessage("initialRemoved", "%team%", finalTeam, "%arena%", arena));
			}
		}else{
			List<String> spawns = Configuration.getArenaConfig().getStringList("arenas." + arena + ".intial." + finalTeam);
			Location loc = player.getLocation();
			String locationString = loc.getX() + " : " + loc.getY() + " : " + loc.getZ() + " : " + loc.getYaw() + " : " + loc.getPitch();
			spawns.add(locationString);
			Configuration.getArenaConfig().set("arenas." + arena + ".initial." + finalTeam, spawns);
			Configuration.saveArenaConfig();
			player.sendMessage(MessageHandler.getMessage("initialSet", "%arena%", arena, "%team%", team));
		}
	}
	private boolean isEnabled(String arena){
		return Configuration.getArenaConfig().getBoolean("arenas." + arena + ".enabled");
	}
	private List<String> hasGeneralNecessities(String arena){
		List<String> issues = new ArrayList<String>();
		if(Configuration.getArenaConfig().get("arenas." + arena + ".lobby") == null){
			issues.add("Lobby spawn not set.");
		}
		if(Configuration.getArenaConfig().get("arenas." + arena + ".death") == null){
			issues.add("Death spawn not set.");
		}
		return issues;
	}
	private List<String> hasTeamSpawns(String arena){
		List<String> issues = new ArrayList<String>();
		if(Configuration.getArenaConfig().get("arenas." + arena + ".spawns.red") == null){
			issues.add("Red spawns not set.");
		}
		if(Configuration.getArenaConfig().get("arenas." + arena + ".spawns.blue") == null){
			issues.add("Blue spawns not set.");
		}
		return issues;
	}
	private List<String> hasTeamFlags(String arena, boolean isCTF){
		List<String> issues = new ArrayList<String>();
		if(Configuration.getArenaConfig().get("arenas." + arena + ".flag.red") == null){
			issues.add("Red flag not set.");
		}
		if(Configuration.getArenaConfig().get("arenas." + arena + ".flag.blue") == null){
			issues.add("Blue flag not set.");
		}
		if(isCTF == false){
			if(Configuration.getArenaConfig().get("arenas." + arena + ".flag.neutral") == null){
				issues.add("Neutral flag not set.");
			}
		}
		return issues;
	}
	private boolean hasGamemode(String arena){
		if(Configuration.getArenaConfig().get("arenas." + arena + ".gamemode") == null){
			return false;
		}
		return true;
	}
}
