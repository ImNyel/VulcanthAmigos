package com.vulcanth.nyel.commands;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.commands.cmd.friendscmds.*;
import com.vulcanth.nyel.misc.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AmigoCommand extends Command {

    private final List<SubCommand> commands = new ArrayList<>();

    public AmigoCommand() {
        super("amigo");

        this.commands.add(new aceitar());
        this.commands.add(new add());
        this.commands.add(new assistir());
        this.commands.add(new fordev());
        this.commands.add(new listar());
        this.commands.add(new negar());
        this.commands.add(new pedidos());

    }

    public void perform(CommandSender sender, String label, String[] args) throws SQLException, PlayerData.NonExistentPlayerException {
        if (!sender.hasPermission("vulcanth.amigo")) {
            sender.sendMessage("§6VulcanthAmigos §7[" + Main.getInstance().getDescription().getVersion() + "] §f- §7Criado por §6kNyel§7.");
            return;
        }

        if (args.length == 0) {
            this.sendHelp(sender, 1);
            return;
        }

        try {
            this.sendHelp(sender, Integer.parseInt(args[0]));
        } catch (Exception ex) {
            SubCommand subCommand = this.commands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (subCommand == null) {
                this.sendHelp(sender, 1);
                return;
            }

            List<String> list = new ArrayList<>(Arrays.asList(args));
            list.remove(0);
            if (subCommand.onlyForPlayer()) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cEsse comando pode ser utilizado apenas pelos jogadores.");
                    return;
                }

                subCommand.perform((Player) sender, list.toArray(new String[list.size()]));
            } else {
                subCommand.perform(sender, list.toArray(new String[list.size()]));
            }
        }
    }

    private void sendHelp(CommandSender sender, int page) {
        List<SubCommand> commands = this.commands.stream().filter(subcommand -> sender instanceof Player || !subcommand.onlyForPlayer()).collect(Collectors.toList());
        Map<Integer, StringBuilder> pages = new HashMap<>();

        int pagesCount = (commands.size() + 6) / 7;
        for (int index = 0; index < commands.size(); index++) {
            int currentPage = (index + 7) / 7;
            if (!pages.containsKey(currentPage)) {
                pages.put(currentPage, new StringBuilder(" \n§eAjuda - " + currentPage + "/" + pagesCount + "\n \n"));
            }

            pages.get(currentPage).append("§6/amigo ").append(commands.get(index).getUsage()).append(" §f- §7").append(commands.get(index).getDescription()).append("\n");
        }

        StringBuilder sb = pages.get(page);
        if (sb == null) {
            sender.sendMessage("§cPágina não encontrada.");
            return;
        }

        sb.append(" ");
        sender.sendMessage(sb.toString());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}