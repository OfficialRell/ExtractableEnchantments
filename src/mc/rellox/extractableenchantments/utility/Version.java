package mc.rellox.extractableenchantments.utility;

import org.bukkit.Bukkit;

public final class Version {
	
	public static final String server;
	
	public static final VersionType version;
	static {
		String s = Bukkit.getServer().getClass().getPackage().getName();
		server = s.substring(s.lastIndexOf('.') + 1);
		if(server.contains("v1_21_R2") == true
				|| Bukkit.getBukkitVersion().startsWith("1.21.3-R0.1") == true) version = VersionType.v_21_2;
		else if(server.contains("v1_21_R1") == true
				|| Bukkit.getBukkitVersion().startsWith("1.21-R0.1") == true
				|| Bukkit.getBukkitVersion().startsWith("1.21.1-R0.1") == true) version = VersionType.v_21_1;
		else if(server.contains("v1_20_R4") == true
				|| Bukkit.getBukkitVersion().startsWith("1.20.6-R0.1") == true) version = VersionType.v_20_4;
		else if(server.contains("v1_20_R3") == true) version = VersionType.v_20_3;
		else if(server.contains("v1_20_R2") == true) version = VersionType.v_20_2;
		else if(server.contains("v1_20_R1") == true) version = VersionType.v_20_1;
		else if(server.contains("v1_19_R3") == true) version = VersionType.v_19_3;
		else if(server.contains("v1_19_R2") == true) version = VersionType.v_19_2;
		else if(server.contains("v1_19_R1") == true) version = VersionType.v_19_1;
		else if(server.contains("v1_18_R2") == true) version = VersionType.v_18_1;
		else if(server.contains("v1_18_R1") == true) version = VersionType.v_18_1;
		else if(server.contains("v1_17_R1") == true) version = VersionType.v_17_1;
		else if(server.contains("v1_16_R3") == true) version = VersionType.v_16_3;
		else if(server.contains("v1_16_R2") == true) version = VersionType.v_16_2;
		else if(server.contains("v1_16_R1") == true) version = VersionType.v_16_1;
		else if(server.contains("v1_15_R1") == true) version = VersionType.v_15_1;
		else if(server.contains("v1_14_R1") == true) version = VersionType.v_14_1;
		else version = null;
	}
	
	public static enum VersionType {
		
		v_14_1,
		v_15_1,
		v_16_1, v_16_2, v_16_3,
		v_17_1,
		v_18_1, v_18_2,
		v_19_1, v_19_2, v_19_3,
		v_20_1, v_20_2, v_20_3, v_20_4,
		v_21_1, v_21_2;
		
		public boolean high(VersionType type) {
			return ordinal() >= type.ordinal();
		}
		
	}

}
