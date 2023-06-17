package dev.plex.fossil;

import java.io.File;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class FossilCommand implements CommandExecutor, IFossil {
    private final List<String> FILES = plugin.getConfig().getStringList("plugins");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("This server is running Fossil to keep plugins in sync."));
            sender.sendMessage(Component.text("Source code available at https://github.com/plexusorg/Fossil"));
            return true;
        }

        if (args.length > 1) {
            if (!sender.hasPermission("fossil.update")) {
                sender.sendMessage(Component.text("You do not have permission to use this command!").color(NamedTextColor.RED));
                return true;
            }
            sender.sendMessage(Component.text("Updating server plugins").color(NamedTextColor.GRAY));

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        for (final String url : FILES) {
                            logger.info("Downloading: " + url);

                            File file = new File("./plugins/update/" + url.substring(url.lastIndexOf("/") + 1));
                            if (file.exists()) {
                                file.delete();
                            }
                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }

                            plugin.downloadFile(url, file, true);
                        }
                        sender.sendMessage(Component.text("Successfully updated. Restart the server to apply changes.").color(NamedTextColor.GRAY));
                    } catch (Exception ex) {
                        logger.severe(ex.toString());
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
        return true;
    }
}