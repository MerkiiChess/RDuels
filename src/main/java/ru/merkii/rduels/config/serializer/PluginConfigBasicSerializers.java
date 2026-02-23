package ru.merkii.rduels.config.serializer;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.menu.messages.MessagesMenuConfiguration;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.model.ExecuteCommand;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.model.KitModel;

import org.bukkit.Material;

public class PluginConfigBasicSerializers {

    public static TypeSerializerCollection serializers() {
        return TypeSerializerCollection.builder()
                .register(Material.class, new MaterialSerializer())
                .register(ItemBuilder.class, new ItemBuilderSerializer())
                .register(KitModel.class, new KitModelSerializer())
                .register(EntityPosition.class, new EntityPositionSerializer())
                .register(ArenaModel.class, new ArenaModelSerializer())
                .register(CustomKitCategory.class, new CustomKitCategorySerializer())
                .register(CustomKitEnchantCategory.class, new CustomKitEnchantCategorySerializer())
                .register(CustomKitModel.class, new CustomKitModelSerializer())
                .registerExact(ExecuteCommand.class, new ExecuteCommandSerializer())
                .registerExact(MessagesMenuConfiguration.class, new MessagesConfigurationSerializer())
                .registerExact(Component.class, new AdventureComponentSerializer())
                .registerExact(MessageConfig.class,  new MessageConfigurationSerializer())
                .register(OptionalSerializer.TYPE, OptionalSerializer.INSTANCE)
                .build();
    }

}