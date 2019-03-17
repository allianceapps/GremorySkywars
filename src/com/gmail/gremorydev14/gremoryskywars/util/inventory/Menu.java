package com.gmail.gremorydev14.gremoryskywars.util.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.Main;

import lombok.Getter;

public abstract class Menu implements Listener {
	@Getter
	private static Map<String, Menu> menus = new HashMap<>();

	@Getter
	private Inventory inventory;
	@Getter
	private Player player;

	public Menu(String title, int row) {
		this.inventory = Bukkit.createInventory(null, 9 * row > 54 ? 54 : 9 * row, title.replace("&", "§"));
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}

	public Menu(Player p, String title, int row) {
		if (row == 0)
			row = 1;
		this.player = p;
		this.inventory = Bukkit.createInventory(null, 9 * row > 54 ? 54 : 9 * row, title.replace("&", "§"));
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}

	public Menu(String name, String title, int row) {
		this.inventory = Bukkit.createInventory(null, 9 * row > 54 ? 54 : 9 * row, title.replace("&", "§"));
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
		menus.put(name, this);
	}

	public void registerFor(Player p) {
	}

	public abstract void onClick(Player p, ItemStack item);
}
