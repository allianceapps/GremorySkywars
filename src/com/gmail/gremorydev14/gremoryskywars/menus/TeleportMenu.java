package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class TeleportMenu extends Menu {

	public TeleportMenu(PlayerData pd) {
		super(pd.getPlayer(), "Teleport", pd.getArena().playersAlive() / 9);
		Arena arena = pd.getArena();
		List<ItemStack> all = new ArrayList<>();

		for (Player ps : arena.getPlayers().keySet()) {
			if (!arena.getSpectators().contains(ps)) {
				all.add(ItemUtils.createSkull("3 : nome=&a" + ps.getName() + " : dono=" + ps.getName() + " : lore=Kills: &7" + arena.getKills(ps) + "\n \n&eClick-teleporte!"));
			}
		}
		for (int i = 0; i < all.size(); i++) {
			if (getInventory().firstEmpty() == -1) {
				break;
			}

			getInventory().setItem(getInventory().firstEmpty(), all.get(i));
		}
		pd.getPlayer().openInventory(getInventory());
	}

	@Override
	public void onClick(Player p, ItemStack item) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null || pd.getArena() == null) {
			p.closeInventory();
			return;
		}
		Arena arena = pd.getArena();

		if (item.getType() == Material.SKULL_ITEM && item.hasItemMeta()) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			Player p2 = Bukkit.getPlayerExact(item.getItemMeta().getDisplayName().replace("§a", ""));
			if (p2 != null && arena.getPlayers().containsKey(p2) && !arena.getSpectators().contains(p2))
				p.teleport(p2);
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getInventory().equals(getInventory())) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item != null && item.hasItemMeta() && p == getPlayer())
				onClick(p, item);
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
