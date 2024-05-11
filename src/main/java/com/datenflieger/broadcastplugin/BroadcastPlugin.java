package com.datenflieger.broadcastplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BroadcastPlugin extends Plugin {

    private String prefix = "";

    @Override
    public void onEnable() {
        loadConfig();
        getProxy().getPluginManager().registerCommand(this, new InformationCommand());
    }

    public void broadcastMessage(String message) {
        message = translateColorCodes(message);

        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            for (ProxiedPlayer player : server.getPlayers()) {
                player.sendMessage("§f ");
                player.sendMessage(prefix + message);
                player.sendMessage("§f ");
            }
        }
    }

    private String translateColorCodes(String message) {
        // Farbcodes übersetzen
        return message.replaceAll("&", "§");
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            prefix = configuration.getString("prefix", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class InformationCommand extends Command {

        InformationCommand() {
            super("information", "broadcast.use");
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            if (args.length > 0) {
                String message = String.join(" ", args);
                broadcastMessage(message);
            } else {
                sender.sendMessage("§7Verwendung§8: §8/§ainformation <text>");
            }
        }
    }
}
