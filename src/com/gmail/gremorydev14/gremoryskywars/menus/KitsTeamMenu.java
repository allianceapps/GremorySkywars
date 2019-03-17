package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Found;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;

public class KitsTeamMenu extends MenuPaged {

	public KitsTeamMenu(Player p) {
		super(p, "Team Kits", 54);
		this.addNoItems(null, 9, 17, 18, 26, 27, 35, 36);
		List<ItemStack> all = new ArrayList<>();
		for (Kit kit : Kit.getKits2()) {
			if (kit.has(p)) {
				all.add(kit.getIcon());
			} else {
				all.add(kit.getUnlocked());
			}
		}
		this.addItems(all, false, 10, 44);
		this.openPage(p, 1);
	}

	public void onClick(Player p, ItemStack item, int slot) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null) {
			p.closeInventory();
			return;
		}
		if (item.getItemMeta().getDisplayName().contains(MenuEditor.getItems().get("page").getDisplay())) {
			if (slot == 50)
				this.openPage(p, currentPage + 1);
			else if (slot == 48)
				this.openPage(p, currentPage - 1);
		} else {
			for (Kit kit : Kit.getKits2())
				if (kit.getIcon().equals(item)) {
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}

					pd.setTKit(kit);
					p.sendMessage(Language.messages$player$select_kit.replace("%kit%", kit.getName()));
					break;
				} else if (kit.getUnlocked().equals(item)) {
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}

					if (kit.getFound() == Found.SOUL_WELL) {
						p.sendMessage(Language.messages$player$kit_soulwell);
					} else if (kit.getFound() == Found.RANK) {
						p.sendMessage(Language.messages$player$kit_rank);
					} else {
						p.sendMessage(Language.messages$player$kit_shop);
					}
					break;
				}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (inventoryEquals(e.getInventory())) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item != null && item.hasItemMeta() && p == getPlayer())
				onClick(p, item, e.getSlot());
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (inventoryEquals(e.getInventory()) && e.getPlayer() == getPlayer() && !openPage)
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
