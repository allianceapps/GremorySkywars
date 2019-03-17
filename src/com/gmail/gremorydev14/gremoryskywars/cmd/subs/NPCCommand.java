package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.api.CitizensAPI;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaNPC;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class NPCCommand extends SubCommand {

	public NPCCommand() {
		super("npc");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw npc add <id> <mode> <type> §7- Add a npc in your location");
			p.sendMessage("§a/sw npc remove <id> §7- Remove an NPC");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
		} else {
			String id = args[1];
			String action = args[0];

			if (action.equalsIgnoreCase("add")) {
				if (args.length <= 2) {
					p.sendMessage("§7§m----------------------------------------");
					p.sendMessage(new String[] { "", "§c§lHELP:" });
					p.sendMessage("§a/sw npc add <id> <mode> <type> §7- Add a npc in your location");
					p.sendMessage("§a/sw npc remove <id> §7- Remove an NPC");
					p.sendMessage("");
					p.sendMessage("§7§m----------------------------------------");
				} else {
					ArenaNPC npc = CitizensAPI.get(id);
					if (npc != null) {
						p.sendMessage("§cThere is already a npc with this id");
						return;
					}
					
					Mode mode = Mode.get(args[2]);
					if (mode == null) {
						p.sendMessage("§cArena mode invalid!");
						return;
					}
					
					Type type = Type.get(args[3]);
					if (type == null) {
						p.sendMessage("§cArena type invalid!");
						return;
					}

					Location loc = p.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
					loc.setYaw(p.getLocation().getYaw());
					loc.setPitch(p.getLocation().getPitch());
					CitizensAPI.add(id, mode, type, loc);
					p.sendMessage("§aYou added an npc in your location");
				}
			} else if (action.equalsIgnoreCase("remove")) {
				ArenaNPC npc = CitizensAPI.get(id);
				if (npc == null) {
					p.sendMessage("§cNPC \"" + id + "\" was not found");
					return;
				}

				CitizensAPI.remove(npc);
				p.sendMessage("§aYou removed the \"" + id + "\" npc");
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw npc add <id> <mode> <type> §7- Add a npc in your location");
				p.sendMessage("§a/sw npc remove <id> §7- Remove an NPC");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
			}
		}
	}
}
