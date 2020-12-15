package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.Config;
import com.brokenworldrp.chatranges.chatrange.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChangeRangeCommand extends BukkitCommand{

	private String rangeKey;
	private String rangeWritePerm;
	
	public ChangeRangeCommand(ChatRange range){
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
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		Optional<ChatRange> range = repo.getChatRangeByKey(rangeKey);
		if(!range.isPresent()){
			MessageUtils.sendMissingCommandRangeMessage(player);
			return true;
		}
		
		Optional<String> message = args.length > 0 
				? Optional.of(StringUtils.join(args, ' '))
				: Optional.empty();

		message.ifPresent(s -> MessageUtils.sendRangedMessage(player, s, range.get()));

		if(config.isAliasSingleMessageEnabled() && message.isPresent()){
			return true;
		}
		repo.setPlayerRangebyKey(player.getUniqueId(), rangeKey);

		return true;
	}
	
}
