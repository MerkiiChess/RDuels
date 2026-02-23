package ru.merkii.rduels.core.customkit.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.model.KitModel;

import java.util.List;

public interface CustomKitAPI {

    /**
     * Retrieves the display name of the selected kit for the specified player.
     *
     * @param player The player whose selected kit display name to retrieve.
     * @return The display name of the selected kit, or "NULL" if no kit is selected.
     */
    String getSelectedKitDisplayName(DuelPlayer player);

    /**
     * Checks if the specified kit is selected by the player.
     *
     * @param player  The player.
     * @param kitName The name of the kit to check.
     * @return True if the specified kit is selected by the player, otherwise false.
     */
    boolean isSelectedKit(DuelPlayer player, String kitName);

    /**
     * Sets the selected kit for the specified player.
     *
     * @param player The player.
     * @param name   The name of the kit to set as selected.
     */
    void setKit(DuelPlayer player, String name);

    /**
     * Retrieves the items from the specified kit for the player.
     *
     * @param player  The player.
     * @param kitName The name of the kit.
     * @return A list containing the items from the specified kit for the player.
     */
    List<ItemStack> getItemsFromKit(DuelPlayer player, String kitName);

    /**
     * Retrieves the kit model for the specified player.
     *
     * @param player The player.
     * @return The kit model for the specified player.
     */
    KitModel getKitModel(DuelPlayer player);

}
