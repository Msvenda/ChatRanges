package com.brokenworldrp.chatranges.chatrange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.ChatRangesMain;
import com.brokenworldrp.chatranges.utils.Recipients;

import net.md_5.bungee.api.ChatColor;

public interface Range {
	public ChatColor getColor();
	
	public String getKey();

	public String getPrefix();

	public String getName();

	public double getRange();

	public boolean isCrossDimensional();

	public String getCommand();

	public List<String> getAliases();
	
	public String getWritePermission();

public Recipients getPlayersInRange(Player player);
	
	public static Optional<ChatRange> getPlayerChatRange(UUID playerID) {
		return Optional.ofNullable(ChatRangesMain.getChatRanges().get(ChatRangesMain.getPlayerRanges().get(playerID)));
		
	}
	
	public static ChatRange getChatRangeByKey(String key) {
		
		return ChatRangesMain.getChatRanges().get(key);
	}
	
	public static EmoteRange getEmoteRangeByKey(String rangeKey) {
		return ChatRangesMain.getEmoteRanges().get(rangeKey);
	}
	
	
	public static boolean setPlayerRangebyKey(UUID playerID, String rangeKey) {
		if(ChatRangesMain.getChatRanges().containsKey(rangeKey)) {
			ChatRangesMain.getPlayerRanges().put(playerID, rangeKey);
			return true;
		}
		return false;
		
		
	}

	public static List<ChatRange> getChatRangeList() {
		return ChatRangesMain.getChatRanges().values().stream().collect(Collectors.toList());
	}

	
	
}
