package com.gmail.gremorydev14.gremoryskywars.util.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

import lombok.Getter;

public class SmartInventory {

	@Getter
	private String name;
	@Getter
	private Map<Integer, Inventory> inventorys = new HashMap<>();

	public SmartInventory(String name) {
		this.name = name;
	}

	public int addInventory(String name) {
		int i = this.inventorys.size();

		Inventory inv = Bukkit.createInventory(null, 54, this.name + name);
		if (i > 0) {
			inv.setItem(48, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(i))));
			Inventory old = this.inventorys.get(i - 1);
			old.setItem(50, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(i + 1))));
		}
		this.inventorys.put(i, inv);
		return i;
	}

	public void setItem(int index, int slot, ItemStack item) {
		this.inventorys.get(index).setItem(slot, item);
	}

	public boolean addItem(int index, ItemStack item) {
		Inventory inv = this.inventorys.get(index);
		if (firstEmpty(index) == -1)
			return false;
		inv.setItem(firstEmpty(index), item);
		return true;
	}

	public void removeItem(int index, int slot) {
		this.setItem(index, slot, new ItemStack(Material.AIR));
		this.organize(index);
	}

	public ItemStack getItem(int index, int slot) {
		return this.inventorys.get(index).getItem(slot);
	}

	public void clear(int index) {
		this.inventorys.get(index).clear();
		Utils.cageInventory(this.inventorys.get(index), false);
	}

	public int firstEmpty(int index) {
		Inventory inv = this.inventorys.get(index);
		for (int i : slots)
			if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
				return i;
		return -1;
	}

	public void organize(int index) {
		Inventory inv = this.inventorys.get(index);
		List<ItemStack> list = new ArrayList<>();
		for (int i : slots) {
			if (inv.getItem(i) != null) {
				list.add(inv.getItem(i));
				this.removeItem(index, i);
			}
		}
		for (int i = 0; i < list.size(); i++)
			inv.setItem(slots[i], list.get(i));
	}

	public List<ItemStack> getContents(int index) {
		Inventory inv = this.inventorys.get(index);
		List<ItemStack> list = new ArrayList<>();
		for (int i : slots)
			if (inv.getItem(i) != null)
				list.add(inv.getItem(i));
		return list;
	}

	public void open(Player p) {
		p.openInventory(inventorys.get(0));
	}

	public static int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };
}
