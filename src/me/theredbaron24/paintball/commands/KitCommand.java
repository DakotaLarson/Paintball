package me.theredbaron24.paintball.commands;

import me.theredbaron24.paintball.kits.Kit;
import me.theredbaron24.paintball.kits.KitType;
import me.theredbaron24.paintball.main.Configuration;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.TaskHandler;
import me.theredbaron24.paintball.utils.Chat;
import me.theredbaron24.paintball.utils.MessageHandler;
import me.theredbaron24.paintball.utils.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class KitCommand extends BukkitCommand{
	
	public KitCommand(String name){
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if((sender.hasPermission("paintball.kit") || sender.hasPermission("paintball.general") || sender.isOp()) == false){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageHandler.getMessage("noPermission"));
		}else{
			if(Main.kitsEnabled == false){
				sender.sendMessage(MessageHandler.getMessage("kitsDisabled"));
				return false;
			}
			Player player = (Player) sender;
			if(args.length == 0){
				listKits(player);
			}else if(args.length == 1){
				if(isValidKit(args[0]) == false){
					sender.sendMessage(MessageHandler.getMessage("kitNoValid", "%kit%", args[0]));
				}else{
					if(args[0].equalsIgnoreCase("none") == false && hasPermission(player, args[0]) == false){
						sender.sendMessage(MessageHandler.getMessage("kitNoPerm", "%kit%", args[0]));
					}else{
						Kit.initKitChange(player, KitType.valueOf(args[0].toUpperCase()), Main.main);
					}
				}
			}else if(args.length == 2){
				if(player.hasPermission("paintball.admin") == false){
					sender.sendMessage(MessageHandler.getMessage("kitUseGeneral"));
				}else{
					if(isValidKit(args[1]) == false){
						sender.sendMessage(MessageHandler.getMessage("kitNoValid", "%kit%", args[0]));
					}else{
						Player target = Bukkit.getPlayer(args[0]);
						if(target != null){
							Kit.initKitChange(target, KitType.valueOf(args[1].toUpperCase()), Main.main);
							target.sendMessage(MessageHandler.getMessage("kitChangedByAdmin", "%kit%", args[1], "%player%", player.getName()));
						}else{
							TaskHandler.runTask(new Runnable() {
								@Override
								public void run() {
									if(MySQL.hasValidConnection() == false){
										MySQL.connectToDatabase();
									}
									if(MySQL.isInDatabase(args[0], true) == false){
										Bukkit.getScheduler().runTask(Main.main, new Runnable() {
											@Override
											public void run() {
												player.sendMessage(MessageHandler.getMessage("playerNotInDatabase", "%player%", args[0]));
											}
										});									
									}else{
										String uuid = MySQL.getIDFromName(args[0]);
										MySQL.setString(uuid, "kit", args[1].toLowerCase());
										Bukkit.getScheduler().runTask(Main.main, new Runnable() {
											@Override
											public void run() {
												player.sendMessage(MessageHandler.getMessage("kitChangedByAdmin", "%kit%", args[1], "%player%", args[0]));
											}
										});
									}
								}
							});
						}
					}
				}
			}else{
				sender.sendMessage(MessageHandler.getMessage("kitUseGeneral"));
			}
		}
		return false;
	}
	private void listKits(Player player){
		player.sendMessage(MessageHandler.getMessage("kitUseGeneral"));
		player.sendMessage(MessageHandler.getMessage("useKitsGreen"));
		player.sendMessage(MessageHandler.getMessage("noKitsGray"));
		player.sendMessage(MessageHandler.getMessage("kitList"));
		for(KitType type : KitType.values()){
			if(type.name().equals("NONE")) continue;
			String title = type.name();
			title = title.substring(0, 1) + title.substring(1, title.length()).toLowerCase();
			if(Main.useClickable){
				if(hasPermission(player, type.name())){
					if(Main.cmdPrefixes){
						new Chat(MessageHandler.getMessage("kitInList", "%kit%", title, "%desc%", getDesc(title)))
						.hover(MessageHandler.getMessage("kitHover", "%kit%", title)).command("/pb kit " + title.toLowerCase()).send(player);
					}else{
						new Chat(MessageHandler.getMessage("kitInList", "%kit%", title, "%desc%", getDesc(title)))
						.hover(MessageHandler.getMessage("kitHover", "%kit%", title)).command("/kit " + title.toLowerCase()).send(player);
					}
					
				}else{
					new Chat(MessageHandler.getMessage("kitInList2", "%kit%", title))
					.hover(MessageHandler.getMessage("kitHover", "%kit%", title)).send(player);
				}
			}else{
				if(hasPermission(player, type.name())){
					player.sendMessage(MessageHandler.getMessage("kitInList", "%kit%", title, "%desc%", getDesc(title)));
				}else{
					player.sendMessage(MessageHandler.getMessage("kitInList2", "%kit%", title, "%desc%", getDesc(title)));
				}
			}
			
		}
	}
	private boolean isValidKit(String kit){
		kit = kit.toUpperCase();
		for(KitType type : KitType.values()){
			if(type.name().equals(kit)){
				return true;
			}
		}
		return false;
	}
	private boolean hasPermission(Player player, String kit){
		if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".enabled") == false){
			return false;
		}if(Configuration.getConfig().getBoolean("kits." + kit.toLowerCase() + ".permissionRequired")){
			if(player.hasPermission("paintball.kit." + kit.toLowerCase()) == false){
				return false;
			}
		}
		return true;
	}
	private String getDesc(String kit){
		return Configuration.getConfig().getString("kits." + kit.toLowerCase() + ".description", "No Description");
	}
}
