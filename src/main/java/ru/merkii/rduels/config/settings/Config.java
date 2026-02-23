package ru.merkii.rduels.config.settings;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import ru.merkii.rduels.util.GsonUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {

    private transient File file;

    public final void setFile(File file) {
        this.file = file;
    }

    public static <T, V> Map<T, V> fastMap(List<T> keys, List<V> values) {
        Preconditions.checkNotNull(keys);
        Preconditions.checkNotNull(values);
        Preconditions.checkArgument(keys.size() == values.size(), "Different keys and values sizes " + keys.size() + " != " + values.size());
        HashMap<T, V> map = new HashMap<T, V>();
        int i = 0;
        while (i < keys.size()) {
            map.put(keys.get(i), values.get(i));
            ++i;
        }
        return map;
    }

    @SafeVarargs
    public static <T> List<T> fastList(T ... values) {
        return Arrays.stream(values).collect(Collectors.toList());
    }

    public static <T extends Config> T load(Plugin p, String fileName, Class<T> clazz) {
        File file = new File(p.getDataFolder(), fileName);
        if (!file.exists() || file.length() == 0L) {
            p.getDataFolder().mkdirs();
            try {
                T config1 = clazz.newInstance();
                config1.setFile(file);
                config1.save();
                config1.init();
                return config1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        JsonObject jobj = GsonUtil.GSON.fromJson(GsonUtil.readFile(file), JsonObject.class);
        T config = GsonUtil.GSON.fromJson(jobj, clazz);
        config.setFile(file);
        config.init();
        try {
            JsonObject def = GsonUtil.GSON.toJsonTree(clazz.newInstance()).getAsJsonObject();
            for (Map.Entry<String, JsonElement> ent : def.entrySet()) {
                if (jobj.has(ent.getKey())) continue;
                config.save();
                break;
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return config;
    }

    public void init() {
    }

    public void save() {
        try {
            Files.write(this.file.toPath(), GsonUtil.GSON.toJson(this).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
