package com.brokenworldrp.chatranges.chatrange;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.ChatRangesMain;
import com.brokenworldrp.chatranges.utils.Recipients;

import net.md_5.bungee.api.ChatColor;

public interface Range {
	public Recipients getPlayersInRange(Player player);
	
	public static ChatRange getPlayerChatRange(UUID playerID) {
		return ChatRangesMain.getChatRanges().get(ChatRangesMain.getPlayerRanges().get(playerID));
	}

	public ChatColor getColor();
	
	public String getKey();

	public String getPrefix();

	public String getName();

	public double getRange();

	public boolean isCrossDimensional();

	public String getCommand();

	public Set<String> getAliases();
	
}
