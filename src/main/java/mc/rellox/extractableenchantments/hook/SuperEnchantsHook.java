package mc.rellox.extractableenchantments.hook;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SuperEnchantsHook implements IHook, IEnchantmentReader {

    private Object manager;
    private Object reader;
    private Object applier;

    private Object mini_message;

    @Override
    public String name() {
        return "SuperEnchants";
    }

    @Override
    public void enable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SuperEnchants");
        manager = RF.fetch(plugin, "enchantManager");
        reader = RF.fetch(
                RF.get("com.maddoxh.superEnchants.items.EnchantReader"),
                "INSTANCE"
        );
        applier = RF.fetch(
                RF.get("com.maddoxh.superEnchants.items.EnchantApplicator"),
                "INSTANCE"
        );
        mini_message = RF.fetch(
                RF.get("net.kyori.adventure.text.minimessage.MiniMessage"),
                "miniMessage"
        );
    }

    @Override
    public String key() {
        return name().toLowerCase();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<IEnchantment, Integer> enchantments(ItemStack item) {
        Map<IEnchantment, Integer> map = new HashMap<>();
        if(manager == null
                || ItemRegistry.nulled(item)
                || !item.hasItemMeta()) return map;

        var enchantments = RF.order(reader, "readEnchants", ItemStack.class)
                .as(Map.class)
                .invoke(item);

        enchantments.forEach((enchantment, level) -> {
            var custom = RF.order(manager, "get", String.class)
                    .invoke(enchantment);
            if(custom == null) return;
            var key = RF.direct(custom, "getId", String.class);
            var formatted = RF.direct(custom, "getDisplayName", String.class);

            var name = RF.order(mini_message, "stripTags", String.class)
                    .as(String.class)
                    .invoke(formatted);

            var maximum = RF.direct(custom, "getMaxLevel", int.class);
            map.put(new SuperEnchantment(this, custom, key, name, maximum), (int) level);
        });

        return map;
    }

    public record SuperEnchantment(
            SuperEnchantsHook hook,
            Object enchantment,
            String key,
            String name,
            int maximum
    ) implements IEnchantment {

        @Override
        public void remove(ItemStack item) {
            RF.order(hook.applier, "removeEnchant", ItemStack.class, String.class)
                    .invoke(item, key);
        }

        @Override
        public void apply(ItemStack item, int level) {
            RF.order(hook.applier, "applyEnchant", ItemStack.class, String.class, int.class)
                    .invoke(item, key, level);
        }

    }

}