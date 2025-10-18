package mc.rellox.extractableenchantments.api.price;

public enum PriceType {
	
	EXPERIENCE_POINTS,
	EXPERIENCE_LEVELS,
	MATERIALS,
	ECONOMY;
	
	/**
	 * @return Price type key
	 */
	
	public String key() {
		return name().replace('_', '-').toLowerCase();
	}

}
