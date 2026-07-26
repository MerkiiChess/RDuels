package ru.merkii.rduels.core.killeffect.effect;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

/**
 * Built-in kill effects. Each constant is self-contained and version-tolerant: particles
 * are resolved by name at runtime (see {@link #particle(String)}), so a constant that
 * names a particle missing on the running server simply does nothing instead of failing
 * to load. The registry exposes them by their lower-case {@link #id()}.
 */
public enum KillEffects implements KillEffect {

    LIGHTNING((killer, loc) -> loc.getWorld().strikeLightningEffect(loc)),
    EXPLOSION(particleBurst("EXPLOSION", 1, 0.0, Sound.ENTITY_GENERIC_EXPLODE)),
    FIREWORK((killer, loc) -> spawnFirework(loc)),
    FLAME(particleBurst("FLAME", 40, 0.4, Sound.ITEM_FIRECHARGE_USE)),
    HEARTS(particleBurst("HEART", 25, 0.5, Sound.ENTITY_VILLAGER_CELEBRATE)),
    BLOOD(dust(Color.fromRGB(140, 0, 0), 40, 0.4, Sound.ENTITY_PLAYER_HURT)),
    SOUL(particleBurst("SOUL", 30, 0.4, Sound.PARTICLE_SOUL_ESCAPE)),
    TORNADO(KillEffects::tornado),
    SNOWSTORM(particleBurst("SNOWFLAKE", 60, 0.6, Sound.BLOCK_SNOW_PLACE)),
    RAINBOW(KillEffects::rainbow),
    ENCHANT(particleBurst("ENCHANT", 60, 0.6, Sound.BLOCK_ENCHANTMENT_TABLE_USE)),
    WITHER(particleBurst("SMOKE", 40, 0.4, Sound.ENTITY_WITHER_SHOOT)),
    DRAGON_BREATH(particleBurst("DRAGON_BREATH", 40, 0.4, Sound.ENTITY_ENDER_DRAGON_GROWL)),
    PORTAL(particleBurst("PORTAL", 80, 0.8, Sound.BLOCK_PORTAL_TRIGGER)),
    SMOKE(particleBurst("LARGE_SMOKE", 40, 0.4, Sound.ENTITY_BLAZE_SHOOT)),
    TOTEM(particleBurst("TOTEM_OF_UNDYING", 60, 0.6, Sound.ITEM_TOTEM_USE)),
    SONIC_BOOM(particleBurst("SONIC_BOOM", 1, 0.0, Sound.ENTITY_WARDEN_SONIC_BOOM)),
    NOTES(particleBurst("NOTE", 30, 0.6, Sound.BLOCK_NOTE_BLOCK_HARP)),
    ANGRY_VILLAGER(particleBurst("ANGRY_VILLAGER", 20, 0.4, Sound.ENTITY_VILLAGER_NO)),
    HAPPY_VILLAGER(particleBurst("HAPPY_VILLAGER", 30, 0.5, Sound.ENTITY_VILLAGER_YES)),
    CRITICAL(particleBurst("CRIT", 40, 0.4, Sound.ENTITY_ARROW_HIT_PLAYER)),
    WITCH(particleBurst("WITCH", 40, 0.5, Sound.ENTITY_WITCH_CELEBRATE)),
    WATER_SPLASH(particleBurst("SPLASH", 60, 0.5, Sound.ENTITY_PLAYER_SPLASH)),
    BUBBLE(particleBurst("BUBBLE_POP", 40, 0.4, Sound.ENTITY_PLAYER_SWIM)),
    CLOUD(particleBurst("CLOUD", 40, 0.5, Sound.ENTITY_PHANTOM_FLAP)),
    SLIME(particleBurst("ITEM_SLIME", 40, 0.4, Sound.ENTITY_SLIME_SQUISH)),
    CHERRY(particleBurst("CHERRY_LEAVES", 60, 0.6, Sound.BLOCK_CHERRY_LEAVES_FALL)),
    ELECTRIC_SPARK(particleBurst("ELECTRIC_SPARK", 40, 0.4, Sound.ENTITY_LIGHTNING_BOLT_THUNDER)),
    INFERNO(particleBurst("FLAME", 80, 0.8, Sound.ENTITY_BLAZE_AMBIENT));

    private final BiConsumer<Player, Location> action;

    KillEffects(BiConsumer<Player, Location> action) {
        this.action = action;
    }

    @Override
    public String id() {
        return name().toLowerCase();
    }

    @Override
    public void play(Player killer, Player victim, Location location) {
        Location center = location.clone().add(0.0, 1.0, 0.0);
        action.accept(killer, center);
    }

    /** Spawns {@code count} of a named particle in a small cloud, then plays a sound. */
    private static BiConsumer<Player, Location> particleBurst(String particleName, int count, double spread, Sound sound) {
        return (killer, loc) -> {
            World world = loc.getWorld();
            if (world == null) {
                return;
            }
            Particle particle = particle(particleName);
            if (particle != null) {
                world.spawnParticle(particle, loc, count, spread, spread, spread, 0.0);
            }
            if (sound != null) {
                world.playSound(loc, sound, 1.0F, 1.0F);
            }
        };
    }

    private static BiConsumer<Player, Location> dust(Color color, int count, double spread, Sound sound) {
        return (killer, loc) -> {
            World world = loc.getWorld();
            if (world == null) {
                return;
            }
            Particle dust = particle("DUST");
            if (dust != null) {
                world.spawnParticle(dust, loc, count, spread, spread, spread, new Particle.DustOptions(color, 1.5F));
            }
            if (sound != null) {
                world.playSound(loc, sound, 1.0F, 1.0F);
            }
        };
    }

    private static void tornado(Player killer, Location loc) {
        World world = loc.getWorld();
        Particle cloud = particle("CLOUD");
        if (world == null || cloud == null) {
            return;
        }
        for (double y = 0; y < 3.0; y += 0.25) {
            double angle = y * Math.PI * 2;
            double radius = 0.6 + y * 0.2;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            world.spawnParticle(cloud, loc.clone().add(x, y, z), 3, 0.0, 0.0, 0.0, 0.0);
        }
        world.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 1.0F, 0.6F);
    }

    private static void rainbow(Player killer, Location loc) {
        World world = loc.getWorld();
        Particle dust = particle("DUST");
        if (world == null || dust == null) {
            return;
        }
        Color[] colors = {
                Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, Color.AQUA, Color.BLUE, Color.PURPLE
        };
        for (int i = 0; i < colors.length; i++) {
            double y = i * 0.3;
            world.spawnParticle(dust, loc.clone().add(0, y, 0), 15, 0.4, 0.1, 0.4,
                    new Particle.DustOptions(colors[i], 1.5F));
        }
        world.playSound(loc, Sound.ENTITY_VILLAGER_CELEBRATE, 1.0F, 1.2F);
    }

    private static void spawnFirework(Location loc) {
        World world = loc.getWorld();
        if (world == null) {
            return;
        }
        Firework firework = world.spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(randomColor(), randomColor())
                .withFade(randomColor())
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        // Detonate on the next tick so the burst is visible at the death location.
        firework.detonate();
    }

    private static Color randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /** Resolves a Particle constant by name, returning null when the server lacks it. */
    private static Particle particle(String name) {
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
