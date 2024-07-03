package mc.rellox.extractableenchantments.item;

import java.util.List;

import org.bukkit.Material;

import mc.rellox.extractableenchantments.api.item.IItem;
import mc.rellox.extractableenchantments.text.content.Content;

public abstract class Item implements IItem {
	
	protected final Material material;
	protected final List<Content> name;
	protected final List<Content> info;
	protected final boolean glint;
	protected final int model;
	
	public Item(Material material, List<Content> name, List<Content> info,
			boolean glint, int model) {
		this.material = material;
		this.name = name;
		this.info = info;
		this.glint = glint;
		this.model = model;
	}

	@Override
	public Material material() {
		return material;
	}

	@Override
	public List<Content> name() {
		return name;
	}

	@Override
	public List<Content> info() {
		return info;
	}

	@Override
	public boolean glint() {
		return glint;
	}

	@Override
	public int model() {
		return model;
	}

}
