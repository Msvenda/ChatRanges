package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.ChatFormatter;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpyCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		RangeRepository repo = RangeRepository.getRangeRepository();
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyError(sender);
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0){
			if(repo.getSpyStatusForPlayer(player)){
				MessageUtils.sendSpyStatusOnMessage(player);
			}
			else{
				MessageUtils.sendSpyStatusOffMessage(player);
			}
			return true;
		}
		else{
			if(args[0].equalsIgnoreCase("on")){
				repo.enableSpyForPlayer(player);
				MessageUtils.sendSpyEnabledMessage(player);
			}
			else if(args[0].equalsIgnoreCase("off")){
				repo.disableSpyForPlayer(player);
				MessageUtils.sendSpyDisabledMessage(player);
			}
			else{
				MessageUtils.sendMissingMessageEmoteError(player);
				return false;
			}
		}
		return true;
	}

}
