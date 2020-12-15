package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.*;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
	
	public static void sendRangedMessage(Player player, String message, ChatRange range) {
		Config config = Config.getConfig();
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormatedMessage(player, message, range);

		if(config.isRecipientNumberLoggingEnabled()){
			Bukkit.getLogger().info(String.format("%s (%d, %d, %d)",
					formattedMessage.toLegacyText(),
					recipients.recipients.size(),
					recipients.hiddenRecipients.size(),
					recipients.spies.size()));
		}
		else{
			Bukkit.getLogger().info(formattedMessage.toLegacyText());
		}

		player.spigot().sendMessage(formattedMessage);
		
		//send to recievers
		if(!recipients.recipients.isEmpty()) {
			for(Player p : recipients.recipients) {
				p.spigot().sendMessage(formattedMessage);
			}
		}
		else if(config.isNoRecipientAlertEnabled()){
			sendNoRecipientMessage(player, message, range);
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
		Config config = Config.getConfig();
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormatedEmote(player, message, range);

		//log message
		if(config.isRecipientNumberLoggingEnabled()){
			Bukkit.getLogger().info(String.format("%s (%d, %d, %d)",
					formattedMessage.toLegacyText(),
					recipients.recipients.size(),
					recipients.hiddenRecipients.size(),
					recipients.spies.size()));
		}
		else{
			Bukkit.getLogger().info(formattedMessage.toLegacyText());
		}

		player.spigot().sendMessage(formattedMessage);
		
		//send to recievers
		if(!recipients.recipients.isEmpty()) {
			for(Player p : recipients.recipients) {
				p.spigot().sendMessage(formattedMessage);
			}
		}
		else if(config.isNoRecipientAlertEnabled()){
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
		Config config = Config.getConfig();
		sender.sendMessage(String.format("%s%s", config.getErrorColor(), config.getPlayersOnlyMessage()));
	}
	
	public static void sendNoPermissionMessage(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getNoPermissionMessage()));
	}

	public static void sendMissingCommandRangeMessage(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingCommandRangeMessage()));
		
	}
	
	public static void sendRangeNotFoundMessage(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingCommandRangeMessage()));
		
	}

	public static void sendRangeList(Player player) {
		BaseComponent rangeListMessage = TextUtils.createRangeList(player);
		player.spigot().sendMessage(rangeListMessage);
	}

	public static void sendPlayerSpyStatusMessage(Player player) {
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();
		if(repo.getSpies().contains(player)){
			player.sendMessage(String.format("%s%s", config.getDefaultColor(), config.getSpyStatusOnMessage()));
		}
		else{
			player.sendMessage(String.format("%s%s", config.getDefaultColor(), config.getSpyStatusOffMessage()));
		}

	}

	public static void sendSpyEnabledMessage(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getDefaultColor(), config.getSpyToggleOnMessage()));
	}

	public static void sendSpyDisabledMessage(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getDefaultColor(), config.getSpyToggleOffMessage()));
	}
}
