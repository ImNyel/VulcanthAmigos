package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.helpers.Party;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Set;

public class sair extends SubCommand {

    public sair() {
        super("sair", "sair", "Sair da party na qual vós se encontra", false);
    }

    public sair(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /party " + this.getUsage());
            return;
        } else if (args.length > 1) {
            if (Party.getByMember(sender.getName()) == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não pertence a uma party!")));
                return;
            }
            Party party = Party.getByMember(sender.getName());
            Set<String> playersBefore = party.getPlayers().keySet();

            if (party.getPlayerRole(sender.getName()) == Party.Role.OWNER) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não pode abandonar a sua própria party! Use /party excluir ou transfira a sua party para outra pessoa usando /party transferir <jogador>.")));
                return;
            }
            Party.RemoveReturn removeReturn = party.removePlayer(sender.getName());

            for (String pp : playersBefore) {
                if (ProxyServer.getInstance().getPlayer(pp) == null) continue;
                ProxyServer.getInstance().getPlayer(pp).sendMessage(TextComponent.fromLegacyText(Role.getPrefixed(getName()) + ChatColor.RED + " saiu da party!"));
                if (removeReturn == Party.RemoveReturn.PARTY_DELETED)
                    ProxyServer.getInstance().getPlayer(pp).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Não à mais jogadores suficientes para ter uma party, então a party foi apagada."));

            }
            return;
        }
    }
}