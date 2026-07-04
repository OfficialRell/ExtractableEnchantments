package mc.rellox.extractableenchantments;

import mc.rellox.extractableenchantments.command.CommandRegistry;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.event.EventRegistry;
import mc.rellox.extractableenchantments.hook.HookRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.utility.Keys;
import mc.rellox.extractableenchantments.utility.Utility;
import mc.rellox.extractableenchantments.utility.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ExtractableEnchantments extends JavaPlugin {

	private static Plugin plugin;
	
	private boolean loaded;
	
    @Override
    public void onLoad() {
		plugin = this;
		loaded = Version.version != null;
    }
	
	@Override
	public void onEnable() {
		if(loaded) {
			var version = version();
			Utility.check(73954, s -> {
				if(!Utility.isDouble(s) || !Utility.isDouble(version)) return;
				double v = Double.parseDouble(s);
				double c = Double.parseDouble(version());
				if(v >= c) return;
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.YELLOW + "A newer version is available! "
						+ ChatColor.GOLD + "To download visit: " + "https://www.spigotmc.org/resources/extractable-enchantments.73954/");
				new UpdateAvailable();
			});
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "Extractable Enchantments " + 
					ChatColor.AQUA + "v" + version + ChatColor.GREEN + " enabled!");
			
			Keys.initialize();
			EnchantmentRegistry.initialize();
			HookRegistry.initialize();
			Configuration.initialize();
			EventRegistry.initialize();
			
			initializeMetrics();
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
					+ ChatColor.RED + "Unable to load, invalid server version!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "Extractable Enchantments " + 
				ChatColor.AQUA + "v" + version() + ChatColor.GOLD + " disabled!");
	}
	
	@Override
	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command,
							 @NonNull String label, String @NonNull [] args) {
		CommandRegistry.onCommand(sender, command, args);
		return super.onCommand(sender, command, label, args);
	}
	
	@Override
	public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command,
									  @NonNull String alias, String @NonNull [] args) {
		return CommandRegistry.onTabComplete(sender, command, args);
	}
	
	public static Plugin instance() {
		return plugin;
	}

	public static String version() {
		return plugin.getDescription().getVersion();
	}
	
	private void initializeMetrics() {
		new Metrics(this, 8524);
	}
	
	private static class UpdateAvailable implements Listener {
		
		public UpdateAvailable() {
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}

		@EventHandler
		private void onJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			if(!player.isOp()) return; 
			player.sendMessage(ChatColor.DARK_AQUA + "[EE] " + 
					ChatColor.YELLOW + "A newer version is available! " + ChatColor.GOLD + "To download visit: " + 
					"https://www.spigotmc.org/resources/extractable-enchantments.73954/");
		}

	}

}
