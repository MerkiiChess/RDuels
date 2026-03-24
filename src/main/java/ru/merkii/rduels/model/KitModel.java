package ru.merkii.rduels.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KitModel {

    String displayName;
    int slot;
    List<String> lore;
    Material displayMaterial;
    Map<Integer, ItemBuilder> items;
    boolean bindingArena;
    List<String> arenasName;

    public static KitModel create(String displayName, int slot, List<String> lore, Material displayMaterial, Map<Integer, ItemBuilder> items) {
        return new KitModel(displayName, slot, lore, displayMaterial, items, false, new ArrayList<String>());
    }

    public static KitModel create(String displayName, Map<Integer, ItemBuilder> items) {
        return KitModel.create(displayName, 0, new ArrayList<String>(), Material.DIAMOND_CHESTPLATE, items);
    }

    public void giveItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (!this.items.containsKey(i)) continue;
            inventory.setItem(i, this.items.get(i).build());
        }
        player.updateInventory();
    }

    public void giveItemPlayers(Player ... players) {
        this.giveItemPlayers(Arrays.asList(players));
    }

    public void giveItemPlayers(List<Player> players) {
        players.forEach(this::giveItem);
    }

}
