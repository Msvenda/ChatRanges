package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.RunnableMessageContainer;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import com.brokenworldrp.chatranges.utils.Recipients;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChangeRangeCommand extends BukkitCommand{

	private final String rangeKey;
	private final String rangeWritePerm;
	
	public ChangeRangeCommand(ChatRange range){
		super(range.getCommand(), "", "/" + range.getCommand() + " <message>", range.getAliases());
		rangeKey = range.getKey();
		rangeWritePerm = range.getWritePermission();
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(!(sender instanceof Player))	{
			MessageUtils.sendPlayersOnlyError(sender);
			return true;
		}
		Player player = (Player) sender;
		if(!rangeWritePerm.isEmpty() && !(player.hasPermission(rangeWritePerm))) {
			MessageUtils.sendNoPermissionError(player);
			return true;
		}
		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		//get the commands range
		Optional<ChatRange> range = repo.getChatRangeByKey(rangeKey);
		if(!range.isPresent()){
			MessageUtils.sendMissingCommandRangeError(player);
			return true;
		}
		//unmute range
		if(repo.getMuteStatusForPlayer(player, range.get().getKey())){
			repo.unmuteRangeForPlayer(player, range.get().getKey());
			MessageUtils.sendRangeUnmutedMessage(player, range.get());
		}
		//combine args into message string
		Optional<String> message = args.length > 0 
				? Optional.of(StringUtils.join(args, ' '))
				: Optional.empty();

		//get recipients
		Recipients recipients = range.get().getPlayersInRange(player);

		//send messages
		message.ifPresent(s -> {
			Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("ChatRanges"), new RunnableMessageContainer(player, s, range.get(), recipients));
		});
		//if single message alias is enabled and message is present, return
		if(config.isAliasSingleMessageEnabled() && message.isPresent()){
			return true;
		}

		//send range chaged message
		MessageUtils.sendRangeChangedMessage(player, range.get());
		if(!repo.setPlayerRangebyKey(player.getUniqueId(), rangeKey)){
			MessageUtils.sendMissingCommandRangeError(player);
		}
		return true;
	}
	
}
