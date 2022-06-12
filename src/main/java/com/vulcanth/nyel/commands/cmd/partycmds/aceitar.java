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

public class aceitar extends SubCommand {

    public aceitar() {
        super("aceitar", "aceitar", "Aceitar o pedido de party", false);
    }

    public aceitar(String name, String usage, String description, boolean onlyForPlayer) {
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

            nick = ProxyServer.getInstance().getPlayer(nick).getName();
            {
                if (Party.getByMember(sender.getName()) != null) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você já é um membro de uma party!")));
                    return;
                }

            }
            {
                if (!Main.getPD(sender.getName()).getInvites().contains(nick)) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não tem um convite de party do jogador " + nick)));
                    return;
                }
            }
            {
                Party party = Party.get(nick);
                party.addPlayer(sender.getName());
                Main.getPD(sender.getName()).getInvites().remove(nick);
                Party.get(nick).getPlayers().forEach((players, role) -> {
                    if (ProxyServer.getInstance().getPlayer(players) == null) return;
                    ProxyServer.getInstance()
                            .getPlayer(players)
                            .sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + Role.getPrefixed(getName()) + ChatColor.GREEN + " entrou na party!"));
                });
            }

        } else {
            sender.sendMessage(Arrays.toString(new ComponentBuilder("Sem argumentos suficientes.").color(ChatColor.RED).create()));
        }
        return;
    }
}
