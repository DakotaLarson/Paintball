package me.theredbaron24.paintball.events;

import java.util.UUID;

import me.theredbaron24.paintball.commands.LeaveCommand;
import me.theredbaron24.paintball.kits.DemoKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.kits.SniperKit;
import me.theredbaron24.paintball.main.ActionMenuHandler;
import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.CTFFlagHandler;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.KitSelectionHandler;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.RTFFlagHandler;
import me.theredbaron24.paintball.main.VoteHandler;
import me.theredbaron24.paintball.utils.GameMode;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MiscEventListener implements Listener{

	private Main main;
	public MiscEventListener(Main main){
		this.main = main;
	}
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event){
		if(event.getCause() == TeleportCause.ENDER_PEARL){
			if(Utils.getPlayers().contains(event.getPlayer().getUniqueId())){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onThrow(PlayerEggThrowEvent event){
		if(Utils.getPlayers().contains(event.getPlayer().getUniqueId())){
			event.setHatching(false);
		}
	}
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event){
		if(Utils.getPlayers().contains(event.getPlayer().getUniqueId())){
			SniperKit.zoom(event.getPlayer());
		}
	}
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event){
		if(Main.allowHandSwap == false){
			event.getPlayer().sendMessage(MessageHandler.getMessage("noHandSwap"));
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(event.getEntityType() != EntityType.PLAYER){
			return;
		}
		Player player = (Player) entity;
		if(contains(player.getUniqueId())){	
			event.setCancelled(true);
			if(event.getCause() == DamageCause.VOID){
				if(Utils.getAsyncPlayers().contains(player.getUniqueId())){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.teleport(Utils.getLobbySpawn());
						}
					});
				}else if(Utils.getPlayers().contains(player.getUniqueId())){
					if(Utils.getGameStatus() == GameStatus.RUNNING){
						DeathManager.handleDeath(player, null, Arena.getGameMode(), "void", false, main);
					}else if(Utils.getGameStatus() == GameStatus.INITIALIZING ||  Utils.getGameStatus() == GameStatus.FINALIZING){
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.teleport(Arena.getLobbySpawn());
							}
						});
					}else{
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.teleport(Utils.getLobbySpawn());
							}
						});
					}
				}
			}else{
				player.setFireTicks(0);
			}
		}
	}
	@EventHandler
	public void weather(WeatherChangeEvent event){
		if(event.toWeatherState()){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void leaf(LeavesDecayEvent event){
		event.setCancelled(true);
	}
	@EventHandler
	public void checkInteract(PlayerInteractEvent event){
		if(contains(event.getPlayer().getUniqueId())){
			if(event.getAction() == Action.LEFT_CLICK_BLOCK){
				if(event.getClickedBlock().getRelative(event.getBlockFace()).getType() == Material.FIRE){
					event.setCancelled(true);
					event.getClickedBlock().getRelative(event.getBlockFace()).setType(Material.FIRE);
				}
			}
		}
	}
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntityType().equals(EntityType.ITEM_FRAME)) {
			if(event.getDamager().getType().equals(EntityType.PLAYER)) {
				Player player = (Player) event.getDamager();
				if(contains(player.getUniqueId())){
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(contains(event.getPlayer().getUniqueId())){
			if(event.getBlock().getType() == Material.STONE_PLATE){
				DemoKit.handleBreak(event.getPlayer(), event.getBlock().getLocation(), main);
			}else if(event.getBlock().getType() == Material.WOOL && Utils.getGameStatus() == GameStatus.RUNNING){
				if(Arena.getGameMode() == GameMode.CTF){
					if(CTFFlagHandler.isFlag(event.getBlock().getLocation())){
						CTFFlagHandler.handleFlagChange(event.getBlock(), event.getPlayer());
					}
				}else if(Arena.getGameMode() == GameMode.RTF){
					if(RTFFlagHandler.isFlag(event.getBlock().getLocation())){
						RTFFlagHandler.handleFlagChange(event.getPlayer(), event.getBlock());
					}
				}
			}
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event){
		if(contains(event.getPlayer().getUniqueId())){
			if(event.getRightClicked().getType() == EntityType.ITEM_FRAME){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onHunger(FoodLevelChangeEvent event){
		if(contains(event.getEntity().getUniqueId())){
			event.setFoodLevel(20);
			event.setCancelled(true);
		}	
	}
	@EventHandler
	public void onClick(InventoryClickEvent event){
		final Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		if(inv == null || item == null) return;
		if(contains(player.getUniqueId())){
			event.setResult(Result.DENY);
			if(event.getInventory().getTitle().equals(MessageHandler.getMessage("kitSelTitle"))){
				KitSelectionHandler.handleClick(player, item, main);
			}else if(event.getInventory().getTitle().equals(MessageHandler.getMessage("votingInvTitle"))){
				if(item.getType() == Material.WOOL){
					if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(MessageHandler.getMessage("blockCheck"))){
						VoteHandler.handleVotingSelection(player, event.getSlot());
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.closeInventory();
							}
						});
					}
				}
			}else{
				ActionMenuHandler.handleClick(event.getInventory().getTitle(), item, player, main);
			}
		}
	}
	@EventHandler
	public void onHangingBreak(HangingBreakByEntityEvent event){
		if(event.getRemover().getType() == EntityType.PLAYER){
			Player player = (Player) event.getRemover();
			if(contains(player.getUniqueId())){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(contains(event.getPlayer().getUniqueId())){
			if((event.getBlockPlaced().getType() == Material.STONE_PLATE)){
				if(Kit.getKits().get(event.getPlayer().getUniqueId()) == KitType.DEMO){
					if(DemoKit.placeMine(event.getPlayer(), event.getBlockPlaced().getLocation())){
						event.setCancelled(false);
						return;
					}
				}
			}
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		if(contains(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
		}

	}
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event){
		if(contains(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
		}
	}
	private boolean contains(UUID id){
		if(Utils.getPlayers().contains(id) || Utils.getAsyncPlayers().contains(id)){
			return true;
		}
		return false;
	}
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		if(event.getInventory().getTitle().equals(MessageHandler.getMessage("confTitle"))){
			ActionMenuHandler.handleClose(event.getPlayer().getUniqueId());
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		if(contains(event.getEntity().getUniqueId())){
			LeaveCommand.leave(event.getEntity(), true, main);
		}
	}
}
