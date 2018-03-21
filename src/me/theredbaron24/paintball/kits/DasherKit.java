package me.theredbaron24.paintball.kits;

import org.bukkit.entity.Player;

public class DasherKit extends Kit{

	public static void register(Player player) {
		player.setWalkSpeed((float) Kit.dasherMulti / 10);
	}
	
	public static void unRegister(Player player){
		player.setWalkSpeed(.2f);
	}
}
