package com.gmail.gremorydev14.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLite;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

public class SwDelivery {

	private Player player;
	private SQLDatabase storage;
	private Map<Integer, Long> countdowns = new HashMap<>();

	public SwDelivery(Player p) {
		this.player = p;
		this.storage = new SQLDatabase(p);
		String[] deliveries = storage.getString("deliveries").split(" : ");
		for (Entry<Integer, Delivery> entry : Delivery.getDeliveries().entrySet()) {
			if (deliveries.length >= entry.getKey()) {
				countdowns.put(entry.getKey(), Long.valueOf(deliveries[entry.getKey() - 1]));
			} else {
				countdowns.put(entry.getKey(), 0l);
			}
		}
	}
	
	public void set(Integer delivery, long newValue) {
		countdowns.put(delivery, newValue);
	}
	
	public void save() {
		List<String> list = new ArrayList<>();
		for (Entry<Integer, Long> entry : countdowns.entrySet()) {
			list.add(entry.getKey() + " " + entry.getValue());
		}
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return Integer.compare(Integer.parseInt(arg0.split(" ")[0]), Integer.parseInt(arg1.split(" ")[0]));
			}
		});
		List<String> newList = new ArrayList<>();
		for (String string : list) {
			newList.add(string.split(" ")[1]);
		}
		Utils.getStorage().execute("UPDATE g_delivery SET deliveries = ? WHERE uuid = ?", false, StringUtils.join(newList, " : "), player.getUniqueId().toString());
	}

	public void saveAsync() {
		List<String> list = new ArrayList<>();
		for (Entry<Integer, Long> entry : countdowns.entrySet()) {
			list.add(entry.getKey() + " " + entry.getValue());
		}
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return Integer.compare(Integer.parseInt(arg0.split(" ")[0]), Integer.parseInt(arg1.split(" ")[0]));
			}
		});
		List<String> newList = new ArrayList<>();
		for (String string : list) {
			newList.add(string.split(" ")[1]);
		}
		Utils.getStorage().execute("UPDATE g_delivery SET deliveries = ? WHERE uuid = ?", (Utils.getStorage() instanceof SQLite), StringUtils.join(newList, " : "), player.getUniqueId().toString());
	}
	
	public Player getPlayer() {
		return player;
	}

	public Map<Integer, Long> getCountdowns() {
		return countdowns;
	}
	
	private static Map<UUID, SwDelivery> deliveries = new HashMap<>();

	public static void create(Player p) {
		deliveries.put(p.getUniqueId(), new SwDelivery(p));
	}

	public static void delete(Player p) {
		if (get(p) != null) {
			deliveries.remove(p.getUniqueId());
		}
	}

	public static SwDelivery get(Player p) {
		return deliveries.get(p.getUniqueId());
	}

	public static Collection<SwDelivery> values() {
		return deliveries.values();
	}
}
