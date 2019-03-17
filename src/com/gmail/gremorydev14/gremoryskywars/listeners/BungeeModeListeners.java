package com.gmail.gremorydev14.gremoryskywars.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.arena.CuboId;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.menus.InsaneMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsMegaMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsSoloMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsTeamMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SpectatorSettings;
import com.gmail.gremorydev14.gremoryskywars.menus.TeleportMenu;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.profile.Rank;

@SuppressWarnings("all")
public class BungeeModeListeners implements Listener {
	
	@EventHandler
	private void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		Arena a = Arena.getArena0();
		if (a != null) {
			if (!a.getState().isAvaible()) {
				e.disallow(Result.KICK_OTHER, Language.messages$arena$already_ingame);
				return;
			}
			if (a.getPlayers().size() >= a.getSpawns().size() * a.getMode().getSize()) {
				e.disallow(Result.KICK_OTHER, Language.messages$arena$full);
				return;
			}
		}
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();

		PlayerData.create(p);
		Rank.getRank(p).apply(p);
		Arena a = Arena.getArena0();
		if (a != null) {
			a.add(p);
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(null);
		Arena a = Arena.getArena0();
		if (a != null) {
			PlayerData pd = PlayerData.get(p);
			if (pd != null) {
				if (pd.getArena() != null) {
					pd.getArena().remove(p, pd.getArena().getSpectators().contains(p), true);
				}
				pd.saveAsync();
				PlayerData.remove(p);
			}
		}
	}

	@EventHandler
	private void onMotd(ServerListPingEvent e) {
		Arena a = Arena.getArena0();
		if (a != null) {
			e.setMaxPlayers(a.getSpawns().size() * a.getMode().getSize());
			e.setMotd(a.getState().toString() + ":" + a.getMode().toString() + ":" + a.getType().toString() + ":" + a.getName() + ":" + a.getRate());
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();

		PlayerData pd = PlayerData.get(p);
		if (MultiArenaListeners.create.containsKey(e.getPlayer())) {
			if (!MultiArenaListeners.create.get(e.getPlayer()).equals(p.getWorld().getName()))
				return;
			if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				String dis = p.getItemInHand().getItemMeta().getDisplayName();
				if (dis.equals("§eBorder")) {
					e.setCancelled(true);
					if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
						MultiArenaListeners.locs.get(e.getPlayer())[0] = e.getClickedBlock().getLocation();
						e.getPlayer().sendMessage("§aSelected!");
					} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						MultiArenaListeners.locs.get(e.getPlayer())[1] = e.getClickedBlock().getLocation();
						e.getPlayer().sendMessage("§aSelected!");
					}
				} else if (dis.equals("§eComplete")) {
					e.setCancelled(true);
					Location[] location = MultiArenaListeners.locs.get(e.getPlayer());
					if (location[0] != null && location[1] != null) {
						World world = Bukkit.getWorld(MultiArenaListeners.create.get(e.getPlayer()));
						if (world == null) {
							e.getPlayer().sendMessage("§cInvalid world!");
							return;
						}
						p.getInventory().clear();
						p.updateInventory();
						CuboId cuboId = new CuboId(location[0], location[1]);
						long l = System.currentTimeMillis();
						SettingsManager sm = SettingsManager.getConfig(world.getName(), "plugins/GremorySkywars/arenas");
						sm.set("mode", MultiArenaListeners.mode.get(p).toString());
						sm.set("type", MultiArenaListeners.type.get(p).toString());
						sm.set("name", world.getName());
						sm.set("server", "Game" + Arena.getArenas().size() + 1);
						sm.set("min-players", 2);
						sm.set("cubo-id", cuboId.toString());
						List<String> spawns = new ArrayList<>();
						List<String> chests = new ArrayList<>();
						for (Iterator<Block> itr = cuboId.iterator(); itr.hasNext();) {
							Block b = itr.next();
							if (b.getType() == Material.CHEST)
								chests.add(Utils.serializeLocation(b.getLocation()) + " : normal");
							else if (b.getType() == Material.BEACON) {
								spawns.add(Utils.serializeLocation(b.getLocation().add(0.5, 1, 0.5)));
								b.setType(Material.AIR);
							}
						}
						sm.set("spawns", spawns);
						sm.set("chests", chests);
						world.save();
						new BukkitRunnable() {
							long demora = (System.currentTimeMillis() - l);

							@Override
							public void run() {
								Utils.copyDirectory(new File(world.getName()), new File("plugins/GremorySkywars/maps/" + world.getName()));
								new Arena(world.getName(), world);
								MultiArenaListeners.locs.remove(e.getPlayer());
								MultiArenaListeners.create.remove(e.getPlayer());
								MultiArenaListeners.inv.remove(e.getPlayer());
								e.getPlayer().updateInventory();
								e.getPlayer().sendMessage("§aArena created after " + demora + "ms!");
								e.getPlayer().sendMessage("§aSpawns scaneds: " + spawns.size() + "!");
								e.getPlayer().sendMessage("§aChests scaneds: " + chests.size() + "!");
							}
						}.runTaskLater(Main.getPlugin(), 100);
					} else {
						e.getPlayer().sendMessage("§cSelect border!");
					}
				} else if (dis.equals("§cCancel")) {
					e.setCancelled(true);
					MultiArenaListeners.locs.remove(p);
					MultiArenaListeners.create.remove(p);
					p.getInventory().setContents(MultiArenaListeners.inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (MultiArenaListeners.selected.containsKey(e.getPlayer())) {
			Arena arena = MultiArenaListeners.selected.get(e.getPlayer());
			if (!p.getWorld().equals(arena.getWorld()))
				return;
			if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				String dis = p.getItemInHand().getItemMeta().getDisplayName();
				if (dis.equals("§eScan")) {
					int size = arena.getSpawns().size();
					e.setCancelled(true);
					arena.scan(p.getWorld());
					p.sendMessage("§aAdded " + (arena.getSpawns().size() - size) + " spawn(s)!");
					p.getInventory().setItem(8, ItemUtils.createItem("STAINED_CLAY:5 : 1 : name=&aSpawns: " + arena.getSpawns().size()));
				} else if (dis.equals("§eAdd")) {
					e.setCancelled(true);
					arena.addSpawn(p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5));
					p.sendMessage("§aSpawn added!");
					p.getInventory().setItem(8, ItemUtils.createItem("STAINED_CLAY:5 : 1 : name=&aSpawns: " + arena.getSpawns().size()));
				} else if (dis.equals("§cQuit")) {
					e.setCancelled(true);
					MultiArenaListeners.selected.remove(p);
					MultiArenaListeners.locs.remove(p);
					p.getInventory().setContents(MultiArenaListeners.inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (MultiArenaListeners.chestEdit.containsKey(e.getPlayer())) {
			Arena arena = Arena.getArena(e.getPlayer().getWorld());
			if (arena == null)
				return;
			if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				String dis = p.getItemInHand().getItemMeta().getDisplayName();
				if (dis.equals("§eWand")) {
					e.setCancelled(true);
					if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
						Chest chest = MultiArenaListeners.chestEdit.get(e.getPlayer());
						if (chest != null) {
							long start = System.currentTimeMillis();
							arena.getChests().put(e.getClickedBlock().getLocation(), chest);
							SettingsManager sm = arena.getConfig();
							List<String> strings = sm.getStringList("chests");

							boolean added = false;
							for (String string : strings) {
								if (Utils.unserializeLocation(string).equals(e.getClickedBlock().getLocation())) {
									strings.remove(string);
									strings.add(Utils.serializeLocation(e.getClickedBlock().getLocation()) + " : " + chest.getName().toLowerCase());
									added = true;
									break;
								}
							}
							if (!added)
								strings.add(Utils.serializeLocation(e.getClickedBlock().getLocation()) + " : " + chest.getName());
							sm.set("chests", strings);
							p.sendMessage("§aType changed sucessfully, after: " + (System.currentTimeMillis() - start) + "ms");
						} else {
							p.sendMessage("§4WARNING:");
							p.sendMessage("§cType of chest selected is invalid!");
						}
					} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (arena.getChests().get(e.getClickedBlock().getLocation()) != null) {
							Chest chest = arena.getChests().get(e.getClickedBlock().getLocation());
							if (chest == null) {
								arena.getChests().put(e.getClickedBlock().getLocation(), Chest.getChests().get(0));
								chest = Chest.getChests().get(0);
							}
							p.sendMessage("§e§lINFO:");
							p.sendMessage("§eArena: §b" + arena.getName());
							p.sendMessage("§eType: §b" + chest.getName());
							p.sendMessage("§cTo change type use left click");
						} else {
							p.sendMessage("§cThis chest not exists in this arena");
							p.sendMessage("§cIf need to add this chest use left click!");
						}
					}
				} else if (dis.equals("§cQuit")) {
					e.setCancelled(true);
					MultiArenaListeners.chestEdit.remove(p);
					MultiArenaListeners.locs.remove(p);
					p.getInventory().setContents(MultiArenaListeners.inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (pd != null) {
			if (pd.getArena() != null) {
				if (e.getAction().name().contains("RIGHT")) {
					if (pd.getArena().getState().isAvaible()) {
						e.setCancelled(true);
						if (item.equals(Options.getWait_kits().getItem())) {
							if (pd.getArena().getMode() == Mode.SOLO)
								new KitsSoloMenu(p);
							else if (pd.getArena().getMode() == Mode.TEAM)
								new KitsTeamMenu(p);
							else
								new KitsMegaMenu(p);
						} else if (item.equals(Options.getWait_leave().getItem())) {
							pd.getArena().remove(pd.getPlayer(), false, false);
						} else if (item.equals(Options.getWait_insane().getItem())) {
							if (p.hasPermission("gremoryskywars.insaneoptions")) {
								new InsaneMenu(pd);
							} else {
								p.sendMessage(Language.messages$player$insane_permission);
							}
						}
					} else {
						if (pd.getArena().getSpectators().contains(pd.getPlayer())) {
							e.setCancelled(true);
							if (item.equals(Options.getSpec_compass().getItem())) {
								new TeleportMenu(pd);
							} else if (item.equals(Options.getSpec_settings().getItem())) {
								new SpectatorSettings(p);
							} else if (item.equals(Options.getSpec_again().getItem())) {
								String server = Arena.getBungee(pd.getArena().getMode(), pd.getArena().getType());
								if (server != null)
									Utils.sendToServer(p, server);
								else
									pd.getPlayer().sendMessage(Language.messages$player$play_again_null);
							} else if (item.equals(Options.getSpec_leave().getItem())) {
								pd.getArena().remove(pd.getPlayer(), true, false);
							}
						}
						return;
					}

					if (item.getType() == Material.COMPASS) {
						e.setCancelled(true);
						Player c = null;
						for (Entity en : p.getNearbyEntities(300, 128, 300)) {
							if (en instanceof Player) {
								if (en.getLocation().distance(p.getLocation()) > 30 && !pd.getArena().getSpectators().contains(p) && pd.getArena().getTeam(p) != null && !pd.getArena().getTeam(p).hasPlayer((Player) en)) {
									c = (Player) en;
									break;
								}
							}
						}
						
						if (c == null) {
							p.sendMessage(Language.messages$player$compass_null);
							p.setCompassTarget(p.getWorld().getSpawnLocation());
						} else {
							p.sendMessage(Language.messages$player$compass_track.replace("%pName", c.getName()).replace("%pDName%", c.getDisplayName()));
							p.setCompassTarget(c.getLocation());
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		PlayerData pd = PlayerData.get(p);

		if (pd != null && pd.getArena() != null) {
			e.setDeathMessage(null);
			e.setDroppedExp(0);
			Arena arena = pd.getArena();
			Player killer = p.getKiller();
			p.setHealth(20.0);
			p.setFoodLevel(20);
			if (killer == null) {
				if (!pd.getLastHit().isEmpty()) {
					String name = (String) pd.getLastHit().keySet().toArray()[0];
					if ((System.currentTimeMillis() - pd.getLastHit().get(name)) / 1000 < 15)
						killer = Bukkit.getPlayer(name);
					else
						pd.getLastHit().clear();
				}
			}
			String message = "";
			if (killer == null || killer.equals(p))
				message = Language.messages$arena$player_suicide.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName());
			else if (killer.equals(p))
				message = Language.messages$arena$player_suicide.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName());
			else
				message = Language.messages$arena$player_killed.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()).replace("%kName%", killer.getName()).replace("%kDName%", killer.getDisplayName());
			if (killer != null && !killer.getName().equals(p.getName())) {
				PlayerData pd2 = PlayerData.get(killer);
				arena.addKills(killer);
				int coins = Options.getCoins_kill();
				int soul = pd2.getSouls() >= 100 ? 0 : 1;
				pd2.addKills(arena.getMode());
				double old = pd2.getCoins();
				pd2.addCoins(coins);
				pd2.addSouls(soul);
				killer.sendMessage(Language.messages$player$kill_reward
						.replace("%coins%", +((int) pd2.getCoins() - old) + (pd.getProfile().getUsing() != null && pd.getProfile().getTime() > System.currentTimeMillis() ? " §6" + pd.getProfile().isBoost() : ""))
						.replace("%souls%", (soul != 0 ? " and §b1 soul" : "")));
			}
			arena.kill(p, message);
			new BukkitRunnable() {
				public void run() {
					p.setFireTicks(0);
					p.setLevel(0);
					p.setExp(0.0f);
					p.setGameMode(GameMode.ADVENTURE);
					p.setAllowFlight(true);
					p.setFlying(true);
					if (Options.getSpec_compass().getSlot() > -1)
						p.getInventory().setItem(Options.getSpec_compass().getSlot(), Options.getSpec_compass().getItem());
					if (Options.getSpec_settings().getSlot() > -1)
						p.getInventory().setItem(Options.getSpec_settings().getSlot(), Options.getSpec_settings().getItem());
					if (Options.getSpec_again().getSlot() > -1)
						p.getInventory().setItem(Options.getSpec_again().getSlot(), Options.getSpec_again().getItem());
					if (Options.getSpec_leave().getSlot() > -1)
						p.getInventory().setItem(Options.getSpec_leave().getSlot(), Options.getSpec_leave().getItem());
					p.updateInventory();
					p.teleport(arena.getPlayers().get(p).getSpawnLocation());
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 3));
				}
			}.runTaskLater(Main.getPlugin(), 5);
		}
	}

	@EventHandler
	private void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd != null && pd.getArena() != null) {
			e.setRespawnLocation(pd.getArena().getCuboId().getRandomLocation());
			p.setFireTicks(0);
			p.setLevel(0);
			p.setExp(0.0f);
			p.setAllowFlight(true);
			p.setFlying(true);
			if (Options.getSpec_compass().getSlot() > -1)
				p.getInventory().setItem(Options.getSpec_compass().getSlot(), Options.getSpec_compass().getItem());
			if (Options.getSpec_settings().getSlot() > -1)
				p.getInventory().setItem(Options.getSpec_settings().getSlot(), Options.getSpec_settings().getItem());
			if (Options.getSpec_again().getSlot() > -1)
				p.getInventory().setItem(Options.getSpec_again().getSlot(), Options.getSpec_again().getItem());
			if (Options.getSpec_leave().getSlot() > -1)
				p.getInventory().setItem(Options.getSpec_leave().getSlot(), Options.getSpec_leave().getItem());
			p.updateInventory();
			new BukkitRunnable() {
				public void run() {
					p.setGameMode(GameMode.CREATIVE);
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 3));
				}
			}.runTaskLater(Main.getPlugin(), 5);
		}
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent e) {
		String message = e.getMessage().replace("%", "");
		PlayerData pd = PlayerData.get(e.getPlayer());

		if (pd != null) {
			if (pd.getArena() != null) {
				Arena a = pd.getArena();

				if (a.getSpectators().contains(pd.getPlayer())) {
					e.setFormat(Language.messages$chat$format_spectator.replace("%message%", message).replace("%pDName%", pd.getPlayer().getDisplayName()));
					e.getRecipients().clear();
					e.getRecipients().addAll(pd.getArena().getSpectators());
					return;
				}

				if (a.getMode().getSize() > 1) {
					e.setFormat(Language.messages$chat$format_team.replace("%message%", message).replace("%pDName%", pd.getPlayer().getDisplayName()));
					e.getRecipients().clear();
					e.getRecipients().addAll(a.getTeam(pd.getPlayer()) != null ? a.getTeam(pd.getPlayer()).getMembers() : Arrays.asList(pd.getPlayer()));
				} else {
					e.setFormat(Language.messages$chat$format.replace("%message%", message).replace("%pDName%", pd.getPlayer().getDisplayName()));
					e.getRecipients().clear();
					e.getRecipients().addAll(pd.getPlayer().getWorld().getPlayers());
					e.getRecipients().removeAll(pd.getArena().getSpectators());
				}
			}
		}
	}

	@EventHandler
	private void onCommand(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().split(" ")[0].toLowerCase().replace("/", "");
		if (cmd.equals("vote")) {
			if (e.getMessage().split(" ").length <= 1)
				return;
			PlayerData pd = PlayerData.get(e.getPlayer());
			if (pd != null && pd.getArena() != null && pd.getArena().getSpectators().contains(pd.getPlayer())) {
				pd.getArena().vote(pd.getPlayer(), Integer.parseInt(e.getMessage().split(" ")[1]));
				e.setCancelled(true);
			}
		}
		if (cmd.equals("g")) {
			String message = "";
			for (int i = 1; i < e.getMessage().split(" ").length; i++)
				message += e.getMessage().split(" ")[i] + " ";
			if (message.equals(""))
				return;
			PlayerData pd = PlayerData.get(e.getPlayer());
			if (pd != null && pd.getArena() != null && pd.getArena().getMode().getSize() > 1 && !pd.getArena().getSpectators().contains(pd.getPlayer())) {
				List<Player> players = pd.getPlayer().getWorld().getPlayers();
				players.removeAll(pd.getArena().getSpectators());
				for (Player ps : players)
					ps.sendMessage(Language.messages$chat$format_g.replace("%pDName%", pd.getPlayer().getDisplayName()).replace("%message%", message).replace("%team%", pd.getArena().getTeam(pd.getPlayer()) != null ? pd.getArena().getTeam(pd.getPlayer()).getPrefix() : ""));
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	private void onDamage(EntityDamageByEntityEvent e) {
		Player d = null;
		if (e.getDamager() instanceof Player) {
			d = (Player) e.getDamager();
			PlayerData pd = PlayerData.get(d);
			if (pd != null && pd.getArena() != null) {
				if (pd.getArena().getSpectators().contains(d)) {
					e.setCancelled(true);
					return;
				}
			}
		}
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			PlayerData pd = PlayerData.get(p);
			if (pd != null && pd.getArena() != null) {
				Arena arena = pd.getArena();
				if (arena.getSpectators().contains(p)) {
					e.setCancelled(true);
				} else {
					if (d == null)
						if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
							d = (Player) ((Projectile) e.getDamager()).getShooter();
					if (d == null)
						return;
					if (arena.getTeam(d).hasPlayer(p)) {
						e.setCancelled(true);
						return;
					} else {
						e.setCancelled(false);
					}
					if (arena.getPlayers().containsKey(d)) {
						pd.getLastHit().put(d.getName(), System.currentTimeMillis());
						if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()) instanceof Arrow) {
							if (arena.getMode() == Mode.SOLO) {
								if (pd.getSPerk() != null) {
									Perk perk = pd.getSPerk();
									if (perk.getIds().contains(2) && new Random().nextInt(100) < 10)
										d.getInventory().addItem(new ItemStack(Material.ARROW));
								}
							} else if (arena.getMode() == Mode.TEAM) {
								if (pd.getTPerk() != null) {
									Perk perk = pd.getTPerk();
									if (perk.getIds().contains(2) && new Random().nextInt(100) < 10)
										d.getInventory().addItem(new ItemStack(Material.ARROW));
								}
							} else {
								if (pd.getMPerk() != null) {
									Perk perk = pd.getMPerk();
									if (perk.getIds().contains(2) && new Random().nextInt(100) < 10)
										d.getInventory().addItem(new ItemStack(Material.ARROW));
								}
							}
							Player s = d;
							new BukkitRunnable() {

								@Override
								public void run() {
									s.sendMessage(Language.messages$player$arrow_hp.replace("%health%", ((int) p.getHealth()) + "❤").replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()));
								}
							}.runTaskLater(Main.getPlugin(), 8);
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void canBuild(BlockCanBuildEvent e) {
		if (e.isBuildable())
			return;
		Block b = e.getBlock();
		Arena a = Arena.getArena0();
		if (a != null) {
			for (Player ps : a.getSpectators())
				if (b.getWorld().equals(a.getWorld()) && ps.getLocation().distance(b.getLocation()) <= 3) {
					e.setBuildable(true);
					ps.setVelocity(ps.getVelocity().normalize().multiply(-3.2));
					break;
				}
		}
	}

	@EventHandler
	private void onHit(ProjectileHitEvent e) {
		Projectile pt = e.getEntity();
		if (pt.getShooter() instanceof Player && pt instanceof Arrow) {
			Player p = (Player) pt.getShooter();
			PlayerData pd = PlayerData.get(p);
			if (pd != null && pd.getArena() != null) {
				Arena arena = pd.getArena();
				if (arena.getMode() == Mode.SOLO) {
					if (pd.getSPerk() != null) {
						Perk perk = pd.getSPerk();
						if (perk.getIds().contains(3))
							pt.getLocation().getWorld().createExplosion(pt.getLocation(), 1.5f);
					}
				} else if (arena.getMode() == Mode.TEAM) {
					if (pd.getTPerk() != null) {
						Perk perk = pd.getTPerk();
						if (perk.getIds().contains(3))
							pt.getLocation().getWorld().createExplosion(pt.getLocation(), 1.5f);
					}
				} else {
					if (pd.getMPerk() != null) {
						Perk perk = pd.getMPerk();
						if (perk.getIds().contains(3))
							pt.getLocation().getWorld().createExplosion(pt.getLocation(), 1.5f);
					}
				}
			}
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			PlayerData pd = PlayerData.get(p);
			if (pd != null) {
				if (pd.getArena() != null && (pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING || pd.getArena().getState() == State.RESET))
					e.setCancelled(true);
				if (e.getCause() == DamageCause.VOID)
					if (pd.getArena() != null)
						e.setDamage(20.0d);
				if (pd.getArena() != null && pd.getArena().getSpectators().contains(pd.getPlayer()))
					e.setCancelled(true);
				if (e.getCause() == DamageCause.FALL && pd.getArena() != null) {
					Arena arena = pd.getArena();
					if (arena.getMode() == Mode.SOLO) {
						if (pd.getSPerk() != null) {
							Perk perk = pd.getSPerk();
							if (perk.getIds().contains(1))
								e.setCancelled(true);
						}
					} else if (arena.getMode() == Mode.TEAM) {
						if (pd.getTPerk() != null) {
							Perk perk = pd.getTPerk();
							if (perk.getIds().contains(1))
								e.setCancelled(true);
						}
					} else {
						if (pd.getMPerk() != null) {
							Perk perk = pd.getMPerk();
							if (perk.getIds().contains(1))
								e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd != null)
			if (pd.getArena() == null)
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			else if (pd.getArena().getSpectators().contains(pd.getPlayer()) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING)
				e.setCancelled(true);
	}

	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd != null)
			if (pd.getArena() == null)
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			else if (pd.getArena().getSpectators().contains(pd.getPlayer()) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING)
				e.setCancelled(true);
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd != null)
			if (pd.getArena() != null && (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING || pd.getArena().getState() == State.RESET))
				e.setCancelled(true);
	}

	@EventHandler
	private void onPick(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd != null)
			if (pd.getArena() != null && (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING || pd.getArena().getState() == State.RESET))
				e.setCancelled(true);
	}

	@EventHandler
	private void onFood(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			PlayerData pd = PlayerData.get(p);
			if (pd != null)
				if (pd.getArena() != null && (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING))
					e.setCancelled(true);
			p.setSaturation(300.0f);
			p.setExhaustion(0.0f);
		}
	}

	@EventHandler
	private void onLeaves(LeavesDecayEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	private void onCreature(CreatureSpawnEvent e) {
		e.setCancelled(true);
		if (e.getEntityType() == EntityType.ARMOR_STAND || e.getSpawnReason() == SpawnReason.CUSTOM)
			e.setCancelled(false);
	}

	@EventHandler
	private void onWeather(WeatherChangeEvent e) {
		if (e.toWeatherState())
			e.setCancelled(true);
	}
}