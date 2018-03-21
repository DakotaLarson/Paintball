package me.theredbaron24.paintball.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.ScoreboardHandler;

public class ControlPoint {

	private int initialHold;
	private int stage;
	private int maxStage;
	private int stageCheckInterval;
	private boolean allowContest;
	private Map<Integer, Set<Location>> stageBlocks;
	private float radius;
	private int height, negHeight;
	private int teamHoldPoints, teamContestPoints;
	private int pointInterval;
	private Location center;
	private int task;
	private int pointCheck;
	private Map<UUID, Float> awardPointMap;
	private Map<UUID, Float> awardCurrMap;
	private float playerPoints;
	private float playerCurrency;
	
	public void init(String title){
		pullConfigValues(title);
		pointCheck = 0;
		if(initialHold == 0){
			//neutral
			stage = 0;
			updateBlocks();
		}else if(initialHold > 0){
			//red control
			stage = maxStage;
			updateBlocks();
		}else{
			stage = maxStage * -1;
			updateBlocks();
		}
		if(pointInterval < 0){
			task = Bukkit.getScheduler().runTaskTimer(Main.main, new Runnable() {
				@Override
				public void run() {
					updateStage();
				}
			}, stageCheckInterval, stageCheckInterval).getTaskId();
		}else{
			task = Bukkit.getScheduler().runTaskTimer(Main.main, new Runnable() {
				@Override
				public void run() {
					pointCheck ++;
					updateStage();
					if(pointCheck >= pointInterval){
						pointCheck = 0;
						updatePoints();
					}
				}
			}, stageCheckInterval, stageCheckInterval).getTaskId();
		}
		
	}
	
	public void stop(){
		Bukkit.getScheduler().cancelTask(task);
		updatePoints();
		awardPointMap.clear();
		awardPointMap = null;
		awardCurrMap.clear();
		awardCurrMap = null;
	}
	
	private void pullConfigValues(String name){
		awardPointMap = new HashMap<UUID, Float>();
		awardCurrMap = new HashMap<UUID, Float>();
		stageBlocks = new HashMap<Integer, Set<Location>>();
		//get config values
	}
	
	public void reset(){
		if(initialHold == 0){
			//neutral
			stage = 0;
			updateBlocks();
		}else if(initialHold > 0){
			//red control
			stage = maxStage;
			updateBlocks();
		}else{
			stage = maxStage * -1;
			updateBlocks();
		}
		stageBlocks.clear();
		stageBlocks = null;
	}
	
	private void updatePoints(){
		if(stage == 0) return;
		if(teamHoldPoints > 0){
			if(stage == maxStage){
				//award red
				ScoreboardHandler.handleCPTeam(true, teamHoldPoints);
			}else if(stage == maxStage*-1){
				//award blue
				ScoreboardHandler.handleCPTeam(false, teamHoldPoints);
			}
		}if(teamContestPoints > 0){
			if(stage > 0 && stage < maxStage){
				//award red team
				ScoreboardHandler.handleCPTeam(true, teamContestPoints);
			}else if(stage < 0 && stage > maxStage*-1){
				//award blue team
				ScoreboardHandler.handleCPTeam(false, teamContestPoints);

			}
		}
		for(UUID id : awardPointMap.keySet()){
			int points = awardPointMap.get(id).intValue();
			int currency = awardCurrMap.get(id).intValue();
			//award points to player
			ScoreboardHandler.handleCPPlayer(Bukkit.getPlayer(id), points, currency);
		}
	}
	
	private void updateStage(){
		Set<UUID> bluePlayers = getContestingPlayers(Utils.getBlue());
		Set<UUID> redPlayers = getContestingPlayers(Utils.getRed());
		if(bluePlayers.isEmpty() && redPlayers.isEmpty()) return;
		if(allowContest){
			if(bluePlayers.size() > redPlayers.size()){
				//award blue
				if(stage == maxStage * -1) return;
				stage--;
				updateBlocks();
			}else if(redPlayers.size() > bluePlayers.size()){
				//award red
				if(stage == maxStage) return;
				stage++;
				updateBlocks();
			}
		}else{
			if(bluePlayers.isEmpty() == false && redPlayers.isEmpty()){
				//award blue;
				if(stage == maxStage * -1) return;
				stage--;
				updateBlocks();
			}else if(redPlayers.isEmpty() == false && bluePlayers.isEmpty()){
				//award red
				if(stage == maxStage) return;
				stage++;
				updateBlocks();
			}
		}
		trackPlayerPoints(redPlayers, bluePlayers);
	}
	
	private void trackPlayerPoints(Set<UUID> redPlayers, Set<UUID> bluePlayers){
		if(playerPoints > 0){
			for(UUID id : redPlayers){
				if(awardPointMap.containsKey(id)){
					awardPointMap.put(id, awardPointMap.get(id) + playerPoints);
				}else{
					awardPointMap.put(id, playerPoints);
				}
				if(awardCurrMap.containsKey(id)){
					awardCurrMap.put(id, awardCurrMap.get(id) + playerCurrency);
				}else{
					awardCurrMap.put(id, playerCurrency);
				}
			}
			for(UUID id : bluePlayers){
				if(awardPointMap.containsKey(id)){
					awardPointMap.put(id, awardPointMap.get(id) + playerPoints);
				}else{
					awardPointMap.put(id, playerPoints);
				}
				if(awardCurrMap.containsKey(id)){
					awardCurrMap.put(id, awardCurrMap.get(id) + playerCurrency);
				}else{
					awardCurrMap.put(id, playerCurrency);
				}
			}
		}
	}
	
	private void updateBlocks(){
		if(stage == 0){
			//neutral
			for(Set<Location> set : stageBlocks.values()){
				for(Location loc : set){
					Block block = loc.getBlock(); 
					block.setType(Material.WOOL);
					BlockState blockState = block.getState();
					Wool blockWool = (Wool) blockState.getData();
					blockWool.setColor(DyeColor.GRAY);
					blockState.update();
				}
			}
		}else if(stage > 0){ 
			//red advantage
			for(int i = 0; i <= maxStage; i++){
				if(i <= stage){
					for(Location loc : stageBlocks.get(i)){
						Block block = loc.getBlock(); 
						block.setType(Material.WOOL);
						BlockState blockState = block.getState();
						Wool blockWool = (Wool) blockState.getData();
						blockWool.setColor(DyeColor.RED);
						blockState.update();
					}
				}else{ 
					for(Location loc : stageBlocks.get(i)){
						Block block = loc.getBlock(); 
						block.setType(Material.WOOL);
						BlockState blockState = block.getState();
						Wool blockWool = (Wool) blockState.getData();
						blockWool.setColor(DyeColor.GRAY);
						blockState.update();
					}
				}
			}
		}else{
			//blue advantage
			for(int i = 0; i >= maxStage*-1; i--){
				if(i >= stage){
					for(Location loc : stageBlocks.get(i)){
						Block block = loc.getBlock(); 
						block.setType(Material.WOOL);
						BlockState blockState = block.getState();
						Wool blockWool = (Wool) blockState.getData();
						blockWool.setColor(DyeColor.BLUE);
						blockState.update();
					}
				}else{ 
					for(Location loc : stageBlocks.get(i)){
						Block block = loc.getBlock(); 
						block.setType(Material.WOOL);
						BlockState blockState = block.getState();
						Wool blockWool = (Wool) blockState.getData();
						blockWool.setColor(DyeColor.GRAY);
						blockState.update();
					}
				}
			}
		}
	}
	
	private Set<UUID> getContestingPlayers(Set<UUID> team){
		Set<UUID> players = new HashSet<UUID>();
		for(UUID id : team){
			Location location = Bukkit.getPlayer(id).getLocation();
			if(location.getY() <= (center.getY() + height) && location.getY() >= (center.getY() - negHeight)){
				if(abs(location.getX() - center.getX()) <= radius && abs(location.getZ() - center.getZ()) < radius){
					if(location.distance(center) < radius){
						players.add(id);
					}
				}
			}
		}
		return players;
	}
	
	private double abs(double x){
		if(x<0){
			x*=-1;
		}
		return x;
	}
}
