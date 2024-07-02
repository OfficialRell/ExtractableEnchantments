package mc.rellox.extractableenchantments.extractor;

import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.price.IPrice;

public record ExtractPrice(boolean enabled, IPrice price) implements IExtractPrice {
	public ExtractPrice(IPrice price) {
		this(true, price);
	}
}