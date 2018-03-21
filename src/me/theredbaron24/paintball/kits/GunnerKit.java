package me.theredbaron24.paintball.kits;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.Utils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


public class GunnerKit extends Kit{

	public static void shoot(Player player, Main main){
		if(Utils.removeItem(player, Material.SNOW_BALL, MessageHandler.getMessage("paintballTitle"), gunnerInv)){
			player.playSound(player.getEyeLocation(), Sound.BLOCK_WOOD_BREAK, 2.0F, 1.0F);
			for(int i = 0; i < gunnerSize; i++){
				Snowball snowball = player.launchProjectile(Snowball.class);
				Vector vector = new Vector(player.getLocation().getDirection().getX() + (Math.random() - 0.45D) / gunnerSpread, 
				player.getLocation().getDirection().getY() + (Math.random() - 0.45D) / gunnerSpread, 
		        player.getLocation().getDirection().getZ() + (Math.random() - 0.45D) / gunnerSpread).normalize();
				snowball.setVelocity(vector.multiply(gunnerSpeed));
				snowball.setMetadata(KitType.GUNNER.name(), new FixedMetadataValue(main, snowball));
			}
		}
	}
}
