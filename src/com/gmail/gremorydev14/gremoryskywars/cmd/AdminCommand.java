package com.gmail.gremorydev14.gremoryskywars.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;

public class AdminCommand implements CommandExecutor {

	public AdminCommand() {
		Main.getPlugin().getCommand("admin").setExecutor(this);
	}
	
	private Map<Player, ItemStack[]> inv = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player) sender;
		if (!p.hasPermission("gremoryskywars.admin")) {
			p.sendMessage(Language.messages$player$no_permission);
			return true;
		}
		
		if (!p.hasMetadata("ADMIN_MODE")) {
			p.setMetadata("ADMIN_MODE", new FixedMetadataValue(Main.getPlugin(), true));
			p.setAllowFlight(true);
			inv.put(p, p.getInventory().getContents());
			p.getInventory().clear();
			p.updateInventory();
			for (Player ps : p.getWorld().getPlayers()) {
				ps.hidePlayer(p);
				p.showPlayer(ps);
			}
		} else {
			p.setAllowFlight(false);
			p.removeMetadata("ADMIN_MODE", Main.getPlugin());
			p.getInventory().clear();
			if (inv.get(p) != null) {
				p.getInventory().setContents(inv.get(p));
			}
			inv.remove(p);
			p.updateInventory();
			for (Player ps : p.getWorld().getPlayers()) {
				ps.showPlayer(p);
				p.showPlayer(ps);
			}
		}
		return true;
	}
}
