package com.brokenworldrp.chatranges.chatrange;

import java.util.List;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.utils.Recipients;

import net.md_5.bungee.api.ChatColor;

public class EmoteRange implements Range {
	private String key;
	private String emoteName;
	private String emoteDescription;
	private String emoteCommand;
	private ChatRange emoteRange;
	private List<String> emoteAliases;
	private ChatColor emoteColour;
	private String emotePrefix;
	@Override
	public Recipients getPlayersInRange(Player player) {
		return emoteRange.getPlayersInRange(player);
	}
	@Override
	public ChatColor getColor() {
		return emoteColour;
	}

	@Override
	public String getKey() {
		return key;
	}
	@Override
	public String getPrefix() {
		return emotePrefix;
	}
	@Override
	public String getName() {
		return emoteName;
	}
	@Override
	public boolean isCrossDimensional() {
		return emoteRange.isCrossDimensional();
	}
	@Override
	public String getCommand() {
		return emoteCommand;
	}
	@Override
	public double getRange() {
		return emoteRange.getRange();
	}
	@Override
	public List<String> getAliases() {
		return emoteAliases;
	}
	public ChatColor getRangeColor() {
		return emoteRange.getColor();
	}
	@Override
	public String getWritePermission() {
		return emoteRange.getWritePermission();
	}
	
}
