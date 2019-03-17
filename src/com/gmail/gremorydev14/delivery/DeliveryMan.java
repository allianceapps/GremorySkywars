package com.gmail.gremorydev14.delivery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import net.citizensnpcs.api.npc.NPC;

@SuppressWarnings("all")
public class DeliveryMan {

	private Location location;
	private NPC npc;
	private String id;
	private Hologram hologram;

	private int countdown = 0;

	public DeliveryMan(String id, Location location) {
		this.id = id;
		this.location = location;

		this.npc = net.citizensnpcs.api.CitizensAPI.getNamedNPCRegistry("SkyWars").createNPC(EntityType.PLAYER, "");
		npc.setProtected(true);
		npc.spawn(location);
		npc.getEntity().setMetadata("DELIVERY_MAN", new FixedMetadataValue(Main.getPlugin(), this));
		new BukkitRunnable() {

			@Override
			public void run() {
				hologram = HolographicDisplaysAPI.createHologram(Main.getPlugin(), location.clone().add(0, 2.5, 0), "§fEntregador", "§a§lCLIQUE DIREITO");
			}
		}.runTaskLater(HolographicDisplays.getInstance(), 40);
		npcs.put(id, this);
	}

	public void destroy() {
		hologram.delete();
		npc.destroy();
	}
	
	public Location getNPCLocation() {
		return location;
	}
	
	public String getId() {
		return id;
	}

	public void update() {
		/*if (hologram != null) {
			hologram.removeLine(3);
			hologram.addLine((countdown == 0 ? "§6" : countdown == 1 ? "§c" : "§f") + "%deliveries% Deliveries!");
		}
		countdown++;
		if (countdown > 2) {
			countdown = 0;
		}*/
	}

	private static Map<String, DeliveryMan> npcs = new HashMap<>();
	
	public static void add(String id, Location location) {
		new DeliveryMan(id, location);
		SettingsManager sm = Utils.getLocations();
		List<String> npcs = sm.getStringList("dmans");
		npcs.add(Utils.serializeLocation(location) + " : " + id);
		sm.set("dmans", npcs);
	}

	public static void remove(DeliveryMan n) {
		npcs.remove(n.getId());
		SettingsManager sm = Utils.getLocations();
		List<String> npcs = sm.getStringList("dmans");
		npcs.remove(Utils.serializeLocation(n.getNPCLocation()) + " : " + n.getId());
		sm.set("dmans", npcs);
		n.destroy();
	}

	public static DeliveryMan get(String id) {
		for (DeliveryMan n : npcs.values()) {
			if (n.getId().equals(id)) {
				return n;
			}
		}

		return npcs.get(id);
	}

	public static Map<String, DeliveryMan> getDeliveries() {
		return npcs;
	}
}
