package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class LoadCommand extends SubCommand {

	public LoadCommand() {
		super("load");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cCorrect usage: /sw load <world>");
		} else {
			File world = new File(args[0]);
			if (world.exists()) {
				if (Bukkit.getWorld(args[0]) == null) {
					try {
						WorldCreator wc = new WorldCreator(args[0]);
						wc.generateStructures(false);
						World map = wc.createWorld();
						map.setAutoSave(false);
						map.setKeepSpawnInMemory(false);
						map.setAnimalSpawnLimit(1);
						map.setGameRuleValue("doMobSpawning", "false");
						map.setGameRuleValue("doDaylightCycle", "false");
						map.setGameRuleValue("mobGriefing", "false");
						map.setTime(0);
						p.sendMessage("§aWorld " + args[0] + " loaded sucessfully!");
					} catch (Exception e) {
						p.sendMessage("§cError, check the log!");
						e.printStackTrace();
					}
				} else {
					p.sendMessage("§cWorld " + args[0] + " already exists!");
				}
			} else {
				p.sendMessage("§cWorld " + args[0] + " is invalid!");
			}
		}
	}
}
