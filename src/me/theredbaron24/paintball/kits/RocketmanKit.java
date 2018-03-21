package me.theredbaron24.paintball.kits;

import java.util.List;

import me.theredbaron24.paintball.events.ProjectileHitListener;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.ExplosionHandler;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;


public class RocketmanKit extends Kit{
	
	public static void shoot(Player player, Main main){
		if(Utils.removeItem(player, Material.FIREBALL, MessageHandler.getMessage("rcChargeTitle"), 1)){
			player.playSound(player.getEyeLocation(), Sound.ENTITY_SILVERFISH_STEP, 2.0F, 1.0F);
			Fireball ball = player.launchProjectile(Fireball.class);
			ball.setFireTicks(0);
			ball.setIsIncendiary(false);
			ball.setYield(0f);
			ball.setVelocity(player.getLocation().getDirection().multiply(rocketmanSpeed));
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					if(ball.isDead() == false){
						ball.remove();
					}
				}
			}, 100l);
		}else{
			Utils.playNoAmmo(player);
		}
	}
	public static void handleStrike(Fireball ball, Player shooter, Player hitPlayer, Main main){
		if(hitPlayer != null){
			DeathManager.handleDeath(hitPlayer, shooter, Arena.getGameMode(), KitType.ROCKETMAN.name(), true, main);
		}
		List<Entity> entities = ball.getNearbyEntities(rocketmanRadius, rocketmanRadius, rocketmanRadius);
		if(Utils.getRed().contains(shooter.getUniqueId())){
			for(Entity e : entities){
				if(e.getType() == EntityType.PLAYER){
					Player p = (Player) e;
					if(Utils.getBlue().contains(p.getUniqueId())){
						if(ProjectileHitListener.hitIsValid(p, shooter, null)){
							DeathManager.damage(p, shooter, KitType.ROCKETMAN.name(), main, false);
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
							DeathManager.damage(p, shooter, KitType.ROCKETMAN.name(), main, false);
						}
					}
				}
			}
		}
		ExplosionHandler.createExplosion(shooter, rocketmanBallCount, ball.getLocation(), rocketmanBallSpeed, KitType.ROCKETMAN.name(), main);
	}
}
