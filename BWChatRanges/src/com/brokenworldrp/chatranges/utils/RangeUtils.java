package com.brokenworldrp.chatranges.utils;

import java.util.UUID;

import com.brokenworldrp.chatranges.ChatRangesMain;
import com.brokenworldrp.chatranges.chatrange.ChatRange;

public class RangeUtils {

	public static ChatRange getPlayerChatRange(UUID playerID) {
		return ChatRangesMain.getChatRanges().get(ChatRangesMain.getPlayerRanges().get(playerID));
	}
	
}
