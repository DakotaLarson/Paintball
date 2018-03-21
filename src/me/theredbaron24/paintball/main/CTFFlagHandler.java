package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.Map;
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

public class CTFFlagHandler {
	
	private static Map<Integer, Integer> fwTasks = new HashMap<Integer, Integer>();
	
	public static Map<Integer, Integer> getFWTasks(){
		return fwTasks;
	}

	public static void handleFlagChange(Block block, Player player){
		if(DeathManager.isPlayerRespawning(player)){
			player.sendMessage(MessageHandler.getMessage("noFlagRespawning"));
			return;
		}
		UUID id = player.getUniqueId();
		if(Utils.getPlayers().contains(id) == false) return;
		BlockState state = block.getState();
		Wool wool = (Wool) state.getData();
		if(wool.getColor() == DyeColor.RED){
			if(Utils.getRed().contains(id)){
				if(Arena.getRedFlagStatus() == FlagStatus.DROPPED){
					if(block.getLocation().equals(Arena.getRedFlagLocation())){
						block.setType(Material.AIR);
						if(fwTasks.containsKey(0)){
							Bukkit.getScheduler().cancelTask(fwTasks.get(0));
							fwTasks.remove(0);
						}
						Block origin = Arena.getRedFlagOrigin().getBlock();
						origin.setType(Material.WOOL);
						BlockState originState = origin.getState();
						Wool originWool = (Wool) originState.getData();
						originWool.setColor(DyeColor.RED);
						originState.update();
						Arena.setRedFlagStatus(FlagStatus.HOME);
						Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 4);
						ScoreboardHandler.handleFlag(3, player);
					}
				}else{
					if(Arena.getRedFlagStatus() == FlagStatus.HOME){
						if(block.getLocation().equals(Arena.getRedFlagLocation())){
							if(Arena.getBlueFlagStatus() == FlagStatus.CARRIED && Arena.getBlueFlagCarrier() == id){
								Arena.setBlueFlagStatus(FlagStatus.HOME);
								Arena.setBlueFlagCarrier(null);
								Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
								player.getInventory().setItem(4, null);
								Block origin = Arena.getBlueFlagOrigin().getBlock();
								origin.setType(Material.WOOL);
								BlockState originState = origin.getState();
								Wool originWool = (Wool) originState.getData();
								originWool.setColor(DyeColor.BLUE);
								originState.update();
								broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 3);
								ScoreboardHandler.handleFlag(2, player);
							}else{
								player.sendMessage(MessageHandler.getMessage("interactWithOwnFlag"));
							}
						}
					}
				}
			}else{
				if(Arena.getRedFlagStatus() == FlagStatus.HOME){
					if(block.getLocation().equals(Arena.getRedFlagLocation())){
						Arena.setRedFlagCarrier(id);
						Arena.setRedFlagLocation(null);
						Arena.setRedFlagStatus(FlagStatus.CARRIED);
						block.setType(Material.AIR);
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("redFlagItemTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 14);
						player.getInventory().setItem(4, item);
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 1);
						ScoreboardHandler.handleFlag(0, player);
					}
				}else if(Arena.getRedFlagStatus() == FlagStatus.DROPPED){
					if(block.getLocation().equals(Arena.getRedFlagLocation())){
						Arena.setRedFlagCarrier(id);
						Arena.setRedFlagLocation(null);
						Arena.setRedFlagStatus(FlagStatus.CARRIED);
						block.setType(Material.AIR);
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("redFlagItemTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 14);
						player.getInventory().setItem(4, item);
						if(fwTasks.containsKey(0)){
							Bukkit.getScheduler().cancelTask(fwTasks.get(0));
							fwTasks.remove(0);
						}
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 5);
						ScoreboardHandler.handleFlag(4, player);
					}
				}
			}
		}else if(wool.getColor() == DyeColor.BLUE){
			if(Utils.getBlue().contains(id)){
				if(Arena.getBlueFlagStatus() == FlagStatus.DROPPED){
					if(block.getLocation().equals(Arena.getBlueFlagLocation())){
						block.setType(Material.AIR);
						if(fwTasks.containsKey(1)){
							Bukkit.getScheduler().cancelTask(fwTasks.get(1));
							fwTasks.remove(1);
						}
						Block origin = Arena.getBlueFlagOrigin().getBlock();
						origin.setType(Material.WOOL);
						BlockState originState = origin.getState();
						Wool originWool = (Wool) originState.getData();
						originWool.setColor(DyeColor.BLUE);
						originState.update();
						Arena.setBlueFlagStatus(FlagStatus.HOME);
						Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 4);
						ScoreboardHandler.handleFlag(3, player);
					}
				}else{
					if(Arena.getBlueFlagStatus() == FlagStatus.HOME){
						if(block.getLocation().equals(Arena.getBlueFlagLocation())){
							if(Arena.getRedFlagStatus() == FlagStatus.CARRIED && Arena.getRedFlagCarrier() == id){
								Arena.setRedFlagStatus(FlagStatus.HOME);
								Arena.setRedFlagCarrier(null);
								Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
								player.getInventory().setItem(4, null);
								Block origin = Arena.getRedFlagOrigin().getBlock();
								origin.setType(Material.WOOL);
								BlockState originState = origin.getState();
								Wool originWool = (Wool) originState.getData();
								originWool.setColor(DyeColor.RED);
								originState.update();
								broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 3);
								ScoreboardHandler.handleFlag(2, player);
							}else{
								player.sendMessage(MessageHandler.getMessage("interactWithOwnFlag"));
							}
						}
					}
				}
			}else{
				if(Arena.getBlueFlagStatus() == FlagStatus.HOME){
					if(block.getLocation().equals(Arena.getBlueFlagLocation())){
						Arena.setBlueFlagCarrier(id);
						Arena.setBlueFlagLocation(null);
						Arena.setBlueFlagStatus(FlagStatus.CARRIED);
						block.setType(Material.AIR);
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("blueFlagItemTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 11);
						player.getInventory().setItem(4, item);
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 1);
						ScoreboardHandler.handleFlag(0, player);
					}
				}else if(Arena.getBlueFlagStatus() == FlagStatus.DROPPED){
					if(block.getLocation().equals(Arena.getBlueFlagLocation())){
						Arena.setBlueFlagCarrier(id);
						Arena.setBlueFlagLocation(null);
						Arena.setBlueFlagStatus(FlagStatus.CARRIED);
						block.setType(Material.AIR);
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("blueFlagItemTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 11);
						player.getInventory().setItem(4, item);
						if(fwTasks.containsKey(1)){
							Bukkit.getScheduler().cancelTask(fwTasks.get(1));
							fwTasks.remove(1);
						}
						broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 5);
						ScoreboardHandler.handleFlag(4, player);
					}
				}
			}
		}else{
			return;
		}
	}
	
	public static boolean isFlag(Location location){
		if(location.equals(Arena.getBlueFlagOrigin()) || location.equals(Arena.getRedFlagOrigin())) return true;
		if(Arena.getRedFlagLocation() != null && location.equals(Arena.getRedFlagLocation())) return true;
		if(Arena.getBlueFlagLocation() != null && location.equals(Arena.getBlueFlagLocation())) return true;
		return false;
	}
	public static void dropFlag(Player player, Main main){
		player.getInventory().setItem(4, null);
		if(Arena.getBlueFlagCarrier() == player.getUniqueId()){
			Location location = player.getLocation();
			if(location.getY() < 0){
				Block block = Arena.getBlueFlagOrigin().getBlock();
				block.setType(Material.WOOL);
				BlockState state = block.getState();
				Wool wool = (Wool) state.getData();
				wool.setColor(DyeColor.BLUE);
				state.update();
				Arena.setBlueFlagStatus(FlagStatus.HOME);
				Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
				Arena.setBlueFlagCarrier(null);
				broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 6);
				return;
			}else if(location.getBlock().isEmpty()){
				Block block = location.getBlock();
				block.setType(Material.WOOL);
				BlockState state = block.getState();
				Wool wool = (Wool) state.getData();
				wool.setColor(DyeColor.BLUE);
				state.update();
				Arena.setBlueFlagCarrier(null);
				Arena.setBlueFlagLocation(location.getBlock().getLocation());
				Arena.setBlueFlagStatus(FlagStatus.DROPPED);
				broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 2);
				BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
					int time = Main.flagDropTime;
					@Override
					public void run() {
						if(time > 0){
							if(Main.flagFW){
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).with(Type.BALL).flicker(true).trail(true).build());
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}else{
							location.getBlock().setType(Material.AIR);
							Block block = Arena.getBlueFlagOrigin().getBlock();
							block.setType(Material.WOOL);
							BlockState state = block.getState();
							Wool wool = (Wool) state.getData();
							wool.setColor(DyeColor.BLUE);
							state.update();
							Arena.setBlueFlagStatus(FlagStatus.HOME);
							Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
							Bukkit.getScheduler().cancelTask(fwTasks.get(1));
							fwTasks.remove(1);
							broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 7);
						}
						time --;
					}
				}, 20l, 20l);
				fwTasks.put(1, task.getTaskId());
			}else{
				location.setY(location.getY() + 1);
				if(location.getBlock().isEmpty() == false){
					Block block = Arena.getBlueFlagOrigin().getBlock();
					block.setType(Material.WOOL);
					BlockState state = block.getState();
					Wool wool = (Wool) state.getData();
					wool.setColor(DyeColor.BLUE);
					state.update();
					Arena.setBlueFlagStatus(FlagStatus.HOME);
					Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
					Arena.setBlueFlagCarrier(null);
					broadcastFlagChange(player, false, 6);
				}else{
					Block block = location.getBlock();
					block.setType(Material.WOOL);
					BlockState state = block.getState();
					Wool wool = (Wool) state.getData();
					wool.setColor(DyeColor.BLUE);
					state.update();
					Arena.setBlueFlagCarrier(null);
					Arena.setBlueFlagLocation(location.getBlock().getLocation());
					Arena.setBlueFlagStatus(FlagStatus.DROPPED);
					broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 2);
					BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
						int time = Main.flagDropTime;
						@Override
						public void run() {
							if(time > 0){
								if(Main.flagFW){
									Firework fw = location.getWorld().spawn(location, Firework.class);
									FireworkMeta meta = fw.getFireworkMeta();
									meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).with(Type.BALL).flicker(true).trail(true).build());
									meta.setPower(1);
									fw.setFireworkMeta(meta);
								}
							}else{
								location.getBlock().setType(Material.AIR);
								Block block = Arena.getBlueFlagOrigin().getBlock();
								block.setType(Material.WOOL);
								BlockState state = block.getState();
								Wool wool = (Wool) state.getData();
								wool.setColor(DyeColor.BLUE);
								state.update();
								Arena.setBlueFlagStatus(FlagStatus.HOME);
								Arena.setBlueFlagLocation(Arena.getBlueFlagOrigin());
								Bukkit.getScheduler().cancelTask(fwTasks.get(1));
								fwTasks.remove(1);
								broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 7);
							}
							time --;
						}
					}, 20l, 20l);
					fwTasks.put(1, task.getTaskId());
				}
			}
		}else if(Arena.getRedFlagCarrier() == player.getUniqueId()){
			Location location = player.getLocation();
			if(location.getY() < 0){
				Block block = Arena.getRedFlagOrigin().getBlock();
				block.setType(Material.WOOL);
				BlockState state = block.getState();
				Wool wool = (Wool) state.getData();
				wool.setColor(DyeColor.RED);
				state.update();
				Arena.setRedFlagStatus(FlagStatus.HOME);
				Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
				Arena.setRedFlagCarrier(null);
				broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 6);
				return;
			}else if(location.getBlock().isEmpty()){
				Block block = location.getBlock();
				block.setType(Material.WOOL);
				BlockState state = block.getState();
				Wool wool = (Wool) state.getData();
				wool.setColor(DyeColor.RED);
				state.update();
				Arena.setRedFlagStatus(FlagStatus.DROPPED);
				Arena.setRedFlagLocation(location.getBlock().getLocation());
				Arena.setRedFlagCarrier(null);
				broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 2);
				BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
					int time = Main.flagDropTime;
					@Override
					public void run() {
						if(time > 0){
							if(Main.flagFW){
								Firework fw = location.getWorld().spawn(location, Firework.class);
								FireworkMeta meta = fw.getFireworkMeta();
								meta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL).flicker(true).trail(true).build());
								meta.setPower(1);
								fw.setFireworkMeta(meta);
							}
						}else{
							location.getBlock().setType(Material.AIR);
							Block block = Arena.getRedFlagOrigin().getBlock();
							block.setType(Material.WOOL);
							BlockState state = block.getState();
							Wool wool = (Wool) state.getData();
							wool.setColor(DyeColor.RED);
							state.update();
							Arena.setRedFlagStatus(FlagStatus.HOME);
							Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
							Bukkit.getScheduler().cancelTask(fwTasks.get(0));
							fwTasks.remove(0);
							broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 7);
						}
						time --;
					}
				}, 20l, 20l);
				fwTasks.put(0, task.getTaskId());
			}else{
				location.setY(location.getY() + 1);
				if(location.getBlock().isEmpty() == false){
					Block block = Arena.getRedFlagOrigin().getBlock();
					block.setType(Material.WOOL);
					BlockState state = block.getState();
					Wool wool = (Wool) state.getData();
					wool.setColor(DyeColor.RED);
					state.update();
					Arena.setRedFlagStatus(FlagStatus.HOME);
					Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
					broadcastFlagChange(player, true, 6);
				}else{
					Block block = location.getBlock();
					block.setType(Material.WOOL);
					BlockState state = block.getState();
					Wool wool = (Wool) state.getData();
					wool.setColor(DyeColor.RED);
					state.update();
					Arena.setRedFlagStatus(FlagStatus.DROPPED);
					Arena.setRedFlagLocation(location.getBlock().getLocation());
					Arena.setRedFlagCarrier(null);
					broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 2);
					BukkitTask task = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
						int time = Main.flagDropTime;
						@Override
						public void run() {
							if(time > 0){
								if(Main.flagFW){
									Firework fw = location.getWorld().spawn(location, Firework.class);
									FireworkMeta meta = fw.getFireworkMeta();
									meta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL).flicker(true).trail(true).build());
									meta.setPower(1);
									fw.setFireworkMeta(meta);
								}
							}else{
								location.getBlock().setType(Material.AIR);
								Block block = Arena.getRedFlagOrigin().getBlock();
								block.setType(Material.WOOL);
								BlockState state = block.getState();
								Wool wool = (Wool) state.getData();
								wool.setColor(DyeColor.RED);
								state.update();
								Arena.setRedFlagStatus(FlagStatus.HOME);
								Arena.setRedFlagLocation(Arena.getRedFlagOrigin());
								Bukkit.getScheduler().cancelTask(fwTasks.get(0));
								fwTasks.remove(0);
								broadcastFlagChange(player, Utils.getRed().contains(player.getUniqueId()), 7);
							}
							time --;
						}
					}, 20l, 20l);
					fwTasks.put(0, task.getTaskId());
				}
			}
		}
	}
	public static void resetFlags(boolean isAfterMatch){
		if(isAfterMatch){
			for(Integer i : fwTasks.keySet()){
				Bukkit.getScheduler().cancelTask(fwTasks.get(i));
			}
			fwTasks.clear();
			if(Arena.getRedFlagLocation() != null && Arena.getRedFlagLocation().equals(Arena.getRedFlagOrigin()) == false){
				Arena.getRedFlagLocation().getBlock().setType(Material.AIR);
			}
			if(Arena.getBlueFlagLocation() != null && Arena.getBlueFlagLocation().equals(Arena.getBlueFlagOrigin()) == false){
				Arena.getBlueFlagLocation().getBlock().setType(Material.AIR);
			}
			if (Main.flagHats){
				if (Arena.getRedFlagCarrier() != null) {
					Player player = Bukkit.getPlayer(Arena.getRedFlagCarrier());
					if (player != null) {
						RankHandler.setArmor(player);
					}
				}
				if (Arena.getBlueFlagCarrier() != null) {
					Player player = Bukkit.getPlayer(Arena.getBlueFlagCarrier());
					if (player != null) {
						RankHandler.setArmor(player);
					}
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
		Arena.setBlueFlagCarrier(null);
		Arena.setRedFlagCarrier(null);
	}
	private static void setHelm(Player player, boolean teamIsRed){
		if(Main.flagHats == false) return;
		Bukkit.getScheduler().runTask(Main.main, new Runnable() {
			@Override
			public void run() {
				ItemStack h = player.getInventory().getHelmet();
				if(h == null || h.getType() == Material.LEATHER_HELMET){
					if(teamIsRed){
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("blueFlagHelmTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 11);
						player.getInventory().setHelmet(item);
					}else{
						ItemStack item = new ItemStack(Material.WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(MessageHandler.getMessage("redFlagHelmTitle"));
						item.setItemMeta(meta);
						item.setDurability((short) 14);
						player.getInventory().setHelmet(item);
					}
				}else{
					RankHandler.setArmor(player);
				}
			}
		});
	}
	/**
	 * @param isRed the team of the player
	 * @param status 1=grab 2=drop 3=cap 4=return 5=pickup 6=failedDrop 7 = autoReturn
	 */
	private static void broadcastFlagChange(Player player, boolean isRed, int status){
		String text = null;
		if(status == 1){
			setHelm(player, isRed);
			playSound(3);
			if(isRed){
				text = MessageHandler.getMessage("grabbedBlueFlag", "%player%", Utils.getColoredName(player));
			}else{
				text = MessageHandler.getMessage("grabbedRedFlag", "%player%", Utils.getColoredName(player));
			}
		}else if(status == 2){
			setHelm(player, isRed);
			playSound(4);
			if(isRed){
				text = MessageHandler.getMessage("droppedBlueFlag", "%player%", Utils.getColoredName(player));
			}else{
				text = MessageHandler.getMessage("droppedRedFlag", "%player%", Utils.getColoredName(player));
			}
		}else if(status == 3){
			playSound(1);
			setHelm(player, isRed);
			if(isRed){
				text = MessageHandler.getMessage("capturedBlueFlag", "%player%", Utils.getColoredName(player));
			}else{
				text = MessageHandler.getMessage("capturedRedFlag", "%player%", Utils.getColoredName(player));
			}
		}else if(status == 4){
			playSound(2);
			if(isRed == false){
				text = MessageHandler.getMessage("returnedBlueFlag", "%player%", Utils.getColoredName(player));
			}else{
				text = MessageHandler.getMessage("returnedRedFlag", "%player%", Utils.getColoredName(player));
			}
		}else if(status == 5){
			playSound(3);
			setHelm(player, isRed);
			if(isRed){
				text = MessageHandler.getMessage("pickedupBlueFlag", "%player%", Utils.getColoredName(player));
			}else{
				text = MessageHandler.getMessage("pickedupRedFlag", "%player%", Utils.getColoredName(player));
			}
		}else if(status == 6){
			setHelm(player, isRed);
			playSound(2);
			if(isRed){
				text = MessageHandler.getMessage("autoRetBlueFlag");
			}else{
				text = MessageHandler.getMessage("autoRetRedFlag");
			}
		}else if(status == 7){
			playSound(2);
			if(isRed == false){
				text = MessageHandler.getMessage("autoRetObsBlueFlag");
			}else{
				text = MessageHandler.getMessage("autoRetObsRedFlag");
			}
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
				//good
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
