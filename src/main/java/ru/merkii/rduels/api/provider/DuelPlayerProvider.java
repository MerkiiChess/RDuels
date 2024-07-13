package ru.merkii.rduels.api.provider;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.api.DuelPlayer;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;

public class DuelPlayerProvider implements DuelPlayer {

    private final Player player;

    public DuelPlayerProvider(Player player) {
        this.player = player;
    }

    @Override
    public @Nullable DuelFightModel getDuelFightModel() {
        return Duel.getDuelAPI().getFightModelFromPlayer(this.player);
    }

    @Override
    public @Nullable PartyModel getParty() {
        return Duel.getPartyAPI().getPartyModelFromPlayer(this.player);
    }

    @Override
    public boolean isPartyExists() {
        return Duel.getPartyAPI().isPartyPlayer(this.player);
    }

    @Override
    public boolean isFight() {
        return Duel.getDuelAPI().isFightPlayer(this.player);
    }

    @Override
    public boolean isQueue() {
        return Duel.getSignAPI().isQueuePlayer(this.player);
    }

    @Override
    public void addKill(int amount) {
        RDuels.getInstance().getDatabaseManager().addKill(this.player).join();
    }

    @Override
    public void addDeath(int amount) {
        RDuels.getInstance().getDatabaseManager().addDeath(this.player).join();
    }

    @Override
    public void addWin(int amount) {
        RDuels.getInstance().getDatabaseManager().addWinRound(this.player).join();
    }

    @Override
    public int getKills() {
        return RDuels.getInstance().getDatabaseManager().getKills(this.player).join();
    }

    @Override
    public int getDeath() {
        return RDuels.getInstance().getDatabaseManager().getDeaths(this.player).join();
    }

    @Override
    public int getWin() {
        return RDuels.getInstance().getDatabaseManager().getWinRounds(this.player).join();
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
