package com.brokenworldrp.chatranges.chatrange;

import com.brokenworldrp.chatranges.utils.Recipients;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public interface Range {

	ChatColor getColor();
	
	String getKey();

	String getPrefix();

	String getName();

	double getRange();

	boolean isCrossDimensional();

	String getCommand();

	List<String> getAliases();
	
	String getWritePermission();

	Recipients getPlayersInRange(Player player);

	String getDescription();
}
