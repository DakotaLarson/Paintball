package me.theredbaron24.paintball.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

	private static GameStatus gameStatus = GameStatus.NOT_STARTED;
	
	private static Set<UUID> players = new HashSet<UUID>();
	private static Set<UUID> asyncPlayers = new HashSet<UUID>();
	private static Location lobbySpawn = null;
	private static Set<UUID> playersToAdd = new HashSet<UUID>();
	private static Set<UUID> red = new HashSet<UUID>();
	private static Set<UUID> blue = new HashSet<UUID>();
	
	public static GameStatus getGameStatus(){
		return gameStatus;
	}
	
	public static void setGameStatus(GameStatus gameStatus){
		Utils.gameStatus = gameStatus;
	}
	
	public static void setLobbySpawn(Location spawn){
		Utils.lobbySpawn = spawn;
	}

	public static Set<UUID> getPlayers() {
		return players;
	}
	
	public static Location getLobbySpawn(){
		return lobbySpawn;
	}
	
	public static Set<UUID> getPlayersToAdd(){
		return playersToAdd;
	}

	public static Set<UUID> getRed() {
		return red;
	}

	public static Set<UUID> getBlue() {
		return blue;
	}
	
	public static Set<UUID> getAsyncPlayers(){
		return asyncPlayers;
	}
	public static boolean removeItem(Player player,Material material, String displayName, int count){
		if(count == 0) return true;
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		PlayerInventory inv = player.getInventory();
		if(inv.containsAtLeast(item, count)){
			item.setAmount(count);
			inv.removeItem(item);
			if(Main.updateInv){
				player.updateInventory();
			}
			return true;
		}
		return false;
	}
	public static ItemStack getItem(Material material, String displayName, int count){
		ItemStack item = new ItemStack(material, count);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}
	public static String getColoredName(Player player){
		if(getRed().contains(player.getUniqueId())){
			return Main.redTeamColor + player.getName();
		}else{
			return Main.blueTeamColor + player.getName();
		}
	}
	public static String getBoldedColoredName(Player player){
		if(getRed().contains(player.getUniqueId())){
			return "" + Main.redTeamColor + ChatColor.BOLD + player.getName();
		}else{
			return "" + Main.blueTeamColor + ChatColor.BOLD + player.getName();
		}
	}
	public static void playTimerPling(Player player){
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_HAT, 1f, 1f);
	}
	public static void addSnowballs(int count, Player player, boolean isHeavy){
		PlayerInventory inv = player.getInventory();
		ItemStack item = new ItemStack(Material.SNOW_BALL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("paintballTitle"));
		item.setItemMeta(meta);
		if(isHeavy){
			if(inv.getItem(2) == null){
				if(count <=64){
					item.setAmount(count);
					inv.setItem(2, item);
					return;
				}else{
					item.setAmount(64);
					inv.setItem(2, item);
					count -= 64;
				}
			}
			if(inv.getItem(3) == null){
				if(count <= 64){
					item.setAmount(count);
					inv.setItem(3, item);
					return;
				}else{
					item.setAmount(64);
					inv.setItem(3, item);
					count -= 64;
				}
			}
			if(count > 0){
				for(int i = 9; i < 36; i++){
					if(inv.getItem(i) != null){
						if(inv.getItem(i).getType() == Material.SNOW_BALL){
							ItemStack it = inv.getItem(i);
							if(it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(meta.getDisplayName())){
								if(it.getAmount() < 64){
									int itCount = 64 - it.getAmount();
									if(itCount >= count){
										it.setAmount(it.getAmount() + count);
										inv.setItem(i, it);
										return;
									}else{
										it.setAmount(64);
										inv.setItem(i, it);
										count -= itCount;
									}
								}
							}
						}
						continue;
					}
					if(count <= 64){
						item.setAmount(count);
						inv.setItem(i, item);
						return;
					}else{
						item.setAmount(64);
						inv.setItem(i, item);
						count -= 64;
					}
				}
			}
		}else{
			for(int i = 8; i < 36; i++){
				if(inv.getItem(i) != null){
					if(inv.getItem(i).getType() == Material.SNOW_BALL){
						ItemStack it = inv.getItem(i);
						if(it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(meta.getDisplayName())){
							if(it.getAmount() < 64){
								int itCount = 64 - it.getAmount();
								if(itCount >= count){
									it.setAmount(it.getAmount() + count);
									inv.setItem(i, it);
									return;
								}else{
									it.setAmount(64);
									inv.setItem(i, it);
									count -= itCount;
								}
							}
						}
					}
					continue;
				}
				if(count <= 64){
					item.setAmount(count);
					inv.setItem(i, item);
					return;
				}else{
					item.setAmount(64);
					inv.setItem(i, item);
					count -= 64;
				}
			}
		}
	}
	public static void removeArmor(Player player){
		PlayerInventory inv = player.getInventory();
		inv.setBoots(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
	}
	public static boolean isDigit(String str){
		try{
			Integer.parseInt(str);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	public static void playNoAmmo(Player player){
		player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
	}
	@SuppressWarnings("deprecation")
	public static void setBlockHead(Location location, String name){
		location.getBlock().setType(Material.SKULL);
		location.getBlock().setData((byte) 3);
		Skull s = (Skull)location.getBlock().getState();
		s.setOwner(name);
		s.update();
	}
	public static void setSign(Location location, String line1, String line2, String line3, String line4){
		Block block = location.getBlock();
		if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
			Sign sign = (Sign) block.getState();
			line1 = line1 == null ? "" : line1;
			line2 = line2 == null ? "" : line2;
			line3 = line3 == null ? "" : line3;
			line4 = line4 == null ? "" : line4;
			sign.setLine(0, line1);
			sign.setLine(1, line2);
			sign.setLine(2, line3);
			sign.setLine(3, line4);
			sign.update();
		}
	}
}
