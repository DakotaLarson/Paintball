package me.theredbaron24.paintball.commands;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.utils.MessageHandler;

public class ControlPointCommand extends BukkitCommand {

	public ControlPointCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("noUseCommand");
		}else{
			Player player = (Player) sender;
			if(args.length == 0){
				for(int i = 1; i < 22; i++){
					sender.sendMessage(MessageHandler.getMessage("cpHelp" + i));
				}
			}else if(args.length == 1){
				sender.sendMessage(MessageHandler.getMessage("cpNotUnderstood"));
			}else if(args.length == 2){
				if(args[0].equalsIgnoreCase("create")){
					createPoint(args[1], player); 
				}else if(args[0].equalsIgnoreCase("remove")){
					removePoint(args[1], player);
				}else if(args[0].equalsIgnoreCase("info")){
					sendPointInfo(args[1], player);
				}else if(args[0].equalsIgnoreCase("build")){
					buildPoint(args[1], player);
				}else if(args[0].equalsIgnoreCase("center")){
					setCenter(args[1], player.getLocation(), player);
				}else{
					sender.sendMessage(MessageHandler.getMessage("cpNotUnderstood"));
				}
			}else if(args.length == 3){
				if(args[0].equalsIgnoreCase("initialhold")){
					setInitialHold(args[1], args[2], player);
				}else if(args[0].equalsIgnoreCase("stages")){
					setStages(args[1], args[2], player);
				}else if(args[0].equalsIgnoreCase("stageinterval")){
					//setStageInterval(args[1], args[2], player);
				}else if(args[0].equalsIgnoreCase("contest")){
					
				}else if(args[0].equalsIgnoreCase("radius")){
					
				}else if(args[0].equalsIgnoreCase("height")){
					
				}else if(args[0].equalsIgnoreCase("negheight")){
				
				}else if(args[0].equalsIgnoreCase("pointinterval")){
					
				}else{
					sender.sendMessage(MessageHandler.getMessage("cpNotUnderstood"));
				}
			}else if(args.length == 4){
				if(args[0].equalsIgnoreCase("block")){
					
				}else if(args[0].equalsIgnoreCase("holdpoints")){
					
				}else if(args[0].equalsIgnoreCase("contestpoints")){
					
				}else if(args[0].equalsIgnoreCase("neutralpoints")){
					
				}else if(args[0].equalsIgnoreCase("hold")){
					
				}else if(args[0].equalsIgnoreCase("contest")){
					
				}else{
					sender.sendMessage(MessageHandler.getMessage("cpNotUnderstood"));
				}
			}
		}
		return false;
	}
	private void createPoint(String title, Player player){
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section != null){
			player.sendMessage(MessageHandler.getMessage("cpAlreadyExists", "%point%", title));
		}else{
			Location loc = player.getLocation();
			String locString = loc.getWorld().getName() + " : " + (loc.getBlockX() + 0.5) + " : " + (loc.getBlockY() + 0.5) + " : " + (loc.getBlockZ() + 0.5);
			Configuration.getCPConfig().set("points." + title + ".center", locString);
			Configuration.saveCPConfig();
			player.sendMessage(MessageHandler.getMessage("cpCreated", "%point%", title));	
		}
	}
	private void removePoint(String title, Player player){
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section == null){
			player.sendMessage(MessageHandler.getMessage("cpDoesNotExists", "%point%", title));
		}else{
			Set<String> arenas = Configuration.getArenaConfig().getConfigurationSection("arenas").getKeys(false);
			boolean canRemove = true;
			for(String s : arenas){
				if(Configuration.getArenaConfig().getStringList("arenas."+s+".points").contains(title)){
					player.sendMessage(MessageHandler.getMessage("cpInArena", "%point%", title, "%arena%", s));
					canRemove = false;
				}
			}
			if(canRemove){
				Configuration.getCPConfig().set("points." + title, null);
				Configuration.saveCPConfig();
				player.sendMessage(MessageHandler.getMessage("cpRemoved", "%point%", title));
			}else{
				player.sendMessage(MessageHandler.getMessage("cpNotRemoved", "%point%", title));
			}
			
		}
	}
	private void sendPointInfo(String title, Player player){
		
	}
	private void setCenter(String title, Location loc, Player player){
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section != null){
			player.sendMessage(MessageHandler.getMessage("cpDoesNotExist", "%point%", title));
		}else{
			String locString = loc.getWorld().getName() + " : " + (loc.getBlockX() + 0.5) + " : " + (loc.getBlockY() + 0.5) + " : " + (loc.getBlockZ() + 0.5);
			Configuration.getCPConfig().set("points." + title + ".center", locString);
			Configuration.saveCPConfig();
			player.sendMessage(MessageHandler.getMessage("cpCenterSet", "%point%", title));
		}
	}
	private void setInitialHold(String title, String team, Player player){
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section != null){
			player.sendMessage(MessageHandler.getMessage("cpDoesNotExist", "%point%", title));
		}else if(team.equalsIgnoreCase("red") == false && team.equalsIgnoreCase("blue") == false){
			player.sendMessage(MessageHandler.getMessage("cpTeamIncorrect."));
		}
		else{
			Configuration.getCPConfig().set("points." + title + ".initialHold", team.toLowerCase());
			Configuration.saveCPConfig();
			player.sendMessage(MessageHandler.getMessage("cpInitialHoldSet", "%point%", title));
		}
	}
	private void setStages(String title, String inputCount, Player player){
		int count = 0;
		if(count < 1){
			count = 1;
			player.sendMessage(MessageHandler.getMessage("cpCountIncorrect"));
		}
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section != null){
			player.sendMessage(MessageHandler.getMessage("cpDoesNotExist", "%point%", title));
		}else{
			Configuration.getCPConfig().set("points." + title + ".stages", count);
			Configuration.saveCPConfig();
			player.sendMessage(MessageHandler.getMessage("cpStagesSet", "%point%", title));
		}
	}
	/*private void setStageInterval(String title, int count, Player player){
		if(count < 1){
			count = 1;
			player.sendMessage(MessageHandler.getMessage("cpCountIncorrect"));
		}
		if(count <= 5){
			player.sendMessage(MessageHandler.getMessage("cpCountLow"));
		}
		ConfigurationSection section = Configuration.getCPConfig().getConfigurationSection("points." + title);
		if(section != null){
			player.sendMessage(MessageHandler.getMessage("cpDoesNotExist", "%point%", title));
		}else{
			Configuration.getCPConfig().set("points." + title + ".stageInterval", count);
			Configuration.saveCPConfig();
			player.sendMessage(MessageHandler.getMessage("cpStageIntervalSet", "%point%", title));
		}
	}
	private void setContest(String title, boolean contest, int team, Player player){
		
	}
	private void setRadius(){
		
	}
	private void setHeight(){
		
	}
	private void setNegHeight(){
		
	}
	private void setPointInterval(){
		
	}
	private void updateBlocks(){
		
	}
	private void setHoldPoints(){
		
	}
	private void setContestPoints(){
		
	}
	private void setNeutralPoints(){
		
	}
	private void setCanHold(){
		
	}
	private void setCanContest(){
		
	}*/
	private void buildPoint(String title, Player player){ 
		
	}
}
