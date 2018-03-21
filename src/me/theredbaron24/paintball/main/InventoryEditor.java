package me.theredbaron24.paintball.main;


import me.theredbaron24.paintball.kits.BouncerKit;
import me.theredbaron24.paintball.kits.ChargerKit;
import me.theredbaron24.paintball.kits.DasherKit;
import me.theredbaron24.paintball.kits.JuggernautKit;
import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.kits.ReloaderKit;
import me.theredbaron24.paintball.utils.GameStatus;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InventoryEditor extends Kit{
	
	public static void handlePlayerJoin(Player player, int team, Main main){
		PlayerInventory inv = player.getInventory();
		inv.clear();
		ItemStack item = null;
		ItemMeta meta = null;
		if(Main.kitsEnabled){
			item = new ItemStack(Material.EMERALD);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("emeraldTitle"));
			item.setItemMeta(meta);
			inv.setItem(6, item);
			item.setType(Material.DIAMOND);
			meta.setDisplayName(MessageHandler.getMessage("diamondTitle"));
			item.setItemMeta(meta);
			inv.setItem(5, item);
		}
		GameStatus status = Utils.getGameStatus();
		if(status == GameStatus.INITIALIZING){
			if(Main.selectTeamPerm == false || player.hasPermission("paintball.selectteam")){
				item = new ItemStack(Material.WOOL);
				item.setDurability((short) 11);
				meta = item.getItemMeta();
				meta.setDisplayName(MessageHandler.getMessage("woolBlueTeamSelect"));
				item.setItemMeta(meta);
				inv.setItem(3, item);
				item.setDurability((short) 14);
				meta.setDisplayName(MessageHandler.getMessage("woolRedTeamSelect"));
				item.setItemMeta(meta);
				inv.setItem(2, item);
			}
		}else if(status == GameStatus.RUNNING){
			handleMatchStart(player, team);
		}else{
			player.getInventory().setItem(2, VoteHandler.getVotingBlock());
		}
	}
	public static void handleForceEnd(Player player){
		PlayerInventory inv = player.getInventory();
		Utils.removeArmor(player);
		ItemStack item1 = inv.getItem(6);
		ItemStack item2 = inv.getItem(5);
		ItemStack helm = inv.getHelmet();
		inv.clear();
		inv.setItem(6, item1);
		inv.setItem(5, item2);
		inv.setHelmet(helm);
	}
	public static void handleElm(Player player){
		PlayerInventory inv = player.getInventory();
		ItemStack item1 = inv.getItem(5);
		ItemStack item2 = inv.getItem(6);
		ItemStack item3 = inv.getItem(7);
		ItemStack[] armor = inv.getArmorContents();
		inv.clear();
		inv.setItem(5, item1);
		inv.setItem(6, item2);
		inv.setItem(7, item3);
		inv.setArmorContents(armor);
	}
	public static void handleMatchTeleport(Player player){
		PlayerInventory inv = player.getInventory();
		ItemStack item1 = inv.getItem(5);
		ItemStack item2 = inv.getItem(6);
		ItemStack helm = inv.getHelmet();
		inv.clear();
		inv.setItem(5, item1);
		inv.setItem(6, item2);
		inv.setHelmet(helm);
		if(Main.selectTeamPerm == false || player.hasPermission("paintball.selectteam")){
			ItemStack item = new ItemStack(Material.WOOL);
			item.setDurability((short) 11);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("woolBlueTeamSelect"));
			item.setItemMeta(meta);
			inv.setItem(3, item);
			item.setDurability((short) 14);
			meta.setDisplayName(MessageHandler.getMessage("woolRedTeamSelect"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
		}
	}
	
	public static void handleMatchStart(Player player, int team){
		PlayerInventory inv = player.getInventory();
		Material[] materials = {Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS};
		for (Material material : materials){
			ItemStack item = new ItemStack(material);
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if(team == 0){
				meta.setColor(Color.RED);
				meta.setDisplayName(MessageHandler.getMessage("redTeamTitle"));
			}else{
				meta.setColor(Color.BLUE);
				meta.setDisplayName(MessageHandler.getMessage("blueTeamTitle"));
			}
			item.setItemMeta(meta);
			if(material == Material.LEATHER_CHESTPLATE){
				inv.setChestplate(item);
			}else if(material == Material.LEATHER_LEGGINGS){
				inv.setLeggings(item);
			}else{
				inv.setBoots(item);
			}			
		}
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageHandler.getMessage("reloadBarTitle"));
		item.setItemMeta(meta);
		inv.setItem(0, item);
		item.setType(Material.DIAMOND_HOE);
		meta.setDisplayName(MessageHandler.getMessage("pbGunTitle"));
		item.setItemMeta(meta);
		inv.setItem(1, item);
		inv.setItem(2, null);
		inv.setItem(3, null);
		inv.setItem(4, null);
		item.setType(Material.WOOL);
		if(team == 0){
			item.setDurability((short) 14);
			meta.setDisplayName(MessageHandler.getMessage("redTeamTitle"));
		}else{
			item.setDurability((short) 11);
			meta.setDisplayName(MessageHandler.getMessage("blueTeamTitle"));
		}
		item.setItemMeta(meta);
		inv.setItem(7, item);
		Utils.addSnowballs(Main.ammoCount, player, false);
		loadKitItems(player);
	}
	public static void handleMatchEnd(Player player){
		PlayerInventory inv = player.getInventory();
		ItemStack item1 = inv.getItem(5);
		ItemStack item2 = inv.getItem(6);
		ItemStack helm = inv.getHelmet();
		inv.clear();
		inv.setItem(5, item1);
		inv.setItem(6, item2);
		inv.setHelmet(helm);
		inv.setItem(2, VoteHandler.getVotingBlock());
	}
	public static void handleRespawn(Player player){
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		Utils.addSnowballs(Main.ammoCount, player, false);
		meta.setDisplayName(MessageHandler.getMessage("reloadBarTitle"));
		item.setItemMeta(meta);
		player.getInventory().setItem(0, item);
		loadKitItems(player);
	}
	public static void handleDeath(Player player){
		ReloadHandler.cancel(player);
		PlayerInventory inv = player.getInventory();
		inv.remove(Material.SNOW_BALL);
		inv.setItem(2, null);
		inv.setItem(3, null);
	}
	private static void loadKitItems(Player player){
		KitType kit = Kit.getKits().get(player.getUniqueId());
		ItemStack item = null;
		ItemMeta meta = null;
		PlayerInventory inv = player.getInventory();
		if(kit == null){
			player.sendMessage(MessageHandler.getMessage("kitNotLoaded"));
			return;
		}
		switch (kit){
		case BLINDER:
			item = new ItemStack(Material.STONE_HOE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("blinderTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			item.setType(Material.ENDER_PEARL);
			item.setAmount(Kit.blinderCount);
			meta.setDisplayName(MessageHandler.getMessage("blinderCharge"));
			item.setItemMeta(meta);
			inv.setItem(3, item);
			break;
		case BLOCKER:
			item = new ItemStack(Material.BLAZE_ROD, Kit.blockerCount);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("blockerTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case BOMBER:
			item = new ItemStack(Material.EGG, Kit.bomberCount);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("bombTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case CHARGER:
			ChargerKit.register(player);
			if(chargerSpeedPot){
				item = new ItemStack(Material.SPLASH_POTION, chargerSpeedCount);
				PotionMeta pMeta = (PotionMeta) item.getItemMeta();
				pMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, chargerSpeedTime * 20, chargerSpeedAmp - 1), false);
				pMeta.setDisplayName(MessageHandler.getMessage("chargerSpeedPot"));
				item.setItemMeta(pMeta);
				inv.setItem(2, item);
			}
			if(chargerJumpPot){
				item = new ItemStack(Material.SPLASH_POTION, chargerJumpCount);
				PotionMeta pMeta = (PotionMeta) item.getItemMeta();
				pMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, chargerJumpTime * 20, chargerJumpAmp - 1), false);
				pMeta.setDisplayName(MessageHandler.getMessage("chargerJumpPot"));
				item.setItemMeta(pMeta);
				inv.setItem(3, item);
			}
			break;
		case DEMO:
			if(Kit.demoDetonate){
				item = new ItemStack(Material.GOLD_INGOT);
				meta = item.getItemMeta();
				meta.setDisplayName(MessageHandler.getMessage("detonatorTitle"));
				item.setItemMeta(meta);
				inv.setItem(2, item);
				item.setType(Material.STONE_PLATE);
				item.setAmount(Kit.demoCount);
				meta.setDisplayName(MessageHandler.getMessage("mineTitle"));
				item.setItemMeta(meta);
				inv.setItem(3, item);
			}else{
				item = new ItemStack(Material.STONE_PLATE, Kit.demoCount);
				meta = item.getItemMeta();
				meta.setDisplayName(MessageHandler.getMessage("mineTitle"));
				item.setItemMeta(meta);
				inv.setItem(2, item);
			}
			break;
		case GRENADIER:
			item = new ItemStack(Material.EGG, Kit.grenadierCount);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("grenadeTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case GUNNER:
			item = new ItemStack(Material.GOLD_AXE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("gunnerTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case HEALER:
			item = new ItemStack(Material.STICK, Kit.healerCount);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("healerTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case HEAVY:
			Utils.addSnowballs(heavyCount, player, true);
			break;
		case JUGGERNAUT:
			JuggernautKit.register(player.getUniqueId());
			break;
		case NONE:
			break;
		case RELOADER:
			ReloaderKit.register(player.getUniqueId());
			break;
		case ROCKETMAN:
			item = new ItemStack(Material.IRON_AXE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("rocketTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			item.setType(Material.FIREBALL);
			item.setAmount(Kit.rocketmanRocketCount);
			meta.setDisplayName(MessageHandler.getMessage("rcChargeTitle"));
			item.setItemMeta(meta);
			inv.setItem(3, item);
			break;
		case SNIPER:
			item = new ItemStack(Material.GOLD_HOE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("sniperTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case DASHER:
			DasherKit.register(player);
			if(dasherSpeedPot){
				item = new ItemStack(Material.SPLASH_POTION, dasherSpeedCount);
				PotionMeta pMeta = (PotionMeta) item.getItemMeta();
				pMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, dasherSpeedTime * 20, dasherSpeedAmp - 1), false);
				pMeta.setDisplayName(MessageHandler.getMessage("dasherSpeedPot"));
				item.setItemMeta(pMeta);
				inv.setItem(2, item);
			}
			if(dasherJumpPot){
				item = new ItemStack(Material.SPLASH_POTION, dasherJumpCount);
				PotionMeta pMeta = (PotionMeta) item.getItemMeta();
				pMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, dasherJumpTime * 20, dasherJumpAmp - 1), false);
				pMeta.setDisplayName(MessageHandler.getMessage("dasherJumpPot"));
				item.setItemMeta(pMeta);
				inv.setItem(3, item);
			}
			break;
		case SPRAYER:
			item = new ItemStack(Material.STONE_AXE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("sprayerTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
			break;
		case BLASTER:
			item = new ItemStack(Material.IRON_HOE);
			meta = item.getItemMeta();
			meta.setDisplayName(MessageHandler.getMessage("blasterTitle"));
			item.setItemMeta(meta);
			inv.setItem(2, item);
		case BOUNCER:
			BouncerKit.register(player);
		default:
			break;
		}
	}
}
