package com.brokenworldrp.chatranges;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;

public class ChatRangesMain extends JavaPlugin {
	
	private static HashMap<String, EmoteRange> emoteRanges;
	
	private static HashMap<String, ChatRange> chatRanges;
	
	private static HashMap<UUID, String> playerRanges;
	
	private static Set<Player> spies;
	
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
	}
	
	@Override
	public void onDisable() {
		
	}

	public static HashMap<UUID, String> getPlayerRanges() {
		return playerRanges;
	}
	
	public static HashMap<String, ChatRange> getChatRanges() {
		return chatRanges;
	}
	
	public static HashMap<String, EmoteRange> getEmoteRanges(){
		return emoteRanges;
	}
	
	public static Set<Player> getSpies() {
		return spies;
	}
}
