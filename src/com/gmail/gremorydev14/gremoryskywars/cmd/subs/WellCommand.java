package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.mystery.SoulWell;

public class WellCommand extends SubCommand {

	public WellCommand() {
		super("well");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw well add <id> §7- Add a soulwell in you location");
			p.sendMessage("§a/sw well remove <id> §7- Remove an soulwell");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
		} else {
			String id = args[1];
			String action = args[0];

			if (action.equalsIgnoreCase("add")) {
				SoulWell sw = SoulWell.get(id);
				if (sw != null) {
					p.sendMessage("§cThere is already a soulwell with this id");
					return;
				}

				SoulWell.add(id, p.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5));
				p.sendMessage("§aYou added soul well in your location");
			} else if (action.equalsIgnoreCase("remove")) {
				SoulWell sw = SoulWell.get(id);
				if (sw == null) {
					p.sendMessage("§cSoulwell \"" + id + "\" was not found");
					return;
				}

				SoulWell.remove(sw);
				p.sendMessage("§aYou removed the \"" + id + "\" soulwell");
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw well add <id> §7- Add a soulwell in you location");
				p.sendMessage("§a/sw well remove <id> §7- Remove an soulwell");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
			}
		}
	}
}
