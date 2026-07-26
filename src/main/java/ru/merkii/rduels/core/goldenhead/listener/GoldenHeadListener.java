package ru.merkii.rduels.core.goldenhead.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.goldenhead.config.GoldenHeadConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhances a configured "golden head" item: when consumed, applies extra potion
 * effects on top of the item's vanilla behaviour. The item is identified by material
 * and (optionally) display name, so it can simply be placed into any kit.
 */
@Singleton
public class GoldenHeadListener implements Listener {

    private final GoldenHeadConfiguration config;
    private final RDuels plugin;

    @Inject
    public GoldenHeadListener(GoldenHeadConfiguration config, RDuels plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!config.enabled() || !isGoldenHead(event.getItem())) {
            return;
        }
        Player player = event.getPlayer();
        List<PotionEffect> effects = parseEffects();
        // Apply next tick so the vanilla consume effects are already in place.
        plugin.getServer().getScheduler().runTask(plugin, () -> effects.forEach(player::addPotionEffect));
    }

    private boolean isGoldenHead(ItemStack item) {
        if (item == null) {
            return false;
        }
        Material material = resolveMaterial();
        if (material == null || item.getType() != material) {
            return false;
        }
        String configuredName = config.displayName();
        if (configuredName == null || configuredName.isEmpty()) {
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        Component displayName = meta.displayName();
        if (displayName == null) {
            return false;
        }
        String actual = PlainTextComponentSerializer.plainText().serialize(displayName);
        String expected = PlainTextComponentSerializer.plainText()
                .serialize(MiniMessage.miniMessage().deserialize(configuredName));
        return actual.equals(expected);
    }

    private Material resolveMaterial() {
        return Material.matchMaterial(config.material() == null ? "" : config.material());
    }

    private List<PotionEffect> parseEffects() {
        List<PotionEffect> effects = new ArrayList<>();
        for (String raw : config.effects()) {
            PotionEffect effect = parseEffect(raw);
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }

    private PotionEffect parseEffect(String raw) {
        if (raw == null) {
            return null;
        }
        String[] parts = raw.split(",");
        if (parts.length < 3) {
            return null;
        }
        PotionEffectType type = PotionEffectType.getByName(parts[0].trim().toUpperCase());
        if (type == null) {
            return null;
        }
        try {
            int durationTicks = Integer.parseInt(parts[1].trim()) * 20;
            int amplifier = Integer.parseInt(parts[2].trim());
            return new PotionEffect(type, durationTicks, amplifier);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

}
