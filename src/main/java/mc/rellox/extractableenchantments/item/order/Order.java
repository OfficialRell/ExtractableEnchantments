package mc.rellox.extractableenchantments.item.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import mc.rellox.extractableenchantments.api.item.order.IOrder;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.text.content.Content;

public class Order implements IOrder {
	
	private final OrderList list;
	
	private final Map<String, Supplier<List<Content>>> map;
	
	public Order(OrderList list) {
		this.list = list;
		this.map = new HashMap<>();
	}

	@Override
	public void submit(String key, Supplier<List<Content>> supplier) {
		map.put(key, supplier);
	}
	
	@Override
	public void named(List<Content> name) {
		if(name.isEmpty()) return;
		map.put("NAMED", () -> name);
	}

	@Override
	public List<String> build() {
		List<String> build = new ArrayList<>();
		list.keys().forEach(key -> {
			if(key.equals("!")) build.add("");
			else {
				Supplier<List<Content>> supplier = map.get(key);
				if(supplier == null) return;
				List<Content> contents = supplier.get();
				if(contents == null || contents.isEmpty()) return;
				List<String> text = Text.toText(contents);
				if(text.isEmpty()) return;
				build.addAll(text);
			}
		});
		Text.clean(build);
		return build;
	}
	


}
