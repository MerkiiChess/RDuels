package ru.merkii.rduels.core.bedwars;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.bedwars.bucket.BedwarsGameBucket;
import ru.merkii.rduels.core.bedwars.command.BedwarsCommand;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.bedwars.generator.GeneratorService;

import java.util.ArrayList;

/**
 * Bedwars module: teams, generators, shop, upgrades and bed-respawn win condition.
 * Game logic is in the auto-registered listeners; this wires the /shop and /upgrades
 * commands and tears down running games on disable.
 */
@Singleton
public class BedwarsCore implements Core {

    private final Lamp<BukkitCommandActor> lamp;
    private final BedwarsCommand command;
    private final BedwarsGameBucket gameBucket;
    private final GeneratorService generatorService;

    @Inject
    public BedwarsCore(Lamp<BukkitCommandActor> lamp, BedwarsCommand command,
                       BedwarsGameBucket gameBucket, GeneratorService generatorService) {
        this.lamp = lamp;
        this.command = command;
        this.gameBucket = gameBucket;
        this.generatorService = generatorService;
    }

    @Override
    public void enable(RDuels plugin) {
        lamp.register(command);
    }

    @Override
    public void disable(RDuels plugin) {
        for (BedwarsGame game : new ArrayList<>(gameBucket.all())) {
            generatorService.stopGenerators(game);
            gameBucket.remove(game);
        }
    }

    @Override
    public void reloadConfig(RDuels plugin) {
    }
}
