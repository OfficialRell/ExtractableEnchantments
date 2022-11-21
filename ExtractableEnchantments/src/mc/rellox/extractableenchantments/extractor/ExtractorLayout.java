package mc.rellox.extractableenchantments.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ExtractorLayout {
	
	private final List<LayoutType> layout;
	
	public ExtractorLayout(List<String> list) {
		if(list == null || list.isEmpty() == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
					+ ChatColor.DARK_RED + "Unable to read extractor layout, cannot have an empty layout. "
							+ "Using default layout.");
			this.layout = getDefault();
			return;
		}
		boolean valid = true;
		List<LayoutType> layout = new ArrayList<>();
		boolean[] bs = new boolean[4];
		for(String s : list) {
			LayoutType type = LayoutType.of(s);
			if(type == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_RED + "Unable to read extractor layout, invalid value (" + s + "). "
								+ "Using default layout.");
				valid = false;
				break;
			}
			if(type != LayoutType.EMPTY) {
				int i = type.ordinal() - 1;
				if(bs[i] == false) {
					layout.add(type);
					bs[i] = true;
				}
			} else layout.add(type);
		}
		if(valid == true) {
			if((valid = bs[0] && bs[1] && bs[2] && bs[3]) == false) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
						+ ChatColor.DARK_RED + "Unable to read extractor layout, "
								+ "must have all layout types (INFO, CHANCE, DESTROY and COST). "
								+ "Using default layout.");
			}
		}
		this.layout = valid == true ? layout : getDefault();
	}
	
	public List<String> build(List<String> info, String chance, String destroy, String cost) {
		List<String> lore = new ArrayList<>();
		for(LayoutType type : layout) {
			if(type == LayoutType.EMPTY) lore.add("");
			else if(type == LayoutType.INFO) lore.addAll(info);
			else if(type == LayoutType.CHANCE) {
				if(chance != null) lore.add(chance);
			} else if(type == LayoutType.DESTROY) {
				if(destroy != null) lore.add(destroy);
			} else if(type == LayoutType.COST) {
				if(cost != null) lore.add(cost);
			}
		}
		Iterator<String> it = lore.iterator();
		int i = 0;
		while(it.hasNext() == true) {
			String current = it.next();
			if(current == null || current.equals("") == true) {
				if(i + 1 < lore.size()) {
					String next = lore.get(i + 1);
					if(next == null || next.equals("") == true) {
						it.remove();
						continue;
					}
				}
			}
			i++;
		}
		i = lore.size() - 1;
		String last = lore.get(i);
		if(last == null || last.equals("") == true) lore.remove(i);
		return lore;
	}
	
	private static enum LayoutType {
		
		EMPTY, INFO, CHANCE, DESTROY, COST;
		
		public static LayoutType of(String name) {
			try {
				return valueOf(name);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	private static List<LayoutType> getDefault() {
		return Arrays.asList(LayoutType.INFO, LayoutType.EMPTY, LayoutType.CHANCE,
				LayoutType.DESTROY, LayoutType.EMPTY, LayoutType.COST);
	}

}
