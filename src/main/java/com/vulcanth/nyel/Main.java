package com.vulcanth.nyel;

import com.vulcanth.nyel.commands.Commands;
import com.vulcanth.nyel.helpers.PlayerDataParty;
import com.vulcanth.nyel.listeners.Listeners;
import com.vulcanth.nyel.listeners.event.LoginLogoutEventListener;
import dev.vulcanth.pewd.plugin.VulcanthPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream;


public class Main extends VulcanthPlugin {
    //Functions for friend, do delete
    private Configuration friendsConfigYml;
    private ProxyServer proxy;

    public Configuration getFriendsConfig() {
        return friendsConfigYml;
    }

    //Functions for party, do delete
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    String host, port, database, username, password;
    static Connection connection;
    public Configuration configYml;
    private static final List<PlayerDataParty> pdatas = Collections.synchronizedList(new ArrayList<>());

    public static PlayerDataParty getPD(String player) {
        Optional<PlayerDataParty> pd = pdatas.stream().filter(pld -> pld.getNick().equals(player)).findAny();
        return pd.orElse(null);
    }

    public static List<PlayerDataParty> getPdatas() {
        return pdatas;
    }

    public static Connection getConnection() {
        return connection;
    }

    private void loadConfig() throws IOException {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configYml = ConfigurationProvider.getProvider(YamlConfiguration.class)
                .load(new File(getDataFolder(), "config.yml"));
    }

    @Override
    public void start() {
        instance = this;
    }

    @Override
    public void load() {

    }

    @Override
    public void enable() {
        Commands.setupCommands();
        Listeners.setupListeners();

        this.getProxy().getPluginManager().registerListener(new Plugin(), new LoginLogoutEventListener());

        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        host = this.configYml.getString("mysql.host_ip");
        if (this.configYml.getString("mysql.host_ip").split(":").length == 1)
            port = "3306";
        else
            port = this.configYml.getString("mysql.host_ip").split(":")[1];
        database = this.configYml.getString("mysql.database");
        username = this.configYml.getString("mysql.username");
        password = this.configYml.getString("mysql.password");

        try {
            openConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            instance = this;
            statement.addBatch("Create Table If NOT Exists general(" +
                    "   isPublic tinyint(1) Not null," +
                    "   owner VARCHAR(16) Not null," +
                    "   players JSON Not null," +
                    "   Primary key (owner)" +
                    //friends
                    "   nick varchar(16) not null primary key," +
                    "   friends JSON not null," +
                    "   friend_requests JSON not null" +
                    ");");
            statement.addBatch("delete from general");
            statement.executeBatch();
        } catch (SQLException ignored) {
        }
        for (ProxiedPlayer p : getProxy().getPlayers()) {
            Main.getPdatas().add(PlayerDataParty.of(p.getName()));
        }
        System.out.println("Plugin iniciado com sucesso!");
    }


    @Override
    public void disable() {

    }

    public void openConnection() throws SQLException,
            ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                        + this.host + ":" + this.port + "/" + this.database,
                this.username, this.password);
    }

    public ProxyServer getProxy() {
        return null;
    }

    public void setProxy(ProxyServer proxy) {
        this.proxy = proxy;
    }

}

