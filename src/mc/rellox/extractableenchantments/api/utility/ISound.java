package mc.rellox.extractableenchantments.api.utility;

import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import mc.rellox.extractableenchantments.api.configuration.IFile;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public interface ISound {
	
	static ISound fetch(IFile file, String key) {
		String path = "Sounds." + key;
		try {
			return of(file.getStrings("Sounds." + key).stream()
					.map(ISound::parse)
					.filter(Objects::nonNull)
					.toArray(ISound[]::new));
		} catch (Exception e) {
			Text.logFail("Unable to read sounds: " + path);
		}
		return empty;
	}
	
	static ISound parse(String format) {
		try {
			String[] ss = format.split(":");
			Sound sound = RF.fielded(Sound.class, ss[0]);
			float volume = (float) (ss.length > 1 ? Double.parseDouble(ss[1]) : 1);
			float pitch = (float) (ss.length > 2 ? Double.parseDouble(ss[2]) : 1);
			return sound == null ? null : of(sound, volume, pitch);
		} catch (Exception e) {
			Text.logFail("Invalid sound format: " + format);
		}
		return null;
	}
	
	static ISound of(Sound sound, float volume, float pitch) {
		return player -> player.playSound(player.getEyeLocation(), sound, volume, pitch);
	}
	
	static ISound of(ISound... sounds) {
		if(sounds == null || sounds.length <= 0) return empty;
		if(sounds.length == 1) return sounds[0];
		return player -> Stream.of(sounds)
				.forEach(sound -> sound.play(player));
	}
	
	static ISound empty = player -> {};
	
	void play(Player player);

}
