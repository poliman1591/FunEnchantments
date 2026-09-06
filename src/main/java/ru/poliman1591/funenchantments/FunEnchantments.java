package ru.poliman1591.funenchantments;

import org.bukkit.plugin.java.JavaPlugin;

public final class FunEnchantments extends JavaPlugin {

    private EnchantmentManager enchantmentManager;

    @Override
    public void onEnable() {

        enchantmentManager = new EnchantmentManager(this);

        getServer().getPluginManager()
                .registerEvents(enchantmentManager, this);

        enchantmentManager.startMagnetTask();

        getLogger().info("FunEnchantments включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FunEnchantments выключен!");
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
    }
}
