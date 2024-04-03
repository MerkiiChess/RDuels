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
    private final Settings settings = plugin.getSettings();
    private final ArenaSettings arenas = ArenaCore.INSTANCE.getArenas();
    private final ArenaAPI arenaAPI = ArenaCore.INSTANCE.getArenaAPI();
    private final DuelAPI duelAPI = DuelCore.INSTANCE.getDuelAPI();

    @Default
    @CommandPermission("r.duel")
    @Description("Основная команда R-Duel")
    @CommandCompletion("setlobby|arena|sign|savekit")
    public void rDuel(CommandSender sender) {
        sender.sendMessage("§e/r-duel setlobby - Поставить лобби");
        sender.sendMessage("§e/r-duel arena - Создание арен");
        sender.sendMessage("§e/r-duel sign - создание табличек");
        sender.sendMessage("§e/r-duel savekit <название> - сохранить серверный кит");
    }

    @Subcommand("setlobby")
    @CommandPermission("r-duel.setlobby")
    @Description("Поставить точку спавна")
    public void setLobby(Player player) {
        settings.getSpawns().add(new EntityPosition(player));
        settings.save();
        player.sendMessage("Успешно поставлена новая точка спавна");
    }

    @Subcommand("arena")
    @CommandPermission("r-duel.arena")
    @Description("Управление аренами")
    @CommandCompletion("create|setspawn")
    public void arena(CommandSender sender) {
        sender.sendMessage("/r-duels arena create [название_арены] [тип_боя] - создать новую арену");
        sender.sendMessage("r-duels arena setspawn [название_арены] [позиция_игрока] - создать/заменить позицию на арене");
    }

    @Subcommand("arena create")
    @CommandPermission("r-duel.arena.create")
    @Description("Создание новой арены")
    @CommandCompletion("<название_в_конфиге> <название_которое_видят_игроки> @materials")
    public void arenaCreate(CommandSender sender, String name, String displayName, Material material) {
        if (material == null) {
            sender.sendMessage("Материал не найден");
            return;
        }
        if (this.arenaAPI.isContainsArena(name)) {
            sender.sendMessage("Такая арена уже существует");
            return;
        }
        this.arenas.getArenas().add(ArenaModel.builder()
                .material(material)
                .arenaName(name)
                .displayName(displayName)
                .breaking(false)
                .schematic("no")
                .build());
        this.arenas.save();
        sender.sendMessage("Создана арена: " + name);
    }

    @Subcommand("sign")
    @CommandPermission("r-duel.sign")
    @Description("Управление табличками")
    @CommandCompletion("set|remove")
    public void sign(CommandSender sender) {
        sender.sendMessage("§e/r-duel sign set {1v1/2v2} {server/custom}");
        sender.sendMessage("§e/r-duel sign remove");
    }

    @Subcommand("sign remove")
    @CommandPermission("r-duel.sign.remove")
    @Description("Удаление таблички")
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

    @Subcommand("sign set")
    @CommandPermission("r-duel.sign.set")
    @Description("Установка таблички")
    @CommandCompletion("1v1|2v2 server|custom @duelkits")
    public void signSet(Player player, String duelTypeStr, String kitTypeStr, @Default("null") String kitName) {
        Block block = player.getTargetBlock(6);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage("Нужно смотреть на табличку");
            return;
        }
        SignAPI signAPI = SignCore.INSTANCE.getSignAPI();
        Sign sign = (Sign) block.getState();
        DuelType duelType = DuelType.fromString(duelTypeStr);
        if (duelType == null) {
            player.sendMessage("Не найден тип боя. (1v1/2v2)");
            return;
        }
        DuelKitType kitType = DuelKitType.fromString(kitTypeStr);
        if (kitType == null) {
            player.sendMessage("Не найден выбор кита. (server/custom)");
            return;
        }
        if (kitType == DuelKitType.SERVER && !isKitName(kitName)) {
            player.sendMessage("Укажите название кита для серверного выбора");
            return;
        }
        SignModel signModel = new SignModel(new BlockPosition(block.getLocation()), duelType, kitType, kitName.equals("null") ? null : DuelCore.INSTANCE.getDuelAPI().getKitFromName(kitName));
        signAPI.addSign(signModel);
        signAPI.setSignWait(sign, 0, duelType.getSize(), kitType, kitName);
        player.sendMessage("Успешно поставлена");
    }

    private boolean isKitName(String kitName) {
        return this.plugin.getKitConfig().getKits().stream().anyMatch(model -> model.getDisplayName().equalsIgnoreCase(kitName));
    }

    @Subcommand("arena setspawn")
    @CommandPermission("r-duel.arena.setspawn")
    @Description("Установка точки спавна на арене")
    @CommandCompletion("@arenas <позиция>")
    public void arenaSetSpawn(CommandSender sender, String name, String position) {
        ArenaModel arenaModel = this.arenaAPI.getArenaFromName(name);
        if (arenaModel == null) {
            sender.sendMessage("Такой арены не существует");
            return;
        }
        Player player = (Player) sender;
        EntityPosition pos = new EntityPosition(player);
        if (arenaModel.isFfa()) {
            if (TimeUtil.isInt(position)) {
                int posNum = Integer.parseInt(position);
                if (arenaModel.getFfaPositions() == null) {
                    arenaModel.setFfaPositions(new HashMap<>());
                }
                arenaModel.getFfaPositions().put(posNum, pos);
            } else if (position.equalsIgnoreCase("spectate")) {
                arenaModel.setSpectatorPosition(pos);
            } else {
                sender.sendMessage("Укажите или позицию ффа или spectate");
                return;
            }
        } else {
            if (!TimeUtil.isInt(position)) {
                sender.sendMessage("Позиция должна быть числом");
                return;
            }
            switch (Integer.parseInt(position)) {
                case 1: arenaModel.setOnePosition(pos); break;
                case 2: arenaModel.setTwoPosition(pos); break;
                case 3: arenaModel.setThreePosition(pos); break;
                case 4: arenaModel.setFourPosition(pos); break;
                case 5: arenaModel.setSpectatorPosition(pos); break;
                default: sender.sendMessage("Позиция должна быть от 1 до 5"); return;
            }
        }
        this.arenas.getArenas().remove(arenaModel);
        this.arenas.getArenas().add(arenaModel);
        this.arenas.save();
        sender.sendMessage("Локация добавлена");
    }

    @Subcommand("reload")
    @CommandPermission("r.duel.reload")
    @Description("Перезагрузка конфигурации")
    public void reload(CommandSender sender) {
        this.plugin.reloadConfigs();
        sender.sendMessage("Конфиги перезагружены");
    }

    @Subcommand("savekit")
    @CommandPermission("r.duel.savekit")
    @Description("Сохранение кита")
    @CommandCompletion("<название>")
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
