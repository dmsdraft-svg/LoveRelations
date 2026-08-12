package me.loveplugin.effects;

import me.loveplugin.LovePlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SprayEffect extends BukkitRunnable {

    private final Player player;
    private final World world;
    private final int duration;
    private int tick = 0;

    public SprayEffect(LovePlugin plugin, Player player) {
        this.player = player;
        this.world = player.getWorld();
        this.duration = plugin.getConfig().getInt("settings.spray-duration-ticks", 50);
    }

    @Override
    public void run() {
        if (tick >= duration || !player.isOnline()) {
            cancel();
            return;
        }

        Location eye = player.getEyeLocation().clone().subtract(0, 0.25, 0);
        Vector dir = eye.getDirection().normalize();

        for (int i = 0; i < 9; i++) {
            double speed = 0.22 + Math.random() * 0.28;
            Vector vel = dir.clone().multiply(speed);
            vel.add(new Vector((Math.random() - 0.5) * 0.06,
                    0.06 + Math.random() * 0.10,
                    (Math.random() - 0.5) * 0.06));

            Location loc = eye.clone();
            Vector v = vel.clone();
            for (int step = 0; step < 8; step++) {
                loc.add(v);
                v.subtract(new Vector(0, 0.035, 0));
                if (loc.getBlock().getType().isSolid()) {
                    world.spawnParticle(Particle.WHITE_ASH, loc.clone().add(0, 0.06, 0), 3, 0.08, 0.02, 0.08, 0.0);
                    break;
                }
                world.spawnParticle(Particle.WHITE_ASH, loc, 1, 0, 0, 0, 0);
            }
        }

        if (tick % 8 == 0) player.swingMainHand();

        tick++;
    }
}
