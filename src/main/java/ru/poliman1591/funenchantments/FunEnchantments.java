package ru.poliman1591.funenchantments;

import org.bukkit.plugin.java.JavaPlugin;

public final class FunEnchantments extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("FunEnchantments включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FunEnchantments выключен!");
    }
}
