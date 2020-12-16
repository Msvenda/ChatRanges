package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.*;
import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
	
	public static void sendRangedMessage(Player player, String message, ChatRange range) {
		Config config = Config.getConfig();
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormattedMessage(player, message, range);

		if(config.isRecipientNumberLoggingEnabled()){
			LoggingUtil.logMessage(String.format("%s %s(%d, %d, %d)",
					formattedMessage.toLegacyText(),
					ChatColor.GRAY,
					recipients.recipients.size(),
					recipients.hiddenRecipients.size(),
					recipients.spies.size()));
		}
		else{
			LoggingUtil.logMessage(formattedMessage.toLegacyText());
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
		BaseComponent formattedSpyMessage = ChatFormatter.getFormattedSpyMessage(formattedMessage);
		for(Player p : recipients.spies) {
			p.spigot().sendMessage(formattedSpyMessage);
		}
		
	}
	
	public static void sendRangedEmote(Player player, String message, EmoteRange range) {
		Config config = Config.getConfig();
		Recipients recipients = range.getPlayersInRange(player);
		
		BaseComponent formattedMessage = ChatFormatter.getFormattedEmote(player, message, range);

		//log message
		if(config.isRecipientNumberLoggingEnabled()){
			LoggingUtil.logMessage(String.format("%s %s(%d, %d, %d)",
					formattedMessage.toLegacyText(),
					ChatColor.GRAY,
					recipients.recipients.size(),
					recipients.hiddenRecipients.size(),
					recipients.spies.size()));
		}
		else{
			LoggingUtil.logMessage(formattedMessage.toLegacyText());
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
		BaseComponent formattedSpyMessage = ChatFormatter.getFormattedSpyMessage(formattedMessage);
		for(Player p : recipients.spies) {
			p.spigot().sendMessage(formattedSpyMessage);
		}
		
	}
	
	private static void sendNoRecipientMessage(Player player, String message, Range range) {
		BaseComponent noRecipientMessage = ChatFormatter.getNoRecipientMessage(player, message, range);
		player.spigot().sendMessage(noRecipientMessage);
	}
	
	public static void sendPlayersOnlyError(CommandSender sender) {
		Config config = Config.getConfig();
		sender.sendMessage(String.format("%s%s", config.getErrorColor(), config.getPlayersOnlyError()));
	}
	
	public static void sendNoPermissionError(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getNoPermissionError()));
	}

	public static void sendMissingCommandRangeError(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingCommandRangeError()));
	}

	public static void sendMissingMessageEmoteError(Player player){
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingMessageEmoteError()));

	}

	public static void sendMissingCommandEmoteError(Player player){
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingCommandEmoteError()));

	}

	public static void sendRangeNotFoundError(Player player) {
		Config config = Config.getConfig();
		player.sendMessage(String.format("%s%s", config.getErrorColor(), config.getMissingCommandRangeError()));

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

	public static void sendRangeChangedMessage(Player player, ChatRange chatRange) {
		player.spigot().sendMessage(ChatFormatter.getRangeChangedMessage(player, chatRange));
	}
}
