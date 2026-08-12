package me.loveplugin.managers;

import me.loveplugin.LovePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarriageManager {

    private final LovePlugin plugin;
    private final File file;
    private YamlConfiguration data;
    private final Map<UUID, UUID> marriages = new HashMap<>();

    public MarriageManager(LovePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        ConfigurationSection sec = data.getConfigurationSection("marriages");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                marriages.put(UUID.fromString(key), UUID.fromString(sec.getString(key, "")));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public UUID getPartner(UUID uuid) { return marriages.get(uuid); }

    public boolean isMarried(UUID uuid) { return marriages.containsKey(uuid); }

    public void marry(UUID a, UUID b) {
        marriages.put(a, b);
        marriages.put(b, a);
        data.set("marriages." + a, b.toString());
        data.set("marriages." + b, a.toString());
        save();
    }

    public void divorce(UUID a) {
        UUID b = marriages.remove(a);
        if (b != null) marriages.remove(b);
        data.set("marriages." + a, null);
        data.set("marriages." + b, null);
        save();
    }

    public int resetAll() {
        int pairs = marriages.size() / 2;
        marriages.clear();
        data.set("marriages", null);
        save();
        return pairs;
    }

    public void reload() {
        marriages.clear();
        data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось сохранить data.yml: " + e.getMessage());
        }
    }
}
