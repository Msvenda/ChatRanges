package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.utils.MessageUtils;
import com.brokenworldrp.chatranges.utils.Recipients;
import org.bukkit.entity.Player;

public class RunnableMessageContainer implements Runnable{

    Player player;
    String message;
    Range range;
    Recipients recipients;

    public RunnableMessageContainer(Player player, String message, Range range, Recipients recipients){
        this.player = player;
        this.message = message;
        this.range = range;
        this.recipients = recipients;
    }

    @Override
    public void run() {
        if(range instanceof EmoteRange){
            MessageUtils.sendRangedEmote(player, message, (EmoteRange) range, recipients);
        }
        else if(range instanceof ChatRange){
            MessageUtils.sendRangedMessage(player, message, (ChatRange) range, recipients);
        }
    }
}
