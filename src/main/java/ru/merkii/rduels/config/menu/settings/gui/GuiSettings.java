package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Setting;
import com.bivashy.configurate.objectmapping.meta.Transient;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

@ConfigInterface
public interface GuiSettings {

    @Setting(nodeFromParent = true)
    ConfigurationNode root();

    @Transient
    default GuiSettings transform(ConfigurationTransformation transformation) {
        try {
            ConfigurationNode root = root().copy();
            transformation.apply(root);
            return root.get(GuiSettings.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    Component title();

    InventorySettings inventory();

}
