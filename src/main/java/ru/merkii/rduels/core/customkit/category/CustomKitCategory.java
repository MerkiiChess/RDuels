package ru.merkii.rduels.core.customkit.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomKitCategory {

    private String id;
    private InventoryItem item;
    private List<InventoryItem> items;

}
