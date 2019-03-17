package com.gmail.gremorydev14.gremoryskywars.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class ConfirmPerk extends Menu {

	private Perk perk;

	public ConfirmPerk(Player p, Perk perk) {
		super(p, perk.getName(), 3);
		this.perk = perk;
		ItemStack clone = perk.getUnlocked().clone();
		ItemMeta meta = clone.getItemMeta();
		meta.setDisplayName("§aConfirm");
		clone.setItemMeta(meta);
		clone.setType(Material.STAINED_CLAY);
		clone.setDurability((short) 5);
		getInventory().setItem(11, clone);
		getInventory().setItem(15, ItemUtils.createItem("STAINED_CLAY:14 : 1 : name=&cCancel"));
		p.openInventory(getInventory());
	}

	@Override
	public void onClick(Player p, ItemStack item) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null || pd.getArena() != null) {
			p.closeInventory();
			return;
		}

		if (item.getItemMeta().getDisplayName().startsWith("§a")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			if (pd.getCoins() >= perk.getPrice()) {
				pd.removeCoins(perk.getPrice());
				pd.getInventory().addPerk(perk, perk.getMode());
				p.sendMessage(Language.messages$player$unlock_perk.replace("%perk%", perk.getName()));
				new ShopPerksMenu(p, perk.getMode());
			} else {
				new ShopPerksMenu(p, perk.getMode());
				p.sendMessage(Language.messages$player$no_have_enough_coins);
			}
		} else if (item.getItemMeta().getDisplayName().startsWith("§c")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopPerksMenu(p, perk.getMode());
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getInventory().equals(getInventory())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked() instanceof Player && getPlayer() == (Player) e.getWhoClicked()) {
				onClick((Player) e.getWhoClicked(), e.getCurrentItem());
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
}
