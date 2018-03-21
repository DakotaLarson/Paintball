package me.theredbaron24.paintball.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Kit {

	private static Map<UUID, KitType> kits = new HashMap<UUID, KitType>();
	private static Map<UUID, KitType> kitChanges = new HashMap<UUID, KitType>();
	
	protected static double dasherMulti = 0;
	protected static boolean dasherSpeedPot = false;
	protected static boolean dasherJumpPot = false;
	protected static int dasherSpeedTime = 0;
	protected static int dasherJumpTime = 0;
	protected static int dasherSpeedAmp = 0;
	protected static int dasherJumpAmp = 0;
	protected static int dasherJumpCount = 0;
	protected static int dasherSpeedCount = 0;
	protected static double grenadierSpeed = 0;
	protected static int grenadierCount = 0;
	protected static double grenadierRadius = 0;
	protected static int grenadierBallCount = 0;
	protected static double grenadierBallSpeed = 0;
	protected static double sprayerSpeed = 0;
	protected static double sprayerSpread = 0;
	protected static int sprayerCool = 0;
	protected static int sprayerSize = 0;
	protected static int sprayerInv = 0;
	protected static int sniperRange = 0;
	protected static boolean sniperLiquids = false;
	protected static boolean sniperGlass = false;
	protected static boolean sniperDisregardMates = false;
	protected static boolean sniperContinueIter = false;
	protected static boolean sniperZoom = false;
	protected static int sniperCool = 0;
	protected static double bomberSpeed = 0;
	protected static int bomberCount = 0;
	protected static double bomberRadius = 0;
	protected static int bomberBallCount = 0;
	protected static double bomberBallSpeed = 0;
	protected static int demoCount = 0;
	protected static int demoBallCount = 0;
	protected static double demoBallVelocity = 0;
	protected static double demoRadius = 0;
	protected static boolean demoDetonate = false;
	protected static double rocketmanSpeed = 0;
	protected static int rocketmanRocketCount = 0;
	protected static int rocketmanBallCount = 0;
	protected static double rocketmanBallSpeed = 0;
	protected static double rocketmanRadius = 0;
	protected static double gunnerSpeed = 0;
	protected static double gunnerSpread = 0;
	protected static int gunnerSize = 0;
	protected static int gunnerInv = 0;
	protected static int reloaderTime = 0;
	protected static boolean reloaderReload = false;
	protected static int healerCount = 0;
	protected static int healerPoints = 0;
	protected static int healerCurr = 0;
	protected static int heavyCount = 0;
	protected static int blockerCount = 0;
	protected static int blockerTime = 0;
	protected static boolean blockerPunch = false;
	protected static boolean blockerProj = false;
	protected static boolean blockerShift = false;
	protected static int blinderCount = 0;
	protected static double blinderRange = 0;
	protected static int blinderTime = 0;
	protected static double blinderMulti = 0;
	protected static double chargerSpeed = 0;
	protected static boolean chargerSpeedPot = false;
	protected static boolean chargerJumpPot = false;
	protected static int chargerSpeedTime = 0;
	protected static int chargerJumpTime = 0;
	protected static int chargerSpeedAmp = 0;
	protected static int chargerJumpAmp = 0;
	protected static int chargerJumpCount = 0;
	protected static int chargerSpeedCount = 0;
	protected static int juggHits = 0;
	protected static int blasterCount = 0;
	protected static double blasterSpeed = 0;
	protected static int blasterInterval = 0;
	protected static int blasterInv = 0;
	protected static int blasterCool = 0;
	protected static int bouncerCount = 0;
	protected static double bouncerVelocity = 0;
	protected static boolean blockerDisablePB = false;
	protected static boolean blockerReload = false;
	public static Map<UUID, KitType> getKits(){
		return kits;
	}
	public static void initKitChange(Player player, KitType kit, Main main){
		if(Utils.getPlayers().contains(player.getUniqueId()) == false){
			kits.put(player.getUniqueId(), kit);
			String kitTitle = kit.name();
			String initChar = kitTitle.substring(0, 1);
			kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
			player.sendMessage(MessageHandler.getMessage("kitSwitched", "%kit%", kitTitle));
			return;
		}
		if(Utils.getGameStatus() != GameStatus.RUNNING){
			kits.put(player.getUniqueId(), kit);
			String kitTitle = kit.name();
			String initChar = kitTitle.substring(0, 1);
			kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
			player.sendMessage(MessageHandler.getMessage("kitSwitched", "%kit%", kitTitle));
		}else{
			if(DeathManager.isPlayerRespawning(player)){
				kits.put(player.getUniqueId(), kit);
				String kitTitle = kit.name();
				String initChar = kitTitle.substring(0, 1);
				kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
				player.sendMessage(MessageHandler.getMessage("kitSwitched", "%kit%", kitTitle));
			}else{
				kitChanges.put(player.getUniqueId(), kit);
				player.sendMessage(MessageHandler.getMessage("kitWillSwitch"));
			}
		}
		final String uuid = player.getUniqueId().toString();
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(MySQL.hasValidConnection() == false){
					MySQL.connectToDatabase();
				}
				MySQL.setString(uuid, "kit", kit.name().toLowerCase());
			}
		});
		
	}
	public static void changeKit(Player player, Main main){
		if(player.getWalkSpeed() != 0.2f){
			KitType newKit = kitChanges.get(player.getUniqueId());
			if(newKit != KitType.CHARGER && newKit != KitType.DASHER){
				player.setWalkSpeed(0.2f);
			}
		}
		if(kitChanges.containsKey(player.getUniqueId())){
			KitType prevKit = kits.get(player.getUniqueId());
			if(prevKit == KitType.DEMO){
				DemoKit.resetMines(player.getUniqueId(), main);
			}
			else if(prevKit == KitType.RELOADER){
				ReloaderKit.unRegister(player.getUniqueId());
			}
			else if(prevKit == KitType.BOUNCER){
				BouncerKit.handleLeave(player);
			}
			KitType kit = kitChanges.get(player.getUniqueId());
			kits.put(player.getUniqueId(), kit);
			kitChanges.remove(player.getUniqueId());
			String kitTitle = kit.name();
			String initChar = kitTitle.substring(0, 1);
			kitTitle  = initChar.toUpperCase() + kitTitle.substring(1).toLowerCase();
			player.sendMessage(MessageHandler.getMessage("kitLateChange", "%kit%", kitTitle));
		}
	}
	public static void handleLeave(Player player, Main main){
		if(kits.containsKey(player.getUniqueId())){
			kits.remove(player.getUniqueId());
		}if(kitChanges.containsKey(player.getUniqueId())){
			kitChanges.remove(player.getUniqueId());
		}
		SniperKit.handleDeath(player);
		SprayerKit.handleDeath(player);
		DemoKit.resetMines(player.getUniqueId(), main);
		JuggernautKit.unRegister(player.getUniqueId());
		ReloaderKit.unRegister(player.getUniqueId());
		BouncerKit.handleLeave(player);
		player.setWalkSpeed(0.2f);
	}
	public static int getHeavyCount(){
		return heavyCount;
	}
	public static void loadData(ConfigurationSection config){
		dasherMulti = Math.max(Math.min(config.getDouble("dasher.walkspeedMultiplier", 2.5), 10.0), 2.0);
		grenadierSpeed = Math.max(Math.min(config.getDouble("grenadier.speedMultiplier", 2.0), 10.0), 1.0);
		grenadierCount = Math.max(config.getInt("grenadier.grenadeCount", 1), 1);
		grenadierRadius = Math.max(Math.min(config.getDouble("grenadier.radius", 3.0), 25.0), 0.0);
		grenadierBallCount = Math.max(config.getInt("grenadier.ballCount", 10), 0);
		grenadierBallSpeed = Math.max(Math.min(config.getDouble("grenadier.ballVelocity", 2.0), 10.0), 1.0);
		sprayerSpeed = Math.max(Math.min(config.getDouble("sprayer.speedMultiplier", 2.0), 10.0), 1.0);
		sprayerSpread = Math.max(Math.min(config.getDouble("sprayer.groupSpread", 3.0), 10.0), 1.0);
		sprayerCool = Math.max(config.getInt("sprayer.cooldown", 10), 0);
		sprayerSize = Math.max(config.getInt("sprayer.groupSize", 10), 0);
		sprayerInv = Math.max(config.getInt("sprayer.invBallsNeeded", 0), 0);
		sniperRange = Math.max(config.getInt("sniper.range", 150), 10);
		sniperLiquids = config.getBoolean("sniper.shootThroughLiquids", true);
		sniperGlass = config.getBoolean("sniper.shootThroughGlass", true);
		sniperDisregardMates = config.getBoolean("sniper.disregardTeammates", true);
		sniperContinueIter = config.getBoolean("sniper.continueIterAfterHit", true);
		sniperZoom = config.getBoolean("sniper.useZoom", true);
		sniperCool = Math.max(config.getInt("sniper.cooldown", 30), 0);
		bomberSpeed = Math.max(Math.min(config.getDouble("speedMulitplier", 3.0), 10.0), 1.0);
		bomberCount = Math.max(config.getInt("bomber.bombCount", 3), 1);
		bomberRadius = Math.max(Math.min(config.getDouble("bomber.radius", 4.0), 10.0), 0.0);
		bomberBallCount = Math.max(config.getInt("bomber.ballCount", 15), 0);
		bomberBallSpeed = Math.max(Math.min(config.getDouble("bomber.ballVelocity", 2.0), 10.0), 1.0);
		demoCount = Math.max(config.getInt("demo.numberOfMines", 3), 1);
		demoBallCount = Math.max(config.getInt("demo.numberOfBalls", 10), 0);
		demoBallVelocity = Math.max(Math.min(config.getDouble("demo.ballVelocity", 2.0), 10.0), 1.0);
		demoRadius = Math.max(Math.min(config.getDouble("demo.damageRadius", 4), 10.0), 1.0);
		demoDetonate = config.getBoolean("demo.canBeManuallyDetonated", true);
		rocketmanSpeed = Math.max(Math.min(config.getDouble("rocketman.speedMultiplier", 6.0), 10.0), 1.0);
		rocketmanRocketCount = Math.max(config.getInt("rocketman.rocketCount", 3), 1);
		rocketmanBallCount = Math.max(config.getInt("rocketman.ballCount", 30), 0);
		rocketmanRadius = Math.max(Math.min(config.getDouble("rocketman.radius", 5.0), 10.0), 0.0);
		rocketmanBallSpeed = Math.max(Math.min(config.getDouble("rocketman.ballSpeed", 1.0), 10.0), 1.0);
		gunnerSpeed = Math.max(Math.min(config.getDouble("gunner.speedMulitplier", 2.0), 10.0), 1.0);
		gunnerSpread = Math.max(Math.min(config.getDouble("gunner.groupSpread", 3.0), 10.0), 1.0);
		gunnerSize = Math.max(config.getInt("gunner.groupSize", 5), 0);
		gunnerInv = Math.max(config.getInt("gunner.invPaintballsNeeded", 3), 0);
		reloaderTime = Math.max(config.getInt("reloader.reloadTime", 3), 0);
		reloaderReload = config.getBoolean("reloader.reloadAuto", true);
		healerCount = Math.max(config.getInt("healer.healCount", 3), 1);
		healerPoints = Math.max(config.getInt("healer.teammateHealPoints", 10), 0);
		healerCurr = Math.max(config.getInt("healer.teammateHealCurrency", 50), 0);
		heavyCount = Math.max(config.getInt("heavy.paintballAmount", 128), 1);
		blockerCount = Math.max(config.getInt("blocker.blockingActivations", 1), 1);
		blockerTime = Math.max(config.getInt("blocker.blockingTime", 5), 1);
		blockerPunch = config.getBoolean("blocker.blockPunches", false);
		blockerProj = config.getBoolean("blocker.blockOtherProjectiles", false);
		blockerShift = config.getBoolean("blocker.sneakOverride", true);
		blinderCount = Math.max(config.getInt("blinder.blinderCount", 3), 1);
		blinderRange = Math.max(Math.min(config.getDouble("blinder.attackRange", 5.0), 10.0), 0.0);
		blinderTime = Math.max(config.getInt("blinder.attackLength", 5), 1);
		blinderMulti = Math.max(Math.min(config.getDouble("blinder.multiplier", 2.5), 10.0), 1.0);
		chargerSpeed = Math.max(Math.min(config.getDouble("charger.walkspeedMultiplier", 4.0), 10.0), 1.0);
		juggHits = Math.max(config.getInt("juggernaut.hitsForKill", 2), 1);
		blasterCount = Math.max(config.getInt("blaster.ballCount", 3), 1);
		blasterSpeed = Math.max(Math.min(config.getDouble("blaster.ballSpeed", 2.5), 10.0), 1.0);
		blasterInterval = Math.max(config.getInt("blaster.interval", 2), 0);
		blasterInv = Math.max(config.getInt("blaster.invPaintballsNeeded", 2), 0);
		blasterCool = Math.max(config.getInt("blaster.cooldown", blasterInterval * blasterCount), 0);
		bouncerCount = Math.max(Math.min(config.getInt("bouncer.bounces", 1), 25), 1);
		bouncerVelocity = Math.max(Math.min(config.getDouble("bouncer.velocity", 1.5), 25), 1);
		blockerDisablePB = config.getBoolean("blocker.disablePBGun", false);
		blockerReload = config.getBoolean("blocker.reloadOnInit", false);
		dasherSpeedPot = config.getBoolean("dasher.addSpeedPotion", true);
		dasherJumpPot = config.getBoolean("dasher.addJumpPot", false);
		dasherSpeedTime = Math.max(config.getInt("dasher.speedPotTime", 30), 1);
		dasherJumpTime = Math.max(config.getInt("dasher.jumpPotTime", 30), 1);
		dasherSpeedAmp = Math.max(config.getInt("dasher.speedPotAmp", 1), 1);
		dasherJumpAmp = Math.max(config.getInt("dasher.jumpPotAmp", 1), 1);
		dasherJumpCount = Math.max(config.getInt("dasher.jumpPotCount", 1),  1);
		dasherSpeedCount = Math.max(config.getInt("dasher.speedPotCount", 1),  1);
		chargerSpeedPot = config.getBoolean("charger.addSpeedPotion", true);
		chargerJumpPot = config.getBoolean("charger.addJumpPot", true);
		chargerSpeedTime = Math.max(config.getInt("charger.speedPotTime", 30), 1);
		chargerJumpTime = Math.max(config.getInt("charger.jumpPotTime", 30), 1);
		chargerSpeedAmp = Math.max(config.getInt("charger.speedPotAmp", 2), 1);
		chargerJumpAmp = Math.max(config.getInt("charger.jumpPotAmp", 2), 1);
		chargerJumpCount = Math.max(config.getInt("charger.jumpPotCount", 1),  1);
		chargerSpeedCount = Math.max(config.getInt("charger.speedPotCount", 1),  1);

	}
	public static int getChangesSize(){
		return kitChanges.size();
	}
}
