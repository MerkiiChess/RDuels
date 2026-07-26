package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.randomkit.RandomKitService;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.model.KitModel;

/** Picks a random server kit (respecting random-kits.yml) and marks it on the request. */
public class SelectRandomKitClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_RANDOM_KIT";

    private final RandomKitService randomKitService;
    private final MenuConfiguration config;

    public SelectRandomKitClickHandler(RandomKitService randomKitService, MenuConfiguration config) {
        this.randomKitService = randomKitService;
        this.config = config;
    }

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        DuelRequest duelRequest = context.require("duel_request");
        KitModel kit = randomKitService.pickRandom();
        if (kit == null) {
            config.messages().sendTo(player, "no-item");
            return;
        }
        duelRequest.setDuelKit(DuelKitType.SERVER);
        duelRequest.setKitModel(kit);
        config.settings().notification().playSound(player, "select-option");
        config.messages().sendTo(player, Placeholder.wrapped("%option_name%", kit.getDisplayName()), "option-selected");
        new DuelChoiceKitMenu().open(player, duelRequest, (Boolean) context.get("ffa").orElse(false));
    }
}
