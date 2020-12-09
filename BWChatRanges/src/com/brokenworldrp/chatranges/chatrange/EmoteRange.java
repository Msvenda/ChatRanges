package com.brokenworldrp.chatranges.chatrange;

import java.util.Set;

import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.utils.Recipients;

import net.md_5.bungee.api.ChatColor;

public class EmoteRange implements Range {
	private String key;
	private String emoteName;
	private String emoteDescription;
	private String emoteCommand;
	private ChatRange emoteRange;
	private Set<String> emoteAliases;
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
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPrefix() {
		return emotePrefix;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return emoteName;
	}
	@Override
	public boolean isCrossDimensional() {
		// TODO Auto-generated method stub
		return emoteRange.isCrossDimensional();
	}
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return emoteCommand;
	}
	@Override
	public double getRange() {
		return emoteRange.getRange();
	}
	@Override
	public Set<String> getAliases() {
		// TODO Auto-generated method stub
		return emoteAliases;
	}
	public ChatColor getRangeColor() {
		return emoteRange.getColor();
	}
	
}
