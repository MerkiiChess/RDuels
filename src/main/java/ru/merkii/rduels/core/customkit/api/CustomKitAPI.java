package ru.merkii.rduels.core.customkit.api;

import org.bukkit.inventory.ItemStack;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Optional;

public interface CustomKitAPI {

    /**
     * Retrieves the display name of the selected kit for the specified player.
     *
     * @param player The player whose selected kit display name to retrieve.
     * @return The display name of the selected kit, or "NULL" if no kit is selected.
     */
    @Deprecated(forRemoval = false, since = "2.0.0")
    String getSelectedKitDisplayName(DuelPlayer player);

    default Optional<String> getSelectedKit(DuelPlayer player) {
        return Optional.ofNullable(getSelectedKitDisplayName(player))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .filter(value -> !"NULL".equalsIgnoreCase(value));
    }

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
