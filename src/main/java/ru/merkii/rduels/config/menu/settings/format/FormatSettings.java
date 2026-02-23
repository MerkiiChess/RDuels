package ru.merkii.rduels.config.menu.settings.format;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Transient;

import java.text.DecimalFormat;

@ConfigInterface
public interface FormatSettings {

    String decimalFormat();

    @Transient
    default DecimalFormat formatter() {
        String format = decimalFormat();
        if (format == null)
            format = "0";
        return new DecimalFormat(format);
    }

}
