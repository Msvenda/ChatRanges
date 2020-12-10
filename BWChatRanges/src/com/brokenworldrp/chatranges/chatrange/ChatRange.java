package com.brokenworldrp.chatranges.chatrange;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.brokenworldrp.chatranges.ChatRangesMain;
import com.brokenworldrp.chatranges.utils.Recipients;

import net.md_5.bungee.api.ChatColor;

public class ChatRange implements Range{
	private String key;
	private String rangeName;
	private String rangeCommand;
	private String rangeDescription;
	private List<String> rangeAliases;
	private double rangeRadius;
	private boolean crossDimension;
	private ChatColor rangeColor;
	private String rangePrefix;
	private String rangeWritePermission;
	private String rangeReadPermission;
	
	public String getRangeName() {
		return rangeName;
	}
	
	public Recipients getPlayersInRange(Player player){
		Stream<? extends Player> playersInRange;
		
		//get players with permission in range
		if(rangeRadius <= 0) {
			playersInRange = Bukkit.getOnlinePlayers().stream()
					.filter(pl -> !pl.equals(player))
					.filter(pl -> pl.hasPermission(rangeReadPermission));
			if(!crossDimension) {
				playersInRange.filter(pl -> pl.getWorld().equals(player.getWorld()));
			}
		}
		else {
			//get all players in range
			playersInRange = player.getNearbyEntities(rangeRadius, rangeRadius, rangeRadius).stream()
				.filter(en -> en instanceof Player)
				.map(en -> (Player) en)
				.filter(pl -> !pl.equals(player))
				.filter(pl -> pl.hasPermission(rangeReadPermission));
			//TODO: if config do radial check
		}
		Set<Player> allRecipients = playersInRange.collect(Collectors.toSet());
		
		//filter out invisible/hidden/gm3 players
		playersInRange.filter(pl -> !player.canSee(pl) 
				|| pl.hasPotionEffect(PotionEffectType.INVISIBILITY)
				|| pl.getGameMode() == GameMode.SPECTATOR);
		
		Recipients rec = new Recipients();
		//filtered list of visible recipients
		rec.recipients = playersInRange.collect(Collectors.toSet());
		
		//list of invisible recipients (all - visible)
		rec.hiddenRecipients = new HashSet<Player>(allRecipients);   
		rec.hiddenRecipients.removeAll(rec.recipients);
		
		//list of spies (spies - all - player)
		rec.spies = new HashSet<Player>(ChatRangesMain.getSpies()); 
		rec.spies.removeAll(allRecipients);
		rec.spies.remove(player);
		
		return rec;
	}
	
	public static ChatRange getPlayerChatRange(UUID playerID) {
		return ChatRangesMain.getChatRanges().get(ChatRangesMain.getPlayerRanges().get(playerID));
	}

	@Override
	public ChatColor getColor() {
		return rangeColor;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getPrefix() {
		return rangePrefix;
	}

	@Override
	public String getName() {
		return rangeName;
	}

	@Override
	public double getRange() {
		return rangeRadius;
	}

	@Override
	public boolean isCrossDimensional() {
		return crossDimension;
	}

	@Override
	public String getCommand() {
		return rangeCommand;
	}

	@Override
	public List<String> getAliases() {
		return rangeAliases;
	}

	@Override
	public String getWritePermission() {
		return this.rangeWritePermission;
	}

}
