package com.gmail.gremorydev14.profile.menus;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class BoostersMenu extends Menu {

	private static MenuItem pbooster, nbooster, back;

	public BoostersMenu(Player p) {
		super(p, "Boosters", 4);
		if (pbooster.getSlot() > -1)
			getInventory().setItem(pbooster.getSlot(), pbooster.getItem());
		if (nbooster.getSlot() > -1)
			getInventory().setItem(nbooster.getSlot(), nbooster.getItem());
		getInventory().setItem(31, back.getItem());
		p.openInventory(getInventory());
	}

	public void onClick(Player p, ItemStack item, int slot) {
		if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ProfileMenu(p);
		} else if (item.equals(pbooster.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new PrivateBoosters(p);
		} else if (item.equals(nbooster.getItem())) {
			if (Main.getEnderman() != null)
				p.playSound(p.getLocation(), Main.getEnderman(), 1.0f, 1.0f);
			p.sendMessage(Language.messages$player$in_development);
		}
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		pbooster = items.get("b_pbooster");
		nbooster = items.get("b_nbooster");
		back = items.get("back");
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getInventory().equals(getInventory())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked() instanceof Player && getPlayer() == (Player) e.getWhoClicked()) {
				onClick((Player) e.getWhoClicked(), e.getCurrentItem(), e.getSlot());
			}
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (getInventory().equals(e.getInventory()) && e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@Override
	public void onClick(Player p, ItemStack item) {
	}

}
