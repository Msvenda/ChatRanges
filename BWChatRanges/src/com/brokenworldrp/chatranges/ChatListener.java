package com.brokenworldrp.chatranges;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.utils.MessageUtils;

public class ChatListener implements Listener{

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		ChatRange chatRange = ChatRange.getPlayerChatRange(sender.getUniqueId());
		MessageUtils.sendRangedMessage(sender, event.getMessage(), chatRange);
	}
}
