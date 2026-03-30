package ru.merkii.rduels.core.duel.preparation;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.duel.spectator.DuelSpectatorService;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.PlayerUtil;
import ru.merkii.rduels.util.PluginConsole;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerPreparationServiceImpl implements PlayerPreparationService {

    private final SettingsConfiguration settings;
    private volatile boolean missingSpawnsWarningLogged;
    @Inject
    public DuelSpectatorService spectatorService;

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
            PluginConsole.warn(RDuels.getInstance(), "Не удалось выдать стартовые предметы игроку " + player.getUUID() + ": игрок уже не в сети.");
            return;
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
        if (spawns == null || spawns.isEmpty()) {
            if (!missingSpawnsWarningLogged) {
                missingSpawnsWarningLogged = true;
                PluginConsole.warn(RDuels.getInstance(), "В settings.yml не настроены spawns. Используется spawn первого мира.");
            }
            if (Bukkit.getWorlds().isEmpty()) {
                return new EntityPosition("world", 0, 64, 0);
            }
            Location fallbackSpawn = Bukkit.getWorlds().getFirst().getSpawnLocation();
            return new EntityPosition(fallbackSpawn);
        }
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
    }
}
