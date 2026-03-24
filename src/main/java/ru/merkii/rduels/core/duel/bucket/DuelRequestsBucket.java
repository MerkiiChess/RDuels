package ru.merkii.rduels.core.duel.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.duel.model.DuelRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuelRequestsBucket {

    List<DuelRequest> requests = new ArrayList<>();

    public void addRequest(DuelRequest request) {
        this.requests.add(request);
    }

    public void removeRequest(DuelRequest request) {
        this.requests.remove(request);
    }

}
