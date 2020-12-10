package com.brokenworldrp.chatranges.utils;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtils {
	private static HashMap<String, BaseComponent> rangePrefixComponents = new HashMap<String, BaseComponent>();
	private static HashMap<String, BaseComponent> rangeTextComponents = new HashMap<String, BaseComponent>();
	
	private static final TextComponent NEW_LINE = new TextComponent("\n");
	
	public static final ChatColor COLOUR_PREFIX = ChatColor.GRAY;
	public static final ChatColor COLOUR_KEY = ChatColor.GOLD;
	public static final ChatColor COLOUR_VALUE = ChatColor.AQUA;
	
	public static BaseComponent getRangePrefixComponent(Range range) {
		return rangePrefixComponents.get(range.getKey());
	}
	
	public static BaseComponent getRangeTextComponent(Range range) {
		return rangeTextComponents.get(range.getKey());
	}
	
	public static Boolean createRangeComponents(Range range) {
		if(rangePrefixComponents.containsKey(range.getKey())) {
			return false;
		}
		
		//create shared hover text
		String clickCommand = String.format("/%s ", range.getCommand());
		BaseComponent hoverText = new TextComponent(simpleKeyValueComponent("Name:", range.getName()));
		hoverText.addExtra(simpleKeyValueComponent("Range:", String.format("%.1f blocks", range.getRange())));
		hoverText.addExtra(TextUtils.simpleKeyValueComponent("Cross-Dimensional", range.isCrossDimensional() ?
				"\u2713" : "\u2717"));
		hoverText.addExtra(simpleKeyValueComponent("Command:", clickCommand));
		for(String alias : range.getAliases()) {
			hoverText.addExtra(simpleListComponent(alias));
		}
		hoverText.addExtra(simpleKeyValueComponent("Prefix:", range.getPrefix()));
		hoverText.addExtra(simpleKeyValueComponent("Colour:", range.getColor().getName()));
		hoverText.addExtra(NEW_LINE);
		hoverText.addExtra(new TextComponent("Click to change to this range.\nShift-click to pre-type \"" + clickCommand + "\""));
		
		
		
		//create prefix component
		BaseComponent messageText = new TextComponent(range.getPrefix());
		messageText.setColor(range.getColor());
		
		messageText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		messageText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
		
		rangePrefixComponents.put(range.getKey(), messageText);
		//create range text component
		messageText = new TextComponent(range.getName());
		messageText.setColor(range.getColor());
		
		messageText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		messageText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
		rangeTextComponents.put(range.getKey(), messageText);
		return true;
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
	
	public static BaseComponent getMessageTextComponents(String m, Player p, Range r) {
		BaseComponent message = new TextComponent();
		//surround @hand/offhand with brackets for easier recognition
		if( m.contains("@hand") || m.contains("@offhand")) {
			m.replaceAll("@hand", "{@hand}");
			m.replaceAll("@offhand", "{@offhand}");
		}
		String[] parts = m.split("{|}");
		for(String part : parts) {
			if(part.equals("@hand")) {
				ItemStack heldItem = p.getInventory().getItemInMainHand();
				message.addExtra(getItemComponent(heldItem));
			}
			else if(part.equals("@offhand")) {
				ItemStack heldItem = p.getInventory().getItemInOffHand();
				message.addExtra(getItemComponent(heldItem));
			}
			else {
				BaseComponent partText = new TextComponent(part);
				partText.setColor(r.getColor());
				message.addExtra(partText);
			}
		}
		return message;
	}
	
	public static BaseComponent getEmoteTextComponents(String m, Player p, EmoteRange r) {
		BaseComponent message = new TextComponent();
		String[] splitMessage = m.split("\"");
		//if there is an odd number of elements, there is an event number of quotations
		
		for(int i = 0; i < splitMessage.length; i++) {
			//surround @hand/offhand with brackets for easier recognition
			if( splitMessage[i].contains("@hand") || splitMessage[i].contains("@offhand")) {
				splitMessage[i].replaceAll("@hand", "{@hand}");
				splitMessage[i].replaceAll("@offhand", "{@offhand}");
			}
			String[] parts = splitMessage[i].split("{|}");
			for(String part : parts) {
				if(part.equals("@hand")) {
					ItemStack heldItem = p.getInventory().getItemInMainHand();
					message.addExtra(getItemComponent(heldItem));
				}
				else if(part.equals("@offhand")) {
					ItemStack heldItem = p.getInventory().getItemInOffHand();
					message.addExtra(getItemComponent(heldItem));
				}
				else {
					BaseComponent partText = new TextComponent(part);
					if(splitMessage.length%2 == 1) {
						partText.setColor(r.getColor());
					}
					else {
						partText.setColor(i%2 == 0 ? r.getColor() : r.getRangeColor());
					}
					message.addExtra(partText);
				}
			}
		}
		return message;
	}
	
	
	public static BaseComponent formatText(String message, ChatColor chatColor) {
		BaseComponent formattedMessage = new TextComponent(message);
		formattedMessage.setColor(chatColor);
		return formattedMessage;
	}
	
	public static BaseComponent getSpyTextComponent() {
		BaseComponent spyText = new TextComponent("[spy]");
		spyText.setColor(ChatColor.GRAY);
		
		String command = "/spy toggle";
		
		BaseComponent hoverText = new TextComponent("You are seeing this message because you are out of range of the sender and you have Spy enabled.\n\nClick to toggle spy status.");
		hoverText.setColor(COLOUR_KEY);
		
		spyText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		spyText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		
		return spyText;
	}
	
	public static BaseComponent createRangeList(Player player) {
		ChatColor baseColor = ConfigUtils.getDefaultColor();
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(baseColor);
		BaseComponent crossDimension = new TextComponent(" *");
		crossDimension.setColor(baseColor);
		BaseComponent listText = new TextComponent("Availiable ranges: \n");
		listText.setColor(baseColor);
		for(Range r : Range.getChatRangeList()) {
			BaseComponent e = r.isCrossDimensional() 
					? new TextComponent(prefix, getRangeTextComponent(r), crossDimension, NEW_LINE)
					: new TextComponent(prefix, getRangeTextComponent(r), NEW_LINE);
			listText.addExtra(e);
		}
		return listText;
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
	
	private static BaseComponent simpleListComponent(String v) {
		BaseComponent spacer = new TextComponent("  ");
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(COLOUR_PREFIX);
		BaseComponent value = new TextComponent(v);
		value.setColor(COLOUR_VALUE);
		return new TextComponent(spacer, prefix, value, NEW_LINE);
	}
	
	private static BaseComponent getItemComponent(ItemStack heldItem) {
		BaseComponent itemText = new TextComponent("[");
		for(BaseComponent c : TextComponent.fromLegacyText(heldItem.getItemMeta().getDisplayName())) {
			itemText.addExtra(c);
		}
		itemText.addExtra(new TextComponent("]"));
		String itemJson;
		try {
			itemJson = getItemJson(heldItem);
			itemText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent(itemJson)}));
		} catch (ReflectiveOperationException e) {
			itemText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("Error fetching item data")}));
			e.printStackTrace();
		}
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
