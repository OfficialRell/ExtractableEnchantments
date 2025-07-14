package mc.rellox.extractableenchantments.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconomyHook implements IHook {
	
	private Economy economy;

	@Override
	public String name() {
		return "Vault";
	}
	
	@Override
	public void enable() {
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		economy = provider == null ? null : provider.getProvider();
	}
	
	public ICurrency currency() {
		if(economy == null) return null;
		return new ICurrency() {
			@Override
			public double get(Player player) {
				return economy.getBalance(player);
			}
			@Override
			public void remove(Player player, double value) {
				economy.withdrawPlayer(player, value);
			}
		};
	}

}
