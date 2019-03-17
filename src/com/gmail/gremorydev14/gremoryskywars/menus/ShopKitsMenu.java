package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.ArrayList;
import java.util.Collection;
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
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;

public class ShopKitsMenu extends MenuPaged {

	private Mode mode;

	public ShopKitsMenu(Player p, Mode mode) {
		super(p, mode.getName() + " Kits", 54);
		this.mode = mode;
		this.addNoItems(null, 9, 17, 18, 26, 27, 35, 36);
		this.addNoItems(ItemUtils.createItem(ShopMenu.coins.getBuild().replace("%coins%", Utils.decimal(PlayerData.get(p).getCoins()))), 49);
		List<ItemStack> all = new ArrayList<>();
		if (mode == Mode.SOLO) {
			for (Kit kit : Kit.getKits()) {
				if (kit.has(p)) {
					all.add(kit.getLocked());
				} else {
					all.add(kit.getShop());
				}
			}
		} else if (mode == Mode.TEAM) {
			for (Kit kit : Kit.getKits2()) {
				if (kit.has(p)) {
					all.add(kit.getLocked());
				} else {
					all.add(kit.getShop());
				}
			}
		} else {
			for (Kit kit : Kit.getKits3()) {
				if (kit.has(p)) {
					all.add(kit.getLocked());
				} else {
					all.add(kit.getShop());
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
			Collection<Kit> kits = mode == Mode.SOLO ? Kit.getKits() : mode == Mode.TEAM ? Kit.getKits2() : Kit.getKits3();
			for (Kit kit : kits)
				if (kit.getLocked().equals(item)) {
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}
					
					p.sendMessage(Language.messages$player$already_have_kit.replace("%kit%", kit.getName()));
					break;
				} else if (kit.getShop().equals(item)) {
					p.closeInventory();
					if (Main.getSound_orb() != null) {
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					}
					
					if (kit.getFound() == Found.SOUL_WELL) {
						p.sendMessage(Language.messages$player$kit_soulwell);
					} else if (kit.getFound() == Found.RANK) {
						p.sendMessage(Language.messages$player$kit_rank);
					} else {
						if (pd.getCoins() >= kit.getPrice()) {
							new ConfirmKit(p, kit);
						} else {
							p.sendMessage(Language.messages$player$no_have_enough_coins);
						}
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
