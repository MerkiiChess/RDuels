package ru.merkii.rduels.core.duel.kit;

import org.bukkit.entity.Player;
import ru.merkii.rduels.model.KitModel;

public interface KitService {

    KitModel getKitFromName(String kitName);
    void saveKitServer(Player player, String kitName);
    boolean isKitNameContains(String kitName);
    int getFreeSlotKit();
    KitModel getRandomKit();

}
