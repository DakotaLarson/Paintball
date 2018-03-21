package me.theredbaron24.paintball.kits;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;

public class BlasterKit extends Kit{
	
	private static Set<UUID> shooters = new HashSet<UUID>();

	public static void shoot(Player player, Main main){
		if(shooters.contains(player.getUniqueId())) return;
		if(Utils.removeItem(player, Material.SNOW_BALL, MessageHandler.getMessage("paintballTitle"), blasterInv)){
			long time = 0;
			if(blasterCool > 0){
				shooters.add(player.getUniqueId());
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						shooters.remove(player.getUniqueId());
					}
				}, blasterCool);
			}
			for(int i = 0; i < blasterCount; i++){
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {
					@Override
					public void run() {
						player.playSound(player.getEyeLocation(), Sound.BLOCK_WOOD_BREAK, 2.0F, 1.0F);
						Snowball ball = player.launchProjectile(Snowball.class);
						ball.setVelocity(player.getLocation().getDirection().multiply(blasterSpeed));
						ball.setMetadata(KitType.BLASTER.name(), new FixedMetadataValue(main, ball));
					}
				}, time);
				time += blasterInterval;
				
			}
		}else{
			Utils.playNoAmmo(player);
		}
	}
	public static int getSize(){
		return shooters.size();
	}
}
