package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Optional;

public class MuteCommand  implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyError(sender);
			return true;
		}
		Player player = (Player) sender;
		if(args.length < 1) {
			MessageUtils.sendMissingRangeMuteError(player);
		}
		RangeRepository repo = RangeRepository.getRangeRepository();

		Optional<ChatRange> range = repo.getChatRangeByKey(args[0].toLowerCase(Locale.ROOT));
		if(!range.isPresent()){
			MessageUtils.sendMutingUnknownRangeError(player);
			return true;
		}
		String key = range.get().getKey();
		if(repo.getMuteStatusForPlayer(player, key)){
			repo.unmuteRangeForPlayer(player, key);
			MessageUtils.sendRangeUnmutedMessage(player, range.get());
		}
		else{
			repo.muteRangeForPlayer(player, key);
			MessageUtils.sendRangeMutedMessage(player, range.get());
		}
		return true;
	}
}
