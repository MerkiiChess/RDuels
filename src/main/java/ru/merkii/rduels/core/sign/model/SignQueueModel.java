package ru.merkii.rduels.core.sign.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;

@Getter
public class SignQueueModel {

    private final SignModel signModel;
    @Setter
    private DuelPlayer sender;
    @Setter
    private DuelPlayer senderHelper;
    @Setter
    private DuelPlayer receiver;
    @Setter
    private DuelPlayer receiverHelper;

    public SignQueueModel(SignModel signModel, DuelPlayer sender) {
        this.signModel = signModel;
        this.sender = sender;
    }

    public static SignQueueModel create(SignModel signModel, DuelPlayer sender) {
        return new SignQueueModel(signModel, sender);
    }

    public static SignQueueModelBuilder builder() {
        return new SignQueueModelBuilder();
    }

    public static class SignQueueModelBuilder {

        private SignModel signModel;
        private DuelPlayer sender;
        private DuelPlayer senderHelper;
        private DuelPlayer receiver;
        private DuelPlayer receiverHelper;

        public SignQueueModelBuilder() {
        }

        public SignQueueModelBuilder signModel(SignModel signModel) {
            this.signModel = signModel;
            return this;
        }

        public SignQueueModelBuilder sender(DuelPlayer sender) {
            this.sender = sender;
            return this;
        }

        public SignQueueModelBuilder senderHelper(DuelPlayer senderHelper) {
            this.senderHelper = senderHelper;
            return this;
        }

        public SignQueueModelBuilder receiver(DuelPlayer receiver) {
            this.receiver = receiver;
            return this;
        }

        public SignQueueModelBuilder receiverHelper(DuelPlayer receiverHelper) {
            this.receiverHelper = receiverHelper;
            return this;
        }

        public SignQueueModel build() {
            SignQueueModel signQueueModel = new SignQueueModel(signModel, sender);
            signQueueModel.setSenderHelper(senderHelper);
            signQueueModel.setReceiver(receiver);
            signQueueModel.setReceiverHelper(receiverHelper);
            return signQueueModel;
        }

    }
}
