package com.gmail.gremorydev14.profile.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;
import com.gmail.gremorydev14.profile.Booster;
import com.gmail.gremorydev14.profile.Profile;

public class PrivateBoosters extends MenuPaged {

	private static MenuItem back, booster;
	private Map<ItemStack, Booster> boosters = new HashMap<>();

	public PrivateBoosters(Player p) {
		super(p, "Private Boosters", 36);
		this.setSlotNext(35);
		this.setSlotPrevious(26);
		this.setSlotGo(30);

		Profile pf = Profile.get(p);
		List<ItemStack> all = new ArrayList<>();
		for (Booster b : pf.getBoosters()) {
			ItemStack item = ItemUtils
					.createItem(booster.getBuild().replace("%multiplier%", "" + b.getType().getModifier()).replace("%time%", "" + b.getTime()).replace("%timeType%", b.getTimeType().getName().replace(b.getTime() > 1 ? "" : "s", "")));
			all.add(item);
			boosters.put(item, b);
		}
		this.addItems(all, true, 0, 26);
		this.openPage(p, 1);
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		booster = items.get("b_booster");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		Profile pf = Profile.get(p);
		PlayerData pd = PlayerData.get(p);
		if (pf == null || pd == null) {
			p.closeInventory();
			return;
		}
		if (item.getItemMeta().getDisplayName().contains(MenuEditor.getItems().get("page").getDisplay())) {
			if (slot == 50)
				this.openPage(p, currentPage + 1);
			else if (slot == 48)
				this.openPage(p, currentPage - 1);
		} else if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new BoostersMenu(p);
		} else {
			Booster b = boosters.get(item);
			if (b != null) {
				p.closeInventory();
				pf.useBoost(b);
			}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (inventoryEquals(e.getInventory())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked() instanceof Player && getPlayer() == (Player) e.getWhoClicked()) {
				onClick((Player) e.getWhoClicked(), e.getCurrentItem(), e.getSlot());
			}
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (inventoryEquals(e.getInventory()) && e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
