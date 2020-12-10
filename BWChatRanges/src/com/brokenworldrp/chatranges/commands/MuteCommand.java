package com.brokenworldrp.chatranges.commands;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.utils.MessageUtils;

public class MuteCommand  implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyMessage(sender);
			return true;
		}
		if(args.length < 1) {
			return false;
		}
		Player player = (Player) sender;
		
		Optional<ChatRange> range = Range.getChatRangeByKey(args[0]);
		if(!range.isPresent()){
			MessageUtils.sendRangeNotFoundMessage(player);
			return false;
		}
		Range.toggleMuteRangeForPlayer(player, range.get().getKey());
		return true;
	}
}
