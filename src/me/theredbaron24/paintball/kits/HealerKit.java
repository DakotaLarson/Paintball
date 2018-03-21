package me.theredbaron24.paintball.kits;

import me.theredbaron24.paintball.main.ArmorHandler;
import me.theredbaron24.paintball.main.DeathManager;
import me.theredbaron24.paintball.main.ScoreboardHandler;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HealerKit extends Kit{

	public static void healSelf(Player player){
		if(DeathManager.getHits(player) == DeathManager.playerHits){
			player.sendMessage(MessageHandler.getMessage("healerAtFull"));
			return;
		}
		if(player.getInventory().getItemInMainHand().getAmount() == 1){
			player.getInventory().setItemInMainHand(null);
		}else{
			ItemStack item = player.getInventory().getItemInMainHand();
			item.setAmount(item.getAmount() -1);
			player.getInventory().setItemInMainHand(item);
		}
		DeathManager.getHits().put(player.getUniqueId(), DeathManager.playerHits);
		ArmorHandler.respawn(player);
		player.setExp(1f);
		player.sendMessage(MessageHandler.getMessage("healerHealSelf"));
		ScoreboardHandler.handleHeal(null, player);
		player.playSound(player.getEyeLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.0F);
	}
	public static void healOther(Player player, Player healed){
		if(DeathManager.getHits(healed) == DeathManager.playerHits){
			player.sendMessage(MessageHandler.getMessage("healerPlayerAtFull", "%player%", Utils.getColoredName(healed)));
			return;
		}
		if(player.getInventory().getItemInMainHand().getAmount() == 1){
			player.getInventory().setItemInMainHand(null);
		}else{
			ItemStack item = player.getInventory().getItemInMainHand();
			item.setAmount(item.getAmount() -1);
			player.getInventory().setItemInMainHand(item);
		}
		DeathManager.getHits().put(healed.getUniqueId(), DeathManager.playerHits);
		ArmorHandler.respawn(healed);
		healed.setExp(1f);
		player.sendMessage(MessageHandler.getMessage("healerHealOther", "%player%", Utils.getColoredName(healed)));
		healed.sendMessage(MessageHandler.getMessage("healerHealOther1", "%player%", Utils.getColoredName(player)));
		player.playSound(player.getEyeLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.0F);
		healed.playSound(healed.getEyeLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.0F);
		ScoreboardHandler.handleHeal(healed, player);
	}
	public static int getHealPoints(){
		return healerPoints;
	}
	public static int getHealCurr(){
		return healerCurr;
	}
}
