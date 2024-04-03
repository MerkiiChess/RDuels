package ru.merkii.rduels.core.customkit.api.provider;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.model.KitModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomKitAPIProvider implements CustomKitAPI {

    private final CustomKitStorage kitStorage = CustomKitCore.INSTANCE.getCustomKitStorage();

    @Override
    public String getSelectedKitDisplayName(Player player) {
        FileConfiguration config = this.kitStorage.getConfig(player);
        if (config.getString("selectedKit") == null) {
            config.set("selectedKit", "NULL");
            this.kitStorage.save(config, player);
            return "NULL";
        }
        return config.getString("selectedKit");
    }

    @Override
    public String getNameKitSlot(int slot) {
        return CustomKitCore.INSTANCE.getCustomKitConfig().getCreateMenu().getKits().stream().filter(kitModel -> kitModel.getSlot() == slot).map(CustomKitModel::getDisplayName).findFirst().orElse(null);
    }

    @Override
    public boolean isSelectedKit(Player player, String kitName) {
        return this.getSelectedKitDisplayName(player).equalsIgnoreCase(kitName);
    }

    @Override
    public void setKit(Player player, String name) {
        FileConfiguration config = this.kitStorage.getConfig(player);
        config.set("selectedKit", name);
        this.kitStorage.save(config, player);
    }

    @Override
    public List<ItemStack> getItemsFromKit(Player player, String kitName) {
        return new ArrayList<>(this.kitStorage.getAllItemsKit(player, kitName).values());
    }

    @Override
    public KitModel getKitModel(Player player) {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        this.kitStorage.getAllItemsKit(player, this.getSelectedKitDisplayName(player)).forEach((key, value) -> map.put(key, ItemBuilder.builder().fromItemStack(value)));
        return KitModel.create(this.getSelectedKitDisplayName(player), map);
    }
}
