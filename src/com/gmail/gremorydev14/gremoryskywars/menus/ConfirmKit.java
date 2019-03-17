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
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class ConfirmKit extends Menu {

	private Kit kit;

	public ConfirmKit(Player p, Kit kit) {
		super(p, kit.getName(), 3);
		this.kit = kit;
		ItemStack clone = kit.getShop().clone();
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
			if (pd.getCoins() >= kit.getPrice()) {
				pd.removeCoins(kit.getPrice());
				pd.getInventory().addKit(kit, kit.getMode());
				p.sendMessage(Language.messages$player$unlock_kit.replace("%kit%", kit.getName()));
				new ShopKitsMenu(p, kit.getMode());
			} else {
				new ShopKitsMenu(p, kit.getMode());
				p.sendMessage(Language.messages$player$no_have_enough_coins);
			}
		} else if (item.getItemMeta().getDisplayName().startsWith("§c")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ShopKitsMenu(p, kit.getMode());
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
