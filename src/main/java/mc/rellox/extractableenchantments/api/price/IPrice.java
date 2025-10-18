package mc.rellox.extractableenchantments.api.price;

import org.bukkit.entity.Player;

import mc.rellox.extractableenchantments.text.content.Content;

public interface IPrice {
	
	/**
	 * @return Price type
	 */
	
	PriceType type();
	
	/**
	 * @return Price value
	 */
	
	int value();
	
	/**
	 * @param player - player
	 * @return If the player has this amount of the price
	 */
	
	boolean has(Player player);
	
	/**
	 * Removed the price from the player.
	 * 
	 * @param player - player
	 */
	
	void remove(Player player);
	
	/**
	 * @param player - player
	 * @return Player balance value
	 */
	
	int balance(Player player);
	
	/**
	 * @return Insufficient funds content
	 */
	
	Content insufficient();
	
	/**
	 * @return Price display content
	 */
	
	Content text();

}
