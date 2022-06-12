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

public class negar extends SubCommand {

    public negar() {
        super("negar", "negar", "Negar o pedido de amizade", false);
    }

    public negar(String name, String usage, String description, boolean onlyForPlayer) {
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
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cColoque uma pessoa para negar o pedido de amizade dela! /amigo negar <jogador>")));

        } else {
            PlayerData argPdata = new PlayerData(args[1], true);
            if (!playerData.hasRequestFrom(args[1])) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não tem um pedido de amizade desse jogador!")));
                return;
            }
            if (args[1].equalsIgnoreCase(sender.getName())) {

                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não pode negar amizade para você mesmo!")));
                return;
            }
            playerData.refuseFriendRequest(argPdata);
            playerData.saveDB();
            argPdata.saveDB();
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê negou o pedido de amizade!")));
            if (ProxyServer.getInstance().getPlayer(args[1]) == null) return;
            ProxyServer.getInstance().getPlayer(args[1]).sendMessage(TextComponent.fromLegacyText(Role.getPrefixed(sender.getName()) + "§c negou o seu pedido!"));
        }
    }
}
