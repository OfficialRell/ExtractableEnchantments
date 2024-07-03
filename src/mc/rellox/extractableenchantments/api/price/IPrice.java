package mc.rellox.extractableenchantments.api.price;

import org.bukkit.entity.Player;

import mc.rellox.extractableenchantments.text.content.Content;

public interface IPrice {
	
	PriceType type();
	
	int value();
	
	boolean has(Player player);
	
	void remove(Player player);
	
	int balance(Player player);
	
	Content insufficient();
	
	Content text();

}
