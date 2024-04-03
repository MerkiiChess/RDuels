package ru.merkii.rduels.core.customkit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;
import ru.merkii.rduels.core.customkit.menu.CustomKitCreateMenu;

@CommandAlias("custom-kit")
public class CustomKitCommand extends BaseCommand {

    @Default
    @Description("Открыть меню с кастомными китами")
    public void onCustomKitEdit(Player player) {
        new CustomKitCreateMenu(player).open(player);
    }

}
