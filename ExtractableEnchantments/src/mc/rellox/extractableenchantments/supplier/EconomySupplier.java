package mc.rellox.extractableenchantments.supplier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public final class EconomySupplier {
	
	private Economy economy;
	
	public Economy get() {
		return this.economy;
	}
	
	public void load() {
		this.economy = loadEconomy();
	}
	
	private static Economy loadEconomy() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return null;
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		return provider == null ? null : provider.getProvider();
	}

}
