package com.vulcanth.nyel.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.vulcanth.nyel.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerData {
    private final String nick;
    private final ArrayList<String> friends = new ArrayList<>();
    private final ArrayList<String> friend_requests = new ArrayList<>();
    private final HashMap<String, Long> negated_times;

    private static final HashMap<String, HashMap<String, Long>> negated_times_hashmap = new HashMap<>();

    public PlayerData(String nick, boolean retrieve) throws NonExistentPlayerException {

        String temp_nick = nick;

        if (retrieve) {
            try {
                ResultSet friends_row = Main.getConnection().createStatement().executeQuery("select * from friends_table where UPPER(`nick`) = UPPER('" + nick + "');");
                if (!friends_row.next()) {
                    throw new NonExistentPlayerException(nick);
                }
                temp_nick = friends_row.getString("nick");
                JsonArray friends_json = (JsonArray) new JsonParser().parse(new StringReader(friends_row.getString("friends")));
                friends_json.forEach((friend) -> {
                    friends.add(friend.getAsString());
                });
                JsonArray friend_requests_json = (JsonArray) new JsonParser().parse(new StringReader(friends_row.getString("friend_requests")));
                friend_requests_json.forEach((friend_request) -> {
                    friend_requests.add(friend_request.getAsString());
                });

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        this.nick = temp_nick;
        negated_times_hashmap.putIfAbsent(this.nick, new HashMap<>());
        negated_times = negated_times_hashmap.get(this.nick);
    }

    public ProxiedPlayer unwrap() {
        return Main.getInstance().getProxy().getPlayer(this.nick);
    }

    public static String getServerDisplayName(String nick) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(nick);
        if (p == null) {
            return "offline";
        }
        String bungee_server_name = p.getServer().getInfo().getName();
        Configuration config = Main.getInstance().getFriendsConfig();
        if (config.getStringList("servers_skywars").contains(bungee_server_name)) {
            return "jogando Skywars Solo";
        }
        if (config.getStringList("servers_bedwars_solo").contains(bungee_server_name)) {
            return "jogando Bedwars Solo";
        }
        if (config.getStringList("servers_bedwars_doubles").contains(bungee_server_name)) {
            return "jogando Bedwars Dupla";
        }
        if (config.getStringList("servers_bedwars_triples").contains(bungee_server_name)) {
            return "jogando Bedwars Trio";
        }
        if (config.getStringList("servers_bedwars_quads").contains(bungee_server_name)) {
            return "jogando Bedwars Quarteto";
        }

        if (config.getStringList("servers_lobby_skywars").contains(bungee_server_name)) {
            return "no Lobby de Skywars";
        }
        if (config.getStringList("servers_lobby_bedwars").contains(bungee_server_name)) {
            return "no Lobby de Bedwars";
        }
        if (config.getString("server_rankup").equals(bungee_server_name)) {
            return "jogando Rankup";
        }
        if (config.getString("server_factions").equals(bungee_server_name)) {
            return "jogando Factions";
        }
        return "";
    }

    public static boolean existsInTable(String nick) {
        try {
            return Main.getConnection().createStatement().executeQuery("select * from friends_table where UPPER(`nick`) = UPPER('" + nick + "');").next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public List<String> getFriends() {
        return Collections.unmodifiableList(friends);
    }

    public List<String> getFriendRequests() {
        return Collections.unmodifiableList(friend_requests);
    }

    public boolean hasRequestFrom(String nick) {
        try (Statement statement = Main.getConnection().createStatement()) {
            return statement.executeQuery("Select * from friends_table where json_contains(`friend_requests`, \"[\\\"" + nick + "\\\"]\") and nick = '" + this.nick + "'").next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void saveDB() throws SQLException {

        KotlinFeatures.use(Main.getConnection().createStatement(), (it) -> {
            try {
                if (!it.executeQuery("Select * from friends_table where UPPER(`nick`) = UPPER(\"" + nick + "\")").next()) {

                    it.addBatch("INSERT IGNORE INTO `friends_table` (`friends`, `friend_requests`, `nick`) VALUES (\"" + new Gson().toJson(friends).replace("\"", "\\\"") + "\",\"" + new Gson().toJson(friend_requests).replace("\"", "\\\"") + "\",'" + nick + "');");
                } else {
                    it.addBatch("update friends_table set `friends` = \"" + new Gson().toJson(friends).replace("\"", "\\\"") + "\", friend_requests = \"" + new Gson().toJson(friend_requests).replace("\"", "\\\"") + "\" where UPPER(`nick`) = UPPER('" + nick + "')");
                }
                it.executeBatch();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });


    }

    private void addFriendRequest(PlayerData who) {
        this.friend_requests.add(who.nick);
    }

    public void addFriend(PlayerData who) {
        this.friends.add(who.nick);
        who.friends.add(this.nick);
    }

    /**
     * returns true if the player exists and it did that successfully, false otherwise
     */
    public boolean requestFriendTo(String otherUser) {
        try {
            return requestFriendTo(new PlayerData(otherUser, true));
        } catch (NonExistentPlayerException e) {
            return false;
        }
    }

    /**
     * returns true if the player exists and it did that successfully, false otherwise
     */
    public boolean requestFriendTo(PlayerData otherUser) {
        AtomicBoolean ret = new AtomicBoolean(true);
        KotlinFeatures.let(otherUser, (it) -> {

            try {
                it.addFriendRequest(this);
                it.saveDB();
            } catch (SQLException e) {
                ret.set(false);
                e.printStackTrace();
            }
        });
        return ret.get();

    }

    public void acceptFriendRequest(PlayerData otherUser) {
        this.friend_requests.remove(otherUser.nick);
        this.addFriend(otherUser);
    }

    public boolean negated(PlayerData other) {
        if (this.negated_times.get(other.nick) == null) {
            return false;
        }
        System.out.println(System.currentTimeMillis() - this.negated_times.get(other.nick));
        return (System.currentTimeMillis() - this.negated_times.get(other.nick)) <= 30 * 60000;
    }

    public void refuseFriendRequest(PlayerData otherUser) {
        this.friend_requests.remove(otherUser.nick);

        this.negated_times.put(otherUser.nick, System.currentTimeMillis());
    }

    public boolean isFriendOf(PlayerData argPdata) {
        return this.friends.contains(argPdata.nick);
    }

    public static class NonExistentPlayerException extends Exception {
        private final String nick;

        protected NonExistentPlayerException(String player) {
            super("O player \"" + player + "\" não existe no banco de dados!");
            this.nick = player;
        }

        public String getNick() {
            return nick;
        }
    }
}
