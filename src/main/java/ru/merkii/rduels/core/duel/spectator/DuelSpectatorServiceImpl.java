package ru.merkii.rduels.core.duel.spectator;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.adapter.Task;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.duel.bucket.DuelFightBucket;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.duel.preparation.PlayerPreparationService;

import java.util.UUID;

@Singleton
public class DuelSpectatorServiceImpl implements DuelSpectatorService {

    private final RDuels plugin;
    private final MessageConfig config;
    private final DuelFightBucket fightBucket;
    private final PlayerPreparationService preparationService;
    private final Task task;

    @Inject
    public DuelSpectatorServiceImpl(RDuels plugin, MessageConfig config,
                                    DuelFightBucket fightBucket,
                                    PlayerPreparationService preparationService,
                                    Task task) {
        this.plugin = plugin;
        this.config = config;
        this.fightBucket = fightBucket;
        this.preparationService = preparationService;
        this.task = task;
    }

    @Override
    public void addSpectate(DuelPlayer player, DuelFightModel duelFightModel) {
        duelFightModel.getSpectates().add(player.getUUID());
        player.teleport(duelFightModel.getArenaModel().getSpectatorPosition());
        task.syncDelay(() -> player.setGameMode(GameMode.SPECTATOR), 10L);
    }

    @Override
    public void removeSpectate(DuelPlayer player, DuelFightModel duelFightModel, boolean fighting) {
        duelFightModel.getSpectates().remove(player.getUUID());
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(preparationService.getRandomSpawn());
        if (fighting) {
            Component message = config.message(Placeholder.wrapped("(player)", player.getName()), "stop-spectate");
            duelFightModel.getSender().sendMessage(message);
            duelFightModel.getReceiver().sendMessage(message);
        }
    }

    @Override
    public boolean isSpectate(DuelPlayer p) {
        return getDuelFightModelFromSpectator(p) != null;
    }

    @Override
    public DuelFightModel getDuelFightModelFromSpectator(DuelPlayer p) {
        UUID uuid = p.getUUID();
        return fightBucket.getDuelFights().stream()
                .filter(f -> f.getSpectates().contains(uuid))
                .findFirst().orElse(null);
    }
}