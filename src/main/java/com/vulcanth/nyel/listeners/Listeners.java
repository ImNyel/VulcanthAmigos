package com.vulcanth.nyel.listeners;

import com.vulcanth.nyel.Main;
import com.vulcanth.nyel.listeners.event.EventListener;
import com.vulcanth.nyel.listeners.event.LoginLogoutEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Listeners {

    public static void setupListeners() {
        try {
            PluginManager pm = Bukkit.getPluginManager();

            pm.getClass().getDeclaredMethod("registerEvents", Listener.class, Plugin.class)
                    .invoke(pm, new EventListener(), Main.getInstance());
            pm.getClass().getDeclaredMethod("registerEvents", Listener.class, Plugin.class)
                    .invoke(pm, new LoginLogoutEventListener(), Main.getInstance());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
