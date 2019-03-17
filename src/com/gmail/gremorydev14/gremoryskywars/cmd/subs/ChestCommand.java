package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class ChestCommand extends SubCommand {

	public ChestCommand() {
		super("chest");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cCorrect usage: /sw chest <name>");
		} else {
			Chest chest = Chest.getChest(args[0]);
			if (chest != null) {
				chest.getEditor().open(p);
			} else {
				p.sendMessage("§cChest type invalid!");
			}
		}
	}
}
