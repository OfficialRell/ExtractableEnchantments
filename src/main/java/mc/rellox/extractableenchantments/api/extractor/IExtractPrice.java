package mc.rellox.extractableenchantments.api.extractor;

import mc.rellox.extractableenchantments.api.price.IPrice;

public interface IExtractPrice {
	
	static IExtractPrice empty = new IExtractPrice() {
		@Override
		public IPrice price() {return null;}
		@Override
		public boolean enabled() {return false;}
	};
	
	/**
	 * @return Whether the extraction price is enabled
	 */
	
	boolean enabled();
	
	/**
	 * @return The price for extracting an enchantment
	 */
	
	IPrice price();

}
