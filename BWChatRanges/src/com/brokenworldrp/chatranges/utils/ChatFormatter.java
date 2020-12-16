package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.*;
import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ChatFormatter {
	public static final String PREFIX_PLACEHOLDER = "prefix";
	public static final String PLAYER_PLACEHOLDER = "player";
	public static final String MESSAGE_PLACEHOLDER = "message";
	public static final String RANGE_PLACEHOLDER = "range";
	
	private static final String DELIMITER_REGEX = "[{}]+";
	//private static final String DELIMITER_REGEX = "\\{";

	public static BaseComponent getFormattedMessage(Player player, String message, ChatRange range) {
		//"<prefix>" component has range info on hover, set range on click, pre-type on shift-click
		//"<player>" component has player info on hover, click to pre-type msg
		//"<message>" component has @hand and @offhand option, shows item data on hover
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		//BaseComponent[] formattedMessage = new ComponentBuilder("").create();
		BaseComponent formattedMessage = new TextComponent("");
		
		for(String component : config.getMessageFormat().split(DELIMITER_REGEX)) {
			if(component.equals(PREFIX_PLACEHOLDER)) {
				//formattedMessage = new ComponentBuilder().append(formattedMessage).append(repo.getRangePrefixComponent(range)).create();
				formattedMessage = new TextComponent(formattedMessage, repo.getRangePrefixComponent(range));
			}
			else if(component.equals(PLAYER_PLACEHOLDER)) {
				//formattedMessage = new ComponentBuilder().append(formattedMessage).append(TextUtils.getNameTextComponent(player)).create();
				formattedMessage = new TextComponent(formattedMessage, TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(MESSAGE_PLACEHOLDER)) {
				//formattedMessage = new ComponentBuilder().append(formattedMessage).append(TextUtils.getMessageTextComponents(message, player, range)).create();
				formattedMessage = new TextComponent(formattedMessage, TextUtils.getMessageTextComponents(message, player, range));
			}
			else {
				//formattedMessage = new ComponentBuilder().append(formattedMessage).append(TextUtils.formatText(component, range.getColor())).create();
				formattedMessage = new TextComponent(formattedMessage, TextUtils.formatText(component, range.getColor()));
			}
		}
		
		return formattedMessage;
	}

	public static BaseComponent getFormattedSpyMessage(BaseComponent formattedMessage) {
		Config config = Config.getConfig();
		BaseComponent spyMessage = TextUtils.getSpyTextComponent();
		if(config.getSpyPosition().equals("prefix")) {
			return new TextComponent(spyMessage, new TextComponent(""), formattedMessage);
		}
		else {
			return new TextComponent(formattedMessage, new TextComponent(""), spyMessage);
		}		
	}
	
	public static BaseComponent getFormattedEmote(Player player, String message, EmoteRange range){
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : config.getEmoteFormat().split(DELIMITER_REGEX)) {
			if(component.equals(PREFIX_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage, repo.getRangePrefixComponent(range));
			}
			else if(component.equals(PLAYER_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(MESSAGE_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.getEmoteTextComponents(message, player, range));
			}
			else {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.formatText(component, range.getColor()));
			}
		}
		
		return formattedMessage;
	}

	public static BaseComponent getNoRecipientMessage(Player player, String message, Range range) {
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : config.getNoRecipientMessage().split(DELIMITER_REGEX)) {
			if(component.equals(RANGE_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,repo.getRangePrefixComponent(range));
			}
			else if(component.equals(PLAYER_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(MESSAGE_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.getMessageTextComponents(message, player, range));
			}
			else {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.formatText(component, ChatColor.GRAY));
			}
		}
		
		return formattedMessage;
	}

	public static BaseComponent getRangeChangedMessage(Player player, ChatRange range) {
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		BaseComponent formattedMessage = new TextComponent();

		for(String component : config.getChangedRangeMessage().split(DELIMITER_REGEX)){
			if(component.equals(RANGE_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,repo.getRangePrefixComponent(range));
			}
			else if(component.equals(PLAYER_PLACEHOLDER)) {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.getNameTextComponent(player));
			}
			else {
				formattedMessage = new TextComponent(formattedMessage,TextUtils.formatText(component, config.getDefaultColor()));
			}
		}
		return formattedMessage;
	}
}
