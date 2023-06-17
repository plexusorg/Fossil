package dev.plex.fossil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FossilCommand implements CommandExecutor, TabCompleter, IFossil {
    private List<String> FILES = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("This server is running Fossil to keep plugins in sync."));
            sender.sendMessage(Component.text("Source code available at: https://github.com/plexusorg/Fossil"));
            return true;
        }

        if (args[0].equalsIgnoreCase("update")) {
            if (!sender.hasPermission("fossil.update")) {
                sender.sendMessage(Component.text("You do not have permission to use this command!").color(NamedTextColor.RED));
                return true;
            }
            sender.sendMessage(Component.text("Updating server plugins").color(NamedTextColor.GRAY));

            for (String file : plugin.getConfig().getStringList("plugins")) {
                FILES.add(plugin.getPlugin(file));
            }

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
        } else {
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("fossil.update")) {
            return Collections.singletonList("update");
        }
        return Collections.emptyList();
    }
}