package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.theredbaron24.paintball.utils.FlagStatus;
import me.theredbaron24.paintball.utils.GameMode;

import org.bukkit.Location;

public class Arena {

	private static GameMode gameMode = null;
	private static Location deathSpawn = null;
	private static Location lobbySpawn = null;
	private static Location redFlagLocation = null;
	private static Location blueFlagLocation = null;
	private static FlagStatus blueFlagStatus = null;
	private static FlagStatus redFlagStatus = null;
	private static UUID redFlagCarrier = null;
	private static UUID blueFlagCarrier = null;
	private static Location neutralFlagLocation = null;
	private static FlagStatus neutralFlagStatus = null;
	private static UUID neutralFlagCarrier = null;
	private static Location neutralFlagOrigin = null;
	private static String title = null;
	private static List<Location> redSpawns = new ArrayList<Location>();
	private static List<Location> blueSpawns = new ArrayList<Location>();
	private static Location redFlagOrigin = null;
	private static Location blueFlagOrigin = null;
	
	public static void initializeCTFArena(Location deathSpawn, Location lobbySpawn,
			Location redFlagLocation, Location blueFlagLocation, String title,
			List<Location> redSpawns, List<Location> blueSpawns) {
		Arena.gameMode = GameMode.CTF;
		Arena.deathSpawn = deathSpawn;
		Arena.lobbySpawn = lobbySpawn;
		Arena.redFlagLocation = redFlagLocation;
		Arena.blueFlagLocation = blueFlagLocation;
		Arena.redFlagOrigin = redFlagLocation;
		Arena.blueFlagOrigin = blueFlagLocation;
		Arena.title = title;
		Arena.redSpawns = redSpawns;
		Arena.blueSpawns = blueSpawns;
		Arena.redFlagStatus = FlagStatus.HOME;
		Arena.blueFlagStatus = FlagStatus.HOME;
		Arena.neutralFlagCarrier = null;
		Arena.neutralFlagLocation = null;
		Arena.neutralFlagStatus = null;
		Arena.neutralFlagOrigin = null;
		CTFFlagHandler.resetFlags(false);
	}

	public static void initializeTDMArena(Location lobbySpawn, Location deathSpawn, String title, List<Location> redSpawns,
			List<Location> blueSpawns) {
		Arena.gameMode = GameMode.TDM;
		Arena.lobbySpawn = lobbySpawn;
		Arena.deathSpawn = deathSpawn;
		Arena.title = title;
		Arena.redSpawns = redSpawns;
		Arena.blueSpawns = blueSpawns;
		Arena.redFlagLocation = null;
		Arena.blueFlagLocation = null;
		Arena.redFlagStatus = null;
		Arena.blueFlagStatus = null;
		Arena.redFlagOrigin = null;
		Arena.blueFlagOrigin = null;
		Arena.neutralFlagCarrier = null;
		Arena.neutralFlagLocation = null;
		Arena.neutralFlagStatus = null;
		Arena.neutralFlagOrigin = null;
	}
	public static void initializeELMArena(Location lobbySpawn, Location deathSpawn, String title, List<Location> redSpawns,
			List<Location> blueSpawns){
		Arena.gameMode = GameMode.ELM;
		Arena.lobbySpawn = lobbySpawn;
		Arena.deathSpawn = deathSpawn;
		Arena.title = title;
		Arena.redSpawns = redSpawns;
		Arena.blueSpawns = blueSpawns;
		Arena.redFlagLocation = null;
		Arena.blueFlagLocation = null;
		Arena.redFlagStatus = null;
		Arena.blueFlagStatus = null;
		Arena.redFlagOrigin = null;
		Arena.blueFlagOrigin = null;
		Arena.neutralFlagCarrier = null;
		Arena.neutralFlagLocation = null;
		Arena.neutralFlagStatus = null;
		Arena.neutralFlagOrigin = null;
	}
	
	public static void initializeRTFArena(Location deathSpawn, Location lobbySpawn,
			Location flagLocation, Location redFlagLocation, Location blueFlagLocation,
			String title, List<Location> redSpawns, List<Location> blueSpawns) {
		Arena.gameMode = GameMode.RTF;
		Arena.deathSpawn = deathSpawn;
		Arena.lobbySpawn = lobbySpawn;
		Arena.redFlagLocation = null;
		Arena.blueFlagLocation = null;
		Arena.redFlagOrigin = redFlagLocation;
		Arena.blueFlagOrigin = blueFlagLocation;
		Arena.title = title;
		Arena.redSpawns = redSpawns;
		Arena.blueSpawns = blueSpawns;
		Arena.redFlagStatus = null;
		Arena.blueFlagStatus = null;
		Arena.neutralFlagCarrier = null;
		Arena.neutralFlagLocation = flagLocation;
		Arena.neutralFlagOrigin = flagLocation;
		Arena.neutralFlagStatus = FlagStatus.HOME;
		RTFFlagHandler.resetFlags(false);
	}
	
	public static Location getRedFlagOrigin(){
		return redFlagOrigin;
	}
	
	public static Location getBlueFlagOrigin(){
		return blueFlagOrigin;
	}
	
	public static void setRedFlagLocation(Location location){
		Arena.redFlagLocation = location;
	}
	
	public static void setBlueFlagLocation(Location location){
		Arena.blueFlagLocation = location;
	}

	public static GameMode getGameMode() {
		return gameMode;
	}

	public static Location getDeathSpawn() {
		return deathSpawn;
	}

	public static Location getLobbySpawn() {
		return lobbySpawn;
	}

	public static Location getRedFlagLocation() {
		return redFlagLocation;
	}

	public static Location getBlueFlagLocation() {
		return blueFlagLocation;
	}

	public static String getTitle() {
		return title;
	}

	public static List<Location> getRedSpawns() {
		return redSpawns;
	}

	public static List<Location> getBlueSpawns() {
		return blueSpawns;
	}

	public static FlagStatus getBlueFlagStatus() {
		return blueFlagStatus;
	}

	public static void setBlueFlagStatus(FlagStatus blueFlagStatus) {
		Arena.blueFlagStatus = blueFlagStatus;
	}

	public static FlagStatus getRedFlagStatus() {
		return redFlagStatus;
	}

	public static void setRedFlagStatus(FlagStatus redFlagStatus) {
		Arena.redFlagStatus = redFlagStatus;
	}

	public static UUID getRedFlagCarrier() {
		return redFlagCarrier;
	}

	public static void setRedFlagCarrier(UUID redFlagCarrier) {
		Arena.redFlagCarrier = redFlagCarrier;
	}

	public static UUID getBlueFlagCarrier() {
		return blueFlagCarrier;
	}

	public static void setBlueFlagCarrier(UUID blueFlagCarrier) {
		Arena.blueFlagCarrier = blueFlagCarrier;
	}

	public static Location getNeutralFlagLocation() {
		return neutralFlagLocation;
	}

	public static FlagStatus getNeutralFlagStatus() {
		return neutralFlagStatus;
	}

	public static UUID getNeutralFlagCarrier() {
		return neutralFlagCarrier;
	}

	public static Location getNeutralFlagOrigin() {
		return neutralFlagOrigin;
	}

	public static void setNeutralFlagLocation(Location neutralFlagLocation) {
		Arena.neutralFlagLocation = neutralFlagLocation;
	}

	public static void setNeutralFlagStatus(FlagStatus neutralFlagStatus) {
		Arena.neutralFlagStatus = neutralFlagStatus;
	}

	public static void setNeutralFlagCarrier(UUID neutralFlagCarrier) {
		Arena.neutralFlagCarrier = neutralFlagCarrier;
	}
	
}
