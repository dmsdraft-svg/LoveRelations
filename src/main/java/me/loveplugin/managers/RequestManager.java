package me.loveplugin.managers;

import me.loveplugin.LovePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager {

    private final LovePlugin plugin;
    private final Map<UUID, Pending> sexRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Pending> marryRequests = new ConcurrentHashMap<>();

    public RequestManager(LovePlugin plugin) {
        this.plugin = plugin;
    }

    public record Pending(UUID from, BukkitTask task) {}

    public void putSex(UUID target, UUID from, Runnable onExpire) {
        put(sexRequests, target, from, plugin.getConfig().getLong("settings.sex-request-timeout", 30), onExpire);
    }

    public void putMarry(UUID target, UUID from, Runnable onExpire) {
        put(marryRequests, target, from, plugin.getConfig().getLong("settings.marry-request-timeout", 30), onExpire);
    }

    public Pending takeSex(UUID target) { return take(sexRequests, target); }

    public Pending takeMarry(UUID target) { return take(marryRequests, target); }

    private void put(Map<UUID, Pending> map, UUID target, UUID from, long seconds, Runnable onExpire) {
        Pending old = map.remove(target);
        if (old != null && old.task() != null) old.task().cancel();
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (map.remove(target) != null) onExpire.run();
        }, 20L * seconds);
        map.put(target, new Pending(from, task));
    }

    private Pending take(Map<UUID, Pending> map, UUID target) {
        Pending p = map.remove(target);
        if (p != null && p.task() != null) p.task().cancel();
        return p;
    }
}
