package me.theredbaron24.paintball.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BouncerKit extends Kit{

	private static Map<Integer, Integer> balls = new HashMap<Integer, Integer>();
	private static Map<UUID, Integer> bounces = new HashMap<UUID, Integer>();
	
	public static void handleHit(Player player, Projectile proj){
		if(bounces.containsKey(player.getUniqueId())){
			if(Kit.getKits().get(player.getUniqueId())!= KitType.BOUNCER){
				bounces.remove(player.getUniqueId());
				return;
			}
			if(balls.containsKey(proj.getEntityId())){
				int prevBounces = balls.get(proj.getEntityId());
				if(prevBounces < bounces.get(player.getUniqueId())){
					Vector vel = proj.getVelocity();
					Location loc = proj.getLocation();
					Block hitBlock = loc.getBlock();
					BlockFace blockFace = null;
					BlockIterator blockIterator = new BlockIterator(loc.getWorld(), loc.toVector(), vel, 0.0D, 3);
					Block previousBlock = hitBlock;
					Block nextBlock = blockIterator.next();
					while (blockIterator.hasNext() && (nextBlock.getType() == Material.AIR || nextBlock.isLiquid() || nextBlock.equals(hitBlock))) {
						previousBlock = nextBlock;
						nextBlock = blockIterator.next();
					}
					blockFace = nextBlock.getFace(previousBlock);
					if (blockFace != null) {
						if (blockFace == BlockFace.SELF) {
							blockFace = BlockFace.UP;
						}
						Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
						double dotProduct = vel.dot(mirrorDirection);
						mirrorDirection = mirrorDirection.multiply(dotProduct).multiply(2.0D);
						Projectile newProjectile = (Projectile) proj.getWorld().spawnEntity(loc, proj.getType());
						newProjectile.setVelocity(vel.subtract(mirrorDirection).normalize().multiply(bouncerVelocity));
						newProjectile.setShooter(proj.getShooter());
						newProjectile.setFireTicks(proj.getFireTicks());
						newProjectile.setMetadata("bouncing", new FixedMetadataValue(Main.main, newProjectile));
						balls.put(newProjectile.getEntityId(), prevBounces + 1);
						proj.remove();
					}	
				}else{
					balls.remove(proj.getEntityId());
				}
			}else{
				Vector vel = proj.getVelocity();
				Location loc = proj.getLocation();
				Block hitBlock = loc.getBlock();
				BlockFace blockFace = null;
				BlockIterator blockIterator = new BlockIterator(loc.getWorld(), loc.toVector(), vel, 0.0D, 3);
				Block previousBlock = hitBlock;
				Block nextBlock = blockIterator.next();
				while (blockIterator.hasNext() && (nextBlock.getType() == Material.AIR || nextBlock.isLiquid() || nextBlock.equals(hitBlock))) {
					previousBlock = nextBlock;
					nextBlock = blockIterator.next();
				}
				blockFace = nextBlock.getFace(previousBlock);
				if (blockFace != null) {
					if (blockFace == BlockFace.SELF) {
						blockFace = BlockFace.UP;
					}
					Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
					double dotProduct = vel.dot(mirrorDirection);
					mirrorDirection = mirrorDirection.multiply(dotProduct).multiply(2.0D);
					Projectile newProjectile = (Projectile) proj.getWorld().spawnEntity(loc, proj.getType());
					newProjectile.setVelocity(vel.subtract(mirrorDirection).normalize().multiply(bouncerVelocity));
					newProjectile.setShooter(proj.getShooter());
					newProjectile.setFireTicks(proj.getFireTicks());
					newProjectile.setMetadata(KitType.BOUNCER.name(), new FixedMetadataValue(Main.main, newProjectile));
					balls.put(newProjectile.getEntityId(), 1);
					proj.remove();
				}	
			}
		}
	}
	public static void register(Player player){
		bounces.put(player.getUniqueId(), bouncerCount);
	}
	public static void handleDeath(Player player){
		if(bounces.containsKey(player.getUniqueId())){
			if(bounces.get(player.getUniqueId()) != bouncerCount){
				bounces.put(player.getUniqueId(), bouncerCount);
			}
		}
	}
	public static void handleLeave(Player player){
		bounces.remove(player.getUniqueId());
	}
	public static void handleMatchEnd(){
		balls.clear();
	}
	public static void upgrade(Player player, int bounceCount){
		bounces.put(player.getUniqueId(), bounceCount);
	}
	public static int getBallCount(){
		return balls.size();
	}
	public static int getBouncesSize(){
		return bounces.size();
	}
}
