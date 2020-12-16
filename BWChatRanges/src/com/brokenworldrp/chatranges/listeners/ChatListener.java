package com.brokenworldrp.chatranges.listeners;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.data.RangeRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener{

	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerChat(PlayerChatEvent event) {
		Player sender = event.getPlayer();
		RangeRepository repo = RangeRepository.getRangeRepository();
		Optional<ChatRange> chatRange = repo.getPlayerChatRange(sender.getUniqueId());
		chatRange.ifPresent(range -> {
			Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("ChatRanges"), new RunnableMessageContainer(sender, event.getMessage(), range));
		});
		event.setCancelled(true);
	}
}
