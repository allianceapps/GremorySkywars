package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class SchematicCommand extends SubCommand {

	public SchematicCommand() {
		super("schematic");
	}

	@Override
	public void onSend(Player p, String[] args) {
		Arena arena = Arena.getArena(p.getWorld());
		if (arena != null) {
			arena.setSchematic(p.getLocation());
			p.sendMessage("§aSchematic location selected successfully!");
		} else {
			p.sendMessage("§cThere is no arena in this world.!");
		}
	}
}
