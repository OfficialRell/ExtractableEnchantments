package mc.balzarian.ee;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands {

	public static Command command;

	public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Commands.command == null) return false;
		if(sender instanceof Player	== false) return false;
		Player player = (Player) sender;
		if(Commands.command.equals(command) == false) return false;
		String help = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " "
				+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "update" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "get" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "recipe" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "material" + ChatColor.AQUA + "/" 
				+ ChatColor.GOLD + "name" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "lore" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "glint" + ChatColor.AQUA + "/"
				+ ChatColor.GOLD + "message" + ChatColor.DARK_AQUA + "]";
		if(args.length < 1) player.sendMessage(help);
		else if(args[0].equalsIgnoreCase("update") == true) {
			Main.updateRecipe();
			player.sendMessage(ChatColor.AQUA + "Updating recipe...");
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
		} else if(args[0].equalsIgnoreCase("get") == true) {
			String help_get = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " get "
					+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "amount" + ChatColor.DARK_AQUA + "]";
			if(args.length < 2) player.sendMessage(help_get);
			else if(Main.isInteger(args[1]) == true) {
				int a = Integer.parseInt(args[1]);
				ItemStack item = Main.scroll(a);
				player.getInventory().addItem(item);
			} else player.sendMessage(help_get);
		} else if(args[0].equalsIgnoreCase("recipe") == true) {
			if(Main.re != null) {
				Player editor = Main.re.getPlayer();
				if(editor.isOnline() == false) {
					Main.re.close();
					Main.re = new RecipeEditor(player);
				} else {
					player.sendMessage(ChatColor.RED + "(!) " + ChatColor.YELLOW + editor.getName() + ChatColor.GOLD + " is already editing the recipe");
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 2f, 0f);
				}
			} else Main.re = new RecipeEditor(player);
		} else if(args[0].equalsIgnoreCase("material") == true) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if(Main.isNull(item) == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Material cannot be empty!");
			else if(Main.material == item.getType()) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Material was not changed");
			else {
				Material m = item.getType();
				Main.config.set("Material", m.name());
				Main.saveC();
				Main.material = m;
				player.sendMessage(ChatColor.GREEN + "Material: " + ChatColor.AQUA + m.name());
			}
		} else if(args[0].equalsIgnoreCase("name") == true) {
			String help_name = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " name "
					+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "name" + ChatColor.DARK_AQUA + "]";
			if(args.length < 2) player.sendMessage(help_name);
			else {
				String n = "";
				for(int i = 1; i < args.length; i++) {
					if(i > 1) n += " ";
					n += args[i];
				}
				String name = ChatColor.translateAlternateColorCodes('&', n);
				if(Main.name.equals(n) == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Name was not changed");
				else if(ChatColor.stripColor(name).isEmpty() == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Name cannot be empty");
				else {
					Main.config.set("Name", n);
					Main.saveC();
					Main.name = name;
					player.sendMessage(ChatColor.GREEN + "New name: " + ChatColor.RESET + name);
				}
			}
		} else if(args[0].equalsIgnoreCase("lore") == true) {
			String help_lore = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore "
					+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "toggle" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "view" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "add" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "replace" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "insert" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "remove" + ChatColor.AQUA + "/"
					+ ChatColor.GOLD + "clear" + ChatColor.DARK_AQUA + "]";
			if(args.length < 2) player.sendMessage(help_lore);
			else if(args[1].equalsIgnoreCase("toggle") == true) {
				String help_lore_toggle = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore toggle "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "true" + ChatColor.AQUA + "/" 
						+ ChatColor.GOLD + "false" + ChatColor.DARK_AQUA + "]";
				if(args.length < 3) player.sendMessage(help_lore_toggle);
				else if(args[2].equalsIgnoreCase("true") == true || args[2].equalsIgnoreCase("false") == true) {
					boolean b = Boolean.valueOf(args[2].toLowerCase());
					if(Main.toggle == b) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Value was not changed");
					else {
						Main.config.set("Toggle", b);
						Main.saveC();
						Main.toggle = b;
						player.sendMessage(ChatColor.GREEN + "Lore Toggled: " + ChatColor.AQUA + b);
					}
				} else player.sendMessage(help_lore_toggle);
			} else if(args[1].equalsIgnoreCase("view") == true) {
				if(Main.lore.isEmpty() == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Lore is empty");
				else sendLore(player);
			} else if(args[1].equalsIgnoreCase("add") == true) {
				String help_lore_add = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore add "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "line" + ChatColor.DARK_AQUA + "]";
				if(args.length < 3) player.sendMessage(help_lore_add);
				else {
					String l = "";
					for(int i = 2; i < args.length; i++) {
						if(i > 2) l += " ";
						l += args[i];
					}
					Main.lore.add(l);
					Main.config.set("Lore", Main.lore);
					Main.saveC();
					String line = ChatColor.translateAlternateColorCodes('&', l);
					player.sendMessage(ChatColor.GREEN + "Added line: " + ChatColor.RESET + line);
					sendLore(player);
				}
			} else if(args[1].equalsIgnoreCase("replace") == true) {
				String help_lore_edit = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore edit "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "index" + ChatColor.DARK_AQUA + "] "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "line" + ChatColor.DARK_AQUA + "]";
				if(args.length < 3) player.sendMessage(help_lore_edit);
				else if(Main.isInteger(args[2]) == true) {
					if(args.length < 4) player.sendMessage(help_lore_edit);
					else {
						int k = Integer.parseInt(args[2]);
						if(k < 0 || k >= Main.lore.size()) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Index out of bounds");
						else {
							String l = "";
							for(int i = 3; i < args.length; i++) {
								if(i > 3) l += " ";
								l += args[i];
							}
							Main.lore.set(k, l);
							Main.config.set("Lore", Main.lore);
							Main.saveC();
							String line = ChatColor.translateAlternateColorCodes('&', l);
							player.sendMessage(ChatColor.GREEN + "Replaced line: " + ChatColor.RESET + line);
							sendLore(player);
						}
					}
				} else player.sendMessage(help_lore_edit);
			} else if(args[1].equalsIgnoreCase("insert") == true) {
				String help_lore_insert = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore insert "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "index" + ChatColor.DARK_AQUA + "] "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "line" + ChatColor.DARK_AQUA + "]";
				if(args.length < 3) player.sendMessage(help_lore_insert);
				else if(Main.isInteger(args[2]) == true) {
					if(args.length < 4) player.sendMessage(help_lore_insert);
					else {
						int k = Integer.parseInt(args[2]);
						if(k < 0 || k >= Main.lore.size()) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Index out of bounds");
						else {
							String l = "";
							for(int i = 3; i < args.length; i++) {
								if(i > 3) l += " ";
								l += args[i];
							}
							Main.lore.add(k, l);
							Main.config.set("Lore", Main.lore);
							Main.saveC();
							String line = ChatColor.translateAlternateColorCodes('&', l);
							player.sendMessage(ChatColor.GREEN + "Inserted line: " + ChatColor.RESET + line);
							sendLore(player);
						}
					}
				} else player.sendMessage(help_lore_insert);
			} else if(args[1].equalsIgnoreCase("remove") == true) {
				String help_lore_remove = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " lore remove "
						+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "index" + ChatColor.DARK_AQUA + "]";
				if(args.length < 3) player.sendMessage(help_lore_remove);
				else if(Main.isInteger(args[2]) == true) {
					int k = Integer.parseInt(args[2]);
					if(k < 0 || k >= Main.lore.size()) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Index out of bounds");
					else {
						String line = ChatColor.translateAlternateColorCodes('&', Main.lore.get(k));
						Main.lore.remove(k);
						Main.config.set("Lore", Main.lore);
						Main.saveC();
						player.sendMessage(ChatColor.GREEN + "Removed line: " + ChatColor.RESET + line);
						sendLore(player);
					}
				} else player.sendMessage(help_lore_remove);
			} else if(args[1].equalsIgnoreCase("clear") == true) {
				Main.lore.clear();
				Main.config.set("Lore", Main.lore);
				Main.saveC();
				player.sendMessage(ChatColor.GREEN + "Lore has been cleared");
			} else player.sendMessage(help_lore);
		} else if(args[0].equalsIgnoreCase("glint") == true) {
			String help_glint = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " glint "
					+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "true" + ChatColor.AQUA + "/" 
					+ ChatColor.GOLD + "false" + ChatColor.DARK_AQUA + "]";
			if(args.length < 2) player.sendMessage(help_glint);
			else if(args[1].equalsIgnoreCase("true") == true || args[1].equalsIgnoreCase("false") == true) {
				boolean b = Boolean.valueOf(args[1].toLowerCase());
				if(Main.glint == b) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Value was not changed");
				else {
					Main.config.set("Glint", b);
					Main.saveC();
					Main.glint = b;
					player.sendMessage(ChatColor.GREEN + "Glint: " + ChatColor.AQUA + b);
				}
			} else player.sendMessage(help_glint);
		} else if(args[0].equalsIgnoreCase("message") == true) {
			String help_message = ChatColor.YELLOW + "Usage: " + ChatColor.AQUA + "/" + command.getLabel() + " messsage "
					+ ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "message" + ChatColor.DARK_AQUA + "]";
			if(args.length < 2) player.sendMessage(help_message);
			else {
				String m = "";
				for(int i = 1; i < args.length; i++) {
					if(i > 1) m += " ";
					m += args[i];
				}
				String message = ChatColor.translateAlternateColorCodes('&', m);
				if(Main.message.equals(m) == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Message was not changed");
				else if(ChatColor.stripColor(message).isEmpty() == true) player.sendMessage(ChatColor.RED + "(!) " + ChatColor.GOLD + "Message cannot be empty");
				else {
					Main.config.set("Message", m);
					Main.saveC();
					Main.message = message;
					player.sendMessage(ChatColor.GREEN + "New message: " + ChatColor.RESET + message);
				}
			}
		} else player.sendMessage(help);
		return false;
	}

	public static List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(sender instanceof Player	== false) return null;
		List<String> l = new ArrayList<>();
		if(Commands.command.equals(command) == true) {
			if(args.length < 1) return null;
			else if(args.length < 2) return ee(args[0]);
			else if(args[0].equalsIgnoreCase("lore") == true) {
				if(args.length < 3) return lore(args[1]);
				else if(args[1].equalsIgnoreCase("toggle") == true) return bool(args[2]);
				else return l;
			} else if(args[0].equalsIgnoreCase("glint") == true) return bool(args[1]);
			else return l;
		}
		return null;
	}

	public static List<String> ee(String s) {
		List<String> l = new ArrayList<>();
		l.add("update");
		l.add("get");
		l.add("recipe");
		l.add("material");
		l.add("name");
		l.add("lore");
		l.add("glint");
		l.add("message");
		return reduce(l, s);
	}

	public static List<String> lore(String s) {
		List<String> l = new ArrayList<>();
		l.add("toggle");
		l.add("view");
		l.add("add");
		l.add("replace");
		l.add("insert");
		l.add("remove");
		l.add("clear");
		return reduce(l, s);
	}

	public static List<String> bool(String s) {
		List<String> l = new ArrayList<>();
		l.add("true");
		l.add("false");
		return reduce(l, s);
	}

	public static List<String> reduce(List<String> l, String s) {
		List<String> r = new ArrayList<>();
		for(String a : l) if(a.toLowerCase().startsWith(s.toLowerCase()) == true) r.add(a);
		return r;
	}

	public static void sendLore(Player player) {
		player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Current lore:");
		for(String line : Main.lore) player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
	}
}
