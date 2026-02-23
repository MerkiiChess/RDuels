package ru.merkii.rduels.core.sign.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Sign;
import java.util.List;

@UtilityClass
public class SignUtil {

    public void clearSignsLines(Sign sign) {
        for (int i = 0 ; i < sign.lines().size() ; i++) {
            sign.setLine(i, "");
        }
        sign.update();
    }

    public void setLines(Sign sign, Component text) {
        List<Component> lines = text
                .replaceText(builder -> builder.match("\n").replacement(Component.newline()))
                .children();

        for (int i = 0; i < 4; i++) {
            sign.line(i, i < lines.size() ? lines.get(i) : Component.empty());
        }

        sign.update();
    }

}
