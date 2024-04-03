package ru.merkii.rduels.core.party.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class PartyRequestModel {

    private final PartyModel invitedParty;
    private final UUID invitedPlayer;
    private final long endDurationRequest;

    public static PartyRequestModel create(PartyModel invitedParty, UUID invitedPlayer) {
        return new PartyRequestModel(invitedParty, invitedPlayer, System.currentTimeMillis() + TimeUtil.parseTime(PartyCore.INSTANCE.getPartyConfig().getDuration(), TimeUnit.MINUTES));
    }

}
