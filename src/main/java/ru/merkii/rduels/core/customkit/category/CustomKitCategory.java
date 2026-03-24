package ru.merkii.rduels.core.customkit.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomKitCategory {

    String id;
    String displayName;
    Material displayMaterial;
    List<Material> items;

}
