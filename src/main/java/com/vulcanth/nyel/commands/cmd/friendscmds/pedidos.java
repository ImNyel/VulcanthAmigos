package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class pedidos extends SubCommand {

    public pedidos() {
        super("pedidos", "pedidos", "Veja seus pedidos de amizade", false);
    }

    public pedidos(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws PlayerData.NonExistentPlayerException, SQLException {
        PlayerData playerData = new PlayerData(sender.getName(), true);
        int page = 0;
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
        List<String> friendRequests = playerData.getFriendRequests();
        int startIndex = page * 15;
        int maxPages = ((int) (friendRequests.size() / 15f));

        if (page > maxPages) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§cIsso não é um numero valido! escolha um numero entre 0-" + maxPages + 1)));
            return;
        }

        sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("§eSeus pedidos de amizade:")));
        for (int i = 0; i < 15; i++) {
            if (startIndex + i + 1 > friendRequests.size()) break;
            sender.sendMessage(
                    Arrays.toString(new ComponentBuilder(friendRequests.get(startIndex + i) + ":").color(ChatColor.GRAY)
                            .append(" clique").color(ChatColor.YELLOW)
                            .append(" AQUI").color(ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Clique aqui para aceitar"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigo aceitar " + friendRequests.get(startIndex + i)))
                            .append(" para aceitar").color(ChatColor.YELLOW)
                            .append(" ou")
                            .append(" AQUI").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "Clique aqui para negar"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigo negar " + friendRequests.get(startIndex + i)))
                            .append(" para negar").color(ChatColor.YELLOW)
                            .create())
            );
        }
        sender.sendMessage(Arrays.toString(new ComponentBuilder(new TextComponent("\n")).append(new TextComponent("Pagina " + page + "\\" + maxPages)).color(ChatColor.YELLOW).create()));
    }
}