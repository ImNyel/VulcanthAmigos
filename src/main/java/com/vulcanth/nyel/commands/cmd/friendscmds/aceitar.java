package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.PlayerData;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Arrays;

public class aceitar extends SubCommand {

    public aceitar() {
        super("aceitar", "aceitar", "Aceitar o pedido de amizade", false);
    }

    public aceitar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws PlayerData.NonExistentPlayerException, SQLException {
        PlayerData playerData = new PlayerData(sender.getName(), true);
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /amigo " + this.getUsage());
            return;
        } else if (args.length == 1) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cColoque uma pessoa para aceitar o pedido de amizade dela! /amigo aceitar <jogador>")));

        } else {
            if (args[1].equalsIgnoreCase(sender.getName())) {

                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não pode aceitar a amizade para você mesmo!")));
                return;
            }
            PlayerData argPdata = new PlayerData(args[1], true);
            if (!playerData.hasRequestFrom(args[1])) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não tem um pedido de amizade desse jogador!")));
                return;
            }
            playerData.acceptFriendRequest(argPdata);
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§aVocê aceitou o pedido de amizade!")));
            playerData.saveDB();
            argPdata.saveDB();
            if (ProxyServer.getInstance().getPlayer(args[1]) == null) return;
            ProxyServer.getInstance().getPlayer(args[1]).sendMessage(TextComponent.fromLegacyText(Role.getPrefixed(sender.getName()) + "§a agora é seu amigo!"));
        }
    }
}
