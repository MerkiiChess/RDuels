package ru.merkii.rduels.core.duel.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.core.party.model.PartyModel;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.model.UserModel;
import ru.merkii.rduels.util.CommandUtil;
import ru.merkii.rduels.util.PlayerUtil;

import java.util.Objects;
import java.util.UUID;

public class DuelListener implements Listener {

    private final RDuels plugin = RDuels.getInstance();
    private final MessageConfiguration messageConfiguration = plugin.getPluginMessage();
    private final DuelCore duelCore = DuelCore.INSTANCE;
    private final DuelAPI duelAPI = duelCore.getDuelAPI();
    private final DatabaseManager databaseManager = RDuels.getInstance().getDatabaseManager();


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!this.duelAPI.isFightPlayer(player)) {
            return;
        }
        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (fightModel == null) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, player.spigot()::respawn, 1L);
        UUID playerUUID = player.getUniqueId();
        Player killer = getPlayer(player, fightModel, playerUUID);
        if (!this.databaseManager.isTableExists(player).join()) {
            this.databaseManager.insert(UserModel.create(player.getUniqueId().toString(), 0, 0, 0, 0));
        }
        if (!this.databaseManager.isTableExists(killer).join()) {
            this.databaseManager.insert(UserModel.create(killer.getUniqueId().toString(), 0, 0,0 ,0));
        }
        this.databaseManager.addKill(killer).join();
        this.databaseManager.addDeath(player).join();
        player.teleport(killer);
        player.setGameMode(GameMode.SPECTATOR);
        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                onParty(player, fightModel);
                return;
            }
            onFour(player, fightModel);
            return;
        }
        this.databaseManager.addAllRound(player);
        this.databaseManager.addWinRound(killer);
        this.databaseManager.addAllRound(killer);
        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);
        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            Player winner = this.duelAPI.getWinnerFromFight(fightModel, player);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> Bukkit.getScheduler().runTask(this.plugin, () -> this.duelAPI.stopFight(fightModel, winner, player)), this.plugin.getSettings().getStopFightTime() * 20L);
            return;
        }
        String message = this.messageConfiguration.getMessage("duelNextRound").replace("(player)", killer.getName()).replace("(round)", String.valueOf(fightModel.getCountNumGames())).replace("(rounds)", String.valueOf(fightModel.getNumGames()));
        PlayerUtil.sendMessage(message, player, killer);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), this.plugin.getSettings().getNextRoundTime() * 20L);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        String worldName = player.getWorld().getName();
        if (this.plugin.getSettings().getSpawns().stream().anyMatch(entityPosition -> entityPosition.getWorldName().equalsIgnoreCase(worldName)) && !this.duelAPI.isFightPlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        DuelFightModel fightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (fightModel == null) {
            return;
        }

        // Проверка 2 на 2 чтоб не было урона
        if (fightModel.getPlayer2() != null && fightModel.getPlayer4() != null) {
            if ((player.equals(fightModel.getSender()) && damager.equals(fightModel.getPlayer2())) || (player.equals(fightModel.getPlayer2()) && damager.equals(fightModel.getSender()))) {
                event.setCancelled(true);
                return;
            }
            if ((player.equals(fightModel.getReceiver()) && damager.equals(fightModel.getPlayer4())) || (player.equals(fightModel.getPlayer4()) && damager.equals(fightModel.getReceiver()))) {
                event.setCancelled(true);
                return;
            }
            return;
        }

        // Проверка чтоб в пати не было урона
        if (fightModel.getSenderParty() != null && fightModel.getReceiverParty() != null) {
            UUID playerUUID = player.getUniqueId();
            UUID damagerUUID = damager.getUniqueId();
            if ((fightModel.getSenderParty().getOwner().equals(playerUUID) || fightModel.getSenderParty().getPlayers().contains(playerUUID)) && (fightModel.getSenderParty().getOwner().equals(damagerUUID) || fightModel.getSenderParty().getPlayers().contains(damagerUUID))) {
                event.setCancelled(true);
                return;
            }
            if ((fightModel.getReceiverParty().getOwner().equals(playerUUID) || (fightModel.getReceiverParty().getPlayers().contains(playerUUID))) && (fightModel.getReceiverParty().getOwner().equals(damagerUUID) || fightModel.getReceiverParty().getPlayers().contains(damagerUUID))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @NotNull
    private Player getPlayer(Player player, DuelFightModel fightModel, UUID playerUUID) {
        Player killer = player.getKiller();
        if (killer == null) {
            if (fightModel.getArenaModel().isFfa()) {
                if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                    if (fightModel.getReceiver().getUniqueId().equals(player.getUniqueId()) || (!Objects.requireNonNull(fightModel.getReceiverParty()).getPlayers().isEmpty() && fightModel.getReceiverParty().getPlayers().contains(playerUUID))) {
                        killer = fightModel.getSender();
                    } else {
                        killer = fightModel.getReceiver();
                    }
                }
                if (fightModel.getPlayer2() != null && fightModel.getPlayer4() != null) {
                    if (fightModel.getReceiver().equals(player) || fightModel.getPlayer4().equals(player)) {
                        killer = fightModel.getSender();
                    } else {
                        killer = fightModel.getReceiver();
                    }
                }
            } else if (fightModel.getReceiver().equals(player)) {
                killer = fightModel.getSender();
            } else {
                killer = fightModel.getReceiver();
            }

        }
        assert killer != null;
        return killer;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.duelAPI.isNoMovePlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.duelAPI.isNoMovePlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUsagePearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.duelAPI.isNoMovePlayer(player)) {
            PlayerInventory playerInventory = player.getInventory();
            Material itemMainHand = playerInventory.getItemInMainHand().getType();
            Material itemOffHand = playerInventory.getItemInOffHand().getType();
            if (itemMainHand == Material.ENDER_PEARL || itemMainHand == Material.BOW || itemMainHand == Material.SPLASH_POTION || itemMainHand == Material.LINGERING_POTION
            || itemOffHand == Material.ENDER_PEARL || itemOffHand == Material.BOW || itemOffHand == Material.SPLASH_POTION || itemOffHand == Material.LINGERING_POTION) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = CommandUtil.getOriginalCommand(event.getMessage());
        if (this.duelAPI.isFightPlayer(event.getPlayer()) && this.plugin.getSettings().getBlackCommandsSpectator().stream().anyMatch(command::equalsIgnoreCase)) {
            event.getPlayer().sendMessage(this.plugin.getPluginMessage().getMessage("duelCommandIsBlocked"));
            event.setCancelled(true);
            return;
        }
        if (this.duelAPI.isSpectate(event.getPlayer()) && this.plugin.getSettings().getBlackCommandsSpectator().stream().anyMatch(command::equalsIgnoreCase)) {
            event.getPlayer().sendMessage(this.plugin.getPluginMessage().getMessage("duelCommandIsBlocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.duelAPI.isFightPlayer(player)) {
            Item item = event.getItemDrop();
            Bukkit.getScheduler().runTaskLater(this.plugin, item::remove, this.duelCore.getDuelConfig().getItemRemoveSeconds() * 20L);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.duelAPI.isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (duelFightModel == null) {
            return;
        }
        this.duelAPI.stopFight(duelFightModel, this.duelAPI.getOpponentFromFight(duelFightModel, player), player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (!this.duelAPI.isFightPlayer(player)) {
            return;
        }
        DuelFightModel duelFightModel = this.duelAPI.getFightModelFromPlayer(player);
        if (duelFightModel == null) {
            return;
        }
        this.duelAPI.stopFight(duelFightModel, this.duelAPI.getOpponentFromFight(duelFightModel, player), player);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (this.duelAPI.isFightPlayer(player)) {
            event.setCancelled(true);
        }
    }

    private void onParty(Player player, DuelFightModel fightModel) {
        UUID playerUUID = player.getUniqueId();
        boolean alive = false;
        assert fightModel.getReceiverParty() != null;
        PartyModel deadParty = fightModel.getReceiverParty().getPlayers().contains(playerUUID) || fightModel.getReceiverParty().getOwner().equals(playerUUID) ? fightModel.getReceiverParty() : fightModel.getSenderParty();
        assert deadParty != null;
        PartyModel killerParty = deadParty.getOwner().equals(fightModel.getReceiverParty().getOwner()) ? fightModel.getSenderParty() : fightModel.getReceiverParty();
        if (!deadParty.getPlayers().isEmpty()) {
            for (Player playerParty : PlayerUtil.convertListUUID(deadParty.getPlayers())) {
                if (!playerParty.isDead() || playerParty.getGameMode() == GameMode.SURVIVAL) alive = true;
            }
        }
        if (!Objects.requireNonNull(Bukkit.getPlayer(deadParty.getOwner())).isDead() || Objects.requireNonNull(Bukkit.getPlayer(deadParty.getOwner())).getGameMode() == GameMode.SURVIVAL) {
            alive = true;
        }
        assert killerParty != null;
        if (alive) {
            return;
        }
        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);
        Player winnerOwner = Bukkit.getPlayer(killerParty.getOwner());
        Player loseOwner = Bukkit.getPlayer(deadParty.getOwner());

        // Добавляем в бд записи с выигрешем

        if (!killerParty.getPlayers().isEmpty()) {
            for (Player killerPartyPlayer : PlayerUtil.convertListUUID(killerParty.getPlayers())) {
                if (!this.databaseManager.isTableExists(killerPartyPlayer).join()) {
                    this.databaseManager.insert(UserModel.create(killerPartyPlayer.getUniqueId().toString(), 1, 1, 0, 0));
                    continue;
                }
                this.databaseManager.addWinRound(killerPartyPlayer).join();
                this.databaseManager.addAllRound(killerPartyPlayer).join();
            }
        }

        // Добавляем в бд запиши с проигрышем

        if (!deadParty.getPlayers().isEmpty()) {
            for (Player deadPartyPlayer : PlayerUtil.convertListUUID(deadParty.getPlayers())) {
                if (!this.databaseManager.isTableExists(deadPartyPlayer).join()) {
                    this.databaseManager.insert(UserModel.create(deadPartyPlayer.getUniqueId().toString(), 0, 1, 0, 0));
                    continue;
                }
                this.databaseManager.addAllRound(deadPartyPlayer);
            }
        }

        // Если достигаем количества игр, то заканчиваем дуэль, в ином случае мы начинаем следующий раунд

        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            this.duelAPI.stopFight(fightModel, winnerOwner, loseOwner);
            return;
        }

        // Сообщение о следующем раунде

        assert winnerOwner != null;
        String message = this.messageConfiguration.getMessage("duelNextRound").replace("(player)", winnerOwner.getName()).replace("(round)", String.valueOf(fightModel.getCountNumGames())).replace("(rounds)", String.valueOf(fightModel.getNumGames()));
        PlayerUtil.sendMessage(message, winnerOwner, loseOwner);
        if (!deadParty.getPlayers().isEmpty()) {
            PlayerUtil.convertListUUID(deadParty.getPlayers()).forEach(players -> players.sendMessage(message));
        }
        if (!killerParty.getPlayers().isEmpty()) {
            PlayerUtil.convertListUUID(killerParty.getPlayers()).forEach(players -> players.sendMessage(message));
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), this.plugin.getSettings().getNextRoundTime() * 20L);
    }

    private void onFour(Player player, DuelFightModel fightModel) {
        if (isAlive(player, fightModel)) {
            return;
        }
        fightModel.setCountNumGames(fightModel.getCountNumGames() + 1);
        Player winner = getWinner(player, fightModel);
        Player loser = getLoser(player, fightModel);

        this.databaseManager.addWinRound(winner).join();
        this.databaseManager.addAllRound(winner).join();
        this.databaseManager.addAllRound(fightModel.getPlayer2()).join();
        this.databaseManager.addAllRound(fightModel.getPlayer4()).join();
        if (winner.equals(fightModel.getSender())) {
            this.databaseManager.addWinRound(fightModel.getPlayer2()).join();
        } else {
            this.databaseManager.addWinRound(fightModel.getPlayer4()).join();
        }

        if (fightModel.getCountNumGames() == fightModel.getNumGames()) {
            this.duelAPI.stopFight(fightModel, winner, loser);
            return;
        }
        String message = this.messageConfiguration.getMessage("duelNextRound").replace("(player)", winner.getName()).replace("(round)", String.valueOf(fightModel.getCountNumGames())).replace("(rounds)", String.valueOf(fightModel.getNumGames()));
        PlayerUtil.sendMessage(message, winner, loser, fightModel.getPlayer2(), fightModel.getPlayer4());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.duelAPI.nextRound(fightModel), this.plugin.getSettings().getNextRoundTime() * 20L);
    }

    private boolean isAlive(Player player, DuelFightModel fightModel) {
        boolean alive = true;
        if (player.equals(fightModel.getSender()) || player.equals(fightModel.getPlayer2())) {
            if (fightModel.getSender().isDead() || fightModel.getSender().getGameMode() == GameMode.SPECTATOR){
                alive = false;
            }
            if (fightModel.getPlayer2().isDead() || fightModel.getPlayer2().getGameMode() == GameMode.SURVIVAL) {
                alive = false;
            }
        } else {
            if (fightModel.getReceiver().isDead() || fightModel.getReceiver().getGameMode() == GameMode.SURVIVAL) {
                alive = false;
            }
            if (fightModel.getPlayer4().isDead() || fightModel.getPlayer4().getGameMode() == GameMode.SPECTATOR){
                alive = false;
            }
        }
        this.plugin.getLogger().info(player.getName() + ": Alive: " + alive + ", Dead: " + player.isDead() + ", GameMode: " + player.getGameMode());
        return alive;
    }

    @NotNull
    private Player getWinner(Player player, DuelFightModel duelFightModel) {
        Player player1 = null;
        if (player.equals(duelFightModel.getSender()) || player.equals(duelFightModel.getPlayer2())) player1 = duelFightModel.getReceiver();
        if (player.equals(duelFightModel.getReceiver()) || player.equals(duelFightModel.getPlayer4())) player1 = duelFightModel.getSender();
        assert player1 != null;
        return player1;
    }

    @NotNull
    private Player getLoser(Player player, DuelFightModel duelFightModel) {
        Player player1 = null;
        if (player.equals(duelFightModel.getSender()) || player.equals(duelFightModel.getPlayer2())) player1 = duelFightModel.getSender();
        if (player.equals(duelFightModel.getReceiver()) || player.equals(duelFightModel.getPlayer4())) player1 = duelFightModel.getReceiver();
        assert player1 != null;
        return player1;
    }

}