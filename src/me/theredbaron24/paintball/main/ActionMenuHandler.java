package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ActionMenuHandler {

	private static Inventory actionMenuInv = null;
	private static Inventory kitShopInv = null;
	private static Inventory confirmInv = null;
	private static Map<UUID, String> confMap = new HashMap<UUID, String>();
	private static String addedCmdType = null;
	private static String addedCmd = null;
	private static boolean consoleExe = false;
	
	public static void createInventories(){
		actionMenuInv = Bukkit.createInventory(null, 9, MessageHandler.getMessage("aaTitle"));
		ItemStack item = null;
		ItemMeta meta = null;
		item = new ItemStack(Material.SNOW_BALL);
		meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("aaTitle1"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(1, item);
		item.setType(Material.PAPER);
		meta.setDisplayName(MessageHandler.getMessage("aaTitle2"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(2, item);
		item.setType(Material.SLIME_BALL);
		meta.setDisplayName(MessageHandler.getMessage("aaTitle3"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(3, item);
		item.setType(Material.IRON_INGOT);
		meta.setDisplayName(MessageHandler.getMessage("aaTitle4"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(4, item);
		item.setType(Material.GOLD_INGOT);
		meta.setDisplayName(MessageHandler.getMessage("aaTitle5"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(5, item);
		ConfigurationSection section = Configuration.getConfig().getConfigurationSection("general.commandObj");
		if(section != null && section.getBoolean("enabled")){
			String type = section.getString("itemType");
			if(type != null){
				Material mat = null;
				try{
					mat = Material.valueOf(type.toUpperCase());
				}finally{
					if(mat != null){
						ItemStack cmdItem = new ItemStack(mat);
						String title = section.getString("itemTitle");
						ItemMeta cmdMeta = cmdItem.getItemMeta();
						if(title != null){
							cmdMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
						}
						List<String> cmdLore = section.getStringList("itemLore");
						List<String> finalLore = new ArrayList<String>();
						if(cmdLore != null && cmdLore.isEmpty() == false){
							for(String str : cmdLore){
								finalLore.add(ChatColor.translateAlternateColorCodes('&', str));
							}
							cmdMeta.setLore(finalLore);
						}
						cmdItem.setItemMeta(cmdMeta);
						String command = section.getString("command");
						if(command != null){
							addedCmd = command;
							consoleExe = section.getBoolean("consoleExecution");
							addedCmdType = mat.name();
							actionMenuInv.setItem(6,cmdItem);
						}
					}
				}
			}
		}else{
		}
		//add additional command items
		if(Main.kitsEnabled == false || Main.enableCurrency == false) return;
		item = new ItemStack(Material.BOOK);
		meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("kitShopTitle"));
		item.setItemMeta(meta);
		actionMenuInv.setItem(0, item);
		kitShopInv = Bukkit.createInventory(null, 45, MessageHandler.getMessage("kitShopTitle"));
		Map<String, ItemStack> kits = new LinkedHashMap<String, ItemStack>();
		kits.put("Dasher", new ItemStack(Material.FEATHER));
		kits.put("Grenadier", new ItemStack(Material.EGG));
		kits.put("Sprayer", new ItemStack(Material.STONE_AXE));
		kits.put("Sniper", new ItemStack(Material.GOLD_HOE));
		kits.put("Bomber", new ItemStack(Material.EGG, 2));
		kits.put("Demo", new ItemStack(Material.STONE_PLATE));
		kits.put("Rocketman", new ItemStack(Material.IRON_AXE));
		kits.put("Blaster", new ItemStack(Material.IRON_HOE));
		kits.put("Gunner", new ItemStack(Material.GOLD_AXE));
		kits.put("Reloader", new ItemStack(Material.IRON_INGOT));
		kits.put("Healer", new ItemStack(Material.STICK));
		kits.put("Heavy", new ItemStack(Material.SNOW_BALL, 2));
		kits.put("Blocker", new ItemStack(Material.BLAZE_ROD));
		kits.put("Blinder", new ItemStack(Material.STONE_HOE));
		kits.put("Charger", new ItemStack(Material.FEATHER, 2));
		kits.put("Juggernaut", new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		kits.put("Bouncer", new ItemStack(Material.SLIME_BALL));
		item = new ItemStack(Material.STAINED_GLASS_PANE);
		meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		boolean b = new Random().nextBoolean();
		if(b){
			item.setDurability((short) 11);
		}else{
			item.setDurability((short) 14);
		}
		for(int i = 0; i < 9; i++){
			kitShopInv.setItem(i, item);
		}
		kitShopInv.setItem(9, item);
		kitShopInv.setItem(17, item);
		if(b){
			item.setDurability((short) 10);
		}else{
			item.setDurability((short) 4);
		}
		kitShopInv.setItem(18, item);
		kitShopInv.setItem(26, item);
		if(b){
			item.setDurability((short) 14);
		}else{
			item.setDurability((short) 11);
		}
		kitShopInv.setItem(27, item);
		kitShopInv.setItem(35, item);
		for(int i = 36; i < 45; i++){
			kitShopInv.setItem(i, item);
		}
		for(String kit : kits.keySet()){
			int price = Configuration.getConfig().getInt("currency.prices." + kit.toLowerCase());
			if(price <= 0) continue;
			if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled") == false) continue;
			if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".permissionRequired") == false) continue;
			item = kits.get(kit);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + kit);
			List<String> lore = new ArrayList<String>();
			lore.add(MessageHandler.getMessage("priceTitle", "%price%", "" + price));
			meta.setLore(lore);
			item.setItemMeta(meta);
			kitShopInv.addItem(kits.get(kit));
		}
		kits.clear();
		kits = null;
		confirmInv = Bukkit.createInventory(null, 45, MessageHandler.getMessage("confTitle"));
		ItemStack confItem = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta confmeta = confItem.getItemMeta();
		confmeta.setDisplayName(MessageHandler.getMessage("confBlockTitle"));
		confItem.setItemMeta(confmeta);
		ItemStack rejItem = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta rejMeta = rejItem.getItemMeta();
		rejMeta.setDisplayName(MessageHandler.getMessage("rejBlockTitle"));
		rejItem.setItemMeta(rejMeta);
		for(int i = 0; i < 5; i++){
			for(int pos = 0; pos < 9; pos ++){
				if(pos < 3){
					int slot = (i * 9) + pos;
					confirmInv.setItem(slot, confItem);
				}else if(pos > 5){
					int slot = (i * 9) + pos;
					confirmInv.setItem(slot, rejItem);
				}
			}
		}
	}
	public static void handleClick(String title, ItemStack item, Player player, Main main){
		if(title.equals(MessageHandler.getMessage("aaTitle"))){
			if(item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
				String name = item.getItemMeta().getDisplayName();
				if(name.equals(MessageHandler.getMessage("kitShopTitle"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							if(kitShopInv != null){
								player.openInventory(kitShopInv);
							}
						}
					});
				}else if(name.equals(MessageHandler.getMessage("aaTitle1"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							player.performCommand("toggle");
						}
					});
				}else if(name.equals(MessageHandler.getMessage("aaTitle2"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							player.performCommand("stats");
						}
					});
				}else if(name.equals(MessageHandler.getMessage("aaTitle3"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							player.performCommand("stats general");
						}
					});
				}else if(name.equals(MessageHandler.getMessage("aaTitle4"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							player.performCommand("leave");
						}
					});
				}else if(name.equals(MessageHandler.getMessage("aaTitle5"))){
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							player.performCommand("join");
						}
					});
				}else if(addedCmdType != null && addedCmdType.equals(item.getType().name())){
					String finalCommand = addedCmd.replaceAll("%player%", player.getName());
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
							if(consoleExe){
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
							}else{
								player.performCommand(finalCommand);
							}
						}
					});
				}
			}
		}else if(title.equals(MessageHandler.getMessage("kitShopTitle"))){
			if(item != null && item.getType() != Material.STAINED_GLASS_PANE && item.getType() != Material.AIR){
				if(item.getType() == Material.DIAMOND || item .getType() == Material.EMERALD) return;
				if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
					String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase();
					confMap.put(player.getUniqueId(), name);
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.openInventory(confirmInv);
						}
					});
				}	
			}
		}else if(title.equals(MessageHandler.getMessage("confTitle"))){
			if(item != null && item.getType() != Material.AIR){
				if(item.getType() == Material.EMERALD_BLOCK){
					if(player.hasPermission("paintball.kit." + confMap.get(player.getUniqueId()).toLowerCase())){
						player.sendMessage(MessageHandler.getMessage("alreadyHavePerm"));
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.closeInventory();
							}
						});
						return;
					}
					List<String> commands = Configuration.getConfig().getStringList("currency.commands");
					if(commands.isEmpty()){
						player.sendMessage(MessageHandler.getMessage("noCmdsRan"));
						Bukkit.getScheduler().runTask(main, new Runnable() {
							@Override
							public void run() {
								player.closeInventory();
							}
						});
						return;
					}else{
						String uuid = player.getUniqueId().toString();
						int price = Configuration.getConfig().getInt("currency.prices." + confMap.get(player.getUniqueId()));
						TaskHandler.runTask(new Runnable() {
							@Override
							public void run() {
								if(MySQL.hasValidConnection() == false){
									MySQL.connectToDatabase();
								}
								int curr = MySQL.getInt(uuid, "currency");
								if(curr > price){
									int remaining = curr - price;
									MySQL.setInt(uuid, "currency", remaining);
								}
								Bukkit.getScheduler().runTask(main, new Runnable() {
									@Override
									public void run() {
										if(price > curr){
											player.sendMessage(MessageHandler.getMessage("notEnoughCurr"));
											confMap.remove(player.getUniqueId());
											player.closeInventory();
										}else{
											for(String command : commands){
												String finalCommand = command.replaceAll("%name%", player.getName()).replaceAll("%kit%", confMap.get(player.getUniqueId()));
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
											}
											if(Main.announcePurchases){
												String msg = MessageHandler.getMessage("purchaseAnnounce", "%player%", player.getName(), "%kit%", confMap.get(player.getUniqueId()));
												for(UUID id : Utils.getPlayers()){
													Bukkit.getPlayer(id).sendMessage(msg);
												}
											}
											player.sendMessage(MessageHandler.getMessage("successfulPurchase"));
											confMap.remove(player.getUniqueId());
											player.closeInventory();
										}
									}
								});
							}
						});
					}
				}else if(item.getType() == Material.REDSTONE_BLOCK){
					confMap.remove(player.getUniqueId());
					player.sendMessage(MessageHandler.getMessage("purchaseDeclined"));
					Bukkit.getScheduler().runTask(main, new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
						}
					});
				}
			}
		}
	}
	public static void handleClose(UUID id){
		if(confMap.containsKey(id)){
			confMap.remove(id);
		}
	}
	public static void openInv(Player player){
		if(actionMenuInv != null){
			player.openInventory(actionMenuInv);
		}
	}
	public static int getSize(){
		return confMap.size();
	}
}
