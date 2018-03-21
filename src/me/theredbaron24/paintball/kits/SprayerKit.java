package me.theredbaron24.paintball.kits;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


public class SprayerKit extends Kit{
	
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
							player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 2.0f, 2.0f);
							player.sendMessage(MessageHandler.getMessage("sprayerOffCool"));
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
			}finally{
				task = 0;
			}
		}
		cooldown.clear();
	}
	public static void shoot(Player player, Main main){
		if(cooldown.containsKey(player.getUniqueId())){
			player.sendMessage(MessageHandler.getMessage("sprayerOnCool"));
			Utils.playNoAmmo(player);			
			return;
		}else{
			if(enhancements.containsKey(player.getUniqueId())){
				cooldown.put(player.getUniqueId(), enhancements.get(player.getUniqueId()));
			}else{
				cooldown.put(player.getUniqueId(), sprayerCool);
			}
			player.playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);
			player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
			ItemStack item = player.getInventory().getItemInMainHand();
			item.setAmount(Math.min(64, cooldown.get(player.getUniqueId())));
			player.getInventory().setItemInMainHand(item);
			for(int i = 0; i < sprayerSize; i++){
				Snowball snowball = player.launchProjectile(Snowball.class);
				Vector vector = new Vector(player.getLocation().getDirection().getX() + (Math.random() - 0.45D) / sprayerSpread, 
				player.getLocation().getDirection().getY() + (Math.random() - 0.45D) / sprayerSpread, 
		        player.getLocation().getDirection().getZ() + (Math.random() - 0.45D) / sprayerSpread).normalize();
				snowball.setVelocity(vector.multiply(sprayerSpeed));
				snowball.setMetadata(KitType.SPRAYER.name(), new FixedMetadataValue(main, snowball));
			}
			return;
		}
	}
	public static void handleDeath(Player player){
		cooldown.remove(player.getUniqueId());
		enhancements.remove(player.getUniqueId());
	}
	public static void endCooldown(Player player){
		if(cooldown.remove(player.getUniqueId()) != null){
			ItemStack item = player.getInventory().getItem(2);
			item.setAmount(1);
			player.getInventory().setItem(2, item);
		}
	}
	public static int getSize(){
		return cooldown.size();
	}
	public static void enhance(Player player, int time){
		enhancements.put(player.getUniqueId(), time);
	}
}
