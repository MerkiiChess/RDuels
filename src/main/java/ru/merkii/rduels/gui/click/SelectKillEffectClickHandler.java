package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.killeffect.config.KillEffectEntry;
import ru.merkii.rduels.core.killeffect.menu.KillEffectMenu;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.statistic.StatisticService;

/**
 * Selects (or, when re-clicked, clears) the kill effect of the clicked menu item and
 * re-opens the menu so the "selected" markers refresh.
 */
public class SelectKillEffectClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_KILL_EFFECT";

    private final StatisticService statisticService;
    private final MenuConfiguration config;

    public SelectKillEffectClickHandler(StatisticService statisticService, MenuConfiguration config) {
        this.statisticService = statisticService;
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        Object model = context.get("model").orElse(null);
        if (!(model instanceof KillEffectEntry entry)) {
            return;
        }
        String permission = entry.permission();
        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
            config.messages().sendTo(player, "no-permission");
            return;
        }
        String current = statisticService.getKillEffect(player.getUniqueId());
        // Clicking the active effect toggles it off.
        String next = entry.id().equalsIgnoreCase(current) ? "" : entry.id();
        statisticService.setKillEffect(player.getUniqueId(), next);
        config.settings().notification().playSound(player, "select-option");
        config.messages().sendTo(player, Placeholder.wrapped("%kill_effect_name%",
                next.isEmpty() ? config.messages().plainMessage("kill-effect-none") : entry.name()), "kill-effect-set");
        new KillEffectMenu().open(player);
    }
}
