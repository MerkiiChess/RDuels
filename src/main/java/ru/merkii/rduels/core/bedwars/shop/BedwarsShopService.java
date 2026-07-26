package ru.merkii.rduels.core.bedwars.shop;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.bedwars.bucket.BedwarsGameBucket;
import ru.merkii.rduels.core.bedwars.config.BedwarsShopConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsShopItem;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeConfiguration;
import ru.merkii.rduels.core.bedwars.config.BedwarsUpgradeEntry;
import ru.merkii.rduels.core.bedwars.config.UpgradeLevel;
import ru.merkii.rduels.core.bedwars.game.BedwarsGame;
import ru.merkii.rduels.core.bedwars.game.BedwarsTeam;
import ru.merkii.rduels.core.bedwars.game.PlayerBuyState;
import ru.merkii.rduels.core.bedwars.loadout.LoadoutService;
import ru.merkii.rduels.core.bedwars.resource.ResourceService;
import ru.merkii.rduels.core.bedwars.resource.ResourceType;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeService;
import ru.merkii.rduels.core.bedwars.upgrade.UpgradeType;

import java.util.ArrayList;
import java.util.List;

/** Builds and drives the Bedwars item shop and team upgrade shop. */
@Singleton
public class BedwarsShopService {

    private final BedwarsShopConfiguration shopConfig;
    private final BedwarsUpgradeConfiguration upgradeConfig;
    private final BedwarsGameBucket gameBucket;
    private final ResourceService resourceService;
    private final LoadoutService loadoutService;
    private final UpgradeService upgradeService;
    private final MessageConfig messages;

    @Inject
    public BedwarsShopService(BedwarsShopConfiguration shopConfig, BedwarsUpgradeConfiguration upgradeConfig,
                              BedwarsGameBucket gameBucket, ResourceService resourceService,
                              LoadoutService loadoutService, UpgradeService upgradeService, MessageConfig messages) {
        this.shopConfig = shopConfig;
        this.upgradeConfig = upgradeConfig;
        this.gameBucket = gameBucket;
        this.resourceService = resourceService;
        this.loadoutService = loadoutService;
        this.upgradeService = upgradeService;
        this.messages = messages;
    }

    public void openShop(Player player) {
        BedwarsGame game = gameBucket.byPlayer(player.getUniqueId());
        if (game == null) {
            return;
        }
        BedwarsMenuHolder holder = new BedwarsMenuHolder(game, BedwarsMenuHolder.Type.SHOP);
        Inventory inventory = Bukkit.createInventory(holder, 54, MiniMessage.miniMessage().deserialize(shopConfig.title()));
        holder.setInventory(inventory);
        for (BedwarsShopItem item : shopConfig.items()) {
            if (item.slot() >= 0 && item.slot() < inventory.getSize()) {
                inventory.setItem(item.slot(), icon(Material.matchMaterial(item.material()), item.name(), item.lore(),
                        item.costAmount() + " " + resourceName(item.costResource())));
            }
        }
        player.openInventory(inventory);
    }

    public void openUpgrades(Player player) {
        BedwarsGame game = gameBucket.byPlayer(player.getUniqueId());
        if (game == null) {
            return;
        }
        BedwarsTeam team = game.teamOf(player.getUniqueId());
        BedwarsMenuHolder holder = new BedwarsMenuHolder(game, BedwarsMenuHolder.Type.UPGRADES);
        Inventory inventory = Bukkit.createInventory(holder, 27, MiniMessage.miniMessage().deserialize(upgradeConfig.title()));
        holder.setInventory(inventory);
        for (BedwarsUpgradeEntry entry : upgradeConfig.upgrades()) {
            UpgradeType type = UpgradeType.fromName(entry.type());
            if (type == null || entry.slot() < 0 || entry.slot() >= inventory.getSize()) {
                continue;
            }
            UpgradeLevel next = upgradeService.nextLevel(team, type);
            int level = upgradeService.currentLevel(team, type);
            String cost = next == null ? "МАКС" : next.costAmount() + " " + resourceName(next.costResource());
            inventory.setItem(entry.slot(), icon(Material.matchMaterial(entry.material()), entry.name(),
                    List.of(), "Уровень " + level + "/" + upgradeService.maxLevel(type) + " · " + cost));
        }
        player.openInventory(inventory);
    }

    public void handleClick(Player player, BedwarsMenuHolder holder, int slot) {
        if (holder.getType() == BedwarsMenuHolder.Type.SHOP) {
            shopConfig.items().stream().filter(item -> item.slot() == slot).findFirst()
                    .ifPresent(item -> buyItem(player, holder.getGame(), item));
        } else {
            upgradeConfig.upgrades().stream().filter(entry -> entry.slot() == slot).findFirst()
                    .ifPresent(entry -> buyUpgrade(player, holder.getGame(), entry));
        }
    }

    private void buyItem(Player player, BedwarsGame game, BedwarsShopItem item) {
        ResourceType resource = ResourceType.fromName(item.costResource());
        if (resource == null) {
            return;
        }
        if (!resourceService.take(player, resource.material(), item.costAmount())) {
            messages.sendTo(player, "bedwars-not-enough");
            return;
        }
        BedwarsTeam team = game.teamOf(player.getUniqueId());
        PlayerBuyState buy = game.buyState(player.getUniqueId());
        Material material = Material.matchMaterial(item.material());
        switch (item.category().toLowerCase()) {
            case "armor" -> {
                buy.upgradeArmor(item.tier());
                loadoutService.give(game, team, player);
            }
            case "pickaxe" -> {
                buy.upgradePickaxe(material, item.tier());
                player.getInventory().addItem(new ItemStack(material));
            }
            case "axe" -> {
                buy.upgradeAxe(material, item.tier());
                player.getInventory().addItem(new ItemStack(material));
            }
            case "shears" -> {
                buy.giveShears();
                player.getInventory().addItem(new ItemStack(Material.SHEARS));
            }
            default -> player.getInventory().addItem(buildItem(item));
        }
        loadoutService.reapplyUpgrades(team, player);
        messages.sendTo(player, "bedwars-bought");
    }

    private void buyUpgrade(Player player, BedwarsGame game, BedwarsUpgradeEntry entry) {
        UpgradeType type = UpgradeType.fromName(entry.type());
        BedwarsTeam team = game.teamOf(player.getUniqueId());
        if (type == null || team == null) {
            return;
        }
        UpgradeLevel next = upgradeService.nextLevel(team, type);
        if (next == null) {
            messages.sendTo(player, "bedwars-upgrade-max");
            return;
        }
        ResourceType resource = ResourceType.fromName(next.costResource());
        if (resource == null || !resourceService.take(player, resource.material(), next.costAmount())) {
            messages.sendTo(player, "bedwars-not-enough");
            return;
        }
        upgradeService.applyPurchase(team, type);
        // Re-apply the new upgrade to every online team member.
        for (java.util.UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                loadoutService.reapplyUpgrades(team, member);
            }
        }
        messages.sendTo(player, "bedwars-upgrade-bought");
        openUpgrades(player);
    }

    private ItemStack buildItem(BedwarsShopItem item) {
        Material material = Material.matchMaterial(item.material());
        ItemStack stack = new ItemStack(material == null ? Material.STONE : material, Math.max(1, item.amount()));
        item.enchants().forEach((name, level) -> {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
            if (enchantment != null) {
                stack.addUnsafeEnchantment(enchantment, level);
            }
        });
        return stack;
    }

    private ItemStack icon(Material material, String name, List<String> lore, String costLine) {
        ItemStack stack = new ItemStack(material == null ? Material.BARRIER : material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (name != null && !name.isEmpty()) {
                meta.displayName(MiniMessage.miniMessage().deserialize(name).decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
            }
            List<Component> lines = new ArrayList<>();
            for (String line : lore) {
                lines.add(MiniMessage.miniMessage().deserialize(line));
            }
            lines.add(MiniMessage.miniMessage().deserialize("<yellow>Цена: <white>" + costLine));
            meta.lore(lines);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private String resourceName(String resource) {
        ResourceType type = ResourceType.fromName(resource);
        return type == null ? resource : switch (type) {
            case IRON -> "железа";
            case GOLD -> "золота";
            case DIAMOND -> "алмазов";
            case EMERALD -> "изумрудов";
        };
    }
}
