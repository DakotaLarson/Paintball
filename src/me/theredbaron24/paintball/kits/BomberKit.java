package me.theredbaron24.paintball.kits;

import java.util.List;

import me.theredbaron24.paintball.events.ProjectileHitListener;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.ExplosionHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class BomberKit extends Kit{
	

	public static void handleClick(Player player){
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item.getAmount() == 1){
			player.getInventory().setItemInMainHand(null);
		}else{
			item.setAmount(item.getAmount() - 1);
			player.getInventory().setItemInMainHand(item);
		}
		Egg egg = player.launchProjectile(Egg.class);
		egg.setVelocity(player.getLocation().getDirection().multiply(bomberSpeed));
		player.playSound(player.getEyeLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 2f, 1f);
		
	}
	
	public static void handleStrike(Entity entity, Player shooter, Player hitPlayer, Main main){
		if(hitPlayer != null){
			DeathManager.handleDeath(hitPlayer, shooter, Arena.getGameMode(), KitType.BOMBER.name(), true, main);
		}
		List<Entity> entities = entity.getNearbyEntities(bomberRadius, bomberRadius, bomberRadius);
		if(Utils.getRed().contains(shooter.getUniqueId())){
			for(Entity e : entities){
				if(e.getType() == EntityType.PLAYER){
					Player p = (Player) e;
					if(Utils.getBlue().contains(p.getUniqueId())){
						if(ProjectileHitListener.hitIsValid(p, shooter, null)){
							DeathManager.damage(p, shooter, KitType.BOMBER.name(), main, false);
						}
					}
				}
			}
		}else{
			for(Entity e : entities){
				if(e.getType() == EntityType.PLAYER){
					Player p = (Player) e;
					if(Utils.getRed().contains(p.getUniqueId())){
						if(ProjectileHitListener.hitIsValid(p, shooter, null)){
							DeathManager.damage(p, shooter, KitType.BOMBER.name(), main, false);
						}
					}
				}
			}
		}
		ExplosionHandler.createExplosion(shooter, bomberBallCount, entity.getLocation(), bomberBallSpeed, KitType.BOMBER.name(), main);
	}	
}
