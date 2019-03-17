package com.gmail.gremorydev14.gremoryskywars.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.profile.Profile;

import lombok.Getter;

@Getter
public class SwAchievement {

	private Mode mode;
	private int quantity, points;
	private AchievementType type;
	private String title, description;

	public SwAchievement(Mode mode, AchievementType type, int points, int quantity, String title, String description) {
		this.mode = mode;
		this.type = type;
		this.title = title;
		this.points = points;
		this.quantity = quantity;
		this.description = description;
		achievements.add(this);
	}

	public ItemStack getIcon(Player p) {
		Map<String, MenuItem> items = MenuEditor.getItems();
		String string = unlocked(PlayerData.get(p)) ? items.get("a_unlocked").getBuild() : items.get("a_locked").getBuild();
		ItemStack item = ItemUtils.createItem(string.replace("%title%", title).replace("%description%", description).replace("%points%", String.valueOf(points)));
		return item;
	}

	public void check(PlayerData pd) {
		Profile p = Profile.get(pd.getPlayer());
		if (type == AchievementType.KILLS) {
			int current = mode == Mode.SOLO ? pd.getKillsSolo() : mode == Mode.TEAM ? pd.getKillsTeam() : pd.getKillsMega();
			if (current != quantity) {
				return;
			}

			pd.getPlayer().sendMessage(Language.messages$player$unlock_achievement.replace("%name%", title));
			p.setAchievements_points(p.getAchievements_points() + points);
		} else {
			int current = mode == Mode.SOLO ? pd.getWinsSolo() : mode == Mode.TEAM ? pd.getWinsTeam() : pd.getWinsMega();
			if (current != quantity) {
				return;
			}

			new BukkitRunnable() {

				@Override
				public void run() {
					if (pd != null && pd.getPlayer().isOnline()) {
						pd.getPlayer().sendMessage(Language.messages$player$unlock_achievement.replace("%name%", title));
					}
				}
			}.runTaskLater(Main.getPlugin(), 7);
			p.setAchievements_points(p.getAchievements_points() + points);
		}
	}

	public boolean unlocked(PlayerData pd) {
		int current = 0;
		if (type == AchievementType.KILLS) {
			current = mode == Mode.SOLO ? pd.getKillsSolo() : mode == Mode.TEAM ? pd.getKillsTeam() : pd.getKillsMega();
		} else {
			current = mode == Mode.SOLO ? pd.getWinsSolo() : mode == Mode.TEAM ? pd.getWinsTeam() : pd.getWinsMega();
		}
		return current >= quantity;
	}

	@Getter
	private static List<SwAchievement> achievements = new ArrayList<>();

	public static void register() {
		SettingsManager sm = Utils.getAchievements();

		ConfigurationSection sec = sm.getSection("achievements");
		for (String key : sec.getKeys(false)) {
			try {
				Mode mode = Mode.valueOf(sec.getString(key + ".mode").toUpperCase());
				AchievementType type = AchievementType.valueOf(sec.getString(key + ".type").toUpperCase());
				if (mode == null || type == null)
					return;
				int points = sec.getInt(key + ".points"), quantity = sec.getInt(key + ".quantity");
				new SwAchievement(mode, type, points, quantity, sec.getString(key + ".title"), sec.getString(key + ".description"));
			} catch (Exception e) {
				System.out.println(key + ": ERROR");
			}
		}
		Logger.info("Loaded " + achievements.size() + " achievement(s)!");
	}

	public static void check(PlayerData pd, Mode mode, AchievementType type) {
		for (SwAchievement sa : achievements)
			if (mode == sa.getMode() && type == sa.getType())
				sa.check(pd);
	}

	public static enum AchievementType {
		KILLS, WINS;
	}
}
