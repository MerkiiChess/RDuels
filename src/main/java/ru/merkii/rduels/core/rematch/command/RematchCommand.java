package ru.merkii.rduels.core.rematch.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelRequest;
import ru.merkii.rduels.core.rematch.bucket.RematchBucket;
import ru.merkii.rduels.core.world.WorldRestrictionService;

@Singleton
public class RematchCommand {

    private final RematchBucket rematchBucket;
    private final DuelAPI duelAPI;
    private final ArenaAPI arenaAPI;
    private final MessageConfig messages;
    private final WorldRestrictionService worldRestrictionService;

    @Inject
    public RematchCommand(RematchBucket rematchBucket, DuelAPI duelAPI, ArenaAPI arenaAPI, MessageConfig messages,
                          WorldRestrictionService worldRestrictionService) {
        this.rematchBucket = rematchBucket;
        this.duelAPI = duelAPI;
        this.arenaAPI = arenaAPI;
        this.messages = messages;
        this.worldRestrictionService = worldRestrictionService;
    }

    @Command({"rematch", "again"})
    public void onRematch(Player bukkitPlayer) {
        if (worldRestrictionService.denyIfDisabled(bukkitPlayer)) {
            return;
        }
        DuelPlayer sender = BukkitAdapter.adapt(bukkitPlayer);
        var rematch = rematchBucket.get(sender.getUUID()).orElse(null);
        if (rematch == null) {
            messages.sendTo(sender, "rematch-none");
            return;
        }
        DuelPlayer opponent = BukkitAdapter.getPlayer(rematch.opponent());
        if (opponent == null) {
            messages.sendTo(sender, "rematch-offline");
            return;
        }
        if (duelAPI.isFightPlayer(sender) || duelAPI.isFightPlayer(opponent)) {
            messages.sendTo(sender, Placeholder.wrapped("(player)", opponent.getName()), "duel-already-fight");
            return;
        }
        ArenaModel arena = arenaAPI.getFreeArena();
        if (arena == null) {
            messages.sendTo(sender, "duel-arenas-full");
            return;
        }
        // Consume the stored rematch so it isn't reused after the request is sent.
        rematchBucket.clear(sender.getUUID());

        DuelRequest request = DuelRequest.create(sender, opponent);
        request.setKitModel(rematch.kitModel());
        request.setDuelKit(DuelKitType.SERVER);
        request.setNumGames(1);
        request.setArena(arena);
        duelAPI.addRequest(request);
    }

}
