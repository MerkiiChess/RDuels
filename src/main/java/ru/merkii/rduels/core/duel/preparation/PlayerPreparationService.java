package ru.merkii.rduels.core.duel.preparation;

import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.EntityPosition;

import java.util.List;

public interface PlayerPreparationService {

    void preparationToFight(List<DuelPlayer> players);
    void preparationToFight(DuelPlayer... players);
    void preparationToFight(PartyModel senderParty, PartyModel receiverParty);
    void giveStartItems(DuelPlayer player);
    EntityPosition getRandomSpawn();

}
