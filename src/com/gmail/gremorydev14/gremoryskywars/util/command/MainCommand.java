package com.gmail.gremorydev14.gremoryskywars.util.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.AddCommand;

import lombok.Getter;

@Getter
public class MainCommand implements CommandExecutor {

	private String name;
	private String perm;
	private boolean console;
	private List<String> usages;
	private List<SubCommand> commands = new ArrayList<>();

	public MainCommand(Main plugin, String name, String perm, boolean console, String... usages) {
		this.name = name;
		this.perm = perm;
		this.console = console;
		this.usages = Arrays.asList(usages);
		plugin.getCommand(name).setExecutor(this);
	}

	public boolean addCommand(SubCommand cmd) {
		if (!commands.contains(cmd))
			return commands.add(cmd);
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {

			Player p = (Player) sender;
			if (!perm.equals("none") && !p.hasPermission(perm)) {
				p.sendMessage(Language.messages$player$no_permission);
				return true;
			}
		}

		if (args.length == 0) {
			if (!console && (!(sender instanceof Player))) {
				return true;
			}
			sender.sendMessage("§7§m----------------------------------------");
			sender.sendMessage(new String[] { "" });
			for (String string : usages) {
				sender.sendMessage(string);
			}
			sender.sendMessage("");
			sender.sendMessage("§7§m----------------------------------------");
			return true;
		} else {
			List<String> list = new ArrayList<>();
			list.addAll(Arrays.asList(args));
			list.remove(0);
			String[] newArgs = list.toArray(new String[list.size()]);
			if (sender instanceof Player) {
				for (SubCommand c : commands) {
					if (c.getAliases().contains(args[0])) {
						c.onSend((Player) sender, newArgs);
						return true;
					}
				}
			} else {
				for (SubCommand c : commands) {
					if (c.getAliases().contains(args[0]) && c instanceof AddCommand) {
						((AddCommand) c).onSend(sender, newArgs);
						return true;
					}
				}
			}
			if (!console && (!(sender instanceof Player))) {
				return true;
			}
			sender.sendMessage("§7§m----------------------------------------");
			sender.sendMessage(new String[] { "", });
			for (String string : usages) {
				sender.sendMessage(string);
			}
			sender.sendMessage("");
			sender.sendMessage("§7§m----------------------------------------");
			return true;
		}
	}

	@Getter
	public static abstract class SubCommand {

		private List<String> aliases;

		public SubCommand(String... aliases) {
			this.aliases = Arrays.asList(aliases);
		}

		public abstract void onSend(Player p, String[] args);
	}

}
