package ru.merkii.rduels.core.duel.request;

import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelRequest;

import java.util.List;

public interface DuelRequestService {

    void addRequest(DuelRequest duelRequest);
    void removeRequest(DuelRequest duelRequest);
    List<DuelRequest> getRequestsFromReceiver(DuelPlayer receiver);
    DuelRequest getRequestFromSender(DuelPlayer sender, DuelPlayer receiver);

}
