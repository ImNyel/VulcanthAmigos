package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.PlayerData;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class add extends SubCommand {

    public add() {
        super("add", "add", "Enviar um pedido de amizade", false);
    }

    public add(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws PlayerData.NonExistentPlayerException {
        if (!(sender instanceof ProxiedPlayer)) return;
        PlayerData playerData = new PlayerData(sender.getName(), true);
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /amigo " + this.getUsage());
            return;
        } else if (args.length == 1) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cInforme o nick do jogador! /amigo add <jogador>")));
        } else {
            PlayerData argPdata = new PlayerData(args[1], true);
            if (args[1].equalsIgnoreCase(sender.getName())) {

                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não pode pedir amizade para você mesmo!")));
                return;
            }
            if (argPdata.hasRequestFrom(sender.getName())) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê já mandou um pedido de amizade para esse jogador!")));
                return;
            }
            if (playerData.isFriendOf(argPdata)) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê já é um amigo desse jogador!")));
                return;

            }
            if (argPdata.negated(playerData)) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cEssa pessoa negou o seu pedido de amizade, você precisa de esperar pelo menos 30 minutos para mandar um pedido novamente.")));
                return;
            }
            playerData.requestFriendTo(argPdata);
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.YELLOW + "Você mandou um pedido de amizade para " + args[1] + "!")));
            if (ProxyServer.getInstance().getPlayer(args[1]) != null) {
                ProxyServer.getInstance().getPlayer(args[1]).sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"Você recebeu um pedido de amizade de \",\"color\":\"dark_green\"},{\"text\":\"" + Role.getPrefixed(sender.getName()) + "\",\"color\":\"gray\"},{\"text\":\"\\n\"},{\"text\":\"Clique \",\"color\":\"dark_green\"},{\"text\":\"AQUI\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/amigo aceitar " + sender.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Clique aqui para aceitar\"}},{\"text\":\" para aceitar ou \",\"color\":\"dark_green\"},{\"text\":\"AQUI\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/amigo negar " + sender.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Clque aqui para negar\"}},{\"text\":\" para negar.\",\"color\":\"dark_green\"}]"));
            }
        }
    }
}
