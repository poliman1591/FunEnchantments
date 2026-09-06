package ru.poliman1591.funenchantments;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;

import java.util.*;

public class EnchantmentManager implements Listener {

    private final JavaPlugin plugin;

    private final NamespacedKey bulldozerKey;
    private final NamespacedKey autosmeltKey;
    private final NamespacedKey magnetKey;
    private final NamespacedKey lumberjackKey;
    private final NamespacedKey efficiencyKey;
    private final NamespacedKey fortuneKey;
    private final NamespacedKey protectionKey;
    private final NamespacedKey sharpnessKey;
    private final NamespacedKey strengthKey;
    private final NamespacedKey unbreakingKey;

    public EnchantmentManager(JavaPlugin plugin) {
        this.plugin = plugin;

        bulldozerKey = new NamespacedKey(plugin, "bulldozer");
        autosmeltKey = new NamespacedKey(plugin, "autosmelt");
        magnetKey = new NamespacedKey(plugin, "magnet");
        lumberjackKey = new NamespacedKey(plugin, "lumberjack");
        efficiencyKey = new NamespacedKey(plugin, "efficiency_custom");
        fortuneKey = new NamespacedKey(plugin, "fortune_custom");
        protectionKey = new NamespacedKey(plugin, "protection_custom");
        sharpnessKey = new NamespacedKey(plugin, "sharpness_custom");
        strengthKey = new NamespacedKey(plugin, "strength_custom");
        unbreakingKey = new NamespacedKey(plugin, "unbreaking_custom");
    }

    // =========================================================
    // Установка чар на предмет
    // =========================================================

    public void setEnchantment(ItemStack item, String enchantment, int level) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        NamespacedKey key = getKey(enchantment);

        if (key == null) {
            return;
        }

        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.INTEGER,
                level
        );

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.removeIf(line ->
                line.toLowerCase(Locale.ROOT)
                        .startsWith(getDisplayName(enchantment).toLowerCase(Locale.ROOT))
        );

        lore.add("§7" + getDisplayName(enchantment) + " " + toRoman(level));

        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    private NamespacedKey getKey(String enchantment) {

        return switch (enchantment.toLowerCase(Locale.ROOT)) {
            case "бульдозер", "bulldozer" -> bulldozerKey;
            case "автоплавка", "autosmelt" -> autosmeltKey;
            case "магнит", "magnet" -> magnetKey;
            case "лесоруб", "lumberjack" -> lumberjackKey;
            case "эффективность", "efficiency" -> efficiencyKey;
            case "удача", "fortune" -> fortuneKey;
            case "защита", "protection" -> protectionKey;
            case "острота", "sharpness" -> sharpnessKey;
            case "сила", "strength" -> strengthKey;
            case "прочность", "unbreaking" -> unbreakingKey;
            default -> null;
        };
    }

    private String getDisplayName(String enchantment) {

        return switch (enchantment.toLowerCase(Locale.ROOT)) {
            case "бульдозер", "bulldozer" -> "Бульдозер";
            case "автоплавка", "autosmelt" -> "Автоплавка";
            case "магнит", "magnet" -> "Магнит";
            case "лесоруб", "lumberjack" -> "Лесоруб";
            case "эффективность", "efficiency" -> "Эффективность";
            case "удача", "fortune" -> "Удача";
            case "защита", "protection" -> "Защита";
            case "острота", "sharpness" -> "Острота";
            case "сила", "strength" -> "Сила";
            case "прочность", "unbreaking" -> "Прочность";
            default -> enchantment;
        };
    }

    private String toRoman(int number) {

        String[] values = {
                "", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX", "X"
        };

        if (number >= 1 && number <= 10) {
            return values[number];
        }

        return String.valueOf(number);
    }

    // =========================================================
    // Получение уровня чара
    // =========================================================

    private int getLevel(ItemStack item, NamespacedKey key) {

        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return 0;
        }

        Integer level = meta.getPersistentDataContainer()
                .get(key, PersistentDataType.INTEGER);

        return level == null ? 0 : level;
    }

    // =========================================================
    // БУЛЬДОЗЕР
    // =========================================================

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        ItemStack tool = player.getInventory().getItemInMainHand();

        int level = getLevel(tool, bulldozerKey);

        if (level <= 0) {
            return;
        }

        Block center = event.getBlock();

        int radius = Math.min(level, 3);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (x == 0 && z == 0) {
                    continue;
                }

                Block block = center.getRelative(x, 0, z);

                if (block.getType().isAir()) {
                    continue;
                }

                if (!block.getType().isSolid()) {
                    continue;
                }

                block.breakNaturally(tool);
            }
        }
    }

    // =========================================================
    // АВТОПЛАВКА
    // =========================================================

    @EventHandler
    public void onAutoSmelt(BlockBreakEvent event) {

        Player player = event.getPlayer();

        ItemStack tool = player.getInventory().getItemInMainHand();

        int level = getLevel(tool, autosmeltKey);

        if (level <= 0) {
            return;
        }

        Material type = event.getBlock().getType();

        Material result = switch (type) {

            case IRON_ORE, DEEPSLATE_IRON_ORE ->
                    Material.IRON_INGOT;

            case GOLD_ORE, DEEPSLATE_GOLD_ORE ->
                    Material.GOLD_INGOT;

            case COPPER_ORE, DEEPSLATE_COPPER_ORE ->
                    Material.COPPER_INGOT;

            case ANCIENT_DEBRIS ->
                    Material.NETHERITE_SCRAP;

            case RAW_IRON_BLOCK ->
                    Material.IRON_BLOCK;

            case RAW_GOLD_BLOCK ->
                    Material.GOLD_BLOCK;

            case RAW_COPPER_BLOCK ->
                    Material.COPPER_BLOCK;

            default -> null;
        };

        if (result == null) {
            return;
        }

        event.setDropItems(false);

        Item item = event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation(),
                new ItemStack(result)
        );

        if (level >= 2) {
            item.setPickupDelay(0);
        }
    }

    // =========================================================
    // МАГНИТ
    // =========================================================

    public void pullItems(Player player) {

        int level = getLevel(
                player.getInventory().getItemInMainHand(),
                magnetKey
        );

        if (level <= 0) {
            return;
        }

        double radius = 3.0 + level * 2.0;

        for (Item item : player.getWorld().getEntitiesByClass(Item.class)) {

            if (item.getLocation().distanceSquared(player.getLocation())
                    > radius * radius) {
                continue;
            }

            if (item.isDead()) {
                continue;
            }

            org.bukkit.util.Vector direction =
                    player.getLocation().add(0, 1, 0)
                            .toVector()
                            .subtract(item.getLocation().toVector())
                            .normalize();

            item.setVelocity(direction.multiply(0.5 + level * 0.15));
        }
    }

    // =========================================================
    // ЛЕСОРУБ
    // =========================================================

    @EventHandler
    public void onLumberjack(BlockBreakEvent event) {

        Player player = event.getPlayer();

        ItemStack tool = player.getInventory().getItemInMainHand();

        int level = getLevel(tool, lumberjackKey);

        if (level <= 0) {
            return;
        }

        Material type = event.getBlock().getType();

        if (!isLog(type)) {
            return;
        }

        int maxBlocks;

        if (level == 1) {
            maxBlocks = 2;
        } else if (level == 2) {
            maxBlocks = 64;
        } else {
            maxBlocks = 256;
        }

        Set<Block> visited = new HashSet<>();

        Queue<Block> queue = new LinkedList<>();

        queue.add(event.getBlock());

        while (!queue.isEmpty() && visited.size() < maxBlocks) {

            Block block = queue.poll();

            if (!visited.add(block)) {
                continue;
            }

            if (!isLog(block.getType())) {
                continue;
            }

            if (block.equals(event.getBlock())) {
                continue;
            }

            block.breakNaturally(tool);

            for (Block nearby : getNearbyBlocks(block)) {

                if (!visited.contains(nearby)) {
                    queue.add(nearby);
                }
            }
        }
    }

    private boolean isLog(Material material) {

        String name = material.name();

        return name.endsWith("_LOG")
                || name.endsWith("_WOOD")
                || name.equals("CRIMSON_STEM")
                || name.equals("WARPED_STEM")
                || name.equals("STRIPPED_CRIMSON_STEM")
                || name.equals("STRIPPED_WARPED_STEM");
    }

    private List<Block> getNearbyBlocks(Block block) {

        List<Block> blocks = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    blocks.add(block.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    // =========================================================
    // УРОН ОТ ОСТРОТЫ И СИЛЫ
    // =========================================================

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack weapon =
                player.getInventory().getItemInMainHand();

        int sharpness =
                getLevel(weapon, sharpnessKey);

        int strength =
                getLevel(weapon, strengthKey);

        double extraDamage = 0;

        if (sharpness > 0) {

            if (sharpness <= 5) {
                extraDamage += sharpness * 1.0;
            } else {
                extraDamage += 5.0 + (sharpness - 5) * 1.5;
            }
        }

        if (strength > 0) {
            extraDamage += strength * 1.5;
        }

        if (extraDamage > 0) {
            event.setDamage(event.getDamage() + extraDamage);
        }
    }

    // =========================================================
    // МАГНИТ — периодическое притягивание
    // =========================================================

    public void startMagnetTask() {

        plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {

                    for (Player player :
                            plugin.getServer().getOnlinePlayers()) {

                        pullItems(player);
                    }

                },
                1L,
                5L
        );
    }
}
