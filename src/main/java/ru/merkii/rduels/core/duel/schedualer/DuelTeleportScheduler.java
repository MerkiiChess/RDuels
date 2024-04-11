package ru.merkii.rduels.core.duel.schedualer;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfig;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.util.ColorUtil;
import ru.merkii.rduels.util.PlayerUtil;

public class DuelTeleportScheduler extends BukkitRunnable {

    private final DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();
    @Getter
    private final DuelFightModel duelFightModel;
    private int time;

    public DuelTeleportScheduler(DuelFightModel fightModel) {
        this.duelFightModel = fightModel;
        this.time = 5;
        this.duelAPI.addNoMove(fightModel.getReceiver());
        this.duelAPI.addNoMove(fightModel.getSender());
        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                PlayerUtil.convertListUUID(fightModel.getReceiverParty().getPlayers()).forEach(this.duelAPI::addNoMove);
                PlayerUtil.convertListUUID(fightModel.getSenderParty().getPlayers()).forEach(this.duelAPI::addNoMove);
            } else {
                this.duelAPI.addNoMove(fightModel.getPlayer2());
                this.duelAPI.addNoMove(fightModel.getPlayer4());
            }
        }
        runTaskTimer(RDuels.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        --this.time;
        Player receiver = this.duelFightModel.getReceiver();
        Player sender = this.duelFightModel.getSender();
        DuelConfig.TitleSettings titleSettings = DuelCore.INSTANCE.getDuelConfig().getTitleSettings();
        if (this.time != 1) {
            String text = ColorUtil.color(titleSettings.getToFight().getText()).replace("(time)", String.valueOf(this.time));
            receiver.sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut());
            sender.sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut());
            if (this.duelFightModel.getArenaModel().isFfa()) {
                if (this.duelFightModel.getSenderParty() != null && this.duelFightModel.getReceiverParty() != null) {
                    PlayerUtil.convertListUUID(this.duelFightModel.getReceiverParty().getPlayers()).forEach(player -> player.sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut()));
                    PlayerUtil.convertListUUID(this.duelFightModel.getSenderParty().getPlayers()).forEach(player -> player.sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut()));
                } else if (this.duelFightModel.getPlayer2() != null && this.duelFightModel.getPlayer4() != null) {
                    this.duelFightModel.getPlayer4().sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut());
                    this.duelFightModel.getPlayer2().sendTitle(text, "", titleSettings.getToFight().getFadeIn(), titleSettings.getToFight().getStay(), titleSettings.getToFight().getFadeOut());
                }
            }
            Sound sound = Sound.valueOf(titleSettings.getToFight().getSoundName());
            if (sound == null) {
                RDuels.getInstance().debug("Sound " + titleSettings.getToFight().getSoundName() + " not found!");
                return;
            }
            receiver.playSound(receiver.getLocation(), sound, titleSettings.getToFight().getV1(), titleSettings.getToFight().getV2());
            sender.playSound(sender.getLocation(), sound, titleSettings.getToFight().getV1(), titleSettings.getToFight().getV2());
            return;
        }
        Sound sound = Sound.valueOf(titleSettings.getFight().getSoundName());
        if (sound == null) {
            RDuels.getInstance().debug("Sound " + titleSettings.getFight().getSoundName() + " not found!");
        } else {
            receiver.playSound(receiver.getLocation(), sound, titleSettings.getFight().getV1(), titleSettings.getFight().getV2());
            sender.playSound(sender.getLocation(), sound, titleSettings.getFight().getV1(), titleSettings.getFight().getV2());
        }
        receiver.sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
        sender.sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
        this.duelAPI.removeNoMove(receiver);
        this.duelAPI.removeNoMove(sender);
        if (this.duelFightModel.getArenaModel().isFfa()) {
            if (this.duelFightModel.getReceiverParty() != null && this.duelFightModel.getSenderParty() != null) {
                PlayerUtil.convertListUUID(this.duelFightModel.getReceiverParty().getPlayers()).forEach(player -> {
                    this.duelAPI.removeNoMove(player);
                    player.sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
                });
                PlayerUtil.convertListUUID(this.duelFightModel.getSenderParty().getPlayers()).forEach(player -> {
                    this.duelAPI.removeNoMove(player);
                    player.sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
                });
            } else {
                this.duelFightModel.getPlayer2().sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
                this.duelFightModel.getPlayer4().sendTitle(ColorUtil.color(titleSettings.getFight().getText()), "", titleSettings.getFight().getFadeIn(), titleSettings.getFight().getStay(), titleSettings.getFight().getFadeOut());
                this.duelAPI.removeNoMove(this.duelFightModel.getPlayer2());
                this.duelAPI.removeNoMove(this.duelFightModel.getPlayer4());
            }
        }
        this.cancel();
    }
}
