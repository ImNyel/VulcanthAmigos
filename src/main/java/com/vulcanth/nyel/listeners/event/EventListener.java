package com.vulcanth.nyel.listeners.event;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.helpers.Party;
import com.vulcanth.nyel.helpers.PlayerDataParty;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if (Main.getPdatas().stream().noneMatch((pd) -> pd.getNick().equals(event.getPlayer().getName())))
            Main.getPdatas().add(PlayerDataParty.of(event.getPlayer().getName()));
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        String player_that_leaved;
        new OnLeaveRunnable(event.getPlayer().getName(), (TimeUnit.MINUTES), Main.getInstance(), null);
    }

    @EventHandler
    public void onServerChange(ServerConnectedEvent e) {
        Party party = Party.get(e.getPlayer().getName());
        if (party == null) return;
        try {
            party.saveDB(Main.getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return;
        }
        party.getPlayers().forEach((nick, role) -> {
            if (ProxyServer.getInstance().getPlayer(nick) == null) return;
            if (role == Party.Role.NORMAL && !Main.getInstance().configYml.getStringList("lobbies").contains(e.getServer().getInfo().getName())) {
                ProxyServer.getInstance().getPlayer(nick).connect(e.getServer().getInfo());
            }
        });

    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.getMessage().startsWith("#")) {
            if (!(e.getSender() instanceof ProxiedPlayer)) return;
            Party party = Party.getByMember(((ProxiedPlayer) e.getSender()).getName());
            if (party == null) return;

            party.handleSendMessage((ProxiedPlayer) e.getSender(), e.getMessage().replaceFirst("#", ""));

            e.setCancelled(true);
        }
    }

    static class OnLeaveRunnable implements Runnable {
        final String player_that_leaved;

        protected OnLeaveRunnable(String player_that_leaved) {
            this.player_that_leaved = player_that_leaved;
        }

        public OnLeaveRunnable(String name, TimeUnit minutes, Main instance, String player_that_leaved) {
            this.player_that_leaved = player_that_leaved;
        }

        @Override
        public void run() {
            Party party = Party.getByMember(player_that_leaved);
            if (party == null | ProxyServer.getInstance().getPlayer(player_that_leaved) != null) {
                return;
            }
            Set<String> players = party.getPlayers().keySet();
            Party.RemoveReturn removeReturn = party.removePlayer(player_that_leaved);
            for (String pp : players) {
                if (pp.equalsIgnoreCase(player_that_leaved)) continue;
                if (ProxyServer.getInstance().getPlayer(pp) == null) continue;
                ProxyServer.getInstance().getPlayer(pp).sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + player_that_leaved + ChatColor.YELLOW + " saiu do servidor à mais de 5 minutos! Removendo da party..."));
                if (removeReturn == Party.RemoveReturn.PARTY_DELETED)
                    ProxyServer.getInstance().getPlayer(pp).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Não à mais jogadores suficientes para ter uma party, então a party foi apagada."));
                if (removeReturn == Party.RemoveReturn.OWNERSHIP_TRANSFERRED)
                    ProxyServer.getInstance().getPlayer(pp).sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "O dono da party foi removido e por isso " + party.getOwnerNick() + " é o dono da party agora!"));

            }
        }
    }
}
