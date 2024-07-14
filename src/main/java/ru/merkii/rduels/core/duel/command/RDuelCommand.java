package ru.merkii.rduels.core.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.arena.config.ArenaSettings;
import ru.merkii.rduels.core.arena.model.ArenaModel;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.model.DuelKitType;
import ru.merkii.rduels.core.duel.model.DuelType;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.core.sign.api.SignAPI;
import ru.merkii.rduels.core.sign.model.SignModel;
import ru.merkii.rduels.model.BlockPosition;
import ru.merkii.rduels.model.EntityPosition;
import ru.merkii.rduels.util.TimeUtil;
import java.util.HashMap;

@CommandAlias("r-duel|r-duels")
public class RDuelCommand extends BaseCommand {

    private final RDuels plugin = RDuels.getInstance();
    private final Settings settings = this.plugin.getSettings();
    private final ArenaSettings arenas = ArenaCore.INSTANCE.getArenas();
    private final ArenaAPI arenaAPI = ArenaCore.INSTANCE.getArenaAPI();
    private final DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();

    @Default
    @CommandPermission(value="r.duel")
    @Description(value="Основная команда R-Duel")
    @CommandCompletion(value="setlobby|arena|sign|savekit")
    public void rDuel(CommandSender sender) {
        sender.sendMessage("§e/r-duel setlobby - Поставить лобби");
        sender.sendMessage("§e/r-duel arena - Создание арен");
        sender.sendMessage("§e/r-duel sign - создание табличек");
        sender.sendMessage("§e/r-duel savekit <название> - сохранить серверный кит");
    }

    @Subcommand(value="setlobby")
    @CommandPermission(value="r-duel.setlobby")
    @Description(value="Поставить точку спавна")
    public void setLobby(Player player) {
        this.settings.getSpawns().add(new EntityPosition(player));
        this.settings.save();
        player.sendMessage("Успешно поставлена новая точка спавна");
    }

    @Subcommand(value="arena")
    @CommandPermission(value="r-duel.arena")
    @Description(value="Управление аренами")
    @CommandCompletion(value="create|setspawn")
    public void arena(CommandSender sender) {
        sender.sendMessage("/r-duels arena create [название_арены] [тип_боя] - создать новую арену");
        sender.sendMessage("r-duels arena setspawn [название_арены] [позиция_игрока] - создать/заменить позицию на арене");
    }

    @Subcommand(value="arena create")
    @CommandPermission(value="r-duel.arena.create")
    @Description(value="Создание новой арены")
    @CommandCompletion(value="<название_в_конфиге> <название_которое_видят_игроки> @materials")
    public void arenaCreate(CommandSender sender, String name, String displayName, Material material) {
        if (material == null) {
            sender.sendMessage("Материал не найден");
            return;
        }
        if (this.arenaAPI.isContainsArena(name)) {
            sender.sendMessage("Такая арена уже существует");
            return;
        }
        this.arenas.getArenas().add(ArenaModel.builder().material(material).arenaName(name).displayName(displayName).breaking(false).schematic("no").build());
        this.arenas.save();
        sender.sendMessage("Создана арена: " + name);
    }

    @Subcommand(value="sign")
    @CommandPermission(value="r-duel.sign")
    @Description(value="Управление табличками")
    @CommandCompletion(value="set|remove")
    public void sign(CommandSender sender) {
        sender.sendMessage("§e/r-duel sign set {1v1/2v2} {server/custom}");
        sender.sendMessage("§e/r-duel sign remove");
    }

    @Subcommand(value="sign remove")
    @CommandPermission(value="r-duel.sign.remove")
    @Description(value="Удаление таблички")
    public void signRemove(Player player) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage("Смотрите на табличку");
            return;
        }
        BlockPosition blockPosition = new BlockPosition(block.getLocation());
        if (!SignCore.INSTANCE.getSignAPI().removeSign(blockPosition)) {
            player.sendMessage("Табличка не найдена в файлах!");
            return;
        }
        player.sendMessage("Табличка удалена");
    }

    @Subcommand(value="sign set 1v1")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички боя 1в1")
    @CommandCompletion(value="server|custom")
    public void signSetOne(Player player) {
        player.sendMessage("/r-duel sign set 1v1 server - серверный кит");
        player.sendMessage("/r-duel sign set 1v1 custom - кастомный кит");
    }

    @Subcommand(value="sign set 2v2")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установить таблички боя 2в2")
    @CommandCompletion(value="server|custom")
    public void signSetTwo(Player player) {
        player.sendMessage("/r-duel sign set 2v2 server - серверный кит");
        player.sendMessage("/r-duel sign set 2v2 custom - кастомный кит");
    }

    @Subcommand(value="sign set 1v1 server")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички боя 1в1 кастом кита")
    @CommandCompletion(value="@duelkits")
    public void signSetOneServer(Player player, String kitName) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            player.sendMessage("Нужно смотреть на табличку");
            return;
        }
        if (!this.isKitName(kitName)) {
            player.sendMessage("Укажите название кита для серверного выбора");
            return;
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        DuelType duelType = DuelType.ONE;
        DuelKitType duelKitType = DuelKitType.SERVER;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, DuelCore.INSTANCE.getDuelAPI().getKitFromName(kitName));
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, kitName);
        player.sendMessage("Успешно поставлена");
    }

    @Subcommand(value="sign set 1v1 custom")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички боя 1в1 кастом кита")
    public void signSetOneCustom(Player player) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage("Нужно смотреть на табличку");
            return;
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        Sign sign = (Sign) block.getState();
        DuelType duelType = DuelType.ONE;
        DuelKitType duelKitType = DuelKitType.CUSTOM;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, null);
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, "");
        player.sendMessage("Успешно поставлена");
    }

    @Subcommand(value="sign set 2v2 server")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички боя 1в1 кастом кита")
    @CommandCompletion(value="@duelkits")
    public void signSetTwoServer(Player player, String kitName) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            player.sendMessage("Нужно смотреть на табличку");
            return;
        }
        if (!this.isKitName(kitName)) {
            player.sendMessage("Укажите название кита для серверного выбора");
            return;
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        DuelType duelType = DuelType.TWO;
        DuelKitType duelKitType = DuelKitType.SERVER;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, DuelCore.INSTANCE.getDuelAPI().getKitFromName(kitName));
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, kitName);
        player.sendMessage("Успешно поставлена");
    }

    @Subcommand(value="sign set 2v2 custom")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички боя 2в2 кастом кита")
    public void signSetTwoCustom(Player player) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign sign)) {
            player.sendMessage("Нужно смотреть на табличку");
            return;
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        DuelType duelType = DuelType.TWO;
        DuelKitType duelKitType = DuelKitType.CUSTOM;
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, duelKitType, null);
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), duelKitType, "");
        player.sendMessage("Успешно поставлена");
    }

    @Subcommand(value="sign set")
    @CommandPermission(value="r-duel.sign.set")
    @Description(value="Установка таблички")
    @CommandCompletion(value="1v1|2v2")
    public void signSet(Player player) {
        player.sendMessage("/r-duel sign set 1v1 - установить табличку 1 на 1");
        player.sendMessage("/r-duel sign set 1v1 - установить табличку 2 на 2");
    }

    private boolean isKitName(String kitName) {
        return this.plugin.getKitConfig().getKits().stream().anyMatch(model -> model.getDisplayName().equalsIgnoreCase(kitName));
    }

    @Subcommand(value="arena setspawn")
    @CommandPermission(value="r-duel.arena.setspawn")
    @Description(value="Установка точки спавна на арене")
    @CommandCompletion(value="@arenas <позиция>")
    public void arenaSetSpawn(CommandSender sender, String name, String position) {
        ArenaModel arenaModel = this.arenaAPI.getArenaFromName(name);
        if (arenaModel == null) {
            sender.sendMessage("Такой арены не существует");
            return;
        }
        Player player = (Player) sender;
        EntityPosition pos = new EntityPosition(player);
        if (position.equalsIgnoreCase("schematic")) {
            arenaModel.setSchematicPosition(pos);
        } else if (position.equalsIgnoreCase("spectate") || position.equalsIgnoreCase("spec") || position.equalsIgnoreCase("spectator")) {
            arenaModel.setSpectatorPosition(pos);
        } else if (arenaModel.isFfa()) {
            if (!TimeUtil.isInt(position)) {
                sender.sendMessage("Укажите или позицию ффа или spectate");
                return;
            }
            int posNum = Integer.parseInt(position);
            if (arenaModel.getFfaPositions() == null) {
                arenaModel.setFfaPositions(new HashMap<>());
            }
            arenaModel.getFfaPositions().put(posNum, pos);
        } else {
            if (!TimeUtil.isInt(position)) {
                sender.sendMessage("Позиция должна быть числом");
                return;
            }
            switch (Integer.parseInt(position)) {
                case 1: {
                    arenaModel.setOnePosition(pos);
                    break;
                }
                case 2: {
                    arenaModel.setTwoPosition(pos);
                    break;
                }
                default: {
                    sender.sendMessage("Позиция должна быть от 1 до 2");
                    return;
                }
            }
        }
        this.arenas.getArenas().remove(arenaModel);
        this.arenas.getArenas().add(arenaModel);
        this.arenas.save();
        sender.sendMessage("Локация добавлена");
    }

    @Subcommand(value="reload")
    @CommandPermission(value="r.duel.reload")
    @Description(value="Перезагрузка конфигурации")
    public void reload(CommandSender sender) {
        this.plugin.reloadConfigs();
        sender.sendMessage("Конфиги перезагружены");
    }

    @Subcommand(value="savekit")
    @CommandPermission(value="r.duel.savekit")
    @Description(value="Сохранение кита")
    @CommandCompletion(value="<название>")
    public void saveKit(CommandSender sender, String kitName) {
        if (this.duelAPI.isKitNameContains(kitName)) {
            sender.sendMessage("Такое название уже существует");
            return;
        }
        this.duelAPI.saveKitServer((Player) sender, kitName);
        this.plugin.reloadConfigs();
    }

    @CatchUnknown
    public void noPermission(CommandSender sender) {
        sender.sendMessage(this.plugin.getPluginMessage().getMessage("noPermission"));
    }

}
