package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.kits.ReloaderKit;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class ReloadHandler {

	private static Map<UUID, Integer> reloaders = new HashMap<UUID, Integer>();
	
	public static void reload(Player player, Main main){
		if(reloaders.containsKey(player.getUniqueId())){
			player.sendMessage(MessageHandler.getMessage("alreadyReloading"));
			return;
		}else if(DeathManager.isPlayerRespawning(player)) return;
		player.getInventory().remove(Material.SNOW_BALL);
		player.getInventory().setItem(0, null);
		if(ReloaderKit.reload(player.getUniqueId())){
			BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
				int seconds = ReloaderKit.getTime();
				@Override
				public void run() {
					if(seconds > 0){
						if(seconds <= 5 || seconds % 5 == 0){
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_HAT, 1f, 1f);
							player.sendMessage(MessageHandler.getMessage("reloadTime", "%time%", "" + seconds));
						}	
					}else{
						loadItems(player);
						player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 2.0F, 2.0F);
						player.sendMessage(MessageHandler.getMessage("reloadComplete"));
						Bukkit.getScheduler().cancelTask(reloaders.get(player.getUniqueId()));
						reloaders.remove(player.getUniqueId());
					}
				seconds --;
				}
			}, 0l, 20l);
			reloaders.put(player.getUniqueId(), task.getTaskId());
		}else{
			BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
				int seconds = Main.reloadTime;
				@Override
				public void run() {
					if(seconds > 0){
						if(seconds <= 5 || seconds % 5 == 0){
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_HAT, 1f, 1f);
							player.sendMessage(MessageHandler.getMessage("reloadTime", "%time%", "" + seconds));
						}	
					}else{
						loadItems(player);
						player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, 2.0F, 2.0F);
						player.sendMessage(MessageHandler.getMessage("reloadComplete"));
						Bukkit.getScheduler().cancelTask(reloaders.get(player.getUniqueId()));
						reloaders.remove(player.getUniqueId());
					}
					seconds --;
				}
			}, 0l, 20l);
			reloaders.put(player.getUniqueId(), task.getTaskId());
		}
		
	}
	public static void handleMatchEnd(){
		for(int task : reloaders.values()){
			Bukkit.getScheduler().cancelTask(task);
		}
		reloaders.clear();
	}
	private static void loadItems(Player player){
		Utils.addSnowballs(Main.ammoCount, player, false);
		if(Kit.getKits().get(player.getUniqueId()) == KitType.HEAVY){
			Utils.addSnowballs(Kit.getHeavyCount(), player, true);
		}
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("reloadBarTitle"));
		item.setItemMeta(meta);
		player.getInventory().setItem(0, item);
	}
	public static void cancel(Player player){
		if(reloaders.containsKey(player.getUniqueId())){
			Bukkit.getScheduler().cancelTask(reloaders.get(player.getUniqueId()));
			reloaders.remove(player.getUniqueId());
		}
	}
	public static boolean isReloading(Player player){
		return reloaders.containsKey(player.getUniqueId());
	}
	public static int getSize(){
		return reloaders.size();
	}
}
