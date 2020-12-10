package com.brokenworldrp.chatranges.commands;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.utils.MessageUtils;

public class EmoteCommand extends BukkitCommand {
	
	private String rangeKey;
	private String rangeWritePerm;
	
	public EmoteCommand(ChatRange range){
		super(range.getCommand(), "", "/" + range.getCommand() + " <message>", range.getAliases());
		rangeKey = range.getKey();
		rangeWritePerm = range.getWritePermission();
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyMessage(sender);
			return true;
		}
		Player player = (Player) sender;
		if(!(player.hasPermission(rangeWritePerm))) {
			MessageUtils.sendNoPermissionMessage(player);
		}
		
		Optional<String> message = args.length > 0 
				? Optional.of(StringUtils.join(args, ' '))
				: Optional.empty();
		if(message.isPresent()) {
			MessageUtils.sendRangedEmote(player, message.get(), Range.getEmoteRangeByKey(rangeKey).get());
		}
		else {
			MessageUtils.sendNoEmoteMessage(player);
		}
		return true;
	}
}
