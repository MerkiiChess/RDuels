package ru.merkii.rduels.core.duel.preparation;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.duel.spectator.DuelSpectatorService;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class PlayerPreparationServiceImpl implements PlayerPreparationService {

    private final SettingsConfiguration settings;
    @Inject
    public DuelSpectatorService spectatorService;

    @Inject
    public PlayerPreparationServiceImpl(SettingsConfiguration settings) {
        this.settings = settings;
    }

    @Override
    public void preparationToFight(List<DuelPlayer> players) {
        players.forEach(p -> {
            if (p == null) return;
            if (spectatorService.isSpectate(p)) {
                spectatorService.removeSpectate(p, spectatorService.getDuelFightModelFromSpectator(p), true);
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.setFireTicks(0);
            Player bukkitPlayer = Bukkit.getPlayer(p.getUUID());
            PlayerUtil.clearEffects(bukkitPlayer);
            PlayerUtil.healPlayers(bukkitPlayer);
        });
    }

    @Override
    public void preparationToFight(DuelPlayer... players) {
        preparationToFight(Arrays.asList(players));
    }

    @Override
    public void preparationToFight(PartyModel s, PartyModel r) {
        preparationToFight(PlayerUtil.duelPlayersConvertListUUID(s.getPlayers()));
        preparationToFight(PlayerUtil.duelPlayersConvertListUUID(r.getPlayers()));
    }

    @Override
    public void giveStartItems(DuelPlayer player) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Player with UUID: " + player.getUUID() + " is not online!");
        }
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(null);
        if (settings.itemOpenCustomKit()) {
            bukkitPlayer.getInventory().setItem(settings.createCustomKit().slot(), settings.createCustomKit().build());
        }
        bukkitPlayer.updateInventory();
    }

    @Override
    public EntityPosition getRandomSpawn() {
        List<EntityPosition> spawns = settings.spawns();
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
    }
}
