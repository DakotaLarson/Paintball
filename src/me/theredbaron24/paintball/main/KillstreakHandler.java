package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.JuggernautKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillstreakHandler {
	
	private static Map<Integer, String> levels = new HashMap<Integer, String>();

	public static void handleKill(Player player, Player killed){
		if(Main.enableKillstreaks == false || Main.ksKills < 0 || player == null) return;
		int killedLevel = killed.getLevel();
		if(killedLevel >= Main.ksKills){
			String message = getDeathMessage(killed);
			if(message == null) return;
			for(UUID id : Utils.getPlayers()){
				Bukkit.getPlayer(id).sendMessage(message);
			}
		}
		int level = player.getLevel() + 1;
		if(levels.containsKey(level)){
			ConfigurationSection section = Configuration.getConfig().getConfigurationSection("killstreaks.values." + levels.get(level));
			List<String> tasks = section.getStringList("kit." + Kit.getKits().get(player.getUniqueId()).name().toLowerCase());
			if(section.getBoolean("general.use") || tasks.isEmpty()){
				tasks.addAll(section.getStringList("general.tasks"));
			}
			runTasks(player, tasks);
		}
	}
	public static void initKillStreaks(){
		ConfigurationSection section = Configuration.getConfig().getConfigurationSection("killstreaks.streaks");
		if(section == null){
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning killstreaks. Please regenerate config.");
			Main.enableKillstreaks = false;
		}else{
			Set<String> streaks = section.getKeys(false);
			if(streaks == null || streaks.isEmpty()){
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning killstreaks.There are none defined.");
				Main.enableKillstreaks = false;
			}else{
				for(String streak : streaks){
					try{
						int i = Integer.parseInt(streak);
						if(levels.containsKey(i)){
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning killstreaks. " + streak + " is used more than once");
						}else{
							String secTitle = Configuration.getConfig().getString("killstreaks.streaks." + streak);
							if(Configuration.getConfig().getConfigurationSection("killstreaks.values." + secTitle) == null){
								Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning killstreaks. " + streak + " does not have actions defined.");
							}else{
								levels.put(i, secTitle);
							}
						}
					}catch(Exception e){
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error concerning killstreaks. " + streak + " is not a number.");
					}
				}
				Main.main.getLogger().info("" + levels.size() + " killstreak(s) found.");
			}
		}
	}
	private static String getDeathMessage(Player player){
		String message = Configuration.getConfig().getString("killstreaks.endOfStreak");
		if(message == null) return null;
		message = message.replaceAll("%cplayer%", Utils.getColoredName(player))
				.replaceAll("%player%", player.getName()).replaceAll("%streak%", "" + player.getLevel());
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	private static void runTasks(Player player, List<String> tasks){
		for(String task : tasks){
			if(task == null || task.equals("")) continue;
			String[] taskArgs = task.split("-");
			switch(taskArgs[0].toLowerCase()){
				case "message":
					sendMessages(taskArgs, player);
					break;
				case "sound":
					playSounds(taskArgs, player);
					break;
				case "token":
					addTokens(taskArgs, player);
					break;
				case "paintball":
					addPBs(taskArgs, player);
					break;
				case "speed":
					effectPlayers(taskArgs, player, 1);
					break;
				case "health":
					healPlayers(taskArgs, player);
					break;
				case "disorient":
					effectPlayers(taskArgs, player, 2);
					break;
				case "blind":
					effectPlayers(taskArgs, player, 3);
					break;
				case "slow":
					effectPlayers(taskArgs, player, 4);
					break;
				case "jump":
					effectPlayers(taskArgs, player, 5);
				case "kit":
					addKitItems(taskArgs, player);
					break;
				default:
					break;
			}
		}
	}
	private static void playSounds(String[] args, Player player){
		if(args.length == 5){
			String title = args[2].toUpperCase();
			Sound sound = null;
			float vol = 0f;
			float pit = 0f;
			try{
				sound = Sound.valueOf(title);
				vol = Float.parseFloat(args[3]);
				pit = Float.parseFloat(args[4]);
			}catch(Exception e){
				return;
			}
			if(sound == null || vol == 0f) return;
			if(args[1].equalsIgnoreCase("player")){
				player.playSound(player.getEyeLocation(), sound, vol, pit);
			}else if(args[1].equalsIgnoreCase("team")){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						p.playSound(p.getEyeLocation(), sound, vol, pit);
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						p.playSound(p.getEyeLocation(), sound, vol, pit);					
					}
				}
			}else if(args[1].equalsIgnoreCase("all")){
				for(UUID id : Utils.getPlayers()){
					Player p = Bukkit.getPlayer(id);
					p.playSound(p.getEyeLocation(), sound, vol, pit);
				}
			}else if(args[1].equalsIgnoreCase("enemy")){
				if(Utils.getBlue().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						p.playSound(p.getEyeLocation(), sound, vol, pit);
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						p.playSound(p.getEyeLocation(), sound, vol, pit);
					}
				}
			}
		}
	}
	private static void sendMessages(String[] args, Player player){
		if(args.length > 2){
			String finalString = args[2];
			if(args.length != 3){
				for(int i = 3; i < args.length; i++){
					finalString += "-" + args[i];
				}
			}
			int streak = player.getLevel() + 1;
			finalString = ChatColor.translateAlternateColorCodes('&', finalString);
			finalString = finalString.replaceAll("%cplayer%", Utils.getColoredName(player))
					.replaceAll("%player%", player.getName()).replaceAll("%streak%", "" + streak);
			if(args[1].equalsIgnoreCase("player")){
				player.sendMessage(finalString);
			}else if(args[1].equalsIgnoreCase("team")){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Bukkit.getPlayer(id).sendMessage(finalString);
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Bukkit.getPlayer(id).sendMessage(finalString);
					}
				}
			}else if(args[1].equalsIgnoreCase("all")){
				for(UUID id : Utils.getPlayers()){
					Bukkit.getPlayer(id).sendMessage(finalString);
				}
			}else if(args[1].equalsIgnoreCase("enemy")){
				if(Utils.getBlue().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Bukkit.getPlayer(id).sendMessage(finalString);
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Bukkit.getPlayer(id).sendMessage(finalString);
					}
				}
			}
		}
	}
	private static void healPlayers(String[] args, Player player){
		if(args.length == 2){
			if(args[1].equalsIgnoreCase("player")){
				if(DeathManager.isPlayerRespawning(player) == false){
					DeathManager.getHits().put(player.getUniqueId(), DeathManager.playerHits);
					ArmorHandler.respawn(player);
					player.setExp(1f);
					ScoreboardHandler.handleHeal(null, player);
				}
			}else if(args[1].equalsIgnoreCase("team")){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							DeathManager.getHits().put(id, DeathManager.playerHits);
							ArmorHandler.respawn(p);
							p.setExp(1f);
							ScoreboardHandler.handleHeal(null, p);
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							if(DeathManager.isPlayerRespawning(p) == false){
								DeathManager.getHits().put(id, DeathManager.playerHits);
								ArmorHandler.respawn(p);
								p.setExp(1f);
								ScoreboardHandler.handleHeal(null, p);
							}
						}
					}
				}
			}else if(args[1].equalsIgnoreCase("all")){
				for(UUID id : Utils.getPlayers()){
					Player p = Bukkit.getPlayer(id);
					if(DeathManager.isPlayerRespawning(p) == false){
						if(DeathManager.isPlayerRespawning(p) == false){
							DeathManager.getHits().put(id, DeathManager.playerHits);
							ArmorHandler.respawn(p);
							p.setExp(1f);
							ScoreboardHandler.handleHeal(null, p);
						}
					}
				}
			}else if(args[1].equalsIgnoreCase("enemy")){
				if(Utils.getBlue().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							if(DeathManager.isPlayerRespawning(p) == false){
								DeathManager.getHits().put(id, DeathManager.playerHits);
								ArmorHandler.respawn(p);
								p.setExp(1f);
								ScoreboardHandler.handleHeal(null, p);
							}
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							if(DeathManager.isPlayerRespawning(p) == false){
								DeathManager.getHits().put(id, DeathManager.playerHits);
								ArmorHandler.respawn(p);
								p.setExp(1f);
								ScoreboardHandler.handleHeal(null, p);
							}
						}
					}
				}
			}
		}
	}
	private static void addTokens(String[] args, Player player){
		if(args.length == 3){
			if(Main.enableTokens){
				int count = 0;
				try{
					count = Integer.parseInt(args[2]);
				}catch(Exception e){
					return;
				}
				if(count <= 0) return;
				if(args[1].equalsIgnoreCase("player")){
					TokenHandler.getTokens().put(player.getUniqueId(), TokenHandler.getTokens().get(player.getUniqueId()) + count);
				}else if(args[1].equalsIgnoreCase("team")){
					if(Utils.getRed().contains(player.getUniqueId())){
						for(UUID id : Utils.getRed()){
							Player p = Bukkit.getPlayer(id);
							TokenHandler.getTokens().put(p.getUniqueId(), TokenHandler.getTokens().get(p.getUniqueId()) + count);
						}
					}else{
						for(UUID id : Utils.getBlue()){
							Player p = Bukkit.getPlayer(id);
							TokenHandler.getTokens().put(p.getUniqueId(), TokenHandler.getTokens().get(p.getUniqueId()) + count);
						}
					}
				}else if(args[1].equalsIgnoreCase("all")){
					for(UUID id : Utils.getPlayers()){
						Player p = Bukkit.getPlayer(id);
						TokenHandler.getTokens().put(p.getUniqueId(), TokenHandler.getTokens().get(p.getUniqueId()) + count);
					}
				}else if(args[1].equalsIgnoreCase("enemy")){
					if(Utils.getBlue().contains(player.getUniqueId())){
						for(UUID id : Utils.getRed()){
							Player p = Bukkit.getPlayer(id);
							TokenHandler.getTokens().put(p.getUniqueId(), TokenHandler.getTokens().get(p.getUniqueId()) + count);
						}
					}else{
						for(UUID id : Utils.getBlue()){
							Player p = Bukkit.getPlayer(id);
							TokenHandler.getTokens().put(p.getUniqueId(), TokenHandler.getTokens().get(p.getUniqueId()) + count);
						}
					}
				}
			}
		}
	}
	private static void addPBs(String[] args, Player player){
		if(args.length == 3){
			int count = 0;
			try{
				count = Integer.parseInt(args[2]);
			}catch(Exception e){
				return;
			}
			if(count <= 0) return;
			if(args[1].equalsIgnoreCase("player")){
				if(DeathManager.isPlayerRespawning(player) == false){
					Utils.addSnowballs(count, player, false);
				}else{

				}
			}else if(args[1].equalsIgnoreCase("team")){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							Utils.addSnowballs(count, p, false);
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							Utils.addSnowballs(count, p, false);
						}
					}
				}
			}else if(args[1].equalsIgnoreCase("all")){
				for(UUID id : Utils.getPlayers()){
					Player p = Bukkit.getPlayer(id);
					if(DeathManager.isPlayerRespawning(p) == false){
						Utils.addSnowballs(count, p, false);
					}
				}
			}else if(args[1].equalsIgnoreCase("enemy")){
				if(Utils.getBlue().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							Utils.addSnowballs(count, p, false);
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							Utils.addSnowballs(count, p, false);
						}
					}
				}
			}
		}
	}
	private static void effectPlayers(String[] args, Player player, int data){
		if(args.length == 4){
			int time = 0;
			int amplifier = 0;
			try{
				time = Integer.parseInt(args[2]) * 20;
				amplifier = Integer.parseInt(args[3]) - 1;
			}catch(Exception e){
				return;
			}
			if(amplifier < 0 || time <= 0) return;
			PotionEffect effect = null;
			if(data == 1){
				effect = new PotionEffect(PotionEffectType.SPEED, time, amplifier);
			}else if(data == 2){
				effect = new PotionEffect(PotionEffectType.CONFUSION, time, amplifier);
			}else if(data == 3){
				effect = new PotionEffect(PotionEffectType.BLINDNESS, time, amplifier);
			}else if(data == 4){
				effect = new PotionEffect(PotionEffectType.SLOW, time, amplifier);
			}else{
				effect = new PotionEffect(PotionEffectType.JUMP, time, amplifier);
			}
			if(args[1].equalsIgnoreCase("player")){
				if(DeathManager.isPlayerRespawning(player) == false){
					player.addPotionEffect(effect);
				}
			}else if(args[1].equalsIgnoreCase("team")){
				if(Utils.getRed().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							p.addPotionEffect(effect);
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							p.addPotionEffect(effect);
						}
					}
				}
			}else if(args[1].equalsIgnoreCase("all")){
				for(UUID id : Utils.getPlayers()){
					Player p = Bukkit.getPlayer(id);
					if(DeathManager.isPlayerRespawning(p) == false){
						p.addPotionEffect(effect);
					}
				}
			}else if(args[1].equalsIgnoreCase("enemy")){
				if(Utils.getBlue().contains(player.getUniqueId())){
					for(UUID id : Utils.getRed()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							p.addPotionEffect(effect);
						}
					}
				}else{
					for(UUID id : Utils.getBlue()){
						Player p = Bukkit.getPlayer(id);
						if(DeathManager.isPlayerRespawning(p) == false){
							p.addPotionEffect(effect);
						}
					}
				}
			}
		}
	}
	private static void addKitItems(String[] args, Player player){
		if(args.length == 2){
			if(DeathManager.isPlayerRespawning(player)) return;
			int count = 0;
			try{
				count = Integer.parseInt(args[1]);
			}catch(Exception e){
				return;
			}
			ItemStack item = null;
			ItemMeta meta = null;
			PlayerInventory inv = player.getInventory();
			switch(Kit.getKits().get(player.getUniqueId())){
				case BLINDER :
					if(count <= 0) return;
					item = new ItemStack(Material.ENDER_PEARL, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("blinderCharge"));
					item.setItemMeta(meta);
					inv.addItem(item);
					break;
				case BLOCKER :
					if(count <= 0) return;
					item = new ItemStack(Material.BLAZE_ROD, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("blockerTitle"));
					item.setItemMeta(meta);
					inv.addItem(item);
					break;
				case BOMBER :
					if(count <= 0) return;
					item = new ItemStack(Material.EGG, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("bombTitle"));
					item.setItemMeta(meta);
					inv.addItem(item);
					break;
				case GRENADIER :
					if(count <= 0) return;
					item = new ItemStack(Material.EGG, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("grenadeTitle"));
					item.setItemMeta(meta);
					inv.addItem(item);
					break;
				case JUGGERNAUT :
					JuggernautKit.respawn(player);
					break;
				case ROCKETMAN :
					if(count <= 0) return;
					item = new ItemStack(Material.FIREBALL, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("rcChargeTitle"));
					item.setItemMeta(meta);
					inv.addItem(item);
					break;
				case SNIPER :
					SniperKit.endCooldown(player);
					if(count > 0){
						SniperKit.enhance(player, count);
					}
					break;
				case SPRAYER :
					SprayerKit.endCooldown(player);
					if(count > 0){
						SprayerKit.enhance(player, count);
					}
					break;
				case HEALER:
					if(count <= 0) return;
					item = new ItemStack(Material.STICK, count);
					meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("rcTitle"));
					item.setItemMeta(meta);
					inv.addItem(item);
				case BOUNCER:
					if(count > 0){
						BouncerKit.upgrade(player, count);
					}
				default :
					break;
			}
		}
	}
}
