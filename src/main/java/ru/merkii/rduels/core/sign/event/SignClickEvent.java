package ru.merkii.rduels.core.sign.event;

import lombok.Getter;
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
    @Getter
    private final Player player;
    @Getter
    private final Block block;
    @Getter
    private final Sign sign;
    @Getter
    private final SignModel signModel;
    @Getter
    private final boolean leftClick;
    @Getter
    private final boolean rightClick;
    private boolean cancelled;

    public SignClickEvent(Player player, Block block, Sign sign, SignModel signModel, boolean isLeftClick, boolean isRightClick) {
        this.player = player;
        this.block = block;
        this.sign = sign;
        this.signModel = signModel;
        this.cancelled = false;
        this.leftClick = isLeftClick;
        this.rightClick = isRightClick;
    }


    public void call() {
        RDuels.getInstance().getServer().getPluginManager().callEvent(this);
    }

    public static SignClickEvent create(Player player, Block block, Sign sign, SignModel signModel, boolean isLeftClick, boolean isRightClick) {
        return new SignClickEvent(player, block, sign, signModel, isLeftClick, isRightClick);
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
