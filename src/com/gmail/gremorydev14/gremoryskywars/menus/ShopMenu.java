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

public class ShopMenu extends Menu {

	public static MenuItem coins, back;
	private static MenuItem sKit, tKit, mKit, sPerk, tPerk, mPerk, cosmetics;

	public ShopMenu(Player p) {
		super(p, "SkyWars Shop", 6);
		if (sKit.getSlot() > -1)
			getInventory().setItem(sKit.getSlot(), sKit.getItem());
		if (tKit.getSlot() > -1)
			getInventory().setItem(tKit.getSlot(), tKit.getItem());
		if (mKit.getSlot() > -1)
			getInventory().setItem(mKit.getSlot(), mKit.getItem());
		if (sPerk.getSlot() > -1)
			getInventory().setItem(sPerk.getSlot(), sPerk.getItem());
		if (tPerk.getSlot() > -1)
			getInventory().setItem(tPerk.getSlot(), tPerk.getItem());
		if (mPerk.getSlot() > -1)
			getInventory().setItem(mPerk.getSlot(), mPerk.getItem());
		if (cosmetics.getSlot() > -1)
			getInventory().setItem(cosmetics.getSlot(), cosmetics.getItem());
		getInventory().setItem(48, back.getItem());
		getInventory().setItem(49, ItemUtils.createItem(coins.getBuild().replace("%coins%", Utils.decimal(PlayerData.get(p).getCoins()))));
		p.openInventory(getInventory());
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		sKit = items.get("sh_sKits");
		tKit = items.get("sh_tKits");
		mKit = items.get("sh_mKits");
		sPerk = items.get("sh_sPerks");
		tPerk = items.get("sh_tPerks");
		mPerk = items.get("sh_mPerks");
		cosmetics = items.get("sh_Cosmetics");
		coins = items.get("sh_Coins");
	}

	public void onClick(Player p, ItemStack item, int slot) {
		if (item.equals(back.getItem())) {
			p.closeInventory();
		} else if (item.equals(sKit.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopKitsMenu(p, Mode.SOLO);
		} else if (item.equals(tKit.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopKitsMenu(p, Mode.TEAM);
		} else if (item.equals(mKit.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopKitsMenu(p, Mode.MEGA);
		} else if (item.equals(sPerk.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopPerksMenu(p, Mode.SOLO);
		} else if (item.equals(tPerk.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopPerksMenu(p, Mode.TEAM);
		} else if (item.equals(mPerk.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopPerksMenu(p, Mode.MEGA);
		} else if (item.equals(cosmetics.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new CosmeticsMenu(p);
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
