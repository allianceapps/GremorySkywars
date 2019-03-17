package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Found;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Rarity;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.mystery.SoulWell.Reward;
import com.gmail.gremorydev14.profile.Rank;

import lombok.Getter;

@SuppressWarnings("all")
public class Cage implements Reward {

	@Getter
	private int price, id;
	@Getter
	private ItemStack icon, unlocked;
	@Getter
	private String name;
	@Getter
	private Rarity rarity;
	@Getter
	private Found found;
	@Getter
	private ItemStack[] parts = new ItemStack[5];

	public Cage(ItemStack icon, String name, Rarity r, Found f, ItemStack[] parts, int price) {
		this.name = name;
		this.rarity = r;
		this.found = f;
		this.parts = parts;
		this.price = price;
		ItemStack icon1 = icon.clone();
		ItemMeta meta1 = icon1.getItemMeta();
		List<String> lore1 = meta1.hasLore() ? meta1.getLore() : new ArrayList<>();
		lore1.add("");
		lore1.add("§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase());
		if (this.found == Found.ALL || this.found == Found.PERMISSION)
			this.found = Found.SOUL_WELL;
		lore1.add("§eClick to select!");
		meta1.setLore(lore1);
		icon1.setItemMeta(meta1);
		this.icon = icon1;
		ItemStack icon2 = getIcon().clone();
		icon2.setType(Material.INK_SACK);
		icon2.setDurability((short) 8);
		ItemMeta meta2 = icon2.getItemMeta();
		List<String> lore2 = meta2.getLore();
		lore2.remove(lore2.size() - 1);
		lore2.add(this.found.getDesc());
		meta2.setLore(lore2);
		icon2.setItemMeta(meta2);
		this.unlocked = icon2;
		cages.put(name.toLowerCase(), this);
		this.id = cages.size();
	}

	public void apply(Location loc, boolean team) {
		Location loc2 = loc;
		loc = new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ());
		if (!team) {
			loc.add(0, -1, 0).getBlock().setTypeIdAndData(parts[0].getTypeId(), parts[0].getData().getData(), true);
			for (int i = 1; i < 4; i++) {
				loc.add(0, 1, 0);
				Location[] locs = { loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1) };
				for (Location location : locs) {
					location.getBlock().setTypeIdAndData(parts[i].getTypeId(), parts[i].getData().getData(), true);
				}
			}
			loc.add(0, 1, 0).getBlock().setTypeIdAndData(parts[4].getTypeId(), parts[4].getData().getData(), true);
		} else {
			loc.add(0, -1, 0);
			Location[] downs = { loc, loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1), loc.clone().add(1, 0, 1), loc.clone().add(-1, 0, 1), loc.clone().add(1, 0, -1),
					loc.clone().add(-1, 0, -1) };
			for (Location down : downs) {
				down.getBlock().setTypeIdAndData(parts[0].getTypeId(), parts[0].getData().getData(), true);
			}
			for (int i = 1; i < 4; i++) {
				loc.add(0, 1, 0);
				Location[] uppers = { loc.clone().add(2, 0, 0), loc.clone().add(-2, 0, 0), loc.clone().add(0, 0, 2), loc.clone().add(0, 0, -2), loc.clone().add(2, 0, 1), loc.clone().add(2, 0, -1), loc.clone().add(-2, 0, 1),
						loc.clone().add(-2, 0, -1), loc.clone().add(1, 0, 2), loc.clone().add(-1, 0, -2), loc.clone().add(1, 0, -2), loc.clone().add(-1, 0, 2) };
				for (Location upper : uppers) {
					upper.getBlock().setTypeIdAndData(parts[i].getTypeId(), parts[i].getData().getData(), true);
				}
			}
			loc.add(0, 1, 0);
			downs = new Location[] { loc, loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1), loc.clone().add(1, 0, 1), loc.clone().add(-1, 0, 1), loc.clone().add(1, 0, -1),
					loc.clone().add(-1, 0, -1) };
			for (Location down : downs) {
				down.getBlock().setTypeIdAndData(parts[4].getTypeId(), parts[4].getData().getData(), true);
			}
		}
	}

	public boolean has(Player p) {
		if (found == Found.RANK) {
			return !Rank.getRank(p).getPerm().equals("none");
		}

		return PlayerData.get(p).getInventory().getCages().contains(id);
	}

	public static void remove(Location loc, boolean team) {
		Location loc2 = loc;
		loc = new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ());
		if (!team) {
			loc.add(0, -1, 0).getBlock().setType(Material.AIR);
			for (int i = 1; i < 4; i++) {
				loc.add(0, 1, 0);
				Location[] locs = { loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1) };
				for (Location location : locs) {
					location.getBlock().setType(Material.AIR);
				}
			}
			loc.add(0, 1, 0).getBlock().setType(Material.AIR);
		} else {
			loc.add(0, -1, 0);
			Location[] downs = { loc, loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1), loc.clone().add(1, 0, 1), loc.clone().add(-1, 0, 1), loc.clone().add(1, 0, -1),
					loc.clone().add(-1, 0, -1) };
			for (Location down : downs) {
				down.getBlock().setType(Material.AIR);
			}
			for (int i = 1; i < 4; i++) {
				loc.add(0, 1, 0);
				Location[] uppers = { loc.clone().add(2, 0, 0), loc.clone().add(-2, 0, 0), loc.clone().add(0, 0, 2), loc.clone().add(0, 0, -2), loc.clone().add(2, 0, 1), loc.clone().add(2, 0, -1), loc.clone().add(-2, 0, 1),
						loc.clone().add(-2, 0, -1), loc.clone().add(1, 0, 2), loc.clone().add(-1, 0, -2), loc.clone().add(1, 0, -2), loc.clone().add(-1, 0, 2) };
				for (Location upper : uppers) {
					upper.getBlock().setType(Material.AIR);
				}
			}
			loc.add(0, 1, 0);
			downs = new Location[] { loc, loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1), loc.clone().add(1, 0, 1), loc.clone().add(-1, 0, 1), loc.clone().add(1, 0, -1),
					loc.clone().add(-1, 0, -1) };
			for (Location down : downs) {
				down.getBlock().setType(Material.AIR);
			}
		}
	}

	private static Map<String, Cage> cages = new LinkedHashMap<>();

	public static void register() {
		ConfigurationSection sec = Utils.getCages().getSection("cages");
		for (String key : sec.getKeys(false)) {
			try {
				String name = sec.getString(key + ".name");
				String icon = sec.getString(key + ".icon");
				String perm = sec.getString(key + ".perm");
				int price = sec.getInt(key + ".price");
				Rarity r = Rarity.valueOf(sec.getString(key + ".rarity") != null ? sec.getString(key + ".rarity").toUpperCase() : "COMMON");
				Found f = Found.valueOf(sec.getString(key + ".found") != null ? sec.getString(key + ".found").toUpperCase() : "ALL");

				ItemStack item = ItemUtils.createItem(icon);
				List<ItemStack> stacks = new ArrayList<>();
				stacks.add(ItemUtils.createItem(sec.getString(key + ".floor")));
				stacks.add(ItemUtils.createItem(sec.getString(key + ".lower-middle")));
				stacks.add(ItemUtils.createItem(sec.getString(key + ".middle")));
				stacks.add(ItemUtils.createItem(sec.getString(key + ".higher-middle")));
				stacks.add(ItemUtils.createItem(sec.getString(key + ".roof")));
				new Cage(item, name, r, f, stacks.toArray(new ItemStack[stacks.size()]), price);
			} catch (Exception e) {
				continue;
			}
		}
		if (cages.isEmpty())
			new Cage(ItemUtils.createItem("GLASS : 1 : name=&aNormal"), "Normal", Rarity.COMMON, Found.ALL,
					new ItemStack[] { new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS) }, 0);
		Logger.info("Loaded " + cages.size() + " cage(s)!");
	}

	public static Cage getCage(String name) {
		return cages.get(name.toLowerCase());
	}

	public static List<Cage> getCages() {
		return new ArrayList<>(cages.values());
	}
}
