package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.Getter;

@Getter
public class ArenaSign {

	private Mode mode;
	private Type type;
	private SignType signType;
	private Location location;
	private boolean timed = false;

	public ArenaSign(SignType signType, Mode mode, Type type, Location location) {
		this.mode = mode;
		this.type = type;
		this.signType = signType;
		this.location = location;
		signs.put(location, this);
	}

	public void update(int count) {
		Block b = location.getBlock();
		if (!(b.getState() instanceof Sign)) {
			List<String> list = Utils.getLocations().getStringList("signs");
			list.remove(Utils.serializeLocation(location) + " : " + signType.toString().toLowerCase() + " : " + mode.toString().toLowerCase() + " : " + type.toString().toLowerCase());
			Utils.getLocations().set("signs", list);
			Logger.warn("Sign breaked removed in " + location.toString());
			signs.remove(location);
			return;
		}
		if (signType == SignType.SELECTOR && timed) {
			return;
		}
		Sign sign = (Sign) b.getState();
		if (signType != SignType.SELECTOR) {

			String _1 = Language.sign$global_1.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%queue%", Utils.decimal(count)).replace("%typeName%", getType().getName())
					.replace("%color%", getType().getColor().replace("9", "1"));
			String _2 = Language.sign$global_2.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%queue%", Utils.decimal(count)).replace("%typeName%", getType().getName())
					.replace("%color%", getType().getColor().replace("9", "1"));
			String _3 = Language.sign$global_3.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%queue%", Utils.decimal(count)).replace("%typeName%", getType().getName())
					.replace("%color%", getType().getColor().replace("9", "1"));
			String _4 = Language.sign$global_4.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%queue%", Utils.decimal(count)).replace("%typeName%", getType().getName())
					.replace("%color%", getType().getColor().replace("9", "1"));

			sign.setLine(0, _1);
			sign.setLine(1, _2);
			sign.setLine(2, _3);
			sign.setLine(3, _4);
		} else {
			timed = true;
			String _1 = Language.sign$selector_1.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%typeName%", getType().getName()).replace("%color%",
					getType().getColor().replace("9", "1"));
			String _2 = Language.sign$selector_2.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%typeName%", getType().getName()).replace("%color%",
					getType().getColor().replace("9", "1"));
			String _3 = Language.sign$selector_3.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%typeName%", getType().getName()).replace("%color%",
					getType().getColor().replace("9", "1"));
			String _4 = Language.sign$selector_4.replace("%mode%", getMode().getName().equals("Team") ? getMode().toString() + "S" : getMode().toString()).replace("%typeName%", getType().getName()).replace("%color%",
					getType().getColor().replace("9", "1"));

			sign.setLine(0, _1);
			sign.setLine(1, _2);
			sign.setLine(2, _3);
			sign.setLine(3, _4);
		}
		sign.update();
	}

	private static void apply(Mode mode, Type type) {
		int count = Arena.getCount(mode, type);
		if (mode == Mode.SOLO) {
			if (type == Type.NORMAL) {
				if (count == 0) {
					solo = Arena.getFromRandom(Mode.SOLO, Type.NORMAL);
				} else {
					solo = Arena.getFrom(Mode.SOLO, Type.NORMAL);	
				}
			} else {
				if (count == 0) {
					i_solo = Arena.getFromRandom(Mode.SOLO, Type.INSANE);
				} else {
					i_solo = Arena.getFrom(Mode.SOLO, Type.INSANE);	
				}
			}
		} else if (mode == Mode.TEAM) {
			if (type == Type.NORMAL) {
				if (count == 0) {
					team = Arena.getFromRandom(Mode.TEAM, Type.NORMAL);
				} else {
					team = Arena.getFrom(Mode.TEAM, Type.NORMAL);	
				}
			} else {
				if (count == 0) {
					i_team = Arena.getFromRandom(Mode.TEAM, Type.INSANE);
				} else {
					i_team = Arena.getFrom(Mode.TEAM, Type.INSANE);	
				}
			}
		} else if (mode == Mode.MEGA) {
			if (count == 0) {
				mega = Arena.getFromRandom(Mode.MEGA, Type.MEGA);
			} else {
				mega = Arena.getFrom(Mode.MEGA, Type.MEGA);	
			}
		}
	}

	@Getter
	private static Map<Location, ArenaSign> signs = new HashMap<>();
	@Getter
	private static Arena solo, team, mega, i_solo, i_team;

	public static void register() {
		SettingsManager sm = Utils.getLocations();
		if (!sm.contains("signs")) {
			sm.set("signs", new ArrayList<String>());
		}
		for (String serialized : sm.getStringList("signs")) {
			String[] split = serialized.split(" : ");
			Location location = Utils.unserializeLocation(serialized);
			Type type = Type.valueOf(split[8].toUpperCase());
			Mode mode = Mode.valueOf(split[7].toUpperCase());
			SignType signType = SignType.valueOf(split[6].toUpperCase());
			new ArenaSign(signType, mode, type, location);
		}
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			new BukkitRunnable() {

				@Override
				public void run() {
					apply(Mode.SOLO, Type.NORMAL);
					apply(Mode.SOLO, Type.INSANE);
					apply(Mode.TEAM, Type.NORMAL);
					apply(Mode.TEAM, Type.INSANE);
					apply(Mode.MEGA, Type.NORMAL);
				}
			}.runTaskTimer(Main.getPlugin(), 0, 60);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (ArenaSign sign : new ArrayList<>(signs.values())) {
					if (sign.getSignType() == SignType.SELECTOR) {
						sign.update(0);
					} else {
						int count = Arena.getCount(sign.getMode(), sign.getType());
						sign.update(count);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 40);
	}

	public static ArenaSign get(Location loc) {
		return signs.get(loc);
	}

	public static Arena switchFrom(Mode mode, Type type) {
		if (mode == Mode.SOLO) {
			if (type == Type.NORMAL) {
				return solo;
			} else {
				return i_solo;
			}
		} else if (mode == Mode.TEAM) {
			if (type == Type.NORMAL) {
				return team;
			} else {
				return i_team;
			}
		} else {
			return mega;
		}
	}

	public static enum SignType {
		GLOBAL, SELECTOR;
	}
}
