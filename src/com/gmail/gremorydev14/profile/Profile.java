package com.gmail.gremorydev14.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLite;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Profile {

	@Setter
	private double xp;
	private Player player;
	private SQLDatabase storage;
	@Setter
	private int achievements_points, level;
	@Setter
	private boolean used;
	@Setter
	private long time;
	@Setter
	private Booster using;
	@Getter
	private List<Booster> boosters = new ArrayList<>();

	public Profile(Player p) {
		this.player = p;
		this.storage = new SQLDatabase(p);

		this.xp = storage.getDouble("xp");
		int id = Integer.parseInt(storage.getString("time").split(":")[0]);
		if (id != 0)
			this.using = Booster.get(id);
		this.time = Long.valueOf(storage.getString("time").split(":")[1]) * 1000;
		this.level = storage.getInt("level");
		this.used = Boolean.valueOf(storage.getString("levels"));
		String booster = storage.getString("booster");
		if (!booster.equals(""))
			for (String boosterId : booster.split(":"))
				boosters.add(Booster.get(Integer.parseInt(boosterId)));
		this.achievements_points = storage.getInt("achievements_points");
	}

	public void useBoost(Booster b) {
		if (using != null && time > System.currentTimeMillis()) {
			if (Main.getEnderman() != null)
				player.playSound(player.getLocation(), Main.getEnderman(), 1.0f, 1.0f);
			player.sendMessage(Language.messages$player$already_using_booster);
			return;
		}

		if (Main.getLevel() != null)
			player.playSound(player.getLocation(), Main.getLevel(), 1.0f, 1.0f);
		this.boosters.remove(b);
		this.using = b;
		this.time = ((System.currentTimeMillis() / 1000) + (b.getTimeType().getModifier() * b.getTime())) * 1000;
		player.sendMessage(
				Language.messages$player$already_use_booster.replace("%modifier%", "" + b.getType().getModifier()).replace("%time%", b.getTime() + "").replace("%type%", b.getTimeType().getName().replace(b.getTime() > 1 ? "" : "s", "")));
	}

	public String isBoost() {
		return using == null || time <= System.currentTimeMillis() ? "" : "(" + using.getType().getModifier() + " Private Booster)";
	}

	public void addXp(double xp) {
		this.xp += xp;
		Level level = Level.getNext(this.level);
		if (level.getLevel() != this.level)
			if (this.xp >= level.getXp()) {
				this.level++;
				if (!used)
					Level.getNext(this.level - 2).getReward().give(this);
				this.used = false;
				new BukkitRunnable() {

					@Override
					public void run() {
						player.sendMessage(Language.messages$player$level_up.replace("%level%", "" + Profile.this.level));
					}
				}.runTaskLater(Main.getPlugin(), 7);
			}
	}

	public void save() {
		Utils.getStorage().execute("UPDATE g_profile SET achievements_points=?,xp=?,level=?,booster=?,time=?,levels=? WHERE uuid=?", false, achievements_points, xp, level, Booster.parse(boosters),
				(using != null ? using.getId() : 0) + ":" + time / 1000, String.valueOf(used), player.getUniqueId().toString());
	}

	public void saveAsync() {
		Utils.getStorage().execute("UPDATE g_profile SET achievements_points=?,xp=?,level=?,booster=?,time=?,levels=? WHERE uuid=?", (Utils.getStorage() instanceof SQLite), achievements_points, xp, level, Booster.parse(boosters),
				(using != null ? using.getId() : 0) + ":" + time / 1000, String.valueOf(used), player.getUniqueId().toString());
	}

	private static Map<UUID, Profile> profiles = new HashMap<>();

	public static void create(Player p) {
		profiles.put(p.getUniqueId(), new Profile(p));
	}

	public static void delete(Player p) {
		if (get(p) != null)
			profiles.remove(p.getUniqueId());
	}

	public static Profile get(Player p) {
		return profiles.get(p.getUniqueId());
	}

	public static Collection<Profile> values() {
		return profiles.values();
	}
}
