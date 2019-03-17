package com.gmail.gremorydev14.profile.menus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
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
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class PreferencesMenu extends Menu {

	private static MenuItem on, off, players, tell, back;

	public PreferencesMenu(Player p) {
		super(p, "Settings", 6);
		PlayerData pd = PlayerData.get(p);

		getInventory().setItem(0, players.getItem());
		getInventory().setItem(9, pd.isView() ? on.getItem() : off.getItem());

		getInventory().setItem(1, tell.getItem());
		getInventory().setItem(10, pd.isTell() ? on.getItem() : off.getItem());

		getInventory().setItem(49, back.getItem());
		p.openInventory(getInventory());
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		on = items.get("pr_on");
		off = items.get("pr_off");
		players = items.get("pr_players");
		tell = items.get("pr_tell");
		back = items.get("back");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		PlayerData pd = PlayerData.get(p);
		if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ProfileMenu(p);
		} else if (item.equals(on.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			ItemStack previous = getInventory().getItem(slot - 9);
			if (previous.equals(players.getItem())) {
				if (MultiArenaListeners.delay_players.containsKey(pd.getPlayer()) && MultiArenaListeners.delay_players.get(pd.getPlayer()) > System.currentTimeMillis()) {
					p.sendMessage(Language.messages$player$players_toggle_delay.replace("%time%", MultiArenaListeners.until(p)));
					return;
				}
				MultiArenaListeners.delay_players.put(pd.getPlayer(), TimeUnit.SECONDS.toMillis(15) + System.currentTimeMillis());
				pd.setView(false);
				getInventory().setItem(slot, off.getItem());
				if (Options.isReceive_players())
					p.getInventory().setItem(7, ItemUtils.createItem("INK_SACK:" + (PlayerData.get(p).isView() ? 10 : 8) + " : 1 : name=" + (PlayerData.get(p).isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
				p.updateInventory();
				for (Player ps : Bukkit.getOnlinePlayers()) {
					if (!ps.getWorld().getName().equals(p.getWorld().getName())) {
						p.hidePlayer(ps);
						ps.hidePlayer(p);
					} else {
						if (pd.isView())
							p.showPlayer(ps);
						else
							p.hidePlayer(ps);
						if (PlayerData.get(ps) == null || PlayerData.get(ps).isView())
							ps.showPlayer(p);
						else
							ps.hidePlayer(p);
					}
				}
			} else {
				pd.setTell(false);
				getInventory().setItem(slot, off.getItem());
			}
		} else if (item.equals(off.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			ItemStack previous = getInventory().getItem(slot - 9);
			if (previous.equals(players.getItem())) {
				if (MultiArenaListeners.delay_players.containsKey(pd.getPlayer()) && MultiArenaListeners.delay_players.get(pd.getPlayer()) > System.currentTimeMillis()) {
					p.sendMessage(Language.messages$player$players_toggle_delay.replace("%time%", MultiArenaListeners.until(p)));
					return;
				}
				MultiArenaListeners.delay_players.put(pd.getPlayer(), TimeUnit.SECONDS.toMillis(15) + System.currentTimeMillis());
				pd.setView(true);
				getInventory().setItem(slot, on.getItem());
				if (Options.isReceive_players())
					p.getInventory().setItem(7, ItemUtils.createItem("INK_SACK:" + (PlayerData.get(p).isView() ? 10 : 8) + " : 1 : name=" + (PlayerData.get(p).isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
				p.updateInventory();
				for (Player ps : Bukkit.getOnlinePlayers()) {
					if (!ps.getWorld().getName().equals(p.getWorld().getName())) {
						p.hidePlayer(ps);
						ps.hidePlayer(p);
					} else {
						if (pd.isView())
							p.showPlayer(ps);
						else
							p.hidePlayer(ps);
						if (PlayerData.get(ps) == null || PlayerData.get(ps).isView())
							ps.showPlayer(p);
						else
							ps.hidePlayer(p);
					}
				}
			} else {
				pd.setTell(true);
				getInventory().setItem(slot, on.getItem());
			}
		}
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
