package ru.merkii.rduels.core.party.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.merkii.rduels.adapter.DuelPlayer;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyModel implements Cloneable {

    @Setter
    UUID owner;
    final List<UUID> players;

    public static PartyModel create(DuelPlayer owner, List<UUID> players) {
        return new PartyModel(owner.getUUID(), players);
    }

    @Override
    public PartyModel clone() {
        try {
            return (PartyModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
