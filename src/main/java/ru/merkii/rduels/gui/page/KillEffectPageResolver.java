package ru.merkii.rduels.gui.page;

import org.bukkit.entity.Player;
import ru.merkii.rduels.core.killeffect.config.KillEffectConfiguration;
import ru.merkii.rduels.core.killeffect.config.KillEffectEntry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;

import java.util.List;

/** Lists the configured kill effects for the /killeffect menu. */
public class KillEffectPageResolver implements PageResolver<KillEffectEntry> {

    private final KillEffectConfiguration config;

    public KillEffectPageResolver(KillEffectConfiguration config) {
        this.config = config;
    }

    @Override
    public List<KillEffectEntry> resolve(Player player, InventoryContext context) {
        return config.effects();
    }
}
