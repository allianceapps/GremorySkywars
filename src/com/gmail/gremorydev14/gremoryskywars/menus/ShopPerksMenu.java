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
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Found;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;

public class ShopPerksMenu extends MenuPaged {

	private Mode mode;

	public ShopPerksMenu(Player p, Mode mode) {
		super(p, mode.getName() + " Perks", 54);
		this.mode = mode;
		this.addNoItems(null, 9, 17, 18, 26, 27, 35, 36);
		this.addNoItems(ItemUtils.createItem(ShopMenu.coins.getBuild().replace("%coins%", Utils.decimal(PlayerData.get(p).getCoins()))), 49);
		List<ItemStack> all = new ArrayList<>();
		if (mode == Mode.SOLO) {
			for (Perk perk : Perk.getPerks()) {
				if (perk.has(p)) {
					all.add(perk.getLocked());
				} else {
					all.add(perk.getUnlocked());
				}
			}
		} else if (mode == Mode.TEAM) {
			for (Perk perk : Perk.getPerks2()) {
				if (perk.has(p)) {
					all.add(perk.getLocked());
				} else {
					all.add(perk.getUnlocked());
				}
			}
		} else {
			for (Perk perk : Perk.getPerks3()) {
				if (perk.has(p)) {
					all.add(perk.getLocked());
				} else {
					all.add(perk.getUnlocked());
				}
			}
		}
		this.addItems(all, true, 10, 44);
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
		} else if (item.equals(ShopMenu.back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopMenu(p);
		} else {
			List<Perk> perks = mode == Mode.SOLO ? Perk.getPerks() : mode == Mode.TEAM ? Perk.getPerks2() : Perk.getPerks3();
			for (Perk perk : perks) {
				if (perk.getLocked().equals(item)) {
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}
					
					p.sendMessage(Language.messages$player$already_have_perk.replace("%perk%", perk.getName()));
					break;
				} else if (perk.getUnlocked().equals(item)) {
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}
					
					if (perk.getFound() == Found.SOUL_WELL) {
						p.sendMessage(Language.messages$player$perk_soulwell);
					} else if (perk.getFound() == Found.RANK) {
						p.sendMessage(Language.messages$player$perk_rank);
					} else {
						if (pd.getCoins() >= perk.getPrice()) {
							new ConfirmPerk(p, perk);
						} else {
							p.sendMessage(Language.messages$player$no_have_enough_coins);
						}
					}
					break;
				}
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
