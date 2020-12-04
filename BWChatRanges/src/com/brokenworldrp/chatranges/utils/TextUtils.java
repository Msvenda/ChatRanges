package com.brokenworldrp.chatranges.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.brokenworldrp.chatranges.chatrange.ChatRange;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtils {
	private static HashMap<String, TextComponent> rangePrefixTextComponents = new HashMap<String, TextComponent>();
	private static HashMap<String, TextComponent> rangeSuffixTextComponents = new HashMap<String, TextComponent>();
	
	private static final TextComponent NEW_LINE = new TextComponent("\n");
	
	public static final ChatColor COLOUR_PREFIX = ChatColor.GRAY;
	public static final ChatColor COLOUR_KEY = ChatColor.GOLD;
	public static final ChatColor COLOUR_VALUE = ChatColor.AQUA;
	
	public static void addRange(ChatRange range) {
		//TODO: 
	}
	
	public static TextComponent getRangePrefixTextComponent(ChatRange range) {
		return rangePrefixTextComponents.get(range.getRangeName());
	}

	public static TextComponent getRangeSuffixTextComponent(ChatRange range) {
		return rangeSuffixTextComponents.get(range.getRangeName());
	}

	public static BaseComponent getNameTextComponent(Player player) {
		BaseComponent hoverText = new TextComponent();
		String commandSuggest = "/msg " + player.getName() + " ";
		
		//get formatted nick
		BaseComponent displayName = new TextComponent();
		for(BaseComponent c : TextComponent.fromLegacyText(player.getDisplayName())) {
			displayName.addExtra(c);
		}
		
		//nickname
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(COLOUR_PREFIX);
		BaseComponent key = new TextComponent("Nickname: ");
		key.setColor(COLOUR_KEY);
		BaseComponent value = new TextComponent(displayName);
		hoverText.addExtra(new TextComponent(prefix, key, value, NEW_LINE));
		//real name
		hoverText.addExtra(simpleKeyValueComponent("Real name", player.getName()));
		hoverText.addExtra(NEW_LINE);
		hoverText.addExtra(String.format("Click to pre-type \"{0}\".", commandSuggest));
		
		displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { hoverText }));
		displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggest));
		
		return displayName;
	}
	
	public static BaseComponent getMessageTextComponents(String m, Player p, ChatRange range) {
		BaseComponent message = new TextComponent();
		//check for @hand and modify message accordingly
		if(m.contains("@hand") || m.contains("@offhand")) {
			List<String> substrings = Stream.of(m.split("@")).collect(Collectors.toList());
			message.addExtra(substrings.get(0));
			for(int i = 1; i < substrings.size(); i++) {
				if(substrings.get(i).startsWith("hand") || substrings.get(i).startsWith("offhand")) {
					ItemStack heldItem;
					//get item and remove placeholder from string
					if(substrings.get(i).startsWith("hand")) {
						heldItem = p.getInventory().getItemInMainHand();
						substrings.set(i, substrings.get(i).substring(4));
					}
					else {
						heldItem = p.getInventory().getItemInOffHand();
						substrings.set(i, substrings.get(i).substring(7));
					}
					
					//set up item component
					BaseComponent item = getItemNameComponent(heldItem);
					String itemJson;
					try {
						itemJson = getItemJson(heldItem);
						item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent(itemJson)}));
					} catch (ReflectiveOperationException e) {
						item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("Error fetching item data")}));
						e.printStackTrace();
					}
					message.addExtra(item);
				}
				else {
					//if @hand not found, re-add @ (ex. "hey look @ this item! @hand")
					substrings.set(i, "@"+substrings.get(i));
				}
				message.addExtra(substrings.get(i));
				message.setColor(range.getRangeColor());
			}
		}
		else {
			//if no @hand found, send normal message
			message.addExtra(m);
			message.setColor(range.getRangeColor());
		}
		return message;
	}
	
	public static BaseComponent formatText(String message, ChatRange range) {
		BaseComponent formattedMessage = new TextComponent(message);
		formattedMessage.setColor(range.getRangeColor());
		return formattedMessage;
	}
	
	public static BaseComponent getSpyTextComponent() {
		BaseComponent spyText = new TextComponent();
		
		return null;
	}
	
	private static BaseComponent simpleKeyValueComponent(String k, String v) {
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(COLOUR_PREFIX);
		BaseComponent key = new TextComponent(k);
		key.setColor(COLOUR_KEY);
		BaseComponent value = new TextComponent(v);
		value.setColor(COLOUR_VALUE);
		
		return new TextComponent(prefix, key, value, NEW_LINE);
		
	}
	
	private static BaseComponent getItemNameComponent(ItemStack heldItem) {
		BaseComponent itemText = new TextComponent("[");
		for(BaseComponent c : TextComponent.fromLegacyText(heldItem.getItemMeta().getDisplayName())) {
			itemText.addExtra(c);
		}
		itemText.addExtra(new TextComponent("]"));
		return itemText;
	}
	
	private static String getItemJson(ItemStack item) throws ReflectiveOperationException
	{
		Class<?> craftItemStackClass = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
		Class<?> nbtTagCompoundClass = ReflectionUtil.getNMSClass("NBTTagCompound");
		Class<?> nmsItemStackClass = ReflectionUtil.getNMSClass("ItemStack");

		Method asNmsCopyMethod = ReflectionUtil.getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class);
		Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClass, "save", nbtTagCompoundClass);

		// net.minecraft.server.ItemStack nmsItemObject =
		// org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(item);
		Object nmsItemObject = asNmsCopyMethod.invoke(null, item);
		// net.minecraft.server.NBTTagCompound nbtTagCompoundObject = new NBTTagCompound();
		Object nbtTagCompoundObject = nbtTagCompoundClass.getDeclaredConstructor().newInstance();
		// net.minecraft.server.NBTTagCompound itemNbtCompoundObject = nmsItemObject.save(nbtTagCompoundObject);
		Object itemNbtCompoundObject = saveNmsItemStackMethod.invoke(nmsItemObject, nbtTagCompoundObject);

		return itemNbtCompoundObject.toString();
	}

	
	
}
