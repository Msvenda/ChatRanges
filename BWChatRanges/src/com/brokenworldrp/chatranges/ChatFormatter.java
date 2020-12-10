package com.brokenworldrp.chatranges;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.utils.ConfigUtils;
import com.brokenworldrp.chatranges.utils.TextUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatFormatter {
	public static BaseComponent getFormatedMessage(Player player, String message, ChatRange range) {
		//"<prefix>" component has range info on hover, set range on click, pre-type on shif-click
		//"<player>" component has player info on hover, click to pre-type msg
		//"<message>" component has @hand and @offhand option, shows item data on hover
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : ConfigUtils.getMessageFormat().split("{|}")) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getRangePrefixComponent(range));
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
		BaseComponent spyMessage = TextUtils.getSpyTextComponent();
		if(ConfigUtils.getSpyPosition().equals("prefix")) {
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
		
		//TODO: change ChatRange and EmoteRange to implement/extend range to unify methods
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : ConfigUtils.getEmoteFormat().split("{|}")) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getRangePrefixComponent(range));
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
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : ConfigUtils.getEmoteFormat().split("{|}")) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getRangePrefixComponent(range));
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
