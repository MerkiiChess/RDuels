package ru.merkii.rduels.core.killeffect.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.core.killeffect.menu.KillEffectMenu;

@Singleton
public class KillEffectCommand {

    @Inject
    public KillEffectCommand() {
    }

    @Command({"killeffect", "killeffects"})
    public void onKillEffect(Player player) {
        new KillEffectMenu().open(player);
    }

}
