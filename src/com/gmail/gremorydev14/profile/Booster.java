package com.gmail.gremorydev14.profile;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Booster {

	private int time, id;
	private BoosterType type;
	private TimeBooster timeType;

	public Booster(int time, BoosterType type, TimeBooster timeType) {
		this.time = time;
		this.type = type;
		this.timeType = timeType;
		boosters.add(this);
		this.id = boosters.size();
	}

	@Getter
	private static List<Booster> boosters = new ArrayList<>();

	public static void register() {
		SettingsManager sm = Utils.getBoosters();
		ConfigurationSection sec = sm.getSection("boosters");

		for (String key : sec.getKeys(false)) {
			try {
				int time = sec.getInt(key + ".time");
				BoosterType type;
				try {
					type = BoosterType.valueOf(sec.getString(key + ".type"));
				} catch (Exception e) {
					Logger.warn("Booster type \"" + sec.getString(key + ".type") + "\" invalid (ID " + key + ")");
					continue;
				}
				TimeBooster timeType;
				try {
					timeType = TimeBooster.valueOf(sec.getString(key + ".timeType"));
				} catch (Exception e) {
					Logger.warn("Booster timeType \"" + sec.getString(key + ".timeType") + "\" invalid (ID " + key + ")");
					continue;
				}
				new Booster(time, type, timeType);
			} catch (Exception e) {
			}
		}
		Logger.info("Loaded " + boosters.size() + " booster(s)!");
	}

	public static String parse(List<Booster> boosters) {
		List<String> ids = new ArrayList<>();
		for (Booster booster : boosters)
			ids.add("" + booster.getId());
		return ids.toString().replace(", ", ":").replace("[", "").replace("]", "");
	}

	public static Booster get(int id) {
		for (Booster b : boosters)
			if (b.getId() == id)
				return b;
		return null;
	}

	@Getter
	@AllArgsConstructor
	public static enum BoosterType {
		b1_5(1.5), b2_0(2.0), b2_5(2.5), b3_0(3.0);

		private double modifier;
	}

	@Getter
	@AllArgsConstructor
	public static enum TimeBooster {
		DAYS("Days", 86400), HOURS("Hours", 3600);

		private String name;
		private int modifier;
	}
}
