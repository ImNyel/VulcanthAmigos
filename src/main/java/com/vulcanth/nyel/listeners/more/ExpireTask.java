package com.vulcanth.nyel.listeners.more;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.helpers.Party;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class ExpireTask implements Runnable {
    String senderNick;
    String _nick;

    public ExpireTask(String senderNick, String _nick) {
        this.senderNick = senderNick;
        this._nick = _nick;
    }

    @Override
    public void run() {
        try {
            if (Party.get(senderNick) == null) {
                return;
            }
            if (Main.getPD(_nick).getInvites().contains(senderNick)) {
                Main.getPD(_nick).getInvites().remove(senderNick);
                Party.get(senderNick).getPlayers().forEach((players, role) -> {
                    if (ProxyServer.getInstance()
                            .getPlayer(players) == null) return;
                    ProxyServer.getInstance()
                            .getPlayer(players)
                            .sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + Role.getPrefixed(_nick) + ChatColor.RED + " ignorou o pedido de party!"));
                });
                ProxyServer.getInstance()
                        .getPlayer(_nick).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "O convite de " + ChatColor.GRAY + senderNick + "§r expirou!"));

            }
        } catch (NullPointerException throwable) {
            throwable.printStackTrace();

        }
    }

}