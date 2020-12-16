package com.brokenworldrp.chatranges.chatrange;

import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.Recipients;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatRange implements Range{
	private final String key;
	private final String rangeName;
	private final String rangeCommand;
	private final String rangeDescription;
	private final List<String> rangeAliases;
	private final double rangeRadius;
	private final boolean crossDimension;
	private final ChatColor rangeColor;
	private final String rangePrefix;
	private final String rangeWritePermission;
	private final String rangeReadPermission;

    public ChatRange(String rangeKey, String name, String description, String command, List<String> aliases,
					 boolean crossDimension, Double distance, ChatColor colour, String prefix,
					 String permission, String readPermission) {
		this.key = rangeKey;
		this.rangeName = name;
		this.rangeDescription = description;
		this.rangeCommand = command;
		this.rangeAliases = aliases;
		this.crossDimension = crossDimension;
		this.rangeRadius = distance;
		this.rangeColor = colour;
		this.rangePrefix = prefix;
		this.rangeWritePermission = permission;
		this.rangeReadPermission = readPermission;

    }
	
	public Recipients getPlayersInRange(Player player){
    	RangeRepository repo = RangeRepository.getRangeRepository();
    	Config config = Config.getConfig();

		Set<Player> allRecipients;
		//get players with permission in range
		if(rangeRadius <= 0) {
			if(crossDimension){
				allRecipients = Bukkit.getOnlinePlayers().stream()
						.filter(pl -> !pl.equals(player))
						.filter(pl -> pl.hasPermission(rangeReadPermission))
						.collect(Collectors.toSet());
			}
			else{
				allRecipients = Bukkit.getOnlinePlayers().stream()
						.filter(pl -> !pl.equals(player))
						.filter(pl -> pl.hasPermission(rangeReadPermission))
						.filter(pl -> pl.getWorld().equals(player.getWorld()))
						.collect(Collectors.toSet());
			}
		}
		else {
			//get all players in range
			allRecipients = player.getNearbyEntities(rangeRadius, rangeRadius, rangeRadius).stream()
				.filter(en -> en instanceof Player)
				.map(en -> (Player) en)
				.filter(pl -> !pl.equals(player))
				.filter(pl -> pl.hasPermission(rangeReadPermission))
					.collect(Collectors.toSet());
			if(config.isRadialDistanceCheckEnabled()){
				allRecipients = allRecipients.stream().filter(pl -> pl.getLocation().distance(player.getLocation()) < rangeRadius)
						.collect(Collectors.toSet());
			}
		}

		//filter out invisible/hidden/gm3 players
		
		Recipients rec = new Recipients();
		//filtered list of visible recipients
		rec.recipients = allRecipients.stream()
				.filter(pl -> !player.canSee(pl)
				|| pl.hasPotionEffect(PotionEffectType.INVISIBILITY)
				|| pl.getGameMode() == GameMode.SPECTATOR)
				.collect(Collectors.toSet());
		
		//list of invisible recipients (all - visible)
		rec.hiddenRecipients = new HashSet<>(allRecipients);
		rec.hiddenRecipients.removeAll(rec.recipients);
		
		//list of spies (spies - all - player)
		rec.spies = new HashSet<>(repo.getSpies());
		rec.spies.removeAll(allRecipients);
		rec.spies.remove(player);
		
		return rec;
	}

	@Override
	public String getDescription() {
		return rangeDescription;
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
