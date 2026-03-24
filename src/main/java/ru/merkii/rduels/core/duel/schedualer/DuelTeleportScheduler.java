package ru.merkii.rduels.core.duel.schedualer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.duel.config.TitleSettingsConfiguration;
import ru.merkii.rduels.core.duel.model.DuelFightModel;
import ru.merkii.rduels.util.PlayerUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DuelTeleportScheduler extends BukkitRunnable {

    final TitleSettingsConfiguration titleSettings;
    final DuelAPI duelAPI;
    @Getter
    final DuelFightModel duelFightModel;
    final List<Player> participants = new ArrayList<>();
     int time = 5;

    public DuelTeleportScheduler(DuelFightModel fightModel) {
        this.duelAPI = RDuels.beanScope().get(DuelAPI.class);
        DuelConfiguration duelConfiguration = RDuels.beanScope().get(DuelConfiguration.class);
        this.titleSettings = duelConfiguration.titleSettings();
        this.duelFightModel = fightModel;
        initParticipants(fightModel);
        this.participants.forEach(p -> this.duelAPI.addNoMove(BukkitAdapter.adapt(p)));
        this.runTaskTimer(RDuels.getInstance(), 20L, 20L);
    }

    private void initParticipants(DuelFightModel fm) {
        participants.add(BukkitAdapter.adapt(fm.getReceiver()));
        participants.add(BukkitAdapter.adapt(fm.getSender()));
        if (fm.getArenaModel().isFfa()) {
            if (fm.getReceiverParty() != null && fm.getSenderParty() != null) {
                participants.addAll(PlayerUtil.convertListUUID(fm.getReceiverParty().getPlayers()));
                participants.addAll(PlayerUtil.convertListUUID(fm.getSenderParty().getPlayers()));
            } else {
                if (fm.getPlayer2() != null) participants.add(BukkitAdapter.adapt(fm.getPlayer2()));
                if (fm.getPlayer4() != null) participants.add(BukkitAdapter.adapt(fm.getPlayer4()));
            }
        }
    }

    @Override
    public void run() {
        if (time <= 0) {
            finish();
            return;
        }
        broadcastTitleAndSound(false);
        this.time--;
    }

    private void finish() {
        broadcastTitleAndSound(true);
        this.participants.forEach(p -> this.duelAPI.removeNoMove(BukkitAdapter.adapt(p)));
        this.cancel();
    }

    private void broadcastTitleAndSound(boolean isStart) {
        String rawText = isStart ? titleSettings.fight().text() : titleSettings.toFight().text();
        int fadeIn = isStart ? titleSettings.fight().fadeIn() : titleSettings.toFight().fadeIn();
        int stay = isStart ? titleSettings.fight().stay() : titleSettings.toFight().stay();
        int fadeOut = isStart ? titleSettings.fight().fadeOut() : titleSettings.toFight().fadeOut();
        String soundName = isStart ? titleSettings.fight().soundName() : titleSettings.toFight().soundName();
        float v1 = isStart ? titleSettings.fight().v1() : titleSettings.toFight().v1();
        float v2 = isStart ? titleSettings.fight().v2() : titleSettings.toFight().v2();

        if (!isStart) {
            rawText = rawText.replace("(time)", String.valueOf(time));
        }

        Component titleContent = MiniMessage.miniMessage().deserialize(rawText);
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );
        Title title = Title.title(titleContent, Component.empty(), times);
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            for (Player player : participants) {
                player.showTitle(title);
                player.playSound(player.getLocation(), sound, v1, v2);
            }
        } catch (IllegalArgumentException e) {
            participants.forEach(p -> p.showTitle(title));
        }
    }
}