package me.theredbaron24.paintball.events;

import me.theredbaron24.paintball.kits.BlinderKit;
import me.theredbaron24.paintball.kits.BlockerKit;
import me.theredbaron24.paintball.kits.BomberKit;
import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.GrenadierKit;
import me.theredbaron24.paintball.kits.JuggernautKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.kits.RocketmanKit;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener{

	private Main main = null;
	public ProjectileHitListener(Main main){
		this.main = main;
	}
	@EventHandler
	public void onHit(ProjectileHitEvent event){
		if(Utils.getGameStatus() == GameStatus.RUNNING){
			if(event.getEntityType() == EntityType.SNOWBALL){
				BouncerKit.handleHit((Player) event.getEntity().getShooter(), event.getEntity());
			}else if(event.getEntityType() == EntityType.ENDER_PEARL){
				Player player = (Player) event.getEntity().getShooter();
				if(Utils.getPlayers().contains(player.getUniqueId())){
					EnderPearl pearl = (EnderPearl) event.getEntity();
					BlinderKit.handleImpact(pearl, player, null, main);
				}
			}else if(event.getEntityType() == EntityType.EGG){
				Player shooter = (Player) event.getEntity().getShooter();
				if(Utils.getPlayers().contains(shooter.getUniqueId())){
					if(Kit.getKits().get(shooter.getUniqueId()) == KitType.BOMBER){
						BomberKit.handleStrike(event.getEntity(), shooter, null, main);
					}else{
						GrenadierKit.handleStrike(event.getEntity(), shooter, null, main);
					}
				}
			}else if(event.getEntityType() == EntityType.FIREBALL){
				Fireball fireball = (Fireball) event.getEntity();
				Player shooter = (Player) fireball.getShooter();
				if(Utils.getPlayers().contains(shooter.getUniqueId())){
					RocketmanKit.handleStrike(fireball, shooter, null, main);
				}
			}
		}
	}
	@EventHandler
	public void onStrike(EntityDamageByEntityEvent event){
		if(Utils.getGameStatus() == GameStatus.RUNNING){
			if(event.getEntityType() == EntityType.PLAYER){
				Player player = (Player) event.getEntity();
				if(Utils.getPlayers().contains(player.getUniqueId()) == false){
					return;
				}
				event.setCancelled(true);
				if(event.getDamager().getType() == EntityType.SNOWBALL){
					Snowball ball = (Snowball) event.getDamager();
					Player shooter = (Player) ball.getShooter();
					if(Utils.getPlayers().contains(shooter.getUniqueId()) == false){
						return;
					}
					if(hitIsValid(player, shooter, ball)){
						DeathManager.damage(player, shooter, getData(ball), main, true);
					}
				}else if(event.getDamager().getType() == EntityType.EGG){
					Egg egg = (Egg) event.getDamager();
					Player shooter = (Player) egg.getShooter();
					if(Utils.getPlayers().contains(shooter.getUniqueId()) == false){
						return;
					}
					if(Main.instantKill && hitIsValid(player, shooter, egg)){
						if(Kit.getKits().get(shooter.getUniqueId()) == KitType.BOMBER){
							BomberKit.handleStrike(egg, shooter, player, main);
						}else{
							GrenadierKit.handleStrike(egg, shooter, player, main);
						}
					}else{
						if(Kit.getKits().get(shooter.getUniqueId()) == KitType.BOMBER){
							BomberKit.handleStrike(egg, shooter, null, main);
						}else{
							GrenadierKit.handleStrike(egg, shooter, null, main);
						}
					}
				}else if(event.getDamager().getType() == EntityType.ENDER_PEARL){
					EnderPearl pearl = (EnderPearl) event.getDamager();
					Player shooter = (Player) pearl.getShooter();
					if(Utils.getPlayers().contains(shooter.getUniqueId()) == false){
						return;
					}
					if(Main.instantKill && hitIsValid(player, shooter, pearl)){
						BlinderKit.handleImpact(pearl, shooter, player, main);
					}else{
						BlinderKit.handleImpact(pearl, shooter, null, main);
					}
				}else if(event.getDamager().getType() == EntityType.FIREBALL){
					Fireball ball = (Fireball) event.getDamager();
					ball.setYield(0f);
					ball.setIsIncendiary(false);
					Player shooter = (Player) ball.getShooter();
					if(Utils.getPlayers().contains(shooter.getUniqueId()) == false){
						return;
					}
					if(Main.instantKill && hitIsValid(player, shooter, ball)){
						RocketmanKit.handleStrike(ball, shooter, player, main);
					}else{
						RocketmanKit.handleStrike(ball, shooter, null, main);
					}
				}else if(event.getDamager().getType() == EntityType.PLAYER){
					if(event.getCause() == DamageCause.ENTITY_ATTACK){
						Player damager = (Player) event.getDamager();
						if(Main.punchForAll){
							if(punchIsValid(player, damager)){
								DeathManager.damage(player, damager, "punch", main, false);
							}
						}else if(Main.punchForSome){
							if(Main.kitsThatCanPunch.contains(Kit.getKits().get(damager.getUniqueId()).name().toLowerCase())){
								if(punchIsValid(player, damager)){
									DeathManager.damage(player, damager, "punch", main, false);
								}
							}
						}
					}
				}
			}
		}
	}
	public static boolean hitIsValid(Player player, Player shooter, Projectile proj){
		if(player.getName() == shooter.getName()) return false;
		if((Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(shooter.getUniqueId())) ||
				(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(shooter.getUniqueId()))){
			shooter.sendMessage(MessageHandler.getMessage("playerOnTeam", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(DeathManager.isPlayerRespawning(player)){
			shooter.sendMessage(MessageHandler.getMessage("playerRespawning", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(DeathManager.isSpawnProtected(player)){
			player.sendMessage(MessageHandler.getMessage("respawnProtect", "%player%", Utils.getColoredName(shooter)));
			shooter.sendMessage(MessageHandler.getMessage("respawnProtect1", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(DeathManager.isEliminated(player)){
			shooter.sendMessage(MessageHandler.getMessage("playerElm"));
			return false;
		}else if(BlockerKit.getBlocked().contains(player.getUniqueId())){
			if(proj != null && proj.getType() != EntityType.SNOWBALL && BlockerKit.protectsAgainstProj() == false){
				player.sendMessage(MessageHandler.getMessage("blockerOverride", "%player%", Utils.getColoredName(shooter)));
				shooter.sendMessage(MessageHandler.getMessage("blockerOverride1", "%player%", Utils.getColoredName(player)));
				return true;
			}else{
				if(BlockerKit.shiftOverride() == false){
					player.sendMessage(MessageHandler.getMessage("blockerPrevent1", "%player%", Utils.getColoredName(shooter)));
					shooter.sendMessage(MessageHandler.getMessage("blockerPrevent", "%player%", Utils.getColoredName(player)));
					return false;
				}else{
					if(shooter.isSneaking()){
						player.sendMessage(MessageHandler.getMessage("blockerSneakOver1", "%player%", Utils.getColoredName(shooter)));
						shooter.sendMessage(MessageHandler.getMessage("blockerSneakOver", "%player%", Utils.getColoredName(player)));
						return true;
					}else{
						player.sendMessage(MessageHandler.getMessage("blockerPrevent", "%player%", Utils.getColoredName(shooter)));
						shooter.sendMessage(MessageHandler.getMessage("blockerPrevent2", "%player%", Utils.getColoredName(player)));
						return false;
					}
				}
			}
		}else{
			if((proj != null && proj.getType() == EntityType.SNOWBALL) || (proj == null && Kit.getKits().get(shooter.getUniqueId()) != KitType.SNIPER)){
				int hits = JuggernautKit.hit(player.getUniqueId());
				if(hits == -1){
					return true;
				}else{
					shooter.sendMessage(MessageHandler.getMessage("juggProt1", "%player%", Utils.getColoredName(player)));
					player.sendMessage(MessageHandler.getMessage("juggProt", "%player%", Utils.getColoredName(shooter)));
					if(hits != 0){
						player.setExp((float) hits/JuggernautKit.getJuggHits());
					}else{
						player.setExp(1f);
					}
					return false;
				}
			}else{
				return true;
			}
			
		}
	}
	private String getData(Projectile proj){
		KitType[] kits = {KitType.BLASTER, KitType.BLINDER, KitType.BOMBER, KitType.DEMO, KitType.GRENADIER, KitType.GUNNER, KitType.ROCKETMAN, KitType.SPRAYER, KitType.BOUNCER};
		for(KitType kit : kits){
			if(proj.hasMetadata(kit.name())){
				return kit.name().toLowerCase();
			}
		}
		return null;
	}
	private static boolean punchIsValid(Player player, Player damager){
		if((Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(damager.getUniqueId())) ||
				(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(damager.getUniqueId()))){
			damager.sendMessage(MessageHandler.getMessage("playerOnTeam", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(DeathManager.isPlayerRespawning(player)){
			damager.sendMessage(MessageHandler.getMessage("playerRespawning", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(DeathManager.isSpawnProtected(player)){
			player.sendMessage(MessageHandler.getMessage("respawnProtect", "%player%", Utils.getColoredName(damager)));
			damager.sendMessage(MessageHandler.getMessage("respawnProtect1", "%player%", Utils.getColoredName(player)));
			return false;
		}else if(BlockerKit.getBlocked().contains(player.getUniqueId())){
			if(BlockerKit.protectsAgainstPunch()){
				if(BlockerKit.shiftOverride()){
					if(damager.isSneaking()){
						return true;
					}
					player.sendMessage(MessageHandler.getMessage("blockerPrevent", "%player%", Utils.getColoredName(damager)));
					damager.sendMessage(MessageHandler.getMessage("blockerPrevent2", "%player%", Utils.getColoredName(player)));
				}else{
					player.sendMessage(MessageHandler.getMessage("blockerPrevent", "%player%", Utils.getColoredName(damager)));
					damager.sendMessage(MessageHandler.getMessage("blockerPrevent1", "%player%", Utils.getColoredName(player)));
				}
				return false;
			}
		}
		return true;
	}
}
