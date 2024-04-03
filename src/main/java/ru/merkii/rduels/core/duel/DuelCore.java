package ru.merkii.rduels.core.duel;

import lombok.Getter;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.api.provider.DuelAPIProvider;
import ru.merkii.rduels.core.duel.bucket.DuelFightBucket;
import ru.merkii.rduels.core.duel.bucket.DuelRequestsBucket;
import ru.merkii.rduels.core.duel.command.DuelCommand;
import ru.merkii.rduels.core.duel.command.LeaveCommand;
import ru.merkii.rduels.core.duel.command.RDuelCommand;
import ru.merkii.rduels.core.duel.command.SpectatorCommand;
import ru.merkii.rduels.core.duel.config.DuelConfig;
import ru.merkii.rduels.core.duel.listener.DuelListener;

@Getter
public class DuelCore implements Core {

    public static DuelCore INSTANCE;
    private DuelAPI duelAPI;
    private DuelRequestsBucket duelRequestsBucket;
    private DuelFightBucket duelFightBucket;
    private DuelConfig duelConfig;

    @Override
    public void enable(RDuels plugin) {
        INSTANCE = this;
        reloadConfig(plugin);
        this.duelFightBucket = new DuelFightBucket();
        this.duelRequestsBucket = new DuelRequestsBucket();
        this.duelAPI = new DuelAPIProvider();
        plugin.registerListeners(new DuelListener());
        plugin.registerCommands(new DuelCommand(), new SpectatorCommand(), new RDuelCommand(), new LeaveCommand());
    }

    @Override
    public void disable(RDuels plugin) {
        plugin.getServer().getOnlinePlayers().stream().filter(player -> this.duelAPI.isFightPlayer(player)).forEach(player -> this.duelAPI.stopFight(this.duelAPI.getFightModelFromPlayer(player), null, null));
    }

    @Override
    public void reloadConfig(RDuels plugin) {
        this.duelConfig = plugin.loadSettings("duelConfig.json", DuelConfig.class);
    }
}
