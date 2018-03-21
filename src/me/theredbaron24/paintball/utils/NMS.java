package me.theredbaron24.paintball.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.theredbaron24.paintball.main.Main;

import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

public class NMS {

	private static void sendPacket(Player player, Object packet){
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private static Class<?> getNMSClass(String name){
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}
	private static Class getNestedNMSClass(String superName, String name){
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + superName + "$" + name);
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}
	private static Class<?> getOBCClass(String name){
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public static void resetTitle(Player player){
		sendTitle(player, 0, 1, 0, "", "");
	}
	public static void sendTabTitle(Player player, String headerText, String footerText ){
		if(Main.useNMSCode == false) return;
		if (headerText == null){
			headerText = "";
		}
		if (footerText == null){
			footerText = "";
		}
		try {
			Object header = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] {headerText});
			Object footer = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] {footerText});
			Object packet = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]).newInstance(new Object[0]);
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, header);
			Field footerField = packet.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(packet, footer);
			sendPacket(player, packet);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public static void sendActionBar(Player player, String message, int fadeIn, int stay, int fadeOut){
		if(Main.useNMSCode == false) return;
		if(message == null) return;
		try {
				Object title = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] {message});
				Enum enumValue = Enum.valueOf(getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), "ACTIONBAR");

				Object packet = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{ getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE}).newInstance(new Object[] {enumValue, title, fadeIn, stay, fadeOut});
				sendPacket(player, packet);
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static void spawnFirework(Location location, FireworkEffect effect){
		if(Main.useNMSCode == false) return;
		try{
			Constructor<?> constructor = getNMSClass("EntityFireworks").getConstructor(new Class[] { getNMSClass("World")});
			Object craftWorld = getOBCClass("CraftWorld").cast(location.getWorld());
			Object world = craftWorld.getClass().getMethod("getHandle", new Class[0]).invoke(craftWorld, new Object[0]);
			Object firework = constructor.newInstance(new Object[] {world});
			Object craftEntity = firework.getClass().getMethod("getBukkitEntity", new Class[0]).invoke(firework, new Object[0]);
			Firework fw = Firework.class.cast(craftEntity);
			FireworkMeta meta = fw.getFireworkMeta();
			meta.addEffect(effect);
			craftEntity.getClass().getMethod("setFireworkMeta", new Class[] {FireworkMeta.class}).invoke(craftEntity, new Object[] {meta});
			firework.getClass().getMethod("setPosition", new Class[] {Double.TYPE, Double.TYPE, Double.TYPE}).invoke(firework, new Object[]{location.getX(), location.getY(), location.getZ()});
			Boolean b = (Boolean) world.getClass().getMethod("addEntity", new Class[]{getNMSClass("Entity")}).invoke(world, new Object[]{firework});
			if(b){
				firework.getClass().getMethod("setInvisible", new Class[] {Boolean.TYPE}).invoke(firework, new Object[]{Boolean.TRUE});
				world.getClass().getMethod("broadcastEntityEffect", new Class[]{getNMSClass("Entity"), Byte.TYPE}).invoke(world, new Object[]{firework, Byte.valueOf((byte) 17)});
			}
			firework.getClass().getMethod("die", new Class[0]).invoke(firework, new Object[0]);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static void registerCommand(String label, Command command){
		try {
			Object craftServer = getOBCClass("CraftServer").cast(Main.main.getServer());
			Object cmdMap = craftServer.getClass().getMethod("getCommandMap", new Class[0]).invoke(craftServer, new Object[0]);
			cmdMap.getClass().getMethod("register", new Class[] {String.class, Command.class}).invoke(cmdMap, new Object[] {label, command});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String titleText, String subtitleText){
		try {
			if(titleText != null){
				Object title = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] {titleText});
				Enum enumValue = Enum.valueOf(getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), "TITLE");

				Object packet = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{ getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE}).newInstance(new Object[] {enumValue, title, fadeIn, stay, fadeOut});
				sendPacket(player, packet);
			}
			if(subtitleText != null){
				Object subtitle = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] {subtitleText});
				Enum enumValue = Enum.valueOf(getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), "SUBTITLE");

				Object packet = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{ getNestedNMSClass("PacketPlayOutTitle", "EnumTitleAction"), getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE}).newInstance(new Object[] {enumValue, subtitle, fadeIn, stay, fadeOut});
				sendPacket(player, packet);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	/*

	Legacy tab header/footer method. Used Versions < 1.12.

	public static void sendTabTitle(Player player, String header, String footer){
		if(Main.useNMSCode == false) return;
		if (header == null){
			header = "";
		}
		if (footer == null){
			footer = "";
		}
		try {
			Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + header + "\"}" });
			Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a",new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNMSClass("IChatBaseComponent") });
			Object packet = titleConstructor.newInstance(new Object[] { tabHeader });
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, tabFooter);
			sendPacket(player, packet);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	Legacy Title Method Used Versions < 1.12

	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle){
		if(Main.useNMSCode == false) return;
		try {
			if (title != null){
				Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
				Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
				Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] {
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				Object titlePacket = titleConstructor.newInstance(new Object[] {enumTitle, chatTitle, fadeIn, stay, fadeOut });
				sendPacket(player, titlePacket);
			}
			if (subtitle != null){
				Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
				Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
				Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] {
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
				Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { enumSubtitle, chatSubtitle, fadeIn, stay, fadeOut });
				sendPacket(player, subtitlePacket);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	Legacy Action Bar Method Used Versions < 1.12.

	public static void sendActionBar(Player player, String message){
		if(Main.useNMSCode == false) return;
		if(message == null) return;
		try{
			Object msg = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + message + "\"}" });
			Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { getNMSClass("IChatBaseComponent"), Byte.TYPE });
			Object packet = constructor.newInstance(new Object[] { msg, Byte.valueOf((byte) 2) });
			sendPacket(player, packet);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	 */
}
