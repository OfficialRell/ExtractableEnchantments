package mc.rellox.extractableenchantments.api.item.order;

import java.util.List;
import java.util.function.Supplier;

import mc.rellox.extractableenchantments.text.content.Content;

public interface IOrder {
	
	/**
	 * Submits a content supplier for processing.
	 * 
	 * @param type - type of content
	 * @param supplier - content supplier
	 */
	
	void submit(String type, Supplier<List<Content>> supplier);
	
	/**
	 * @return Built list of content in order
	 */
	
	List<String> build();
	
	/**
	 * Sets the named content and overflow.
	 * 
	 * @param overflow - name content and overflow
	 */
	
	void named(List<Content> overflow);

}
