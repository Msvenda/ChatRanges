package com.brokenworldrp.chatranges.utils;

import org.bukkit.Bukkit;

public class LoggingUtil {
    private static String prefix = "[ChatRanges] ";
    public static void logWarning(String message){
        Bukkit.getLogger().warning(prefix + message);
    }
    public static void logInfo(String message){
        Bukkit.getLogger().info(prefix + message);
    }
    public static void logMessage(String message){
        Bukkit.getLogger().info(message);
    }
}
