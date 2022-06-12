package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.helpers.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class puxar extends SubCommand {

    public puxar() {
        super("puxar", "puxar", "Puxar os membros da party para um partida/lobby", false);
    }

    public puxar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /party " + this.getUsage());
            return;
        } else if (args.length > 1) {
            Party party = Party.get(sender.getName());


            if (!(party == null)) {
                party.getPlayers().forEach((nick, role) -> {
                    if (role == Party.Role.NORMAL) {
                        if (ProxyServer.getInstance().getPlayer(nick) == null) return;
                        ProxyServer.getInstance().getPlayer(nick).connect(((ProxiedPlayer) sender).getServer().getInfo());
                        ProxyServer.getInstance().getPlayer(nick).sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "Você foi puxado para o servidor de " + ChatColor.GRAY + sender.getName() + ChatColor.YELLOW + "!"));
                    }
                });
            } else {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não é dono de uma party!")));
            }
        }
    }
}
