package com.gmail.gremorydev14.profile;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.Getter;

@Getter
public class Level {

	private int level, xp;
	private LevelReward reward;

	public Level(int level, int xp, LevelReward reward) {
		this.xp = xp;
		this.level = level;
		this.reward = reward;
		levels.add(this);
	}

	@Getter
	private static List<Level> levels = new ArrayList<>();

	public static void register() {
		SettingsManager sm = Utils.getLevels();
		ConfigurationSection sec = sm.getSection("levels");

		for (String key : sec.getKeys(false)) {
			try {
				int level = Integer.parseInt(key);
				int xp = sec.getInt(key + ".xp");
				String str = sec.getString(key + ".reward");
				try {
					LevelReward a = LevelReward.parse(str);
					if (a == null) {
						Logger.warn("Level reward \"" + str + "\" invalid (Level " + key + ")");
						continue;
					}
					new Level(level, xp, a);
				} catch (Exception e) {
					Logger.warn("Level reward \"" + str + "\" invalid (Level " + key + ")");
				}
			} catch (Exception e) {
			}
		}
		Logger.info("Loaded " + levels.size() + " level(s)!");
	}

	public static Level getNext(int level) {
		if (level > levels.size())
			return levels.get(level - 1);
		return levels.get(level);
	}

	public static interface LevelReward {

		public void give(Profile pf);

		public static LevelReward parse(String string) {
			try {
				if (string.toLowerCase().startsWith("coins:"))
					return new CoinsReward(Integer.parseInt(string.split(":")[1]));
				else if (string.toLowerCase().startsWith("booster:"))
					return new BoosterReward(Integer.parseInt(string.split(":")[1]));
			} catch (Exception e) {
			}
			return null;
		}

		@Getter
		public static class CoinsReward implements LevelReward {

			private int coins;

			public CoinsReward(int coins) {
				this.coins = coins;
			}

			@Override
			public void give(Profile pf) {
				PlayerData pd = PlayerData.get(pf.getPlayer());
				if (pd != null) {
					pd.addCoins(getCoins());
					pf.getPlayer().sendMessage(Language.messages$player$level_reward_coins.replace("%coins%", "" + coins));
				}
			}
		}

		@Getter
		public static class BoosterReward implements LevelReward {

			private int id;

			public BoosterReward(int id) {
				this.id = id;
			}

			@Override
			public void give(Profile pf) {
				Booster b = Booster.get(getId());
				if (b != null) {
					pf.getBoosters().add(b);
					pf.getPlayer().sendMessage(Language.messages$player$level_reward_booster.replace("%modifier%", "" + b.getType().getModifier()).replace("%time%", b.getTime() + "").replace("%type%", b.getTimeType().getName().replace(b.getTime() > 1 ? "" : "s", "")));
				}
			}
		}
	}
}