package ru.merkii.rduels.gui.click;

import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.menu.DuelChoiceKitMenu;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.gui.internal.click.AbstractClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

public class SelectCustomKitClickHandler implements ClickHandlerRegistry.ClickHandlerFacade {

    public static final String NAME = "SELECT_CUSTOM_KIT";

    @Override
    public void handle(InventoryContext context, Player player, AbstractClickHandler handler) {
        DuelRequest duelRequest = context.require("duel_request");
        duelRequest.setDuelKit(DuelKitType.CUSTOM);
        new DuelChoiceKitMenu().open(player, duelRequest, (Boolean) context.get("ffa").orElse(false));
    }
}
