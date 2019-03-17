package com.gmail.gremorydev14.gremoryskywars.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.delivery.SwDelivery;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.api.GremorySkywarsAPI;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Cage;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.editor.ScoreboardConstructor;
import com.gmail.gremorydev14.gremoryskywars.player.SwAchievement.AchievementType;
import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLDatabase;
import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLite;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Options;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.inventory.SwInventory;
import com.gmail.gremorydev14.profile.Profile;

import lombok.Getter;
import lombok.Setter;

public class PlayerData {

	@Getter
	@Setter
	private Cage cage;
	@Getter
	@Setter
	private Arena arena;
	@Getter
	private Player player;
	@Getter
	@Setter
	private Kit sKit, tKit, mKit;
	@Getter
	@Setter
	private Perk sPerk, tPerk, mPerk;
	@Getter
	private SQLDatabase storage;
	@Getter
	private SmartScoreboard scoreboard;
	@Getter
	private SwInventory inventory;
	@Getter
	private SwDelivery delivery;
	@Getter
	@Setter
	private boolean view = false, tell = true;
	@Getter
	private Profile profile;
	@Getter
	private Map<String, Long> lastHit = new HashMap<>();
	@Getter
	private int killsSolo = 0, killsTeam = 0, killsMega = 0, winsSolo = 0, winsTeam = 0, winsMega = 0, souls = 0;

	public PlayerData(Player p) {
		this.player = p;
		this.storage = new SQLDatabase(p);
		Profile.create(p);
		SwInventory.create(p);
		SwDelivery.create(p);
		this.profile = Profile.get(p);
		this.inventory = SwInventory.get(p);
		this.delivery = SwDelivery.get(p);
		String[] options = this.storage.getString("options").split(" : ");

		this.killsSolo = this.storage.getInt("kSolo");
		this.killsTeam = this.storage.getInt("kTeam");
		this.killsMega = this.storage.getInt("kMega");
		this.winsSolo = this.storage.getInt("wSolo");
		this.winsTeam = this.storage.getInt("wTeam");
		this.winsMega = this.storage.getInt("wMega");
		this.souls = this.storage.getInt("souls");
		this.sKit = Kit.getKit(Options.KIT.getString(options).split(";")[0], Mode.SOLO);
		this.tKit = Kit.getKit(Options.KIT.getString(options).split(";")[1], Mode.TEAM);
		this.mKit = Kit.getKit(Options.KIT.getString(options).split(";")[2], Mode.MEGA);
		this.cage = Cage.getCage(Options.CAGE.getString(options));
		this.sPerk = Perk.getPerk(Options.PERK.getString(options).split(";")[0], Mode.SOLO);
		if (Options.PERK.getString(options).split(";").length > 1) {
			this.tPerk = Perk.getPerk(Options.PERK.getString(options).split(";")[1], Mode.TEAM);
			this.mPerk = Perk.getPerk(Options.PERK.getString(options).split(";")[2], Mode.MEGA);
		}
		this.view = Boolean.valueOf(Options.PLAYERS.getString(options));
		this.tell = Boolean.valueOf(Options.TELL.getString(options));
		if (this.sKit == null) {
			this.sKit = Kit.getKit("sDefault", Mode.SOLO);
		}
		if (this.tKit == null) {
			this.tKit = Kit.getKit("tDefault", Mode.TEAM);
		}
		if (this.mKit == null) {
			this.mKit = Kit.getKit("mDefault", Mode.MEGA);
		}
		if (this.cage == null) {
			this.cage = Cage.getCages().get(0);
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable() {
					
					@Override
					public void run() {
						scoreboard = new SmartScoreboard(p, ScoreboardConstructor.getScoreboard_lobby_title()) {

							@Override
							public String placeHolders(String str) {
								return str.replace("%sKills%", Utils.decimal(killsSolo)).replace("%sWins%", Utils.decimal(winsSolo)).replace("%tKills%", Utils.decimal(killsTeam)).replace("%tWins%", Utils.decimal(winsTeam))
										.replace("%mKills%", Utils.decimal(killsMega)).replace("%mWins%", Utils.decimal(winsMega)).replace("%coins%", Utils.decimal(getCoins())).replace("%souls%", Utils.decimal(souls));
							}
						};
						for (String line : ScoreboardConstructor.getScoreboard_lobby_lines())
							scoreboard.add(line);
						if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() == ServerMode.MULTI_ARENA) {
							if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getWorlds_allowed().contains(p.getWorld().getName())) {
								if (ScoreboardConstructor.isScoreboard_lobby_enabled()) {
									scoreboard.set(p);
								}
							}
						} else if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() == ServerMode.LOBBY) {
							if (ScoreboardConstructor.isScoreboard_lobby_enabled()) {
								scoreboard.set(p);
							}
						}
						scoreboard.updateScoreboard();
					}
				});
			}
		});
	}

	public void addKills(Mode mode) {
		if (mode == Mode.SOLO) {
			killsSolo++;
		} else if (mode == Mode.TEAM) {
			killsTeam++;
		} else {
			killsMega++;
		}
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
		SwAchievement.check(this, mode, AchievementType.KILLS);
	}

	public void addWins(Mode mode) {
		if (mode == Mode.SOLO) {
			winsSolo++;
		} else if (mode == Mode.TEAM) {
			winsTeam++;
		} else {
			winsMega++;
		}
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
		SwAchievement.check(this, mode, AchievementType.WINS);
	}

	public void addCoins(int coins) {
		if (profile.getUsing() != null) {
			if (profile.getTime() > System.currentTimeMillis()) {
				GremorySkywarsAPI.addCoins(player, coins * profile.getUsing().getType().getModifier());
			} else {
				profile.setUsing(null);
				GremorySkywarsAPI.addCoins(player, coins);
			}
		} else {
			GremorySkywarsAPI.addCoins(player, coins);
		}
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
	}

	public void addSouls(int souls) {
		this.souls += souls;
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
	}

	public void removeCoins(int coins) {
		GremorySkywarsAPI.removeCoins(player, coins);
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
	}

	public void removeSouls(int souls) {
		this.souls -= souls;
		if (com.gmail.gremorydev14.gremoryskywars.editor.Options.getMode() != ServerMode.BUNGEE) {
			this.scoreboard.updateScoreboard();
		}
	}

	public double getCoins() {
		return GremorySkywarsAPI.getCoins(getPlayer());
	}

	public void save() {
		Utils.getStorage().execute("UPDATE g_sw SET kSolo=?,kTeam=?,kMega=?,wSolo=?,wTeam=?,wMega=?,souls=?,options=? WHERE uuid=?", false, killsSolo, killsTeam, killsMega, winsSolo, winsTeam, winsMega, souls, Options.parseFrom(this),
				player.getUniqueId().toString());
		profile.save();
		inventory.save();
		delivery.save();
	}

	public void saveAsync() {
		Utils.getStorage().execute("UPDATE g_sw SET kSolo=?,kTeam=?,kMega=?,wSolo=?,wTeam=?,wMega=?,souls=?,options=? WHERE uuid=?", (Utils.getStorage() instanceof SQLite), killsSolo, killsTeam, killsMega, winsSolo, winsTeam, winsMega,
				souls, Options.parseFrom(this), player.getUniqueId().toString());
		profile.saveAsync();
		inventory.saveAsync();
		delivery.saveAsync();
	}

	private static Map<UUID, PlayerData> datas = new HashMap<>();

	public static PlayerData create(Player p) {
		PlayerData pd = new PlayerData(p);
		datas.put(p.getUniqueId(), pd);
		return pd;
	}

	public static boolean remove(Player p) {
		if (get(p) == null) {
			return false;
		}
		datas.remove(p.getUniqueId());
		Profile.delete(p);
		SwInventory.delete(p);
		SwDelivery.delete(p);
		return true;
	}

	public static PlayerData get(Player p) {
		return datas.get(p.getUniqueId());
	}

	public static Collection<PlayerData> values() {
		return datas.values();
	}
}
