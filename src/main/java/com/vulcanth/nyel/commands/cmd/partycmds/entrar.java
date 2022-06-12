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

public class entrar extends SubCommand {

    public entrar() {
        super("entrar", "entrar", "Entrar em uma party", false);
    }

    public entrar(String name, String usage, String description, boolean onlyForPlayer) {
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
            Party party = Party.get(nick);

            if (party == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não é dono de uma party!")));
                return;
            }
            if (!party.isPublic()) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Essa party não é pública!")));
                return;
            }
            party.addPlayer(sender.getName());
            for (String playerNicks : party.getPlayers().keySet()) {
                if (ProxyServer.getInstance().getPlayer(playerNicks) == null) continue;
                ProxyServer.getInstance().getPlayer(playerNicks).sendMessage(TextComponent.fromLegacyText(Role.getPrefixed(getName()) + ChatColor.GREEN + " entrou na party!"));

            }
        } else {
            sender.sendMessage(Arrays.toString(new ComponentBuilder("Sem argumentos suficientes.").color(ChatColor.RED).create()));
        }
        return;
    }
}
