package ru.merkii.rduels.core.customkit.menu;

import org.bukkit.entity.Player;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.config.CustomKitConfig;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.menu.event.ClickEvent;
import ru.merkii.rduels.util.ColorUtil;

public class CustomKitCreateMenu extends VMenu {

    private final CustomKitConfig customKitConfig = CustomKitCore.INSTANCE.getCustomKitConfig();
    private final CustomKitAPI customKitAPI = CustomKitCore.INSTANCE.getCustomKitAPI();

    public CustomKitCreateMenu(Player player) {
        super(CustomKitCore.INSTANCE.getCustomKitConfig().getSize(), ColorUtil.color(CustomKitCore.INSTANCE.getCustomKitConfig().getTitleCreate()));
        this.customKitConfig.getCreateMenu().getKits().forEach(kits -> {
            if (kits.getPermission() != null && !player.hasPermission(kits.getPermission())) {
                if (kits.isInvisible()) {
                    return;
                }
                setItem(kits.getSlot(), customKitConfig.getCreateMenu().getNoPermissionItem());
            } else if (!this.customKitAPI.isSelectedKit(player, kits.getDisplayName())) {
                setItem(kits.getSlot(), customKitConfig.getCreateMenu().getNotSelected());
            } else {
                setItem(kits.getSlot(), customKitConfig.getCreateMenu().getSelected());
            }
            setItem(kits.getSlot() - 9, customKitConfig.getCreateMenu().getEditItem().clone().replaceDisplayName("(kit)", kits.getDisplayName()));
        });
    }

    @Override
    public void onClick(ClickEvent event) {
        Player player = event.getPlayer();
        if (event.getItemBuilder().getMaterial() == this.customKitConfig.getCreateMenu().getEditItem().getMaterial()) {
            new CustomKitEditMenu(customKitAPI.getNameKitSlot(event.getSlot() + 9), player).open(player);
            return;
        }
        if (event.getItemBuilder().equals(this.customKitConfig.getCreateMenu().getNotSelected())) {
            this.customKitAPI.setKit(player, customKitAPI.getNameKitSlot(event.getSlot()));
            new CustomKitCreateMenu(player).open(player);
            return;
        }
    }
}
