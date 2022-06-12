package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.helpers.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class priv extends SubCommand {

    public priv() {
        super("fechar", "fechar", "Fechar a party para que o jogador entre somente com convite", false);
    }

    public priv(String name, String usage, String description, boolean onlyForPlayer) {
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

            if (party == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não é dono de uma party!")));
                return;
            }
            party.setPublic(true);

            for (String playerNicks : party.getPlayers().keySet()) {
                if (ProxyServer.getInstance().getPlayer(playerNicks) == null) continue;
                ProxyServer.getInstance().getPlayer(playerNicks).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "A party tornou-se privada!"));

            }
        }
    }
}