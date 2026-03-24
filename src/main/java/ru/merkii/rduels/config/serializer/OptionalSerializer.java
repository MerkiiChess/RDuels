package ru.merkii.rduels.config.serializer;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

final class OptionalSerializer implements TypeSerializer<Optional<?>> {

    static final TypeToken<Optional<?>> TYPE = new TypeToken<Optional<?>>() {};
    static final TypeSerializer<Optional<?>> INSTANCE = new OptionalSerializer();

    private OptionalSerializer() {
    }

    private static Type extractParameter(final Type optional) throws SerializationException {
        if (!(optional instanceof ParameterizedType)) {
            throw new SerializationException(optional, "Raw types are not supported for collections");
        }
        return ((ParameterizedType) optional).getActualTypeArguments()[0];
    }

    @Override
    public Optional<?> deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        if (node.empty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(node.get(extractParameter(type)));
    }

    @Override
    public void serialize(final Type type, final @Nullable Optional<?> obj, final ConfigurationNode node) throws SerializationException {
        if (obj == null || !obj.isPresent()) {
            node.set(null);
            return;
        }

        node.set(extractParameter(type), obj.get());
    }

    @Override
    public Optional<?> emptyValue(final Type specificType, final ConfigurationOptions options) {
        return Optional.empty();
    }

    static final class OfInt implements TypeSerializer<OptionalInt> {

        static final Class<OptionalInt> TYPE = OptionalInt.class;
        static final TypeSerializer<OptionalInt> INSTANCE = new OfInt();

        private OfInt() {
        }

        @Override
        public OptionalInt deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            if (node.empty()) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(node.get(int.class));
        }

        @Override
        public void serialize(final Type type, final @Nullable OptionalInt obj, final ConfigurationNode node) throws SerializationException {
            if (obj == null || !obj.isPresent()) {
                node.set(null);
                return;
            }

            node.set(int.class, obj.getAsInt());
        }

        @Override
        public OptionalInt emptyValue(final Type specificType, final ConfigurationOptions options) {
            return OptionalInt.empty();
        }

    }

    static final class OfLong implements TypeSerializer<OptionalLong> {

        static final Class<OptionalLong> TYPE = OptionalLong.class;
        static final TypeSerializer<OptionalLong> INSTANCE = new OfLong();

        private OfLong() {
        }

        @Override
        public OptionalLong deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            if (node.empty()) {
                return OptionalLong.empty();
            }
            return OptionalLong.of(node.get(long.class));
        }

        @Override
        public void serialize(final Type type, final @Nullable OptionalLong obj, final ConfigurationNode node) throws SerializationException {
            if (obj == null || !obj.isPresent()) {
                node.set(null);
                return;
            }

            node.set(long.class, obj.getAsLong());
        }

        @Override
        public OptionalLong emptyValue(final Type specificType, final ConfigurationOptions options) {
            return OptionalLong.empty();
        }

    }

    static final class OfDouble implements TypeSerializer<OptionalDouble> {

        static final Class<OptionalDouble> TYPE = OptionalDouble.class;
        static final TypeSerializer<OptionalDouble> INSTANCE = new OfDouble();

        private OfDouble() {
        }

        @Override
        public OptionalDouble deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            if (node.empty()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(node.get(double.class));
        }

        @Override
        public void serialize(final Type type, final @Nullable OptionalDouble obj, final ConfigurationNode node) throws SerializationException {
            if (obj == null || !obj.isPresent()) {
                node.set(null);
                return;
            }

            node.set(double.class, obj.getAsDouble());
        }

        @Override
        public OptionalDouble emptyValue(final Type specificType, final ConfigurationOptions options) {
            return OptionalDouble.empty();
        }

    }

}