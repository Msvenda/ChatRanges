package com.brokenworldrp.chatranges.utils;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.data.Config;
import com.brokenworldrp.chatranges.data.RangeRepository;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class TextUtils {
	private static final String DELIMITER_REGEX = "[{}]+";
	
	private static final TextComponent NEW_LINE = new TextComponent("\n");
	

	
	public static Boolean createRangeComponents(ChatRange range) {
		RangeRepository repo = RangeRepository.getRangeRepository();

		if(repo.containsComponentsFor(range.getKey())) {
			return false;
		}
		
		//create shared hover text
		String clickCommand = String.format("/%s ", range.getCommand());
		BaseComponent hoverText = new TextComponent(simpleKeyValueComponent("Name", range.getName()));
		if(range.isCrossDimensional() || range.getRange() <=0){
			hoverText = new TextComponent(hoverText, simpleKeyValueComponent("Range", "\u221e"));
		}
		else{
			hoverText = new TextComponent(hoverText, simpleKeyValueComponent("Range", String.format("%.1f blocks", range.getRange())));
		}
		hoverText = new TextComponent(hoverText, TextUtils.simpleKeyValueComponent("Cross-Dimensional", range.isCrossDimensional() ?
				"\u2713" : "\u2717"));
		hoverText = new TextComponent(hoverText, simpleKeyValueComponent("Command", clickCommand));
		for(String alias : range.getAliases()) {
			hoverText = new TextComponent(hoverText, simpleListComponent(alias));
		}
		hoverText = new TextComponent(hoverText, simpleKeyValueComponent("Prefix", range.getPrefix()));
		hoverText = new TextComponent(hoverText, simpleKeyValueComponent("Colour", range.getColor().getName()));
		hoverText = new TextComponent(hoverText, NEW_LINE);
		hoverText = new TextComponent(hoverText, new TextComponent("Click to change to this range.\nShift-click to pre-type \"" + clickCommand + "\""));
		
		

		//LoggingUtil.logInfo(range.getPrefix() + " | " + range.getName());

		//create prefix component
		BaseComponent messageText = new TextComponent(range.getPrefix());
		messageText.setColor(range.getColor());
		
		messageText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		messageText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
		
		repo.addRangePrefixComponent(range.getKey(), messageText);
		//LoggingUtil.logInfo(messageText.toLegacyText());
		//create range text component
		messageText = new TextComponent(range.getName());
		messageText.setColor(range.getColor());
		
		messageText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		messageText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
		repo.addRangeTextComponent(range.getKey(), messageText);
		//LoggingUtil.logInfo(messageText.toLegacyText());
		return true;
	}

	public static BaseComponent getNameTextComponent(Player player) {
		Config config = Config.getConfig();
		BaseComponent[] hoverText;
		String commandSuggest = "/msg " + player.getName() + " ";
		
		//get formatted nick
		BaseComponent displayName = new TextComponent();
		for(BaseComponent c : TextComponent.fromLegacyText(player.getDisplayName())) {
			displayName.addExtra(c);
		}
		
		//nickname
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(config.getPrefixColor());
		BaseComponent key = new TextComponent("Nickname: ");
		key.setColor(config.getListKeyColor());
		BaseComponent value = new TextComponent(displayName);

		//real name
		hoverText = new ComponentBuilder(prefix)
				.append(key)
				.append(value)
				.append(NEW_LINE)
				.append(simpleKeyValueComponent("Real name", player.getName()))
				.append(NEW_LINE)
				.append(String.format("Click to pre-type \"%s\".", commandSuggest)).create();

		displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
		displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggest));
		
		return displayName;
	}
	
	public static BaseComponent getMessageTextComponents(String m, Player p, Range r) {
		Config config = Config.getConfig();

		BaseComponent message = new TextComponent("");
		//surround @hand/offhand with brackets for easier recognition
		if(config.isDisplayItemInChatEnabled()){
			if( m.contains("@hand") || m.contains("@offhand")) {
				m = m.replaceAll("@hand", "{@hand}");
				m = m.replaceAll("@offhand", "{@offhand}");
			}
		}
		String[] parts = m.split(DELIMITER_REGEX);
		for(String part : parts) {
			if(part.equals("@hand")) {
				ItemStack heldItem = p.getInventory().getItemInMainHand();
				message = new TextComponent(message, getItemComponent(heldItem));
			}
			else if(part.equals("@offhand")) {
				ItemStack heldItem = p.getInventory().getItemInOffHand();
				message = new TextComponent(message, getItemComponent(heldItem));
			}
			else {
				BaseComponent partText = new TextComponent(part);
				partText.setColor(r.getColor());
				message = new TextComponent(message, partText);
			}
		}
		return message;
	}
	
	public static BaseComponent getEmoteTextComponents(String m, Player p, EmoteRange r) {
		Config config = Config.getConfig();
		m = m+" ";
		BaseComponent message = new TextComponent();
		String[] splitMessage = m.split("\"");

		BaseComponent quotations = new TextComponent("\"");
		quotations.setColor(r.getRangeColor());
		//if there is an odd number of elements, there is an even number of quotations

		for(int i = 0; i < splitMessage.length; i++) {
			//surround @hand/offhand with brackets for easier recognition
			if(config.isDisplayItemInChatEnabled()){
				splitMessage[i] = splitMessage[i].replaceAll("@hand", "{@hand}");
				splitMessage[i] = splitMessage[i].replaceAll("@offhand", "{@offhand}");
			}
			String[] parts = splitMessage[i].split(DELIMITER_REGEX);
			for(String part : parts) {
				if(part.equals("@hand")) {
					ItemStack heldItem = p.getInventory().getItemInMainHand();
					message = new TextComponent(message, getItemComponent(heldItem));
				}
				else if(part.equals("@offhand")) {
					ItemStack heldItem = p.getInventory().getItemInOffHand();
					message = new TextComponent(message, getItemComponent(heldItem));
				}
				else {
					BaseComponent partText = new TextComponent(part);
					if(splitMessage.length%2 == 0) {
						partText.setColor(r.getColor());
					}
					else {
						partText.setColor(i%2 == 0 ? r.getColor() : r.getRangeColor());
					}
					message = new TextComponent(message, partText);
				}
			}

			//put quotations around vocal part of emote
			if(splitMessage.length%2 == 1 && i%2 == 1){
				message = new TextComponent(quotations, message, quotations);
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
		Config config = Config.getConfig();
		BaseComponent spyText = new TextComponent(config.getSpyTag());
		spyText.setColor(config.getSpyColor());
		
		String command = "/spy toggle";
		
		BaseComponent hoverText = new TextComponent(config.getSpyInfoMessage() + "\n\nClick to toggle spy status.");
		hoverText.setColor(config.getListKeyColor());
		
		spyText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));
		spyText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		
		return spyText;
	}
	
	public static BaseComponent createRangeList(Player player) {
		RangeRepository repo = RangeRepository.getRangeRepository();
		Config config = Config.getConfig();
		ChatColor baseColor = config.getDefaultColor();
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(baseColor);
		BaseComponent crossDimension = new TextComponent(" *");
		crossDimension.setColor(baseColor);
		BaseComponent listText = new TextComponent(config.getRangesCommandPrefix() + "\n");
		listText.setColor(baseColor);
		for(Range r : repo.getChatRangeList()) {
			if(r.getWritePermission().isEmpty() || player.hasPermission(r.getWritePermission())){
				BaseComponent rangeComponent = repo.getRangeTextComponent(r);
				BaseComponent e = r.isCrossDimensional()
						? new TextComponent(prefix, rangeComponent, crossDimension, NEW_LINE)
						: new TextComponent(prefix, rangeComponent, NEW_LINE);
				listText = new TextComponent(listText, e);
			}
		}
		return listText;
	}
	
	public static BaseComponent simpleKeyValueComponent(String k, String v) {
		Config config = Config.getConfig();
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(config.getPrefixColor());
		BaseComponent key = new TextComponent(k+": ");
		key.setColor(config.getListKeyColor());
		BaseComponent value = new TextComponent(v);
		value.setColor(config.getListValueColour());
		
		return new TextComponent(prefix, key, value, NEW_LINE);
		
	}

	public static BaseComponent simpleListComponent(String v) {
		Config config = Config.getConfig();
		BaseComponent spacer = new TextComponent("  ");
		BaseComponent prefix = new TextComponent("- ");
		prefix.setColor(config.getPrefixColor());
		BaseComponent value = new TextComponent(v);
		value.setColor(config.getListValueColour());
		return new TextComponent(spacer, prefix, value, NEW_LINE);
	}
	
	private static BaseComponent getItemComponent(ItemStack heldItem) {
		Config config = Config.getConfig();
		BaseComponent itemText;
		if(heldItem != null){
			itemText = new TextComponent("[");
			if(heldItem.getItemMeta().getDisplayName().isEmpty()){
				itemText = new TextComponent(itemText, new TextComponent(WordUtils.capitalize(heldItem.getType().name().replace("_", " ").toLowerCase())));
			}
			else{
				System.out.println(heldItem.getItemMeta().getDisplayName());
				BaseComponent itemC = new TextComponent(heldItem.getItemMeta().getDisplayName());
				itemText = new TextComponent(itemText, itemC);
			}
			itemText = new TextComponent(itemText,  new TextComponent(ChatColor.WHITE + "]"));
			String itemJson;
			try {
				itemJson = getItemJson(heldItem);
				itemText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent(itemJson)}));
			} catch (ReflectiveOperationException e) {
				itemText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(config.getItemParsingError())}));
				e.printStackTrace();
			}
		}
		else{
			itemText = new TextComponent("[Empty hand]");
			itemText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("An empty hand.")}));
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
