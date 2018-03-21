package me.theredbaron24.paintball.kits;

import org.bukkit.entity.Player;

public class ChargerKit {

	public static void register(Player player) {
		player.setWalkSpeed((float) (Kit.chargerSpeed / 10));
	}
	
	public static void unRegister(Player player){
		player.setWalkSpeed(.2f);
	}

}
