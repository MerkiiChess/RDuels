package ru.merkii.rduels.core.sign.api;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.model.SignQueueModel;
import ru.merkii.rduels.model.BlockPosition;

import java.util.Optional;

public interface SignAPI {

    /**
     * Adds a sign model to the sign storage and saves it.
     *
     * @param signModel The sign model to add.
     */
    void addSign(SignModel signModel);

    /**
     * Removes a sign model from the sign storage and saves the changes.
     *
     * @param signModel The sign model to remove.
     */
    void removeSign(SignModel signModel);

    /**
     * Removes a sign model based on its block position and saves the changes.
     *
     * @param blockPosition The block position of the sign to remove.
     * @return True if the sign was successfully removed, otherwise false.
     */
    boolean removeSign(BlockPosition blockPosition);

    /**
     * Checks if the sign storage contains a sign model with the specified block position.
     *
     * @param blockPosition The block position to check.
     * @return True if the sign storage contains the specified block position, otherwise false.
     */
    boolean isContainsSignPosition(BlockPosition blockPosition);

    /**
     * Retrieves the sign model associated with the specified block position.
     *
     * @param blockPosition The block position of the sign model to retrieve.
     * @return The sign model associated with the block position, or null if not found.
     */
    @Nullable
    SignModel getModelInBlockPosition(BlockPosition blockPosition);

    /**
     * Retrieves the sign queue model associated with the specified sign model.
     *
     * @param signModel The sign model for which to retrieve the queue model.
     * @return The sign queue model associated with the sign model, or null if not found.
     */
    @Nullable
    SignQueueModel getQueueInSignModel(SignModel signModel);


    /**
     * Checks if the specified sign model is associated with a fight sign.
     *
     * @param signModel The sign model to check.
     * @return True if the sign model is associated with a fight sign, otherwise false.
     */
    boolean isFightSign(SignModel signModel);

    /**
     * Adds a sign model to the fight sign storage.
     *
     * @param signModel The sign model to add as a fight sign.
     */
    void addSignFight(SignModel signModel);

    /**
     * Adds a sign queue model to the sign queue storage.
     *
     * @param signQueueModel The sign queue model to add.
     */
    void addQueueSign(SignQueueModel signQueueModel);

    /**
     * Removes a sign model from the fight sign storage and its associated queue if exists.
     *
     * @param signModel The sign model to remove from the fight sign storage.
     */
    void removeSignFight(SignModel signModel);

    /**
     * Removes a sign queue model from the sign queue storage.
     *
     * @param signQueueModel The sign queue model to remove.
     */
    void removeQueueSign(SignQueueModel signQueueModel);

    /**
     * Checks if the specified player is in any sign queue.
     *
     * @param player The player to check.
     * @return True if the player is in a sign queue, otherwise false.
     */
    boolean isQueuePlayer(Player player);

    Optional<SignQueueModel> getQueueFromPlayer(Player player);

    /**
     * Checks if the specified player clicked on a sign associated with the specified sign model.
     *
     * @param player    The player to check.
     * @param signModel The sign model associated with the sign.
     * @return True if the player clicked on a sign associated with the specified sign model and is in a queue, otherwise false.
     */
    boolean isClickedSignQueuePlayer(Player player, SignModel signModel);

    /**
     * Sets the text on the specified sign to indicate waiting state with the provided information.
     *
     * @param sign         The sign to update.
     * @param players      The number of players in the queue.
     * @param size         The maximum size of the queue.
     * @param duelKitType  The type of duel kit.
     * @param kitName      The name of the kit.
     */
    void setSignWait(Sign sign, int players, int size, DuelKitType duelKitType, String kitName);

    /**
     * Sets the text on the specified sign to indicate active state with the provided information.
     *
     * @param sign        The sign to update.
     * @param sender      The sender player.
     * @param receiver    The receiver player.
     * @param duelKitType The type of duel kit.
     */
    void setSignActive(Sign sign, Player sender, Player receiver, DuelKitType duelKitType);

    void removePlayerQueueSign(Player player);

    void removePlayerQueueSign(Player... players);

}
