package me.theredbaron24.paintball.kits;
 
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.ReloadHandler;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockerKit extends Kit{
	
	private static Set<UUID> blocked = new HashSet<UUID>();
	
	public static Set<UUID> getBlocked(){
		return blocked;
	}

	public static void handleClick(Player player, Main main){
		if(blocked.contains(player.getUniqueId())){
			player.sendMessage(MessageHandler.getMessage("blockerAlreadyProt"));
			Utils.playNoAmmo(player);
		}else{
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item.getAmount() == 1){
				player.getInventory().setItemInMainHand(null);
			}else{
				item.setAmount(item.getAmount() -1);
				player.getInventory().setItemInMainHand(item);
			}
			blocked.add(player.getUniqueId());
			if(blockerReload){
				if(ReloadHandler.isReloading(player) == false){
					ReloadHandler.reload(player, main);
				}
			}
			player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2.0F, 1.0F);
			player.sendMessage(MessageHandler.getMessage("blockerProt", "%time%", "" + blockerTime));
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					blocked.remove(player.getUniqueId());
					player.sendMessage(MessageHandler.getMessage("blockerEndProt"));
				}
			}, 20 * blockerTime);
		}
	}
	public static void removeBlockedStatus(Player player){
		if(blocked.contains(player.getUniqueId())){
			blocked.remove(player.getUniqueId());
		}
	}
	public static boolean protectsAgainstProj(){
		return blockerProj;
	}
	public static boolean shiftOverride(){
		return blockerShift;
	}
	public static boolean protectsAgainstPunch(){
		return blockerPunch;
	}
	public static int getSize(){
		return blocked.size();
	}
	public static boolean canShoot(Player player){
		if(blockerDisablePB){
			if(blocked.contains(player.getUniqueId())){
				return false;
			}
		}
		return true;
	}
}
