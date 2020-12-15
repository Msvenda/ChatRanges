package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ChatFormatter {
	private static final String DELIMITER_REGEX = "[{}]+";

	public static BaseComponent getFormatedMessage(Player player, String message, ChatRange range) {
		//"<prefix>" component has range info on hover, set range on click, pre-type on shift-click
		//"<player>" component has player info on hover, click to pre-type msg
		//"<message>" component has @hand and @offhand option, shows item data on hover
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : config.getMessageFormat().split(DELIMITER_REGEX)) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(repo.getRangePrefixComponent(range));
			}
			else if(component.equals(ConfigUtils.playerPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(ConfigUtils.messagePlaceholder)) {
				formattedMessage.addExtra(TextUtils.getMessageTextComponents(message, player, range));
			}
			else {
				formattedMessage.addExtra(TextUtils.formatText(component, range.getColor()));
			}
		}
		
		return formattedMessage;
	}

	public static BaseComponent getFormatedSpyMessage(BaseComponent formattedMessage) {
		Config config = Config.getConfig();
		BaseComponent spyMessage = TextUtils.getSpyTextComponent();
		if(config.getSpyPosition().equals("prefix")) {
			spyMessage.addExtra(" ");
			spyMessage.addExtra(formattedMessage);
			return spyMessage;
		}
		else {
			formattedMessage.addExtra(" ");
			formattedMessage.addExtra(spyMessage);
			return formattedMessage;
		}		
	}
	
	public static BaseComponent getFormatedEmote(Player player, String message, EmoteRange range){
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : config.getEmoteFormat().split(DELIMITER_REGEX)) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(repo.getRangePrefixComponent(range));
			}
			else if(component.equals(ConfigUtils.playerPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(ConfigUtils.messagePlaceholder)) {
				formattedMessage.addExtra(TextUtils.getEmoteTextComponents(message, player, range));
			}
			else {
				formattedMessage.addExtra(TextUtils.formatText(component, range.getColor()));
			}
		}
		
		return formattedMessage;
	}

	public static BaseComponent getNoRecipientMessage(Player player, String message, Range range) {
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : config.getEmoteFormat().split(DELIMITER_REGEX)) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(repo.getRangePrefixComponent(range));
			}
			else if(component.equals(ConfigUtils.playerPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(ConfigUtils.messagePlaceholder)) {
				formattedMessage.addExtra(TextUtils.getMessageTextComponents(message, player, range));
			}
			else {
				formattedMessage.addExtra(TextUtils.formatText(component, ChatColor.GRAY));
			}
		}
		
		return formattedMessage;
	}
}
