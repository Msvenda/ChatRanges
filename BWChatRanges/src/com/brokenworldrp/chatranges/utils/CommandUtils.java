package com.brokenworldrp.chatranges.utils;

import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class CommandUtils
{
    /*
     * Credits to mine-care (https://bukkit.org/members/mine-care.90737861/) from
     * Util - Register Commands Without Plugin.yml! (https://bukkit.org/threads/register-commands-without-plugin-yml.349373/)
     */
    public static void registerCommand(Command command, Plugin plugin) throws ReflectiveOperationException
    {
        Method commandMapMethod = plugin.getServer().getClass().getMethod("getCommandMap");
        Object commandMapObject = commandMapMethod.invoke(plugin.getServer());
        Method registerMethod = commandMapObject.getClass().getMethod("register", String.class, Command.class);
        registerMethod.invoke(commandMapObject, command.getName(), command);
    }
}