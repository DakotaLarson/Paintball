package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.theredbaron24.paintball.utils.MessageHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSelectionHandler {

	private static Inventory inv = null;
	
	public static void createKitInv(){
		if(Main.kitsEnabled == false) return;
		Map<String, ItemStack> kits = new LinkedHashMap<String, ItemStack>();
		kits.put("None", new ItemStack(Material.DIAMOND_HOE));
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
		inv = Bukkit.createInventory(null, 45, MessageHandler.getMessage("kitSelTitle"));
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		boolean b = new Random().nextBoolean();
		if(b){
			item.setDurability((short) 11);
		}else{
			item.setDurability((short) 14);
		}
		for(int i = 0; i < 9; i++){
			inv.setItem(i, item);
		}
		inv.setItem(9, item);
		inv.setItem(17, item);
		if(b){
			item.setDurability((short) 10);
		}else{
			item.setDurability((short) 4);
		}
		inv.setItem(18, item);
		inv.setItem(26, item);
		if(b){
			item.setDurability((short) 14);
		}else{
			item.setDurability((short) 11);
		}
		inv.setItem(27, item);
		inv.setItem(35, item);
		for(int i = 36; i < 45; i++){
			inv.setItem(i, item);
		}
		for(String kit : kits.keySet()){
			item = kits.get(kit);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + kit);
			List<String> lore = new ArrayList<String>();
			if(kit.equals("None")){
				lore.add(MessageHandler.getMessage("spawnWithNone"));
			}else{
				if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled") == false) continue;
				String data = Configuration.getConfig().getString("kits." + kit.toLowerCase() + ".description", "No Description");
				List<String> messages = splitText(data);
				if(messages == null){
					lore.add(MessageHandler.getMessage("descTitle", "%desc%", data));
				}else{
					lore.addAll(messages);
				}
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(kits.get(kit));
		}
		kits.clear();
		kits = null;
	}
	public static void openInv(Player player){
		if(inv != null){
			player.openInventory(inv);
		}
	}
	public static void handleClick(Player player, ItemStack item, Main main){
		if(item != null && item.getType() != Material.STAINED_GLASS_PANE && item.getType() != Material.EMERALD && item.getType() != Material.DIAMOND){
			if(item.hasItemMeta()){
				String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				if(Main.cmdPrefixes){
					player.performCommand("pb kit " + name);
				}else{
					player.performCommand("kit " + name);
				}
				Bukkit.getScheduler().runTask(main, new Runnable() {
					@Override
					public void run() {
						player.closeInventory();
					}
				});
			}
		}
	}
	private static List<String> splitText(String data){
		if(data.length() <= 30){
			return null;
		}else{
			for(int i = 30; i < data.length(); i ++){
				if(data.charAt(i) == ' '){
					List<String> messages = new ArrayList<String>();
					messages.add(MessageHandler.getMessage("descTitle", "%desc%", data.substring(0, i)));
					messages.add(MessageHandler.getMessage("descTitle", "%desc%", data.substring(i, data.length())));
					return messages;
				}
			}
			return null;
		}
	}
	public static Inventory getInv(){
		return inv;
	}
}
