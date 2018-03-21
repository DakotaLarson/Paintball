package me.theredbaron24.paintball.utils;

import java.util.Random;

import me.theredbaron24.paintball.main.Main;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ExplosionHandler {

	private static Random random = new Random();
	
	public static void createExplosion(Player player, int ballCount, Location location, double velocity, String data, Main main){
		location.setY(location.getY() + 1d);
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), -1f, false, false);
		for (int i = 0; i < ballCount; i ++){
			Snowball snowball = (Snowball)location.getWorld().spawn(location, Snowball.class);
			snowball.setMetadata(data, new FixedMetadataValue(main, snowball));
			snowball.setShooter(player);
			Vector direction = snowball.getVelocity();
			direction.setX(random.nextDouble() * 2 - 1);
			direction.setY(random.nextDouble() * 2 - 1);
			direction.setZ(random.nextDouble() * 2 - 1);
			snowball.setVelocity(direction.multiply(velocity).normalize());
		}
	}	
}
