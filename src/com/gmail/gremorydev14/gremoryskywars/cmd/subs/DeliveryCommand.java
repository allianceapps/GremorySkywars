package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.delivery.DeliveryMan;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class DeliveryCommand extends SubCommand {

	public DeliveryCommand() {
		super("dman");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw dman add <id> §7- Add a deliveryman in your location");
			p.sendMessage("§a/sw dman remove <id> §7- Remove an deliveryman");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
			return;
		} else {
			String id = args[1];
			String action = args[0];

			if (action.equalsIgnoreCase("add")) {
				Location loc = p.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
				loc.setYaw(p.getLocation().getYaw());
				loc.setPitch(p.getLocation().getPitch());
				DeliveryMan.add(id, loc);
				p.sendMessage("§aYou added an DeliveryMan in your location");
				return;
			} else if (action.equalsIgnoreCase("remove")) {
				DeliveryMan npc = DeliveryMan.get(id);
				if (npc == null) {
					p.sendMessage("§cDeliveryMan \"" + id + "\" was not found");
					return;
				}

				DeliveryMan.remove(npc);
				p.sendMessage("§aYou removed the \"" + id + "\" DeliveryMan");
				return;
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw dman add <id> §7- Add a deliveryman in your location");
				p.sendMessage("§a/sw dman remove <id> §7- Remove an deliveryman");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
				return;
			}
		}
	}
}
