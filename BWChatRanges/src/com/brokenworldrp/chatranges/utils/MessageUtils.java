package com.brokenworldrp.chatranges.utils;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.ChatFormatter;
import com.brokenworldrp.chatranges.chatrange.ChatRange;

import net.md_5.bungee.api.chat.BaseComponent;

public class MessageUtils {
	
	public static void sendRangedMessage(Player player, String message, ChatRange range) {
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormatedMessage(player, message, range);
		
		player.spigot().sendMessage(formattedMessage);
		
		//send to recievers
		if(recipients.recipients.isEmpty()) {
			sendNoRecipientMessage(player, message, range);
		}
		else {
			for(Player p : recipients.recipients) {
				p.spigot().sendMessage(formattedMessage);
			}
		}
		//send to hidden recievers
		for(Player p : recipients.hiddenRecipients) {
			p.spigot().sendMessage(formattedMessage);
		}
		//send to spies
		BaseComponent formattedSpyMessage = ChatFormatter.getFormatedSpyMessage(formattedMessage);
		for(Player p : recipients.spies) {
			p.spigot().sendMessage(formattedSpyMessage);
		}
		
	}
	
	private static void sendNoRecipientMessage(Player player, String message, ChatRange range) {
		BaseComponent noRecipientMessage = ChatFormatter.getNoRecipientMessage(player, message, range);
		player.spigot().sendMessage(noRecipientMessage);
	}
	
}
