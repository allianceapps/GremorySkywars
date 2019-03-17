package com.gmail.gremorydev14.gremoryskywars.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.delivery.DeliveryMan;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaNPC;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.InventoryUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

public class CitizensAPI {

	@Getter
	private static Map<String, ArenaNPC> npcs = new HashMap<>();

	public CitizensAPI() {
		new BukkitRunnable() {

			@Override
			public void run() {
				net.citizensnpcs.api.CitizensAPI.createNamedNPCRegistry("SkyWars", new NPCDataStore() {

					private int ID = 0;

					@Override
					public void storeAll(NPCRegistry arg0) {
					}

					@Override
					public void store(NPC arg0) {
					}

					@Override
					public void saveToDiskImmediate() {
					}

					@Override
					public void saveToDisk() {
					}

					@Override
					public void loadInto(NPCRegistry arg0) {
					}

					@Override
					public int createUniqueNPCId(NPCRegistry arg0) {
						return ID++;
					}

					@Override
					public void clearData(NPC arg0) {
					}
				});
				setupNPCS();
				setupDelivery();
				Bukkit.getPluginManager().registerEvents(new Listener() {
					@EventHandler
					private void onNPCClick(PlayerInteractEntityEvent e) {
						Player p = e.getPlayer();
						if (net.citizensnpcs.api.CitizensAPI.getNamedNPCRegistry("SkyWars").isNPC(e.getRightClicked())) {
							ArenaNPC arenaNPC = e.getRightClicked().hasMetadata("ARENA_NPC") ? (ArenaNPC) e.getRightClicked().getMetadata("ARENA_NPC").get(0).value() : null;
							if (arenaNPC != null) {
								if (Options.getMode() == ServerMode.LOBBY) {
									String server = Arena.getBungee(arenaNPC.getMode(), arenaNPC.getType());
									if (server != null) {
										PlayerData.get(p).saveAsync();
										Utils.sendToServer(p, server);
									} else {
										p.sendMessage(Language.messages$player$play_again_null);
									}
								} else {
									Arena arena = ArenaSign.switchFrom(arenaNPC.getMode(), arenaNPC.getType());
									if (arena != null) {
										arena.add(p);
									} else {
										p.sendMessage(Language.messages$player$play_again_null);
									}
								}
							}
							DeliveryMan dman = e.getRightClicked().hasMetadata("DELIVERY_MAN") ? (DeliveryMan) e.getRightClicked().getMetadata("DELIVERY_MAN").get(0).value() : null;
							if (dman != null) {
								e.setCancelled(true);
								InventoryUtils.deliveryMan(p, dman);
							}
						}
					}
				}, Main.getPlugin());
			}
		}.runTaskLater(Main.getPlugin(), 60);
	}

	public static void setupNPCS() {
		SettingsManager sm = Utils.getLocations();
		if (!sm.contains("npcs")) {
			sm.set("npcs", new ArrayList<String>());
		}

		for (String serialized : sm.getStringList("npcs")) {
			String[] split = serialized.split(" : ");
			Location location = Utils.unserializeLocation(serialized);
			Mode mode = Mode.get(split[6]);
			Type type = Type.get(split[7]);
			String id = split[8];
			new ArenaNPC(id, mode, type, location);
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				for (ArenaNPC npc : npcs.values()) {
					npc.update();
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 20);
	}
	
	public static void setupDelivery() {
		SettingsManager sm = Utils.getLocations();
		if (!sm.contains("dmans")) {
			sm.set("dmans", new ArrayList<String>());
		}
		
		for (String serialized : sm.getStringList("dmans")) {
			String[] split = serialized.split(" : ");
			Location location = Utils.unserializeLocation(serialized);
			String id = split[6];
			new DeliveryMan(id, location);
		}
	}

	public static void add(String id, Mode mode, Type type, Location location) {
		new ArenaNPC(id, mode, type, location);
		SettingsManager sm = Utils.getLocations();
		List<String> npcs = sm.getStringList("npcs");
		npcs.add(Utils.serializeLocation(location) + " : " + mode.getName() + " : " + type.getName() + " : " + id);
		sm.set("npcs", npcs);
	}

	public static void remove(ArenaNPC n) {
		npcs.remove(n.getId());
		SettingsManager sm = Utils.getLocations();
		List<String> npcs = sm.getStringList("npcs");
		npcs.remove(Utils.serializeLocation(n.getNPCLocation()) + " : " + n.getMode().getName() + " : " + n.getType().getName() + " : " + n.getId());
		sm.set("npcs", npcs);
		n.destroy();
	}

	public static ArenaNPC get(String id) {
		for (ArenaNPC n : npcs.values()) {
			if (n.getId().equals(id)) {
				return n;
			}
		}

		return npcs.get(id);
	}
}
