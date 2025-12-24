package mc.rellox.extractableenchantments.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface IHook {
	
	String name();
	
	void enable();
	
	default boolean load() {
		Plugin plugin = plugin();
		return plugin != null && plugin.isEnabled();
	}
	
	default Plugin plugin() {
		return Bukkit.getPluginManager().getPlugin(name());
	}

}
