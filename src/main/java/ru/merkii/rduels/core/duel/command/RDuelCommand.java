package ru.merkii.rduels.core.duel.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.ResourceConfiguration;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelType;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.lamp.suggestion.Arenas;
import ru.merkii.rduels.lamp.suggestion.DuelKits;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.TimeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Command({"r-duel", "r-duels"})
@Singleton
public class RDuelCommand {

    private final RDuels plugin;
    private final ResourceConfiguration resourceConfiguration;
    private final KitConfiguration kitConfiguration;
    private final SettingsConfiguration settings;
    private final ArenaConfiguration arenaConfiguration;
    private final ArenaAPI arenaAPI;
    private final DuelAPI duelAPI;
    private final SignAPI signAPI;

    @Inject
    public RDuelCommand(RDuels plugin, ResourceConfiguration resourceConfiguration, SettingsConfiguration settings, ArenaConfiguration arenaConfiguration, ArenaAPI arenaAPI, DuelAPI duelAPI, SignAPI signAPI, KitConfiguration kitConfiguration) {
        this.plugin = plugin;
        this.resourceConfiguration = resourceConfiguration;
        this.settings = settings;
        this.arenaConfiguration = arenaConfiguration;
        this.arenaAPI = arenaAPI;
        this.duelAPI = duelAPI;
        this.signAPI = signAPI;
        this.kitConfiguration = kitConfiguration;
    }

    @Command("r-duel")
    @CommandPermission("r.duel")
    @Description("Основная команда R-Duel")
    public void rDuel(BukkitCommandActor actor) {
        actor.reply("§e/r-duel setlobby - Поставить лобби");
        actor.reply("§e/r-duel arena - Создание арен");
        actor.reply("§e/r-duel sign - создание табличек");
        actor.reply("§e/r-duel savekit <название> - сохранить серверный кит");
    }

    @Command("r-duel setlobby")
    @CommandPermission("r.duel.setlobby")
    @Description("Поставить точку спавна")
    public void setLobby(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        this.settings.spawns().add(new EntityPosition(player));
        try {
            this.resourceConfiguration.updateAndSave("settings.yml", SettingsConfiguration.class, this.settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        actor.reply("§aУспешно поставлена новая точка спавна");
    }

    @Command("r-duel arena")
    @CommandPermission("r.duel.arena")
    @Description("Управление аренами")
    public void arena(BukkitCommandActor actor) {
        actor.reply("/r-duels arena create [название_арены] [тип_боя] - создать новую арену");
        actor.reply("/r-duels arena setspawn [название_арены] [позиция_игрока] - создать/заменить позицию на арене");
    }

    @Command("r-duel arena create")
    @CommandPermission("r.duel.arena.create")
    @Description("Создание новой арены")
    public void arenaCreate(BukkitCommandActor actor, String name, String displayName, Material material) {
        if (material == null) {
            actor.reply("§cМатериал не найден");
            return;
        }
        if (this.arenaAPI.isContainsArena(name)) {
            actor.reply("§cТакая арена уже существует");
            return;
        }
        Map<ArenaModel, ItemBuilder> map = new HashMap<>(this.arenaConfiguration.arenas());
        map.put(
                ArenaModel.builder().material(material).arenaName(name).displayName(displayName).breaking(false).schematic("no").build(),
                ItemBuilder.builder().setMaterial(material).setDisplayName(displayName)
        );
        this.arenaConfiguration.arenas(map);
        try {
            this.resourceConfiguration.updateAndSave("arenas.yml", ArenaConfiguration.class, this.arenaConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        actor.reply("§aСоздана арена: " + name);
    }

    @Command("r-duel sign")
    @CommandPermission("r.duel.sign")
    @Description("Управление табличками")
    public void sign(BukkitCommandActor actor) {
        actor.reply("§e/r-duel sign set {1v1/2v2} {server/custom}");
        actor.reply("§e/r-duel sign remove");
    }

    @Command("r-duel sign remove")
    @CommandPermission("r.duel.sign.remove")
    @Description("Удаление таблички")
    public void signRemove(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign)) {
            actor.reply("§cСмотрите на табличку");
            return;
        }
        BlockPosition blockPosition = new BlockPosition(block.getLocation());
        if (!signAPI.removeSign(blockPosition)) {
            actor.reply("§cТабличка не найдена в файлах!");
            return;
        }
        actor.reply("§aТабличка удалена");
    }

    @Command("r-duel sign set 1v1")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички боя 1в1")
    public void signSetOne(BukkitCommandActor actor) {
        actor.reply("/r-duel sign set 1v1 server - серверный кит");
        actor.reply("/r-duel sign set 1v1 custom - кастомный кит");
    }

    @Command("r-duel sign set 2v2")
    @CommandPermission("r.duel.sign.set")
    @Description("Установить таблички боя 2в2")
    public void signSetTwo(BukkitCommandActor actor) {
        actor.reply("/r-duel sign set 2v2 server - серверный кит");
        actor.reply("/r-duel sign set 2v2 custom - кастомный кит");
    }

    @Command("r-duel sign set 1v1 server")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички боя 1в1 серверный кит")
    public void signSetOneServer(BukkitCommandActor actor, @SuggestWith(DuelKits.class) String kitName) {
        Player player = actor.requirePlayer();
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            actor.reply("§cНужно смотреть на табличку");
            return;
        }
        if (!this.isKitName(kitName)) {
            actor.reply("§cУкажите название кита для серверного выбора");
            return;
        }
        DuelType duelType = DuelType.ONE;
        DuelKitType duelKitType = DuelKitType.SERVER;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, duelAPI.getKitFromName(kitName));
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, kitName);
        actor.reply("§aУспешно поставлена");
    }

    @Command("r-duel sign set 1v1 custom")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички боя 1в1 кастом кит")
    public void signSetOneCustom(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            actor.reply("§cНужно смотреть на табличку");
            return;
        }
        DuelType duelType = DuelType.ONE;
        DuelKitType duelKitType = DuelKitType.CUSTOM;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, null);
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, "");
        actor.reply("§aУспешно поставлена");
    }

    @Command("r-duel sign set 2v2 server")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички боя 2в2 серверный кит")
    public void signSetTwoServer(BukkitCommandActor actor, @SuggestWith(DuelKits.class) String kitName) {
        Player player = actor.requirePlayer();
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            actor.reply("§cНужно смотреть на табличку");
            return;
        }
        if (!this.isKitName(kitName)) {
            actor.reply("§cУкажите название кита для серверного выбора");
            return;
        }
        DuelType duelType = DuelType.TWO;
        DuelKitType duelKitType = DuelKitType.SERVER;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, duelAPI.getKitFromName(kitName));
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, kitName);
        actor.reply("§aУспешно поставлена");
    }

    @Command("r-duel sign set 2v2 custom")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички боя 2в2 кастом кит")
    public void signSetTwoCustom(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            actor.reply("§cНужно смотреть на табличку");
            return;
        }
        DuelType duelType = DuelType.TWO;
        DuelKitType duelKitType = DuelKitType.CUSTOM;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, null);
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, "");
        actor.reply("§aУспешно поставлена");
    }

    @Command("r-duel sign set")
    @CommandPermission("r.duel.sign.set")
    @Description("Установка таблички (help)")
    public void signSet(BukkitCommandActor actor) {
        actor.reply("/r-duel sign set 1v1 - установить табличку 1 на 1");
        actor.reply("/r-duel sign set 2v2 - установить табличку 2 на 2");
    }

    @Command("r-duel arena setspawn")
    @CommandPermission("r.duel.arena.setspawn")
    @Description("Установка точки спавна на арене")
    public void arenaSetSpawn(BukkitCommandActor actor, @SuggestWith(Arenas.class) String name, @Suggest({"1", "2", "schematic", "spec"}) String position) {
        ArenaModel arenaModel = this.arenaAPI.getArenaFromName(name);
        if (arenaModel == null) {
            actor.reply("§cТакой арены не существует");
            return;
        }
        Player player = actor.asPlayer();
        ItemBuilder itemBuilder = this.arenaConfiguration.arenas().get(arenaModel);
        EntityPosition pos = new EntityPosition(player);
        if (position.equalsIgnoreCase("schematic")) {
            arenaModel.setSchematicPosition(pos);
        } else if (position.equalsIgnoreCase("spectate") || position.equalsIgnoreCase("spec") || position.equalsIgnoreCase("spectator")) {
            arenaModel.setSpectatorPosition(pos);
        } else if (arenaModel.isFfa()) {
            if (!TimeUtil.isInt(position)) {
                actor.reply("§cУкажите или позицию ффа или spectate");
                return;
            }
            int posNum = Integer.parseInt(position);
            if (arenaModel.getFfaPositions() == null) {
                arenaModel.setFfaPositions(new HashMap<>());
            }
            arenaModel.getFfaPositions().put(posNum, pos);
        } else {
            if (!TimeUtil.isInt(position)) {
                actor.reply("§cПозиция должна быть числом");
                return;
            }
            int posInt = Integer.parseInt(position);
            switch (posInt) {
                case 1 -> arenaModel.setOnePosition(pos);
                case 2 -> arenaModel.setTwoPosition(pos);
                default -> {
                    actor.reply("§cПозиция должна быть от 1 до 2");
                    return;
                }
            }
        }
        this.arenaConfiguration.arenas().remove(arenaModel);
        this.arenaConfiguration.arenas().put(arenaModel, itemBuilder);
        try {
            this.resourceConfiguration.updateAndSave("arenas.yml", ArenaConfiguration.class, this.arenaConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        actor.reply("§aЛокация добавлена");
    }

    @Command("r-duel reload")
    @CommandPermission("r.duel.reload")
    @Description("Перезагрузка конфигурации")
    public void reload(BukkitCommandActor actor) {
        this.plugin.reloadConfigs();
        actor.reply("§aКонфиги перезагружены");
    }

    @Command("r-duel savekit")
    @CommandPermission("r.duel.savekit")
    @Description("Сохранение кита")
    public void saveKit(BukkitCommandActor actor, String kitName) {
        if (this.duelAPI.isKitNameContains(kitName)) {
            actor.reply("§cТакое название уже существует");
            return;
        }
        Player player = actor.requirePlayer();
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        this.duelAPI.saveKitServer(duelPlayer, kitName);
        this.plugin.reloadConfigs();
        actor.reply("§aКит сохранен");
    }

    private boolean isKitName(String kitName) {
        return kitConfiguration.kits()
                .keySet()
                .stream().anyMatch(model -> model.getDisplayName().equalsIgnoreCase(kitName));
    }
}