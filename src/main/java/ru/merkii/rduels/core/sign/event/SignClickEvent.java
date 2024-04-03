package ru.merkii.rduels.core.sign.event;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.sign.model.SignModel;

public class SignClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Block block;
    private final Sign sign;
    private final SignModel signModel;
    private final boolean isLeftClick;
    private final boolean isRightClick;
    private boolean cancelled;

    public SignClickEvent(Player player, Block block, Sign sign, SignModel signModel, boolean isLeftClick, boolean isRightClick) {
        this.player = player;
        this.block = block;
        this.sign = sign;
        this.signModel = signModel;
        this.cancelled = false;
        this.isLeftClick = isLeftClick;
        this.isRightClick = isRightClick;
    }


    public void call() {
        RDuels.getInstance().getServer().getPluginManager().callEvent(this);
    }

    public static SignClickEvent create(Player player, Block block, Sign sign, SignModel signModel, boolean isLeftClick, boolean isRightClick) {
        return new SignClickEvent(player, block, sign, signModel, isLeftClick, isRightClick);
    }

    public Player getPlayer() {
        return player;
    }

    public SignModel getSignModel() {
        return signModel;
    }

    public boolean isLeftClick() {
        return isLeftClick;
    }

    public boolean isRightClick() {
        return isRightClick;
    }

    public Block getBlock() {
        return block;
    }

    public Sign getSign() {
        return sign;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
