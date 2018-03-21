package me.theredbaron24.paintball.main;

import java.util.UUID;

import me.theredbaron24.paintball.utils.FlagStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.NMS;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitTask;

public class RTFFlagHandler {
	private static int task = -1;
	
	public static void handleFlagChange(Player player, Block block){
		if(DeathManager.isPlayerRespawning(player)){
			player.sendMessage(MessageHandler.getMessage("noFlagRespawning"));
			return;
		}
		UUID id = player.getUniqueId();
		if(Utils.getPlayers().contains(id)){
			if(Arena.getNeutralFlagStatus() == FlagStatus.HOME){
				if(block.getLocation().equals(Arena.getNeutralFlagOrigin())){
					Arena.setNeutralFlagCarrier(id);
					Arena.setNeutralFlagLocation(null);
					Arena.setNeutralFlagStatus(FlagStatus.CARRIED);
					block.setType(Material.AIR);
					ItemStack item = new ItemStack(Material.WOOL);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("flagItemTitle"));
					item.setItemMeta(meta);
					player.getInventory().setItem(4, item);
					broadcastFlagChange(player, 1);
					ScoreboardHandler.handleFlag(0, player);
				}
			}else if(Arena.getNeutralFlagStatus() == FlagStatus.DROPPED){
				if(block.getLocation().equals(Arena.getNeutralFlagLocation())){
					Arena.setNeutralFlagCarrier(id);
					Arena.setNeutralFlagLocation(null);
					Arena.setNeutralFlagStatus(FlagStatus.CARRIED);
					block.setType(Material.AIR);
					ItemStack item = new ItemStack(Material.WOOL);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("flagItemTitle"));
					item.setItemMeta(meta);
					player.getInventory().setItem(4, item);
					broadcastFlagChange(player, 5);
					Bukkit.getScheduler().cancelTask(task);
					task = -1;
					ScoreboardHandler.handleFlag(4, player);
				}
			}else{
				if(Arena.getNeutralFlagCarrier() == id){
					if(Utils.getRed().contains(id)){
						if(block.getLocation().equals(Arena.getBlueFlagOrigin())){
							Arena.setNeutralFlagCarrier(null);
							Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
							Arena.setNeutralFlagStatus(FlagStatus.HOME);
							player.getInventory().setItem(4, null);
							broadcastFlagChange(player, 3);
							Block b = Arena.getNeutralFlagOrigin().getBlock();
							b.setType(Material.WOOL);
							ScoreboardHandler.handleFlag(2, player);
						}
					}else{
						if(block.getLocation().equals(Arena.getRedFlagOrigin())){
							Arena.setNeutralFlagCarrier(null);
							Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
							Arena.setNeutralFlagStatus(FlagStatus.HOME);
							player.getInventory().setItem(4, null);
							broadcastFlagChange(player, 3);
							Block b = Arena.getNeutralFlagOrigin().getBlock();
							b.setType(Material.WOOL);
							ScoreboardHandler.handleFlag(2, player);
						}
					}
				}
			}
		}
	}
	
	public static boolean isFlag(Location location){
		if(location.equals(Arena.getBlueFlagOrigin()) || location.equals(Arena.getRedFlagOrigin())) return true;
		if(Arena.getNeutralFlagLocation() != null && location.equals(Arena.getNeutralFlagLocation())) return true;
		return false;
	}
	public static void dropFlag(Player player, Main main){
		player.getInventory().setItem(4, null);
		if(Arena.getNeutralFlagCarrier() == player.getUniqueId()){
			Location location = player.getLocation();
			if(location.getY() < 0){
				Block block = Arena.getNeutralFlagOrigin().getBlock();
				block.setType(Material.WOOL);
				Arena.setNeutralFlagStatus(FlagStatus.HOME);
				Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
				Arena.setNeutralFlagCarrier(null);
				broadcastFlagChange(player, 6);
				return;
			}else if(location.getBlock().isEmpty()){
				Block block = location.getBlock();
				block.setType(Material.WOOL);
				Arena.setNeutralFlagCarrier(null);
				Arena.setNeutralFlagLocation(location.getBlock().getLocation());
				Arena.setNeutralFlagStatus(FlagStatus.DROPPED);
				broadcastFlagChange(player, 2);
				BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
					int time = Main.flagDropTime;
					@Override
					public void run() {
						if(time > 0){
							if(Main.flagFW){
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE).with(Type.BALL).flicker(true).trail(true).build());
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}else{
							location.getBlock().setType(Material.AIR);
							Block block = Arena.getNeutralFlagOrigin().getBlock();
							block.setType(Material.WOOL);
							Arena.setNeutralFlagStatus(FlagStatus.HOME);
							Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
							Bukkit.getScheduler().cancelTask(task);
							task = -1;
							broadcastFlagChange(player, 7);
						}
						time --;
					}
				}, 20l, 20l);
				task = bukkitTask.getTaskId();
			}else{
				location.setY(location.getY() + 1);
				if(location.getBlock().isEmpty() == false){
					Block block = Arena.getNeutralFlagOrigin().getBlock();
					block.setType(Material.WOOL);
					Arena.setNeutralFlagStatus(FlagStatus.HOME);
					Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
					Arena.setNeutralFlagCarrier(null);
					broadcastFlagChange(player, 6);
				}else{
					Block block = location.getBlock();
					block.setType(Material.WOOL);
					Arena.setNeutralFlagCarrier(null);
					Arena.setNeutralFlagLocation(location.getBlock().getLocation());
					Arena.setNeutralFlagStatus(FlagStatus.DROPPED);
					broadcastFlagChange(player, 2);
					BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
						int time = Main.flagDropTime;
						@Override
						public void run() {
							if(time > 0){
								if(Main.flagFW){
									Firework fw = location.getWorld().spawn(location, Firework.class);
									FireworkMeta meta = fw.getFireworkMeta();
									meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE).with(Type.BALL).flicker(true).trail(true).build());
									meta.setPower(1);
									fw.setFireworkMeta(meta);
								}
							}else{
								location.getBlock().setType(Material.AIR);
								Block block = Arena.getNeutralFlagOrigin().getBlock();
								block.setType(Material.WOOL);
								Arena.setNeutralFlagStatus(FlagStatus.HOME);
								Arena.setNeutralFlagLocation(Arena.getNeutralFlagOrigin());
								Bukkit.getScheduler().cancelTask(task);
								task = -1;
								broadcastFlagChange(player, 7);
							}
							time --;
						}
					}, 20l, 20l);
					task = bukkitTask.getTaskId();
				}
			}
		}
	}	
	private static void setHelm(Player player){
		if(Main.flagHats == false) return;
		Bukkit.getScheduler().runTask(Main.main, new Runnable() {
			@Override
			public void run() {
				ItemStack h = player.getInventory().getHelmet();
				if(h == null || h.getType() == Material.LEATHER_HELMET){
					ItemStack item = new ItemStack(Material.WOOL);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(MessageHandler.getMessage("flagItemTitle"));
					item.setItemMeta(meta);
					player.getInventory().setHelmet(item);
				}else{
					RankHandler.setArmor(player);
				}
			}
		});
	}	
	public static void resetFlags(boolean isAfterMatch){
		if(isAfterMatch){
			if(task != -1){
				Bukkit.getScheduler().cancelTask(task);
			}
			if(Arena.getNeutralFlagLocation() != null && Arena.getNeutralFlagLocation().equals(Arena.getNeutralFlagOrigin()) == false){
				Arena.getNeutralFlagLocation().getBlock().setType(Material.AIR);
			}
			if(Arena.getNeutralFlagCarrier() != null && Main.flagHats){
				Player player = Bukkit.getPlayer(Arena.getNeutralFlagCarrier());
				if(player != null){
					RankHandler.setArmor(player);
				}
			}
		}
		Location location = Arena.getRedFlagOrigin();
		if(location.getBlock().getType() != Material.WOOL){
			Block b = location.getBlock();
			b.setType(Material.WOOL);
			BlockState state = b.getState();
			Wool wool = (Wool) state.getData();
			wool.setColor(DyeColor.RED);
			state.update();
		}
		location = Arena.getBlueFlagOrigin();
		if(location.getBlock().getType() != Material.WOOL){
			Block b = location.getBlock();
			b.setType(Material.WOOL);
			BlockState state = b.getState();
			Wool wool = (Wool) state.getData();
			wool.setColor(DyeColor.BLUE);
			state.update();
		}
		if(Arena.getNeutralFlagCarrier() != null){
			Player player = Bukkit.getPlayer(Arena.getNeutralFlagCarrier());
			if(player != null){
				RankHandler.setArmor(player);
			}
		}
		Arena.setNeutralFlagCarrier(null);
		location = Arena.getNeutralFlagOrigin();
		if(location.getBlock().getType() != Material.WOOL){
			Block b = location.getBlock();
			b.setType(Material.WOOL);
		}
	}
	/**
	 * @param status 1=grab 2=drop 3=cap 4=return 5=pickup 6=failedDrop 7 = autoReturn
	 */
	private static void broadcastFlagChange(Player player, int status){
		String text = null;
		if(status == 1){
			setHelm(player);
			playSound(3);
			text = MessageHandler.getMessage("neutralFlagGrab", "%player%", Utils.getColoredName(player));
		}else if(status == 2){
			setHelm(player);
			playSound(4);
			text = MessageHandler.getMessage("neutralFlagDrop", "%player%", Utils.getColoredName(player));
		}else if(status == 3){
			setHelm(player);
			playSound(1);
			text = MessageHandler.getMessage("neutralFlagCap", "%player%", Utils.getColoredName(player));
		}else if(status == 4){
			playSound(2);
			text = MessageHandler.getMessage("neutralFlagReturn", "%player%", Utils.getColoredName(player));
		}else if(status == 5){
			setHelm(player);
			playSound(3);
			text = MessageHandler.getMessage("neutralFlagPick", "%player%", Utils.getColoredName(player));
		}else if(status == 6){
			setHelm(player);
			playSound(4);
			text = MessageHandler.getMessage("neutralFlagOBS");
		}else if(status == 7){
			playSound(2);
			text = MessageHandler.getMessage("neutralFlagAutoRet");
		}
		if(text != null){
			for(UUID id : Utils.getPlayers()){
				Player p = Bukkit.getPlayer(id);
				p.sendMessage(text);
				NMS.sendActionBar(p, text, 5, 30, 5);
			}
		}
	}
	/**
	 * 
	 * @param status 1=capture 2=return 3=grab 4=drop
	 */
	private static void playSound(int status){
		if(status == 1){
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_WARN, 2.0f, 1.0f);
			}
		}else if(status == 2){
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_AMBIENT, 2.0f, 1.0f);
			}
		}else if(status == 3){
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_HURT, 2.0f, 1.0f);
			}
		}else if(status == 4){
			for(UUID id : Utils.getPlayers()){
				Player player = Bukkit.getPlayer(id);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_GHAST_SCREAM, 2.0f, 1.0f);
			}
		}
	}
}
