package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Required;
import com.bivashy.configurate.objectmapping.meta.Setting;
import com.bivashy.configurate.objectmapping.meta.Transient;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import ru.merkii.rduels.gui.internal.click.ClickHandler;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.List;
import java.util.Optional;

@ConfigInterface
public interface InventoryItem {

    @Setting(nodeFromParent = true)
    ConfigurationNode root();

    @Transient
    default InventoryItem transform(ConfigurationTransformation transformation) {
        try {
            ConfigurationNode root = root().copy();
            transformation.apply(root);
            return root.get(InventoryItem.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    @Required
    String material();

    @Transient
    default Material bukkitMaterial() {
        return Material.valueOf(material());
    }

    Optional<Component> name();

    Optional<List<Component>> lore();

    RootClickSettings onClick();

    @Transient
    default ClickHandler createHandler(InventoryContext context) {
        return onClick().clickHandler(context);
    }

}
