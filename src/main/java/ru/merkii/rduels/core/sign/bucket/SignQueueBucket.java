package ru.merkii.rduels.core.sign.bucket;

import jakarta.inject.Singleton;
import lombok.Getter;
import ru.merkii.rduels.core.sign.model.SignQueueModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
public class SignQueueBucket {

    private final List<SignQueueModel> queues = new ArrayList<>();

    public void addQueue(SignQueueModel signQueueModel) {
        this.queues.add(signQueueModel);
    }

    public void removeQueue(SignQueueModel signQueueModel) {
        this.queues.remove(signQueueModel);
    }

}
