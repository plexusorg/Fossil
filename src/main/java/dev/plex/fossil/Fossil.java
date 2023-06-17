package dev.plex.fossil;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class Fossil extends JavaPlugin {

    public static Fossil plugin;
    public static Server server;

    @Override
    public void onEnable() {
        plugin = this;
        server = plugin.getServer();
        getCommand("fossil").setExecutor(new FossilCommand());
        getCommand("fossil").setTabCompleter(new FossilCommand());
    }

    @Override
    public void onDisable() {
    }

    public void downloadFile(String url, File output, boolean verbose) throws java.lang.Exception
    {
        try
        {
            final var website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(output);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            fos.close();

            if (verbose)
            {
                getLogger().info("Downloaded " + url + " to " + output + ".");
            }
        }
        catch (FileNotFoundException ex)
        {
            getLogger().info(url + " does not exist.");
        }

    }

    public String getPlugin(String pluginName)
    {
        return "https://fossil.plex.us.org/" + getNMSVersion() + "/" + pluginName + ".jar";
        // Example: https://updater.telesphoreo.me/v1_14_R1/WorldEdit.jar
    }

    public String getNMSVersion()
    {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
}
