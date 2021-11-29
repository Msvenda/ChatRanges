package com.brokenworldrp.chatranges.commands;

import com.brokenworldrp.chatranges.data.RangeRepository;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CheckSpiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RangeRepository repo = RangeRepository.getRangeRepository();
        List<Player> spies = repo.getSpies();
        StringBuilder spiesString = new StringBuilder("Current spies: ");
        for(Player spy : spies){
            spiesString.append(spy.getDisplayName()).append(", ");
        }
        sender.sendMessage(spiesString.toString());
        return true;
    }
}
