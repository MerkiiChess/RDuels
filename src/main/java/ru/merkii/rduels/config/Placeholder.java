package ru.merkii.rduels.config;

import java.util.Collections;
import java.util.List;

public interface Placeholder {

    String match();

    String replacement();

    static Placeholder of(String match, String replacement) {
        return new Placeholder() {
            @Override
            public String match() {
                return match;
            }

            @Override
            public String replacement() {
                return replacement;
            }
        };
    }

    static Placeholders wrapped(String match, String replacement) {
        return Placeholders.of(Placeholder.of(match, replacement));
    }

    interface Placeholders {

        static Placeholders of(Placeholder... placeholders) {
            return () -> List.of(placeholders);
        }

        static Placeholders empty() {
            return Collections::emptyList;
        }

        List<Placeholder> placeholders();

    }

}
