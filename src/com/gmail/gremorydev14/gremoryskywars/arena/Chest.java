package com.gmail.gremorydev14.gremoryskywars.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.SmartInventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Chest {
	@Getter
	private String name, refill;
	@Getter
	private SmartInventory editor;
	@Getter
	@Setter
	private List<ChestItem> items = new ArrayList<>();

	public static final List<Integer> SLOTS = new ArrayList<>();

	public Chest(String name, String refill, List<ChestItem> items) {
		this.name = name;
		this.items = items;
		this.refill = refill;

		this.editor = new SmartInventory("§8Editing: §b");
		int i = 0;
		double j2 = Math.ceil(Double.valueOf(items.isEmpty() ? 1 : items.size() / SmartInventory.slots.length) + 1.0d);
		if (!items.isEmpty() && j2 < 2.0)
			j2 += 1.0d;
		for (int j = 1; j < j2; j++) {
			int index = this.editor.addInventory(name + " §e#" + j);
			Utils.cageInventory(this.editor.getInventorys().get(index), false);
			for (int slot : SmartInventory.slots) {
				if (i >= items.size())
					break;
				this.editor.setItem(index, slot, items.get(i++).getItem());
			}
			addSettings(index);
		}
		chests.put(name.toLowerCase(), this);
	}

	public void addSettings(int index) {
		this.editor.setItem(index, 49, ItemUtils.createItem("WATCH : 1 : name=&bSave"));
		this.editor.setItem(index, 44, ItemUtils.createItem("ARROW : 1 : name=&2Add page"));
	}

	public void fill(Location location) {
		if (location.getBlock().getType() == Material.CHEST) {
			Collections.shuffle(SLOTS);
			Collections.shuffle(items);

			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
			chest.getInventory().clear();
			if (items.isEmpty())
				return;
			int slot = 0;
			for (ChestItem item : items)
				if (new Random().nextInt(100) <= item.getPercentage()) {
					chest.getInventory().setItem(SLOTS.get(slot++), item.getItem());
					if (slot >= 27)
						break;
				}
			chest.update();
		}
	}

	public void refill(Location location) {
		if (location.getBlock().getType() == Material.CHEST) {
			Collections.shuffle(SLOTS);

			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
			chest.getInventory().clear();
			Chest c = getChest(refill) != null ? getChest(refill) : this;
			if (c.getItems().isEmpty()) {
				return;
			}
			Collections.shuffle(c.getItems());
			int slot = 0;
			for (ChestItem item : c.getItems())
				if (new Random().nextInt(100) <= item.getPercentage()) {
					chest.getInventory().setItem(SLOTS.get(slot++), item.getItem());
					if (slot >= 27) {
						break;
					}
				}
			chest.update();
		}
	}

	private static Map<String, Chest> chests = new HashMap<>();

	public static void register() {
		SettingsManager sm = SettingsManager.getConfig("chests");
		ConfigurationSection sec = sm.getSection("chests");
		for (String key : sec.getKeys(false)) {
			try {
				String name = key;
				String refill = sec.getString(key + ".refill");
				List<String> items = sec.getStringList(key + ".items");
				List<ChestItem> stacks = new ArrayList<>();
				for (String item : items) {
					try {
						String itemStackString = "";
						for (int i = 1; i < item.split(" : ").length; i++)
							itemStackString += item.split(" : ")[i] + (i + 1 == item.split(" : ").length ? "" : " : ");
						ItemStack itemStack = ItemUtils.createItem(itemStackString);
						ChestItem ci = new ChestItem(itemStack, Integer.parseInt(item.split(" : ")[0]));
						stacks.add(ci);
					} catch (Exception ignore) {
					}
				}
				new Chest(name, refill, stacks);
			} catch (Exception e) {
			}
		}
		if (chests.isEmpty()) {
			new Chest("Default", "Default", new ArrayList<>());
		}
		Logger.info("Loaded " + chests.size() + " chest(s)!");
	}

	public static Chest getChest(String name) {
		return chests.get(name.toLowerCase());
	}

	public static List<Chest> getChests() {
		return new ArrayList<>(chests.values());
	}

	public ChestItem getChestItem(ItemStack item) {
		for (ChestItem items : this.items)
			if (items.getItem().equals(item))
				return items;
		return null;
	}

	static {
		for (int i = 0; i < 27; i++) {
			SLOTS.add(i);
		}
	}

	@AllArgsConstructor
	@Getter
	public static class ChestItem {
		private ItemStack item;
		private int percentage;

	}
}
