package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.helpers.Party;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class negar extends SubCommand {

    public negar() {
        super("negar", "negar", "Negar o pedido de party", false);
    }

    public negar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /party " + this.getUsage());
            return;
        } else if (args.length > 1) {
            String nick = args[1];
            if (ProxyServer.getInstance().getPlayer(nick) == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador não está online!")));
                return;
            }
            nick = ProxyServer.getInstance().getPlayer(nick).getName();

            {
                if (!Main.getPD(sender.getName()).getInvites().contains(nick)) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não tem um convite de party do jogador " + nick)));
                    if (Party.get(nick) == null)
                        sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador não pertence a uma party")));
                    return;
                } else if (Party.get(nick) == null) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("Esse jogador não pertence a uma party", ChatColor.RED)));

                    Main.getPD(sender.getName()).getInvites().remove(nick);
                }

            }
            {

                Main.getPD(sender.getName()).getInvites().remove(nick);
                Party.get(nick).getPlayers().forEach((players, role) -> {
                    if (ProxyServer.getInstance().getPlayer(players) == null) return;
                    ProxyServer.getInstance()
                            .getPlayer(players)
                            .sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + Role.getPrefixed(getName()) + ChatColor.RED + " negou o pedido de party!"));
                });
                ProxyServer.getInstance()
                        .getPlayer(sender.getName())
                        .sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + Role.getPrefixed(getName()) + ChatColor.RED + " negou o pedido de party!"));
            }

        } else {
            sender.sendMessage(Arrays.toString(new ComponentBuilder("Sem argumentos suficientes.").color(ChatColor.RED).create()));
        }
        return;
    }
}
