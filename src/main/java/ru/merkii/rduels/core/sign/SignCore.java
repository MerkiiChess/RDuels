package ru.merkii.rduels.core.sign;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.api.provider.SignAPIProvider;
import ru.merkii.rduels.core.sign.command.QueueCommand;
import ru.merkii.rduels.core.sign.listener.SignListener;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.core.sign.storage.SignStorage;
import ru.merkii.rduels.core.sign.util.SignUtil;
import ru.merkii.rduels.model.BlockPosition;

@Getter
@Singleton
public class SignCore implements Core {

    private SignAPI signAPI;
    private SignStorage signStorage;
    private final Lamp<BukkitCommandActor> lamp;

    @Inject
    public SignCore(Lamp<BukkitCommandActor> lamp) {
        this.lamp = lamp;
    }

    @Override
    public void enable(RDuels plugin) {
        this.signAPI = RDuels.beanScope().get(SignAPI.class);
        this.signStorage = RDuels.beanScope().get(SignStorage.class);
        plugin.registerListeners(SignListener.class);
        lamp.register(RDuels.beanScope().get(QueueCommand.class));
        for (SignModel signModel : signStorage.getSigns()) {
            BlockPosition blockPosition = signModel.getBlockPosition();
            try {
                Block block = blockPosition.getBlock();
                Sign sign = (Sign) block.getState();
                this.signAPI.setSignWait(sign, 0, signModel.getDuelType().getSize(), signModel.getDuelKit(), signModel.getKitModel() == null ? "" : signModel.getKitModel().getDisplayName());
            } catch (Exception ignored) {
                plugin.getLogger().info("Табличка на координатах: " + blockPosition.getWorldName() + " " + blockPosition.getBlockX() + " " + blockPosition.getBlockY() + " " + blockPosition.getBlockZ() + " не была поставлена. Проверьте хранилище с табличками");
            }
        }
    }

    @Override
    public void disable(RDuels plugin) {
        for (SignModel signModel : this.signStorage.getSigns()) {
            BlockPosition blockPosition = signModel.getBlockPosition();
            try {
                Block block = blockPosition.getBlock();
                Sign sign = (Sign) block.getState();
                SignUtil.clearSignsLines(sign);
            } catch (Exception ignored) {
                plugin.getLogger().info("Табличка в мире: " + blockPosition.getWorldName() + " на координатах" + blockPosition.getBlockX() + " " + blockPosition.getBlockY() + " " + blockPosition.getBlockZ() + " не была очищена. Проверьте хранилище с табличками");
            }
        }
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}
