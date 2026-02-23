package ru.merkii.rduels.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public enum ComponentSerializerProviders {
    MINI_MESSAGE(MiniMessage.miniMessage()),
    LEGACY_AMPERSAND(LegacyComponentSerializer.legacyAmpersand().toBuilder().hexColors().build()),
    LEGACY_SECTION(LegacyComponentSerializer.legacySection().toBuilder().hexColors().build());
    private final ComponentSerializer<? super Component, ? extends Component, String> componentSerializer;

    ComponentSerializerProviders(ComponentSerializer<? super Component, ? extends Component, String> componentSerializer) {
        this.componentSerializer = componentSerializer;
    }

    public ComponentSerializer<? super Component, ? extends Component, String> componentSerializer() {
        return componentSerializer;
    }
}