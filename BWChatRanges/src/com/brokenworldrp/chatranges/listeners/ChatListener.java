package com.brokenworldrp.chatranges.listeners;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener{

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		RangeRepository repo = RangeRepository.getRangeRepository();
		Optional<ChatRange> chatRange = repo.getPlayerChatRange(sender.getUniqueId());
		chatRange.ifPresent(range -> MessageUtils.sendRangedMessage(sender, event.getMessage(), range));

	}
}
