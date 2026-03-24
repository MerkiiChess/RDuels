package ru.merkii.rduels.core.party.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.party.model.PartyModel;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyFightBucket {

    List<PartyModel> fightParty = new ArrayList<>();

    public void add(PartyModel partyModel) {
        this.fightParty.add(partyModel);
    }

    public void remove(PartyModel partyModel) {
        this.fightParty.remove(partyModel);
    }

}
