package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class SpawnCommand extends SubCommand {

	public SpawnCommand() {
		super("spawn");
	}

	@Override
	public void onSend(Player p, String[] args) {
		Arena arena = Arena.getArena(p.getWorld());
		if (arena != null) {
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
			MultiArenaListeners.selected.put(p, arena);
			MultiArenaListeners.inv.put(p, p.getInventory().getContents());
			p.getInventory().clear();
			p.getInventory().addItem(ItemUtils.createItem("STAINED_CLAY:4 : 1 : name=&eAdd"));
			p.getInventory().addItem(ItemUtils.createItem("BEACON : 1 : name=&eScan"));
			p.getInventory().addItem(ItemUtils.createItem("STAINED_CLAY:15 : 1 : name=&cQuit"));
			p.getInventory().setItem(8, ItemUtils.createItem("STAINED_CLAY:5 : 1 : name=&aSpawns: " + arena.getSpawns().size()));
			p.updateInventory();
			p.sendMessage("§aArena selected successfully!");
		} else {
			p.sendMessage("§cThere is no arena in this world.!");
		}
	}
}
