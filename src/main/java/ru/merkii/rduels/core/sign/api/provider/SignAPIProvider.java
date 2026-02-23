package ru.merkii.rduels.core.sign.api.provider;

import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.bucket.SignFightBucket;
import ru.merkii.rduels.core.sign.bucket.SignQueueBucket;
import ru.merkii.rduels.core.sign.model.SignQueueModel;
import ru.merkii.rduels.core.sign.storage.SignStorage;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.util.SignUtil;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.util.ColorUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class SignAPIProvider implements SignAPI {

    private final MessageConfig messageConfig;
    private final SignStorage signStorage;
    private final SignQueueBucket signQueue;
    private final SignFightBucket signFight;

    public SignAPIProvider(MessageConfig messageConfig, SignStorage signStorage, SignQueueBucket signQueue, SignFightBucket signFight) {
        this.messageConfig = messageConfig;
        this.signStorage = signStorage;
        this.signQueue = signQueue;
        this.signFight = signFight;
    }

    @Override
    public void addSign(SignModel signModel) {
        this.signStorage.getSigns().add(signModel);
        this.signStorage.save();
    }

    @Override
    public void removeSign(SignModel signModel) {
        this.signStorage.getSigns().remove(signModel);
        this.signStorage.save();
    }

    @Override
    public boolean removeSign(BlockPosition blockPosition) {
        Optional<SignModel> signModelOptional = this.signStorage.getSigns().stream().filter(signModel -> signModel.getBlockPosition().equals(blockPosition)).findFirst();
        if (signModelOptional.isPresent()) {
            this.removeSign(signModelOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean isContainsSignPosition(BlockPosition blockPosition) {
        return this.signStorage.getSigns().stream().anyMatch(signModel -> signModel.getBlockPosition().equals(blockPosition));
    }

    @Override
    public @Nullable SignModel getModelInBlockPosition(BlockPosition blockPosition) {
        return this.signStorage.getSigns().stream().filter(signModel -> signModel.getBlockPosition().equals(blockPosition)).findFirst().orElse(null);
    }

    @Override
    public @Nullable SignQueueModel getQueueInSignModel(SignModel signModel) {
        return this.signQueue.getQueues().stream().filter(queueModel -> queueModel.getSignModel().equals(signModel)).findFirst().orElse(null);
    }

    @Override
    public boolean isFightSign(SignModel signModel) {
        return this.signFight.getFightSigns().stream().anyMatch(fightModel -> fightModel.equals(signModel));
    }

    @Override
    public void addSignFight(SignModel signModel) {
        this.signFight.add(signModel);
    }

    @Override
    public void addQueueSign(SignQueueModel signQueueModel) {
        this.signQueue.addQueue(signQueueModel);
    }

    @Override
    public void removeSignFight(SignModel signModel) {
        this.signFight.remove(signModel);
        SignQueueModel signQueueModel = this.getQueueInSignModel(signModel);
        if (signQueueModel != null) {
            this.removeQueueSign(signQueueModel);
        }
        Sign sign = (Sign) signModel.getBlockPosition().getBlock().getState();
        SignUtil.clearSignsLines(sign);
        this.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
    }

    @Override
    public void removeQueueSign(SignQueueModel signQueueModel) {
        this.signQueue.removeQueue(signQueueModel);
    }

    @Override
    public boolean isQueuePlayer(DuelPlayer player) {
        for (SignQueueModel signQueueModel : this.signQueue.getQueues()) {
            if ((signQueueModel.getSender() != null && signQueueModel.getSender().equals(player)) || (signQueueModel.getSenderHelper() != null && signQueueModel.getSenderHelper().equals(player)) || (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().equals(player)) || (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().equals(player))) return true;
        }
        return false;
    }

    @Override
    public Optional<SignQueueModel> getQueueFromPlayer(DuelPlayer player) {
        for (SignQueueModel signQueueModel : this.signQueue.getQueues()) {
            if ((signQueueModel.getSender() != null && signQueueModel.getSender().equals(player)) || (signQueueModel.getSenderHelper() != null && signQueueModel.getSenderHelper().equals(player)) || (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().equals(player)) || (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().equals(player))) return Optional.of(signQueueModel);
        }
        return Optional.empty();
    }

    @Override
    public boolean isClickedSignQueuePlayer(DuelPlayer player, SignModel signModel) {
        return this.isQueuePlayer(player) && this.getQueueInSignModel(signModel) != null && this.signQueue.getQueues().stream().anyMatch(Objects.requireNonNull(this.getQueueInSignModel(signModel))::equals);
    }

    @Override
    public void setSignWait(Sign sign, int players, int size, DuelKitType duelKitType, String kitName) {
        SignUtil.clearSignsLines(sign);
        String typeValue = duelKitType == DuelKitType.CUSTOM
                ? messageConfig.plainMessage("sign-custom-replacer")
                : messageConfig.plainMessage("sign-server-replacer").replace("(kit)", kitName);
        Placeholder.Placeholders placeholders = Placeholder.Placeholders.of(
                Placeholder.of("(players)", String.valueOf(players)),
                Placeholder.of("(playersMaxSize)", String.valueOf(size)),
                Placeholder.of("(kitName)", ColorUtil.color(kitName)),
                Placeholder.of("(type)", typeValue)
        );
        Component message = messageConfig.message(placeholders, "sign-text-wait");
        SignUtil.setLines(sign, message);
    }

    @Override
    public void setSignActive(Sign sign, DuelPlayer sender, DuelPlayer receiver, DuelKitType duelKitType) {
        SignUtil.clearSignsLines(sign);
        BlockPosition blockPosition = new BlockPosition(sign.getBlock());
        SignModel signModel = this.getModelInBlockPosition(blockPosition);
        String kitName = signModel != null && signModel.getKitModel() != null ? signModel.getKitModel().getDisplayName() : "";
        String typeValue = duelKitType == DuelKitType.CUSTOM
                ? messageConfig.plainMessage("sign-custom-replacer")
                : messageConfig.plainMessage("sign-server-replacer").replace("(kit)", kitName);
        Placeholder.Placeholders placeholders = Placeholder.Placeholders.of(
                Placeholder.of("(player)", sender.getName()),
                Placeholder.of("(player2)", receiver.getName()),
                Placeholder.of("(type)", typeValue)
        );
        SignUtil.setLines(sign, messageConfig.message(placeholders, "sign-text-active"));
    }

    @Override
    public void removePlayerQueueSign(DuelPlayer player) {
        this.getQueueFromPlayer(player).ifPresent(queue -> {
            if (queue.getSender() != null && queue.getSender().equals(player)) {
                queue.setSender(null);
            } else if (queue.getReceiver() != null && queue.getReceiver().equals(player)) {
                queue.setReceiver(null);
            } else if (queue.getReceiverHelper() != null && queue.getReceiverHelper().equals(player)) {
                queue.setReceiverHelper(null);
            } else if (queue.getSenderHelper() != null && queue.getSenderHelper().equals(player)) {
                queue.setSenderHelper(null);
            }
            int size = 0;
            if (queue.getSender() != null) size++;
            if (queue.getSenderHelper() != null) size++;
            if (queue.getReceiver() != null) size++;
            if (queue.getReceiverHelper() != null) size++;
            Sign sign = (Sign) queue.getSignModel().getBlockPosition().getBlock().getState();
            this.setSignWait(sign, size, queue.getSignModel().getDuelType().getSize(), queue.getSignModel().getDuelKit(), queue.getSignModel().getKitModel().getDisplayName());
        });
    }

    @Override
    public void removePlayerQueueSign(DuelPlayer... players) {
        Arrays.asList(players).forEach(this::removePlayerQueueSign);
    }

}