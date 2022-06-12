package com.vulcanth.nyel.commands.cmd.partycmds;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.helpers.Party;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class chat extends Command {
    public chat() {
        super("p");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        {
            try (Statement statement = Main.getConnection().createStatement()) {
                Party party = Party.getByMember(sender.getName());
                if (party == null) {
                    sender.sendMessage(Arrays.toString(TextComponent.fromLegacyText("Você não pertence a uma party!")));
                }
                party.handleSendMessage((ProxiedPlayer) sender, String.join(" ", args));
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return false;
    }
}