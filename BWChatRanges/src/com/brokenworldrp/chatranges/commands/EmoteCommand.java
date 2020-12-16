package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.listeners.RunnableMessageContainer;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import com.brokenworldrp.chatranges.utils.Recipients;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Optional;

public class EmoteCommand extends BukkitCommand {
	
	private final String rangeKey;
	private final String rangeWritePerm;
	
	public EmoteCommand(EmoteRange range){
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
		RangeRepository repo = RangeRepository.getRangeRepository();
		Optional<EmoteRange> range = repo.getEmoteRangeByKey(rangeKey);
		if(!range.isPresent()){
			MessageUtils.sendMissingCommandEmoteError(player);
			return true;
		}
		Optional<String> message = args.length > 0 
				? Optional.of(StringUtils.join(args, ' '))
				: Optional.empty();
		if(message.isPresent()) {
			Recipients recipients = range.get().getPlayersInRange(player);
			Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("ChatRanges"), new RunnableMessageContainer(player, message.get(), range.get(), recipients));
		}
		else {
			MessageUtils.sendMissingMessageEmoteError(player);
			return false;
		}
		return true;
	}
}
