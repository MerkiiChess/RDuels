package ru.merkii.rduels.core.customkit.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import ru.merkii.rduels.gui.internal.InventoryGUI;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CustomKitCommand {

    InventoryGUIFactory factory;

    @Command("custom-kit")
    public void onCustomKitEdit(Player player) {
        factory.create("create-kit", player, InventoryContext.empty()).ifPresent(InventoryGUI::open);
    }

}
