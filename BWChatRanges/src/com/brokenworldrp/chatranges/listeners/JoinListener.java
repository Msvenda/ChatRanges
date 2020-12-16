package com.brokenworldrp.chatranges.listeners;

import com.brokenworldrp.chatranges.data.RangeRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void playerChat(PlayerJoinEvent event) {
        Player sender = event.getPlayer();
        RangeRepository repo = RangeRepository.getRangeRepository();
        repo.playerSetup(sender.getUniqueId());
    }
}
