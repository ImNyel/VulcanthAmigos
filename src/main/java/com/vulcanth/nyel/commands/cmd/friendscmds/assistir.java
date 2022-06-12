package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.PlayerData;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class assistir extends SubCommand {

    public assistir() {
        super("assistir", "assistir", "Assistir algum amigo ", false);
    }

    public assistir(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws PlayerData.NonExistentPlayerException {
        PlayerData playerData = new PlayerData(sender.getName(), true);
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /amigo " + this.getUsage());
            return;
        } else if (args.length == 1) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cColoque uma pessoa para assisti-la! /amigo assistir <jogador>")));

        } else {
            if (args[1].equalsIgnoreCase(sender.getName())) {

                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não pode assistir você mesmo! Ao menos que tenha os olhos fora da cabeça.")));
                return;
            }
            PlayerData argPdata = new PlayerData(args[1], true);
            if (!argPdata.isFriendOf(playerData)) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cVocê não é amigo deste jogador.")));
                return;
            }
            if (argPdata.unwrap() == null) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cEsse amigo não está online")));
                return;
            }
            if (Main.getInstance().configYml.getStringList("lobbies").contains(argPdata.unwrap().getServer().getInfo().getName())) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cEsse amigo não está em partida")));
                return;
            }
            playerData.unwrap().connect(argPdata.unwrap().getServer().getInfo());
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§aVocê está assistindo: " + Role.getPrefixed(argPdata.unwrap().getName()))));
            ProxyServer.getInstance().getPlayer(args[1]).sendMessage(TextComponent.fromLegacyText(Role.getPrefixed(sender.getName()) + "§a está assistindo você!"));
        }
    }
}