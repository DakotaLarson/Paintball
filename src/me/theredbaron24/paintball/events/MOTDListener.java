package me.theredbaron24.paintball.events;


import me.theredbaron24.paintball.main.Arena;
import me.theredbaron24.paintball.main.Main;
import me.theredbaron24.paintball.main.Match;
import me.theredbaron24.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;


public class MOTDListener implements Listener{
    private String notStarted = null;
    private String countingDown = null;
    private String initializing = null;
    private String running = null;
    private String finalizing = null;

    public void initStrings(){
        notStarted = Main.main.getConfig().getString("motd.notStarted", null);
        countingDown = Main.main.getConfig().getString("motd.countingDown", null);
        initializing = Main.main.getConfig().getString("motd.initializing", null);
        running = Main.main.getConfig().getString("motd.running", null);
        finalizing = Main.main.getConfig().getString("motd.finalizing", null);
        if(notStarted != null) notStarted = ChatColor.translateAlternateColorCodes('&', notStarted);
        if(countingDown != null) countingDown = ChatColor.translateAlternateColorCodes('&', countingDown);
        if(initializing != null) initializing = ChatColor.translateAlternateColorCodes('&', initializing);
        if(running != null) running = ChatColor.translateAlternateColorCodes('&', running);
        if(finalizing != null) finalizing = ChatColor.translateAlternateColorCodes('&', finalizing);

    }

    @EventHandler
    public void onMOTD(ServerListPingEvent event){
        switch(Utils.getGameStatus()){
            case NOT_STARTED:
                event.setMotd(notStarted);
                break;
            case COUNTING_DOWN:
                event.setMotd(countingDown);
                break;
            case INITIALIZING:
                event.setMotd(initializing.replaceAll("%arena%", Arena.getTitle()));
                break;
            case RUNNING:
                event.setMotd(running.replaceAll("%arena%", Arena.getTitle()));
                break;
            case FINALIZING:
                event.setMotd(finalizing);
                break;
            default:
                break;
        }
    }
}
