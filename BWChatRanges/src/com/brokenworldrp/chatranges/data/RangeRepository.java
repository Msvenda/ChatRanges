package com.brokenworldrp.chatranges.data;

import com.brokenworldrp.chatranges.chatrange.ChatRange;
import com.brokenworldrp.chatranges.chatrange.EmoteRange;
import com.brokenworldrp.chatranges.chatrange.Range;
import com.brokenworldrp.chatranges.utils.LoggingUtil;
import com.brokenworldrp.chatranges.utils.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RangeRepository {
    private static final String RANGE_FILE = "plugins/BWChatRanges/rangeData.yml";

    private static RangeRepository singleton;

    private String defaultKey;
    private Map<String, EmoteRange> emoteRanges;
    private Map<String, ChatRange> chatRanges;
    private Map<UUID, String> playerRanges;
    private Map<String, List<Player>> mutedRanges;
    private Map<String, BaseComponent> rangePrefixComponents;
    private Map<String, BaseComponent> rangeTextComponents;
    private Set<Player> spies;

    public static RangeRepository getRangeRepository(){
        if(singleton == null){
            singleton = new RangeRepository();
        }
        return singleton;
    }

    private RangeRepository(){
        emoteRanges = new HashMap<>();
        chatRanges = new HashMap<>();
        mutedRanges = new HashMap<>();
        spies = new HashSet<>();
        rangeTextComponents = new HashMap<>();
        rangePrefixComponents = new HashMap<>();
        loadRepositoryData();
    }

    public boolean saveRepositoryData(){
        File configFile = new File(RANGE_FILE);
        YamlConfiguration data = YamlConfiguration.loadConfiguration(configFile);
        for(UUID id : playerRanges.keySet()){
            data.set(id.toString(), playerRanges.get(id));
        }
        try {
            data.save(configFile);
            LoggingUtil.logInfo(String.format("Saved player ranges to %s.", RANGE_FILE));
            return true;
        } catch (IOException e) {
            LoggingUtil.logWarning(String.format("Failed to save player ranges to %s.", RANGE_FILE));
            e.printStackTrace();
            return false;
        }
    }

    public void loadRepositoryData(){

        File configFile = new File(RANGE_FILE);
        YamlConfiguration data = YamlConfiguration.loadConfiguration(configFile);
        playerRanges = new HashMap<>();
        for(String key : data.getKeys(false)){
            playerRanges.put(UUID.fromString(key), data.getString(key));
        }
    }

    public Boolean createRangeComponents(Range range) {
        if(rangePrefixComponents.containsKey(range.getKey())) {
            return false;
        }

        //create shared hover text
        String clickCommand = String.format("/%s ", range.getCommand());
        BaseComponent hoverText = new TextComponent(TextUtils.simpleKeyValueComponent("Name:", range.getName()));
        hoverText.addExtra(TextUtils.simpleKeyValueComponent("Range:", String.format("%.1f blocks", range.getRange())));
        hoverText.addExtra(TextUtils.simpleKeyValueComponent("Cross-Dimensional", range.isCrossDimensional() ?
                "\u2713" : "\u2717"));
        hoverText.addExtra(TextUtils.simpleKeyValueComponent("Command:", clickCommand));
        for(String alias : range.getAliases()) {
            hoverText.addExtra(TextUtils.simpleListComponent(alias));
        }
        hoverText.addExtra(TextUtils.simpleKeyValueComponent("Prefix:", range.getPrefix()));
        hoverText.addExtra(TextUtils.simpleKeyValueComponent("Colour:", range.getColor().getName()));
        hoverText.addExtra(new TextComponent("\n"));
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

    public boolean setDefaultRangeKey(String key){
        if(chatRanges.containsKey(key)){
            defaultKey = key;
            return true;
        }
        return false;
    }

    public String getDefaultKey(){
        return defaultKey;
    }

    public void addChatRange(ChatRange range){
        if(defaultKey == null){
            defaultKey = range.getKey();
        }
        chatRanges.put(range.getKey(), range);
        mutedRanges.put(range.getKey(), new ArrayList<>());
    }
    public void addEmoteRange(EmoteRange range){
        emoteRanges.put(range.getKey(), range);
    }

    public Optional<ChatRange> getPlayerChatRange(UUID playerID) {
        return Optional.ofNullable(chatRanges.get(playerRanges.get(playerID)));
    }

    public Optional<ChatRange> getChatRangeByKey(String key) {

        return Optional.ofNullable(chatRanges.get(key));
    }

    public Optional<EmoteRange> getEmoteRangeByKey(String rangeKey) {
        return Optional.ofNullable(emoteRanges.get(rangeKey));
    }

    public boolean setPlayerRangebyKey(UUID playerID, String rangeKey) {
        if(chatRanges.containsKey(rangeKey)) {
            playerRanges.put(playerID, rangeKey);
            return true;
        }
        return false;
    }

    public List<ChatRange> getChatRangeList() {
        return new ArrayList<>(chatRanges.values());
    }

    public List<EmoteRange> getEmoteRangeList() {
        return new ArrayList<>(emoteRanges.values());
    }

    public void toggleMuteRangeForPlayer(Player player, String rangeKey) {
        if(mutedRanges.get(rangeKey).contains(player)) {
            mutedRanges.get(rangeKey).remove(player);
            return;
        }
        mutedRanges.get(rangeKey).add(player);
    }

    public Set<Player> getSpies(){
        return spies;
    }

    public void enableSpyForPlayer(Player player) {
        spies.add(player);
    }

    public void disableSpyForPlayer(Player player) {
        spies.remove(player);
    }


    public boolean containsComponentsFor(String key) {
        return rangePrefixComponents.containsKey(key);
    }

    public void addRangePrefixComponent(String key, BaseComponent messageText) {
        rangePrefixComponents.put(key, messageText);
    }

    public void addRangeTextComponent(String key, BaseComponent messageText) {
        rangePrefixComponents.put(key, messageText);
    }

    public BaseComponent getRangePrefixComponent(Range range) {
        initializeComponent(range);
        return rangePrefixComponents.get(range.getKey());
    }

    public BaseComponent getRangeTextComponent(Range range) {
        initializeComponent(range);
        return rangeTextComponents.get(range.getKey());
    }
    private void initializeComponent(Range range){
        if(rangePrefixComponents.containsKey(range.getKey())){
            return;
        }
        if(!TextUtils.createRangeComponents(range)){
            LoggingUtil.logWarning(String.format("Failed creating chat components for range '%s'!", range.getKey()));
        }
    }

    public void playerSetup(UUID id) {
        playerRanges.put(id, defaultKey);
    }
}
