package ru.merkii.rduels.core.sign.bucket;

import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.core.sign.model.SignQueueModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SignQueueBucket {

    List<SignQueueModel> queues = new ArrayList<>();

    public void addQueue(SignQueueModel signQueueModel) {
        this.queues.add(signQueueModel);
    }

    public void removeQueue(SignQueueModel signQueueModel) {
        this.queues.remove(signQueueModel);
    }

}
