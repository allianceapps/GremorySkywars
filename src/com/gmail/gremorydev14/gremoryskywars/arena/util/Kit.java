package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
public class Kit implements Reward {

	@Getter
	private Mode mode;
	@Getter
	private int price, id;
	@Getter
	private ItemStack icon, unlocked, shop, locked;
	@Getter
	private ItemStack[] armor;
	@Getter
	private String name;
	@Getter
	private String permission;
	@Getter
	private Rarity rarity;
	@Getter
	private Found found;
	@Getter
	private List<ItemStack> items;
	private List<PotionEffect> effects;

	public Kit(ItemStack icon, String name, String permission, Mode mode, Rarity r, Found f, List<PotionEffect> ef, List<ItemStack> items, ItemStack[] armor, int price) {
		this.name = name;
		this.mode = mode;
		this.rarity = r;
		this.found = f;
		this.permission = permission;
		this.items = items;
		this.armor = armor;
		this.price = price;
		this.effects = ef;
		// CLONE PRIMARY
		ItemStack icon2 = icon.clone();
		ItemMeta meta2 = icon2.getItemMeta();
		meta2.setDisplayName(icon.getItemMeta().getDisplayName() + " §7[" + mode.toString() + "]");
		List<String> all = meta2.hasLore() ? meta2.getLore() : new ArrayList<>();
		all.addAll(Arrays.asList("", "§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase(), "§eClick to select!"));
		meta2.setLore(all);
		icon2.setItemMeta(meta2);
		this.icon = icon2;
		// CLONE UNLOCKED
		ItemStack icon3 = icon.clone();
		icon3.setType(Material.STAINED_GLASS_PANE);
		icon3.setDurability((short) 14);
		ItemMeta meta3 = icon3.getItemMeta();
		meta3.setDisplayName(icon.getItemMeta().getDisplayName() + " §7[" + mode.toString() + "]");
		all = meta3.hasLore() ? meta3.getLore() : new ArrayList<>();
		all.addAll(Arrays.asList("", "§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase(), "§cBlocked!"));
		meta3.setLore(all);
		icon3.setItemMeta(meta3);
		this.unlocked = icon3;
		// CLONE LOCKED
		ItemStack icon4 = icon.clone();
		ItemMeta meta4 = icon4.getItemMeta();
		meta4.setDisplayName(icon.getItemMeta().getDisplayName() + " §7[" + mode.toString() + "]");
		all = meta4.hasLore() ? meta4.getLore() : new ArrayList<>();
		all.addAll(Arrays.asList("", "§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase(), "§eAlready have!"));
		meta4.setLore(all);
		icon4.setItemMeta(meta4);
		this.locked = icon4;
		// CLONE SHOP
		ItemStack iconClone = icon.clone();
		ItemMeta meta = iconClone.getItemMeta();
		meta.setDisplayName(icon.getItemMeta().getDisplayName() + " §7[" + mode.toString() + "]");
		all = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		all.addAll(Arrays.asList("", "§7Rarity: " + rarity.getColor() + rarity.getName().toUpperCase(), "§7Price: §6" + price, ""));
		if (!f.getDesc().equals("")) {
			all.add(f.getDesc());
			all.add("");
		}
		all.add("§eClick to buy!");
		meta.setLore(all);
		iconClone.setItemMeta(meta);
		this.shop = iconClone;

		if (mode == Mode.SOLO) {
			kits.put(name.toLowerCase(), this);
			this.id = kits.size();
		} else if (mode == Mode.TEAM) {
			kits2.put(name.toLowerCase(), this);
			this.id = kits2.size();
		} else {
			kits3.put(name.toLowerCase(), this);
			this.id = kits3.size();
		}
	}

	public void apply(Player p) {
		p.getInventory().setArmorContents(armor);
		if (items.size() > 0) {
			for (ItemStack item : items) {
				p.getInventory().addItem(item);
			}
		}
		for (PotionEffect ef : effects) {
			p.addPotionEffect(ef);
		}
		p.updateInventory();
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
			return PlayerData.get(p).getInventory().getSKits().contains(id);
		} else if (mode == Mode.TEAM) {
			return PlayerData.get(p).getInventory().getTKits().contains(id);
		}
		return PlayerData.get(p).getInventory().getMKits().contains(id);
	}

	private static Map<String, Kit> kits = new LinkedHashMap<>(), kits2 = new LinkedHashMap<>(), kits3 = new LinkedHashMap<>();

	public static void register() {
		ConfigurationSection sec = Utils.getKits().getSection("kits");
		for (String key : sec.getKeys(false)) {
			try {
				String name = sec.getString(key + ".name");
				Mode mode = Mode.valueOf(sec.get(key + ".mode") != null ? sec.getString(key + ".mode").toUpperCase() : "SOLO");
				Rarity r = Rarity.valueOf(sec.getString(key + ".rarity") != null ? sec.getString(key + ".rarity").toUpperCase() : "COMMON");
				Found f = Found.valueOf(sec.getString(key + ".found") != null ? sec.getString(key + ".found").toUpperCase() : "ALL");
				String icon = sec.getString(key + ".icon");
				String permission = sec.getString(key + ".permission");
				int price = sec.getInt(key + ".price");
				List<String> effe = sec.getStringList(key + ".effects");
				String[] armors = new String[] { sec.getString(key + ".helmet"), sec.getString(key + ".chestplate"), sec.getString(key + ".leggings"), sec.getString(key + ".boots") };
				List<String> items = sec.getStringList(key + ".items");

				ItemStack[] armored = new ItemStack[4];
				List<ItemStack> stacks = new ArrayList<>();
				List<PotionEffect> ef = new ArrayList<>();
				for (String string : effe) {
					try {
						ef.add(new PotionEffect(PotionEffectType.getByName(string.split(" : ")[0]), Integer.parseInt(string.split(" : ")[2]) * 20, Integer.parseInt(string.split(" : ")[1])));
					} catch (Exception e) {
					}
				}
				ItemStack iconStack = ItemUtils.createItem(icon);
				for (String item : items) {
					try {
						stacks.add(ItemUtils.createItem(item));
					} catch (Exception ignore) {
						continue;
					}
				}
				int i = 3;
				for (String armor : armors) {
					armored[i--] = ItemUtils.createItem(armor);
				}
				new Kit(iconStack, name, permission, mode, r, f, ef, stacks, armored, price);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (kits.isEmpty()) {
			new Kit(ItemUtils.createItem("WOOD_PICKAXE : 1 : name=&aDefault : lore=&7Picareta de madeira\n&7Machado de madeira\n&7Pá de madeira"), "Default", "", Mode.SOLO, Rarity.COMMON, Found.ALL, new ArrayList<>(),
					Arrays.asList(new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE), new ItemStack(Material.WOOD_SPADE)), null, 0);
		}
		if (kits2.isEmpty()) {
			new Kit(ItemUtils.createItem("WOOD_PICKAXE : 1 : name=&aDefault : lore=&7Picareta de madeira\n&7Machado de madeira\n&7Pá de madeira"), "Default", "", Mode.TEAM, Rarity.COMMON, Found.ALL, new ArrayList<>(),
					Arrays.asList(new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE), new ItemStack(Material.WOOD_SPADE)), null, 0);
		}
		if (kits3.isEmpty()) {
			new Kit(ItemUtils.createItem("WOOD_PICKAXE : 1 : name=&aDefault : lore=&7Picareta de madeira\n&7Machado de madeira\n&7Pá de madeira"), "Default", "", Mode.MEGA, Rarity.COMMON, Found.ALL, new ArrayList<>(),
					Arrays.asList(new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE), new ItemStack(Material.WOOD_SPADE)), null, 0);
		}
		Logger.info("Loaded " + kits.size() + " solo kit(s)!");
		Logger.info("Loaded " + kits2.size() + " team kit(s)!");
		Logger.info("Loaded " + kits3.size() + " mega kit(s)!");
	}

	public static Kit getKit(String name, Mode mode) {
		if (mode == Mode.MEGA) {
			return kits3.get(name.toLowerCase());
		} else if (mode == Mode.TEAM) {
			return kits2.get(name.toLowerCase());
		}
		return kits.get(name.toLowerCase());
	}

	public static Collection<Kit> getKits() {
		return kits.values();
	}

	public static Collection<Kit> getKits2() {
		return kits2.values();
	}

	public static Collection<Kit> getKits3() {
		return kits3.values();
	}
}
