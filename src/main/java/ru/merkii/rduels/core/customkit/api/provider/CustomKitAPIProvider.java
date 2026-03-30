package ru.merkii.rduels.core.customkit.api.provider;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CustomKitAPIProvider implements CustomKitAPI {

    CustomKitStorage kitStorage;

    @Override
    public String getSelectedKitDisplayName(DuelPlayer player) {
        return kitStorage.getSelectedKit(player.getUUID()).orElse("NULL");
    }

    @Override
    public boolean isSelectedKit(DuelPlayer player, String kitName) {
        return this.getSelectedKitDisplayName(player).equalsIgnoreCase(kitName);
    }

    @Override
    public void setKit(DuelPlayer player, String name) {
        kitStorage.setSelectedKit(player, name);
    }

    @Override
    public List<ItemStack> getItemsFromKit(DuelPlayer player, String kitName) {
        return new ArrayList<>(this.kitStorage.getAllItemsKit(player, kitName).values());
    }

    @Override
    public KitModel getKitModel(DuelPlayer player) {
        String selectedKit = kitStorage.getSelectedKit(player.getUUID()).orElse(null);
        if (selectedKit == null) {
            return null;
        }

        Map<Integer, ItemBuilder> map = new HashMap<>();
        this.kitStorage.getAllItemsKit(player, selectedKit)
                .forEach((key, value) -> map.put(key, ItemBuilder.builder().fromItemStack(value)));

        return map.isEmpty() ? null : KitModel.create(selectedKit, map);
    }
}
