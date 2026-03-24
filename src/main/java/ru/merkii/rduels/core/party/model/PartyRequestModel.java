package ru.merkii.rduels.core.party.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyRequestModel {

    PartyModel invitedParty;
    UUID invitedPlayer;
    long endDurationRequest;

    public static PartyRequestModel create(PartyModel invitedParty, UUID invitedPlayer) {
        return new PartyRequestModel(invitedParty, invitedPlayer, System.currentTimeMillis() + TimeUtil.parseTime("1m", TimeUnit.MINUTES));
    }

}
