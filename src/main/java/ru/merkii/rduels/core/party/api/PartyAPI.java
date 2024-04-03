package ru.merkii.rduels.core.party.api;

import org.bukkit.entity.Player;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.core.party.model.PartyRequestModel;

import javax.annotation.Nullable;
import java.util.List;

public interface PartyAPI {

    /**
     * Creates a party for the specified player, adds it to the party bucket, and gives start items to the player.
     *
     * @param player The player for whom the party is created.
     */
    void createParty(Player player);

    /**
     * Removes the specified player from their party, handles ownership changes if necessary, and sends appropriate messages.
     * Clears player's inventory, sets their game mode to survival if they are fighting, and gives start items.
     *
     * @param player The player leaving the party.
     */
    void leaveParty(Player player);

    void leaveParty(Player player, boolean text);

    /**
     * Invites a player to join the specified party, adds the invitation to the request bucket, and sends invitation messages.
     *
     * @param partyModel The party to which the player is invited.
     * @param player     The player being invited.
     */
    void inviteParty(PartyModel partyModel, Player player);

    /**
     * Allows a player to join the specified party, adds them to the party, gives start items, and notifies relevant players.
     * If joining a fight party, adds the player as a spectator.
     *
     * @param partyModel The party the player is joining.
     * @param player     The player joining the party.
     */
    void joinParty(PartyModel partyModel, Player player);

    /**
     * Adds a party request to the request bucket.
     *
     * @param partyRequestModel The party request to add.
     */
    void addRequest(PartyRequestModel partyRequestModel);

    /**
     * Removes a party request from the request bucket.
     *
     * @param partyRequestModel The party request to remove.
     */
    void removeRequest(PartyRequestModel partyRequestModel);

    /**
     * Retrieves the party request model between the sender and receiver players.
     *
     * @param sender   The sender of the party request.
     * @param receiver The receiver of the party request.
     * @return The party request model if found and not expired, otherwise null.
     */
    @Nullable
    PartyRequestModel getPartyRequestModel(Player sender, Player receiver);

    /**
     * Retrieves the party model associated with the specified player.
     *
     * @param player The player for whom to retrieve the party model.
     * @return The party model associated with the player, or null if not found.
     */
    @Nullable
    PartyModel getPartyModelFromPlayer(Player player);

    /**
     * Checks if the specified player is a member of any party.
     *
     * @param player The player to check.
     * @return True if the player is in a party, otherwise false.
     */
    boolean isPartyPlayer(Player player);

    /**
     * Adds one or more parties to the fight party bucket.
     *
     * @param partyModels The parties to add to the fight party bucket.
     */
    void addFightParty(PartyModel... partyModels);

    /**
     * Removes one or more parties from the fight party bucket.
     *
     * @param partyModels The parties to remove from the fight party bucket.
     */
    void removeFightParty(PartyModel... partyModels);

    /**
     * Checks if the specified party is a fight party.
     *
     * @param partyModel The party to check.
     * @return True if the party is a fight party, otherwise false.
     */
    boolean isFightParty(PartyModel partyModel);

    /**
     * Retrieves all parties in the party bucket.
     *
     * @return A list containing all parties.
     */
    List<PartyModel> getAllParty();

    /**
     * Teleports players from sender and receiver parties to the specified arena model.
     *
     * @param duelRequest The duel request containing sender and receiver parties and the arena model.
     */
    void teleportToArena(DuelRequest duelRequest);

    /**
     * Teleports players from sender and receiver parties of the provided duel fight model to the associated arena model.
     *
     * @param duelFightModel The duel fight model containing sender and receiver parties and the arena model.
     */
    void teleportToArena(DuelFightModel duelFightModel);

    /**
     * Teleports players from sender and receiver parties to the specified arena model.
     *
     * @param senderParty   The sender party.
     * @param receiverParty The receiver party.
     * @param arenaModel    The arena model.
     */
    void teleportToArena(PartyModel senderParty, PartyModel receiverParty, ArenaModel arenaModel);

    /**
     * Gives start items to the specified players.
     *
     * @param players The players to give start items.
     */
    void giveStartItems(Player... players);

}
