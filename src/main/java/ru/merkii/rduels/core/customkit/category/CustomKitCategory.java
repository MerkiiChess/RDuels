package ru.merkii.rduels.core.customkit.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import java.util.Map;

@AllArgsConstructor
public class CustomKitCategory {

    private ItemBuilder item;
    private Map<Integer, ItemBuilder> items;

    public ItemBuilder getItem() {
        return item;
    }

    public Map<Integer, ItemBuilder> getItems() {
        return items;
    }
}
