package me.theredbaron24.paintball.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

public class Chat {
	private TextComponent component = null;
	
	public Chat(String text){
		component = new TextComponent(TextComponent.fromLegacyText(text));
	}
	
	public Chat hover(String message){
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message)));
		return this;
	}
	
	public Chat suggest(String message){
		component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message));
		return this;
	}
	
	public Chat command(String command){
		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return this;
	}
	
	public void send(Player player){
		player.spigot().sendMessage(component);
	}
	
	public Chat append(TextComponent comp){
		component.addExtra(comp);
		return this;
	}
	public Chat getChat(){
		return this;
	}
}
