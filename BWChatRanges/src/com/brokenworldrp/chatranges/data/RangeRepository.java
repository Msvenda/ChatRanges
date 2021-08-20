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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RangeRepository {
    private static final String RANGE_FILE = "plugins/BWChatRanges/rangeData.yml";

    private static RangeRepository singleton;

    private String defaultKey;
    private final Map<String, EmoteRange> emoteRanges;
    private final Map<String, ChatRange> chatRanges;
    private Map<UUID, String> playerRanges;
    private final Map<String, Set<UUID>> mutedRanges;
    private final Map<String, BaseComponent> rangePrefixComponents;
    private final Map<String, BaseComponent> rangeTextComponents;
    private final Set<UUID> spies;

    public static RangeRepository getRangeRepository(){
        if(singleton == null){
            singleton = new RangeRepository();
        }
        return singleton;
    }

    private RangeRepository(){
        emoteRanges = new HashMap<>();
        chatRanges = new HashMap<>();
        playerRanges = new HashMap<>();
        mutedRanges = new HashMap<>();
        spies = new HashSet<>();
        rangePrefixComponents = new HashMap<>();
        rangeTextComponents = new HashMap<>();
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
        mutedRanges.put(range.getKey(), new HashSet<>());
    }
    public void addEmoteRange(EmoteRange range){
        emoteRanges.put(range.getKey(), range);
    }

    public Optional<ChatRange> getPlayerChatRange(UUID playerID) {
        //playerSetup(playerID);
        return Optional.ofNullable(chatRanges.get(playerRanges.get(playerID)));

    }

    public Optional<ChatRange> getChatRangeByKey(String key) {

        return Optional.ofNullable(chatRanges.get(key));
    }

    public Optional<EmoteRange> getEmoteRangeByKey(String rangeKey) {
        return Optional.ofNullable(emoteRanges.get(rangeKey));
    }

    public boolean setPlayerRangeByKey(UUID playerID, String rangeKey) {
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

    public boolean getMuteStatusForPlayer(Player player, String rangeKey){
        return mutedRanges.get(rangeKey).contains(player.getUniqueId());
    }

    public void muteRangeForPlayer(Player player, String rangeKey){
        mutedRanges.get(rangeKey).add(player.getUniqueId());
    }

    public void unmuteRangeForPlayer(Player player, String rangeKey){
        mutedRanges.get(rangeKey).remove(player.getUniqueId());
    }

    public List<Player> getSpies(){
        List<Player> players = new ArrayList<>();
        for(UUID id : spies){
            Player p = Bukkit.getPlayer(id);
            if(p != null){
                players.add(p);
            }
            else{
                LoggingUtil.logWarning("[getSpies] player with UUID " + id + " not found, removing from spy list");
                spies.remove(id);
            }
        }
        return players;
    }

    public boolean getSpyStatusForPlayer(Player player){
        return spies.contains(player.getUniqueId());
    }

    public void enableSpyForPlayer(Player player) {
        spies.add(player.getUniqueId());
    }

    public void disableSpyForPlayer(Player player) {
        spies.remove(player.getUniqueId());
    }


    public boolean containsComponentsFor(String key) {
        return rangePrefixComponents.containsKey(key);
    }

    public void addRangePrefixComponent(String key, BaseComponent messageText) {
        rangePrefixComponents.put(key, messageText);
    }

    public void addRangeTextComponent(String key, BaseComponent messageText) {
        rangeTextComponents.put(key, messageText);
    }

    public BaseComponent getRangePrefixComponent(Range range) {
        if(range instanceof  EmoteRange){
            ChatRange r = ((EmoteRange)range).getEmoteRange();
            initializeComponent(r);
            return rangePrefixComponents.get(r.getKey());
        }
        else{
            initializeComponent((ChatRange)range);
            return rangePrefixComponents.get(range.getKey());
        }
    }
    public BaseComponent getRangeTextComponent(Range range) {
        if(range instanceof  EmoteRange){
            ChatRange r = ((EmoteRange)range).getEmoteRange();
            initializeComponent(r);
            return rangeTextComponents.get(r.getKey());
        }
        else{
            initializeComponent((ChatRange)range);
            return rangeTextComponents.get(range.getKey());
        }
    }

    private void initializeComponent(ChatRange range){
        if(rangePrefixComponents.containsKey(range.getKey())){
            return;
        }
        if(!TextUtils.createRangeComponents(range)){
            LoggingUtil.logWarning(String.format("Failed creating chat components for range '%s'!", range.getKey()));
        }
    }

    public ChatRange playerSetup(Player player) {
        UUID id = player.getUniqueId();
        if(!playerRanges.containsKey(id)){
            setPlayerRangeByKey(id, defaultKey);
        }
        else if(!chatRanges.containsKey(playerRanges.get(id))){
            setPlayerRangeByKey(id, defaultKey);
        }
        return chatRanges.get(playerRanges.get(id));
    }

    public void playerCleanup(Player player) {
        for(Set<UUID> muted : mutedRanges.values()){
            muted.remove(player.getUniqueId());
        }
    }

    public List<Player> getMutedPlayersForRange(String key) {
        List<Player> players = new ArrayList<>();
        for(UUID id : mutedRanges.get(key)){
            players.add(Bukkit.getPlayer(id));
        }
        return players;
    }


}
