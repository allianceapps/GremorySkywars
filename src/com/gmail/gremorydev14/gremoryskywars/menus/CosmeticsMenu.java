package com.gmail.gremorydev14.gremoryskywars.menus;

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
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class CosmeticsMenu extends Menu {

	private static MenuItem back, cages, perks, coins;

	public CosmeticsMenu(Player p) {
		super(p, "SkyWars Cosmetics", 6);
		PlayerData pd = PlayerData.get(p);
		if (cages.getSlot() > -1)
			getInventory().setItem(cages.getSlot(), ItemUtils.createItem(cages.getBuild().replace("%cage%", pd.getCage().getName())));
		if (perks.getSlot() > -1)
			getInventory().setItem(perks.getSlot(), ItemUtils.createItem(perks.getBuild().replace("%sPerk%", pd.getSPerk() != null ? pd.getSPerk().getName() : "None")
					.replace("%tPerk%", pd.getTPerk() != null ? pd.getTPerk().getName() : "None").replace("%mPerk%", pd.getMPerk() != null ? pd.getMPerk().getName() : "None")));
		getInventory().setItem(48, back.getItem());
		getInventory().setItem(49, ItemUtils.createItem(coins.getBuild().replace("%coins%", Utils.decimal(pd.getCoins()))));
		p.openInventory(getInventory());
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		coins = items.get("sh_Coins");
		cages = items.get("c_Cages");
		perks = items.get("c_Perks");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		if (item.equals(back.getItem())) {
			new ShopMenu(p);
		} else if (item.getItemMeta().getDisplayName().equals(perks.getDisplay())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new PerksMenu(p, Mode.SOLO);
		} else if (item.getItemMeta().getDisplayName().equals(cages.getDisplay())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new CagesMenu(p);
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
