package me.loveplugin.managers;

import me.loveplugin.LovePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenderManager {

    private final LovePlugin plugin;
    private final File file;
    private YamlConfiguration data;
    private final Map<UUID, String> genders = new HashMap<>();

    public GenderManager(LovePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        ConfigurationSection sec = data.getConfigurationSection("genders");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                genders.put(UUID.fromString(key), sec.getString(key, "male").toLowerCase());
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public String get(UUID uuid) { return genders.get(uuid); }

    public boolean has(UUID uuid) { return genders.containsKey(uuid); }

    public void set(UUID uuid, String gender) {
        genders.put(uuid, gender.toLowerCase());
        data.set("genders." + uuid, gender.toLowerCase());
        save();
    }

    public void reload() {
        genders.clear();
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

    public String ruName(String gender) {
        if ("male".equals(gender)) return "мужской";
        if ("female".equals(gender)) return "женский";
        return "не выбран";
    }
}
