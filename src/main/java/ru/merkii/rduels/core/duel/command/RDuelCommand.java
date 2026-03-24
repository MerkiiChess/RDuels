package ru.merkii.rduels.core.duel.command;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.Placeholder;
import ru.merkii.rduels.config.ResourceConfiguration;
import ru.merkii.rduels.config.category.CategoryItemConfiguration;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.config.settings.SettingsConfiguration;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.config.ArenaConfiguration;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelType;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.lamp.suggestion.Arenas;
import ru.merkii.rduels.lamp.suggestion.DuelKits;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;

import java.io.IOException;
import java.util.*;
import java.util.Optional;

@Singleton
@Command({"r-duel", "r-duels"})
@CommandPermission("r.duel")
@RequiredArgsConstructor(onConstructor_ = @Inject)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RDuelCommand {

    ResourceConfiguration resourceConfiguration;
    KitConfiguration kitConfiguration;
    SettingsConfiguration settings;
    ArenaConfiguration arenaConfiguration;
    CategoryItemConfiguration categoryItemConfiguration;
    MessageConfig messageConfig;
    ArenaAPI arenaAPI;
    DuelAPI duelAPI;
    SignAPI signAPI;

    @Subcommand("help")
    @Description("Показать список всех команд")
    public void help(BukkitCommandActor actor) {
        messageConfig.send(actor, "admin-help");
    }

    @Subcommand("setlobby")
    @CommandPermission("r.duel.setlobby")
    public void setLobby(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        settings.spawns().add(new EntityPosition(player));
        saveResource("settings.yml", SettingsConfiguration.class, settings);
        messageConfig.send(actor, "admin-setlobby-success");
    }

    @Subcommand("reload")
    @CommandPermission("r.duel.reload")
    public void reload(BukkitCommandActor actor) {
        try {
            resourceConfiguration.reloadAll();
            messageConfig.send(actor, "admin-reload-success");
        } catch (IOException e) {
            messageConfig.send(actor, "admin-reload-error");
            e.printStackTrace();
        }
    }

    @Subcommand("savekit")
    @CommandPermission("r.duel.savekit")
    public void saveK(BukkitCommandActor actor) {
        messageConfig.send(actor, "admin-savekit-args");
    }

    @Subcommand("savekit")
    @CommandPermission("r.duel.savekit")
    public void saveKit(BukkitCommandActor actor, String kitName) {
        if (duelAPI.isKitNameContains(kitName)) {
            messageConfig.send(actor, "admin-savekit-error");
            return;
        }
        Player player = actor.requirePlayer();
        duelAPI.saveKitServer(BukkitAdapter.adapt(player), kitName);
        messageConfig.send(actor, "admin-savekit-server");
    }

    // --- ARENA ---

    @Subcommand({"arena help", "arena", "arena create", "arena setspawn"})
    @CommandPermission("r.duel.arena")
    public void arenaHelp(BukkitCommandActor actor) {
        messageConfig.send(actor, "arena-help");
    }

    @Subcommand("arena create")
    @CommandPermission("r.duel.arena.create")
    public void create(BukkitCommandActor actor, String name, String displayName, Material material) {
        if (arenaAPI.isContainsArena(name)) {
            messageConfig.send(actor, "arena-already-exists");
            return;
        }

        ArenaModel model = ArenaModel.builder()
                .arenaName(name).displayName(displayName)
                .material(material).breaking(false).schematic("no")
                .build();
        arenaConfiguration.arenas().put(model, ItemBuilder.builder().setMaterial(material).setDisplayName(displayName));
        saveResource("arenas.yml", ArenaConfiguration.class, arenaConfiguration);
        messageConfig.send(actor, Placeholder.wrapped("(name)", name), "arena-created");
    }

    @Subcommand("arena setspawn")
    @CommandPermission("r.duel.arena.setspawn")
    public void setSpawn(BukkitCommandActor actor, @SuggestWith(Arenas.class) String name, @Suggest({"1", "2", "spec", "schematic"}) String posLabel) {
        arenaAPI.getArenaFromName(name).ifPresentOrElse(arena -> {
            EntityPosition pos = new EntityPosition(actor.requirePlayer());
            switch (posLabel.toLowerCase()) {
                case "schematic" -> arena.setSchematicPosition(pos);
                case "spec", "spectator" -> arena.setSpectatorPosition(pos);
                case "1" -> arena.setOnePosition(pos);
                case "2" -> arena.setTwoPosition(pos);
                default -> {
                    try {
                        int ffaIndex = Integer.parseInt(posLabel);
                        if (arena.getFfaPositions() == null) arena.setFfaPositions(new HashMap<>());
                        arena.getFfaPositions().put(ffaIndex, pos);
                    } catch (NumberFormatException e) {
                        actor.reply("§cИспользуйте 1, 2, spec или номер позиции для FFA.");
                        return;
                    }
                }
            }
            saveResource("arenas.yml", ArenaConfiguration.class, arenaConfiguration);
            messageConfig.send(
                    actor,
                    Placeholder.Placeholders.of(
                            Placeholder.of("(pos)", posLabel),
                            Placeholder.of("(name)", name)
                    ),
                    "arena-spawn-set");
            }, () -> messageConfig.send(actor, Placeholder.wrapped("(name)", name), "arena-not-found"));
    }

    @Subcommand({"sign help", "sign set"})
    @CommandPermission("r.duel.sign")
    public void signHelp(BukkitCommandActor actor) {
        messageConfig.send(actor, "sign-help");
    }

    @Subcommand("sign remove")
    @CommandPermission("r.duel.sign.remove")
    public void remove(BukkitCommandActor actor) {
        getTargetSign(actor).ifPresent(block -> {
            if (signAPI.removeSign(new BlockPosition(block.getLocation()))) {
                messageConfig.send(actor, "sign-removed");
            } else {
                messageConfig.send(actor, "sign-not-found");
            }
        });
    }

    @Subcommand("sign set")
    @CommandPermission("r.duel.sign.set")
    public void set(BukkitCommandActor actor, DuelType type, DuelKitType kitType, @SuggestWith(DuelKits.class) String kitName) {
        getTargetSign(actor).ifPresent(block -> {
            Sign sign = (Sign) block.getState();

            if (kitType == DuelKitType.SERVER && (kitName == null || !isKitValid(kitName))) {
                messageConfig.send(actor, "sign-invalid-kit");
                return;
            }
            SignModel model = new SignModel(
                    new BlockPosition(block.getLocation()),
                    type, kitType,
                    kitType == DuelKitType.SERVER ? duelAPI.getKitFromName(kitName) : null
            );
            signAPI.addSign(model);
            signAPI.setSignWait(sign, 0, type.getSize(), kitType, kitType == DuelKitType.SERVER ? kitName : "");
            messageConfig.send(actor, "sign-set-success");
        });
    }

    // --- CATEGORY ---

    @Subcommand({"category help", "category create", "category addmaterial"})
    @CommandPermission("r.duel.category")
    public void categoryHelp(BukkitCommandActor actor) {
        messageConfig.send(actor, "category-help");
    }

    @Subcommand("category create")
    @CommandPermission("r.duel.category.create")
    public void create(BukkitCommandActor actor, Material material, String id) {
        if (categoryItemConfiguration.categories().containsKey(id)) {
            messageConfig.send(actor, "category-already-exists");
            return;
        }
        CustomKitCategory category = new CustomKitCategory(id, id, material, new ArrayList<>());
        categoryItemConfiguration.categories().put(id, category);
        saveResource("category-items.yml", CategoryItemConfiguration.class, categoryItemConfiguration);
        messageConfig.send(actor, Placeholder.wrapped("(id)", id), "category-created");
    }

    @Subcommand("category addmaterial")
    @CommandPermission("r.duel.category.addmaterial")
    public void addMaterial(BukkitCommandActor actor, String id, Material material) {
        Optional.ofNullable(categoryItemConfiguration.categories().get(id)).ifPresentOrElse(cat -> {
            if (cat.getItems().contains(material)) {
                messageConfig.send(actor,
                        Placeholder.wrapped("(material)", material.name()),
                        "category-material-already-exists");
                return;
            }
            cat.getItems().add(material);
            saveResource("category-items.yml", CategoryItemConfiguration.class, categoryItemConfiguration);
            messageConfig.send(
                    actor,
                    Placeholder.Placeholders.of(
                            Placeholder.of("(id)", id),
                            Placeholder.of("(material)", material.name())
                    ),
                    "category-material-added");
            }, () -> messageConfig.send(actor, Placeholder.wrapped("(id)", id), "category-not-found"));
    }

    private <T> void saveResource(String fileName, Class<T> tClass, T config) {
        try {
            resourceConfiguration.updateAndSave(fileName, tClass, config);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить " + fileName, e);
        }
    }

    private Optional<Block> getTargetSign(BukkitCommandActor actor) {
        Block block = actor.requirePlayer().getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Sign)) {
            messageConfig.send(actor, "sign-not-found");
            return Optional.empty();
        }
        return Optional.of(block);
    }

    private boolean isKitValid(String name) {
        return kitConfiguration.kits().keySet().stream()
                .anyMatch(k -> k.getDisplayName().equalsIgnoreCase(name));
    }
}