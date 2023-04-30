package mc.rellox.extractableenchantments;

import java.util.List;

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

import mc.rellox.extractableenchantments.commands.CommandRegistry;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.supplier.CustomEnchantsSupplier;
import mc.rellox.extractableenchantments.supplier.ESupplier;
import mc.rellox.extractableenchantments.supplier.EcoEnchantsSupplier;
import mc.rellox.extractableenchantments.supplier.EconomySupplier;
import mc.rellox.extractableenchantments.supplier.ExcellentEnchantsSupplier;
import mc.rellox.extractableenchantments.supplier.ESupplier.HookType;
import mc.rellox.extractableenchantments.utils.Metrics;
import mc.rellox.extractableenchantments.utils.Utils;
import mc.rellox.extractableenchantments.utils.Version;

public class ExtractableEnchantments extends JavaPlugin {
	
	private static Plugin plugin;
	
	private static final double VERSION_PLUGIN = 9.9;
	
	public static final EconomySupplier ECONOMY =
			(EconomySupplier) ESupplier.of(HookType.economy);//new EconomySupplier();
	
	public static final ExcellentEnchantsSupplier EXCELLENT_ENCHANTS =
			(ExcellentEnchantsSupplier) ESupplier.of(HookType.excellent_enchant);//new ExcellentEnchantsSupplier();
	public static final CustomEnchantsSupplier CUSTOM_ENCHANTS =
			(CustomEnchantsSupplier) ESupplier.of(HookType.custom_enchants);//new CustomEnchantsSupplier();
	public static final EcoEnchantsSupplier ECO_ENCHANTS =
			(EcoEnchantsSupplier) ESupplier.of(HookType.eco_enchants);//new EcoEnchantsSupplier();
    
	private boolean loaded;
	
    @Override
    public void onLoad() {
		plugin = this;
		loaded = Version.version != null;
    }
	
	@Override
	public void onEnable() {
		if(loaded == true) {
			Utils.check(73954, s -> {
				if(Utils.isDouble(s) == false) return; 
				double v = Double.parseDouble(s);
				if(v <= VERSION_PLUGIN) return;
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.YELLOW + "A newer version is available! "
						+ ChatColor.GOLD + "To download visit: " + "https://www.spigotmc.org/resources/extractable-enchantments.73954/");
				new UpdateAvailable();
			});
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "Extractable Enchantments " + 
					ChatColor.AQUA + "v" + VERSION_PLUGIN + ChatColor.GREEN + " enabled!");
			if(ECONOMY != null) {
				ECONOMY.load();
				if(ECONOMY.get() != null) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_AQUA + "Vault has been found, economy enabled!");
			}
			if(EXCELLENT_ENCHANTS != null) {
				EXCELLENT_ENCHANTS.load();
				if(EXCELLENT_ENCHANTS.get() != null) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_BLUE + "ExcellentEnchants has been found!");
			}
			if(CUSTOM_ENCHANTS != null) {
				CUSTOM_ENCHANTS.load();
				if(CUSTOM_ENCHANTS.get() != null) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_BLUE + "Custom Enchantments has been found!");
			}
			if(ECO_ENCHANTS != null) {
				ECO_ENCHANTS.load();
				if(ECO_ENCHANTS.get() != null) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_BLUE + "EcoEnchants has been found!");
			}
			Configuration.initialize();
			Language.initialize();
			ExtractorRegistry.initialize();
			DustRegistry.initialize();
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
				ChatColor.AQUA + "v" + VERSION_PLUGIN + ChatColor.GOLD + " disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		CommandRegistry.onCommand(sender, command, args);
		return super.onCommand(sender, command, label, args);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return CommandRegistry.onTabComplete(sender, command, args);
	}
	
	public static Plugin instance() {
		return plugin;
	}
	
	public static void update(CommandSender sender) {
		Configuration.initialize();
		Language.initialize();
		ExtractorRegistry.update();
		DustRegistry.update();
		if(sender == null) return;
		sender.sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.AQUA + "Reloading plugin...");
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
			if(player.isOp() == false) return; 
			player.sendMessage(ChatColor.DARK_AQUA + "[EE] " + 
					ChatColor.YELLOW + "A newer version is available! " + ChatColor.GOLD + "To download visit: " + 
					"https://www.spigotmc.org/resources/extractable-enchantments.73954/");
		}

	}

}
