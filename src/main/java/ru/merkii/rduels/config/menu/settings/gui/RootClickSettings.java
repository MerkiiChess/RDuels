package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;
import io.avaje.inject.BeanScope;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.gui.internal.click.ClickHandler;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.Optional;

@ConfigInterface
public interface RootClickSettings extends ClickSettings {

    Optional<ClickSettings> left();

    Optional<ClickSettings> middle();

    Optional<ClickSettings> right();

    Optional<ClickSettings> shiftLeft();

    Optional<ClickSettings> shiftRight();

    @Transient
    default ClickHandler clickHandler(InventoryContext context) {
        BeanScope beanScope = RDuels.beanScope();
        context.overrideOrCreate("settings", this);
        return beanScope.get(ClickHandlerRegistry.class).findAndCreate("root", context).orElseThrow(() -> new IllegalStateException("Cannot find root factory for ClickHandler"));
    }

}
