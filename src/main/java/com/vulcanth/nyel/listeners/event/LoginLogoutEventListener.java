package com.vulcanth.nyel.listeners.event;

import com.vulcanth.nyel.misc.PlayerData;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;

public class LoginLogoutEventListener implements Listener {
    @EventHandler
    public void onJoin(PostLoginEvent e) {
        try {
            if (!PlayerData.existsInTable(e.getPlayer().getName())) {
                new PlayerData(e.getPlayer().getName(), false).saveDB();
            }
            PlayerData playerData = new PlayerData(e.getPlayer().getName(), true);
            for (String friends :
                    playerData.getFriends()) {
                if (ProxyServer.getInstance().getPlayer(friends) == null) continue;


                ProxyServer.getInstance().getPlayer(friends).sendMessage(TextComponent.fromLegacyText(Role.getColored(e.getPlayer().getName()) + ChatColor.YELLOW + " entrou."));
            }
        } catch (PlayerData.NonExistentPlayerException | SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PostLoginEvent e) {
        try {
            if (!PlayerData.existsInTable(e.getPlayer().getName())) {
                new PlayerData(e.getPlayer().getName(), false).saveDB();
            }
            PlayerData playerData = new PlayerData(e.getPlayer().getName(), true);
            for (String friends :
                    playerData.getFriends()) {
                if (ProxyServer.getInstance().getPlayer(friends) == null) continue;
                ProxyServer.getInstance().getPlayer(friends).sendMessage(TextComponent.fromLegacyText(Role.getColored(e.getPlayer().getName()) + ChatColor.YELLOW + " saiu."));
            }
        } catch (PlayerData.NonExistentPlayerException | SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
