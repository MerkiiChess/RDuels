package ru.merkii.rduels.gui.extractor;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.killeffect.config.KillEffectEntry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;
import ru.merkii.rduels.statistic.StatisticService;

import java.util.Optional;

/** Resolves %kill_effect_*% placeholders for KillEffectEntry page items. */
public class KillEffectValueExtractor implements ValueExtractor {

    private final StatisticService statisticService;
    private final MenuConfiguration config;

    public KillEffectValueExtractor(StatisticService statisticService, MenuConfiguration config) {
        this.statisticService = statisticService;
        this.config = config;
    }

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof KillEffectEntry entry)) {
            return text;
        }
        String selected = "";
        Optional<Player> player = context.get("player");
        if (player.isPresent()) {
            String current = statisticService.getKillEffect(player.get().getUniqueId());
            boolean isSelected = entry.id().equalsIgnoreCase(current);
            selected = config.messages().plainMessage(isSelected ? "kill-effect-selected" : "kill-effect-not-selected");
        }
        return text
                .replace("%kill_effect_name%", entry.name())
                .replace("%kill_effect_material%", entry.material())
                .replace("%kill_effect_selected%", selected);
    }
}
