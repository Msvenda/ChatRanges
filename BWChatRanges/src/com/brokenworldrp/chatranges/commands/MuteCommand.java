package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class MuteCommand  implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyError(sender);
			return true;
		}
		if(args.length < 1) {
			return false;
		}
		RangeRepository repo = RangeRepository.getRangeRepository();

		Player player = (Player) sender;
		
		Optional<ChatRange> range = repo.getChatRangeByKey(args[0]);
		if(!range.isPresent()){
			MessageUtils.sendRangeNotFoundError(player);
			return false;
		}
		repo.toggleMuteRangeForPlayer(player, range.get().getKey());
		return true;
	}
}
