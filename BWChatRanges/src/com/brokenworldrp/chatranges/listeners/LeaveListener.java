package com.brokenworldrp.chatranges.listeners;

import com.brokenworldrp.chatranges.data.RangeRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RangeRepository repo = RangeRepository.getRangeRepository();
        repo.playerCleanup(player);
    }
}
