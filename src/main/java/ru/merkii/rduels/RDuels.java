package ru.merkii.rduels;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.merkii.rduels.command.DayCommand;
import ru.merkii.rduels.command.NightCommand;
import ru.merkii.rduels.config.messages.MessageConfiguration;
import ru.merkii.rduels.config.messages.PluginConfig;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.config.settings.KitConfig;
import ru.merkii.rduels.config.settings.Settings;
import ru.merkii.rduels.core.Core;
import ru.merkii.rduels.core.arena.ArenaCore;
import ru.merkii.rduels.core.customkit.CustomKitCore;
import ru.merkii.rduels.core.duel.DuelCore;
import ru.merkii.rduels.core.party.PartyCore;
import ru.merkii.rduels.core.sign.SignCore;
import ru.merkii.rduels.listener.InventoryClickListener;
import ru.merkii.rduels.listener.PlayerListener;
import ru.merkii.rduels.manager.DatabaseManager;
import ru.merkii.rduels.menu.VMenu;
import ru.merkii.rduels.model.KitModel;
import ru.merkii.rduels.placeholder.DuelPAPIHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class RDuels extends JavaPlugin {

    @Getter
    private static RDuels instance;
    private List<Core> cores;
    private Settings settings;
    private KitConfig kitConfig;
    private DatabaseManager databaseManager;
    private PluginConfig pluginConfig;
    private MessageConfiguration pluginMessage;
    private DuelPAPIHook duelPAPIHook;
    private PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        instance = this;
        this.pluginConfig = new PluginConfig(this);
        this.pluginMessage = this.pluginConfig.getMessages();
        this.settings = loadSettings("config.json", Settings.class);
        this.kitConfig = loadSettings("kitConfig.json", KitConfig.class);
        this.cores = new ArrayList<>();
        this.databaseManager = new DatabaseManager();
        this.databaseManager.createTable().join();
        registerListeners(new InventoryClickListener(), new PlayerListener());
        this.duelPAPIHook = new DuelPAPIHook();
        this.duelPAPIHook.register();
        this.paperCommandManager = new PaperCommandManager(this);
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = this.paperCommandManager.getCommandCompletions();
        commandCompletions.registerCompletion("allplayers", c -> getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> !c.getPlayer().getName().equalsIgnoreCase(name)).collect(Collectors.toList()));
        commandCompletions.registerCompletion("duelkits", c -> this.kitConfig.getKits().stream().map(KitModel::getDisplayName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("materials", c -> Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()));
        registerCores(new PartyCore(), new CustomKitCore(), new ArenaCore(), new DuelCore(), new SignCore());
        registerCommands(new DayCommand(), new NightCommand());
    }

    public void registerCommands(BaseCommand... baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            this.paperCommandManager.registerCommand(baseCommand);
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getOnlinePlayers().stream().filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof VMenu).forEach(Player::closeInventory);
        this.cores.forEach(core -> core.disable(this));
        this.duelPAPIHook.unregister();
    }

    private void registerCores(Core... cores) {
        for (Core core : cores) {
            core.enable(this);
            this.cores.add(core);
        }
    }

    public <T extends Config> T loadSettings(String file, Class<T> clazz) {
        return Config.load(this, file, clazz);
    }

    public void reloadConfigs() {
        for (Core core : this.cores) {
            core.reloadConfig(this);
        }
        this.settings = loadSettings("config.json", Settings.class);
        this.kitConfig = loadSettings("kitConfig.json", KitConfig.class);
    }

    public void debug(String str) {
        if (this.settings.isDebug()) {
            getLogger().info(str);
        }
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

}
