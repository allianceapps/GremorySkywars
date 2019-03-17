package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class TeleportCommand extends SubCommand {

	public TeleportCommand() {
		super("tp");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cCorrect usage: /sw tp <world>");
		} else {
			World world = Bukkit.getWorld(args[0]);
			if (world != null) {
				p.teleport(world.getSpawnLocation());
				p.sendMessage("§cTeleported sucessfully!");
			} else {
				p.sendMessage("§cWorld " + args[0] + " is invalid!");
			}
		}
	}
}
