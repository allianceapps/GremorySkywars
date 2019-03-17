package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaVote.SubVoteType;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class ArenaVotesMenu extends Menu {
	
	private static MenuItem back, doubleh;

	public ArenaVotesMenu(Player p) {
		super(p, "Insane Votes", 4);
		if (doubleh.getSlot() > -1) {
			getInventory().setItem(doubleh.getSlot(), doubleh.getItem());
		}
		getInventory().setItem(31, back.getItem());
		p.openInventory(getInventory());
	}
	
	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		doubleh = items.get("v_double");
	}

	@Override
	public void onClick(Player p, ItemStack item) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null || pd.getArena() == null) {
			p.closeInventory();
			return;
		}

		if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null) {
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			}
			
			p.closeInventory();
			new InsaneMenu(pd);
		} else if (item.equals(doubleh.getItem())) {
			Arena arena = pd.getArena();
			p.closeInventory();
			if (Main.getSound_orb() != null) {
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			}
			
			if (arena.getHealth().has(p, SubVoteType.DOUBLE_HEALTH)) {
				arena.getHealth().clear(p);
				p.sendMessage(Language.messages$votes$removed);
				return;
			}
			
			arena.getHealth().put(p, SubVoteType.DOUBLE_HEALTH);
			p.sendMessage(Language.messages$votes$added);
			arena.broadcastMessage(Language.messages$votes$vote_double.replace("%player%", p.getDisplayName()).replace("%size%", arena.getHealth().getVotesMap().get(SubVoteType.DOUBLE_HEALTH).size() + "").replace("%max%", "" + (arena.getMode() == Mode.SOLO ? Options.getVote_dh_solo() : arena.getMode() == Mode.TEAM ? Options.getVote_dh_team() : Options.getVote_dh_mega())));
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getInventory().equals(getInventory())) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item != null && item.hasItemMeta() && p == getPlayer())
				onClick(p, item);
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
