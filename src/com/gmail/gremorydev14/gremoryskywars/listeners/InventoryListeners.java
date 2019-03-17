package com.gmail.gremorydev14.gremoryskywars.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.delivery.Delivery;
import com.gmail.gremorydev14.delivery.SwDelivery;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.arena.Chest.ChestItem;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.mystery.SoulWell;

@SuppressWarnings("all")
public class InventoryListeners implements Listener {

	public InventoryListeners() {
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			Inventory inv = e.getInventory();
			ItemStack item = e.getCurrentItem();

			if (inv.getTitle().equals("Soul Well")) {
				e.setCancelled(true);
				if (p.hasMetadata("SOUL_WELL")) {
					SoulWell sw = (SoulWell) p.getMetadata("SOUL_WELL").get(0).value();

					if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();

						if (name.equals("§aOpen")) {
							p.closeInventory();
							if (PlayerData.get(p) != null) {
								sw.open(PlayerData.get(p));
							}
						} else if (name.equals("§cCancel")) {
							p.closeInventory();
						}
					}
				}
			} else if (inv.getTitle().equals("The Delivery Man")) {
				e.setCancelled(true);
				if (p.hasMetadata("DELIVERY_MAP")) {
					if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();

						if (!name.startsWith("§c")) {
							p.closeInventory();
							Delivery d = ((Map<Integer, Delivery>) p.getMetadata("DELIVERY_MAP").get(0).value()).get(e.getSlot());
							if (d.has(p)) {
								SwDelivery data = SwDelivery.get(p);
								if (data != null) {
									Date date = new Date();
									date.setHours(23);
									date.setMinutes(59);
									date.setSeconds(59);
									data.set(d.getId(), d.getTime().getDays() == 1 ? date.getTime() : (((d.getTime().getDays() - 1) * 86400) * 1000) + date.getTime());
									for (String command : d.getCommands()) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()).replace("&", "§"));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onEdit(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			int slot = e.getRawSlot();
			Player p = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			if (e.getInventory().getTitle().startsWith("§8Editing: §b")) {
				if (item == null || item.getType() == Material.AIR || (item.getType() == Material.STAINED_GLASS_PANE && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§8-")))
					return;
				Chest chest = Chest.getChest(ChatColor.stripColor(e.getInventory().getTitle().split(": ")[1].split(" ")[0]));
				if (chest == null) {
					p.closeInventory();
					return;
				}

				if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains(MenuEditor.getItems().get("page").getDisplay())) {
					if (slot == 50) {
						if (Main.getSound_orb() != null)
							p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
						e.setCancelled(true);
						p.closeInventory();
						p.openInventory(chest.getEditor().getInventorys().get(Integer.parseInt(ChatColor.stripColor(e.getInventory().getTitle().split(": ")[1].split(" ")[1].replace("#", "")))));
						return;
					} else {
						if (Main.getSound_orb() != null)
							p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
						e.setCancelled(true);
						p.closeInventory();
						p.openInventory(chest.getEditor().getInventorys().get(Integer.parseInt(ChatColor.stripColor(e.getInventory().getTitle().split(": ")[1].split(" ")[1].replace("#", ""))) - 2));
						return;
					}
				} else if (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§2Add page")) {
					if (Main.getSound_orb() != null)
						p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
					e.setCancelled(true);
					e.getInventory().setItem(slot, null);
					int index = chest.getEditor().addInventory(chest.getName() + " §e#" + (chest.getEditor().getInventorys().size() + 1));
					Utils.cageInventory(chest.getEditor().getInventorys().get(index), false);
					chest.addSettings(index);
					return;
				}

				if (slot < 54 && slot > 44) {
					e.setCancelled(true);
					if (item.getItemMeta().getDisplayName().equals("§bSave")) {
						if (Main.getSound_orb() != null)
							p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
						long start = System.currentTimeMillis();
						List<ItemStack> contents = new ArrayList<>();
						for (int index = 0; index < chest.getEditor().getInventorys().size(); index++)
							contents.addAll(chest.getEditor().getContents(index));
						List<String> parseds = new ArrayList<>();
						List<ChestItem> ci = new ArrayList<>();
						for (ItemStack content : contents) {
							parseds.add((chest.getChestItem(content) != null ? chest.getChestItem(content).getPercentage() : 100) + " : " + Utils.serializeItemStack(content));
							ci.add(new ChestItem(content, (chest.getChestItem(content) != null ? chest.getChestItem(content).getPercentage() : 100)));
						}
						SettingsManager sm = SettingsManager.getConfig("chests");
						sm.set("chests." + chest.getName() + ".items", parseds);
						chest.setItems(ci);
						p.closeInventory();
						p.sendMessage("§aChest saved, after: " + (System.currentTimeMillis() - start) + "ms");
						return;
					}
				}
				return;
			}

			PlayerData pd = PlayerData.get(p);
			if (pd != null && Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd.getArena() == null && p.getGameMode() != GameMode.CREATIVE)
				e.setCancelled(true);
			if (pd != null && pd.getArena() != null && pd.getArena().getSpectators().contains(p))
				e.setCancelled(true);
		}
	}
}
