package com.brokenworldrp.chatranges.chatlistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.brokenworldrp.chatranges.ChatRangesMain;
import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.enums.RangeType;
import com.brokenworldrp.chatranges.utils.RangeUtils;

public class ChatListener implements Listener{

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		ChatRange chatRange = RangeUtils.getPlayerChatRange(sender.getUniqueId());
		event.getRecipients();
	}
}
