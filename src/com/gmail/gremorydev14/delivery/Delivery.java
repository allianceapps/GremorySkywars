package com.gmail.gremorydev14.delivery;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

public class Delivery {

	private int slot, id;
	private String item;
	private String permission;
	private DeliveryType type;
	private List<String> commands;
	private DeliveryTime time;

	public Delivery(Integer slot, String permission, String item, DeliveryType type, List<String> commands, DeliveryTime time) {
		this.slot = slot;
		this.item = item;
		this.type = type;
		this.permission = permission;
		this.commands = commands;
		this.time = time;
		this.id = deliveries.size() + 1;
		deliveries.put(deliveries.size() + 1, this);
	}
	
	public int getId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}
	
	public String getItemString(boolean claim, boolean perm) {
		return item.replace("%material%", claim ? "STORAGE_MINECART" : "MINECART").replace("%color%", claim ? "&a" : "&c").replace("%final%", perm ? DeliveryMessages.claim_perm : DeliveryMessages.claim_not_perm).replace("%month%", DeliveryMessages.months.get(Calendar.getInstance().get(Calendar.MONTH) + 1));
	}

	public ItemStack getItem(boolean claim, boolean perm) {
		return ItemUtils.createItem(item.replace("%material%", claim ? "STORAGE_MINECART" : "MINECART").replace("%color%", claim ? "&a" : "&c").replace("%final%", perm ? DeliveryMessages.claim_perm : DeliveryMessages.claim_not_perm).replace("%month%", DeliveryMessages.months.get(Calendar.getInstance().get(Calendar.MONTH) + 1)));
	}

	public DeliveryType getType() {
		return type;
	}
	
	public boolean has(Player p) {
		return type == DeliveryType.FREE || p.hasPermission(permission);
	}

	public List<String> getCommands() {
		return commands;
	}

	public DeliveryTime getTime() {
		return time;
	}
	
	private static Map<Integer, Delivery> deliveries = new HashMap<>();

	public static void register() {
		SettingsManager delivery = SettingsManager.getConfig("delivery");
		if (!delivery.contains("rewards")) {
			delivery.createSection("rewards");
		}
		
		for (String key : delivery.getSection("rewards").getKeys(false)) {
			int days = delivery.getInt("rewards." + key + ".days");
			int slot = delivery.getInt("rewards." + key + ".slot");
			String typeString = delivery.getString("rewards." + key + ".type");
			String permission = delivery.getString("rewards." + key + ".perm");
			List<String> commands = delivery.getStringList("rewards." + key + ".commands");
			String icon = delivery.getString("rewards." + key + ".icon");
			
			new Delivery(slot, permission, icon, DeliveryType.getType(typeString), commands, new DeliveryTime(days));
		}
		Bukkit.getLogger().info("Loaded " + deliveries.size() + " Delivery(ies)!");
	}
	
	public static Map<Integer, Delivery> getDeliveries() {
		return deliveries;
	}

	public static enum DeliveryType {
		PERMISSION, FREE;
		
		public static DeliveryType getType(String name) {
			if (name.equalsIgnoreCase("permission")) {
				return PERMISSION;
			} else {
				return FREE;
			}
		}
	}

	public static class DeliveryTime {

		private int days;

		public DeliveryTime(int days) {
			this.days = days;
		}

		public int getDays() {
			return days;
		}
	}

	public static class DeliveryMessages {

		public static String claim_perm = "";
		public static String claim_not_perm = "";
		public static String claim_lore = "";
		public static String next_delivery = "";
		public static Map<Integer, String> months = new HashMap<>();

		public static void register() {
			SettingsManager sm = SettingsManager.getConfig("delivery");
			claim_perm = sm.getString("claim.perm").replace("&", "§");
			claim_not_perm = sm.getString("claim.no-perm").replace("&", "§");
			claim_lore = sm.getString("claim.lore").replace("&", "§");
			next_delivery = sm.getString("next").replace("&", "§");
			
			for (String key : sm.getSection("months").getKeys(false)) {
				int month = Integer.parseInt(key);
				months.put(month, sm.getString("months." + key));
			}
		}
	}
}
