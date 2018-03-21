package me.theredbaron24.paintball.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TokenHandler {
	
	private static Map<UUID, Integer> tokens = new HashMap<UUID, Integer>();

	public static void handleClick(Player player){
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			if(item.getItemMeta().getDisplayName().startsWith(MessageHandler.getMessage("tokenCheck"))){
				if(DeathManager.isPlayerRespawning(player)){
					if(tokens.containsKey(player.getUniqueId())){
						int tokenCount = tokens.get(player.getUniqueId());
						if(tokenCount >= Main.neededTokens){
							tokens.put(player.getUniqueId(), tokenCount - Main.neededTokens);
							String id = player.getUniqueId().toString();
							int finalCount = tokenCount - Main.neededTokens;
							TaskHandler.runTask(new Runnable() {
								@Override
								public void run() {
									if(MySQL.hasValidConnection() == false){
										MySQL.connectToDatabase();
									}
									MySQL.setInt(id, "tokens", finalCount);
								}
							});
							player.getInventory().setItemInMainHand(null);
							Bukkit.getScheduler().runTask(Main.main, new Runnable() {
								@Override
								public void run() {
									DeathManager.forceRespawn(player);
								}
							});
						}else{
							player.sendMessage(MessageHandler.getMessage("notEnoughTokens"));
						}
					}else{
						player.sendMessage(MessageHandler.getMessage("tokenNoRegister"));
					}
				}else{
					player.sendMessage(MessageHandler.getMessage("tokenNoUse"));
				}
			}
		}
	}
	public static void handleJoin(Player player){
		if(Main.enableTokens){
			UUID id = player.getUniqueId();
			TaskHandler.runTask(new Runnable() {
				@Override
				public void run() {
					if(MySQL.hasValidConnection() == false){
						MySQL.connectToDatabase();
					}
					int tokenCount = MySQL.getInt(id.toString(), "tokens");
					tokens.put(id, tokenCount);
				}
			});
		}
	}
	public static void handleLeave(Player player){
		if(TokenHandler.getTokens().containsKey(player.getUniqueId()) == false) return;
		String uuid = player.getUniqueId().toString();
		int count = TokenHandler.getTokens().get(player.getUniqueId());
		TaskHandler.runTask(new Runnable() {
			@Override
			public void run() {
				if(MySQL.hasValidConnection() == false){
					MySQL.connectToDatabase();
				}
				MySQL.setInt(uuid, "tokens", count);
			}
		});
		tokens.remove(player.getUniqueId());
	}
	public static void handleDeath(Player player){
		if(Main.enableTokens){
			if(tokens.containsKey(player.getUniqueId())){
				int tokenCount = tokens.get(player.getUniqueId());
				ItemStack item = new ItemStack(Material.REDSTONE);
				ItemMeta meta = item.getItemMeta();
				if(tokenCount >= Main.neededTokens){
					meta.setDisplayName(MessageHandler.getMessage("tokenTitle", "%count%", "" + tokenCount));
				}else{
					meta.setDisplayName(MessageHandler.getMessage("tokenTitle1", "%count%", "" + tokenCount));
				}
				item.setItemMeta(meta);
				player.getInventory().setItem(4, item);
			}else{
				player.sendMessage(MessageHandler.getMessage("tokenNoRegister"));
			}
		}
	}
	public static Map<UUID, Integer> getTokens(){
		return tokens;
	}
}
