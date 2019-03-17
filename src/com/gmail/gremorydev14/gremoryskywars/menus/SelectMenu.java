package com.gmail.gremorydev14.gremoryskywars.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;

public class SelectMenu extends MenuPaged {

	private Map<ItemStack, List<Arena>> items = new HashMap<>();
	private Map<String, Integer> old = new HashMap<>();

	private Mode mode;
	private Type type;

	public SelectMenu(Player p, Mode mode, Type type) {
		super(p, mode.getName() + ": " + type.getName(), 54);
		this.mode = mode;
		this.type = type;
		this.addNoItems(null, 9, 17, 18, 26, 27, 35, 36);
		this.addNoItems(ItemUtils.createItem("FIREWORK : 1 : name=&aRandom map"), 39);
		List<ItemStack> all = new ArrayList<>();

		for (Arena a : Arena.getList(mode, type)) {
			if (Arena.getList(mode, type, a.getName()) >= 1) {
				ItemStack item = ItemUtils.createFirework("&a" + a.getName(), 1, Color.YELLOW,
						new String[] { "&8" + a.getMode().getName() + " " + a.getType().getName(), "", "&7Rating: &e" + a.getRate() + "✫", "&7Avaible Servers: &a" + Arena.getList(mode, type, a.getName()), "", "&e► Click to play!" });

				if (items.get(item) == null) {
					List<Arena> list = new ArrayList<>();
					list.add(a);
					items.put(item, list);
					all.add(item);
					old.put(a.getName(), all.size() - 1);
				} else {
					List<Arena> list = items.get(item);
					list.add(a);
					items.put(item, list);
					all.remove((int)old.get(a.getName()));
					all.add(item);
					old.put(a.getName(), all.size() - 1);
				}
			}
		}
		this.addItems(all, false, 10, 35);
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
		} else if (item.getType() == Material.FIREWORK) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			Arena a = Arena.getFrom(mode, type);
			if (a != null)
				a.add(p);
			else
				p.sendMessage(Language.messages$player$play_again_null);
		} else if (item.getItemMeta().getDisplayName().startsWith("§a")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			List<Arena> a = items.get(item);
			for (Arena arena : a)
				if (arena.getState().isAvaible() && arena.getPlayers().size() < arena.getSpawns().size() * arena.getMode().getSize()) {
					arena.add(p);
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
