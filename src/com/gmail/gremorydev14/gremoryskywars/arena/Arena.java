package com.gmail.gremorydev14.gremoryskywars.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaTeam;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaVote;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaVote.VoteType;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Cage;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.editor.ScoreboardConstructor;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.player.SmartScoreboard;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Ping;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage.ChatExtra;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage.ClickEventType;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage.HoverEventType;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.jnbt.Schematic;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;
import com.gmail.gremorydev14.profile.Rank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@SuppressWarnings("all")
public class Arena {

	private double rate;
	private int votes;
	private Type type;
	private Mode mode;
	private World world;
	private String name, sv;
	private CuboId cuboId;

	private State state;
	private Location schematic;
	private SettingsManager config;

	private BukkitTask countdownTask;
	private String theEvent;
	private int countdown = 60, minPlayers = 2, nextEvent;

	// private Map<Location, ABlock> blocks = new HashMap<>();
	private List<Block> blocks = new ArrayList<>();
	private List<ArenaTeam> spawns = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();
	private Map<String, Integer> kills = new HashMap<>();
	private Map<Location, Chest> chests = new HashMap<>();
	private Map<Player, ArenaTeam> players = new HashMap<>();

	private ArenaVote health = new ArenaVote(this, VoteType.HEALTH);
	private ArenaVote time = new ArenaVote(this, VoteType.TIME);

	public Arena(String name, World world) {
		this.name = name;
		this.world = world;
		this.config = SettingsManager.getConfig(world.getName(), "plugins/GremorySkywars/arenas");
		// SettingsManager data = SettingsManager.getConfig2(world.getName(),
		// "plugins/GremorySkywars/saves");

		this.sv = config.getString("server");
		if (!Utils.getVotes().contains(name + ".rate")) {
			Utils.getVotes().set(name + ".rate", 100);
		}
		if (!Utils.getVotes().contains(name + ".voted")) {
			Utils.getVotes().set(name + ".voted", 0);
		}
		if (!Utils.getVotes().contains(name + ".votes")) {
			Utils.getVotes().set(name + ".votes", new ArrayList<String>());
		}
		this.votes = Utils.getVotes().getInt(name + ".voted");
		this.rate = Utils.getVotes().getInt(name + ".rate");
		this.cuboId = new CuboId(config.getString("cubo-id"));
		/*
		 * if (!data.contains(cuboId.toString())) { Scan.generateData(this,
		 * data); } for (String blocked : data.getStringList(cuboId.toString()))
		 * { int typeId = Integer.parseInt(blocked.split(" : ")[6]); byte
		 * dataByte = Byte.parseByte(blocked.split(" : ")[7]);
		 * blocks.put(Utils.unserializeLocation(blocked), new ABlock(typeId,
		 * dataByte)); }
		 */
		this.mode = Mode.get(config.getString("mode")) == null ? Mode.SOLO : Mode.get(config.getString("mode"));
		this.type = Type.get(config.getString("type")) == null ? Type.NORMAL : Type.get(config.getString("type"));
		if (this.mode == Mode.MEGA) {
			this.type = Type.MEGA;
		}
		if (config.contains("schematic") && mode != Mode.SOLO) {
			schematic = Utils.unserializeLocation(config.getString("schematic"));
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), new Runnable() {

				@Override
				public void run() {
					Schematic.generateAsync(schematic, blocks);
				}
			}, 1);
		}
		for (String loc : config.getStringList("chests")) {
			if (loc.split(" : ").length > 5) {
				this.chests.put(Utils.unserializeLocation(loc), Chest.getChest(loc.split(" : ")[6]) == null ? Chest.getChest("normal") : Chest.getChest(loc.split(" : ")[6]));
			}
		}
		for (String loc : config.getStringList("spawns")) {
			if (loc.split(" : ").length > 4) {
				this.spawns.add(mode == Mode.SOLO ? new ArenaTeam(this, Utils.unserializeLocation(loc)) : new ArenaTeam(this, Utils.unserializeLocation(loc), ""));
			}
		}
		this.minPlayers = config.getInt("min-players");
		this.state = State.WAITING;
		arenas.put(world.getName(), this);
	}

	private void addParty(Player p, PartyBukkit pb) {
		for (Player ps : pb.getMembers()) {
			if (!ps.equals(p)) {
				PlayerData pd = PlayerData.get(ps);

				if (pd != null && pd.getArena() == null && players.size() < spawns.size() * mode.getSize()) {
					ArenaTeam team = getAvaibleTeam(1);
					if (team == null) {
						continue;
					}
					pd.setArena(Arena.this);
					ps.setGameMode(GameMode.ADVENTURE);
					if (team.getMembers().size() == 0) {
						pd.getCage().apply(team.getSpawnLocation(), mode.getSize() > 1);
					}
					team.addPlayer(ps);
					ps.setAllowFlight(false);
					players.put(ps, team);
					SmartScoreboard.put(ps, ScoreboardConstructor.createFromState(ps, Arena.this));
					SmartScoreboard.get(ps).updateScoreboard();
					ps.setLevel(0);
					ps.setExp(0.0f);
					if (schematic != null) {
						ps.teleport(schematic);
					} else {
						ps.teleport(team.getSpawnLocation());
					}
					ps.getInventory().clear();
					ps.getInventory().setArmorContents(null);
					if (Options.getWait_kits().getSlot() > -1) {
						ps.getInventory().setItem(Options.getWait_kits().getSlot(), Options.getWait_kits().getItem());
					}
					if (type == Type.INSANE) {
						if (Options.getWait_insane().getSlot() > -1) {
							ps.getInventory().setItem(Options.getWait_insane().getSlot(), Options.getWait_insane().getItem());
						}
					}
					if (Options.getWait_leave().getSlot() > -1) {
						ps.getInventory().setItem(Options.getWait_leave().getSlot(), Options.getWait_leave().getItem());
					}
					ps.updateInventory();
					broadcastMessage(Language.messages$arena$join.replace("%pName%", ps.getName()).replace("%pDName%", ps.getDisplayName()).replace("%on%", String.valueOf(players.size())).replace("%max%",
							String.valueOf(spawns.size() * mode.getSize())));
					for (Player ps2 : Bukkit.getOnlinePlayers()) {
						if (!ps2.getWorld().getName().equals(ps.getWorld().getName())) {
							ps.hidePlayer(ps2);
							ps.hidePlayer(ps);
						}
					}
					for (Player ps2 : players.keySet()) {
						ps2.showPlayer(ps);
						ps.showPlayer(ps2);
					}
				}
			}
		}
	}

	private void addForTeam(Player p, PartyBukkit pb, ArenaTeam team) {
		for (Player ps : pb.getMembers()) {
			if (!ps.equals(pb.getOwner()) && !ps.equals(p)) {
				PlayerData pd = PlayerData.get(ps);

				if (pd != null && players.size() < spawns.size() * mode.getSize()) {
					pd.setArena(Arena.this);
					ps.setGameMode(GameMode.ADVENTURE);
					team.addPlayer(ps);
					ps.setAllowFlight(false);
					players.put(ps, team);
					SmartScoreboard.put(ps, ScoreboardConstructor.createFromState(ps, Arena.this));
					SmartScoreboard.get(ps).updateScoreboard();
					ps.setLevel(0);
					ps.setExp(0.0f);
					if (schematic != null) {
						ps.teleport(schematic);
					} else {
						ps.teleport(team.getSpawnLocation());
					}
					ps.getInventory().clear();
					ps.getInventory().setArmorContents(null);
					if (Options.getWait_kits().getSlot() > -1) {
						ps.getInventory().setItem(Options.getWait_kits().getSlot(), Options.getWait_kits().getItem());
					}
					if (type == Type.INSANE) {
						if (Options.getWait_insane().getSlot() > -1) {
							ps.getInventory().setItem(Options.getWait_insane().getSlot(), Options.getWait_insane().getItem());
						}
					}
					if (Options.getWait_leave().getSlot() > -1) {
						ps.getInventory().setItem(Options.getWait_leave().getSlot(), Options.getWait_leave().getItem());
					}
					ps.updateInventory();
					broadcastMessage(Language.messages$arena$join.replace("%pName%", ps.getName()).replace("%pDName%", ps.getDisplayName()).replace("%on%", String.valueOf(players.size())).replace("%max%",
							String.valueOf(spawns.size() * mode.getSize())));
					for (Player ps2 : Bukkit.getOnlinePlayers()) {
						if (!ps2.getWorld().getName().equals(ps.getWorld().getName())) {
							ps.hidePlayer(ps2);
							ps.hidePlayer(ps);
						}
					}
					for (Player ps2 : players.keySet()) {
						ps2.showPlayer(ps);
						ps.showPlayer(ps2);
					}
				}
			}
		}
	}

	public void add(Player p) {
		if (!getState().isAvaible()) {
			p.sendMessage(Language.messages$arena$not_avaible);
			return;
		}

		if (getPlayers().size() >= getSpawns().size() * getMode().getSize()) {
			p.sendMessage(Language.messages$arena$full);
			return;
		}

		PlayerData pd = PlayerData.get(p);
		if (pd.getArena() != null) {
			p.sendMessage(Language.messages$arena$already_ingame);
			return;
		}

		int teamSize = 1;
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			PartyBukkit party = PartyBukkit.get(p);
			if (party != null) {
				if (mode != Mode.SOLO) {
					teamSize = party.getSlots();
				}

				if (party.getOwner().equals(p)) {
					for (Player ps : party.getMembers()) {
						if (!ps.equals(p)) {
							PlayerData pd2 = PlayerData.get(ps);
							if (pd2 != null && pd2.getArena() != null) {
								p.sendMessage(Language.messages$party$members_in_arena);
								return;
							}
						}
					}
				} else {
					p.sendMessage(Language.messages$party$leader_match);
					return;
				}
			}
		}
		ArenaTeam team = getAvaibleTeam(teamSize);
		if (team == null) {
			p.sendMessage(Language.messages$arena$full);
			return;
		}
		if (team.getMembers().size() == 0) {
			pd.getCage().apply(team.getSpawnLocation(), mode.getSize() > 1);
		}
		team.addPlayer(p);
		players.put(p, team);
		SmartScoreboard.put(p, ScoreboardConstructor.createFromState(p, this));
		SmartScoreboard.get(p).updateScoreboard();
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(false);
		pd.setArena(this);
		p.setMaxHealth(20.0);
		p.setLevel(0);
		p.setExp(0.0f);
		if (schematic != null) {
			p.teleport(schematic);
		} else {
			p.teleport(team.getSpawnLocation());
		}
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (!ps.getWorld().getName().equals(p.getWorld().getName())) {
				p.hidePlayer(ps);
				ps.hidePlayer(p);
			} else {
				PlayerData pd2 = PlayerData.get(ps);
				if (pd2 != null && pd2.getArena() != null && pd2.getArena().equals(this)) {
					p.showPlayer(ps);
					ps.showPlayer(p);
				}
			}
		}
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.getInventory().setHeldItemSlot(4);
		if (Options.getWait_kits().getSlot() > -1) {
			p.getInventory().setItem(Options.getWait_kits().getSlot(), Options.getWait_kits().getItem());
		}
		if (type == Type.INSANE) {
			if (Options.getWait_insane().getSlot() > -1) {
				p.getInventory().setItem(Options.getWait_insane().getSlot(), Options.getWait_insane().getItem());
			}
		}
		if (Options.getWait_leave().getSlot() > -1) {
			p.getInventory().setItem(Options.getWait_leave().getSlot(), Options.getWait_leave().getItem());
		}
		p.updateInventory();
		broadcastMessage(
				Language.messages$arena$join.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()).replace("%on%", String.valueOf(getPlayers().size())).replace("%max%", String.valueOf(spawns.size() * mode.getSize())));
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			PartyBukkit party = PartyBukkit.get(p);
			if (party != null) {
				if (mode != Mode.SOLO) {
					addForTeam(p, party, team);
				} else {
					addParty(p, party);
				}
			}
		}
		for (Player ps : players.keySet()) {
			SmartScoreboard sb = SmartScoreboard.get(ps);
			if (sb != null) {
				sb.updateScoreboard();
			}
		}
		if (players.size() >= minPlayers && state.equals(State.WAITING)) {
			state = State.STARTING;
			countdown = Options.getCountdown_start();
			for (Player ps : players.keySet()) {
				SmartScoreboard.put(ps, ScoreboardConstructor.createFromState(ps, this));
				SmartScoreboard.get(ps).updateScoreboard();
			}
			countdownTask = new BukkitRunnable() {

				@Override
				public void run() {
					if (getCountdown() <= 0) {
						cancel();
						start();
						return;
					}
					for (Player ps : players.keySet()) {
						SmartScoreboard sb = SmartScoreboard.get(ps);
						if (sb != null) {
							sb.updateScoreboard();
						}
					}
					if (getCountdown() <= 10) {
						if (getCountdown() <= 5) {
							broadcastTitle(Language.messages$arena$title_starting.split("\n")[0].replace("%count%", "" + getCountdown()), Language.messages$arena$title_starting.split("\n")[1].replace("%count%", "" + getCountdown()));
						} else {
							broadcastTitle(Language.messages$arena$title_skywars.split("\n")[0].replace("%color%", getType().getColor()).replace("%type%", getType().getName()),
									Language.messages$arena$title_skywars.split("\n")[1].replace("%color%", getType().getColor()).replace("%type%", getType().getName()));
						}
						broadcastMessage(Language.messages$arena$broadcast_starting.replace("%count%", "" + getCountdown()).replace("%S%", getCountdown() > 1 ? "s" : ""));
					}
					--countdown;
				}
			}.runTaskTimer(Main.getPlugin(), 0, 20);
		}
		if (state.equals(State.STARTING) && countdown > Options.getCountdown_full() && players.size() >= spawns.size() * mode.getSize()) {
			countdown = Options.getCountdown_full();
			broadcastMessage(Language.messages$arena$broadcast_full.replace("%count%", "" + countdown));
		}
	}

	public void remove(Player p, boolean silent, boolean play) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null || pd.getArena() == null)
			return;
		pd.setArena(null);
		if (Options.getMode() == ServerMode.MULTI_ARENA && play) {
			PartyBukkit party = PartyBukkit.get(p);
			if (party != null) {
				if (!party.getOwner().equals(p)) {
					play = false;
				} else {
					for (Player ps : party.getMembers()) {
						PlayerData user = PlayerData.get(ps);
						if (user != null && user.getArena() != null) {
							play = false;
							break;
						}
					}
				}
			}
		}

		ArenaTeam team = players.get(p);
		team.removePlayer(p);
		this.players.remove(p);
		for (PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}
		for (Player ps : players.keySet()) {
			SmartScoreboard sb = SmartScoreboard.get(ps);
			if (sb != null) {
				sb.updateScoreboard();
			}
		}
		p.setAllowFlight(false);
		if (Options.getMode() != ServerMode.BUNGEE) {
			if (!play) {
				p.teleport(Utils.getLobby());
				p.setGameMode(GameMode.ADVENTURE);
				if (ScoreboardConstructor.isScoreboard_lobby_enabled()) {
					pd.getScoreboard().set(p);
				}
				for (Player ps : Bukkit.getOnlinePlayers()) {
					if (PlayerData.get(ps) == null || PlayerData.get(ps).getArena() != null) {
						p.hidePlayer(ps);
						ps.hidePlayer(p);
					} else {
						if (PlayerData.get(p).isView()) {
							p.showPlayer(ps);
						} else {
							p.hidePlayer(ps);
						}
						if (PlayerData.get(ps) == null || PlayerData.get(ps).isView()) {
							ps.showPlayer(p);
						} else {
							ps.hidePlayer(p);
						}

						if (ps.hasMetadata("ADMIN_MODE")) {
							p.hidePlayer(ps);
						}
					}
				}
			}
			Rank rank = Rank.getRank(p);
			rank.apply(p);
		}
		if (Options.getMode() == ServerMode.BUNGEE) {
			pd.save();
			Utils.sendToServer(p, Options.getLobby_bungee());
		}
		if (spectators.contains(p)) {
			spectators.remove(p);
		}
		if (!silent) {
			broadcastMessage(Language.messages$arena$leave.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()).replace("%on%", String.valueOf(getPlayers().size())).replace("%max%",
					String.valueOf(getSpawns().size() * getMode().getSize())));
		}
		end();
		if (state.isAvaible()) {
			if (team.getMembers().size() == 0) {
				Cage.remove(team.getSpawnLocation(), mode.getSize() > 1);
			}

			if (countdownTask != null && players.size() < minPlayers) {
				countdownTask.cancel();
				countdownTask = null;
				state = State.WAITING;
				for (Player ps : getPlayers().keySet()) {
					ps.sendMessage(Language.messages$arena$broadcast_cancel);
					SmartScoreboard.put(ps, ScoreboardConstructor.createFromState(ps, this));
					SmartScoreboard.get(ps).updateScoreboard();
				}
			}
		}
	}

	public void kill(Player p, String message) {
		spectators.add(p);
		p.setMaxHealth(20.0);
		broadcastMessage(message);
		broadcastAction(Language.messages$arena$kill_action_bar.replace("%players%", "" + playersAlive()));
		Reflection.sendTitle(p, Language.messages$arena$spectator_title.split("\n")[0], Language.messages$arena$spectator_title.split("\n")[1]);
		Reflection.sendChatPacket(p, die.toString());

		ArenaTeam team = players.get(p);
		for (Player ps : players.keySet()) {
			if (spectators.contains(ps)) {
				p.showPlayer(ps);
				ps.showPlayer(p);
			} else {
				ps.hidePlayer(p);
			}

			for (Team t : SmartScoreboard.get(ps).getTeams()) {
				if (t.hasPlayer(p)) {
					t.setPrefix("§7");
				}
			}
		}

		team.removePlayer(p);
		end();
	}

	public void start() {
		if (players.size() < 1) {
			return;
		}

		if (schematic != null && state != State.GAME) {
			if (state != State.STARTING) {
				for (Player ps : players.keySet()) {
					SmartScoreboard sb = ScoreboardConstructor.createFromState(ps, this);
					SmartScoreboard.put(ps, sb);
					sb.updateScoreboard();
				}
			}
			state = State.GAME;
			countdown = 10;
			if (countdownTask != null) {
				countdownTask.cancel();
			}

			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), new Runnable() {

				@Override
				public void run() {
					Bukkit.getScheduler().runTask(Main.getPlugin(), () -> blocks.stream().forEach(b -> b.setType(Material.AIR)));
				}
			}, 1);

			for (Player ps : players.keySet()) {
				ps.teleport(players.get(ps).getSpawnLocation());
				ps.closeInventory();
				ps.getInventory().clear();
				ps.getInventory().setArmorContents(null);
				ps.updateInventory();
			}

			countdownTask = new BukkitRunnable() {

				@Override
				public void run() {
					if (countdown <= 0) {
						start();
						cancel();
						return;
					}

					if (getCountdown() <= 5) {
						broadcastTitle(Language.messages$arena$title_starting.split("\n")[0].replace("%count%", "" + getCountdown()), Language.messages$arena$title_starting.split("\n")[1].replace("%count%", "" + getCountdown()));
					} else {
						broadcastTitle(Language.messages$arena$title_skywars.split("\n")[0].replace("%color%", getType().getColor()).replace("%type%", getType().getName()),
								Language.messages$arena$title_skywars.split("\n")[1].replace("%color%", getType().getColor()).replace("%type%", getType().getName()));
					}

					for (Player ps : players.keySet()) {
						SmartScoreboard sb = SmartScoreboard.get(ps);
						if (sb != null) {
							sb.updateScoreboard();
						}
					}

					--countdown;
				}
			}.runTaskTimer(Main.getPlugin(), 0, 20);
			return;
		}

		if (state == State.GAME || schematic == null) {
			state = State.GAME;
			fillChests();
			boolean doubleH = health.getOrganizedVote() != null;
			for (Player ps : players.keySet()) {
				ps.setGameMode(GameMode.SURVIVAL);
				Reflection.sendTitle(ps, type.getColor() + "§l" + type.toString() + " MODE", "");
				PlayerData pd = PlayerData.get(ps);
				ps.closeInventory();
				ps.getInventory().clear();
				ps.getInventory().setArmorContents(null);
				if (getMode() == Mode.SOLO) {
					if (pd.getSKit() != null) {
						pd.getSKit().apply(ps);
					}
				} else if (getMode() == Mode.TEAM) {
					if (pd.getTKit() != null) {
						pd.getTKit().apply(ps);
					}
				} else {
					if (pd.getMKit() != null) {
						pd.getMKit().apply(ps);
					}
				}
				ps.damage(0.00001d);
				if (doubleH) {
					ps.setMaxHealth(40.0);
					ps.setHealth(40.0);
				}
				ps.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5));
				SmartScoreboard.put(ps, ScoreboardConstructor.createFromState(ps, this));
				SmartScoreboard sb = SmartScoreboard.get(ps);
				sb.updateScoreboard();
				kills.put(ps.getName(), 0);
				ArenaTeam team = players.get(ps);
				for (Player ps2 : players.keySet()) {
					ArenaTeam team2 = players.get(ps2);
					Team t = sb.getScoreboard().registerNewTeam("[TEAM:" + sb.getTeams().size() + "]");
					t.addPlayer(ps2);
					t.setPrefix(team.hasPlayer(ps2) ? (mode == Mode.SOLO ? "§a" : "§a" + team2.getPrefix() + " ") : (mode == Mode.SOLO ? "§c" : "§c" + team2.getPrefix() + " "));
				}
			}
			for (String toBroad : Language.messages$arena$start) {
				broadcastMessage(toBroad.startsWith("%center%") ? Utils.center(toBroad.replace("%center%", "")) : toBroad);
			}
			if (getMode().equals(Mode.SOLO)) {
				broadcastMessage(Language.messages$arena$team_notallowed_solo);
			} else {
				broadcastMessage(Language.messages$arena$team_notallowed_team);
			}
			broadcastMessage(Language.messages$arena$cages_opened);
			for (ArenaTeam team : spawns) {
				if (team.getMembers().size() > 0) {
					Cage.remove(team.getSpawnLocation(), mode.getSize() > 1);
				}
			}
			countdown = Options.getCountdown_game();
			if (countdownTask != null) {
				countdownTask.cancel();
			}
			nextEvent = getHighest(Options.getRefillTimes(), countdown);
			theEvent = getEvent(nextEvent);
			countdownTask = new BukkitRunnable() {

				@Override
				public void run() {
					if (countdown == nextEvent) {
						if (countdown == 0) {
							state = State.RESET;
							List<Entry<String, Integer>> tops = getTopKillers();
							for (String toBroad : Language.messages$arena$end) {
								toBroad = toBroad.contains("%center%") ? Utils.center(toBroad.replace("%center%", "")) : toBroad;

								toBroad = toBroad.replace("%winner%", "Nenhum").replace("%p1Name%", tops.get(0).getKey()).replace("%p1Kills%", "" + tops.get(0).getValue()).replace("%p2Name%", tops.get(1).getKey())
										.replace("%p2Kills%", "" + tops.get(1).getValue()).replace("%p3Name%", tops.get(2).getKey()).replace("%p3Kills%", "" + tops.get(2).getValue());
								broadcastMessage(toBroad);
							}
							for (Player ps : players.keySet()) {
								if (!spectators.contains(ps)) {
									ps.setHealth(20.0);
									ps.setFoodLevel(20);
									ps.setFireTicks(0);
									ps.setLevel(0);
									ps.setExp(0.0f);
									ps.setGameMode(GameMode.ADVENTURE);
									ps.setAllowFlight(true);
									ps.setFlying(true);
									ps.getInventory().clear();
									ps.getInventory().setArmorContents(null);
									if (Options.getSpec_compass().getSlot() > -1) {
										ps.getInventory().setItem(Options.getSpec_compass().getSlot(), Options.getSpec_compass().getItem());
									}
									if (Options.getSpec_settings().getSlot() > -1) {
										ps.getInventory().setItem(Options.getSpec_settings().getSlot(), Options.getSpec_settings().getItem());
									}
									if (Options.getSpec_again().getSlot() > -1) {
										ps.getInventory().setItem(Options.getSpec_again().getSlot(), Options.getSpec_again().getItem());
									}
									if (Options.getSpec_leave().getSlot() > -1) {
										ps.getInventory().setItem(Options.getSpec_leave().getSlot(), Options.getSpec_leave().getItem());
									}
									ps.updateInventory();
									ps.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 3));
									spectators.add(ps);
								}
							}
							broadcastTitle(Language.messages$player$title_lose.split("\n")[0], Language.messages$player$title_lose.split("\n")[1]);
							for (Player ps : getPlayers().keySet()) {
								int soul = 0;
								for (int i = 0; i < getKills(ps); i++) {
									if ((soul + 1) + PlayerData.get(ps).getSouls() <= 100) {
										soul++;
									}
								}
								int coins = 5;
								int experience = 10;
								PlayerData pd = PlayerData.get(ps);
								double old = pd.getCoins();
								pd.addCoins(coins);
								pd.getProfile().addXp(experience);
								for (String toBroad : Language.messages$arena$rewards) {
									toBroad = toBroad.contains("%center%") ? Utils.center(toBroad.replace("%center%", "")) : toBroad;
									String place = toBroad.replace("%tCoins%", "" + ((int) (pd.getCoins() - old))).replace("%tExperience%", "" + experience).replace("%souls%", "" + soul);
									if (toBroad.contains("%tCoins%")) {
										ps.sendMessage(place);
										if (pd.getProfile().getUsing() != null && pd.getProfile().getTime() > System.currentTimeMillis()) {
											ps.sendMessage("     §6+" + ((int) (coins * (pd.getProfile().getUsing().getType().getModifier() - 1.0))) + " " + pd.getProfile().isBoost());
										}
									} else {
										ps.sendMessage(place);
									}
								}
								if (!Utils.getVotes().getStringList(name + ".votes").contains(ps.getUniqueId().toString())) {
									String json = rating.toString();
									Reflection.sendChatPacket(ps, json.toString());
								}
							}
							broadcastJSON(play);
							if (countdownTask != null) {
								countdownTask.cancel();
							}
							countdownTask = new BukkitRunnable() {
								int seconds = 5;

								@Override
								public void run() {
									if (seconds <= 0) {
										cancel();
										stop();
										return;
									}

									--seconds;
								}
							}.runTaskTimer(Main.getPlugin(), 0, 20);
							return;
						}

						nextEvent = getHighest(Options.getRefillTimes(), countdown - 1);
						theEvent = getEvent(nextEvent);
						if (theEvent.equals("Final")) {
							refillChests();
							broadcastTitle("", Language.messages$arena$title_5min);
						} else {
							refillChests();
						}
					}
					for (Player ps : getPlayers().keySet()) {
						SmartScoreboard sb = SmartScoreboard.get(ps);
						if (sb != null) {
							sb.updateScoreboard();
						}

						if (!cuboId.contains(ps.getLocation()) && spectators.contains(ps)) {
							ps.teleport(cuboId.getRandomLocation());
						}
					}
					countdown--;
				}
			}.runTaskTimer(Main.getPlugin(), 0, 20);
			end();
		}
	}

	private static JSONMessage rating;
	private static JSONMessage die;
	private static JSONMessage play;

	public void end() {
		if (state == State.RESET) {
			return;
		}
		if (state != State.GAME) {
			return;
		}
		List<ArenaTeam> alive = new ArrayList<>();
		for (ArenaTeam team : spawns) {
			if (team.getMembers().size() > 0) {
				alive.add(team);
			}
		}
		if (alive.size() > 1) {
			return;
		}
		state = State.RESET;
		ArenaTeam win = null;
		if (alive.size() > 0) {
			win = alive.get(0);
		}
		List<String> name = new ArrayList<>();
		for (Player ps : players.keySet()) {
			if (win != null && (win.hasPlayer(ps) || (spectators.contains(ps) && players.get(ps) != null && players.get(ps).equals(win)))) {
				Reflection.sendTitle(ps, Language.messages$player$title_win.split("\n")[0], Language.messages$player$title_win.split("\n")[1]);
			} else {
				Reflection.sendTitle(ps, Language.messages$player$title_lose.split("\n")[0], Language.messages$player$title_lose.split("\n")[1]);
			}

			if (win != null && (win.hasPlayer(ps) || (spectators.contains(ps) && players.get(ps) != null && players.get(ps).equals(win)))) {
				PlayerData pd = PlayerData.get(ps);

				if (!spectators.contains(ps)) {
					ps.setHealth(20.0);
					ps.setFoodLevel(20);
					ps.setFireTicks(0);
					ps.setLevel(0);
					ps.setExp(0.0f);
					ps.setGameMode(GameMode.ADVENTURE);
					ps.setAllowFlight(true);
					ps.setFlying(true);
					ps.getInventory().clear();
					ps.getInventory().setArmorContents(null);
					if (Options.getSpec_compass().getSlot() > -1)
						ps.getInventory().setItem(Options.getSpec_compass().getSlot(), Options.getSpec_compass().getItem());
					if (Options.getSpec_settings().getSlot() > -1)
						ps.getInventory().setItem(Options.getSpec_settings().getSlot(), Options.getSpec_settings().getItem());
					if (Options.getSpec_again().getSlot() > -1)
						ps.getInventory().setItem(Options.getSpec_again().getSlot(), Options.getSpec_again().getItem());
					if (Options.getSpec_leave().getSlot() > -1)
						ps.getInventory().setItem(Options.getSpec_leave().getSlot(), Options.getSpec_leave().getItem());
					ps.updateInventory();
					ps.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 3));
					spectators.add(ps);
					for (Player ps2 : spectators) {
						ps.showPlayer(ps2);
					}
				}

				name.add(ps.getName());
				pd.addWins(mode);
			}
		}
		List<Entry<String, Integer>> tops = getTopKillers();
		for (String toBroad : Language.messages$arena$end) {
			toBroad = toBroad.contains("%center%") ? Utils.center(toBroad.replace("%center%", "")) : toBroad;

			toBroad = toBroad.replace("%winner%", (name.size() == 1 ? Bukkit.getPlayerExact(name.get(0)).getDisplayName() : win.getPrefix() + " TEAM")).replace("%p1Name%", tops.get(0).getKey())
					.replace("%p1Kills%", "" + tops.get(0).getValue()).replace("%p2Name%", tops.get(1).getKey()).replace("%p2Kills%", "" + tops.get(1).getValue()).replace("%p3Name%", tops.get(2).getKey())
					.replace("%p3Kills%", "" + tops.get(2).getValue());
			broadcastMessage(toBroad);
		}
		for (Player ps : players.keySet()) {
			int soul = 0;
			for (int i = 0; i < getKills(ps); i++) {
				if ((soul + 1) + PlayerData.get(ps).getSouls() <= 100) {
					soul++;
				}
			}
			int coins = win != null && win.hasPlayer(ps) ? Options.getCoins_win() : 5;
			int experience = win != null && win.hasPlayer(ps) ? 20 : 10;
			PlayerData pd = PlayerData.get(ps);
			double old = pd.getCoins();
			pd.addCoins(coins);
			pd.getProfile().addXp(experience);
			for (String toBroad : Language.messages$arena$rewards) {
				toBroad = toBroad.contains("%center%") ? Utils.center(toBroad.replace("%center%", "")) : toBroad;
				String place = toBroad.replace("%tCoins%", "" + ((int) (pd.getCoins() - old))).replace("%tExperience%", "" + experience).replace("%souls%", "" + soul);
				if (toBroad.contains("%tCoins%")) {
					ps.sendMessage(place);
					if (pd.getProfile().getUsing() != null && pd.getProfile().getTime() > System.currentTimeMillis()) {
						ps.sendMessage("     §6+" + ((int) (coins * (pd.getProfile().getUsing().getType().getModifier())) - coins) + " " + pd.getProfile().isBoost());
					}
				} else {
					ps.sendMessage(place);
				}
			}

			if (!Utils.getVotes().getStringList(this.name + ".votes").contains(ps.getUniqueId().toString())) {
				String json = rating.toString();
				Reflection.sendChatPacket(ps, json.toString());
			}
		}
		broadcastJSON(play);
		if (countdownTask != null) {
			countdownTask.cancel();
		}
		countdownTask = new BukkitRunnable() {
			int seconds = 10;

			@Override
			public void run() {
				if (seconds <= 0) {
					cancel();
					stop();
					return;
				} else {
					for (Player ps : getPlayersFrom(name)) {
						if (ps != null && ps.isOnline() && ps.getWorld().equals(getWorld())) {
							Utils.spawnFirework(ps.getLocation());
						}
					}
				}
				seconds--;
			}
		}.runTaskTimer(Main.getPlugin(), 0, 20);
	}

	public void stop() {
		this.state = State.RESET;
		for (Player ps : new ArrayList<>(getPlayers().keySet())) {
			remove(ps, true, false);
		}
		if (Options.getMode() == ServerMode.BUNGEE) {
			new BukkitRunnable() {
				public void run() {
					Bukkit.shutdown();
				}
			}.runTaskLater(Main.getPlugin(), 30);
			return;
		}
		this.spawns.stream().forEach(s -> s.reset());
		this.chests.clear();
		this.players.clear();
		this.spectators.clear();
		this.kills.clear();
		this.countdown = 45;
		RollBack.rollBack(this);
	}

	public void rollBack() {
		/*
		 * Iterator<Block> iterator = cuboId.iterator(); new BukkitRunnable() {
		 * int count = 0;
		 * 
		 * @Override public void run() { while (iterator.hasNext() && count <
		 * 10000) { Block block = iterator.next(); if
		 * (blocks.containsKey(block.getLocation())) { ABlock aBlock =
		 * blocks.get(block.getLocation());
		 * block.setTypeIdAndData(aBlock.getTypeId(), aBlock.getData(), false);
		 * } else if (block.getType() != Material.AIR) {
		 * block.setType(Material.AIR); } count++; } count = 0;
		 * 
		 * if (!iterator.hasNext()) { cancel(); for (Entity e :
		 * world.getEntities()) { if (cuboId.contains(e.getLocation()) &&
		 * e.getType() != EntityType.PLAYER) { e.remove(); } } state =
		 * State.WAITING; } } }.runTaskTimer(Main.getPlugin(), 10L, 1L);
		 */
		time.clear();
		health.clear();
		String name = world.getName();
		for (Player ps : Bukkit.getWorld(name).getPlayers()) {
			ps.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		Bukkit.unloadWorld(name, false);
		countdownTask.cancel();
		countdownTask = null;
		File world = new File(name);
		File newWorld = new File("plugins/GremorySkywars/maps/" + name);
		try {
			Utils.copyDirectory(newWorld, world);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.warn("Failed to copy world to arena '" + name + "'");
			return;
		}
		try {
			WorldCreator wc = new WorldCreator(name);
			wc.generateStructures(false);
			World map = wc.createWorld();
			map.setAutoSave(false);
			map.setKeepSpawnInMemory(false);
			map.setAnimalSpawnLimit(0);
			map.setGameRuleValue("doMobSpawning", "false");
			map.setGameRuleValue("doDaylightCycle", "false");
			map.setGameRuleValue("mobGriefing", "false");
			map.setTime(0);
			map.setStorm(false);
			map.setThundering(false);
			chests.clear();
			new BukkitRunnable() {
				@Override
				public void run() {
					for (String loc : config.getStringList("chests")) {
						if (loc.split(" : ").length > 5) {
							chests.put(Utils.unserializeLocation(loc), Chest.getChest(loc.split(" : ")[6]) == null ? Chest.getChest("normal") : Chest.getChest(loc.split(" : ")[6]));
						}
					}
					Arena.this.world = map;
					state = State.WAITING;
					blocks.clear();
					if (config.contains("schematic") && mode != Mode.SOLO) {
						schematic = Utils.unserializeLocation(config.getString("schematic"));
						Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), new Runnable() {

							@Override
							public void run() {
								Schematic.generateAsync(schematic, blocks);
							}
						}, 1);
					}
				}
			}.runTaskLater(Main.getPlugin(), 20);
		} catch (Exception e) {
			Logger.warn("Failed to generate world to arena '" + name + "'");
			e.printStackTrace();
			return;
		}
		/*
		 * new BukkitRunnable() { public void run() {
		 * Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable() {
		 * 
		 * @Override public void run() { File world = new File(name); File
		 * newWorld = new File("plugins/GremorySkywars/maps/" + name); try {
		 * Utils.copyDirectory(newWorld, world); } catch (Exception e) {
		 * e.printStackTrace(); Logger.warn("Failed to copy world to arena '" +
		 * name + "'"); return; } try { WorldCreator wc = new
		 * WorldCreator(name); wc.generateStructures(false); World map =
		 * wc.createWorld(); map.setAutoSave(false);
		 * map.setKeepSpawnInMemory(false); map.setAnimalSpawnLimit(0);
		 * map.setGameRuleValue("doMobSpawning", "false");
		 * map.setGameRuleValue("doDaylightCycle", "false");
		 * map.setGameRuleValue("mobGriefing", "false"); map.setTime(0);
		 * map.setStorm(false); map.setThundering(false); chests.clear(); new
		 * BukkitRunnable() {
		 * 
		 * @Override public void run() { for (String loc :
		 * config.getStringList("chests")) { if (loc.split(" : ").length > 5) {
		 * chests.put(Utils.unserializeLocation(loc),
		 * Chest.getChest(loc.split(" : ")[6]) == null ?
		 * Chest.getChest("normal") : Chest.getChest(loc.split(" : ")[6])); } }
		 * Arena.this.world = map; state = State.WAITING; }
		 * }.runTaskLater(Main.getPlugin(), 20); } catch (Exception e) {
		 * Logger.warn("Failed to generate world to arena '" + name + "'");
		 * e.printStackTrace(); return; } } }); }
		 * }.runTaskLaterAsynchronously(Main.getPlugin(), 20);
		 */
	}

	public void vote(Player p, int number) {
		List<String> list = Utils.getVotes().getStringList(name + ".votes");
		if (!list.contains(p.getUniqueId().toString())) {
			list.add(p.getUniqueId().toString());
			Utils.getVotes().set(name + ".votes", list);

			this.votes += number;
			Double myRating = (double) list.size() / (double) votes;
			if (String.valueOf(myRating).length() > 3) {
				this.rate = Double.parseDouble(String.valueOf(myRating).substring(0, 3));
			}

			Utils.getVotes().set(name + ".rate", rate);
			Utils.getVotes().set(name + ".voted", votes);
			p.sendMessage(Language.messages$arena$vote_register);
		}
	}

	public void fillChests() {
		for (Location loc : getChests().keySet()) {
			Chest chest = getChests().get(loc);
			chest.fill(loc);
		}
	}

	public void refillChests() {
		for (Location loc : getChests().keySet()) {
			Chest chest = getChests().get(loc);
			chest.refill(loc);
			Chest refill = Chest.getChest(chest.getRefill());
			if (refill != null) {
				chests.put(loc, refill);
			}
		}
		broadcastTitle("", Language.messages$arena$refill_title);
		if (Main.getChest() != null) {
			broadcastSound(Main.getChest());
		}
	}

	public void broadcastAction(String message) {
		JSONMessage msg = new JSONMessage(message);
		for (Player ps : getPlayers().keySet()) {
			if (spectators.contains(ps)) {
				continue;
			}
			Reflection.sendChatAction(ps, msg.toString());
		}
	}

	public void broadcastMessage(String message) {
		for (Player ps : players.keySet()) {
			ps.sendMessage(message.replace("&", "§"));
		}
	}

	public void broadcastJSON(JSONMessage message) {
		for (Player ps : players.keySet()) {
			String json = message.toString();
			Reflection.sendChatPacket(ps, json.toString());
		}
	}

	public void broadcastTitle(String title, String subtitle) {
		for (Player ps : players.keySet()) {
			Reflection.sendTitle(ps, title, subtitle);
		}
	}

	public void broadcastSound(Sound sound) {
		for (Player ps : players.keySet()) {
			ps.playSound(ps.getLocation(), sound, 1.0f, 1.0f);
		}
	}

	public void setSchematic(Location location) {
		this.schematic = location;
		config.set("schematic", Utils.serializeLocation(location));
	}

	public void addKills(Player p) {
		kills.put(p.getName(), getKills(p) + 1);
	}

	public void scan(World world) {
		for (Chunk chunk : world.getLoadedChunks()) {
			for (BlockState state : chunk.getTileEntities()) {
				if (state instanceof Beacon) {
					state.getBlock().setType(Material.AIR);
					addSpawn(state.getLocation().add(0.5, 1, 0.5));
				}
			}
		}
	}

	public void addSpawn(Location loc) {
		List<String> strings = getConfig().getStringList("spawns");
		strings.add(Utils.serializeLocation(loc));
		config.set("spawns", strings);
		spawns.add(mode == Mode.SOLO ? new ArenaTeam(this, loc) : new ArenaTeam(this, loc, ""));
	}

	public List<Entry<String, Integer>> getTopKillers() {
		int i = 0;
		while (kills.size() < 3) {
			this.kills.put("None" + (++i), 0);
		}

		LinkedList<Entry<String, Integer>> linked = new LinkedList<>(getKills().entrySet());
		Collections.sort(linked, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> entry, Entry<String, Integer> entry2) {
				return Integer.compare(entry2.getValue(), entry.getValue());
			}
		});
		return linked;
	}

	public int getKills(Player p) {
		return kills.get(p.getName()) != null ? kills.get(p.getName()) : 0;
	}

	public int playersAlive() {
		int count = 0;
		for (Player ps : getPlayers().keySet()) {
			if (!spectators.contains(ps)) {
				count++;
			}
		}
		return count;
	}

	public String formatTime(int i) {
		return i / 60 + ":" + (i % 60 < 10 ? "0" : "") + i % 60;
	}

	public int getHighest(Integer[] collection, int i) {
		for (Integer integer : collection) {
			if (i >= integer) {
				return integer;
			}
		}
		return 0;
	}

	public String getEvent(int i) {
		for (int refill : Options.getRefillTimes()) {
			if (refill == i) {
				return "Refill";
			}
		}
		return "End";
	}

	private ArenaTeam getAvaibleTeam(int slots) {
		for (ArenaTeam team : spawns) {
			if (team.getMembers().size() + slots <= getMode().getSize()) {
				return team;
			}
		}
		return null;
	}

	public ArenaTeam getTeam(Player p) {
		return players.get(p);
	}

	public int getAliveTeams() {
		int count = 0;
		for (ArenaTeam team : spawns) {
			if (team.getMembers().size() > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param Arena
	 *            Controller
	 * 
	 */

	public static Arena getFrom() {
		List<String> list = new ArrayList<String>();
		for (Arena a : getArenas()) {
			if (a.getState().isAvaible() && a.getPlayers().size() < a.getSpawns().size() * a.getMode().getSize()) {
				list.add(a.getWorld().getName() + " " + a.getPlayers().size());
			}
		}

		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int one = Integer.parseInt(a.split(" ")[1]);
				int two = Integer.parseInt(b.split(" ")[1]);
				return Integer.compare(one, two);
			}
		});
		return list.size() - 1 < 0 ? null : Arena.getArena(Bukkit.getWorld(list.get(list.size() - 1).split(" ")[0]));
	}

	public static int getCountPlaying(Mode mode, Type type) {
		int queue = 0;
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			for (Arena a : getArenas()) {
				if (a.getMode().equals(mode) && a.getType().equals(type)) {
					queue += a.getPlayers().size();
				}
			}
		} else {
			for (String s : Options.getRooms_bungee()) {
				String ip = s.split(" : ")[0];
				int port = Integer.parseInt(s.split(" : ")[1]);
				Ping ping = new Ping(ip, port, 1000);
				if (ping.getMotd() == null) {
					continue;
				}
				String motd = ping.getMotd();
				if (motd.split(":").length <= 1) {
					continue;
				}

				try {
					Mode mode2 = Mode.get(motd.split(":")[1]);
					Type type2 = Type.get(motd.split(":")[2]);
					if (mode2.equals(mode) && type2.equals(type)) {
						queue += ping.getOnline();
					}
				} catch (Exception e) {
				}
			}
		}
		return queue;
	}

	public static int getCount(Mode mode, Type type) {
		int queue = 0;
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			for (Arena a : getList(mode, type)) {
				queue += a.getPlayers().size();
			}
		} else {
			for (String string : getListBungee(mode, type)) {
				queue += new Ping(string.split(" : ")[0], Integer.parseInt(string.split(" : ")[1]), 300).getOnline();
			}
		}
		return queue;
	}

	public static int getList(Mode mode, Type type, String name) {
		int count = 0;
		for (Arena a : getArenas()) {
			if (a.getMode() == mode && a.getType() == type && a.getState().isAvaible() && a.getPlayers().size() < a.getSpawns().size() * a.getMode().getSize()) {
				count++;
			}
		}

		return count;
	}

	public static List<Arena> getList(Mode mode, Type type) {
		List<Arena> list = new ArrayList<>();
		for (Arena a : getArenas()) {
			if (a.getMode() == mode && a.getType() == type && a.getState().isAvaible() && a.getPlayers().size() < a.getSpawns().size() * a.getMode().getSize()) {
				list.add(a);
			}
		}

		return list;
	}

	public static Arena getFrom(Mode mode, Type type) {
		List<String> list = new ArrayList<String>();
		for (Arena a : getArenas()) {
			if (a.getMode() == mode && a.getType() == type && a.getState().isAvaible() && a.getPlayers().size() < a.getSpawns().size() * a.getMode().getSize()) {
				list.add(a.getWorld().getName() + " " + a.getPlayers().size());
			}
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int one = Integer.parseInt(a.split(" ")[1]);
				int two = Integer.parseInt(b.split(" ")[1]);
				return Integer.compare(one, two);
			}
		});

		return list.size() - 1 < 0 ? null : Arena.getArena(Bukkit.getWorld(list.get(list.size() - 1).split(" ")[0]));
	}

	public static Arena getFromRandom(Mode mode, Type type) {
		List<Arena> list = new ArrayList<>();
		for (Arena a : arenas.values()) {
			if (a.getMode() == mode && a.getType() == type && a.getState().isAvaible() && a.getPlayers().size() < a.getSpawns().size() * a.getMode().getSize()) {
				list.add(a);
			}
		}
		return list.size() - 1 < 0 ? null : list.get(new Random().nextInt(list.size()));
	}

	public static String getBungee() {
		List<String> list = new ArrayList<String>();
		for (String s : Options.getRooms_bungee()) {
			String ip = s.split(" : ")[0];
			int port = Integer.parseInt(s.split(" : ")[1]);
			String serverName = s.split(" : ")[2];
			Ping ping = new Ping(ip, port, 1000);
			if (ping.getMotd() == null) {
				continue;
			}
			String motd = ping.getMotd();
			if (motd.split(":").length <= 1) {
				continue;
			}

			State state = State.valueOf(motd.split(":")[0].toUpperCase());
			if (ping.getOnline() <= ping.getMax() && state.isAvaible()) {
				list.add(serverName + " " + ping.getOnline());
			}
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int one = Integer.parseInt(a.split(" ")[1]);
				int two = Integer.parseInt(b.split(" ")[1]);
				return Integer.compare(one, two);
			}
		});

		return list.size() - 1 < 0 ? null : list.get(list.size() - 1).split(" ")[0];
	}

	public static int getListBungee(Mode mode, Type type, String name) {
		int count = 0;
		for (String s : Options.getRooms_bungee()) {
			String ip = s.split(" : ")[0];
			int port = Integer.parseInt(s.split(" : ")[1]);
			Ping ping = new Ping(ip, port, 1000);
			if (ping.getMotd() == null) {
				continue;
			}
			String motd = ping.getMotd();
			if (motd.split(":").length <= 1) {
				continue;
			}

			try {
				if (motd.split(":")[3].equals(name)) {

					State state = State.valueOf(motd.split(":")[0].toUpperCase());
					Mode mode2 = Mode.valueOf(motd.split(":")[1].toUpperCase());
					Type type2 = Type.valueOf(motd.split(":")[2].toUpperCase());
					if (mode2.equals(mode) && type2.equals(type) && ping.getOnline() < ping.getMax() && state.isAvaible()) {
						count++;
					}
				}
			} catch (Exception e) {
			}
		}

		return count;
	}

	public static List<String> getListBungee(Mode mode, Type type) {
		List<String> list = new ArrayList<String>();
		for (String s : Options.getRooms_bungee()) {
			String ip = s.split(" : ")[0];
			int port = Integer.parseInt(s.split(" : ")[1]);
			String serverName = s.split(" : ")[2];
			Ping ping = new Ping(ip, port, 1000);
			if (ping.getMotd() == null) {
				continue;
			}
			String motd = ping.getMotd();
			if (motd.split(":").length <= 1) {
				continue;
			}

			try {
				State state = State.valueOf(motd.split(":")[0].toUpperCase());
				Mode mode2 = Mode.get(motd.split(":")[1]);
				Type type2 = Type.get(motd.split(":")[2]);
				if (mode2.equals(mode) && type2.equals(type) && ping.getOnline() < ping.getMax() && state.isAvaible()) {
					list.add(ip + " : " + port + " : " + serverName);
				}
			} catch (Exception e) {
			}
		}
		return list;
	}

	public static String getBungee(Mode mode, Type type) {
		List<String> list = new ArrayList<String>();
		for (String s : Options.getRooms_bungee()) {
			String ip = s.split(" : ")[0];
			int port = Integer.parseInt(s.split(" : ")[1]);
			String serverName = s.split(" : ")[2];
			Ping ping = new Ping(ip, port, 1000);
			if (ping.getMotd() == null) {
				continue;
			}
			String motd = ping.getMotd();
			if (motd.split(":").length <= 1) {
				continue;
			}
			State state = State.valueOf(motd.split(":")[0].toUpperCase());
			Mode mode2 = Mode.valueOf(motd.split(":")[1].toUpperCase());
			Type type2 = Type.valueOf(motd.split(":")[2].toUpperCase());
			if (mode2.equals(mode) && type2.equals(type) && ping.getOnline() <= ping.getMax() && state.isAvaible()) {
				list.add(serverName + " " + ping.getOnline());
			}
		}

		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int one = Integer.parseInt(a.split(" ")[1]);
				int two = Integer.parseInt(b.split(" ")[1]);
				return Integer.compare(one, two);
			}
		});
		return list.size() - 1 < 0 ? null : list.get(list.size() - 1).split(" ")[0];
	}

	public static int getAll() {
		if (Options.getMode() == ServerMode.BUNGEE) {
			int players = Bukkit.getOnlinePlayers().size();
			for (String s : Options.getRooms_bungee()) {
				String ip = s.split(" : ")[0];
				int port = Integer.parseInt(s.split(" : ")[1]);
				Ping ping = new Ping(ip, port, 1000);
				if (ping.getMotd() == null) {
					continue;
				}
				players += ping.getOnline();
			}
			return players;
		}
		return Bukkit.getOnlinePlayers().size();
	}

	public static List<Player> getPlayersFrom(List<String> names) {
		List<Player> list = new ArrayList<>();
		for (String name : names) {
			list.add(Bukkit.getPlayerExact(name));
		}
		return list;
	}

	public static List<Player> getPlayersFrom(Set<OfflinePlayer> players) {
		List<Player> list = new ArrayList<>();
		for (OfflinePlayer of : players) {
			if (of instanceof Player) {
				list.add((Player) of);
			}
		}
		return list;
	}

	private static Map<String, Arena> arenas = new HashMap<>();

	public static void register() {
		rating = new JSONMessage(Language.messages$player$vote_message);
		ChatExtra five = new ChatExtra(" §a[5]");
		five.addClickEvent(ClickEventType.RUN_COMMAND, "/vote 5");
		five.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$vote_format.replace("%number%", "[5]"));
		ChatExtra four = new ChatExtra(" §a[4]");
		four.addClickEvent(ClickEventType.RUN_COMMAND, "/vote 4");
		four.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$vote_format.replace("%number%", "[4]"));
		ChatExtra three = new ChatExtra(" §a[3]");
		three.addClickEvent(ClickEventType.RUN_COMMAND, "/vote 3");
		three.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$vote_format.replace("%number%", "[3]"));
		ChatExtra two = new ChatExtra(" §a[2]");
		two.addClickEvent(ClickEventType.RUN_COMMAND, "/vote 2");
		two.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$vote_format.replace("%number%", "[2]"));
		ChatExtra one = new ChatExtra(" §c[1]");
		one.addClickEvent(ClickEventType.RUN_COMMAND, "/vote 1");
		one.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$vote_format.replace("%number%", "[1]"));
		rating.addExtra(five);
		rating.addExtra(four);
		rating.addExtra(three);
		rating.addExtra(two);
		rating.addExtra(one);
		play = new JSONMessage(Language.messages$player$want_play_again);
		ChatExtra extraClick = new ChatExtra(Language.messages$player$want_play_again_click);
		extraClick.addClickEvent(ClickEventType.RUN_COMMAND, "/swplayagain");
		extraClick.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$want_play_again_hover);
		play.addExtra(extraClick);
		die = new JSONMessage(Language.messages$player$want_play_again_died);
		ChatExtra extraClick2 = new ChatExtra(Language.messages$player$want_play_again_click);
		extraClick2.addClickEvent(ClickEventType.RUN_COMMAND, "/swplayagain");
		extraClick2.addHoverEvent(HoverEventType.SHOW_TEXT, Language.messages$player$want_play_again_hover);
		die.addExtra(extraClick2);
		File folder = new File("plugins/GremorySkywars/arenas");
		File folder2 = new File("plugins/GremorySkywars/maps");
		if (!folder.exists())
			folder.mkdir();
		if (!folder2.exists())
			folder2.mkdir();
		for (File f : folder.listFiles()) {
			if (!f.isDirectory() && f.getName().contains(".yml")) {
				String name = f.getName().replace(".yml", "");
				File mapa = new File(folder2.toString() + "/" + name);
				FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
				if (!mapa.exists()) {
					Logger.warn("Failed to locate map " + name + " to arena " + cf.getString("name"));
					continue;
				}
				World map;
				try {
					if (Bukkit.getWorld(name) != null)
						Bukkit.unloadWorld(name, false);
					Utils.deleteFile(new File(name));
					Utils.copyDirectory(mapa, new File(name));
					WorldCreator wc = new WorldCreator(name);
					wc.generateStructures(false);
					map = wc.createWorld();
					map.setAutoSave(false);
					map.setKeepSpawnInMemory(false);
					map.setAnimalSpawnLimit(1);
					map.setGameRuleValue("doMobSpawning", "false");
					map.setGameRuleValue("doDaylightCycle", "false");
					map.setGameRuleValue("mobGriefing", "false");
					map.setStorm(false);
					map.setThundering(false);
					map.setTime(0);
				} catch (Exception e) {
					Logger.warn("Failed to import map " + name + " to arena " + cf.getString("name"));
					continue;
				}
				new Arena(cf.getString("name"), map);
			}
		}
		Logger.info("Loaded " + arenas.size() + " arena(s)!");
	}

	public static Arena getArena(World w) {
		for (Arena arena : getArenas())
			if (arena.getWorld().getName().equals(w.getName()))
				return arena;
		return null;
	}

	public static List<Arena> getArenas() {
		return new ArrayList<>(arenas.values());
	}

	public static Arena getArena0() {
		return getArenas().size() > 0 ? getArenas().get(0) : null;
	}

	@AllArgsConstructor
	@Getter
	public static class ABlock {

		private int typeId;
		private byte data;
	}
}
