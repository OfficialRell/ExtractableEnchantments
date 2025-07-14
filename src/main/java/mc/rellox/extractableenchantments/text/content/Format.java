package mc.rellox.extractableenchantments.text.content;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.rellox.extractableenchantments.text.Text;

public final class Format {
	
	public static final Format none = new Format("");
	public static final Format bold = new Format(Text.color_code + "l");
	public static final Format italic = new Format(Text.color_code + "o");
	public static final Format underline = new Format(Text.color_code + "n");
	public static final Format strikethrough = new Format(Text.color_code + "m");
	public static final Format obfuscated = new Format(Text.color_code + "k");
	
	public static Format of(Format... fs) {
		return new Format(Stream.of(fs)
				.map(Format::format)
				.collect(Collectors.joining()));
	}
	
	public static Format of(List<Format> list) {
		return new Format(list.stream()
				.map(Format::format)
				.collect(Collectors.joining()));
	}
	
	public final String format;
	
	private Format(String format) {
		this.format = format;
	}
	
	public String format() {
		return format;
	}
	
	@Override
	public String toString() {
		return format;
	}

}
