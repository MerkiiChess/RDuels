package ru.merkii.rduels.core;

import ru.merkii.rduels.RDuels;

public interface Core {

    void enable(RDuels plugin);

    void disable(RDuels plugin);

    void reloadConfig(RDuels plugin);
}
