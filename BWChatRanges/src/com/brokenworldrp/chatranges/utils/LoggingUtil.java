package com.brokenworldrp.chatranges.utils;

import org.bukkit.Bukkit;

public class LoggingUtil {
    private static final String PREFIX = "[ChatRanges] ";
    public static void logWarning(String message){
        Bukkit.getLogger().warning(PREFIX + message);
    }
    public static void logInfo(String message){
        Bukkit.getLogger().info(PREFIX + message);
    }
    public static void logMessage(String message){
        Bukkit.getLogger().info(message);
    }
}
