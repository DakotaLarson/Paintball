package me.theredbaron24.paintball.kits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlinderKit extends Kit{
	
	private static Set<UUID> blindedPreviously = new HashSet<UUID>();

	public static void shoot(Player player){
		if(Utils.removeItem(player, Material.ENDER_PEARL, MessageHandler.getMessage("blinderCharge"), 1)){
			player.playSound(player.getEyeLocation(), Sound.ENTITY_SILVERFISH_STEP, 2.0F, 1.0F);
			EnderPearl pearl = player.launchProjectile(EnderPearl.class);
			pearl.setVelocity(player.getLocation().getDirection().multiply(Kit.blinderMulti));
			player.playSound(player.getEyeLocation(), Sound.ENTITY_SILVERFISH_STEP, 2.0F, 1.0F);
		}else{
			Utils.playNoAmmo(player);
		}
	}
	public static void handleImpact(EnderPearl pearl, Player player, Player hitPlayer,  Main main){
		if(hitPlayer != null){
			DeathManager.handleDeath(hitPlayer, player, Arena.getGameMode(), KitType.BLINDER.name(), true, main);
		}
		List<Entity> entities = pearl.getNearbyEntities(blinderRange, blinderRange, blinderRange);
		Set<Player> blinded = new HashSet<Player>();
		if(Utils.getRed().contains(player.getUniqueId())){
			for(Entity e : entities){
				if(e.getType() == EntityType.PLAYER){
					if(Utils.getBlue().contains(e.getUniqueId())){
						if(blindedPreviously.contains(e.getUniqueId()) == false){
							blinded.add((Player) e);
						}
					}
				}
			}
		}else{
			for(Entity e : entities){
				if(e.getType() == EntityType.PLAYER){
					if(Utils.getRed().contains(e.getUniqueId())){
						if(blindedPreviously.contains(e.getUniqueId()) == false){
							blinded.add((Player) e);
						}
					}
				}
			}
		}
		PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, blinderTime * 20, 1);
		PotionEffect effect2 = new PotionEffect(PotionEffectType.SLOW, blinderTime * 20, 0);
		for(Player p : blinded){
			p.addPotionEffect(effect);
			p.addPotionEffect(effect2);
			blindedPreviously.add(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					blindedPreviously.remove(p.getUniqueId());
				}
			}, blinderTime * 20l);
			p.sendMessage(MessageHandler.getMessage("blind", "%player%", Utils.getColoredName(player)));
			player.sendMessage(MessageHandler.getMessage("blind1", "%player%", Utils.getColoredName(p)));
		}
	}
	public static int getSize(){
		return blindedPreviously.size();
	}
}
