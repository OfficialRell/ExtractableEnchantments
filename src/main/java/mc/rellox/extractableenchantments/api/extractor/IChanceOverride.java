package mc.rellox.extractableenchantments.api.extractor;

public interface IChanceOverride {
	
	static IChanceOverride emtpy = new IChanceOverride() {
		@Override
		public boolean enabled() {return false;}
		@Override
		public int value() {return 0;}
	};
	
	/**
	 * @return Is chance override enabled
	 */
	
	boolean enabled();
	
	/**
	 * @return Chance override value
	 */
	
	int value();

}
