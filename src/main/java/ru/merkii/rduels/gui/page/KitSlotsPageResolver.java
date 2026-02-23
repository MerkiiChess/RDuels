package ru.merkii.rduels.gui.page;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.config.menu.settings.gui.InventorySettings;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.model.SlotModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitSlotsPageResolver implements PageResolver<SlotModel> {

    private final CustomKitStorage customKitStorage;

    public KitSlotsPageResolver(CustomKitStorage customKitStorage) {
        this.customKitStorage = customKitStorage;
    }

    @Override
    public List<SlotModel> resolve(Player player, InventoryContext context) {
        String kitName = context.require("kit_name");
        InventorySettings settings = context.require("inventorySettings");

        @SuppressWarnings("unchecked")
        List<Character> pageItemChars = context.require("pageItemChars");

        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        Map<Integer, ItemStack> storedItems = customKitStorage.getAllItemsKit(duelPlayer, kitName);

        List<SlotModel> result = new ArrayList<>();
        int autoSlotCounter = 0;

        for (String row : settings.structure()) {
            for (char ch : row.toCharArray()) {
                if (ch == ' ') continue;
                if (!settings.mappingIngredients().containsKey(ch)) continue;
                if (!pageItemChars.contains(ch)) continue;

                InventoryItem itemConfig = settings.mappingIngredients().get(ch);
                ConfigurationNode node = itemConfig.root();

                int slotId = node.node("slot-id").getInt(-1);
                if (slotId == -1) {
                    slotId = autoSlotCounter++;
                }

                String defaultMat = node.node("default-material").getString("BARRIER");
                ItemStack item = storedItems.getOrDefault(slotId,
                        new ItemStack(Material.valueOf(defaultMat)));

                String categoryId = node.node("category-id").getString("ALL");
                String displayName = node.node("name").getString("Слот " + slotId);

                result.add(new SlotModel(slotId, item, displayName, categoryId));
            }
        }

        return result;
    }


}
