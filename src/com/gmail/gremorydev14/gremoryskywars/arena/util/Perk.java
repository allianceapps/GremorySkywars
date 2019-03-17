package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Found;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Rarity;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.mystery.SoulWell.Reward;
import com.gmail.gremorydev14.profile.Rank;

import lombok.Getter;

@SuppressWarnings("all")
public class Perk implements Reward {

	@Getter
	private int price, id;
	@Getter
	private ItemStack icon, locked, unlocked;
	@Getter
	private String name;
	@Getter
	private Rarity rarity;
	@Getter
	private Found found;
	@Getter
	private String permission;
	@Getter
	private Mode mode;
	@Getter
	private List<Integer> ids = new ArrayList<>();

	public Perk(ItemStack icon, String name, String permission, Rarity r, Found f, Mode mode, List<Integer> ids, int price) {
		this.name = name;
		this.rarity = r;
		this.found = f;
		this.ids = ids;
		this.mode = mode;
		this.permission = permission;
		this.price = price;

		ItemMeta meta = icon.getItemMeta();
		meta.setDisplayName(meta.getDisplayName() + " §7[" + mode.toString() + "]");
		icon.setItemMeta(meta);

		ItemStack icon1 = icon.clone();
		ItemMeta meta1 = icon1.getItemMeta();
		List<String> lore1 = meta1.hasLore() ? meta1.getLore() : new ArrayList<>();
		lore1.add("");
		lore1.add("§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase());
		lore1.add("§eClick to select!");
		meta1.setLore(lore1);
		icon1.setItemMeta(meta1);
		this.icon = icon1;
		ItemStack icon2 = getIcon().clone();
		ItemMeta meta2 = icon2.getItemMeta();
		List<String> lore2 = meta2.getLore();
		lore2.remove(lore2.size() - 1);
		lore2.add(this.found.getDesc());
		meta2.setLore(lore2);
		icon2.setItemMeta(meta2);
		this.unlocked = icon2;
		ItemStack icon3 = getIcon().clone();
		ItemMeta meta3 = icon3.getItemMeta();
		List<String> lore3 = meta3.getLore();
		lore3.remove(lore3.size() - 1);
		lore3.add("§eAlready have!");
		meta3.setLore(lore3);
		icon3.setItemMeta(meta3);
		this.locked = icon3;
		if (mode == Mode.SOLO) {
			perks.put(name.toLowerCase(), this);
			this.id = perks.size();
		} else if (mode == Mode.TEAM) {
			perks2.put(name.toLowerCase(), this);
			this.id = perks2.size();
		} else {
			perks3.put(name.toLowerCase(), this);
			this.id = perks3.size();
		}
	}

	public boolean has(Player p) {
		if (found == Found.RANK) {
			return !Rank.getRank(p).getPerm().equals("none");
		}
		
		if (found == Found.PERMISSION) {
			return p.hasPermission(permission);
		}

		if (found == Found.ALL && permission != null && !permission.equals("") && p.hasPermission(permission)) {
			return true;
		}

		if (mode == Mode.SOLO) {
			return PlayerData.get(p).getInventory().getSPerks().contains(id);
		} else if (mode == Mode.TEAM) {
			return PlayerData.get(p).getInventory().getTPerks().contains(id);
		}
		return PlayerData.get(p).getInventory().getMPerks().contains(id);
	}

	private static Map<String, Perk> perks = new LinkedHashMap<>();
	private static Map<String, Perk> perks2 = new LinkedHashMap<>();
	private static Map<String, Perk> perks3 = new LinkedHashMap<>();

	public static void register() {
		ConfigurationSection sec = Utils.getPerks().getSection("perks");
		for (String key : sec.getKeys(false)) {
			try {
				String name = sec.getString(key + ".name");
				String icon = sec.getString(key + ".icon");
				String permission = sec.getString(key + ".permission");
				int price = sec.getInt(key + ".price");
				List<Integer> ids = sec.getIntegerList(key + ".abilities_id");
				Mode mode = Mode.valueOf(sec.get(key + ".mode") != null ? sec.getString(key + ".mode").toUpperCase() : "SOLO");
				Rarity r = Rarity.valueOf(sec.getString(key + ".rarity") != null ? sec.getString(key + ".rarity").toUpperCase() : "COMMON");
				Found f = Found.valueOf(sec.getString(key + ".found") != null ? sec.getString(key + ".found").toUpperCase() : "ALL");

				ItemStack item = ItemUtils.createItem(icon);

				new Perk(item, name, permission, r, f, mode, ids, price);
			} catch (Exception e) {
				continue;
			}
		}
		Logger.info("Loaded " + perks.size() + " solo perk(s)!");
		Logger.info("Loaded " + perks2.size() + " team perk(s)!");
		Logger.info("Loaded " + perks3.size() + " mega perk(s)!");
	}

	public static Perk getPerk(String name, Mode mode) {
		if (mode == Mode.MEGA)
			return perks3.get(name.toLowerCase());
		else if (mode == Mode.TEAM)
			return perks2.get(name.toLowerCase());
		return perks.get(name.toLowerCase());
	}

	public static List<Perk> getPerks() {
		return new ArrayList<>(perks.values());
	}

	public static List<Perk> getPerks2() {
		return new ArrayList<>(perks2.values());
	}

	public static List<Perk> getPerks3() {
		return new ArrayList<>(perks3.values());
	}
}
