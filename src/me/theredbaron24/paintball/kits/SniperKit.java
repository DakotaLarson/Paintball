package me.theredbaron24.paintball.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.events.ProjectileHitListener;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

public class SniperKit extends Kit{
	
	private static Map<UUID, Integer> enhancements = new HashMap<UUID, Integer>();
	private static Map<UUID, Integer> cooldown = new HashMap<UUID, Integer>();
	private static int task = 0;
	
	public static void init(Main main){
		if(task != 0){
			try{
				Bukkit.getScheduler().cancelTask(task);
			}finally{
				task = 0;
			}
		}
		task = Bukkit.getScheduler().runTaskTimer(main, new Runnable(){
			
			Set<UUID> toBeRemoved = new HashSet<UUID>();
			@Override
			public void run() {
				if(cooldown.isEmpty() == false){
					for(UUID id: cooldown.keySet()){
						int time = cooldown.get(id);
						time --;
						if(time == 0){
							toBeRemoved.add(id);
						}else{
							if(enhancements.containsKey(id)){
								cooldown.put(id, Math.min(time, enhancements.get(id)));
							}else{
								cooldown.put(id, time);
							}
							Inventory inv = Bukkit.getPlayer(id).getInventory();
							ItemStack item = inv.getItem(2);
							item.setAmount(Math.min(64, time));
							inv.setItem(2, item);
						}
					}
					if(toBeRemoved.isEmpty() == false){
						for(UUID id: toBeRemoved){
							cooldown.remove(id);
							Player player = Bukkit.getPlayer(id);
							player.sendMessage(MessageHandler.getMessage("sniperOffCool"));
							player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 2.0f, 2.0f);
							
						}
						toBeRemoved.clear();
					}
				}
			}
			
		}, 20l, 20l).getTaskId();
	}
	
	public static void endTask(){
		if(task != 0){
			try{
				Bukkit.getScheduler().cancelTask(task);
				for(UUID id : Utils.getPlayers()){
					removeZoom(Bukkit.getPlayer(id));
				}
			}finally{
				task = 0;
			}
		}
		cooldown.clear();
		enhancements.clear();
	}
	public static void shoot(Player player, Main main, boolean isRight){
		if(cooldown.containsKey(player.getUniqueId())){
			player.sendMessage(MessageHandler.getMessage("sniperOnCool"));
			Utils.playNoAmmo(player);
			return;
		}
		Block block = player.getEyeLocation().getBlock();
		if(block.isEmpty() == false){
			if(((block.isLiquid() && sniperLiquids) || (isGlass(block.getLocation()) && sniperGlass)) == false){
				player.sendMessage(MessageHandler.getMessage("sniperMove"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.0F);
				return;
			}	
		}
		if(enhancements.containsKey(player.getUniqueId())){
			cooldown.put(player.getUniqueId(), enhancements.get(player.getUniqueId()));
		}else{
			cooldown.put(player.getUniqueId(), sniperCool);
		}
		player.playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);
		player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);
		ItemStack item = player.getInventory().getItemInMainHand();
		item.setAmount(Math.min(64, sniperCool));
		player.getInventory().setItemInMainHand(item);
		List<UUID> players = iterate(player, sniperRange);
		if(players.isEmpty()){
			player.sendMessage(MessageHandler.getMessage("sniperMissedShot", "%time%", getTime(player)));
		}else{
			if(sniperContinueIter == false){
				UUID id = players.get(0);
				if((Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(id)) ||
						(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(id))){
					player.sendMessage(MessageHandler.getMessage("sniperOnTeam", "%player%", Utils.getColoredName(Bukkit.getPlayer(id))));
				}else{
					if(ProjectileHitListener.hitIsValid(Bukkit.getPlayer(id), player, null)){
						DeathManager.handleDeath(Bukkit.getPlayer(id), player, Arena.getGameMode(), KitType.SNIPER.name(), true, main);
					}
				}	
			}else{
				for(UUID id: players){
					if((Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(id)) ||
							(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(id))){
						player.sendMessage(MessageHandler.getMessage("sniperOnTeam", "%player%", Utils.getColoredName(Bukkit.getPlayer(id))));
						if(sniperDisregardMates == false){
							break;
						}
					}else{
						if(ProjectileHitListener.hitIsValid(Bukkit.getPlayer(id), player, null)){
							DeathManager.handleDeath(Bukkit.getPlayer(id), player, Arena.getGameMode(), KitType.SNIPER.name(), true, main);
						}
					}	
				}
			}
		}
	}
	public static void handleDeath(Player player){
		cooldown.remove(player.getUniqueId());
		enhancements.remove(player.getUniqueId());
		removeZoom(player);
	}
	public static void endCooldown(Player player){
		if(cooldown.remove(player.getUniqueId()) != null){
			ItemStack item = player.getInventory().getItem(2);
			item.setAmount(1);
			player.getInventory().setItem(2, item);
		}
		
	}
	private static boolean isGlass(Location location){
		Block block = location.getBlock();
		if(block.isEmpty() || block.isLiquid()) return false;
		Material[] materials = {Material.GLASS, Material.THIN_GLASS, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE};
		for(Material mat : materials){
			if(block.getType() == mat){
				return true;
			}
		}
		return false;
	}
	public static List<UUID> iterate(Player player, int distance){
		List<UUID> players = new ArrayList<UUID>();
		BlockIterator iterator = new BlockIterator(player, distance);
		while(iterator.hasNext()){
			Block block = iterator.next();
			Location loc = block.getLocation().add(0.5, 0.5, 0.5);
			if(block.isLiquid()){
				if(sniperLiquids == false){
					break;
				}
			}else if(block.isEmpty() == false){
				if(isGlass(block.getLocation()) == false){
					break;
				}else if(sniperGlass == false){
					break;
				}
			}
			Firework fw = loc.getWorld().spawn(loc, Firework.class);
			for(Entity e : fw.getNearbyEntities(0.5, 0.5, 0.5)){
				if(e.getType() == EntityType.PLAYER){
					Player p = (Player) e;
					if(p.getUniqueId().equals(player.getUniqueId())) continue;
					if(players.contains(p.getUniqueId()) == false){
						players.add(p.getUniqueId());
					}
				}
			}
			fw.remove();
		}
		return players;
	}
	public static int getSize(){
		return cooldown.size();
	}
	public static void enhance(Player player, int time){
		enhancements.put(player.getUniqueId(), time);
	}
	public static void zoom(Player player){
		if(sniperZoom == false || Kit.getKits().get(player.getUniqueId()) != KitType.SNIPER || Utils.getGameStatus() != GameStatus.RUNNING) return;
		if((player.hasPotionEffect(PotionEffectType.SLOW) && player.hasPotionEffect(PotionEffectType.NIGHT_VISION))== false){
			if(player.getInventory().getItemInMainHand().getType() != Material.GOLD_HOE) return;
			PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 9);
			player.addPotionEffect(effect);
			effect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0);
			player.addPotionEffect(effect);
		}else{
			removeZoom(player);
		}
	}
	private static void removeZoom(Player player){
		if(sniperZoom == false) return;
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
	}
	private static String getTime(Player player){
		if(enhancements.containsKey(player.getUniqueId())){
			return "" + enhancements.get(player.getUniqueId());
		}
		return "" + sniperCool;
	}
}
