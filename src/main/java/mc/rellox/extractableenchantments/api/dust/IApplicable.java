package mc.rellox.extractableenchantments.api.dust;

import java.util.Set;

import mc.rellox.extractableenchantments.api.extractor.IExtractor;

public interface IApplicable {
	
	/**
	 * @return List of extractors to which this dust can be applied
	 */
	
	Set<IExtractor> extractors();
	
	/**
	 * @return Can this dust be applied to enchantment books
	 */
	
	boolean books();
	
	/**
	 * @param extractor - extractor
	 * @return {@code true} if this dust can be applied to
	 *  the specified extractor
	 */
	
	default boolean accepts(IExtractor extractor) {
		return extractors().contains(extractor);
	}

}
