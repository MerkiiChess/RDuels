package ru.merkii.rduels.core.party.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.party.model.PartyRequestModel;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyRequestBucket {

    List<PartyRequestModel> partyRequestModels = new ArrayList<>();

    public void addRequest(PartyRequestModel partyRequestModel) {
        this.partyRequestModels.add(partyRequestModel);
    }

    public void removeRequest(PartyRequestModel partyRequestModel) {
        this.partyRequestModels.remove(partyRequestModel);
    }

}
