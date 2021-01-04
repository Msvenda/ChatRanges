package com.brokenworldrp.chatranges.chatrange;

import com.brokenworldrp.chatranges.utils.Recipients;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class EmoteRange implements Range {
	private final String key;
	private final String emoteName;
	private final String emoteDescription;
	private final String emoteFormat;
	private final String emoteCommand;
	private final ChatRange emoteRange;
	private final List<String> emoteAliases;
	private final ChatColor emoteColour;
	private final String emotePrefix;
	private final String emoteWritePermission;

    public EmoteRange(String emoteKey, String name, String description, String format, String command, List<String> aliases,
					  ChatRange range, ChatColor colour, String prefix, String permission) {
    	this.key = emoteKey;
    	this.emoteName = name;
    	this.emoteDescription = description;
    	this.emoteFormat = format;
    	this.emoteCommand = command;
    	this.emoteAliases = aliases;
    	this.emoteRange = range;
    	this.emoteColour = colour;
    	this.emotePrefix = prefix;
    	this.emoteWritePermission = permission;
    }

    @Override
	public Recipients getPlayersInRange(Player player) {
		return emoteRange.getPlayersInRange(player);
	}

	@Override
	public String getDescription() {
		return emoteDescription;
	}

	@Override
	public String getFormat() {
		return null;
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
		return emoteWritePermission;
	}

	public ChatRange getEmoteRange(){
    	return emoteRange;
	}
	
}
