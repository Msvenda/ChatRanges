package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.*;
import com.brokenworldrp.chatranges.data.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
	
	public static void sendRangedMessage(Player player, String message, ChatRange range, Recipients recipients) {
		Config config = Config.getConfig();
		
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
	
	public static void sendRangedEmote(Player player, String message, EmoteRange range, Recipients recipients) {
		Config config = Config.getConfig();
		
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
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getNoRecipientMessage(), player, message, range, ChatColor.GRAY));
	}
	
	public static void sendPlayersOnlyError(CommandSender sender) {
		sender.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getPlayersOnlyError(), null, null, null, Config.getConfig().getErrorColor()));
	}
	
	public static void sendNoPermissionError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getNoPermissionError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendMissingCommandRangeError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getMissingCommandRangeError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendMissingMessageEmoteError(Player player){
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getMissingMessageEmoteError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendMissingCommandEmoteError(Player player){
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getMissingCommandEmoteError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendRangeList(Player player) {
		BaseComponent rangeListMessage = TextUtils.createRangeList(player);
		player.spigot().sendMessage(rangeListMessage);
	}

	public static void sendSpyEnabledMessage(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getSpyToggleOnMessage(), player, null, null, Config.getConfig().getDefaultColor()));
	}

	public static void sendSpyDisabledMessage(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getSpyToggleOffMessage(), player, null, null, Config.getConfig().getDefaultColor()));
	}

	public static void sendRangeChangedMessage(Player player, ChatRange chatRange) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getChangedRangeMessage(), player, null, chatRange, Config.getConfig().getDefaultColor()));
				//.getRangeChangedMessage(player, chatRange));
	}
	public static void sendRangeJoinMessage(Player player, ChatRange chatRange) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getJoinRangeMessage(), player, null, chatRange, Config.getConfig().getDefaultColor()));
	}

	public static void sendRangeMutedMessage(Player player, ChatRange chatRange) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getRangeMutedMessage(), player, null, chatRange, Config.getConfig().getDefaultColor()));
	}
	public static void sendRangeUnmutedMessage(Player player, ChatRange chatRange) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getRangeUnmutedMessage(), player, null, chatRange, Config.getConfig().getDefaultColor()));
	}

	public static void sendMutingUnknownRangeError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getMutingUnknownRangeError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendSpyStatusOnMessage(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getSpyStatusOnMessage(), player, null, null, Config.getConfig().getDefaultColor()));
	}
	public static void sendSpyStatusOffMessage(Player player){
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getSpyStatusOffMessage(), player, null, null, Config.getConfig().getDefaultColor()));
	}

	public static void sendRetrievingCurrentRangeError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getRetrievingCurrentRangeError(), player,  null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendRetievingRangesError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getRetrievingRangesError(), player, null, null, Config.getConfig().getErrorColor()));
	}

	public static void sendMissingRangeMuteError(Player player) {
		player.spigot().sendMessage(ChatFormatter.getStandardFormattedMessage(Config.getConfig().getMissingRangeMuteError(), player, null, null, Config.getConfig().getErrorColor()));
	}
}
