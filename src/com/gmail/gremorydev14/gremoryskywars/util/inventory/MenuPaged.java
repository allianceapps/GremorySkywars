package com.gmail.gremorydev14.gremoryskywars.util.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;

import lombok.Getter;
import lombok.Setter;

public abstract class MenuPaged implements Listener {
	@Getter
	private Player player;
	@Getter
	private List<Inventory> inventorys;

	protected int currentPage = 1;
	protected int inventorySize = 54;
	protected String inventoryTitle;
	protected boolean openPage = false;
	@Setter
	private int slotNext = 50, slotPrevious = 48, slotGo = 48;

	Map<Integer, ItemStack> noUse = new HashMap<>();

	public MenuPaged(Player p, String title, int size) {
		this.player = p;
		this.inventorySize = size;
		this.inventorys = new ArrayList<>();
		this.inventoryTitle = title.replace("&", "§");
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}

	public boolean addNoItems(ItemStack item, int... slots) {
		if (slots.length == 0)
			return false;
		for (int i : slots)
			noUse.put(i, item != null ? item : new ItemStack(Material.AIR));
		return true;
	}

	public boolean addItems(List<ItemStack> list, boolean goBack, int firstSlot, int endSlot) {
		if (list.isEmpty()) {
			ItemStack[] itens = generatePage(inventorySize);
			for (Entry<Integer, ItemStack> entry : noUse.entrySet())
				itens[entry.getKey()] = entry.getValue();
			Inventory inv = Bukkit.createInventory(null, itens.length, inventoryTitle);
			inv.setContents(itens);
			if (goBack)
				inv.setItem(slotGo, MenuEditor.getItems().get("back").getItem());
			inventorys.add(inv);
		} else {
			int currentSlot = firstSlot;
			ItemStack[] itens = generatePage(inventorySize);
			for (Entry<Integer, ItemStack> entry : noUse.entrySet())
				itens[entry.getKey()] = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				if (itens == null) {
					itens = generatePage(inventorySize);
					currentSlot = firstSlot;
					for (Entry<Integer, ItemStack> entry : noUse.entrySet())
						itens[entry.getKey()] = entry.getValue();
				}
				if (noUse.containsKey(currentSlot)) {
					--i;
					currentSlot++;
					if (currentSlot == endSlot) {
						Inventory inv = Bukkit.createInventory(null, itens.length, inventoryTitle);
						inv.setContents(itens);
						inventorys.add(inv);
						int size = inventorys.size();
						if (size > 1) {
							inv.setItem(slotPrevious, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size - 1))));
							inventorys.get(size - 2).setItem(slotNext, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size))));
						} else if (goBack)
							inv.setItem(slotGo, MenuEditor.getItems().get("back").getItem());
						itens = null;
						continue;
					}
				} else {
					if (currentSlot == endSlot) {
						--i;
						Inventory inv = Bukkit.createInventory(null, itens.length, inventoryTitle);
						inv.setContents(itens);
						inventorys.add(inv);
						int size = inventorys.size();
						if (size > 1) {
							inv.setItem(slotPrevious, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size - 1))));
							inventorys.get(size - 2).setItem(slotNext, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size))));
						} else if (goBack)
							inv.setItem(slotGo, MenuEditor.getItems().get("back").getItem());
						itens = null;
						continue;
					}

					try {
						itens[currentSlot++] = list.get(i);
					} catch (Exception e) {
						--i;
						Inventory inv = Bukkit.createInventory(null, itens.length, inventoryTitle);
						inv.setContents(itens);
						inventorys.add(inv);
						int size = inventorys.size();
						if (size > 1) {
							inv.setItem(slotPrevious, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size - 1))));
							inventorys.get(size - 2).setItem(slotNext, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size))));
						} else if (goBack)
							inv.setItem(slotGo, MenuEditor.getItems().get("back").getItem());
						itens = null;
						continue;
					}
					if (i + 1 == list.size()) {
						Inventory inv = Bukkit.createInventory(null, itens.length, inventoryTitle);
						for (Entry<Integer, ItemStack> entry : noUse.entrySet())
							itens[entry.getKey()] = entry.getValue();
						inv.setContents(itens);
						inventorys.add(inv);
						int size = inventorys.size();
						if (size > 1) {
							inv.setItem(slotPrevious, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size - 1))));
							inventorys.get(size - 2).setItem(slotNext, ItemUtils.createItem(MenuEditor.getItems().get("page").getBuild().replace("%page%", String.valueOf(size))));
						} else if (goBack)
							inv.setItem(slotGo, MenuEditor.getItems().get("back").getItem());
						break;
					}
				}
			}
		}
		currentPage = 1;
		return true;
	}

	public boolean openPage(Player p, int page) {
		if (page < 1 || page > inventorys.size())
			return false;
		if (p == null || !p.isOnline())
			return false;
		currentPage = page;
		Inventory inv = inventorys.get(currentPage - 1);
		openPage = true;
		p.openInventory(inv);
		openPage = false;
		return true;
	}

	public boolean inventoryEquals(Inventory inv) {
		return inventorys.get(currentPage - 1).equals(inv);
	}

	private ItemStack[] generatePage(int pageSize) {
		pageSize = (int) (Math.ceil((double) pageSize / 9)) * 9;
		return new ItemStack[pageSize];
	}
}
