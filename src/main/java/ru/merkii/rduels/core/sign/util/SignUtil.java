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

    public void setLines(Sign sign, List<String> text) {
        for (int i = 0 ; i < text.size() ; i++) {
            sign.setLine(i, text.get(i));
        }
        sign.update();
    }

}
