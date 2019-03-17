package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.ArrayList;
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
import com.gmail.gremorydev14.gremoryskywars.player.SwAchievement;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;
import com.gmail.gremorydev14.profile.menus.AchievementMenu;

public class SkywarsAchievementsMenu extends MenuPaged {

	private static MenuItem back, achievements;

	public SkywarsAchievementsMenu(Player p) {
		super(p, "SkyWars Achievements", 54);
		this.addNoItems(null, 4, 5, 6, 7, 8, 13, 14, 15, 16, 17, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35);
		this.addNoItems(achievements.getItem(), 49);
		List<ItemStack> items = new ArrayList<>();
		for (SwAchievement swa : SwAchievement.getAchievements())
			items.add(swa.getIcon(p));
		this.addItems(items, true, 0, 40);
		this.openPage(p, 1);
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		achievements = items.get("p_achievements");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		if (item.getItemMeta().getDisplayName().contains(MenuEditor.getItems().get("page").getDisplay())) {
			if (slot == 50)
				this.openPage(p, currentPage + 1);
			else if (slot == 48)
				this.openPage(p, currentPage - 1);
		} else if (item.equals(back.getItem()) || item.equals(achievements.getItem())) {
			if (Main.getSound_orb() != null) {
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			}
			
			new AchievementMenu(p);
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
