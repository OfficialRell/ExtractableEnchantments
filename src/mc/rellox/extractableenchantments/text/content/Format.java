package mc.rellox.extractableenchantments.text.content;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Format {
	
	public static final Format none = new Format("");
	public static final Format bold = new Format("�l");
	public static final Format italic = new Format("�o");
	public static final Format underline = new Format("�n");
	public static final Format strikethrough = new Format("�m");
	public static final Format obfuscated = new Format("�k");
	
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
