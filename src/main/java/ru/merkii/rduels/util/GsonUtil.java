package ru.merkii.rduels.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

public class GsonUtil {

    public static Gson GSON;
    public static GsonBuilder builder;
    public static final FilenameFilter jsonFilter;

    static {
        builder = new GsonBuilder();
        builder.setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON = builder.create();
        jsonFilter = (dir, name) -> name.endsWith(".json");
    }

    public static String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
