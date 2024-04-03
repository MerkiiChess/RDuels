package ru.merkii.rduels.core.duel.bucket;

import lombok.Getter;
import ru.merkii.rduels.core.duel.model.DuelRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DuelRequestsBucket {

    private final List<DuelRequest> requests = new ArrayList<>();

    public void addRequest(DuelRequest request) {
        this.requests.add(request);
    }

    public void removeRequest(DuelRequest request) {
        this.requests.remove(request);
    }

}
