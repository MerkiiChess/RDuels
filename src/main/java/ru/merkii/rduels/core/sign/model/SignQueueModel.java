package ru.merkii.rduels.core.sign.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class SignQueueModel {

    private final SignModel signModel;
    @Setter
    private Player sender;
    @Setter
    private Player senderHelper;
    @Setter
    private Player receiver;
    @Setter
    private Player receiverHelper;

    public SignQueueModel(SignModel signModel, Player sender) {
        this.signModel = signModel;
        this.sender = sender;
    }

    public static SignQueueModel create(SignModel signModel, Player sender) {
        return new SignQueueModel(signModel, sender);
    }

    public static SignQueueModelBuilder builder() {
        return new SignQueueModelBuilder();
    }

    public static class SignQueueModelBuilder {

        private SignModel signModel;
        private Player sender;
        private Player senderHelper;
        private Player receiver;
        private Player receiverHelper;

        public SignQueueModelBuilder() {
        }

        public SignQueueModelBuilder signModel(SignModel signModel) {
            this.signModel = signModel;
            return this;
        }

        public SignQueueModelBuilder sender(Player sender) {
            this.sender = sender;
            return this;
        }

        public SignQueueModelBuilder senderHelper(Player senderHelper) {
            this.senderHelper = senderHelper;
            return this;
        }

        public SignQueueModelBuilder receiver(Player receiver) {
            this.receiver = receiver;
            return this;
        }

        public SignQueueModelBuilder receiverHelper(Player receiverHelper) {
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
