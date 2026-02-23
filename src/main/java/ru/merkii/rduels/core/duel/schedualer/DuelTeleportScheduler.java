package ru.merkii.rduels.core.duel.schedualer;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.duel.config.TitleSettingsConfiguration;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.util.ColorUtil;
import ru.merkii.rduels.util.PlayerUtil;

public class DuelTeleportScheduler extends BukkitRunnable {

    private final DuelConfiguration duelConfiguration;
    private final TitleSettingsConfiguration titleSettings;
    private final DuelAPI duelAPI;
    @Getter
    private final DuelFightModel duelFightModel;
    private int time;

    public DuelTeleportScheduler(DuelFightModel fightModel) {
        this.duelAPI = RDuels.beanScope().get(DuelAPI.class);
        this.duelConfiguration = RDuels.beanScope().get(DuelConfiguration.class);
        this.titleSettings = duelConfiguration.titleSettings();
        this.duelFightModel = fightModel;
        this.time = 5;
        this.duelAPI.addNoMove(fightModel.getReceiver());
        this.duelAPI.addNoMove(fightModel.getSender());
        if (fightModel.getArenaModel().isFfa()) {
            if (fightModel.getReceiverParty() != null && fightModel.getSenderParty() != null) {
                PlayerUtil.duelPlayersConvertListUUID(fightModel.getReceiverParty().getPlayers()).forEach(this.duelAPI::addNoMove);
                PlayerUtil.duelPlayersConvertListUUID(fightModel.getSenderParty().getPlayers()).forEach(this.duelAPI::addNoMove);
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
        DuelPlayer receiver = this.duelFightModel.getReceiver();
        DuelPlayer sender = this.duelFightModel.getSender();
        Player bukkitReceiver = BukkitAdapter.adapt(receiver);
        Player bukkitSender = BukkitAdapter.adapt(sender);
        String text = this.time != -1 ? ColorUtil.color(titleSettings.toFight().text()) : ColorUtil.color(titleSettings.fight().text());
        int fadeIn = this.time != 1 ? titleSettings.toFight().fadeIn() : titleSettings.fight().fadeIn();
        int fadeOut = this.time != -1 ? titleSettings.toFight().fadeOut() : titleSettings.fight().fadeOut();
        int stay = this.time != -1 ? titleSettings.toFight().stay() : titleSettings.fight().stay();
        Sound sound = this.time != -1 ? Sound.valueOf(titleSettings.toFight().soundName()) : Sound.valueOf(titleSettings.fight().soundName());
        float v1 = this.time != -1 ? titleSettings.toFight().v1() : titleSettings.fight().v1();
        float v2 = this.time != -1 ? titleSettings.toFight().v2() : titleSettings.fight().v2();
        if (this.time != 1) {
            String textS = text.replace("(time)", String.valueOf(this.time));
            bukkitReceiver.sendTitle(textS, "", fadeIn, stay, fadeOut);
            bukkitSender.sendTitle(textS, "", fadeIn, stay, fadeOut);
            if (this.duelFightModel.getArenaModel().isFfa()) {
                if (this.duelFightModel.getSenderParty() != null && this.duelFightModel.getReceiverParty() != null) {
                    PlayerUtil.convertListUUID(this.duelFightModel.getReceiverParty().getPlayers()).forEach(player -> player.sendTitle(textS, "", fadeIn, stay, fadeOut));
                    PlayerUtil.convertListUUID(this.duelFightModel.getSenderParty().getPlayers()).forEach(player -> player.sendTitle(textS, "", fadeIn, stay, fadeOut));
                } else if (this.duelFightModel.getPlayer2() != null && this.duelFightModel.getPlayer4() != null) {
                    BukkitAdapter.adapt(this.duelFightModel.getPlayer4()).sendTitle(textS, "", fadeIn, stay, fadeOut);
                    BukkitAdapter.adapt(this.duelFightModel.getPlayer2()).sendTitle(textS, "", fadeIn, stay, fadeOut);
                }
            }
            if (sound == null) {
                return;
            }
            bukkitReceiver.playSound(bukkitReceiver.getLocation(), sound, v1, v2);
            bukkitSender.playSound(bukkitSender.getLocation(), sound, v1, v2);
            return;
        }
        bukkitReceiver.sendTitle(text, "", fadeIn, stay, fadeOut);
        bukkitSender.sendTitle(text, "", fadeIn, stay, fadeOut);
        this.duelAPI.removeNoMove(receiver);
        this.duelAPI.removeNoMove(sender);
        if (this.duelFightModel.getArenaModel().isFfa()) {
            if (this.duelFightModel.getReceiverParty() != null && this.duelFightModel.getSenderParty() != null) {
                PlayerUtil.convertListUUID(this.duelFightModel.getReceiverParty().getPlayers()).forEach(player -> {
                    this.duelAPI.removeNoMove(BukkitAdapter.adapt(player));
                    player.sendTitle(text, "", fadeIn, stay, fadeOut);
                });
                PlayerUtil.convertListUUID(this.duelFightModel.getSenderParty().getPlayers()).forEach(player -> {
                    this.duelAPI.removeNoMove(BukkitAdapter.adapt(player));
                    player.sendTitle(text, "", fadeIn, stay, fadeOut);
                });
            } else {
                BukkitAdapter.adapt(this.duelFightModel.getPlayer2()).sendTitle(text, "", fadeIn, stay, fadeOut);
                BukkitAdapter.adapt(this.duelFightModel.getPlayer4()).sendTitle(text, "", fadeIn, stay, fadeOut);
                this.duelAPI.removeNoMove(this.duelFightModel.getPlayer2());
                this.duelAPI.removeNoMove(this.duelFightModel.getPlayer4());
            }
        }
        if (sound == null) {
            return;
        }
        bukkitReceiver.playSound(bukkitReceiver.getLocation(), sound, v1, v2);
        bukkitSender.playSound(bukkitSender.getLocation(), sound, v1, v2);
        this.cancel();
    }
}
