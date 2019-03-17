package com.gmail.gremorydev14.leaderboards;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.Getter;

@SuppressWarnings("all")
public class LeaderBoard {

	private String id;
	private String type, e;
	private int top;
	private Hologram hologram;
	private Location armorStand;
	private ArmorStand stand;

	private static Map<Integer, List<String>> style = new HashMap<>();
	private static Map<Integer, List<String>> style2 = new HashMap<>();

	public LeaderBoard(String id, String type, int top, Location armorStand) {
		this.id = id;
		this.armorStand = armorStand;
		if (type.equalsIgnoreCase("Kills")) {
			this.type = "kSolo + kTeam + kMega";
			this.e = "kSolo,kTeam,kMega";
		}
		if (type.equalsIgnoreCase("Wins")) {
			this.type = "wSolo + wTeam + wMega";
			this.e = "wSolo,wTeam,wMega";
		}
		this.top = top;
		if (this.top > 5 || this.top < 1) {
			this.top = 5;
		}
	}

	public void destroy() {
		hologram.delete();
		stand.remove();
	}

	public void update() {
		try {
			PreparedStatement ps = Utils.getStorage().getConnection().prepareStatement("SELECT * FROM g_sw ORDER BY " + getType() + " DESC LIMIT 5");
			ResultSet rs = ps.executeQuery();
			Map<Integer, String> map = new HashMap<>();
			int i = 1;
			while (rs.next()) {
				int value = 0;
				for (String s : e.split(",")) {
					value += rs.getInt(s.replace(" ", ""));
				}
				map.put(i++, rs.getString("name") + ":" + value);
			}
			if (i < 6) {
				while (i < 6) {
					map.put(i++, "None:0");
				}
			}
			i = top;
			if (stand == null) {
				String player = map.get(i);
				// armorStand.setYaw(90.0f);
				// armorStand.setPitch(180.0f);
				stand = (ArmorStand) armorStand.getWorld().spawn(armorStand, ArmorStand.class);
				stand.setBasePlate(false);
				String armor = (i == 1 ? "DIAMOND_" : i == 2 ? "IRON_" : i == 3 ? "GOLD_" : i == 4 ? "CHAINMAIL_" : "LEATHER_");
				stand.setMetadata("SW_ARMOR", new FixedMetadataValue(Main.getPlugin(), true));
				stand.setArms(true);
				stand.setCustomNameVisible(false);
				stand.setHelmet(ItemUtils.createSkull("3 : 1 : owner=" + player.split(":")[0]));
				stand.setChestplate(ItemUtils.createItem(armor + "CHESTPLATE"));
				stand.setLeggings(ItemUtils.createItem(armor + "LEGGINGS"));
				stand.setBoots(ItemUtils.createItem(armor + "BOOTS"));
				stand.setItemInHand(ItemUtils.createItem(i == 1 ? "DIAMOND_SWORD" : i == 2 ? "IRON_SWORD" : i == 3 ? "GOLD_SWORD" : i == 4 ? "STONE_SWORD" : "WOOD_SWORD"));
				stand.setSmall(true);

				Hologram hologram = HolographicDisplaysAPI.createHologram(Main.getPlugin(), armorStand.add(0, 2.5, 0), e.contains("kSolo") ? style.get(top).get(0).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]) : style2.get(top).get(0).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]));
				this.hologram = hologram;
				for (i = 1; i < style.get(top).size(); i++) {
					if (e.contains("kSolo")) {
						hologram.addLine(style.get(top).get(i).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]));
					} else {
						hologram.addLine(style2.get(top).get(i).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]));
					}
				}
			} else {
				String player = map.get(i);
				stand.setHelmet(ItemUtils.createSkull("3 : 1 : owner=" + player.split(":")[0]));

				hologram.clearLines();
				for (i = 0; i < style.get(top).size(); i++) {
					if (e.contains("kSolo")) {
						hologram.addLine(style.get(top).get(i).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]));
					} else {
						hologram.addLine(style2.get(top).get(i).replace("&", "§").replace("<nick>", player.split(":")[0]).replace("<status>", player.split(":")[1]));
					}
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getType() {
		return type;
	}

	public int getSlot() {
		return top;
	}

	public Location getLocation() {
		return armorStand;
	}

	@Getter
	private static Map<String, LeaderBoard> boards = new HashMap<>();

	public static void put(String id, LeaderBoard board, String type) {
		boards.put(id, board);
		Utils.getBoards().set("boards." + id + ".type", type);
		Utils.getBoards().set("boards." + id + ".slot", board.getSlot());
		Utils.getBoards().set("boards." + id + ".location", Utils.serializeLocation(board.getLocation()));
	}

	public static void remove(String id) {
		Utils.getBoards().set("boards." + id, null);
		LeaderBoard board = boards.get(id);
		boards.remove(id);
		board.destroy();
	}

	public static LeaderBoard get(String id) {
		return boards.get(id);
	}

	public static List<LeaderBoard> values() {
		return new ArrayList<>(boards.values());
	}

	public static void setup() {
		SettingsManager sm = Utils.getBoards();
		for (int i = 1; i < 6; i++) {
			style.put(i, sm.getStringList("armorstand_options.style" + i));
		}
		for (int i = 1; i < 6; i++) {
			style2.put(i, sm.getStringList("armorstand_options_wins.style" + i));
		}
		for (String key : sm.getSection("boards").getKeys(false)) {
			String s = "boards." + key;

			String type = sm.getString(s + ".type");
			int top = sm.getInt(s + ".slot");
			Location location = Utils.unserializeLocation(sm.getString(s + ".location"));
			boards.put(key, new LeaderBoard(key, type, top, location));
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				new BukkitRunnable() {
					@Override
					public void run() {
						long start = System.currentTimeMillis();
						for (LeaderBoard board : boards.values()) {
							board.update();
						}
					}
				}.runTaskTimer(Main.getPlugin(), 0, 1200 * sm.getInt("boards_options.minutesTopUpdate"));
			}
		}.runTaskLater(Main.getPlugin(), 200);
	}
}
