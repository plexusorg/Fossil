package dev.plex.fossil;

import java.util.logging.Logger;

public interface IFossil {
    Fossil plugin = Fossil.plugin;
    Logger logger = plugin.getLogger();
}
