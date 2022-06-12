package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.helpers.Party;
import com.vulcanth.nyel.listeners.event.EventListener;
import com.vulcanth.nyel.listeners.more.ExpireTask;
import dev.vulcanth.pewd.player.role.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class convidar extends SubCommand {

    String nick;

    public convidar() {
        super("convidar", "convidar", "Convidar um jogador para a party", false);
    }

    public convidar(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /party " + this.getUsage());
            return;
        } else if (args.length > 1) {
            boolean isANick = true;
            if (args.length == 2) {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você precisa escrever quem você quer convidar!")));
                return;
            }
            nick = args[2];

        }
        if (ProxyServer.getInstance().getPlayer(nick) == null) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador não existe!")));
            return;
        }
        nick = ProxyServer.getInstance().getPlayer(nick).getName();
        if (nick.equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você não pode enviar um pedido de party para você próprio.")));
            return;
        }


        {

            {
                if (Party.getByMember(nick) != null) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Esse jogador já pertence a uma party!")));
                    return;
                }

            }

            if (ProxyServer.getInstance().getPlayer(nick) != null) {


                if (Main.getPD(nick).getInvites().contains(sender.getName())) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Você já convidou este jogador!")));
                    return;
                }
            } else {
                sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText(ChatColor.RED + "Este jogador não está online!")));
                return;
            }
            Main.getPD(nick).getInvites().add(sender.getName());


            ProxyServer.getInstance().getPlayer(nick).sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"" + Role.getPrefixed(getName()) + "\",\"color\":\"dark_gray\"},{\"text\":\" convidou você para a Party dele!\\nClique \",\"color\":\"yellow\"},{\"text\":\"AQUI\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party aceitar " + sender.getName() + "\"}},{\"text\":\" para aceitar ou\",\"color\":\"yellow\"},{\"text\":\" \"},{\"text\":\"AQUI\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party negar " + sender.getName() + "\"}},{\"text\":\" para negar.\",\"color\":\"yellow\"}]"));

            Party party = Party.getOrNew(sender.getName());

            String finalNick = nick;
            party.getPlayers().forEach((player, role) -> {

                if (ProxyServer.getInstance()
                        .getPlayer(player) == null) return;
                ProxyServer.getInstance()
                        .getPlayer(player)
                        .sendMessage(ComponentSerializer.parse("[\"\",{\"text\":\"" + Role.getPrefixed(getName()) + "\",\"color\":\"dark_gray\"},{\"text\":\" foi convidado(a) para a sua party!\\nEle(a) tem \",\"color\":\"yellow\"},{\"text\":\"5\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\" minutos\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\" para \",\"color\":\"yellow\"},{\"text\":\"aceitar \",\"color\":\"dark_green\"},{\"text\":\"ou \",\"color\":\"yellow\"},{\"text\":\"negar\",\"color\":\"red\"},{\"text\":\".\",\"color\":\"yellow\"}]"));
            });
            ProxyServer.getInstance().getScheduler().schedule(new Plugin(), new ExpireTask(sender.getName(), nick), 5, TimeUnit.MINUTES);
            }
        }
    }