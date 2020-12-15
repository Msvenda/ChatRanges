package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

public class EmoteCommand extends BukkitCommand {
	
	private String rangeKey;
	private String rangeWritePerm;
	
	public EmoteCommand(EmoteRange range){
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
		RangeRepository repo = RangeRepository.getRangeRepository();
		
		Optional<String> message = args.length > 0 
				? Optional.of(StringUtils.join(args, ' '))
				: Optional.empty();
		if(message.isPresent()) {
			MessageUtils.sendRangedEmote(player, message.get(), repo.getEmoteRangeByKey(rangeKey).get());
		}
		else {
			return false;
		}
		return true;
	}
}
