package me.theredbaron24.paintball.events;

import me.theredbaron24.paintball.commands.TeamSelectionCommand;
import me.theredbaron24.paintball.kits.BlasterKit;
import me.theredbaron24.paintball.kits.BlinderKit;
import me.theredbaron24.paintball.kits.BlockerKit;
import me.theredbaron24.paintball.kits.BomberKit;
import me.theredbaron24.paintball.kits.DemoKit;
import me.theredbaron24.paintball.kits.GrenadierKit;
import me.theredbaron24.paintball.kits.GunnerKit;
import me.theredbaron24.paintball.kits.HealerKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.kits.ReloaderKit;
import me.theredbaron24.paintball.kits.RocketmanKit;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.kits.SprayerKit;
import me.theredbaron24.paintball.main.ActionMenuHandler;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.CTFFlagHandler;
import me.theredbaron24.paintball.main.KitSelectionHandler;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.RTFFlagHandler;
import me.theredbaron24.paintball.main.ReloadHandler;
import me.theredbaron24.paintball.main.ScoreboardHandler;
import me.theredbaron24.paintball.main.TokenHandler;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractionListener implements Listener{

	private Main main;
	private static Sound sound = null;
	private static float volume = 0f;
	private static float pitch = 0f;
	public InteractionListener(Main main){
		this.main = main;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.hasItem()){
			if(Utils.getPlayers().contains(player.getUniqueId()) || Utils.getAsyncPlayers().contains(player.getUniqueId()) || Utils.getPlayersToAdd().contains(player.getUniqueId())){
				if(event.getItem().getType() == Material.EMERALD){
					if(Main.kitsEnabled){
						KitSelectionHandler.openInv(player);
						return;
					}
				}else if(event.getItem().getType() == Material.DIAMOND){
					ActionMenuHandler.openInv(player);
					return;
				}else if(event.getItem().getType() == Material.WOOL){
					if(event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem()
							.getItemMeta().getDisplayName().equals(MessageHandler.getMessage("mainBlockTitle"))){
						VoteHandler.openVotingInv(player);
						return;
					}
				}
			}
		}
		if(Utils.getGameStatus() == GameStatus.RUNNING){
			if(Utils.getPlayers().contains(player.getUniqueId())){
				if(event.getAction() == Action.PHYSICAL){
					if(event.getClickedBlock().getType() == Material.STONE_PLATE){
						DemoKit.handleStep(player, event.getClickedBlock().getLocation(), main);
					}
				}else{
					if(event.hasBlock()){
						Block block = event.getClickedBlock();
						if(block.getType() == Material.WOOL && Main.flagBreak == false){
							if(Arena.getGameMode() == GameMode.CTF && CTFFlagHandler.isFlag(block.getLocation())){
								CTFFlagHandler.handleFlagChange(block, player);
								return;
							}else if(Arena.getGameMode() == GameMode.RTF && RTFFlagHandler.isFlag(block.getLocation())){
								RTFFlagHandler.handleFlagChange(player, block);
								return;
							}
						}
					}
					if(event.hasItem()){
						event.setCancelled(true);
						Material type = event.getItem().getType();
						if(type == Material.DIAMOND_HOE){
							if(Main.useRight && Main.useLeft){
								shootPaintball(player, main);
							}else{
								if(Main.useLeft){
									if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
										shootPaintball(player, main);
									}else{
										if(Main.useOther){
											if(ReloadHandler.isReloading(player) == false){
												ReloadHandler.reload(player, main);
											}	
										}
										
									}
								}else{
									if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
										shootPaintball(player, main);
									}else{
										if(Main.useOther){
											if(ReloadHandler.isReloading(player) == false){
												ReloadHandler.reload(player, main);
											}	
										}
									}
								}
							}
						}else{
							switch(type){
							case IRON_INGOT:
								ReloadHandler.reload(player, main);
								break;
							case STONE_HOE:
								BlinderKit.shoot(player);
								break;
							case BLAZE_ROD:
								BlockerKit.handleClick(player, main);
								break;
							case EGG:
								if(Kit.getKits().get(player.getUniqueId()) == KitType.GRENADIER){
									GrenadierKit.handleClick(player);
								}else{
									BomberKit.handleClick(player);
								}
								break;
							case GOLD_INGOT:
								DemoKit.detonateMines(player, main);
								break;
							case GOLD_AXE:
								GunnerKit.shoot(player, main);
								break;
							case STICK:
								if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
									HealerKit.healSelf(player);
								}
								break;
							case IRON_AXE:
								RocketmanKit.shoot(player, main);
								break;
							case GOLD_HOE:
								SniperKit.shoot(player, main, event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
								break;
							case STONE_AXE:
								SprayerKit.shoot(player, main);
								break;
							case IRON_HOE:
								BlasterKit.shoot(player, main);
								break;
							case SNOW_BALL:
								player.sendMessage(MessageHandler.getMessage("usePBGun"));
								break;
							case STONE_PLATE:
								event.setCancelled(false);
								break;
							case REDSTONE:
								TokenHandler.handleClick(player);
								break;
							default:
								event.setCancelled(false);
								break;
							}
						}
					}
				}
			}
		}else if(Utils.getGameStatus() == GameStatus.INITIALIZING){
			if(Utils.getPlayers().contains(player.getUniqueId())){
				if(event.hasItem() && event.getItem().getType() == Material.WOOL){
					TeamSelectionCommand.handleWoolSelection(player);
				}
			}
		}
	}
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event){
		if(event.getRightClicked().getType() == EntityType.PLAYER){
			if(Utils.getGameStatus() == GameStatus.RUNNING){
				if(Utils.getPlayers().contains(event.getPlayer().getUniqueId())){
					if(Kit.getKits().get(event.getPlayer().getUniqueId()) == KitType.HEALER){
						Player healer = event.getPlayer();
						ItemStack item = healer.getInventory().getItemInMainHand();
						if(item != null && item.getType() == Material.STICK){
							Player player = (Player) event.getRightClicked();
							if(Utils.getPlayers().contains(player.getUniqueId())){
								if((Utils.getRed().contains(player.getUniqueId()) && Utils.getRed().contains(healer.getUniqueId())) ||
										(Utils.getBlue().contains(player.getUniqueId()) && Utils.getBlue().contains(healer.getUniqueId()))){
									HealerKit.healOther(healer, player);
								}else{
									healer.sendMessage(MessageHandler.getMessage("healerNotOnTeam", "%player%", Utils.getColoredName(player)));
								}
							}
						}
					}
				}
			}
		}
	}
	private static void shootPaintball(Player player, Main main){
		if(BlockerKit.canShoot(player) == false){
			player.sendMessage(MessageHandler.getMessage("blockerNoShoot"));
			Utils.playNoAmmo(player);
			return;
		}
		if(Utils.removeItem(player, Material.SNOW_BALL, MessageHandler.getMessage("paintballTitle"), 1)){
			ScoreboardHandler.handleShot(player);
			Snowball ball = player.launchProjectile(Snowball.class);
			ball.setVelocity(player.getLocation().getDirection().multiply(Main.ballSpeed));
			if(sound != null){
				player.playSound(player.getEyeLocation(), sound, volume, pitch);
				player.getWorld().playSound(player.getEyeLocation(), sound, volume, pitch);
			}
		}else{
			Utils.playNoAmmo(player);
			if(ReloaderKit.reload(player.getUniqueId())){
				if(ReloadHandler.isReloading(player) == false){
					ReloadHandler.reload(player, main);
				}
			}
		}
	}
	public static void setSound(String sound, float volume, float pitch){
		if(sound.equalsIgnoreCase("none")){ 
			InteractionListener.sound = null;
			return;
		}
		Sound snd = null;
		try{
			snd = Sound.valueOf(sound.toUpperCase());
		}catch(Exception e){
			Main.main.getLogger().warning("Unable to load sound: " + sound);
			snd = Sound.ITEM_FLINTANDSTEEL_USE;
		}
		InteractionListener.sound = snd;
		InteractionListener.volume = Math.max(Math.min(volume, 0), 2);
		InteractionListener.pitch = Math.max(Math.min(pitch, 0), 2);
	}
}
