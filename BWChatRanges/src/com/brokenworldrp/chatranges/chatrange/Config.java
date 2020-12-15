package com.brokenworldrp.chatranges.chatrange;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Config {

    //singleton instance
    private static Config config_instance;

    private static final String RANGE_PERM_PREFIX = "chatrange.range.";
    private static final String RANGE_READ_PERM_PREFIX = "chatrange.read.";
    private static final String EMOTE_PERM_PREFIX = "chatrange.emote.";

    private static final String CONFIG_LOCATION = "/Plugins/BWChatRanges/config.yml";

    //defaults
    private ChatColor defaultMessageColor = ChatColor.GOLD;
    private ChatColor defaultErrorColor = ChatColor.RED;
    private ChatColor defaultPrefixColor = ChatColor.GRAY;
    private ChatColor defaultListKeyColor = ChatColor.GOLD;
    private ChatColor defaultListValueColor = ChatColor.AQUA;

    //messages
    String mChangedRange;
    String mJoinRange;
    String mChatNoRecpients;
    String mSpyStatusOff;
    String mSpyStatusOn;
    String mSpyOn;
    String mSpyOff;
    String mSpyInfo;
    String mMuteOn;
    String mMuteOff;

    //errors
    String ePlayersOnly;
    String ePlayerNoPermission;
    String eRetrievingRange;
    String eSettingRange;
    String eRevertRange;
    String eMissingCommandRange;
    String eRetrievingCurrentRange;
    String eRetrievingRetrievingRanges;
    String eRetrievingCommandEmote;
    String eMissingCommandEmote;
    String eMissingMessageEmote;
    String eMissingRangeMute;
    String eRetrievingSpy;
    String eSpyToggle;
    String eItemParsing;
    String eMutingUnknownRange;

    //formats
    String messageFormat;
    String emoteFormat;
    String rangesCommandPrefix;
    //spy
    String spyTag;
    ChatColor spyColor;
    String spyPosition;

    //features
    boolean aliasSingleMessage;
    boolean noRecipientAlert;
    boolean displayItemInChat;
    boolean radialDistanceCheck;
    boolean recipientNumberLogging;

    public static Config getConfig(){
        if(config_instance == null){
            config_instance = new Config();
        }
        return config_instance;
    }

    private Config() {

        Logger logger = Bukkit.getLogger();
        RangeRepository repo = RangeRepository.getRangeRepository();

        File configFile = new File(CONFIG_LOCATION);

        if(!configFile.exists()){
            logger.info(String.format("Config file not found at '%s', creating new config file.", CONFIG_LOCATION));
            createConfigFile();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection rangesSection = config.getConfigurationSection("ranges");
        ConfigurationSection emotesSection = config.getConfigurationSection("emotes");
        ConfigurationSection defaultsSection = config.getConfigurationSection("defaults");
        ConfigurationSection featuresSection = config.getConfigurationSection("features");
        ConfigurationSection formattingSection = config.getConfigurationSection("formatting");
        ConfigurationSection messagingSection = config.getConfigurationSection("messaging");

        //load defaults

        try{
            defaultsSection = defaultsSection.getConfigurationSection("colour");
            defaultMessageColor = ChatColor.valueOf(defaultsSection.getString("message").toUpperCase());
            defaultErrorColor = ChatColor.valueOf(defaultsSection.getString("error").toUpperCase());
            defaultPrefixColor = ChatColor.valueOf(defaultsSection.getString("prefix").toUpperCase());
            defaultsSection = defaultsSection.getConfigurationSection("list");
            defaultListKeyColor = ChatColor.valueOf(defaultsSection.getString("key").toUpperCase());
            defaultListValueColor = ChatColor.valueOf(defaultsSection.getString("value").toUpperCase());
        } catch(IllegalArgumentException e){
            logger.warning("Failed to load default colour configuration, using default values");
            e.printStackTrace();
        }


        //load all ranges
        for(String rangeKey : rangesSection.getKeys(false)){
            //load values
            ConfigurationSection range = rangesSection.getConfigurationSection(rangeKey);
            String name = range.getString("name", rangeKey);
            String command = range.getString("command", rangeKey);
            String description = range.getString("description", "Changes your chat range to {range}. [{distance}]");
            List<String> aliases = range.getStringList("aliases");
            boolean crossDimension = range.getBoolean("cross-dimension", false);
            Double distance = range.getDouble("distance", 10);
            //try get color, use default on fail
            ChatColor colour;
            try{
                colour = ChatColor.valueOf(range.getString("colour").toUpperCase());
            }catch (IllegalArgumentException e){
                logger.warning(String.format("Failed to load colour for range '%s', using default value", rangeKey));
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

        //load all emotes
        for(String emoteKey : emotesSection.getKeys(false)){
            //load values
            ConfigurationSection emote = rangesSection.getConfigurationSection(emoteKey);
            String name = emote.getString("name");
            String command = emote.getString("command");
            String description = emote.getString("description");
            String rangeKey = emote.getString("range");
            Optional<ChatRange> range = repo.getChatRangeByKey(rangeKey);
            List<String> aliases = emote.getStringList("aliases");
            ChatColor colour;
            try{
                colour = ChatColor.valueOf(emote.getString("colour").toUpperCase());
            }catch (IllegalArgumentException e){
                logger.warning(String.format("Failed to load colour for emote '%s', using default value", rangeKey));
                e.printStackTrace();
                colour = defaultPrefixColor;
            }
            String prefix = emote.getString("prefix");
            String permission = emote.contains("permission")
                    ? EMOTE_PERM_PREFIX + emote.getString("permission")
                    : "";

            if(range.isPresent()){
                EmoteRange emoteRange = new EmoteRange(emoteKey, name, description,
                        command, aliases, range.get(), colour, prefix, permission);
                repo.addEmoteRange(emoteRange);

            }
            else{
                logger.warning(String.format("Range not found for emote '%s', skipping.", rangeKey));
            }
        }

        //load default range
        Optional<String> defaultRangeKey = Optional.ofNullable(defaultsSection.getString("range"));
        if(defaultRangeKey.isPresent()){
            if(!repo.setDefaultRangeKey(defaultRangeKey.get())){
                logger.warning(String.format("Failed to set '%s' as default range, defaulted to '%s'.", defaultRangeKey.get(), repo.getDefaultKey()));
            }
        }
        else{
            logger.warning(String.format("Default range not set! Defaulting to '%s'.", repo.getDefaultKey()));
        }

        //load message types
        mChangedRange = messagingSection.getString("message-changed-range", "Your range have been set to {range}.");
        mJoinRange = messagingSection.getString("message-join-range", "Your range is currently set to {range}.");
        mChatNoRecpients = messagingSection.getString(" message-chat-no-recipient", "No players received your message in channel: {range}");
        mSpyStatusOff = messagingSection.getString("message-spy-status-on", "Message spying is currently activated.");
        mSpyStatusOn = messagingSection.getString("message-spy-status-off", "Message spying is currently deactivated.");
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
        formattingSection.getConfigurationSection("command");
        rangesCommandPrefix = formattingSection
                .getConfigurationSection("ranges").getString("message-prefix", "Available Ranges:");
        formattingSection.getConfigurationSection("spy");
        spyTag = formattingSection.getString("tag");
        try{
            spyColor = ChatColor.valueOf(formattingSection.getString("colour").toUpperCase());
        }catch (IllegalArgumentException e){
            logger.warning("Failed to load colour for spy tag, using default value");
            e.printStackTrace();
            spyColor = defaultPrefixColor;
        }
        spyPosition = formattingSection.getString("position", "prefix");
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public String getSpyPosition() {
        return spyPosition;
    }

    public String getEmoteFormat() {
        return emoteFormat;
    }

    public ChatColor getErrorColor() {
        return defaultErrorColor;
    }

    public String getPlayersOnlyMessage() {
        return ePlayersOnly;
    }

    public String getNoPermissionMessage() {
        return ePlayerNoPermission;
    }

    public String getMissingCommandRangeMessage() {
        return eMissingCommandRange;
    }

    public String getNoEmoteMessage() {
        return eMissingCommandEmote;
    }

    public ChatColor getDefaultColor() {
        return defaultMessageColor;
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

    private void createConfigFile() {
        File configFile = new File(CONFIG_LOCATION);
        try {
            configFile.createNewFile();
            Files.write(Paths.get(CONFIG_LOCATION), configData.getBytes());
            Bukkit.getLogger().info(String.format("Config file created at '%s'", CONFIG_LOCATION));
        } catch (IOException e) {
            Bukkit.getLogger().warning(String.format("Failed creating config file at '%s'", CONFIG_LOCATION));
            e.printStackTrace();
        }
    }

    private final String configData = "# \n" +
            "# ===== ChatRange config.yml =====\n" +
            "# \n" +
            "\n" +
            "# ========================================\n" +
            "# ranges - Add your own custom range to the list. Use the following template:\n" +
            "# ........................................\n" +
            "# ranges:\n" +
            "#   <name of range>: Name of the range, this would only be used within this config\n" +
            "#     name: Name of the range to be displayed\n" +
            "#     description: Description to be shown for the command\n" +
            "#                  Placeholders accepted:\n" +
            "#                    - {range}           - Range name\n" +
            "#                    - {distance}        - Distance of this range\n" +
            "#                    - {permission}      - Permission required to use this command\n" +
            "#                    - {read-permission} - Permission required to see messages sent in this range\n" +
            "#     command: Command to switch to this range, whitespace characters not allowed\n" +
            "#     aliases: Different command names that can be used to trigger the command\n" +
            "#     cross-dimension: If this range work across dimensions/worlds\n" +
            "#     distance: Distance of how far can this chat range can reach, decimals allowed\n" +
            "#     colour: Colour of the chat in this range\n" +
            "#     prefix: Prefix to be appended before the chat, usually used to denote the range\n" +
            "#     permission: Permission required to run this command, blank if not required\n" +
            "#                 This will be appended after 'chatrange.range.'\n" +
            "#                 Leave blank to ignore.\n" +
            "#     read-permission: Permission required to see messages sent in this range\n" +
            "#                      This will be appended after 'chatrange.read.'\n" +
            "#                      Leave blank to ignore.\n" +
            "#     language: Options regarding languages here. Leave blank to ignore.\n" +
            "#       permission: Permission required to see this message without any alteration.\n" +
            "#                   Leave blank to not use language feature.\n" +
            "#       dictionary: File name of the CSV file which holds the words to translate to\n" +
            "#                   File should be placed in a folder named \"language\" in the folder which this config is in\n" +
            "#                   First column should hold the original word to look for\n" +
            "#                   Subsequent columns would be the words should be replaced with\n" +
            "#                   If there are no words to be replaced with, the word would be removed\n" +
            "#       # Options for when words are not found in the dictionary\n" +
            "#       randomise-message: Randomises the word arrangement \n" +
            "#       collapse-repeating-characters: Reduces repeating characters to a single character\n" +
            "# ========================================\n" +
            "ranges:\n" +
            "  local:\n" +
            "    name: 'Local'\n" +
            "    command: 'local'\n" +
            "    description: 'Changes your chat range to {range}. [{distance}]'\n" +
            "    aliases:\n" +
            "      - 'l'\n" +
            "    cross-dimension: false\n" +
            "    distance: 20\n" +
            "    colour: 'White'\n" +
            "    prefix: '[Local]'\n" +
            "  global:\n" +
            "    name: 'Global'\n" +
            "    command: 'global'\n" +
            "    description: 'Changes your chat range to {range}. [{distance}]'\n" +
            "    aliases:\n" +
            "      - 'g'\n" +
            "    cross-dimension: true\n" +
            "    colour: 'Aqua'\n" +
            "    prefix: '[Global]'\n" +
            "\n" +
            "# ========================================\n" +
            "# emotes - Add your own custom emote to the list. Use the following template:\n" +
            "# ........................................\n" +
            "# emotes:\n" +
            "#   <name of emotes>: Name of the emote, this would only be used within this config\n" +
            "#     name: Name of the range to be displayed\n" +
            "#     description: Description to be shown for the command\n" +
            "#                  Placeholders accepted:\n" +
            "#                    - {range}      - Range name this emote belongs to\n" +
            "#                    - {distance}   - Distance of this emote, defined by the range\n" +
            "#                    - {permission} - Permission required to use this command\n" +
            "#     command: Command to use this emote, whitespace characters not allowed\n" +
            "#     range: Name of range this emote belongs to, must be declared above\n" +
            "#     aliases: Different command names that can be used to trigger the command\n" +
            "#     prefix: Prefix to be appended before the chat, usually to denote the emote\n" +
            "#     permission: Permission required to run this command, blank if not required\n" +
            "#                 This will be appended after 'chatrange.emote.'\n" +
            "# ========================================\n" +
            "emotes:\n" +
            "  local:\n" +
            "    name: 'Local'\n" +
            "    description: 'Sends an emote at the {range} range.'\n" +
            "    command: 'mel'\n" +
            "    range: 'Local'\n" +
            "    aliases:\n" +
            "      - 'emote'\n" +
            "      - 'emotelocal'\n" +
            "    colour: 'Dark Purple'\n" +
            "    prefix: '[Local]'\n" +
            "\n" +
            "\n" +
            "features:\n" +
            "# Only sends the current message in the range desired if the alias of the command is used\n" +
            "  alias-single-message: true\n" +
            "# Notifies the player if there are no recipients for the message sent\n" +
            "  no-recipients-alert: true\n" +
            "# Using @hand will share a snapshot of the item on your hand to the chat\n" +
            "  display-item-in-chat: true\n" +
            "\n" +
            "defaults:\n" +
            "  range: 'Global'\n" +
            "  colour:\n" +
            "    message: 'Gold'\n" +
            "    error: 'Red'\n" +
            "    prefix: 'Gray'\n" +
            "    list:\n" +
            "      key: 'Gold'\n" +
            "      value: 'Aqua'\n" +
            "\n" +
            "messages:\n" +
            "  message-changed-range: 'Your range have been set to {range}.'\n" +
            "  message-join-range: 'Your range is currently set to {range}.'\n" +
            "  message-chat-no-recipient: 'No players received your message in channel: {range}'\n" +
            "  message-spy-status-on: 'Message spying is currently activated.'\n" +
            "  message-spy-status-off: 'Message spying is currently deactivated.'\n" +
            "  message-spy-on: 'Message spying is now activated.'\n" +
            "  message-spy-off: 'Message spying is now deactivated.'\n" +
            "  message-spy-info: 'You are seeing this message because you are out of range of the sender and have Spy enabled.'\n" +
            "  message-mute-on: 'You have now muted {range}. This will automatically unmute upon the next login.'\n" +
            "  message-mute-off: 'You have now unmuted {range}.'\n" +
            "  \n" +
            "  error-players-only: 'Sorry, this command can only be used by players.'\n" +
            "  error-player-no-permission: 'Sorry, you do not have the permissions to run this command.'\n" +
            "  error-retrieving-command-range: 'Sorry, an unexpected error has occurred while trying to retrieve information about this command.'\n" +
            "  error-setting-range: 'Sorry, an unexpected error has occurred while trying to set your range.'\n" +
            "  error-revert-range: 'Sorry, an unexpected error has occurred while trying to revert your range.'\n" +
            "  error-missing-command-range: 'Sorry, we are unable to find the range for this command.'\n" +
            "  error-retrieving-current-range: 'Sorry, an unexpected error has occurred while trying to retrieve your current range.'\n" +
            "  error-retrieving-ranges: 'Sorry, an unexpected error has occurred while trying to retrieve all of the available ranges.'\n" +
            "  error-retrieving-command-emote: 'Sorry, an unexpected error has occurred while trying to retrieve information about this command.'\n" +
            "  error-missing-command-emote: 'Sorry, we are unable to find the emote for this command.'\n" +
            "  error-missing-message-emote: 'Please include a message for your emote.'\n" +
            "  error-missing-range-mute: 'Please provide the channel you want to mute or unmute.'\n" +
            "  error-retrieving-spy: 'Sorry, an unexpected error has occurred while trying to retrieve your spy status.'\n" +
            "  error-spy-toggle: 'Sorry, an unexpected error has occurred while trying to toggle your spy mode.'\n" +
            "  error-item-parsing: 'Sorry, an unexpected error has occurred while trying to format the item.'\n" +
            "  error-mute-unknown-range: 'Sorry, I am unable to find the range ''{range}''.'\n" +
            "  error-translation: 'Sorry, an unexpected error has occurred while trying to scramble your message.'\n" +
            "  error-mod-retrieving-range: 'Sorry, an unexpected error has occurred while trying to retrieve information about this range.'\n" +
            "  error-mod-missing-range: 'Sorry, we are unable to find the range you have requested.'\n" +
            "  error-mod-no-permission-range: 'Sorry, you do not have the permissions required to change to this range.'\n" +
            "  error-mod-setting-range: 'Sorry, an unexpected error has occurred while trying to set your range.'\n" +
            "\n" +
            "formatting:\n" +
            "  message: '{prefix} {player}: {message}'\n" +
            "  emote: '* {prefix} {player} {message}'\n" +
            "  command:\n" +
            "    ranges:\n" +
            "      message-prefix: 'Available Ranges:'\n" +
            "    spy:\n" +
            "      tag: '[Spy]'\n" +
            "      colour: 'Gray'\n" +
            "      # 'prefix' (default) or 'suffix'\n" +
            "      position: 'prefix'\n";

}
