package ru.merkii.rduels.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ColorUtil {

    private static final Pattern RGBD_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    public String color(String text) {
        Matcher matcher = RGBD_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(sb, ChatColor.of("#" + hexColor).toString());
        }
        matcher.appendTail(sb);

        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public List<String> color(List<String> list) {
        return Optional.ofNullable(list).orElseGet(ArrayList::new).stream()
                .map(ColorUtil::color)
                .collect(Collectors.toList());
    }

}
