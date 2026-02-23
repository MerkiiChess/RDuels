package ru.merkii.rduels.core.party.listener;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.menu.PartyFightMenu;

@Singleton
public class PartyListener implements Listener {

    private final PartyAPI partyAPI;
    private final SettingsConfiguration settings;

    @Inject
    public PartyListener(PartyAPI partyAPI, SettingsConfiguration settings) {
        this.partyAPI = partyAPI;
        this.settings = settings;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player bukkitPlayer = event.getPlayer();
        DuelPlayer player = BukkitAdapter.adapt(bukkitPlayer);
        if (!player.isPartyExists()) {
            return;
        }
        PlayerInventory inventory = bukkitPlayer.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        if (mainHand.equals(this.settings.fightParty().build()) || offHand.equals(this.settings.fightParty().build())) {
            new PartyFightMenu().open(bukkitPlayer);
            event.setCancelled(true);
            return;
        }
        if (mainHand.equals(this.settings.leaveParty().build()) || offHand.equals(this.settings.leaveParty().build())) {
            this.partyAPI.leaveParty(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.partyAPI.leaveParty(BukkitAdapter.adapt(event.getPlayer()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.partyAPI.leaveParty(BukkitAdapter.adapt(event.getPlayer()));
    }

}
