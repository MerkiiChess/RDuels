package ru.merkii.rduels.gui.invui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.menu.settings.gui.GuiSettings;
import ru.merkii.rduels.config.menu.settings.gui.InventoryItem;
import ru.merkii.rduels.config.menu.settings.gui.InventorySettings;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractorRegistry;
import ru.merkii.rduels.gui.internal.paged.PageResolver;
import ru.merkii.rduels.gui.internal.paged.PageResolverRegistry;
import ru.merkii.rduels.gui.invui.wrapper.StructureWrapper;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseInventoryFactory implements InventoryGUIFactory {

    private final MenuConfiguration config;
    private final ValueExtractorRegistry extractorRegistry;
    private final PageResolverRegistry pageResolverRegistry;

    public BaseInventoryFactory(MenuConfiguration config,
                                ValueExtractorRegistry extractorRegistry,
                                PageResolverRegistry pageResolverRegistry) {
        this.config = config;
        this.extractorRegistry = extractorRegistry;
        this.pageResolverRegistry = pageResolverRegistry;
    }

    @Override
    public Optional<InventoryGUI> create(String guiName, Player player, InventoryContext context) {

        return config.settings().gui(guiName).map(guiSettings -> {
            GuiSettings transformedSettings = guiSettings.transform(extractorRegistry.asTransformer(context, null));

            InventorySettings invSettings = transformedSettings.inventory();
            context.overrideOrCreate("inventorySettings", invSettings);
            Structure structure = new Structure(invSettings.structureLines().toArray(new String[0]));
            context.overrideOrCreate("structureWrapper", new StructureWrapper(structure));

            Gui gui;

            Optional<PageResolver<?>> resolverOpt = pageResolverRegistry.pageResolver(invSettings.pageResolver());
            if (resolverOpt.isPresent()) {
                PageResolver<?> resolver = resolverOpt.get();

                List<Character> pageItemChars = guiSettings.inventory().pageItemChars();
                context.overrideOrCreate("pageItemChars", pageItemChars);

                List<InventoryItem> contentTemplates = new ArrayList<>();
                for (String row : invSettings.structureLines()) {
                    for (char ch : row.toCharArray()) {
                        if (ch == ' ') continue;
                        if (pageItemChars.contains(ch)) {
                            InventoryItem template = invSettings.mappingIngredients().get(ch);
                            contentTemplates.add(template);
                        }
                    }
                }

                PagedGui.Builder<Item> builder = PagedGui.items().setStructure(structure);

                invSettings.mappingIngredients().forEach((key, itemConfig) -> {
                    if (pageItemChars.contains(key)) {
                        builder.addIngredient(key, Markers.CONTENT_LIST_SLOT_HORIZONTAL);
                    } else {
                        builder.addIngredient(key, createItem(player, itemConfig, context, null));
                    }
                });

                List<?> content = resolver.resolve(player, context);
                for (int i = 0; i < content.size(); i++) {
                    Object model = content.get(i);
                    InventoryItem template = contentTemplates.get(i);
                    Item item = create(player, template, context, model);
                    builder.addContent(item);
                }

                gui = builder.build();
            } else {
                Gui.Builder.Normal builder = Gui.normal().setStructure(structure);

                invSettings.mappingIngredients().forEach((key, itemConfig) -> {
                    builder.addIngredient(key, createItem(player, itemConfig, context, null));
                });

                gui = builder.build();
            }

            context.overrideOrCreate("gui", gui);

            return new BaseInventoryGUI(player, transformedSettings, gui, context);
        });
    }

    static Item createItem(Player player, InventoryItem item, InventoryContext context, Object model) {
        return createItem(player, () -> item, context, model);
    }

    Item create(Player player, InventoryItem itemConfig, InventoryContext context, Object model) {
        return createItem(player, () -> itemConfig.transform(extractorRegistry.asTransformer(context, model)), context, model);
    }

    static Item createItem(Player player, java.util.function.Supplier<InventoryItem> supplier, InventoryContext context, Object model) {
        SimpleItemWrapper item = new SimpleItemWrapper(ignored -> {
            InventoryItem cfg = supplier.get();
            ItemStack stack = new ItemStack(cfg.bukkitMaterial());
            ItemMeta meta = stack.getItemMeta();

            if (meta != null) {
                cfg.name().ifPresent(name ->
                        InventoryAccess.getItemUtils().setDisplayName(meta, new AdventureComponentWrapper(name)));

                cfg.lore().ifPresent(lore ->
                        InventoryAccess.getItemUtils().setLore(meta, lore.stream()
                                .map(AdventureComponentWrapper::new)
                                .collect(Collectors.toUnmodifiableList())));

                stack.setItemMeta(meta);
            }
            return stack;
        });

        item.setClickHandler(click -> {
            InventoryItem cfg = supplier.get();
            InventoryContext clickContext = context.copy();
            String categoryId = cfg.root()
                    .node("category-id")
                    .getString();
            if (categoryId != null) {
                clickContext.overrideOrCreate("category-id", categoryId);
            }
            clickContext.overrideOrCreate("inventory_item", cfg);
            clickContext.overrideOrCreate("click", click);
            clickContext.overrideOrCreate("model", model);
            clickContext.extend("player", player);
            clickContext.overrideOrCreate("itemConfig", cfg);
            clickContext.overrideOrCreate("item", item);
            clickContext.overrideOrCreate("slot", click.getEvent().getRawSlot());

            cfg.createHandler(clickContext).handle();
        });

        return item;
    }
}
