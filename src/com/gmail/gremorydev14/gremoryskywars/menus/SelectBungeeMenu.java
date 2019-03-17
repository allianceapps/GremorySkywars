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
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Ping;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;

public class SelectBungeeMenu extends MenuPaged {

	private Map<ItemStack, List<String>> items = new HashMap<>();
	private Map<String, Integer> old = new HashMap<>();

	private Mode mode;
	private Type type;

	public SelectBungeeMenu(Player p, Mode mode, Type type) {
		super(p, mode.getName() + ": " + type.getName(), 54);
		this.mode = mode;
		this.type = type;
		this.addNoItems(null, 9, 17, 18, 26, 27, 35, 36);
		this.addNoItems(ItemUtils.createItem("FIREWORK : 1 : name=&aRandom map"), 39);
		List<ItemStack> all = new ArrayList<>();

		for (String s : Arena.getListBungee(mode, type)) {
			String ip = s.split(" : ")[0];
			int port = Integer.parseInt(s.split(" : ")[1]);
			Ping ping = new Ping(ip, port, 1000);
			String motd = ping.getMotd();

			Mode mode2 = Mode.valueOf(motd.split(":")[1].toUpperCase());
			Type type2 = Type.valueOf(motd.split(":")[2].toUpperCase());
			if (mode2.equals(mode) && type2.equals(type)) {
				if (Arena.getListBungee(mode, type, motd.split(":")[3]) >= 1) {
					ItemStack item = ItemUtils.createFirework("&a" + motd.split(":")[3], 1, Color.YELLOW, new String[] { "&8" + mode2.getName() + " " + type2.getName(), "", "&7Rating: &e" + motd.split(":")[4] + "✫",
							"&7Avaible Servers: &a" + Arena.getListBungee(mode, type, motd.split(":")[3]), "", "&e► Click to play!" });
					if (items.get(item) == null) {
						List<String> list = new ArrayList<>();
						list.add(s);
						items.put(item, list);
						all.add(item);
						old.put(motd.split(":")[3], all.size() - 1);
					} else {
						List<String> list = items.get(item);
						list.add(s);
						items.put(item, list);
						all.remove((int)old.get(motd.split(":")[3]));
						all.add(item);
						old.put(motd.split(":")[3], all.size() - 1);
					}
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
			String server = Arena.getBungee(mode, type);
			if (server != null)
				Utils.sendToServer(p, server);
			else
				p.sendMessage(Language.messages$player$play_again_null);
		} else if (item.getItemMeta().getDisplayName().startsWith("§a")) {
			p.closeInventory();
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			List<String> servers = items.get(item);
			for (String s : servers) {
				String ip = s.split(" : ")[0];
				int port = Integer.parseInt(s.split(" : ")[1]);
				Ping ping = new Ping(ip, port, 1000);
				String motd = ping.getMotd();

				if (motd != null && State.valueOf(motd.split(":")[0].toUpperCase()).isAvaible() && ping.getOnline() < ping.getMax())
					Utils.sendToServer(p, s.split(" : ")[2]);
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
