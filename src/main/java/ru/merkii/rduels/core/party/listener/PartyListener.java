package ru.merkii.rduels.core.party.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.core.party.menu.PartyFightMenu;

public class PartyListener implements Listener {

    private final RDuels plugin = RDuels.getInstance();
    private final PartyAPI partyAPI = PartyCore.INSTANCE.getPartyAPI();
    private final Settings settings = plugin.getSettings();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!this.partyAPI.isPartyPlayer(player)) {
            return;
        }
        event.setCancelled(true);
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        if (mainHand.equals(this.settings.getFightParty().build()) || offHand.equals(this.settings.getFightParty().build())) {
            new PartyFightMenu().open(player);
            return;
        }
        if (mainHand.equals(this.settings.getLeaveParty().build()) || offHand.equals(this.settings.getLeaveParty().build())) {
            this.partyAPI.leaveParty(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.partyAPI.leaveParty(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.partyAPI.leaveParty(event.getPlayer());
    }

}
