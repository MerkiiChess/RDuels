package ru.merkii.rduels.core.duel.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.GameMode;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.duel.event.DuelKillPlayerEvent;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.util.CommandUtil;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.*;
import static ru.merkii.rduels.config.Placeholder.Placeholders;

@Singleton
public class DuelListener implements Listener {

    private final RDuels plugin;
    private final MessageConfig messageConfig;
    private final DuelAPI duelAPI;
    private final SettingsConfiguration config;
    private final DuelConfiguration duelConfiguration;
    private final SettingsConfiguration settingsConfiguration;

    @Inject
    public DuelListener(
            RDuels plugin,
            MessageConfig messageConfig,
            DuelAPI duelAPI,
            SettingsConfiguration config,
            DuelConfiguration duelConfiguration,
            SettingsConfiguration settingsConfiguration) {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.duelAPI = duelAPI;
        this.config = config;
        this.duelConfiguration = duelConfiguration;
        this.settingsConfiguration = settingsConfiguration;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().name().contains("BED")) {
            return;
        }
        DuelPlayer player = BukkitAdapter.adapt(event.getPlayer());
        if (player.isFight()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        DuelPlayer player = BukkitAdapter.adapt(event.getEntity());
        if (!player.isFight()) {
            return;
        }
        Optional<DuelFightModel> fightModelOptional = player.getDuelFightModel();
        if (fightModelOptional.isEmpty() || fightModelOptional.get().isEnd()) {
            event.setCancelled(true);
            return;
        }
        fightModelOptional.ifPresent(fightModel -> {
            DuelPlayer killer = getKiller(player, fightModel);
            DuelKillPlayerEvent duelKillPlayerEvent = new DuelKillPlayerEvent(killer, player);
            duelKillPlayerEvent.call();
            if (duelKillPlayerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onKillPlayer(DuelKillPlayerEvent event) {
        DuelPlayer victimPlayer = event.getVictim();
        DuelPlayer killerPlayer = event.getKiller();
        DuelFightModel fightModel = event.getDuelFightModel();

        victimPlayer.respawnPlayer();
        killerPlayer.addKill();
        victimPlayer.addDeath();
        victimPlayer.setGameMode(ru.merkii.rduels.adapter.bukkit.GameMode.SPECTATOR);
        victimPlayer.teleport(killerPlayer);

        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                handlePartyKill(victimPlayer, fightModel);
            } else {
                handleFourKill(victimPlayer, fightModel, killerPlayer);
            }
            return;
        }

        victimPlayer.addAllRound();
        killerPlayer.addWinRound();
        killerPlayer.addAllRound();
        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);

        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            DuelPlayer winner = this.duelAPI.getWinnerFromFight(fightModel, victimPlayer);
            fightModel.setEnd(true);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.stopFight(fightModel, winner, victimPlayer), (long) config.stopFightTime()  * 20L);
            return;
        }

        Placeholders placeholder = Placeholders.of(
                Placeholder.of("(player)", killerPlayer.getName()),
                Placeholder.of("(round)", String.valueOf(fightModel.getCountNumGames())),
                Placeholder.of("(rounds)", String.valueOf(fightModel.getNumGames()))
        );

        Component message = messageConfig.message(placeholder, "duel-next-round");
        sendTo(Arrays.asList(victimPlayer, killerPlayer), message);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), (long) config.nextRoundTime() * 20L);
    }

    private void handlePartyKill(DuelPlayer victim, DuelFightModel fightModel) {
        PartyModel deadParty = determineParty(victim, fightModel);
        PartyModel winnerParty = determineOpponentParty(deadParty, fightModel);

        if (isPartyAlive(deadParty)) {
            return;
        }

        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);

        updatePartyStats(winnerParty, true);
        updatePartyStats(deadParty, false);

        DuelPlayer winnerOwner = BukkitAdapter.getPlayer(winnerParty.getOwner());
        DuelPlayer loserOwner = BukkitAdapter.getPlayer(deadParty.getOwner());

        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.stopFight(fightModel, winnerOwner, loserOwner), (long) config.stopFightTime() * 20L);
            return;
        }

        Placeholders placeholder = Placeholders.of(
                Placeholder.of("(player)", winnerOwner.getName()),
                Placeholder.of("(round)", String.valueOf(fightModel.getCountNumGames())),
                Placeholder.of("(rounds)", String.valueOf(fightModel.getNumGames()))
        );
        Component message = messageConfig.message(placeholder, "duel-next-round");

        List<DuelPlayer> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPartyPlayers(winnerParty));
        allPlayers.addAll(getPartyPlayers(deadParty));

        sendTo(allPlayers, message);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), (long) config.nextRoundTime() * 20L);
    }

    private void handleFourKill(DuelPlayer victim, DuelFightModel fightModel, DuelPlayer killer) {
        if (isTeamAlive(victim, fightModel)) {
            return;
        }

        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);

        DuelPlayer winner = getWinnerFour(victim, fightModel);
        DuelPlayer loser = getLoserFour(victim, fightModel);

        updatePlayerStats(winner, true);
        updatePlayerStats(getTeammate(winner, fightModel), true);
        updatePlayerStats(loser, false);
        updatePlayerStats(getTeammate(loser, fightModel), false);

        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.stopFight(fightModel, winner, loser), (long) config.stopFightTime() * 20L);
            return;
        }

        Placeholders placeholder = Placeholders.of(
                Placeholder.of("(player)", killer.getName()),
                Placeholder.of("(round)", String.valueOf(fightModel.getCountNumGames())),
                Placeholder.of("(rounds)", String.valueOf(fightModel.getNumGames()))
        );
        Component message = messageConfig.message(placeholder, "duel-next-round");
        sendTo(Arrays.asList(winner, getTeammate(winner, fightModel), loser, getTeammate(loser, fightModel)), message);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), (long) config.nextRoundTime() * 20L);
    }

    private DuelPlayer getKiller(DuelPlayer victim, DuelFightModel fightModel) {
        DuelPlayer killer = victim.getKiller();
        if (killer != null) {
            return killer;
        }

        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                PartyModel victimParty = determineParty(victim, fightModel);
                return BukkitAdapter.getPlayer(determineOpponentParty(victimParty, fightModel).getOwner());
            } else if (fightModel.getPlayer2() != null && fightModel.getPlayer4() != null) {
                return isInSenderTeam(victim, fightModel) ? fightModel.getReceiver() : fightModel.getSender();
            }
        } else {
            return duelAPI.getOpponentFromFight(fightModel, victim);
        }

        return duelAPI.getOpponentFromFight(fightModel, victim);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (isInSpawnWorld(player) && !duelPlayer.isFight()) {
            event.setCancelled(true);
        }
    }

    private boolean isInSpawnWorld(Player player) {
        String worldName = player.getWorld().getName();
        return settingsConfiguration.spawns().stream()
                .anyMatch(position -> position.getWorldName().equalsIgnoreCase(worldName));
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victimPlayer) || !(event.getDamager() instanceof Player damagerPlayer)) {
            return;
        }
        DuelPlayer victim = BukkitAdapter.adapt(victimPlayer);
        DuelPlayer damager = BukkitAdapter.adapt(damagerPlayer);
        victim.getDuelFightModel().ifPresent(fightModel -> {
            if (isSameTeam(damager, victim, fightModel)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (this.duelAPI.isNoMovePlayer(duelPlayer)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.duelAPI.isNoMovePlayer(BukkitAdapter.adapt(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUsagePearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (!this.duelAPI.isNoMovePlayer(duelPlayer)) {
            return;
        }
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        if (isRestrictedItem(mainHand) || isRestrictedItem(offHand)) {
            event.setCancelled(true);
        }
    }

    private boolean isRestrictedItem(Material material) {
        return material == Material.ENDER_PEARL || material == Material.BOW ||
                material == Material.SPLASH_POTION || material == Material.LINGERING_POTION;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = CommandUtil.getOriginalCommand(event.getMessage()).toLowerCase();
        List<String> blackCommands = settingsConfiguration.blackCommands().stream()
                .map(String::toLowerCase)
                .toList();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if ((duelPlayer.isFight() || this.duelAPI.isSpectate(duelPlayer)) &&
                blackCommands.contains(command)) {
            messageConfig.sendTo(player, "duel-command-is-blocked");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (duelPlayer.isFight()) {
            Item item = event.getItemDrop();
            Bukkit.getScheduler().runTaskLater(this.plugin, item::remove, (long) duelConfiguration.itemRemoveSeconds() * 20L);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        handlePlayerDisconnect(BukkitAdapter.adapt(event.getPlayer()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handlePlayerDisconnect(BukkitAdapter.adapt(event.getPlayer()));
    }

    private void handlePlayerDisconnect(DuelPlayer player) {
        player.getDuelFightModel().ifPresent(fightModel -> {
            updateStatsOnDisconnect(player);
            if (!fightModel.getArenaModel().isFfa()) {
                this.duelAPI.stopFight(fightModel, this.duelAPI.getOpponentFromFight(fightModel, player), player);
                return;
            }
            if (!isTeamAlive(player, fightModel)) {
                this.duelAPI.stopFight(fightModel, getOpponentOwner(player, fightModel), getPlayerOwner(player, fightModel));
            }
        });
    }

    private void updateStatsOnDisconnect(DuelPlayer player) {
        player.addDeath();
        player.addAllRound();
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (duelPlayer.isFight()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player damagerPlayer) || !(event.getHitEntity() instanceof Player victimPlayer)) {
            return;
        }
        DuelPlayer victim = BukkitAdapter.adapt(victimPlayer);
        DuelPlayer damager = BukkitAdapter.adapt(damagerPlayer);
        victim.getDuelFightModel().ifPresent(fightModel -> {
            if (isSameTeam(damager, victim, fightModel)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player damagerPlayer)) {
            return;
        }
        DuelPlayer damager = BukkitAdapter.adapt(damagerPlayer);
        damager.getDuelFightModel().ifPresent(fightModel -> {
            boolean cancel = event.getAffectedEntities().stream()
                    .filter(entity -> entity instanceof Player)
                    .map(Player.class::cast)
                    .map(BukkitAdapter::adapt)
                    .anyMatch(player -> isSameTeam(damager, player, fightModel));

            if (cancel) {
                event.setCancelled(true);
            }
        });
    }

    private boolean isSameTeam(DuelPlayer player1, DuelPlayer player2, DuelFightModel fightModel) {
        UUID uuid1 = player1.getUUID();
        UUID uuid2 = player2.getUUID();

        if (fightModel.getPlayer2() != null && fightModel.getPlayer4() != null) {
            // 2v2
            boolean team1 = uuid1.equals(fightModel.getSender().getUUID()) || uuid1.equals(fightModel.getPlayer2().getUUID());
            boolean team2 = uuid1.equals(fightModel.getReceiver().getUUID()) || uuid1.equals(fightModel.getPlayer4().getUUID());
            return (team1 && (uuid2.equals(fightModel.getSender().getUUID()) || uuid2.equals(fightModel.getPlayer2().getUUID()))) ||
                    (team2 && (uuid2.equals(fightModel.getReceiver().getUUID()) || uuid2.equals(fightModel.getPlayer4().getUUID())));
        }

        if (fightModel.getSenderParty() != null && fightModel.getReceiverParty() != null) {
            // Party
            PartyModel party1 = fightModel.getSenderParty();
            PartyModel party2 = fightModel.getReceiverParty();

            boolean inParty1 = party1.getOwner().equals(uuid1) || party1.getPlayers().contains(uuid1);
            boolean inParty2 = party2.getOwner().equals(uuid1) || party2.getPlayers().contains(uuid1);

            return (inParty1 && (party1.getOwner().equals(uuid2) || party1.getPlayers().contains(uuid2))) ||
                    (inParty2 && (party2.getOwner().equals(uuid2) || party2.getPlayers().contains(uuid2)));
        }

        return false;
    }

    private PartyModel determineParty(DuelPlayer player, DuelFightModel fightModel) {
        UUID uuid = player.getUUID();
        if (fightModel.getReceiverParty() == null) {
            throw new IllegalStateException("Party not found!");
        }
        if (fightModel.getReceiverParty().getOwner().equals(uuid) || fightModel.getReceiverParty().getPlayers().contains(uuid)) {
            return fightModel.getReceiverParty();
        }
        return fightModel.getSenderParty();
    }

    private PartyModel determineOpponentParty(PartyModel party, DuelFightModel fightModel) {
        return party == fightModel.getSenderParty() ? fightModel.getReceiverParty() : fightModel.getSenderParty();
    }

    private boolean isPartyAlive(PartyModel party) {
        List<DuelPlayer> players = getPartyPlayers(party);
        return players.stream().anyMatch(p -> p.getGameMode() != GameMode.SPECTATOR);
    }

    private List<DuelPlayer> getPartyPlayers(PartyModel party) {
        List<DuelPlayer> players = new ArrayList<>(PlayerUtil.duelPlayersConvertListUUID(party.getPlayers()));
        DuelPlayer owner = BukkitAdapter.getPlayer(party.getOwner());
        players.add(owner);
        return players;
    }

    private void updatePartyStats(PartyModel party, boolean winRound) {
        getPartyPlayers(party).forEach(player -> updatePlayerStats(player, winRound));
    }

    private void updatePlayerStats(DuelPlayer player, boolean winRound) {
        if (winRound) {
            player.addWinRound();
        }
        player.addAllRound();
    }

    private boolean isTeamAlive(DuelPlayer player, DuelFightModel fightModel) {
        return getTeamPlayersFour(player, fightModel).stream()
                .anyMatch(p -> p != null && p.getGameMode() != ru.merkii.rduels.adapter.bukkit.GameMode.SPECTATOR);
    }

    private List<DuelPlayer> getTeamPlayersFour(DuelPlayer player, DuelFightModel fightModel) {
        if (isInSenderTeam(player, fightModel)) {
            return Arrays.asList(fightModel.getSender(), fightModel.getPlayer2());
        } else {
            return Arrays.asList(fightModel.getReceiver(), fightModel.getPlayer4());
        }
    }

    private boolean isInSenderTeam(DuelPlayer player, DuelFightModel fightModel) {
        return player.equals(fightModel.getSender()) || player.equals(fightModel.getPlayer2());
    }

    private DuelPlayer getWinnerFour(DuelPlayer loser, DuelFightModel fightModel) {
        return isInSenderTeam(loser, fightModel) ? fightModel.getReceiver() : fightModel.getSender();
    }

    private DuelPlayer getLoserFour(DuelPlayer loser, DuelFightModel fightModel) {
        return isInSenderTeam(loser, fightModel) ? fightModel.getSender() : fightModel.getReceiver();
    }

    private DuelPlayer getTeammate(DuelPlayer player, DuelFightModel fightModel) {
        if (player.equals(fightModel.getSender())) {
            return fightModel.getPlayer2();
        } else if (player.equals(fightModel.getPlayer2())) {
            return fightModel.getSender();
        } else if (player.equals(fightModel.getReceiver())) {
            return fightModel.getPlayer4();
        } else {
            return fightModel.getReceiver();
        }
    }

    private DuelPlayer getOpponentOwner(DuelPlayer player, DuelFightModel fightModel) {
        PartyModel party = determineParty(player, fightModel);
        PartyModel opponent = determineOpponentParty(party, fightModel);
        return BukkitAdapter.getPlayer(opponent.getOwner());
    }

    private DuelPlayer getPlayerOwner(DuelPlayer player, DuelFightModel fightModel) {
        PartyModel party = determineParty(player, fightModel);
        return BukkitAdapter.getPlayer(party.getOwner());
    }

    private void sendTo(List<DuelPlayer> players, Component message) {
        players.forEach(player -> player.sendMessage(message));
    }

}