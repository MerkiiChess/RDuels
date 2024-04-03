package ru.merkii.rduels.core.party.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.party.model.PartyModel;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PartyFightBucket {

    private final List<PartyModel> fightParty = new ArrayList<>();

    public void add(PartyModel partyModel) {
        this.fightParty.add(partyModel);
    }

    public void remove(PartyModel partyModel) {
        this.fightParty.remove(partyModel);
    }

}
