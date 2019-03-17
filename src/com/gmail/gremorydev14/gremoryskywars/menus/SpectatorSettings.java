package com.gmail.gremorydev14.gremoryskywars.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;

public class SpectatorSettings extends Menu {

	public SpectatorSettings(Player p) {
		super(p, "Spectator Options", 4);
		getInventory().setItem(10, ItemUtils.createItem("LEATHER_BOOTS : 1 : name=&cResetar"));
		getInventory().setItem(11, ItemUtils.createItem("CHAINMAIL_BOOTS : 1 : name=&aVelocidade 1"));
		getInventory().setItem(12, ItemUtils.createItem("IRON_BOOTS : 1 : name=&aVelocidade 2"));
		getInventory().setItem(13, ItemUtils.createItem("GOLD_BOOTS : 1 : name=&aVelocidade 3"));
		getInventory().setItem(14, ItemUtils.createItem("DIAMOND_BOOTS : 1 : name=&aVelocidade 4"));
		p.openInventory(getInventory());
	}

	@Override
	public void onClick(Player p, ItemStack item) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null || pd.getArena() == null) {
			p.closeInventory();
			return;
		}

		if (item.getItemMeta().getDisplayName().startsWith("§aSpeed")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			if (pd.getArena() != null) {
				int level = Integer.parseInt(item.getItemMeta().getDisplayName().replace("§aVelocidade ", ""));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, level));
			}
		} else if (item.getItemMeta().getDisplayName().startsWith("§cResetar")) {
			p.closeInventory();
			if (Main.getSound_orb() != null) {
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			}
			p.removePotionEffect(PotionEffectType.SPEED);
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
