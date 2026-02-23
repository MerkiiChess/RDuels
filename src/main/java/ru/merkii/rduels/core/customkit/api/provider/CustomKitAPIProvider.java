package ru.merkii.rduels.core.customkit.api.provider;

import jakarta.inject.Singleton;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.model.KitModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class CustomKitAPIProvider implements CustomKitAPI {

    private final CustomKitStorage kitStorage;

    public CustomKitAPIProvider(CustomKitStorage customKitStorage) {
        this.kitStorage = customKitStorage;
    }

    @Override
    public String getSelectedKitDisplayName(DuelPlayer player) {
        FileConfiguration config = this.kitStorage.getConfig(player);
        if (config.getString("selectedKit") == null) {
            config.set("selectedKit", "NULL");
            this.kitStorage.save(config, player);
            return "NULL";
        }
        return config.getString("selectedKit");
    }

    @Override
    public boolean isSelectedKit(DuelPlayer player, String kitName) {
        return this.getSelectedKitDisplayName(player).equalsIgnoreCase(kitName);
    }

    @Override
    public void setKit(DuelPlayer player, String name) {
        FileConfiguration config = this.kitStorage.getConfig(player);
        config.set("selectedKit", name);
        this.kitStorage.save(config, player);
    }

    @Override
    public List<ItemStack> getItemsFromKit(DuelPlayer player, String kitName) {
        return new ArrayList<>(this.kitStorage.getAllItemsKit(player, kitName).values());
    }

    @Override
    public KitModel getKitModel(DuelPlayer player) {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        this.kitStorage.getAllItemsKit(player, this.getSelectedKitDisplayName(player)).forEach((key, value) -> map.put(key, ItemBuilder.builder().fromItemStack(value)));
        return KitModel.create(this.getSelectedKitDisplayName(player), map);
    }
}
