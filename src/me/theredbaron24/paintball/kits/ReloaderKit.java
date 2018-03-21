package me.theredbaron24.paintball.kits;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReloaderKit extends Kit{

	private static Set<UUID> players = new HashSet<UUID>();
	
	public static boolean reload(UUID id){
		return players.contains(id);
	}
	
	public static int getTime(){
		return reloaderTime;
	}
	
	public static void register(UUID id){
		if(players.contains(id) == false){
			players.add(id);
		}
	}
	public static void unRegister(UUID id){
		if(players.contains(id)){
			players.remove(id);
		}
	}
	public static void handleMatchEnd(){
		players.clear();
	}
	public static int getSize(){
		return players.size();
	}
}
