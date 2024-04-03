package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomKitItemAmountMenu extends VMenu {

    private final int itemSlot;
    private final CustomKitStorage customKitStorage;
    private final String kitName;

    public CustomKitItemAmountMenu(String kitName, int slot, Material material, Collection<ItemBuilder> items) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getCategoriesMenu().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleEdit().replace("(kit)", kitName)));
        this.itemSlot = slot;
        this.customKitStorage = CustomKitCore.INSTANCE.getCustomKitStorage();
        this.kitName = kitName;
        List<ItemBuilder> itemBuilders = items.stream().map(item -> item.setMaterial(material)).collect(Collectors.toList());
        for (ItemBuilder itemBuilder : itemBuilders) {
            if (itemBuilder.getMaterial().name().contains("POTION")) {
                setItem(itemBuilder);
            } else if (itemBuilder.getMaterial().getMaxStackSize() >= itemBuilder.getAmount()) {
                setItem(itemBuilder);
            }
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        ItemStack itemStack = this.customKitStorage.getItemFromSlot(this.itemSlot, this.kitName, event.getPlayer());
        itemStack.setAmount(CustomKitCore.INSTANCE.getCustomKitConfig().getCategoriesMenu().getItemStacks().get(event.getItemBuilder()));
        this.customKitStorage.setItemSlot(itemStack, this.kitName, this.itemSlot, event.getPlayer());
        new CustomKitEditMenu(this.kitName, event.getPlayer()).open(event.getPlayer());
    }
}
