package com.brokenworldrp.chatranges.listeners;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RangeRepository repo = RangeRepository.getRangeRepository();
        ChatRange playerRange = repo.playerSetup(player.getUniqueId());
        MessageUtils.sendRangeJoinMessage(player, playerRange);
    }
}
