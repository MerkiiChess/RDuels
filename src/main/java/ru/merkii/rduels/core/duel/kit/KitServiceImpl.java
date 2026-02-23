package ru.merkii.rduels.core.duel.kit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.ResourceConfiguration;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Singleton
public class KitServiceImpl implements KitService {

    private final ResourceConfiguration resourceConfiguration;
    private final KitConfiguration kitConfig;

    @Inject
    public KitServiceImpl(ResourceConfiguration resourceConfiguration, KitConfiguration kitConfiguration) {
        this.resourceConfiguration = resourceConfiguration;
        this.kitConfig = kitConfiguration;
    }

    @Override
    public KitModel getKitFromName(String kitName) {
        return this.kitConfig.kits()
                .keySet()
                .stream()
                .filter(model -> model.getDisplayName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveKitServer(Player player, String kitName) {
        PlayerInventory inventory = player.getInventory();
        Map<Integer, ItemBuilder> items = new HashMap<>();
        ItemStack mainHand = inventory.getItemInMainHand();
        Material displayMaterial = (mainHand != null && !mainHand.getType().isAir()) ? mainHand.getType() : Material.AIR;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().isAir()) continue;
            items.put(i, ItemBuilder.builder().fromItemStack(item));
            if (displayMaterial.isAir()) displayMaterial = item.getType();
        }

        if (displayMaterial.isAir()) displayMaterial = Material.PAPER;

        KitModel kitModel = KitModel.create(kitName, this.getFreeSlotKit(), new ArrayList<>(), displayMaterial, items);
        Map<KitModel, ItemBuilder> map = new HashMap<>(this.kitConfig.kits());
        map.put(kitModel, ItemBuilder.builder()
                .setMaterial(kitModel.getDisplayMaterial())
                .setDisplayName(kitModel.getDisplayName()));

        this.kitConfig.kits(map);
        try {
            this.resourceConfiguration.updateAndSave("kits.yml", KitConfiguration.class, this.kitConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isKitNameContains(String kitName) {
        return this.kitConfig.kits()
                .keySet()
                .stream()
                .anyMatch(kit -> kit.getDisplayName().equalsIgnoreCase(kitName));
    }

    @Override
    public int getFreeSlotKit() {
        Set<Integer> occupiedSlots = kitConfig.kits().values().stream()
                .map(ItemBuilder::getSlot)
                .collect(Collectors.toSet());

        int menuSize = 54;
        for (int i = 0; i < menuSize; i++) {
            if (!occupiedSlots.contains(i)) return i;
        }
        return -1;
    }

    @Override
    public KitModel getRandomKit() {
        Set<KitModel> kits = kitConfig.kits().keySet();
        return new ArrayList<>(kits).get(ThreadLocalRandom.current().nextInt(kits.size()));
    }
}
