package ru.merkii.rduels.gui.provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.menu.settings.gui.*;
import ru.merkii.rduels.gui.internal.GUIItem;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractorRegistry;
import ru.merkii.rduels.gui.internal.paged.PageResolverRegistry;
import ru.merkii.rduels.model.DynamicSlot;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class BaseInventoryFactory implements InventoryGUIFactory {

    MenuConfiguration config;
    ValueExtractorRegistry extractorRegistry;
    PageResolverRegistry pageResolverRegistry;

    @Override
    public Optional<InventoryGUI> create(String guiName, Player player, InventoryContext context) {
        return config.settings().gui(guiName).map(guiSettings -> {
            GuiSettings transformedSettings = guiSettings.transform(extractorRegistry.asTransformer(context, null));
            InventorySettings invSettings = transformedSettings.inventory();
            context.overrideOrCreate("inventorySettings", invSettings);
            int size = invSettings.structureLines().size() * 9;
            BaseInventoryGUI gui = new BaseInventoryGUI(player, transformedSettings, size, context);
            context.overrideOrCreate("gui", gui);
            Map<Character, InventoryItem> ingredients = invSettings.mappingIngredients();
            List<String> structure = invSettings.structureLines();
            List<Character> pageChars = invSettings.pageItemChars();
            List<DynamicSlot> dynamicSlots = new ArrayList<>();
            context.overrideOrCreate("pageItemChars", pageChars);
            context.overrideOrCreate("player", player);
            for (int row = 0; row < structure.size(); row++) {
                String line = structure.get(row);
                for (int col = 0; col < Math.min(line.length(), 9); col++) {
                    char symbol = line.charAt(col);
                    int slotIndex = row * 9 + col;
                    if (symbol == ' ') continue;

                    if (pageChars.contains(symbol)) {
                        dynamicSlots.add(new DynamicSlot(slotIndex, symbol));
                        continue;
                    }

                    InventoryItem itemCfg = ingredients.get(symbol);
                    if (itemCfg != null) {
                        gui.setItem(slotIndex, createGUIItem(player, itemCfg, context, null));
                    }
                }
            }
            pageResolverRegistry.pageResolver(invSettings.pageResolver()).ifPresent(resolver -> {
                List<?> models = resolver.resolve(player, context);
                for (int i = 0; i < Math.min(models.size(), dynamicSlots.size()); i++) {
                    Object model = models.get(i);
                    DynamicSlot slotInfo = dynamicSlots.get(i);
                    InventoryItem template = ingredients.get(slotInfo.symbol());
                    if (template != null) {
                        gui.setItem(slotInfo.index(), createGUIItem(player, template, context, model));
                    }
                }
            });

            return gui;
        });
    }

    @Override
    public ItemStack buildItemStack(Player player, InventoryItem itemConfig, InventoryContext context, Object model) {
        InventoryItem transformed = itemConfig.transform(extractorRegistry.asTransformer(context, model));
        ItemStack stack = new ItemStack(transformed.bukkitMaterial());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            transformed.name().ifPresent(meta::displayName);
            transformed.lore().ifPresent(meta::lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private GUIItem createGUIItem(Player player, InventoryItem itemConfig, InventoryContext context, Object model) {
        ItemStack stack = buildItemStack(player, itemConfig, context, model);
        return new GUIItem(stack, itemConfig, model);
    }
}