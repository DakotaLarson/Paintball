package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.commands.DeathMessageHandler;
import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

public class DeathManager {
	
	private static Random random = new Random();
	private static Set<UUID> players = new HashSet<UUID>();
	private static Map<UUID, Integer> playerTimes = new HashMap<UUID, Integer>();
	private static Map<UUID, Integer> hits = new HashMap<UUID, Integer>();
	private static Set<Player> respawning = new HashSet<Player>();
	private static Set<UUID> spawnProtected = new HashSet<UUID>();
	private static Map<UUID, Integer> totalDeaths = new HashMap<UUID, Integer>();
	private static Set<UUID> deadPlayers = new HashSet<UUID>();
	
	private static int task = 0;
	public static int playerHits = 0;

	private static boolean useBox = false;
	private static boolean individualTimers = false;
	private static boolean useTotal = false;
	
	public static void damage(Player player, Player damager, String data, Main main, boolean isPaintball){
		if(hits.containsKey(player.getUniqueId()) == false){
			return;
		}
		int playerHits = hits.get(player.getUniqueId());
		bleed(player);
		if(playerHits == 1){
			hits.put(player.getUniqueId(), 0);
			player.setExp(0f);
			ScoreboardHandler.handleHit(damager, player);
			handleDeath(player, damager, Arena.getGameMode(), data, false, main);
		}else{
			hits.put(player.getUniqueId(), playerHits -1);
			player.setExp(((float) hits.get(player.getUniqueId())) / playerHits);
			ScoreboardHandler.handleHit(damager, player);
			ArmorHandler.damage(player);
			sendDamageMessages(damager, player, isPaintball, false);
		}
	}
	public static void handleDeath(Player player, Player killer, GameMode mode, String data, boolean isFull, Main main){
		int kills = player.getLevel();
		if(kills == 1){
			player.sendMessage(MessageHandler.getMessage("endedWithKill", "%kills%", "" + kills));
		}else{
			player.sendMessage(MessageHandler.getMessage("endedWithKills", "%kills%", "" + kills));
		}
		KillstreakHandler.handleKill(killer, player);
		DeathMessageHandler.sendDeathMessage(player, killer, data, main);
		spawnFW(player.getLocation(), Utils.getRed().contains(player.getUniqueId()));
		hits.put(player.getUniqueId(), playerHits);
		Kit.changeKit(player, main);
		InventoryEditor.handleDeath(player);
		SniperKit.handleDeath(player);
		SprayerKit.handleDeath(player);
		BouncerKit.handleDeath(player);
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		if(useBox){
			ArmorHandler.kill(player);
			TokenHandler.handleDeath(player);
		}
		sendDamageMessages(killer, player, false, true);
		if(mode == GameMode.CTF){
			CTFFlagHandler.dropFlag(player, main);
			placePlayer(player, killer, isFull, Main.ctfDeathTime);
		}else if(mode == GameMode.RTF){
			RTFFlagHandler.dropFlag(player, main);
			placePlayer(player, killer, isFull, Main.rtfDeathTime);
		}else if(mode == GameMode.TDM){
			placePlayer(player, killer, isFull, Main.tdmDeathTime);
		}else if(mode == GameMode.ELM){
			placePlayer(player, killer, isFull, Main.elmDeathTime);
		}
	}
	private static void placePlayer(Player player, Player killer, boolean isFull, int time){
		if(useTotal){
			if(totalDeaths.containsKey(player.getUniqueId())){
				totalDeaths.put(player.getUniqueId(), totalDeaths.get(player.getUniqueId()) + 1);
				if(Arena.getGameMode() == GameMode.ELM){
					int deaths = totalDeaths.get(player.getUniqueId());
					if(deaths >= Main.elmDeaths){
						ScoreboardHandler.handleKill(player, killer, isFull, killer == null);
						Bukkit.getScheduler().runTask(Main.main, new Runnable() {
							@Override
							public void run() {
								player.teleport(Arena.getDeathSpawn());
							}
						});
						handleFinalDeath(player);
						return;
					}else{
						String count = "" + (Main.elmDeaths - deaths - 1);
						if(count.equals("1")){
							player.sendMessage(MessageHandler.getMessage("elmDeath1", "%count%", count));
						}else{
							player.sendMessage(MessageHandler.getMessage("elmDeath", "%count%", count));
						}
					}
				}
			}else{
				Bukkit.broadcastMessage(player.getName() + " was not registered for total deaths.");
			}
		}
		if(useBox){
			if(individualTimers){
				playerTimes.put(player.getUniqueId(), time);
				Bukkit.getScheduler().runTask(Main.main, new Runnable() {
					@Override
					public void run() {
						player.teleport(Arena.getDeathSpawn());
					}
				});
				ScoreboardHandler.handleKill(player, killer, isFull, killer == null);
			}else{
				players.add(player.getUniqueId());
				Bukkit.getScheduler().runTask(Main.main, new Runnable() {
					@Override
					public void run() {
						player.teleport(Arena.getDeathSpawn());
					}
				});
				ScoreboardHandler.handleKill(player, killer, isFull, killer == null);
			}
		}else{
			ScoreboardHandler.handleKill(player, killer, isFull, killer == null);
			respawn(player, true, Main.main);
		}
	}
	private static void handleFinalDeath(Player player){
		ArmorHandler.respawn(player);
		deadPlayers.add(player.getUniqueId());
		ScoreboardHandler.handleElm(player);
		InventoryEditor.handleElm(player);
		player.sendMessage(MessageHandler.getMessage("elmFinalDeath"));
		if(Utils.getRed().contains(player.getUniqueId())){
			if(deadPlayers.containsAll(Utils.getRed())){
				GameTimer.forceEndTimer(2, Main.main, true);
				Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
					@Override
					public void run() {
						String msg = MessageHandler.getMessage("elmMatchEnd");
						for(UUID id : Utils.getPlayers()){
							Bukkit.getPlayer(id).sendMessage(msg);
						}
					}
				}, 5l);
			}
		}else if(deadPlayers.containsAll(Utils.getBlue())){
			GameTimer.forceEndTimer(2, Main.main, true);
			Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
				@Override
				public void run() {
					String msg = MessageHandler.getMessage("elmMatchEnd1");
					for(UUID id : Utils.getPlayers()){
						Bukkit.getPlayer(id).sendMessage(msg);
					}
				}
			}, 5l);
		}
	}
	private static void initBox(int hitCount, boolean useDeathBox, boolean useTimers, final int totalTime, boolean useTotalDeaths){
		for(UUID id : Utils.getPlayers()){
			playerHits = hitCount;
			hits.put(id, hitCount);
			if(useTotalDeaths){
				useTotal = true;
				totalDeaths.put(id, 0);
			}else{
				useTotal = false;
			}
		}
		if(useDeathBox == true){
			useBox = true;
			if(useTimers){
				individualTimers = true;
				task = Bukkit.getScheduler().runTaskTimer(Main.main, new Runnable(){
					@Override
					public void run() {
						for(UUID id: playerTimes.keySet()){
							int time = playerTimes.get(id) - 1;
							if(time <= 0){
								respawning.add(Bukkit.getPlayer(id));
							}else{
								playerTimes.put(id, time);
								Player p = Bukkit.getPlayer(id);
								p.setLevel(time);
								p.setExp((float) time / totalTime);
								if(time < 5 || time % 5 == 0){
									p.sendMessage(MessageHandler.getMessage("respawnTimeLeft", "%time%", "" + time));
								}
							}
						}
						if(respawning.isEmpty() == false){
							for(Player player : respawning){
								playerTimes.remove(player.getUniqueId());
								respawn(player, false, Main.main);
							}
							respawning.clear();
						}
					}

				}, 20l, 20l).getTaskId();
			}else{
				individualTimers = false;
				task = Bukkit.getScheduler().runTaskTimer(Main.main, new Runnable() {
					int time = totalTime;
					@Override
					public void run() {
						time --;
						if(time <= 0){
							for (UUID id : players){
								Player player = Bukkit.getPlayer(id);
								respawn(player, false, Main.main);
							}
							players.clear();
							time = totalTime;
							return;
						}else{
							for(UUID id: players){
								Player p = Bukkit.getPlayer(id);
								p.setLevel(time);
								p.setExp((float)time / totalTime);
								if(time < 5 || time % 5 == 0){
									p.sendMessage(MessageHandler.getMessage("respawnTimeLeft", "%time%", "" + time));
								}
							}
						}
					}
				}, 20l, 20l).getTaskId();
			}
		}else{
			useBox = false;
		}
	}
	public static void startDeathBox(Main main, GameMode gamemode){
		if(gamemode == GameMode.TDM){
			initBox(Main.tdmHits, Main.tdmDeathbox, Main.tdmDeathTimers, Main.tdmDeathTime, gamemode == GameMode.ELM);
		}else if(gamemode == GameMode.CTF){
			initBox(Main.ctfHits, Main.ctfDeathbox, Main.ctfDeathTimers, Main.ctfDeathTime, gamemode == GameMode.ELM);
		}else if(gamemode == GameMode.RTF){
			initBox(Main.rtfHits, Main.rtfDeathbox, Main.rtfDeathTimers, Main.rtfDeathTime, gamemode == GameMode.ELM);
		}else if(gamemode == GameMode.ELM){
			initBox(Main.elmHits, Main.elmDeathbox, Main.elmDeathTimers, Main.elmDeathTime, gamemode == GameMode.ELM);
		}
	}
	private static void respawn(Player player, boolean isInstant, Main main){
		if(isInstant == false){
			if(GameTimer.getTimeLeft() <= 1){
				player.sendMessage(MessageHandler.getMessage("noRespawnTimeLeft"));
				return;
			}
		}
		player.getInventory().setItem(4, null);
		NMS.resetTitle(player);
		NMS.sendActionBar(player, MessageHandler.getMessage("respawnNote") , 5, 30, 5);
		ArmorHandler.respawn(player);
		hits.put(player.getUniqueId(), playerHits);
		player.setLevel(0);
		player.setExp(1f);
		ScoreboardHandler.handleRespawn(player);
		if(Utils.getRed().contains(player.getUniqueId())){
			Location loc = Arena.getRedSpawns().get(random.nextInt(Arena.getRedSpawns().size()));
			Bukkit.getScheduler().runTask(Main.main, new Runnable() {
				@Override
				public void run() {
					player.teleport(loc);
				}
			});
			if(isInstant){
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						InventoryEditor.handleRespawn(player);
					}
				}, 10l);
			}else{
				InventoryEditor.handleRespawn(player);
			}
		}else{
			Location loc = Arena.getBlueSpawns().get(random.nextInt(Arena.getBlueSpawns().size()));
			Bukkit.getScheduler().runTask(Main.main, new Runnable() {
				@Override
				public void run() {
					player.teleport(loc);
				}
			});
			if(isInstant){
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						InventoryEditor.handleRespawn(player);
					}
				}, 10l);
			}else{
				InventoryEditor.handleRespawn(player);
			}
		}
		player.playSound(player.getEyeLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.0F);
		if(Main.spawnProtectionTime > 0){
			final UUID id = player.getUniqueId();
			spawnProtected.add(id);
			player.sendMessage(MessageHandler.getMessage("protectedForTime", "%time%", "" + Main.spawnProtectionTime));
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					spawnProtected.remove(id);
					Player player = Bukkit.getPlayer(id);
					if(player != null){
						player.sendMessage(MessageHandler.getMessage("noMoreProt"));
					}
				}
			}, Main.spawnProtectionTime * 20);
		}
	}
	private static void sendDamageMessages(Player damager, Player player, boolean isPaintball, boolean isKill){
		if(damager == null){
			player.sendMessage(MessageHandler.getMessage("fellToDeath"));
			return;
		}
		int hitsNeeded = playerHits - hits.get(player.getUniqueId());
		if(isKill){
			damager.sendMessage(MessageHandler.getMessage("killedPlayer", "%player%", Utils.getBoldedColoredName(player)));
			player.sendMessage(MessageHandler.getMessage("killedByPlayer", "%player%", Utils.getBoldedColoredName(damager)));
			damager.playSound(damager.getEyeLocation(), Sound.BLOCK_METAL_BREAK, 2.0F, 0.0F);
		    player.playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0F, 0.0F);
		}else{
			if(isPaintball){
				player.sendMessage(MessageHandler.getMessage("hitByPlayer", "%player%", Utils.getColoredName(damager)
						, "%hitsNeeded%", "" + hitsNeeded, "%hits%", "" + playerHits));
				damager.sendMessage(MessageHandler.getMessage("hitPlayer", "%player%", Utils.getColoredName(player)
						, "%hitsNeeded%", "" + hitsNeeded, "%hits%", "" + playerHits));
			}else{
				player.sendMessage(MessageHandler.getMessage("damagedByPlayer", "%player%", Utils.getColoredName(damager)
						, "%hitsNeeded%", "" + hitsNeeded, "%hits%", "" + playerHits));
				damager.sendMessage(MessageHandler.getMessage("damagedPlayer", "%player%", Utils.getColoredName(player)
						, "%hitsNeeded%", "" + hitsNeeded, "%hits%", "" + playerHits));
			}
			damager.playSound(damager.getEyeLocation(), Sound.BLOCK_METAL_BREAK, 2.0F, 1.0F);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_HURT, 2.0F, 1.0F);
		}
	}
	public static void handleMatchEnd(){
		if(task != 0){
			Bukkit.getScheduler().cancelTask(task);
			task = 0;
		}
		players.clear();
		playerTimes.clear();
		respawning.clear();
		hits.clear();
		totalDeaths.clear();
		deadPlayers.clear();
	}
	public static void handleLeave(Player player){
		UUID id = player.getUniqueId();
		players.remove(id);
		hits.remove(id);
		playerTimes.remove(id);
		respawning.remove(player);
		totalDeaths.clear();
	}
	private static void spawnFW(Location location, boolean isRed){
		if(Main.firework == false) return;
		if(Main.useNMSCode){
			if(location.getWorld().getTime() > 12000){
				if(isRed){
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build();
					NMS.spawnFirework(location, effect);
				}else{
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.WHITE).build();
					NMS.spawnFirework(location, effect);
				}
			}else{
				if(isRed){
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.BLACK).build();
					NMS.spawnFirework(location, effect);
				}else{
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.BLACK).build();
					NMS.spawnFirework(location, effect);
				}
			}
		}else{
			if(location.getWorld().getTime() > 12000){
				if(isRed){
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build();
					Firework firework = location.getWorld().spawn(location, Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(effect);
					meta.setPower(1);
					firework.setFireworkMeta(meta);
				}else{
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.WHITE).build();
					Firework firework = location.getWorld().spawn(location, Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(effect);
					meta.setPower(1);
					firework.setFireworkMeta(meta);
				}
			}else{
				if(isRed){
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.BLACK).build();
					Firework firework = location.getWorld().spawn(location, Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(effect);
					meta.setPower(1);
					firework.setFireworkMeta(meta);
				}else{
					FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).withColor(Color.BLACK).build();
					Firework firework = location.getWorld().spawn(location, Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(effect);
					meta.setPower(1);
					firework.setFireworkMeta(meta);
				}
			}
		}
	}
	private static void bleed(Player player){
		if(Main.bleed == false) return;
		if(Utils.getRed().contains(player.getUniqueId())){
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 152);
		}else{
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 22);
		}
	}
	public static void addEntry(UUID uuid){
		hits.put(uuid, playerHits);
		if(useTotal){
			totalDeaths.put(uuid, 0);
		}
	}
	public static boolean isPlayerRespawning(Player player){
		if(useBox){
			if(individualTimers){
				return playerTimes.containsKey(player.getUniqueId());
			}else{
				return players.contains(player.getUniqueId());
			}
		}else{
			return false;
		}
	}
	public static void forceRespawn(Player player){
		if(isPlayerRespawning(player)){
			if(useBox){
				if(individualTimers){
					playerTimes.remove(player.getUniqueId());
				}else{
					players.remove(player.getUniqueId());
				}
				respawn(player, false, Main.main);
			}
		}
	}
	public static int getHits(Player player){
		return hits.get(player.getUniqueId());
	}
	public static boolean usingBox(){
		return useBox;
	}
	public static boolean isSpawnProtected(Player player){
		return spawnProtected.contains(player.getUniqueId());
	}
	public static Map<UUID, Integer> getHits(){
		return hits;
	}
	public static int getPlayersSize(){
		return players.size();
	}
	public static int getPlayerTimesSize(){
		return playerTimes.size();
	}
	public static int getHitsSize(){
		return hits.size();
	}
	public static int getRespawningSize(){
		return respawning.size();
	}
	public static int getProtectedSize(){
		return spawnProtected.size();
	}
	public static boolean isEliminated(Player player){
		return deadPlayers.contains(player.getUniqueId());
	}
}
