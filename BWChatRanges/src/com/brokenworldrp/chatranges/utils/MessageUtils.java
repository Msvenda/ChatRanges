package com.brokenworldrp.chatranges.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.ChatFormatter;
import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;

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
	
	public static void sendRangedEmote(Player player, String message, EmoteRange range) {
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormatedEmote(player, message, range);
		
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
	
	private static void sendNoRecipientMessage(Player player, String message, Range range) {
		BaseComponent noRecipientMessage = ChatFormatter.getNoRecipientMessage(player, message, range);
		player.spigot().sendMessage(noRecipientMessage);
	}
	
	public static void sendPlayersOnlyMessage(CommandSender sender) {
		sender.sendMessage(String.format("%s%s", ConfigUtils.getErrorColor(), ConfigUtils.getPlayersOnlyMessage()));
	}
	
	public static void sendNoPermissionMessage(Player player) {
		player.sendMessage(String.format("%s%s", ConfigUtils.getErrorColor(), ConfigUtils.getNoPermissionMessage()));
	}

	public static void sendMissingCommandRangeMessage(Player player) {
		player.sendMessage(String.format("%s%s", ConfigUtils.getErrorColor(), ConfigUtils.getMissingCommandRangeMessage()));
		
	}

	public static void sendNoEmoteMessage(Player player) {
		player.sendMessage(String.format("%s%s", ConfigUtils.getErrorColor(), ConfigUtils.getNoEmoteMessage()));
		
	}
	
	public static void sendRangeNotFoundMessage(Player player) {
		player.sendMessage(String.format("%s%s", ConfigUtils.getErrorColor(), ConfigUtils.getMissingCommandRangeMessage()));
		
	}

	public static void sendRangeList(Player player) {
		BaseComponent rangeListMessage = TextUtils.createRangeList(player);
		player.spigot().sendMessage(rangeListMessage);
		
	}

	
	
}
