package com.brokenworldrp.chatranges.chatrange;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RangeRepository {
    private static final String RANGE_FILE = "/Plugins/BWChatRanges/rangeData.yml";
    private static final String JSON_KEY = "player-ranges";

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

    public boolean saveRepositoryData(){
        JSONObject object = new JSONObject();
        object.put(JSON_KEY, playerRanges);
        try(FileWriter file = new FileWriter(RANGE_FILE)){
            file.write(object.toJSONString());
            Bukkit.getLogger().info(String.format("Saved player ranges to %s.", RANGE_FILE));
            return true;
        }catch(IOException e){
            Bukkit.getLogger().info(String.format("Failed to save player ranges to %s.", RANGE_FILE));
            return false;
        }
    }

    public boolean loadRepositoryData(){

        File configFile = new File(RANGE_FILE);

        if(!configFile.exists()){
            Bukkit.getLogger().info(String.format("Failed to load player ranges, range file file not found at '%s'.", RANGE_FILE));
            playerRanges = new HashMap<>();
            return false;
        }
        try(FileReader file = new FileReader(RANGE_FILE)){
            JSONParser jsonParser = new JSONParser();
            JSONObject object = (JSONObject) jsonParser.parse(file);
            playerRanges = (HashMap<UUID, String>) object.get(JSON_KEY);
            return true;
        }catch(Exception e){
            Bukkit.getLogger().info(String.format("Failed to load player ranges from '%s'", RANGE_FILE));
        }
        playerRanges = new HashMap<>();
        return false;
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
        return rangePrefixComponents.get(range.getKey());
    }

    public BaseComponent getRangeTextComponent(Range range) {
        return rangeTextComponents.get(range.getKey());
    }

    public void playerSetup(UUID id) {
        playerRanges.put(id, defaultKey);
    }
}
