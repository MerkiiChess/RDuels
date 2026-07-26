package ru.merkii.rduels.core.economy;

import jakarta.inject.Singleton;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Thin wrapper over the Vault {@link Economy} service. Fully optional: when Vault or an
 * economy provider is absent, {@link #isEnabled()} is false and every operation is a
 * safe no-op, so the rest of the plugin can call it unconditionally.
 */
@Singleton
public class EconomyService {

    private Economy economy;
    private boolean resolved;

    /** Resolves the economy provider on first use (Vault registers its service after enable). */
    private Economy economy() {
        if (!resolved) {
            resolved = true;
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
                if (provider != null) {
                    economy = provider.getProvider();
                }
            }
        }
        return economy;
    }

    public boolean isEnabled() {
        return economy() != null;
    }

    public double getBalance(OfflinePlayer player) {
        return isEnabled() ? economy().getBalance(player) : 0.0D;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return isEnabled() && economy().has(player, amount);
    }

    /** Withdraws {@code amount}; returns true only when the transaction succeeded. */
    public boolean withdraw(OfflinePlayer player, double amount) {
        if (!isEnabled() || amount <= 0) {
            return false;
        }
        return economy().withdrawPlayer(player, amount).transactionSuccess();
    }

    /** Deposits {@code amount}; returns true only when the transaction succeeded. */
    public boolean deposit(OfflinePlayer player, double amount) {
        if (!isEnabled() || amount <= 0) {
            return false;
        }
        return economy().depositPlayer(player, amount).transactionSuccess();
    }

    public String format(double amount) {
        return isEnabled() ? economy().format(amount) : String.valueOf(amount);
    }

}
