package me.theredbaron24.paintball.main;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.commands.AdminCommand;
import me.theredbaron24.paintball.commands.ArenaCommand;
import me.theredbaron24.paintball.commands.BestCommand;
import me.theredbaron24.paintball.commands.ControlPointCommand;
import me.theredbaron24.paintball.commands.DeathMessageHandler;
import me.theredbaron24.paintball.commands.GetsizesCommand;
import me.theredbaron24.paintball.commands.JoinCommand;
import me.theredbaron24.paintball.commands.KitCommand;
import me.theredbaron24.paintball.commands.LeaveCommand;
import me.theredbaron24.paintball.commands.LeaveallCommand;
import me.theredbaron24.paintball.commands.PBInfoCommand;
import me.theredbaron24.paintball.commands.PaintballCommand;
import me.theredbaron24.paintball.commands.StatsCommand;
import me.theredbaron24.paintball.commands.TeamCommand;
import me.theredbaron24.paintball.commands.TeamSelectionCommand;
import me.theredbaron24.paintball.commands.VoteCommand;
import me.theredbaron24.paintball.events.*;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private InteractionListener interactionListener = new InteractionListener(this);
	private MiscEventListener miscListener = new MiscEventListener(this);
	private ProjectileHitListener projListener = new ProjectileHitListener(this);
	private PlayerJoinListener pjListener = new PlayerJoinListener(this);
	public LeaveCommand leaveCommand = new LeaveCommand("leave");
	private RankHandler rankHandler = new RankHandler("rank");
	private MOTDListener motdListener = new MOTDListener();

	private Set<String> errors = null;
	public static Map<UUID, Set<Location>> mines = new HashMap<>();
	public static String nextArena = null;
	
	public static int maxPlayers = 0;
	public static int minPlayers = 0;
	public static int tdmTime = 0;
	public static int ctfTime = 0;
	public static int rtfTime = 0;
	public static int elmTime = 0;
	public static int spawnProtectionTime = 0;
	public static boolean useNMSCode = false;
	public static boolean colorArmor = false;
	public static boolean bleed = false;
	public static boolean firework = false;
	public static boolean ctfDeathbox = false;
	public static boolean tdmDeathbox = false;
	public static boolean rtfDeathbox = false;
	public static boolean elmDeathbox = false;
	public static boolean ctfDeathTimers = false;
	public static boolean tdmDeathTimers = false;
	public static boolean rtfDeathTimers = false;
	public static boolean elmDeathTimers = false;
	public static int ctfDeathTime = 0;
	public static int tdmDeathTime = 0;
	public static int rtfDeathTime = 0;
	public static int elmDeathTime = 0;
	public static int ctfHits = 0;
	public static int tdmHits = 0;
	public static int rtfHits = 0;
	public static int elmHits = 0;
	public static boolean useLeft = false;
	public static boolean useRight = false;
	public static int ammoCount = 0;
	public static double ballSpeed = 0;
	public static boolean kitsEnabled = false;
	public static int reloadTime = 0;
	public static boolean useClickable = false;
	public static boolean instantKill = false;
	public static boolean punchForAll = false;
	public static boolean punchForSome = false;
	public static List<String> kitsThatCanPunch = null;
	public static boolean enableCurrency = false;
	public static int countdownTime = 0;
	public static int initializationTime = 0;
	public static boolean selectTeamPerm = false;
	public static boolean hideNametagsFromOpponents = false;
	public static boolean hideNametagsFromTeam = false;
	public static int hitPoints = 0;
	public static int killPoints = 0;
	public static boolean flagBreak = false;
	public static boolean flagFW = false;
	public static int flagDropTime = 0;
	public static int flagGrabPoints = 0;
	public static int flagPickPoints = 0;
	public static int flagDropPoints = 0;
	public static int flagCapPoints = 0;
	public static int flagRetPoints = 0;
	public static int matchWinPoints = 0;
	public static int matchPartPoints = 0;
	public static int hitCurr = 0;
	public static int killCurr = 0;
	public static int flagGrabCurr = 0;
	public static int flagPickCurr = 0;
	public static int flagDropCurr = 0;
	public static int flagCapCurr = 0;
	public static int flagRetCurr = 0;
	public static int matchWinCurr = 0;
	public static int matchPartCurr = 0;
	public static boolean enableVoting = false;
	public static boolean votingPermission = false;
	public static int votingArenaCount = 0;
	public static boolean votingRandomArena = false;
	public static boolean enableRanks = false;
	public static boolean rankPrefix = false;
	public static String rankPrefixColor = null;
	public static boolean announcePurchases = false;
	public static boolean colorText = false;
	public static boolean enableTokens = false;
	public static int neededTokens = 0;
	public static boolean enableKillstreaks = false;
	public static int ksKills = 0;
	public static boolean autoJoin = false;
	public static boolean reloadJoin = false;
	public static boolean pbWorld = false;
	public static int maxCtfCaps = 0;
	public static int maxTdmKills = 0;
	public static int maxRtfCaps = 0;
	public static boolean useVotingBlock = false;
	public static boolean updateInv = false;
	public static boolean useOther = false;
	public static boolean flagHats = false;
	public static boolean cmdPrefixes = false;
	public static int elmDeaths = 0;
	public static boolean useNeutral = false;
	public static boolean cpNeutralContest = false;
	public static boolean sqlDebug = false;
	public static String redTeamColor = null;
	public static String blueTeamColor = null;
	public static String deadTeamColor = null;
	public static boolean allowHandSwap = false;
	public static boolean useHits = false;
	public static boolean updatingMOTD = false;
	
	public static Main main = null;
	public static final String version = "1.9.5";
	
	@Override
	public void onEnable(){
		long free = Runtime.getRuntime().freeMemory();
		long initTime = System.currentTimeMillis();
		main = this;
		Bukkit.getPluginManager().registerEvents(interactionListener, this);
		Bukkit.getPluginManager().registerEvents(pjListener, this);
		Bukkit.getPluginManager().registerEvents(miscListener, this);
		Bukkit.getPluginManager().registerEvents(projListener, this);
		Bukkit.getPluginManager().registerEvents(leaveCommand, this);
		Bukkit.getPluginManager().registerEvents(rankHandler, this);
		TaskHandler.init();
		errors = new HashSet<String>();
		Configuration.init(this);
		loadConfigValues();
		registerCommands();
		MessageHandler.loadMessages();
		ConfigurationSection section = Configuration.getConfig().getConfigurationSection("kits");
		Kit.loadData(section);
		KitSelectionHandler.createKitInv();
		ActionMenuHandler.createInventories();
		if(enableKillstreaks){
			KillstreakHandler.initKillStreaks();
		}
		if(!VoteHandler.createVotingList(null)){
			enableVoting = false;
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Voting was disabled. Please add more arenas.");
		}
		if(enableRanks){
			RankHandler.loadRanks(Configuration.getConfig().getConfigurationSection("ranks"));
		}
		if(updatingMOTD){
			motdListener.initStrings();
			Bukkit.getPluginManager().registerEvents(motdListener, this);
		}
		String host = Configuration.getConfig().getString("database.host");
		int port = Configuration.getConfig().getInt("database.port");
		String database = Configuration.getConfig().getString("database.database");
		String username = Configuration.getConfig().getString("database.username");
		String password = Configuration.getConfig().getString("database.password");
		boolean notDefault = Configuration.getConfig().getBoolean("database.notDefault");
		/*try{
			Metrics metrics = new Metrics(this);
			if(metrics.start()){
				getLogger().info("metrics started");
			}else{
				getLogger().info("metrics not started");
			}
		}catch (IOException e){
			this.getLogger().info("Failed to submit the stats :-(");
		}*/
		if(notDefault){
			if(!MySQL.connectToDatabase(host, port, database, username, password)){
				errors.add("Connection to database was not created.");
				Bukkit.getPluginManager().disablePlugin(this);
			}
			if(!MySQL.createTable()){
				errors.add("Table could not be created in database.");
			}
		}else{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please configure paintball database settings and restart.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if(errors.isEmpty()){
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Paintball " + ChatColor.YELLOW + "[" + version + "]" + ChatColor.GREEN + " was successfully enabled!");
		}else{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Paintball did enable, however, these errors surfaced:");
			for(String s : errors){
				Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "- " + s);
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "If you are unsure of what these errors are caused by, "
					+ "or believe this is an error, please contact THEREDBARON24");
		}
		errors.clear();
		errors = null;
		if(reloadJoin || pbWorld){
			joinPlayers();
		}
		DecimalFormat format = new DecimalFormat("#.##");
		long total = free - Runtime.getRuntime().freeMemory();
		double mb = (double) total/100000;
		total = System.currentTimeMillis() - initTime;
		double secs = (double) total/1000;
		this.getLogger().info("Using: " + format.format(mb) + " megabytes.");
		this.getLogger().info("Initialized in: " + format.format(secs) + " seconds.");
	}
	
	@Override
	public void onDisable(){
		Configuration.saveArenaConfig();
		for(Set<Location> set : Main.mines.values()){
			for(Location location : set){
				location.getBlock().setType(Material.AIR);
			}
		}
		if(!Main.reloadJoin && !Main.pbWorld ){
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				player.setExp(0f);
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						player.teleport(Utils.getLobbySpawn());
					}
				});
				player.getInventory().clear();
				NMS.sendTabTitle(player, null, null);
				player.sendMessage(MessageHandler.getMessage("kickedNoRejoin"));
			}
		}
		Main.mines.clear();
		MySQL.closeConnection();
		TaskHandler.close();
		this.getLogger().info("has been disabled");
	}
	@Override
	public FileConfiguration getConfig(){
		return Configuration.getConfig();
	}
	private void registerCommands(){
		if(cmdPrefixes){
			NMS.registerCommand("pb", new PaintballCommand("pb"));
			PaintballCommand.init( new ArenaCommand("arena"), new AdminCommand("admin"), new TeamCommand("t"), leaveCommand, new JoinCommand("join"), 
					new StatsCommand("stats"), new VoteCommand("vote"), new DeathMessageHandler("toggle"), new BestCommand("best"), 
					new KitCommand("kit"), new GetsizesCommand("getsizes"), rankHandler, new PBInfoCommand("pbinfo"), new TeamSelectionCommand("team"), new LeaveallCommand("leaveall"), new ControlPointCommand("cp"));
		}else{
			NMS.registerCommand("arena", new ArenaCommand("arena"));
			NMS.registerCommand("admin", new AdminCommand("admin"));
			NMS.registerCommand("team", new TeamSelectionCommand("team"));
			NMS.registerCommand("leave", leaveCommand);
			NMS.registerCommand("leaveall", new LeaveallCommand("leaveall"));
			NMS.registerCommand("join", new JoinCommand("join"));
			NMS.registerCommand("stats", new StatsCommand("stats"));
			NMS.registerCommand("vote", new VoteCommand("vote"));
			NMS.registerCommand("toggle", new DeathMessageHandler("toggle"));
			NMS.registerCommand("best", new BestCommand("best"));
			NMS.registerCommand("kit", new KitCommand("kit"));
			NMS.registerCommand("getsizes", new GetsizesCommand("getsizes"));
			NMS.registerCommand("rank", rankHandler);
			NMS.registerCommand("t", new TeamCommand("t"));
			NMS.registerCommand("pbinfo", new PBInfoCommand("pbinfo"));
			NMS.registerCommand("cp", new ControlPointCommand("cp"));
		}
	}
	private void loadConfigValues(){
		setSpawn();
		FileConfiguration config = Configuration.getConfig();
		maxPlayers = config.getInt("general.maxPlayers", -1);
		minPlayers = Math.max(config.getInt("general.minPlayers", 2), 2);
		tdmTime = Math.max(config.getInt("gamemodes.TDM.time", 300), 30);
		ctfTime = Math.max(config.getInt("gamemodes.CTF.time", 300), 30);
		useNMSCode = config.getBoolean("general.useNMSCode", false);
		colorArmor = config.getBoolean("general.colorArmor", true);
		bleed = config.getBoolean("general.bleedOnHit", true);
		firework = config.getBoolean("general.fireworkOnDeath", true);
		ctfDeathbox = config.getBoolean("gamemodes.CTF.deathBox", true);
		tdmDeathbox = config.getBoolean("gamemodes.TDM.deathBox", false);
		ctfDeathTimers = config.getBoolean("gamemodes.CTF.useTimers", false);
		tdmDeathTimers = config.getBoolean("gamemodes.TDM.useTimers", true);
		ctfDeathTime = Math.max(config.getInt("gamemodes.CTF.deathTime", 15), 1);
		tdmDeathTime = Math.max(config.getInt("gamemodes.TDM.deathTime", 15), 1);
		useLeft = config.getBoolean("weapons.useLeft", true);
		useRight = config.getBoolean("weapons.useRight", true);
		if(!useRight && !useLeft){
			useRight = true;
			useLeft = true;
		}
		ammoCount = Math.max(config.getInt("weapons.ammoCount", 64), 1);
		ballSpeed = Math.max(config.getDouble("weapons.ballSpeed", 2.5), 1d);
		kitsEnabled = config.getBoolean("weapons.kitsEnabled", true);
		reloadTime = Math.max(config.getInt("weapons.reloadTime", 5), 0);
		useClickable = config.getBoolean("general.sendClickableMessages",  false);
		instantKill = config.getBoolean("weapons.instantKill", true);
		punchForSome = config.getBoolean("weapons.punchForSome", false);
		punchForAll = config.getBoolean("weapons.punchForAll", true);
		kitsThatCanPunch = config.getStringList("weapons.kitsThatCanPunch");
		enableCurrency = config.getBoolean("currency.enableCurrency", true);
		countdownTime = Math.max(config.getInt("general.countdownTime", 30), 5);
		initializationTime = Math.max(config.getInt("general.initializationTime", 15), 5);
		selectTeamPerm = config.getBoolean("general.selectPerm", false);
		hideNametagsFromOpponents = config.getBoolean("general.hideTagsFromOpponents", true);
		hideNametagsFromTeam = config.getBoolean("general.hideTagsFromTeam", true);
		flagBreak = config.getBoolean("general.flagBreakToGrab", false);
		flagFW = config.getBoolean("general.flagDropFW", true);
		flagDropTime = Math.max(config.getInt("general.flagDropTime", 30), 0);
		hitPoints = Math.max(config.getInt("points.hit", 1), 0);
		killPoints = Math.max(config.getInt("points.kill", 2), 0);
		flagGrabPoints = Math.max(config.getInt("points.flagGrab", 5), 0);
		flagPickPoints = Math.max(config.getInt("points.flagPickup", 5), 0);
		flagDropPoints = Math.max(config.getInt("points.flagDrop", 5), 0);
		flagCapPoints = Math.max(config.getInt("points.flagCapture", 15), 0);
		flagRetPoints = Math.max(config.getInt("points.flagReturn", 5), 0);
		matchWinPoints = Math.max(config.getInt("points.matchWin", 10), 0);
		matchPartPoints = Math.max(config.getInt("points.matchParticipation", 5), 0);
		hitCurr = Math.max(config.getInt("currency.distribution.hit", 1), 0);
		killCurr = Math.max(config.getInt("currency.distribution.kill", 2), 0);
		flagGrabCurr = Math.max(config.getInt("currency.distribution.flagGrab", 5), 0);
		flagPickCurr = Math.max(config.getInt("currency.distribution.flagPickup", 5), 0);
		flagDropCurr = Math.max(config.getInt("currency.distribution.flagDrop", 5), 0);
		flagCapCurr = Math.max(config.getInt("currency.distribution.flagCapture", 15), 0);
		flagRetCurr = Math.max(config.getInt("currency.distribution.flagReturn", 5), 0);
		matchWinCurr = Math.max(config.getInt("currency.distribution.matchWin", 10), 0);
		matchPartCurr = Math.max(config.getInt("currency.distribution.matchParticipation", 5), 0);
		spawnProtectionTime = Math.max(config.getInt("general.spawnProtection", 5), 0);
		enableVoting = config.getBoolean("general.enableVoting", true);
		votingPermission = config.getBoolean("general.votingPermission", false);
		votingRandomArena = config.getBoolean("general.enableRandomArena", true);
		votingArenaCount = Math.max(Math.min(config.getInt("general.votingArenaCount", 4), 8), 2);
		announcePurchases = config.getBoolean("currency.announcePurchase", true);
		enableRanks = config.getBoolean("ranks.enabled", true);
		rankPrefixColor = config.getString("ranks.chatPrefx", "&a");
		rankPrefixColor = ChatColor.translateAlternateColorCodes('&', rankPrefixColor);
		rankPrefix = config.getBoolean("ranks.useChatPrefix", true);
		colorText = config.getBoolean("general.colorText", true);
		enableTokens = config.getBoolean("general.enableTokens", true);
		neededTokens = Math.max(config.getInt("general.neededTokens", 1), 1);
		enableKillstreaks = config.getBoolean("killstreaks.enabled", true);
		ksKills = config.getInt("killstreaks.killsRequired", 7);
		autoJoin = config.getBoolean("general.autoJoin", true);
		reloadJoin = config.getBoolean("general.joinOnReload", true);
		pbWorld = config.getBoolean("general.paintballWorld", false);
		maxCtfCaps = config.getInt("gamemodes.CTF.maxCaptures", -1);
		maxTdmKills = config.getInt("gamemodes.TDM.maxKills", -1);
		maxRtfCaps = config.getInt("gamemodes.RTF.maxCaptures" , -1);
		rtfTime = config.getInt("gamemodes.RTF.time" , 300);
		rtfDeathbox = config.getBoolean("gamemodes.RTF.deathBox" , true);
		rtfDeathTime = config.getInt("gamemodes.RTF.deathTime" , 15);
		maxRtfCaps = config.getInt("gamemodes.RTF.maxCaptures" , -1);
		rtfDeathTimers = config.getBoolean("gamemodes.RTF.useTimers");
		useVotingBlock = config.getBoolean("general.useVotingBlock", true);
		updateInv = config.getBoolean("general.updateInv", false);
		useOther = config.getBoolean("general.useOther", true);
		flagHats = config.getBoolean("general.flagHats", true);
		cmdPrefixes = config.getBoolean("general.useCommandPrefixes", false);
		ctfHits = Math.max(config.getInt("gamemodes.CTF.hits", 3), 1);
		tdmHits = Math.max(config.getInt("gamemodes.TDM.hits", 3), 1);
		rtfHits = Math.max(config.getInt("gamemodes.RTF.hits", 3), 1);
		elmHits = Math.max(config.getInt("gamemodes.ELM.hits", 3), 1);
		elmDeathbox = config.getBoolean("gamemodes.ELM.deathBox", false);
		elmDeathTime = Math.max(config.getInt("gamemodes.ELM.deathTime", 15), 1);
		elmDeathTimers = config.getBoolean("gamemodes.ELM.useTimers", false);
		elmDeaths = Math.max(config.getInt("gamemodes.ELM.maxDeaths", 3), 1);
		elmTime = Math.max(config.getInt("gamemodes.ELM.time", 300), 30);
		sqlDebug = config.getBoolean("general.sqlDebug", false);
		redTeamColor = ChatColor.translateAlternateColorCodes('&',config.getString("general.teamColors.red", "&4"));
		blueTeamColor = ChatColor.translateAlternateColorCodes('&',config.getString("general.teamColors.blue", "&1"));
		deadTeamColor = ChatColor.translateAlternateColorCodes('&',config.getString("general.teamColors.dead", "&8"));
		allowHandSwap = config.getBoolean("general.handSwap", false);
		updatingMOTD = config.getBoolean("motd.enabled", false);
		loadSound(config.getConfigurationSection("weapons.paintballSound"));
	}
	private void setSpawn(){
		try{
			double x = Configuration.getArenaConfig().getDouble("spawn.x");
			double y = Configuration.getArenaConfig().getDouble("spawn.y");
			double z = Configuration.getArenaConfig().getDouble("spawn.z");
			float yaw = (float) Configuration.getArenaConfig().getDouble("spawn.yaw");
			float pitch = (float) Configuration.getArenaConfig().getDouble("spawn.pitch");
			World world = Bukkit.getWorld(Configuration.getArenaConfig().getString("spawn.world"));
			Utils.setLobbySpawn(new Location(world, x, y, z, yaw, pitch));
		}catch(Exception e){
			errors.add("Spawn not enabled.");
		}
	}
	private void loadSound(ConfigurationSection section){
		if(section != null){
			InteractionListener.setSound(section.getString("sound"), (float) section.getDouble("volume"), (float) section.getDouble("pitch"));
		}else{
			InteractionListener.setSound(null, 0, 0);
		}
	}
	public World getWorld(){
		return Bukkit.getWorld(Configuration.getArenaConfig().getString("spawn.world"));
	}
	public void joinPlayers(){
		Collection<? extends Player> players = null;
		if(Main.pbWorld){
			players = getWorld().getPlayers();
		}else{
			players = Bukkit.getOnlinePlayers();
		}
		if(players.size() >= Main.minPlayers){
			GameTimer.startCountdown(this);
		}else{
			for(Player player : players){
				player.sendMessage(MessageHandler.getMessage("notEnoughPlayers"));
			}
		}
		if(Utils.getLobbySpawn() == null){
			Bukkit.broadcastMessage(MessageHandler.getMessage("noLobbySpawn"));
			return;
		}	
		for(Player player : players){
			if(player.getWalkSpeed() != 0.2f){
				player.setWalkSpeed(0.2f);
			}
			Bukkit.getScheduler().runTask(main, new Runnable() {
				@Override
				public void run() {
					player.teleport(Utils.getLobbySpawn());
				}
			});
			Utils.getPlayers().add(player.getUniqueId());
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(20d);
			player.setFoodLevel(20);
			player.setLevel(0);
			player.setExp(1f);
			TokenHandler.handleJoin(player);
			if(Main.enableVoting){
				VoteHandler.setVotingBoard(player);
			}else{
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			Utils.removeArmor(player);
			InventoryEditor.handlePlayerJoin(player, 0, this);
		}
		updateKits(this);
	}
	private void updateKits(Main main){
		Map<UUID, String> kits = new HashMap<UUID, String>();
		Map<UUID, Integer> points = new HashMap<UUID, Integer>();
		for(UUID id : Utils.getPlayers()){
			kits.put(id, null);
		}
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(!MySQL.hasValidConnection()){
					MySQL.connectToDatabase();
				}
				for(UUID id : kits.keySet()){
					points.put(id, MySQL.getInt(id.toString(), "points"));
					kits.put(id, MySQL.getString(id.toString(), "kit"));
				}
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						for(UUID id : kits.keySet()){
							Player player = Bukkit.getPlayer(id);
							String kit = kits.get(id);
							if(kit.toLowerCase().equals("none")){
								Kit.getKits().put(id, KitType.valueOf(kit.toUpperCase()));
								player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", MessageHandler.getMessage("noKit")));
								continue;
							}else if(!Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled")){
								Kit.getKits().put(id, KitType.NONE);
								player.sendMessage(MessageHandler.getMessage("kitRegisReset"));
								continue;
							}else if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".permissionRequired")){
								if(!player.hasPermission("paintball.kit." + kit.toLowerCase())){
									Kit.getKits().put(id, KitType.NONE);
									player.sendMessage(MessageHandler.getMessage("kitRegisNoPerm"));
									continue;
								}
							}
							Kit.getKits().put(id, KitType.valueOf(kit.toUpperCase()));
							String kitTitle = kit;
							String initChar = kitTitle.substring(0, 1);
							kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
							if(points.containsKey(player.getUniqueId())){
								RankHandler.handleJoin(player, points.get(player.getUniqueId()));
							}else{
								RankHandler.handleJoin(player, 0);
								player.sendMessage(MessageHandler.getMessage("pointIssue"));
							}
							player.sendMessage(MessageHandler.getMessage("kitRegis", "%kit%", kitTitle));
						}
					}
				});
			}
		});
	}
}
