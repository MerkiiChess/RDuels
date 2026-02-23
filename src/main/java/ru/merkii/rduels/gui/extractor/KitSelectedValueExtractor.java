package ru.merkii.rduels.gui.extractor;

import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.messages.MessagesMenuConfiguration;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractor;

public class KitSelectedValueExtractor implements ValueExtractor {

    private final MessagesMenuConfiguration config;
    private final CustomKitAPI customKitAPI;

    public KitSelectedValueExtractor(CustomKitAPI customKitAPI, MessagesMenuConfiguration config) {
        this.customKitAPI = customKitAPI;
        this.config = config;
    }

    @Override
    public String extract(InventoryContext context, String text, Object model) {
        if (!(model instanceof CustomKitModel kitModel)) {
            return text;
        }
        text = text.replace("%kit_display_name%", kitModel.getDisplayName());
        Player player = context.require("player");
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        boolean selected = customKitAPI.isSelectedKit(duelPlayer, kitModel.getDisplayName());
        text = text.replace("%is_selected%", config.sub("placeholder").sub("is-selected").plainMessage(selected ? "enabled" : "disabled"));

        return text;
    }

}
