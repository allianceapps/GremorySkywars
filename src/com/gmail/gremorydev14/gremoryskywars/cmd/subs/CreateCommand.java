package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class CreateCommand extends SubCommand {

	public CreateCommand() {
		super("create");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage("§cCorrect usage: /sw create <mode> <type>");
		} else if (Arena.getArena(p.getWorld()) == null) {
			if (MultiArenaListeners.selected.containsKey(p)) {
				p.sendMessage("§cAlready using spawn mode");
				return;
			}
			if (MultiArenaListeners.chestEdit.containsKey(p)) {
				p.sendMessage("§cAlready editing chests");
				return;
			}
			if (MultiArenaListeners.create.containsKey(p)) {
				p.sendMessage("§cAlready creating arena");
				return;
			}
			Mode mode = Mode.get(args[0].toUpperCase());
			if (mode == null) {
				p.sendMessage("§cArena mode invalid!");
				return;
			}
			Type type = Type.get(args[1].toUpperCase());
			if (type == null && mode != Mode.MEGA) {
				p.sendMessage("§cArena type invalid!");
				return;
			}
			MultiArenaListeners.create.put(p, p.getWorld().getName());
			MultiArenaListeners.locs.put(p, new Location[2]);
			MultiArenaListeners.inv.put(p, p.getInventory().getContents());
			MultiArenaListeners.mode.put(p, mode);
			MultiArenaListeners.type.put(p, mode == Mode.MEGA ? Type.MEGA : type);
			p.getInventory().clear();
			p.getInventory().addItem(ItemUtils.createItem("BLAZE_ROD : 1 : name=&eBorder"));
			p.getInventory().addItem(ItemUtils.createItem("STAINED_CLAY:14 : 1 : name=&eComplete"));
			p.getInventory().addItem(ItemUtils.createItem("STAINED_CLAY:15 : 1 : name=&cCancel"));
			p.updateInventory();
		} else {
			p.sendMessage("§cThere is already an arena in this world!");
		}
	}
}
