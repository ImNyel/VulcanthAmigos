package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.PlayerData;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class listar extends SubCommand {

    public listar() {
        super("listar", "listar", "Veja todas suas amizades", false);
    }

    public listar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws PlayerData.NonExistentPlayerException, SQLException {
        int page = 0;
        PlayerData playerData = new PlayerData(sender.getName(), true);
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /amigo " + this.getUsage());
            return;
        } else if (args.length > 1) {
            try {
                page = Integer.parseUnsignedInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cIsso não é um numero valido!")));
                return;
            }
        }
        List<String> friends = playerData.getFriends();
        int startIndex = page * 15;
        int maxPages = ((int) (friends.size() / 15f));

        if (page > maxPages) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Isso não é um numero valido! escolha um numero entre 0-" + maxPages + 1)));
            return;
        }

        sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("Seus amigos:", ChatColor.YELLOW)));
        for (int i = 0; i < 15; i++) {
            if (startIndex + i + 1 > friends.size()) break;
            sender.sendMessage(
                    Arrays.toString(new ComponentBuilder().append(TextComponent.fromLegacyText(Role.getPrefixed(friends.get(startIndex + i))))
                            .append(" está ").color(ChatColor.YELLOW)
                            .append(PlayerData.getServerDisplayName(friends.get(startIndex + i)))
                            .create())
            );
        }
        sender.sendMessage(Arrays.toString(new ComponentBuilder(new TextComponent("\n")).append(new TextComponent("Pagina " + page + "\\" + maxPages)).color(ChatColor.YELLOW).create()));
    }
}
