package mc.rellox.extractableenchantments.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.dust.IDust;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.item.IDustItem;
import mc.rellox.extractableenchantments.api.item.IExtractorItem;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.selection.SelectionExtractChangeable;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.utility.Utility;

public final class CommandRegistry {

	private static final Command extactable_enchantments = Bukkit.getPluginCommand("extractableenchantments");
	private static final Command split_dust = Bukkit.getPluginCommand("splitdust");
	
	public static void onCommand(CommandSender sender, Command command, String[] args) {
		final Player player = sender instanceof Player p ? p : null;
		if(extactable_enchantments != null && extactable_enchantments.equals(command) == true) {
			String help0 = help(command, null, "reload", "extractor", "dust", "open");
			if(args.length < 1) sender.sendMessage(help0);
			else if(args[0].equalsIgnoreCase("reload") == true) {
				reload(sender, player);
			} else if(args[0].equalsIgnoreCase("extractor") == true) {
				extractor(sender, command, args, player);
			} else if(args[0].equalsIgnoreCase("dust") == true) {
				dust(sender, command, args, player);
			} else if(args[0].equalsIgnoreCase("open") == true) {
				open(sender, command, args, player);
			} else sender.sendMessage(help0);
		} else if(split_dust != null && split_dust.equals(command) == true) {
			split(sender, command, args, player);
		}
	}

	private static void split(CommandSender sender, Command command, String[] args, final Player player) {
		if(player == null) {
			warn(sender, "Cannot do this in console!");
			return;
		}

		String help0 = help(command, null, "amount*");
		if(args.length < 1) {
			sender.sendMessage(help0);
			return;
		}

		ItemStack hand = player.getInventory().getItemInMainHand();
		IDust dust;
		if(ItemRegistry.nulled(hand) == true || (dust = DustRegistry.get(hand)) == null) {
			Language.get("Dust.split.held").send(player);
			return;
		}
		if(player.hasPermission("ee.dust.split." + dust.key()) == false) {
			Language.get("Permission.warning.split-dust").send(player);
			Settings.settings.sound_warning.play(player);
			return;
		}
		int perc = DustRegistry.readPercent(hand);
		if(perc <= 1) Language.get("Dust.split.too-small").send(player);
		else if(Utility.isInteger(args[0]) == true) {
			int amount = Integer.parseInt(args[0]);
			if(amount < 1) Language.get("Dust.split.invalid", "amount", amount).send(player);
			else if(amount >= perc) Language.get("Dust.split.too-large", "value", perc).send(player);
			else {
				perc -= amount;
				DustRegistry.writePercent(hand, perc);
				dust.item().update(hand);

				ItemStack split = dust.item().item(amount);
				if(ItemRegistry.free(player) <= 0) player.getWorld().dropItem(player.getLocation(), split);
				else player.getInventory().addItem(split);

				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
				player.playSound(player.getEyeLocation(), Sound.ITEM_BOTTLE_EMPTY, 2f, 1.5f);
			}
		} else Language.get("Dust.split.invalid", "amount", args[0]).send(player);
	}

	private static void dust(CommandSender sender, Command command, String[] args, final Player player) {
		String help1 = help(command, "dust", "dust^") + extra("percent*") + extra("amount?*") + extra("player?");
		if(args.length < 2) {
			sender.sendMessage(help1);
			return;
		}

		IDust dust = DustRegistry.get(args[1]);
		if(dust == null) {
			warn(sender, "Unable to find this dust (#0)!", args[1]);
			return;
		}

		String help2 = help(command, "dust " + args[1], "percent*") + extra("amount?*") + extra("player?");
		if(args.length < 3) {
			sender.sendMessage(help2);
			return;
		}

		String pa = args[2];
		if(pa.matches("\\d+(?:-\\d+)?") == false) {
			warn(sender, "Invalid percentage (#0)!", pa);
			return;
		}
		int p;
		if(pa.indexOf('-') > 0) {
			String[] ps = pa.split("-");
			int i0 = Integer.parseInt(ps[0]);
			int i1 = Integer.parseInt(ps[1]);
			if(i0 > i1) {
				warn(sender, "Invalid percentage (#0)!", pa);
				return;
			}
			p = Utility.between(i0, i1);
		} else p = Integer.parseInt(pa);
		if(p > dust.limit()) {
			warn(sender, "Dust percentage limit reached (#0 > #1)!",
					p, dust.limit());
			return;
		}
		int amount;
		if(args.length < 4) amount = 1;
		else {
			if(Utility.isInteger(args[3]) == false) return;
			amount = Integer.parseInt(args[3]);
			if(amount <= 0) {
				warn(sender, "Invalid amount (#0)!", amount);
				return;
			}
		}

		IDustItem di = dust.item();

		if(args.length < 5) {
			if(player == null) return;
			ItemStack[] items = di.items(p, amount);
			player.getInventory().addItem(items);
			success(sender, "Got #0 " + Text.symbol_multiplier + " #1", Text.display(items[0]), amount);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1.5f);
		} else {
			Player getter = Bukkit.getPlayer(args[4]);
			if(getter == null) {
				warn(sender, "Unable to fine a player with name #0!", args[4]);
				return;
			}
			int free = ItemRegistry.free(getter);
			if(amount <= free) {
				getter.getInventory().addItem(di.items(p, amount));
				return;
			}
			int left = amount - free;
			getter.getInventory().addItem(di.items(p, free));
			ItemStack[] items = di.items(p, left);
			for(ItemStack item : items)
				getter.getWorld().dropItem(getter.getLocation(), item);
		}
	}

	private static void open(CommandSender sender, Command command, String[] args, final Player player) {
		String help1 = help(command, "open", "extractor^") + extra("player?");
		if(args.length < 2) {
			sender.sendMessage(help1);
			return;
		}
		
		IExtractor extractor = ExtractorRegistry.get(args[1]);
		if(extractor == null) {
			warn(sender, "Unable to find this extractor (#0)!", args[1]);
			return;
		}

		Player getter;
		if(args.length < 3) getter = player;
		else if((getter = Bukkit.getPlayer(args[2])) == null) {
			warn(sender, "Unable to fine a player with name #0!", args[3]);
			return;
		}

		if(getter == null) {
			warn(sender, "Cannot open for console!");
			return;
		}
		
		new SelectionExtractChangeable(extractor, player);
	}

	private static void reload(CommandSender sender, final Player player) {
		ExtractorRegistry.clear();
		DustRegistry.clear();
		Configuration.initialize();
		if(sender == null) return;
		sender.sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.AQUA + "Reloading plugin...");
		if(player != null) player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
	}

	private static void extractor(CommandSender sender, Command command, String[] args, final Player player) {
		String help1 = help(command, "extractor", "key^") + extra("amount*") + extra("player?");
		if(args.length < 2) {
			sender.sendMessage(help1);
			return;
		}

		IExtractor extractor = ExtractorRegistry.get(args[1]);
		if(extractor == null) {
			warn(sender, "Unable to find this extractor (#0)!", args[1]);
			return;
		}
		
		int amount;
		if(args.length < 3) amount = 1;
		else {
			if(Utility.isInteger(args[2]) == false) return;
			amount = Integer.parseInt(args[2]);
			if(amount <= 0 || amount > 128) {
				warn(sender, "Invalid amount (#0)!", amount);
				return;
			}
		}
		IExtractorItem ei = extractor.item();

		if(args.length < 4) {
			if(player == null) return;
			ItemStack[] items = ei.items(amount);
			player.getInventory().addItem(items);
			success(sender, "Got #0 " + Text.symbol_multiplier + " #1", Text.display(items[0]), amount);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1.5f);
		} else {
			Player getter = Bukkit.getPlayer(args[3]);
			if(getter == null) {
				warn(sender, "Unable to fine a player with name #0!", args[3]);
				return;
			}
			int free = ItemRegistry.free(getter);
			if(amount <= free) {
				getter.getInventory().addItem(ei.items(amount));
				return;
			}
			int l = amount - free;
			getter.getInventory().addItem(ei.items(free));
			ItemStack[] items = ei.items(l);
			for(ItemStack item : items)
				getter.getWorld().dropItem(getter.getLocation(), item);
		}
	}
	
	public static List<String> onTabComplete(CommandSender sender, Command command, String[] args) {
		List<String> l = new ArrayList<>();
		if(extactable_enchantments.equals(command) == true) {
			if(args.length < 1) return l;
			else if(args.length < 2) return tab_options(args[0]);
			else if(args[0].equalsIgnoreCase("extractor") == true) {
				if(args.length < 3) return tab_extractors(args[1]);
				else if(args.length < 4) return l;
				else if(args.length < 5) return tab_players(args[3]);
				else return l;
			} else if(args[0].equalsIgnoreCase("dust") == true) {
				if(args.length < 3) return tab_dusts(args[1]);
				else if(args.length < 5) return l;
				else if(args.length < 6) return tab_players(args[4]);
				else return l;
			} else if(args[0].equalsIgnoreCase("open") == true) {
				if(args.length < 3) return tab_extractors(args[1]);
				else if(args.length < 4) return tab_players(args[2]);
				else return l;
			} else return l;
		} else if(split_dust.equals(command) == true) return l;
		return null;
	}
	
	private static String help(Command command, String arg, String... ss) {
		String help = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel();
		if(arg != null) help += " " + arg;
		String a =  ChatColor.DARK_AQUA + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += ChatColor.GOLD + ss[i];
			if(i < l) a += ChatColor.AQUA + "/";
		}
		return help + a + ChatColor.DARK_AQUA + "]";
	}
	
	private static String extra(String... ss) {
		String a =  ChatColor.DARK_AQUA + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += ChatColor.GOLD + ss[i];
			if(i < l) a += ChatColor.AQUA + "/";
		}
		return a + ChatColor.DARK_AQUA + "]";
	}
	
	private static void warn(CommandSender sender, String warning, Object... os) {
		String w = ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + warning;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.YELLOW + os[i].toString() + ChatColor.GOLD);
		sender.sendMessage(w);
	}
	
	private static void success(CommandSender sender, String success, Object... os) {
		String w = ChatColor.DARK_GREEN + "(!) " + ChatColor.DARK_AQUA + success;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.AQUA + os[i].toString() + ChatColor.DARK_AQUA);
		sender.sendMessage(w);
	}

	private static List<String> tab_options(String s) {
		List<String> l = new ArrayList<>();
		l.add("reload");
		l.add("extractor");
		l.add("dust");
		l.add("open");
		return reduce(l, s);
	}

	private static List<String> tab_extractors(String s) {
		List<String> l = ExtractorRegistry.all().stream()
				.map(IExtractor::key)
				.collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> tab_dusts(String s) {
		List<String> l = DustRegistry.all().stream()
				.map(IDust::key)
				.collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> tab_players(String s) {
		List<String> l = Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> reduce(List<String> l, String s) {
		if(s.isEmpty() == true) return l;
		String t = s.toLowerCase();
		return l.stream()
				.filter(a -> a.toLowerCase().contains(t))
				.collect(Collectors.toList());
	}

}
