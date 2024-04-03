package ru.merkii.rduels.core.customkit.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import ru.merkii.rduels.builder.ItemBuilder;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CustomKitCategory {

    private ItemBuilder item;
    private Map<Integer, Material> items;

}
