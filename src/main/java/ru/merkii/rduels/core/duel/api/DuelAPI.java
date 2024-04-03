package ru.merkii.rduels.core.duel.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.duel.schedualer.DuelTeleportScheduler;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Optional;

public interface DuelAPI {

    /**
     * Retrieves the kit model based on the given kit name.
     *
     * @param kitName The name of the kit.
     * @return The KitModel corresponding to the given kit name, or null if not found.
     */
    @Nullable
    KitModel getKitFromName(String kitName);

    /**
     * Checks if the player is involved in a fight.
     *
     * @param player The player to check.
     * @return True if the player is involved in a fight, false otherwise.
     */
    boolean isFightPlayer(Player player);

    /**
     * Retrieves the DuelFightModel associated with the given player.
     *
     * @param player The player for whom to retrieve the DuelFightModel.
     * @return The DuelFightModel associated with the player, or null if not found.
     */
    @Nullable
    DuelFightModel getFightModelFromPlayer(Player player);

    /**
     * Adds a duel request to the system.
     *
     * @param duelRequest The DuelRequest to add.
     */
    void addRequest(DuelRequest duelRequest);

    /**
     * Removes a duel request from the system.
     *
     * @param duelRequest The DuelRequest to remove.
     */
    void removeRequest(DuelRequest duelRequest);

    /**
     * Initiates a fight based on the provided duel request.
     *
     * @param duelRequest The duel request to start the fight with.
     */
    void startFight(DuelRequest duelRequest);

    void startFightFour(Player player, Player player2, Player player3, Player player4, DuelRequest duelRequest);

    /**
     * Moves to the next round of the ongoing fight.
     *
     * @param duelFight The DuelFightModel representing the ongoing fight.
     */
    void nextRound(DuelFightModel duelFight);

    /**
     * Stops the ongoing fight and determines the winner and loser.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param winner         The player who won the fight.
     * @param loser          The player who lost the fight.
     */
    void stopFight(DuelFightModel duelFightModel, Player winner, Player loser);

    /**
     * Retrieves a free arena available for a fight.
     *
     * @return A free ArenaModel for a fight, or null if none is available.
     */
    @Nullable
    ArenaModel getFreeArena();

    /**
     * Retrieves a free arena with the specified name.
     *
     * @param name The name of the arena to retrieve.
     * @return A free ArenaModel with the specified name, or null if none is available.
     */
    @Nullable
    ArenaModel getFreeArenaName(String name);

    @Nullable
    ArenaModel getFreeArenaFFA();

    /**
     * Retrieves a list of duel requests received by the specified player.
     *
     * @param receiver The player who received the requests.
     * @return A list of DuelRequest objects received by the player.
     */
    @Nullable
    List<DuelRequest> getRequestsFromReceiver(Player receiver);


    /**
     * Retrieves the winner of a fight based on the provided DuelFightModel and loser.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param loser          The player who lost the fight.
     * @return The winner of the fight.
     */
    Player getWinnerFromFight(DuelFightModel duelFightModel, Player loser);

    /**
     * Retrieves the loser of a fight based on the provided DuelFightModel and winner.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param winner         The player who won the fight.
     * @return The loser of the fight.
     */
    Player getLoserFromFight(DuelFightModel duelFightModel, Player winner);

    /**
     * Retrieves the opponent of a player in the provided DuelFightModel.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param player         The player for whom to retrieve the opponent.
     * @return The opponent of the player in the fight.
     */
    Player getOpponentFromFight(DuelFightModel duelFightModel, Player player);

    /**
     * Retrieves the opponent of a player in the ongoing fight.
     *
     * @param player The player for whom to retrieve the opponent.
     * @return The opponent of the player in the ongoing fight.
     */
    @Nullable
    Player getOpponentFromFight(Player player);

    /**
     * Retrieves a duel request sent by the specified sender to the specified receiver.
     *
     * @param sender   The sender of the request.
     * @param receiver The receiver of the request.
     * @return The DuelRequest sent by the sender to the receiver, if found; otherwise, null.
     */
    @Nullable
    DuelRequest getRequestFromSender(Player sender, Player receiver);

    /**
     * Saves a custom kit for the specified player.
     *
     * @param player  The player saving the kit.
     * @param kitName The name of the kit to save.
     */
    void saveKitServer(Player player, String kitName);

    /**
     * Checks if a kit with the specified name exists.
     *
     * @param kitName The name of the kit to check.
     * @return True if a kit with the specified name exists, otherwise false.
     */
    boolean isKitNameContains(String kitName);

    /**
     * Gets a free slot for a kit.
     *
     * @return The index of a free slot for a kit, or -1 if none is available.
     */
    int getFreeSlotKit();

    /**
     * Retrieves a random spawn location.
     *
     * @return A random Location object representing a spawn location.
     */
    Location getRandomSpawn();

    /**
     * Gives starting items to the specified player.
     *
     * @param player The player to whom to give starting items.
     */
    void giveStartItems(Player player);

    /**
     * Retrieves a random KitModel from the available kits.
     *
     * @return A random KitModel.
     */
    KitModel getRandomKit();

    /**
     * Adds the specified player to the list of players who cannot move during a fight.
     *
     * @param player The player to add.
     */
    void addNoMove(Player player);

    /**
     * Removes the specified player from the list of players who cannot move during a fight.
     *
     * @param player The player to remove.
     */
    void removeNoMove(Player player);

    /**
     * Checks if the specified player is restricted from moving during a fight.
     *
     * @param player The player to check.
     * @return True if the player is restricted from moving, otherwise false.
     */
    boolean isNoMovePlayer(Player player);

    /**
     * Adds the specified player as a spectator in the provided duel fight.
     *
     * @param player       The player to add as a spectator.
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     */
    void addSpectate(Player player, DuelFightModel duelFightModel);

    /**
     * Removes the specified player from being a spectator in the provided duel fight.
     *
     * @param player       The player to remove from being a spectator.
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @param fighting     True if the players are currently fighting, otherwise false.
     */
    void removeSpectate(Player player, DuelFightModel duelFightModel, boolean fighting);

    /**
     * Checks if the specified player is currently a spectator in any ongoing fight.
     *
     * @param player The player to check.
     * @return True if the player is a spectator, otherwise false.
     */
    boolean isSpectate(Player player);

    /**
     * Retrieves the DuelFightModel associated with the specified spectator player.
     *
     * @param player The player who is spectating.
     * @return The DuelFightModel associated with the spectator player, or null if not spectating.
     */
    @Nullable
    DuelFightModel getDuelFightModelFromSpectator(Player player);

    /**
     * Prepares a list of players for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param players The list of players to prepare for the fight.
     */
    void preparationToFight(List<Player> players);

    /**
     * Prepares a group of players for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param players The group of players to prepare for the fight.
     */
    void preparationToFight(Player... players);

    /**
     * Prepares two party models for a fight by setting their game modes, clearing effects, and healing them.
     *
     * @param senderParty   The party model of the sender.
     * @param receiverParty The party model of the receiver.
     */
    void preparationToFight(PartyModel senderParty, PartyModel receiverParty);

    /**
     * Retrieves the teleport scheduler associated with the provided duel fight.
     *
     * @param duelFightModel The DuelFightModel representing the ongoing fight.
     * @return An Optional containing the DuelTeleportScheduler if found, otherwise empty.
     */
    Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel);

}
