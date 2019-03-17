package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class UnloadCommand extends SubCommand {

	public UnloadCommand() {
		super("unload");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cCorrect usage: /sw unload <world>");
		} else {
			World world = Bukkit.getWorld(args[0]);
			if (world != null) {
				try {
					Bukkit.unloadWorld(world, true);
					p.sendMessage("§cWorld " + args[0] + " unload sucessfully!");
				} catch (Exception e) {
					p.sendMessage("§cError, check the log!");
					e.printStackTrace();
				}
			} else {
				p.sendMessage("§cThis " + args[0] + " world is invalid");
			}
		}
	}
}
