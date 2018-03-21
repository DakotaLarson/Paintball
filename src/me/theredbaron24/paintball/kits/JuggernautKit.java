package me.theredbaron24.paintball.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class JuggernautKit extends Kit{

	private static Map<UUID, Integer> hits = new HashMap<UUID, Integer>();
	
	public static int hit(UUID id){
		if(hits.containsKey(id)){
			if(hits.get(id) > 0){
				hits.put(id, hits.get(id) - 1);
				return hits.get(id);
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}
	public static void register(UUID id){
		hits.put(id, Kit.juggHits);
	}
	public static void unRegister(UUID id){
		if(hits.containsKey(id)){
			hits.remove(id);
		}
	}
	public static void handleMatchEnd(){
		hits.clear();
	}
	public static int getJuggHits(){
		return juggHits;
	}
	public static void respawn(Player player){
		hits.put(player.getUniqueId(), juggHits);
		player.setExp(1f);
	}
	public static int getSize(){
		return hits.size();
	}
}
