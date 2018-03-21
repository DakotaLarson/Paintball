package me.theredbaron24.paintball.main;

import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorHandler {

	public static void damage(Player player){
		if(Main.colorArmor == false) return;
		int hits = DeathManager.getHits(player);
		if(hits == 1){
			PlayerInventory inv = player.getInventory();
			ItemStack item = inv.getLeggings();
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.BLACK);
			meta.setDisplayName(MessageHandler.getMessage("carefulArmorTitle"));
			item.setItemMeta(meta);
			inv.setLeggings(item);
		}else if(hits == 2){
			PlayerInventory inv = player.getInventory();
			ItemStack item = inv.getBoots();
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("carefulArmorTitle"));
			meta.setColor(Color.BLACK);
			item.setItemMeta(meta);
			inv.setBoots(item);
		}
	}
	public static void kill(Player player){
		if(Main.colorArmor == false) return;
		PlayerInventory inv = player.getInventory();
		ItemStack item = inv.getBoots();
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("respawningArmorTitle"));
		meta.setColor(Color.BLACK);
		item.setItemMeta(meta);
		inv.setBoots(item);
		item = inv.getLeggings();
		item.setItemMeta(meta);
		inv.setLeggings(item);
		item = inv.getChestplate();
		item.setItemMeta(meta);
		inv.setChestplate(item);
	}
	public static void respawn(Player player){
		if(Main.colorArmor == false) return;
		if(Utils.getRed().contains(player.getUniqueId())){
			PlayerInventory inv = player.getInventory();
			ItemStack item = inv.getBoots();
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("redTeamArmorTitle"));
			meta.setColor(Color.RED);
			item.setItemMeta(meta);
			inv.setBoots(item);
			item = inv.getLeggings();
			item.setItemMeta(meta);
			inv.setLeggings(item);
			item = inv.getChestplate();
			item.setItemMeta(meta);
			inv.setChestplate(item);
		}else{
			PlayerInventory inv = player.getInventory();
			ItemStack item = inv.getBoots();
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("blueTeamArmorTitle"));
			meta.setColor(Color.BLUE);
			item.setItemMeta(meta);
			inv.setBoots(item);
			item = inv.getLeggings();
			item.setItemMeta(meta);
			inv.setLeggings(item);
			item = inv.getChestplate();
			item.setItemMeta(meta);
			inv.setChestplate(item);
		}
	}
}
