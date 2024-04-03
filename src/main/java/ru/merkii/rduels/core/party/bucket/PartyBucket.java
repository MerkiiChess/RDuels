package ru.merkii.rduels.core.party.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.party.model.PartyModel;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PartyBucket {

    private final List<PartyModel> partyModels = new ArrayList<>();

    public void add(PartyModel partyModel) {
        this.partyModels.add(partyModel);
    }

    public void remove(PartyModel partyModel) {
        this.partyModels.remove(partyModel);
    }

}
