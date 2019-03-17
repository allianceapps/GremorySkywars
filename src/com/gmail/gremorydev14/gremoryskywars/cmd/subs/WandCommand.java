package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class WandCommand extends SubCommand {

	public WandCommand() {
		super("wand");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cCorrect usage: /sw wand <name>");
		} else {
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
			Chest chest = Chest.getChest(args[0]);
			if (chest != null) {
				MultiArenaListeners.chestEdit.put(p, chest);
				MultiArenaListeners.inv.put(p, p.getInventory().getContents());
				p.getInventory().clear();
				p.getInventory().addItem(ItemUtils.createItem("BLAZE_ROD : 1 : name=&eWand"));
				p.getInventory().addItem(ItemUtils.createItem("STAINED_CLAY:15 : 1 : name=&cQuit"));
				p.updateInventory();
			} else {
				p.sendMessage("§cChest type invalid!");
			}
		}
	}
}
