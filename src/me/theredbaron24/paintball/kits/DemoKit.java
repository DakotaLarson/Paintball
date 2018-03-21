package me.theredbaron24.paintball.kits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.events.ProjectileHitListener;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.ExplosionHandler;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DemoKit extends Kit{

		
	public static boolean placeMine(Player player, Location location){
		if(Main.mines.containsKey(player.getUniqueId()) == false){
			Main.mines.put(player.getUniqueId(), new HashSet<Location>());
		}
		if(Main.mines.get(player.getUniqueId()).size()>= demoCount){
			player.sendMessage(MessageHandler.getMessage("demoTooMany", "%count%", "" + demoCount));
			return false;
		}else{
			Main.mines.get(player.getUniqueId()).add(location);
			location.getBlock().setType(Material.STONE_PLATE);
			Main.mines.get(player.getUniqueId()).add(location);
			player.sendMessage(MessageHandler.getMessage("demoPlaced"));
			return true;
		}
	}	
	public static void handleStep(Player player, Location location, Main main){
		Player placer = getMineOwner(location);
		if(placer == null) return;
		if(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(placer.getUniqueId())|| 
				Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(placer.getUniqueId())){
			if(player.getName().equals(placer.getName())){
				player.sendMessage(MessageHandler.getMessage("demoOwnMine"));
			}else{
				player.sendMessage(MessageHandler.getMessage("demoTeamMine", "%player%", Utils.getColoredName(placer)));
			}
		}else{
			Main.mines.get(placer.getUniqueId()).remove(location);
			List<Entity> entities = player.getNearbyEntities(demoRadius, demoRadius, demoRadius);
			DeathManager.handleDeath(player, placer, Arena.getGameMode(), KitType.DEMO.name(), true, main);
			if(Utils.getRed().contains(placer.getUniqueId())){
				for(Entity e : entities){
					if(e.getType() == EntityType.PLAYER){
						Player p = (Player) e;
						if(Utils.getBlue().contains(p.getUniqueId())){
							if(ProjectileHitListener.hitIsValid(p, placer, null)){
								DeathManager.damage(p, placer, KitType.DEMO.name(), main, false);
							}
						}
					}
				}
			}else{
				for(Entity e : entities){
					if(e.getType() == EntityType.PLAYER){
						Player p = (Player) e;
						if(Utils.getRed().contains(p.getUniqueId())){
							if(ProjectileHitListener.hitIsValid(p, placer, null)){
								DeathManager.damage(p, placer, KitType.DEMO.name(), main, false);
							}
						}
					}
				}
			}
			removeMine(location, main);
			ExplosionHandler.createExplosion(player, demoBallCount, location, demoBallVelocity, KitType.DEMO.name(), main);
		}
	}
	private static Player getMineOwner(Location location){
		for(UUID id: Main.mines.keySet()){
			for(Location loc: Main.mines.get(id)){
				if(location.toString().equals(loc.toString())){
					return Bukkit.getPlayer(id);
				}
			}
		}
		return null;
	}
	public static void resetMines(UUID id, Main main){
		if(Main.mines.containsKey(id) == false) return;
		for(Location location : Main.mines.get(id)){
			removeMine(location, main);
		}
		Main.mines.remove(id);
	}
	public static void detonateMines(Player player, Main main){
		if(Main.mines.containsKey(player.getUniqueId()) == false){
			player.sendMessage(MessageHandler.getMessage("demoNonePlaced"));
			return;
		}
		for(Location location : Main.mines.get(player.getUniqueId())){
			removeMine(location, main);
			if(demoRadius > 0){
				Snowball ball = location.getWorld().spawn(location, Snowball.class);
				List<Entity> entities = ball.getNearbyEntities(demoRadius, demoRadius, demoRadius);
				ball.remove();
				if(Utils.getRed().contains(player.getUniqueId())){
					for(Entity e : entities){
						if(e.getType() == EntityType.PLAYER){
							Player p = (Player) e;
							if(Utils.getBlue().contains(p.getUniqueId())){
								if(ProjectileHitListener.hitIsValid(p, player, null)){
									DeathManager.damage(p, player, KitType.DEMO.name(), main, false);
								}
							}
						}
					}
				}else{
					for(Entity e : entities){
						if(e.getType() == EntityType.PLAYER){
							Player p = (Player) e;
							if(Utils.getRed().contains(p.getUniqueId())){
								if(ProjectileHitListener.hitIsValid(p, player, null)){
									DeathManager.damage(p, player, KitType.DEMO.name(), main, false);
								}
							}
						}
					}
				}
			}
			ExplosionHandler.createExplosion(player, demoBallCount, location, demoBallVelocity, KitType.DEMO.name(), main);
		}
		Main.mines.remove(player.getUniqueId());
		player.sendMessage(MessageHandler.getMessage("demoMinesDet"));
	}
	public static void removeMines(Main main){
		for(Set<Location> set : Main.mines.values()){
			for(Location location : set){
				removeMine(location, main);
			}
		}
		Main.mines.clear();
	}
	public static void handleBreak(Player player, Location location, Main main){
		if(Utils.getPlayers().contains(player.getUniqueId()) == false) return;
		Player placer = getMineOwner(location);
		if(placer == null) return;
		if(player.getUniqueId().equals(placer.getUniqueId())){
			removeMine(location, main);
			Main.mines.get(placer.getUniqueId()).remove(location);
			PlayerInventory inv = player.getInventory();
			if(inv.contains(Material.STONE_PLATE, demoCount) == false){
				ItemStack item = Utils.getItem(Material.STONE_PLATE, MessageHandler.getMessage("mineTitle"), 1);
				inv.addItem(item);
			}
			player.sendMessage(MessageHandler.getMessage("demoMineRemoved"));
			return;
		}else{
			if(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(placer.getUniqueId())|| 
					Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(placer.getUniqueId())){
				player.sendMessage(MessageHandler.getMessage("demoNoRemove", "%player%", Utils.getColoredName(placer)));
				return;
			}
			removeMine(location, main);
			Main.mines.get(placer.getUniqueId()).remove(location);
			player.sendMessage(MessageHandler.getMessage("demoMineRemoved1", "%player%", Utils.getColoredName(placer)));
			placer.sendMessage(MessageHandler.getMessage("demoMineRemoved2", "%player%", Utils.getColoredName(player)));
			return;
		}
	}
	public static boolean isMine(Location location){
		for(Set<Location> locSet : Main.mines.values()){
			if(locSet.contains(location)){
				return true;
			}
		}
		return false;
	}
	private static void removeMine(Location location, Main main){
		Block block = location.getBlock();
		if(block.getType() == Material.STONE_PLATE){
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					block.setType(Material.AIR);
				}
			}, 5l);
		}
	}
}
