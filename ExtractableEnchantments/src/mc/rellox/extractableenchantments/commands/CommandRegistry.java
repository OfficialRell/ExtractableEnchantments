package mc.rellox.extractableenchantments.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.ExtractorEdit;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.Dust;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.utils.Utils;

public final class CommandRegistry {

	private static final Command EE = Bukkit.getPluginCommand("extractableenchantments");
	private static final Command SPLIT = Bukkit.getPluginCommand("splitdust");
	
	public static void onCommand(CommandSender sender, Command command, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(EE != null && EE.equals(command) == true) {
			String help0 = help(command, null, "reload", "extractor", "dust", "edit");
			if(args.length < 1) sender.sendMessage(help0);
			else if(args[0].equalsIgnoreCase("debug") == true) {
				Object o = ExtractableEnchantments.ECO_ENCHANTS.get();
				if(o != null) {
					try {
						System.out.println("Fields:");
						Field[] fields = o.getClass().getFields();
						for(Field field : fields) {
							System.out.println("- " + field.getName() + " -> " + field.getType().getName());
						}
						System.out.println("Methods:");
						Method[] methods = o.getClass().getMethods();
						for(Method method : methods) {
							System.out.println("- " + method.getName());
							Class<?>[] ps = method.getParameterTypes();
							if(ps != null && ps.length > 0) {
								for(Class<?> p : ps) System.out.println("  > " + p.getName());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else System.out.println("Unable to find EcoEnchants plugin!");
			}
			else if(args[0].equalsIgnoreCase("reload") == true) {
				ExtractableEnchantments.update(sender);
				if(player != null) player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
			} else if(args[0].equalsIgnoreCase("extractor") == true) {
				String help1 = help(command, "extractor", "key^") + extra("amount*") + extra("player?");
				if(args.length < 2) sender.sendMessage(help1);
				else {
					Extractor ex = ExtractorRegistry.extractor(args[1]);
					if(ex == null) warn(sender, "Unable to find this extractor (#0)!", args[1]);
					else {
						int a;
						if(args.length < 3) a = 1;
						else {
							if(Utils.isInteger(args[2]) == true) {
								a = Integer.parseInt(args[2]);
								if(a <= 0 || a > 128) {
									warn(sender, "Invalid amount (#0)!", a);
									return;
								}
							} else {
								warn(sender, "Invalid amount (#0)!", args[2]);
								return;
							}
						}
						if(args.length < 4) {
							if(player == null) return;
							ItemStack[] items = ex.items(a);
							player.getInventory().addItem(items);
							success(sender, "Got #0 × #1", Utils.displayName(items[0]), a);
							player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1.5f);
						} else {
							Player getter = Bukkit.getPlayer(args[3]);
							if(getter == null) warn(sender, "Unable to fine a player with name #0!", args[3]);
							else {
								int f = Utils.slots(getter);
								if(a <= f) getter.getInventory().addItem(ex.items(a));
								else {
									int l = a - f;
									getter.getInventory().addItem(ex.items(f));
									ItemStack[] items = ex.items(l);
									for(ItemStack item : items) getter.getWorld().dropItem(getter.getLocation(), item);
								}
							}
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("dust") == true) {
				String help1 = help(command, "dust", "dust^") + extra("percent*") + extra("amount?*") + extra("player?");
				if(args.length < 2) sender.sendMessage(help1);
				else {
					Dust dust = DustRegistry.dust(args[1]);
					if(dust == null) warn(sender, "Unable to find this dust (#0)!", args[1]);
					else {
						String help2 = help(command, "dust " + args[1], "percent*") + extra("amount?*") + extra("player?");
						if(args.length < 3) sender.sendMessage(help2);
						else {
							if(Utils.isInteger(args[2]) == true) {
								int p = Integer.parseInt(args[2]);
								if(p <= 0) warn(sender, "Invalid percentage (#0)!", p);
								else if(p > dust.limit) warn(sender, "Dust percentage limit reached (#0 > #1)!", p, dust.limit);
								else {
									int a;
									if(args.length < 4) a = 1;
									else {
										if(Utils.isInteger(args[3]) == true) {
											a = Integer.parseInt(args[3]);
											if(a <= 0) {
												warn(sender, "Invalid amount (#0)!", a);
												return;
											}
										} else return;
									}
									if(args.length < 5) {
										if(player == null) return;
										ItemStack[] items = dust.items(p, a);
										player.getInventory().addItem(items);
										success(sender, "Got #0 × #1", Utils.displayName(items[0]), a);
										player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1.5f);
									} else {
										Player getter = Bukkit.getPlayer(args[4]);
										if(getter == null) warn(sender, "Unable to fine a player with name #0!", args[4]);
										else {
											int f = Utils.slots(getter);
											if(a <= f) getter.getInventory().addItem(dust.items(p, a));
											else {
												int l = a - f;
												getter.getInventory().addItem(dust.items(p, f));
												ItemStack[] items = dust.items(p, l);
												for(ItemStack item : items) getter.getWorld().dropItem(getter.getLocation(), item);
											}
										}
									}
								}
							} else warn(sender, "Invalid percentage (#0)!", args[2]);
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("edit") == true) {
				String help1 = help(command, "edit", "extractor^");
				if(args.length < 2) sender.sendMessage(help1);
				else {
					Extractor ex = ExtractorRegistry.extractor(args[1]);
					if(ex == null) warn(sender, "Unable to find this extractor (#0)!", args[1]);
					else {
						if(player == null) warn(sender, "This command can only be used in-game!");
						else new ExtractorEdit(player, ex).open();
					}
				}
			}
		} else if(SPLIT != null && SPLIT.equals(command) == true) {
			if(player == null) {
				warn(sender, "Cannot do this in console!");
				return;
			}
			String help0 = help(command, null, "amount*");
			if(args.length < 1) sender.sendMessage(help0);
			else {
				ItemStack hand = player.getInventory().getItemInMainHand();
				Dust dust;
				if(Utils.isNull(hand) == true || (dust = DustRegistry.dust(hand)) == null) {
					sender.sendMessage(Language.dust_split_held());
					return;
				}
				if(player.hasPermission("ee.dust.split." + dust.key) == false) {
					player.sendMessage(Language.permission_warn_split());
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				int perc = DustRegistry.readPercent(hand);
				if(perc <= 1) sender.sendMessage(Language.dust_split_small());
				else if(Utils.isInteger(args[0]) == true) {
					int a = Integer.parseInt(args[0]);
					if(a < 1) sender.sendMessage(Language.dust_split_amount(a));
					else if(a >= perc) sender.sendMessage(Language.dust_split_less(perc));
					else {
						perc -= a;
						DustRegistry.writePercent(hand, perc);
						dust.update(hand);
						ItemStack split = dust.item(a);
						if(Utils.slots(player) <= 0) player.getWorld().dropItem(player.getLocation(), split);
						else player.getInventory().addItem(split);
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
						player.playSound(player.getEyeLocation(), Sound.ITEM_BOTTLE_EMPTY, 2f, 1.5f);
					}
				} else sender.sendMessage(Language.dust_split_amount(args[0]));
			}
		}
	}
	
	public static List<String> onTabComplete(CommandSender sender, Command command, String[] args) {
		List<String> l = new ArrayList<>();
		if(EE.equals(command) == true) {
			if(args.length < 1) return l;
			else if(args.length < 2) return ee(args[0]);
			else if(args[0].equalsIgnoreCase("extractor") == true) {
				if(args.length < 3) return extractors(args[1]);
				else if(args.length < 4) return l;
				else if(args.length < 5) return players(args[3]);
				else return l;
			} else if(args[0].equalsIgnoreCase("dust") == true) {
				if(args.length < 3) return dusts(args[1]);
				else if(args.length < 5) return l;
				else if(args.length < 6) return players(args[4]);
				else return l;
			} else if(args[0].equalsIgnoreCase("edit") == true) {
				if(args.length < 3) return extractors(args[1]);
				else return l;
			} else return l;
		} else if(SPLIT.equals(command) == true) return l;
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

	private static List<String> ee(String s) {
		List<String> l = new ArrayList<>();
		l.add("reload");
		l.add("extractor");
		l.add("dust");
		l.add("edit");
		return reduce(l, s);
	}

	private static List<String> extractors(String s) {
		List<String> l = ExtractorRegistry.EXTACTORS.stream().map(Extractor::key).collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> dusts(String s) {
		List<String> l = DustRegistry.DUSTS.stream().map(Dust::key).collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> players(String s) {
		List<String> l = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
		return reduce(l, s);
	}

	private static List<String> reduce(List<String> l, String s) {
		return s.isEmpty() == true ? l : l.stream().filter(a -> a.toLowerCase().contains(s)).collect(Collectors.toList());
	}

}
