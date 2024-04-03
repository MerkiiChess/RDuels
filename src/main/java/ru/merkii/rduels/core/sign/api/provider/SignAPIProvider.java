package ru.merkii.rduels.core.sign.api.provider;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.bucket.SignFightBucket;
import ru.merkii.rduels.core.sign.bucket.SignQueueBucket;
import ru.merkii.rduels.core.sign.model.SignQueueModel;
import ru.merkii.rduels.core.sign.storage.SignStorage;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.util.SignUtil;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.util.ColorUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SignAPIProvider implements SignAPI {

    private final MessageConfiguration messageConfiguration = RDuels.getInstance().getPluginMessage();
    private final SignCore signCore = SignCore.INSTANCE;
    private final SignStorage signStorage = signCore.getSignStorage();
    private final SignQueueBucket signQueue = new SignQueueBucket();
    private final SignFightBucket signFight = new SignFightBucket();

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
    public boolean isQueuePlayer(Player player) {
        for (SignQueueModel signQueueModel : this.signQueue.getQueues()) {
            if ((signQueueModel.getSender() != null && signQueueModel.getSender().equals(player)) || (signQueueModel.getSenderHelper() != null && signQueueModel.getSenderHelper().equals(player)) || (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().equals(player)) || (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().equals(player))) return true;
        }
        return false;
    }

    @Override
    public Optional<SignQueueModel> getQueueFromPlayer(Player player) {
        for (SignQueueModel signQueueModel : this.signQueue.getQueues()) {
            if ((signQueueModel.getSender() != null && signQueueModel.getSender().equals(player)) || (signQueueModel.getSenderHelper() != null && signQueueModel.getSenderHelper().equals(player)) || (signQueueModel.getReceiver() != null && signQueueModel.getReceiver().equals(player)) || (signQueueModel.getReceiverHelper() != null && signQueueModel.getReceiverHelper().equals(player))) return Optional.of(signQueueModel);
        }
        return Optional.empty();
    }

    @Override
    public boolean isClickedSignQueuePlayer(Player player, SignModel signModel) {
        return this.isQueuePlayer(player) && this.getQueueInSignModel(signModel) != null && this.signQueue.getQueues().stream().anyMatch(Objects.requireNonNull(this.getQueueInSignModel(signModel))::equals);
    }

    @Override
    public void setSignWait(Sign sign, int players, int size, DuelKitType duelKitType, String kitName) {
        SignUtil.clearSignsLines(sign);
        SignUtil.setLines(sign, this.messageConfiguration.getMessages("signTextWait")
                .stream()
                .map(str -> str.replace("(players)", String.valueOf(players)))
                .map(str -> str.replace("(playersMaxSize)", String.valueOf(size)))
                .map(str -> str.replace("(type)", duelKitType == DuelKitType.CUSTOM ? this.messageConfiguration.getMessage("signCustomReplacer") : this.messageConfiguration.getMessage("signServerReplacer")))
                .map(str -> str.replace("(kitName)", ColorUtil.color(kitName)))
                .collect(Collectors.toList())
        );
    }

    @Override
    public void setSignActive(Sign sign, Player sender, Player receiver, DuelKitType duelKitType) {
        SignUtil.clearSignsLines(sign);
        SignUtil.setLines(sign, this.messageConfiguration.getMessages("signTextActivate")
                .stream()
                .map(str -> str.replace("(player)", sender.getName()))
                .map(str -> str.replace("(player2)", receiver.getName()))
                .map(str -> str.replace("(type)", duelKitType == DuelKitType.CUSTOM ? this.messageConfiguration.getMessage("signCustomReplacer") : this.messageConfiguration.getMessage("signServerReplacer")))
                .collect(Collectors.toList())
        );
    }

}
