package com.vulcanth.nyel.commands.cmd.partycmds;

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
import java.util.Set;

public class expulsar extends SubCommand {

    public expulsar() {
        super("expulsar", "expulsar", "Expulsar um jogador que esteja presente na party", false);
    }

    public expulsar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /party " + this.getUsage());
            return;
        } else if (args.length > 1) {
            String nick = args[2];
            if (ProxyServer.getInstance().getPlayer(nick) == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador não está online")));
            }
            nick = ProxyServer.getInstance().getPlayer(nick).getName();
            Party party = Party.get(sender.getName());
            Set<String> oldPlayers = party.getPlayers().keySet();
            if (party == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não é dono de uma party!")));
                return;
            }
            if (party.getPlayerRole(nick) == Party.Role.OWNER) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não pode expulsar você próprio!")));
                return;
            }
            if (party.getPlayerRole(nick) == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador não está na sua party!")));
                return;

            }
            Party.RemoveReturn removeReturn = party.removePlayer(nick);

            for (String playerNicks : oldPlayers) {
                if (ProxyServer.getInstance().getPlayer(playerNicks) == null) continue;
                ProxyServer.getInstance().getPlayer(playerNicks).sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + Role.getPrefixed(getName()) + ChatColor.RED + " foi expulso da party!"));
                if (removeReturn == Party.RemoveReturn.PARTY_DELETED) {
                    ProxyServer.getInstance().getPlayer(playerNicks).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Não à mais jogadores suficientes para ter uma party, então a party foi deletada."));
                }
            }
        } else {
            sender.sendMessage(Arrays.toString(new ComponentBuilder("Sem argumentos suficientes.").color(ChatColor.RED).create()));
        }
        return;
    }
}
