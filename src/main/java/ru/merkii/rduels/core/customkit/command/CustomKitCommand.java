package ru.merkii.rduels.core.customkit.command;

import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.customkit.menu.CustomKitCreateMenu;

@Singleton
public class CustomKitCommand {

    @Command("custom-kit")
    public void onCustomKitEdit(Player player) {
        new CustomKitCreateMenu().open(player);
    }

}
