package ru.merkii.rduels.gui.internal.click;

import ru.merkii.rduels.gui.internal.context.InventoryContext;

public interface ClickHandlerFactory {

    ClickHandler create(InventoryContext context);

}
