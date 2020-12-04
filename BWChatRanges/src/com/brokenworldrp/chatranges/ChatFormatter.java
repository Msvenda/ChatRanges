package com.brokenworldrp.chatranges;

import java.util.List;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.utils.ConfigUtils;
import com.brokenworldrp.chatranges.utils.TextUtils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatFormatter {
	public static BaseComponent getFormatedMessage(Player player, String message, ChatRange range) {
		//"<prefix>" component has range info on hover, set range on click, pre-type on shif-click
		//"<player>" component has player info on hover, click to pre-type msg
		//"<message>" component has @hand and @offhand option, shows item data on hover
		//"<suffix>" component has range info on hover, set range on click, pre-type on shif-click
		BaseComponent formattedMessage = new TextComponent();
		
		for(String component : ConfigUtils.getMessageFormat().split("{|}")) {
			if(component.equals(ConfigUtils.prefixPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getRangePrefixTextComponent(range));
			}
			else if(component.equals(ConfigUtils.playerPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getNameTextComponent(player));
			}
			else if(component.equals(ConfigUtils.messagePlaceholder)) {
				formattedMessage.addExtra(TextUtils.getMessageTextComponents(message, player, range));
			}
			else if(component.equals(ConfigUtils.suffixPlaceholder)) {
				formattedMessage.addExtra(TextUtils.getRangeSuffixTextComponent(range));
			}
			else {
				formattedMessage.addExtra(TextUtils.formatText(component, range));
			}
		}
		
		
		return formattedMessage;
	}

	public static BaseComponent getFormatedSpyMessage(BaseComponent formattedMessage) {
		BaseComponent spyMessage = TextUtils.getSpyTextComponent();
		spyMessage.addExtra(formattedMessage);
		return spyMessage;
	}
	
	public static List<TextComponent> getFormatedEmote(Player p, String m, ChatRange r){
		// TODO: implement
		return null;
	}

	public static BaseComponent getNoRecipientMessage(Player player, ChatRange range) {
		// TODO Auto-generated method stub
		return null;
	}
}
