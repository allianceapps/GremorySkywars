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

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class StatisticsMenu extends Menu {

	private static MenuItem back, player, total;

	public StatisticsMenu(Player p) {
		super(p, "Stats Viewer", 6);
		PlayerData pd = PlayerData.get(p);
		getInventory().setItem(10,
				ItemUtils.createItem(player.getBuild().replace("%kAll%", Utils.decimal(pd.getKillsSolo() + pd.getKillsTeam() + pd.getKillsMega())).replace("%wAll%", Utils.decimal(pd.getWinsSolo() + pd.getWinsTeam() + pd.getWinsMega()))
						.replace("%kSolo%", Utils.decimal(pd.getKillsSolo())).replace("%wSolo%", Utils.decimal(pd.getWinsSolo())).replace("%kTeam%", Utils.decimal(pd.getKillsTeam())).replace("%wTeam%", Utils.decimal(pd.getWinsTeam()))
						.replace("%kMega%", Utils.decimal(pd.getKillsMega())).replace("%wMega%", Utils.decimal(pd.getWinsMega())).replace("%coins%", Utils.decimal((int) pd.getCoins())).replace("%souls%", Utils.decimal(pd.getSouls()))));
		getInventory().setItem(48, back.getItem());
		getInventory().setItem(49, ItemUtils.createItem(total.getBuild().replace("%kAll%", Utils.decimal(pd.getKillsSolo() + pd.getKillsTeam() + pd.getKillsMega()))
				.replace("%wAll%", Utils.decimal(pd.getWinsSolo() + pd.getWinsTeam() + pd.getWinsMega())).replace("%coins%", Utils.decimal((int) pd.getCoins()))));
		p.openInventory(getInventory());
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		player = items.get("s_player");
		total = items.get("s_total");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ProfileMenu(p);
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
