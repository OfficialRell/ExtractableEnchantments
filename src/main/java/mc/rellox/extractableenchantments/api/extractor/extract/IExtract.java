package mc.rellox.extractableenchantments.api.extractor.extract;

public interface IExtract {
	
	/**
	 * @return Can this extractor extract unsafe enchantments (e.g. Sharpness X, Mending III, etc.)
	 */
	
	boolean unsafe();
	
	/**
	 * RANDOM or SELECTION.
	 * 
	 * @return Extraction type
	 */
	
	ExtractType type();
	
	/**
	 * ALL, MINECRAFT or CUSTOM.
	 * 
	 * @return Extraction filter
	 */
	
	ExtractFilter filter();
	
	/**
	 * @return Accepted enchantment checker
	 */
	
	IAccepted accepted();
	
	/**
	 * @return Can hidden enchantments be extracted
	 */
	
	boolean hidden();

}
