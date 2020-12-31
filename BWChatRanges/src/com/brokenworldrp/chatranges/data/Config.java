package com.brokenworldrp.chatranges.data;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.utils.LoggingUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class Config {

    //singleton instance
    private static Config config_instance;

    private static final String RANGE_PERM_PREFIX = "chatrange.range.";
    private static final String RANGE_READ_PERM_PREFIX = "chatrange.read.";
    private static final String EMOTE_PERM_PREFIX = "chatrange.emote.";

    private static final String CONFIG_LOCATION = "plugins/BWChatRanges/config.yml";

    //defaults
    private ChatColor defaultMessageColor = ChatColor.GOLD;
    private ChatColor defaultErrorColor = ChatColor.RED;
    private ChatColor defaultPrefixColor = ChatColor.GRAY;
    private ChatColor defaultListKeyColor = ChatColor.GOLD;
    private ChatColor defaultListValueColor = ChatColor.AQUA;

    //messages
    final String mChangedRange;
    final String mJoinRange;
    final String mChatNoRecpients;
    final String mSpyStatusOff;
    final String mSpyStatusOn;
    final String mSpyOn;
    final String mSpyOff;
    final String mSpyInfo;
    final String mMuteOn;
    final String mMuteOff;

    //errors
    final String ePlayersOnly;
    final String ePlayerNoPermission;
    final String eRetrievingRange;
    final String eSettingRange;
    final String eRevertRange;
    final String eMissingCommandRange;
    final String eRetrievingCurrentRange;
    final String eRetrievingRetrievingRanges;
    final String eRetrievingCommandEmote;
    final String eMissingCommandEmote;
    final String eMissingMessageEmote;
    final String eMissingRangeMute;
    final String eRetrievingSpy;
    final String eSpyToggle;
    final String eItemParsing;
    final String eMutingUnknownRange;

    //formats
    final String messageFormat;
    final String emoteFormat;
    final String rangesCommandPrefix;
    //spy
    final String spyTag;
    ChatColor spyColor;
    final String spyPosition;

    //features
    final boolean aliasSingleMessage;
    final boolean noRecipientAlert;
    final boolean displayItemInChat;
    final boolean radialDistanceCheck;
    final boolean recipientNumberLogging;

    public static Config getConfig() throws NullPointerException{
        if(config_instance == null){
            config_instance = new Config();
        }
        return config_instance;
    }

    private ChatColor getColourFromString(String colour) throws IllegalArgumentException{
        colour = colour.replaceAll(" ", "_");
        colour = colour.toUpperCase();
        return ChatColor.valueOf(colour);
    }

    private void createConfigFile() {
        File configFile = new File(CONFIG_LOCATION);
        try {
            File parent = configFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            Files.copy(getClass().getResourceAsStream("/config.yml"), Paths.get(CONFIG_LOCATION), StandardCopyOption.REPLACE_EXISTING);
            LoggingUtil.logInfo(String.format("Config file created at '%s'", CONFIG_LOCATION));
        } catch (IOException e) {
            LoggingUtil.logWarning(String.format("Failed creating config file at '%s'", CONFIG_LOCATION));
            e.printStackTrace();
        }
    }

    private Config() throws NullPointerException{
        RangeRepository repo = RangeRepository.getRangeRepository();

        File configFile = new File(CONFIG_LOCATION);

        if(!configFile.exists()){
            LoggingUtil.logInfo(String.format("Config file not found at '%s', creating new config file.", configFile.getAbsolutePath()));
            createConfigFile();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection rangesSection = config.getConfigurationSection("ranges");
        ConfigurationSection emotesSection = config.getConfigurationSection("emotes");
        ConfigurationSection defaultsSection = config.getConfigurationSection("defaults");
        ConfigurationSection featuresSection = config.getConfigurationSection("features");
        ConfigurationSection formattingSection = config.getConfigurationSection("formatting");
        ConfigurationSection messagingSection = config.getConfigurationSection("messages");

        //load defaults

        try{
            defaultsSection = defaultsSection.contains("colour") ? defaultsSection.getConfigurationSection("colour") : defaultsSection;
            defaultMessageColor = getColourFromString(defaultsSection.getString("message", ""));
            defaultErrorColor = getColourFromString(defaultsSection.getString("error", ""));
            defaultPrefixColor = getColourFromString(defaultsSection.getString("prefix", ""));
            defaultsSection = defaultsSection.contains("list") ? defaultsSection.getConfigurationSection("list") : defaultsSection;
            defaultListKeyColor = getColourFromString(defaultsSection.getString("key", ""));
            defaultListValueColor = getColourFromString(defaultsSection.getString("value", ""));
        } catch(IllegalArgumentException e){
            LoggingUtil.logWarning("Failed to load default colour configuration, using default values");
            e.printStackTrace();
        }


        //load all ranges
        for(String rangeKey : rangesSection.getKeys(false)){
            //load values

            if(rangesSection.contains(rangeKey)) {
                ConfigurationSection range = rangesSection.getConfigurationSection(rangeKey);
                String name = range.getString("name", rangeKey);
                String command = range.getString("command", rangeKey);
                String description = range.getString("description", "Changes your chat range to {range}. [{distance}]");
                List<String> aliases = range.getStringList("aliases");
                boolean crossDimension = range.getBoolean("cross-dimension", false);
                Double distance = range.getDouble("distance", -1);
                //try get color, use default on fail
                ChatColor colour;
                try {
                    colour = getColourFromString(range.getString("colour"));
                } catch (IllegalArgumentException e) {
                    LoggingUtil.logWarning(String.format("Failed to load colour for range '%s', using default value", rangeKey));
                    e.printStackTrace();
                    colour = defaultPrefixColor;
                }
                String prefix = range.getString("prefix", String.format("[%s]", rangeKey));
                String permission = range.contains("permission")
                        ? RANGE_PERM_PREFIX + range.getString("permission")
                        : "";
                String readPermission = range.contains("read-permission")
                        ? RANGE_READ_PERM_PREFIX + range.getString("read-permission")
                        : "";
                //create ChatRange and add to chatRangeList
                ChatRange chatRange = new ChatRange(rangeKey, name, description,
                        command, aliases, crossDimension, distance,
                        colour, prefix, permission, readPermission);

                repo.addChatRange(chatRange);
            }
            else{
                LoggingUtil.logWarning("Failed loading range "+rangeKey);
            }
        }

        //load all emotes
        for(String emoteKey : emotesSection.getKeys(false)){

            //load values
            if(emotesSection.contains(emoteKey)){
                ConfigurationSection emote = emotesSection.getConfigurationSection(emoteKey);
                String name = emote.getString("name", emoteKey);
                String command = emote.getString("command", emoteKey);
                String description = emote.getString("description", "");
                String rangeKey = emote.getString("range", "");
                Optional<ChatRange> range = repo.getChatRangeByKey(rangeKey);
                List<String> aliases = emote.getStringList("aliases");
                ChatColor colour;
                try{
                    colour = getColourFromString(emote.getString("colour"));
                }catch (IllegalArgumentException e){
                    LoggingUtil.logWarning(String.format("Failed to load colour for emote '%s', using default value", rangeKey));
                    e.printStackTrace();
                    colour = defaultPrefixColor;
                }
                String prefix = emote.getString("prefix", String.format("[%s]", rangeKey));
                String permission = emote.contains("permission")
                        ? EMOTE_PERM_PREFIX + emote.getString("permission")
                        : "";

                if(range.isPresent()){
                    EmoteRange emoteRange = new EmoteRange(emoteKey, name, description,
                            command, aliases, range.get(), colour, prefix, permission);
                    repo.addEmoteRange(emoteRange);

                }
                else{
                    LoggingUtil.logWarning(String.format("Range not found for emote '%s', skipping.", rangeKey));
                }
            }
            else{
                LoggingUtil.logWarning("Failed loading emote "+emoteKey);
            }
        }

        //load default range
        Optional<String> defaultRangeKey = Optional.ofNullable(config.getConfigurationSection("defaults").getString("range"));
        if(defaultRangeKey.isPresent()){
            if(!repo.setDefaultRangeKey(defaultRangeKey.get())){
                LoggingUtil.logWarning(String.format("Failed to set '%s' as default range, defaulted to '%s'.", defaultRangeKey.get(), repo.getDefaultKey()));
            }
        }
        else{
            LoggingUtil.logWarning(String.format("Default range not set! Defaulting to '%s'.", repo.getDefaultKey()));
        }

        //load message types
        mChangedRange = messagingSection.getString("message-changed-range", "Your range have been set to {range}.");
        mJoinRange = messagingSection.getString("message-join-range", "Your range is currently set to {range}.");
        mChatNoRecpients = messagingSection.getString(" message-chat-no-recipient", "No players received your message in channel: {range}");
        mSpyStatusOn = messagingSection.getString("message-spy-status-on", "Message spying is currently activated.");
        mSpyStatusOff = messagingSection.getString("message-spy-status-off", "Message spying is currently deactivated.");
        mSpyOn = messagingSection.getString("message-spy-on", "Message spying is now activated.");
        mSpyOff = messagingSection.getString("message-spy-off", "Message spying is now deactivated.");
        mSpyInfo = messagingSection.getString("message-spy-info", "You are seeing this message because you are out of range of the sender and have Spy enabled.");
        mMuteOn = messagingSection.getString("message-mute-on", "You have now muted {range}. This will automatically unmute upon the next login.");
        mMuteOff = messagingSection.getString("message-mute-off", "You have now unmuted {range}.");

        //load error types
        ePlayersOnly = messagingSection.getString("error-players-only",  "Sorry, this command can only be used by players.");
        ePlayerNoPermission = messagingSection.getString("error-player-no-permission",  "Sorry, you do not have the permissions to run this command.");
        eRetrievingRange = messagingSection.getString("error-retrieving-command-range",  "Sorry, an unexpected error has occurred while trying to retrieve information about this command.");
        eSettingRange = messagingSection.getString("error-setting-range",  "Sorry, an unexpected error has occurred while trying to set your range.");
        eRevertRange = messagingSection.getString("error-revert-range",  "Sorry, an unexpected error has occurred while trying to revert your range.");
        eMissingCommandRange = messagingSection.getString("error-missing-command-range",  "Sorry, we are unable to find the range for this command.");
        eRetrievingCurrentRange = messagingSection.getString("error-retrieving-current-range",  "Sorry, an unexpected error has occurred while trying to retrieve your current range.");
        eRetrievingRetrievingRanges = messagingSection.getString("error-retrieving-ranges",  "Sorry, an unexpected error has occurred while trying to retrieve all of the available ranges.");
        eRetrievingCommandEmote = messagingSection.getString("error-retrieving-command-emote",  "Sorry, an unexpected error has occurred while trying to retrieve information about this command.");
        eMissingCommandEmote = messagingSection.getString("error-missing-command-emote",  "Sorry, we are unable to find the emote for this command.");
        eMissingMessageEmote = messagingSection.getString("error-missing-message-emote",  "Please include a message for your emote.");
        eMissingRangeMute = messagingSection.getString("error-missing-range-mute",  "Please provide the channel you want to mute or unmute.");
        eRetrievingSpy = messagingSection.getString("error-retrieving-spy",  "Sorry, an unexpected error has occurred while trying to retrieve your spy status.");
        eSpyToggle = messagingSection.getString("error-spy-toggle",  "Sorry, an unexpected error has occurred while trying to toggle your spy mode.");
        eItemParsing = messagingSection.getString("error-item-parsing",  "Sorry, an unexpected error has occurred while trying to format the item.");
        eMutingUnknownRange = messagingSection.getString("error-mute-unknown-range",  "Sorry, I am unable to find the range '{range}'.");

        //load features
        aliasSingleMessage = featuresSection.getBoolean("alias-single-message", false);
        noRecipientAlert = featuresSection.getBoolean("no-recipients-alert", true);
        displayItemInChat = featuresSection.getBoolean("display-item-in-chat", true);
        radialDistanceCheck = featuresSection.getBoolean("radial-distance-check", false);
        recipientNumberLogging = featuresSection.getBoolean("recipient-number-logging", false);

        //load formatting
        messageFormat = formattingSection.getString("message", "{prefix} {player}: {message}");
        emoteFormat = formattingSection.getString("emote", "* {prefix} {player} {message}");
        formattingSection = formattingSection
                .getConfigurationSection("command");
        rangesCommandPrefix = formattingSection
                .getConfigurationSection("ranges").getString("message-prefix", "Available Ranges:");
        formattingSection = formattingSection.getConfigurationSection("spy");
        spyTag = formattingSection.getString("tag");
        try{
            spyColor = getColourFromString(formattingSection.getString("colour", ""));
        }catch (IllegalArgumentException e){
            LoggingUtil.logWarning("Failed to load colour for spy tag, using default value");
            e.printStackTrace();
            spyColor = defaultPrefixColor;
        }
        spyPosition = formattingSection.getString("position", "prefix");
    }

    //format
    public String getMessageFormat() {
        return messageFormat;
    }
    public String getEmoteFormat() {
        return emoteFormat;
    }
    public String getSpyPosition() {
        return spyPosition;
    }

    //info messages
    public String getNoRecipientMessage() {
        return mChatNoRecpients;
    }
    public String getSpyStatusOnMessage() {
        return mSpyStatusOn;
    }
    public String getSpyStatusOffMessage() {
        return mSpyStatusOff;
    }
    public String getSpyToggleOnMessage() {
        return mSpyOn;
    }
    public String getSpyToggleOffMessage() {
        return mSpyOff;
    }
    public String getChangedRangeMessage() {
        return mChangedRange;
    }
    public String getJoinRangeMessage() {
        return mJoinRange;
    }
    public String getRangeMutedMessage() {
        return mMuteOn;
    }
    public String getRangeUnmutedMessage() {
        return mMuteOff;
    }
    public String getMutingUnknownRangeError() {
        return eMutingUnknownRange;
    }

    //error messages
    public String getNoPermissionError() {
        return ePlayerNoPermission;
    }
    public String getMissingCommandRangeError() {
        return eMissingCommandRange;
    }
    public String getPlayersOnlyError() {
        return ePlayersOnly;
    }
    public String getMissingCommandEmoteError(){
        return eMissingCommandEmote;
    }
    public String getMissingMessageEmoteError(){
        return eMissingMessageEmote;
    }
    public String getItemParsingError(){
        return eItemParsing;
    }

    //colours
    public ChatColor getDefaultColor() {
        return defaultMessageColor;
    }
    public ChatColor getPrefixColor() {
        return defaultPrefixColor;
    }
    public ChatColor getListValueColour() {
        return defaultListValueColor;
    }
    public ChatColor getListKeyColor() {
        return defaultListKeyColor;
    }
    public ChatColor getErrorColor() {
        return defaultErrorColor;
    }

    //features
    public boolean isAliasSingleMessageEnabled(){
        return aliasSingleMessage;
    }
    public boolean isDisplayItemInChatEnabled() {
        return displayItemInChat;
    }
    public boolean isNoRecipientAlertEnabled(){
        return noRecipientAlert;
    }
    public boolean isRadialDistanceCheckEnabled(){
        return radialDistanceCheck;
    }
    public boolean isRecipientNumberLoggingEnabled() {
        return recipientNumberLogging;
    }



}
