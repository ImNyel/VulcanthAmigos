package com.vulcanth.nyel.commands.cmd.friendscmds;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.commands.SubCommand;
import com.vulcanth.nyel.misc.KotlinFeatures;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class fordev extends SubCommand {

    public fordev() {
        super("limpardbqwertyuiop", "limpardbqwertyuiop", "Limpa a db", false);
    }

    public fordev(String name, String usage, String description, boolean onlyForPlayer) {
        super(name, usage, description, onlyForPlayer);
    }

    @Override
    public void perform(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length == 0) {
            sender.sendMessage("§cUtilize /amigo " + this.getUsage());
            return;
        } else if (args.length > 1) {
            if (sender.hasPermission("role.master")) {
                sender.sendMessage("§cVocê não é um desenvolvedor para usar ferramentas de debug.");
            }
            KotlinFeatures.use(Main.getConnection().createStatement(), (it) -> {
                it.execute("delete from friends_table where nick = '" + sender.getName() + "'");
            });
        }
    }
}
