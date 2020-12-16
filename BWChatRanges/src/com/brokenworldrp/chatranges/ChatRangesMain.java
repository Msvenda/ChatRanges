package com.brokenworldrp.chatranges;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.Config;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.RangeRepository;
import com.brokenworldrp.chatranges.commands.*;
import com.brokenworldrp.chatranges.listeners.ChatListener;
import com.brokenworldrp.chatranges.utils.CommandUtils;
import com.brokenworldrp.chatranges.utils.LoggingUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatRangesMain extends JavaPlugin {

	@Override
	public void onEnable() {

		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginCommand("ranges").setExecutor(new CheckRangeCommand());
		Bukkit.getPluginCommand("spy").setExecutor(new SpyCommand());
		Bukkit.getPluginCommand("mute").setExecutor(new MuteCommand());

		Config config = Config.getConfig();
		RangeRepository repo = RangeRepository.getRangeRepository();

		for(ChatRange range : repo.getChatRangeList()){
			//create range command and register it
			ChangeRangeCommand rangeCommand = new ChangeRangeCommand(range);
			rangeCommand.setDescription(range.getDescription());
			rangeCommand.setPermission(range.getWritePermission());
			try {
				CommandUtils.registerCommand(rangeCommand, this);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				LoggingUtil.logWarning(String.format("Failed to create command for range %s", range.getName()));

			}
		}

		for(EmoteRange range : repo.getEmoteRangeList()){
			//create emote command and register it
			EmoteCommand emoteCommand = new EmoteCommand(range);
			emoteCommand.setDescription(range.getDescription());
			emoteCommand.setPermission(range.getWritePermission());
			try {
				CommandUtils.registerCommand(emoteCommand, this);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				LoggingUtil.logWarning(String.format("Failed to create command for emote %s", range.getName()));
			}
		}
	}
	
	@Override
	public void onDisable() {
		RangeRepository repo = RangeRepository.getRangeRepository();
		repo.saveRepositoryData();
	}
}
