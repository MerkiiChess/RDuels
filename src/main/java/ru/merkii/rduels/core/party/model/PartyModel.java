package ru.merkii.rduels.core.party.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PartyModel implements Cloneable {

    @Setter
    private UUID owner;
    private final List<UUID> players;

    public static PartyModel create(Player owner, List<UUID> players) {
        return new PartyModel(owner.getUniqueId(), players);
    }

    @Override
    public PartyModel clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (PartyModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
