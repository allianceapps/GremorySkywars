package com.gmail.gremorydev14.mystery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Cage;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Found;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

@SuppressWarnings("all")
public class SoulWell {

	private String id;
	private Location location;

	private boolean opening;

	public SoulWell(String id, Location location) {
		this.id = id;
		this.location = location;
		location.getBlock().setType(Material.ENDER_PORTAL_FRAME);
		wells.put(id, this);
	}
	
	private static Bag small = new Bag(150), normal = new Bag(400), big = new Bag(1000);

	public void open(PlayerData pd) {
		Player p = pd.getPlayer();

		if (opening) {
			//p.sendMessage(Language.messages$soulwell$already_in_use);
			//return;
		}

		if (pd.getSouls() < 10) {
			p.sendMessage(Language.messages$soulwell$no_have_enough_souls);
			return;
		}

		opening = true;
		pd.removeSouls(10);
		Inventory inv = Bukkit.createInventory(null, 45, "Almas");
		for (int i = 0; i < 45; i++) {
			inv.setItem(i, ItemUtils.createItem("STAINED_GLASS_PANE:14 : 1 : name=&8-"));
		}
		ItemStack openingGlass = ItemUtils.createItem("STAINED_GLASS:15 : 1 : name=&8-");
		inv.setItem(21, openingGlass);
		inv.setItem(23, openingGlass);
		List<Reward> items = new ArrayList<>();
		int randomSmall = new Random().nextInt(15) + 1;
		int randomNormal = new Random().nextInt(10) + 1;
		int randomBig = new Random().nextInt(10) + 1;
		Map<ItemStack, Reward> itemMap = new HashMap<>();
		itemMap.put(small.getIcon(), small);
		itemMap.put(normal.getIcon(), normal);
		itemMap.put(big.getIcon(), big);
		for (int i = 0; i < randomSmall; i++) {
			items.add(small);
		}
		for (int i = 0; i < randomNormal; i++) {
			items.add(normal);
		}
		for (int i = 0; i < randomBig; i++) {
			items.add(big);
		}
		for (Kit kit : Kit.getKits()) {
			if (kit.getFound() == Found.SOUL_WELL && !kit.has(p)) {
				items.add(kit);
				itemMap.put(kit.getIcon(), kit);
			}
		}
		for (Kit kit : Kit.getKits2()) {
			if (kit.getFound() == Found.SOUL_WELL && !kit.has(p)) {
				items.add(kit);
				itemMap.put(kit.getIcon(), kit);
			}
		}
		for (Kit kit : Kit.getKits3()) {
			if (kit.getFound() == Found.SOUL_WELL && !kit.has(p)) {
				items.add(kit);
				itemMap.put(kit.getIcon(), kit);
			}
		}
		for (Perk perk : Perk.getPerks()) {
			if (perk.getFound() == Found.SOUL_WELL && !perk.has(p)) {
				items.add(perk);
				itemMap.put(perk.getIcon(), perk);
			}
		}
		for (Perk perk : Perk.getPerks2()) {
			if (perk.getFound() == Found.SOUL_WELL && !perk.has(p)) {
				items.add(perk);
				itemMap.put(perk.getIcon(), perk);
			}
		}
		for (Perk perk : Perk.getPerks3()) {
			if (perk.getFound() == Found.SOUL_WELL && !perk.has(p)) {
				items.add(perk);
				itemMap.put(perk.getIcon(), perk);
			}
		}
		for (Cage cage : Cage.getCages()) {
			if (cage.getFound() == Found.SOUL_WELL && !cage.has(p)) {
				items.add(cage);
				itemMap.put(cage.getIcon(), cage);
			}
		}
		if (items.size() == 0) {
			p.sendMessage(Language.messages$soulwell$already_have_all_items);
			return;
		}
		
		Collections.shuffle(items);
		p.openInventory(inv);
		new BukkitRunnable() {
			int size = 0;
			Reward old = null;

			@Override
			public void run() {
				if (!p.isOnline()) {
					cancel();
					itemMap.clear();
					opening = false;
					return;
				}

				if (!inv.getViewers().contains(p)) {
					p.openInventory(inv);
				}
				if (size < 90) {
					for (int i = 0; i < inv.getSize(); i++) {
						if (slots.contains(i) || i == 21 || i == 23) {
							continue;
						} else {
							inv.getItem(i).setDurability((short) (new Random().nextInt(6) + 1));
						}
					}
					for (int i = slots.size() - 1; i > 0; i--) {
						inv.setItem(slots.get(i), inv.getItem(slots.get(i - 1)));
					}
					old = items.get(new Random().nextInt(items.size()));
					inv.setItem(slots.get(0), old.getIcon());
					if (Reflection.getVersion().startsWith("v1_8")) {
						p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0f, 1.0f);
					}
				}

				if (size == 110) {
					cancel();
					if (itemMap.get(inv.getItem(22)) != null) {
						old = itemMap.get(inv.getItem(22));
					}
					
					itemMap.clear();
					p.closeInventory();
					opening = false;
					if (old instanceof Kit) {
						pd.getInventory().addKit((Kit)old, ((Kit)old).getMode());
						p.sendMessage(Language.messages$soulwell$won_kit.replace("%kit%", old.getName()));
					} else if (old instanceof Cage) {
						pd.getInventory().addCage((Cage)old);
						p.sendMessage(Language.messages$soulwell$won_cage.replace("%cage%", old.getName()));
					} else if (old instanceof Perk) {
						pd.getInventory().addPerk((Perk)old, ((Perk)old).getMode());
						p.sendMessage(Language.messages$soulwell$won_perk.replace("%perk%", old.getName()));
					} else if (old instanceof Bag) {
						pd.addCoins(Integer.parseInt(old.getName()));
						p.sendMessage(Language.messages$soulwell$won_bag.replace("%bag%", old.getIcon().getItemMeta().getDisplayName()).replace("%coins%", old.getName()));
					}
					return;
				}
				++size;
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2);
	}

	public String getId() {
		return id;
	}

	public Location getBlockLocation() {
		return location;
	}

	private static Map<String, SoulWell> wells = new HashMap<>();
	private static final List<Integer> slots = Arrays.asList(4, 13, 22, 31, 40);

	public static void add(String id, Location location) {
		wells.put(id, new SoulWell(id, location));
		SettingsManager sm = Utils.getLocations();
		List<String> souls = sm.getStringList("soulwell");
		souls.add(Utils.serializeLocation(location) + " : " + id);
		sm.set("soulwell", souls);
	}
	
	public static void remove(SoulWell sw) {
		wells.remove(sw.getId());
		SettingsManager sm = Utils.getLocations();
		List<String> souls = sm.getStringList("soulwell");
		for (String string : new ArrayList<>(souls)) {
			if (string.equals(Utils.serializeLocation(sw.getBlockLocation()) + " : " + sw.getId())) {
				souls.remove(string);
			}
		}
		sm.set("soulwell", souls);
	}
	
	public static void register() {
		SettingsManager sm = Utils.getLocations();
		if (!sm.contains("soulwell")) {
			sm.set("soulwell", new ArrayList<>());
		}
		for (String soul : sm.getStringList("soulwell")) {
			if (soul.split(" : ").length > 5) {
				new SoulWell(soul.split(" : ")[6], Utils.unserializeLocation(soul));
			}
		}
		Logger.info("Loaded " + wells.size() + " soul well(s)!");
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				for (SoulWell sw : wells()) {
					HolographicDisplaysAPI.createHologram(Main.getPlugin(), sw.getBlockLocation().clone().add(0, 1.8, 0), Language.messages$soulwell$title, Language.messages$soulwell$subtitle);
				}
			}
		}, 80);
	}

	public static SoulWell get(String id) {
		return wells.get(id);
	}

	public static SoulWell get(Location location) {
		for (SoulWell sw : wells()) {
			if (sw.getBlockLocation().equals(location)) {
				return sw;
			}
		}
		return null;
	}

	public static Collection<SoulWell> wells() {
		return wells.values();
	}
	
	public static interface Reward {
		public ItemStack getIcon();
		public String getName();
	}
	
	public static class Bag implements Reward {
		
		private int coins;
		
		public Bag(int coins) {
			this.coins = coins;
		}

		@Override
		public ItemStack getIcon() {
			return ItemUtils.createItem(coins == 150 ? "GOLD_NUGGET : 1 : name=&7Bolsinha de Dinheiro" : coins == 400 ? "GOLD_INGOT : 1 : name=&aBolsa de Dinheiro" : "GOLD_BLOCK : 1 : name=&6Grande Bolsa de Dinheiro");
		}

		@Override
		public String getName() {
			return String.valueOf(coins);
		}
	}
}
