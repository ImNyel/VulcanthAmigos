package com.vulcanth.nyel.helpers;


import com.google.gson.Gson;
import com.vulcanth.nyel.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class Party implements Cloneable {
    private static final HashMap<String, Party> cache = new HashMap<>();
    final HashMap<String, Role> players = new HashMap<>();
    private boolean isPublic = false;
    private String ownerNick;
    private boolean deleted = false;
    private boolean created = false;

    private Party(String ownerNick) {
        this.ownerNick = ownerNick;
        players.put(ownerNick, Role.OWNER);
        cache.put(ownerNick, this);
    }

    private Party() {

    }

    public static Party getOrNew(String ownerNick) {
        if (cache.get(ownerNick) == null) {

            return new Party(ownerNick);
        } else {
            if (cache.get(ownerNick).isDeleted())
                return new Party(ownerNick);
            return cache.get(ownerNick);
        }
    }

    public static Party getByMemberOrNew(String ownerNick, String nick) {
        if (getByMember(nick) == null) {
            return new Party(ownerNick);
        } else {
            return getByMember(nick);
        }
    }

    public static Party get(String ownerNick) {

        return cache.get(ownerNick) == null ? null : (cache.get(ownerNick).isDeleted() ? null : cache.get(ownerNick));

    }

    public static Party getByMember(String nick) {
        Optional<Map.Entry<String, Party>> result = cache.entrySet().stream().filter((ent) -> ent.getValue().getPlayers().containsKey(nick)).findAny();

        return result.filter(stringPartyEntry -> !stringPartyEntry.getValue().isDeleted()).map(Map.Entry::getValue).orElse(null);
    }

    public static boolean partyExists(String ownerNick) {
        return cache.containsKey(ownerNick);
    }

    public HashMap<String, Role> getPlayers() {
        return (HashMap<String, Role>) players.clone();//clonar para evitar modificação direta
    }

    public boolean isDeleted() {
        return deleted;
    }

    public RemoveReturn removePlayer(String playerToRemove) throws RuntimeException {
        if (players.size() - 1 == 1) {
            try {
                this.delete(Main.getConnection());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return RemoveReturn.PARTY_DELETED;
        }
        if (playerToRemove.equals(ownerNick)) {
            Random random = new Random();
            transferOwnership((String) players.keySet().toArray()[random.nextInt(players.keySet().size())]);
            return RemoveReturn.OWNERSHIP_TRANSFERRED;
        }
        players.remove(playerToRemove);
        return RemoveReturn.SUCCESSFULLY;
    }

    public void addPlayer(String playerNick) {
        players.put(playerNick, Role.NORMAL);
    }

    public void transferOwnership(String toPlayer) {
        if (!players.containsKey(toPlayer)) return;
        try (Statement statement = Main.getConnection().createStatement()) {

            statement.execute("Delete from `parties` where `owner` = '" + ownerNick + "'");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Party clone = new Party();
            clone.deleted = true;
            cache.put(ownerNick, clone);
            clone.delete(Main.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cache.put(toPlayer, this);
        players.put(ownerNick, Role.NORMAL);
        players.put(toPlayer, Role.OWNER);
        this.ownerNick = toPlayer;
    }

    public void handleSendMessage(ProxiedPlayer player, String message) {

        for (String _players : players.keySet()) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(_players);
            if (p == null) continue;
            p.sendMessage(new ComponentBuilder()
                    .append("[Party] ").color(ChatColor.LIGHT_PURPLE)
                    .append(player.getDisplayName() + ": ").color(ChatColor.GRAY)
                    .append(p.hasPermission("party.coloredchat") ? message.replaceAll("(?-i)(&)(?<code>[0-9a-fk-or])", "§${code}") : message).color(ChatColor.WHITE)
                    .create());
        }
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Role getPlayerRole(String name) {
        return players.get(name);
    }

    public void delete(Connection c) throws SQLException {
        if (deleted) return;
        Statement statement = c.createStatement();

        statement.execute("Delete from `parties` where `owner` = '" + ownerNick + "'");
        players.clear();
        ownerNick = null;
        isPublic = false;
        deleted = true;
        statement.close();
    }

    public void saveDB(Connection c) throws SQLException {
        if (deleted) return;

        Statement statement = c.createStatement();


        if (!statement.executeQuery("Select * from parties where `owner` = '" + ownerNick + "'").next()) {
            if (created) {
                if (!deleted) {
                    players.clear();
                    ownerNick = null;
                    isPublic = false;
                    deleted = true;
                }
                return;
            }
            created = true;
            statement.addBatch("INSERT IGNORE INTO `parties` (`isPublic`, `owner`, `players`) VALUES ('" + (isPublic ? 1 : 0) + "', '" + ownerNick + "', \"" + new Gson().toJson(players).replace("\"", "\\\"") + "\");");
        } else {
            statement.addBatch("update parties set `isPublic` = " + isPublic + ", `players` = \"" + new Gson().toJson(players).replace("\"", "\\\"") + "\" where `owner` = '" + ownerNick + "'");
        }
        statement.executeBatch();
        statement.close();
    }

    public void setOwnerNick(String owner) {
        this.ownerNick = owner;
    }

    public String getOwnerNick() {
        return ownerNick;
    }

    public enum RemoveReturn {
        OWNERSHIP_TRANSFERRED,
        PARTY_DELETED,
        SUCCESSFULLY
    }

    public enum Role {
        OWNER,
        NORMAL;

        @Override
        public String toString() {
            return name();
        }
    }
}
