package com.gmail.gremorydev14.gremoryskywars.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.arena.CuboId;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign.SignType;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.editor.ScoreboardConstructor;
import com.gmail.gremorydev14.gremoryskywars.menus.InsaneMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsMegaMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsSoloMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.KitsTeamMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SelectBungeeMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SelectMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.ShopMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SpectatorSettings;
import com.gmail.gremorydev14.gremoryskywars.menus.TeleportMenu;
import com.gmail.gremorydev14.gremoryskywars.packets.TagUtils;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.InventoryUtils;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.mystery.SoulWell;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;
import com.gmail.gremorydev14.profile.Level;
import com.gmail.gremorydev14.profile.Rank;
import com.gmail.gremorydev14.profile.menus.ProfileMenu;

@SuppressWarnings("all")
public class MultiArenaListeners implements Listener, CommandExecutor {

	public static Map<Player, Mode> mode = new HashMap<>();
	public static Map<Player, Type> type = new HashMap<>();
	public static Map<Player, String> create = new HashMap<>();
	public static Map<Player, Arena> selected = new HashMap<>();
	public static Map<Player, Chest> chestEdit = new HashMap<>();
	public static Map<Player, ItemStack[]> inv = new HashMap<>();
	public static Map<Player, Location[]> locs = new HashMap<>();
	public static Map<Player, Long> delay_players = new HashMap<>();

	public static String until(Player p) {
		return (TimeUnit.MILLISECONDS.toSeconds(delay_players.get(p) - System.currentTimeMillis())) + "s";
	}

	/**
	 * 
	 * @param Sign
	 *            Controller
	 */

	@EventHandler
	private void onChange(SignChangeEvent e) {
		if (e.getLine(0) != null && e.getLine(0).equalsIgnoreCase("[SkyWars]") && e.getLines().length > 1) {
			if (!e.getPlayer().hasPermission("sign.create"))
				return;
			String line1 = e.getLine(1);
			if (line1.equalsIgnoreCase("global") && e.getLines().length > 3) {
				String mode = e.getLine(2);
				String type = e.getLine(3);

				if (mode.equalsIgnoreCase("mega") && type.equalsIgnoreCase("mega")) {

				} else if (type.equalsIgnoreCase("mega"))
					return;
				SettingsManager sm = Utils.getLocations();

				Location loc = e.getBlock().getLocation();
				List<String> all = new ArrayList<>();
				all.addAll(sm.getStringList("signs"));
				all.add(Utils.serializeLocation(loc) + " : global : " + mode.toLowerCase() + " : " + type.toLowerCase());
				sm.set("signs", all);
				new ArenaSign(SignType.GLOBAL, Mode.valueOf(mode.toUpperCase()), Type.valueOf(type.toUpperCase()), loc);
			} else if (line1.equalsIgnoreCase("selector") && e.getLines().length > 3) {
				String mode = e.getLine(2);
				String type = e.getLine(3);

				if (mode.equalsIgnoreCase("mega") || type.equalsIgnoreCase("mega"))
					return;
				SettingsManager sm = Utils.getLocations();

				Location loc = e.getBlock().getLocation();
				List<String> all = new ArrayList<>();
				all.addAll(sm.getStringList("signs"));
				all.add(Utils.serializeLocation(loc) + " : selector : " + mode.toLowerCase() + " : " + type.toLowerCase());
				sm.set("signs", all);
				new ArenaSign(SignType.SELECTOR, Mode.valueOf(mode.toUpperCase()), Type.valueOf(type.toUpperCase()), loc);
			} else {
				for (int i = 0; i < e.getLines().length; i++)
					e.setLine(i, e.getLine(i).replace("&", "§"));
			}
		}
	}

	@EventHandler
	private void onSign(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST)) {
			if (ArenaSign.get(e.getClickedBlock().getLocation()) != null) {
				e.setCancelled(true);
				ArenaSign sign = ArenaSign.get(e.getClickedBlock().getLocation());
				if (sign.getSignType() == SignType.GLOBAL) {
					if (Options.getMode() == ServerMode.LOBBY) {
						String server = Arena.getBungee(sign.getMode(), sign.getType());
						if (server != null) {
							PlayerData.get(p).saveAsync();
							Utils.sendToServer(p, server);
						} else {
							p.sendMessage(Language.messages$player$play_again_null);
						}
					} else {
						Arena arena = ArenaSign.switchFrom(sign.getMode(), sign.getType());
						if (arena != null) {
							arena.add(p);
						} else {
							p.sendMessage(Language.messages$player$play_again_null);
						}
					}
				} else {
					if (Options.getMode() == ServerMode.LOBBY) {
						if (p.hasPermission("sign.selector")) {
							new SelectBungeeMenu(p, sign.getMode(), sign.getType());
						} else {
							p.sendMessage(Language.messages$player$no_permission);
						}
					} else {
						if (p.hasPermission("sign.selector")) {
							new SelectMenu(p, sign.getMode(), sign.getType());
						} else {
							p.sendMessage(Language.messages$player$no_permission);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param Skywars
	 *            Controller
	 */

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		TagUtils.sendTeams(p);

		if (Options.isJoin_teleport())
			p.teleport(Utils.getLobby());
		PlayerData pd = PlayerData.create(p);
		Rank rank = Rank.getRank(p);
		rank.apply(p);
		p.setMaxHealth(20.0);
		p.setGameMode(GameMode.ADVENTURE);
		if (p.hasPermission("broadcast.name"))
			for (Player ps : Bukkit.getOnlinePlayers())
				if (PlayerData.get(ps) == null || PlayerData.get(ps).getArena() == null)
					ps.sendMessage(Language.messages$player$broadcast_login.replace("%player%", p.getDisplayName()));

		if (Options.isOn_join_items()) {
			p.setHealth(20.0);
			p.setFoodLevel(20);
			p.setExp((float) ((double) (pd.getProfile().getXp()) / (Level.getNext(pd.getProfile().getLevel()).getXp() < pd.getProfile().getXp() ? pd.getProfile().getXp() : Level.getNext(pd.getProfile().getLevel()).getXp())));
			p.setLevel(pd.getProfile().getLevel());
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			if (Options.isReceive_profile())
				p.getInventory().setItem(Options.getSlot_profile(), ItemUtils.createSkull("3 : name=" + Options.getDisplay_profile() + " : owner=" + p.getName()));
			if (Options.isReceive_shop())
				p.getInventory().setItem(Options.getSlot_shop(), ItemUtils.createItem(Options.getMaterial_shop() + " : 1 : name=" + Options.getDisplay_shop()));
			if (Options.isReceive_players())
				p.getInventory().setItem(Options.getSlot_players(), ItemUtils.createItem("INK_SACK:" + (pd.isView() ? 10 : 8) + " : 1 : name=" + (pd.isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
			if (Options.isReceive_lobbies())
				p.getInventory().setItem(Options.getSlot_lobbies(), ItemUtils.createItem(Options.getMaterial_lobbies() + " : 1 : name=" + Options.getDisplay_lobbies()));
			p.updateInventory();
		}
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (PlayerData.get(ps) == null || PlayerData.get(ps).getArena() != null) {
				p.hidePlayer(ps);
				ps.hidePlayer(p);
			} else {
				if (pd.isView()) {
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

	@EventHandler
	private void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (pd == null)
			return;
		if (p.hasMetadata("ADMIN_MODE")) {
			for (Player ps : p.getWorld().getPlayers()) {
				ps.hidePlayer(p);
				p.showPlayer(ps);
			}
		}
		if (Options.getWorlds_allowed().contains(p.getWorld().getName())) {
			if (Options.isOn_world_change_items()) {
				p.setMaxHealth(20.0);
				p.setHealth(20.0);
				p.setFoodLevel(20);
				p.setExp((float) ((double) (pd.getProfile().getXp()) / (Level.getNext(pd.getProfile().getLevel()).getXp() < pd.getProfile().getXp() ? pd.getProfile().getXp() : Level.getNext(pd.getProfile().getLevel()).getXp())));
				p.setLevel(pd.getProfile().getLevel());
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				if (Options.isReceive_profile())
					p.getInventory().setItem(Options.getSlot_profile(), ItemUtils.createSkull("3 : name=" + Options.getDisplay_profile() + " : owner=" + p.getName()));
				if (Options.isReceive_shop())
					p.getInventory().setItem(Options.getSlot_shop(), ItemUtils.createItem(Options.getMaterial_shop() + " : 1 : name=" + Options.getDisplay_shop()));
				if (Options.isReceive_players())
					p.getInventory().setItem(Options.getSlot_players(), ItemUtils.createItem("INK_SACK:" + (PlayerData.get(p).isView() ? 10 : 8) + " : 1 : name=" + (PlayerData.get(p).isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
				if (Options.isReceive_lobbies())
					p.getInventory().setItem(Options.getSlot_lobbies(), ItemUtils.createItem(Options.getMaterial_lobbies() + " : 1 : name=" + Options.getDisplay_lobbies()));
				p.updateInventory();
				if (!Options.getWorlds_allowed().contains(e.getFrom().getName()))
					if (ScoreboardConstructor.isScoreboard_lobby_enabled())
						PlayerData.get(p).getScoreboard().set(p);
			}
		} else {
			if (PlayerData.get(p) != null && PlayerData.get(p).getArena() == null && p.getScoreboard().equals(PlayerData.get(p).getScoreboard().getScoreboard())) {
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		}
	}

	@EventHandler
	private void onInteractAt(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().hasMetadata("SW_ARMOR")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(null);
		if (p.getGameMode() != GameMode.ADVENTURE) {
			p.setGameMode(GameMode.ADVENTURE);
		}

		PlayerData pd = PlayerData.get(p);
		if (pd != null) {
			if (pd.getArena() != null)
				pd.getArena().remove(p, pd.getArena().getSpectators().contains(p), false);
			pd.saveAsync();
			PlayerData.remove(p);
		}

		PartyBukkit party = PartyBukkit.get(p);
		if (party != null) {
			party.removePlayer(p);
			if (party.getMembers().isEmpty()) {
				PartyBukkit.remove(party);
			} else {
				party.broadcastMessage(Language.messages$party$leave_broadcast.replace("%player%", p.getName()));
			}
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();

		PlayerData pd = PlayerData.get(p);
		if (create.containsKey(e.getPlayer())) {
			if (!create.get(e.getPlayer()).equals(p.getWorld().getName()))
				return;
			if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				String dis = p.getItemInHand().getItemMeta().getDisplayName();
				if (dis.equals("§eBorder")) {
					e.setCancelled(true);
					if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
						locs.get(e.getPlayer())[0] = e.getClickedBlock().getLocation();
						e.getPlayer().sendMessage("§aSelected!");
					} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						locs.get(e.getPlayer())[1] = e.getClickedBlock().getLocation();
						e.getPlayer().sendMessage("§aSelected!");
					}
				} else if (dis.equals("§eComplete")) {
					e.setCancelled(true);
					Location[] location = locs.get(e.getPlayer());
					if (location[0] != null && location[1] != null) {
						World world = Bukkit.getWorld(create.get(e.getPlayer()));
						if (world == null) {
							e.getPlayer().sendMessage("§cInvalid world!");
							return;
						}
						p.getInventory().clear();
						p.updateInventory();
						CuboId cuboId = new CuboId(location[0], location[1]);
						long l = System.currentTimeMillis();
						SettingsManager sm = SettingsManager.getConfig(world.getName(), "plugins/GremorySkywars/arenas");
						sm.set("mode", mode.get(p).toString());
						sm.set("type", type.get(p).toString());
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
								locs.remove(e.getPlayer());
								create.remove(e.getPlayer());
								inv.remove(e.getPlayer());
								e.getPlayer().updateInventory();
								e.getPlayer().sendMessage("§aArena created after " + demora + "ms!");
								e.getPlayer().sendMessage("§aSpawns scaneds: " + spawns.size() + "!");
								e.getPlayer().sendMessage("§aChests scaneds: " + chests.size() + "!");
							}
						}.runTaskLater(Main.getPlugin(), 100);
					} else {
						e.getPlayer().sendMessage("§cSelect border!");
					}
				} else if (dis.equals("§cQuit")) {
					e.setCancelled(true);
					locs.remove(p);
					create.remove(p);
					p.getInventory().setContents(inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (selected.containsKey(e.getPlayer())) {
			Arena arena = selected.get(e.getPlayer());
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
					selected.remove(p);
					locs.remove(p);
					p.getInventory().setContents(inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (chestEdit.containsKey(e.getPlayer())) {
			Arena arena = Arena.getArena(e.getPlayer().getWorld());
			if (arena == null)
				return;
			if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				String dis = p.getItemInHand().getItemMeta().getDisplayName();
				if (dis.equals("§eWand")) {
					e.setCancelled(true);
					if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
						Chest chest = chestEdit.get(e.getPlayer());
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
					chestEdit.remove(p);
					locs.remove(p);
					p.getInventory().setContents(inv.get(p));
					p.updateInventory();
				}
			}
			return;
		}
		if (pd != null) {
			if (pd.getArena() == null) {
				if (e.getAction().name().contains("RIGHT")) {
					if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
						SoulWell sw = SoulWell.get(e.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5));
						if (sw != null) {
							InventoryUtils.wellConfirm(p, sw);
							return;
						}
					}

					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						String display = item.getItemMeta().getDisplayName();
						if (display.equalsIgnoreCase(Options.getDisplay_compass())) {
							e.setCancelled(true);
							p.sendMessage(Language.messages$player$in_development);
						} else if (display.equalsIgnoreCase(Options.getDisplay_profile())) {
							e.setCancelled(true);
							new ProfileMenu(p);
						} else if (display.equalsIgnoreCase(Options.getDisplay_shop())) {
							e.setCancelled(true);
							new ShopMenu(p);
						} else if (display.equalsIgnoreCase(Options.getDisplay_lobbies())) {
							e.setCancelled(true);
							p.chat("/menu menu");
						} else if (display.equalsIgnoreCase(Options.getDisplay_players_on()) || display.equalsIgnoreCase(Options.getDisplay_players_off())) {
							if (delay_players.containsKey(pd.getPlayer()) && TimeUnit.MILLISECONDS.toSeconds(delay_players.get(pd.getPlayer()) - System.currentTimeMillis()) > 0) {
								pd.getPlayer().sendMessage(Language.messages$player$players_toggle_delay.replace("%time%", until(pd.getPlayer())));
								return;
							}

							delay_players.put(pd.getPlayer(), TimeUnit.SECONDS.toMillis(15) + System.currentTimeMillis());
							if (pd.isView()) {
								item.setDurability((short) 8);
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(Options.getDisplay_players_off());
								item.setItemMeta(meta);
								p.updateInventory();
								pd.setView(false);
								p.sendMessage(Language.messages$player$players_disable);
							} else {
								item.setDurability((short) 10);
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(Options.getDisplay_players_on());
								item.setItemMeta(meta);
								p.updateInventory();
								pd.setView(true);
								p.sendMessage(Language.messages$player$players_enable);
							}
							for (Player ps : Bukkit.getOnlinePlayers()) {
								if (!ps.getWorld().getName().equals(p.getWorld().getName())) {
									p.hidePlayer(ps);
									ps.hidePlayer(p);
								} else {
									if (pd.isView())
										p.showPlayer(ps);
									else
										p.hidePlayer(ps);
									if (PlayerData.get(ps) == null || PlayerData.get(ps).isView())
										ps.showPlayer(p);
									else
										ps.hidePlayer(p);
									
									if (ps.hasMetadata("ADMIN_MODE")) {
										p.hidePlayer(ps);
									}
								}
							}
						}
					}
				}
			} else {
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
					if (pd.getArena().getSpectators().contains(p)) {
						e.setCancelled(true);
						if (item.equals(Options.getSpec_compass().getItem())) {
							new TeleportMenu(pd);
						} else if (item.equals(Options.getSpec_settings().getItem())) {
							new SpectatorSettings(p);
						} else if (item.equals(Options.getSpec_again().getItem())) {
							Arena arena = ArenaSign.switchFrom(pd.getArena().getMode(), pd.getArena().getType());
							if (arena != null) {
								pd.getArena().remove(p, true, true);
								arena.add(p);
							} else {
								pd.getPlayer().sendMessage(Language.messages$player$play_again_null);
							}
						} else if (item.equals(Options.getSpec_leave().getItem())) {
							pd.getArena().remove(p, true, false);
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
			if (killer == null) {
				message = Language.messages$arena$player_suicide.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName());
			} else if (killer.equals(p)) {
				message = Language.messages$arena$player_suicide.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName());
			} else {
				message = Language.messages$arena$player_killed.replace("%pName%", p.getName()).replace("%pDName%", p.getDisplayName()).replace("%kName%", killer.getName()).replace("%kDName%", killer.getDisplayName());
			}
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
			if (pd.getArena() == null) {
				if (!Options.getWorlds_allowed().contains(pd.getPlayer().getWorld().getName()))
					return;
				e.setFormat(Language.messages$chat$format.replace("%message%", message).replace("%pDName%", pd.getPlayer().getDisplayName()));
				e.getRecipients().clear();
				e.getRecipients().addAll(pd.getPlayer().getWorld().getPlayers());
			} else {
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
		if (PlayerData.get(e.getPlayer()) == null || (PlayerData.get(e.getPlayer()).getArena() == null && !Options.getWorlds_allowed().contains(e.getPlayer().getWorld().getName())))
			return;
		if (cmd.equals("tell")) {
			if (cmd.split(" ").length > 1) {
				String player = cmd.split(" ")[1];
				if (Bukkit.getPlayerExact(player) != null && PlayerData.get(Bukkit.getPlayerExact(player)) != null) {
					e.setCancelled(PlayerData.get(Bukkit.getPlayerExact(player)).isTell());
					if (e.isCancelled()) {
						e.getPlayer().sendMessage(Language.messages$player$tell_disabled);
						return;
					}
				}
			}
		} else if (cmd.equals("vote")) {
			if (e.getMessage().split(" ").length <= 1)
				return;
			PlayerData pd = PlayerData.get(e.getPlayer());
			if (pd != null && pd.getArena() != null && pd.getArena().getSpectators().contains(pd.getPlayer())) {
				pd.getArena().vote(pd.getPlayer(), Integer.parseInt(e.getMessage().split(" ")[1]));
				e.setCancelled(true);
			}
		} else if (cmd.equals("g")) {
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
					ps.sendMessage(Language.messages$chat$format_g.replace("%pDName%", pd.getPlayer().getDisplayName()).replace("%message%", message).replace("%team%",
							pd.getArena().getTeam(pd.getPlayer()) != null ? pd.getArena().getTeam(pd.getPlayer()).getPrefix() : ""));
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
			if (pd != null) {
				if (pd.getArena() == null || pd.getArena().getSpectators().contains(d)) {
					if (!Options.getWorlds_allowed().contains(d.getWorld().getName()) && pd.getArena() == null)
						return;
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
					if (d == null) {
						if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
							d = (Player) ((Projectile) e.getDamager()).getShooter();
						}
					}
					if (d == null) {
						return;
					}

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
		Arena a = Arena.getArena(b.getWorld());
		if (a != null) {
			for (Player ps : a.getSpectators())
				if (ps.getLocation().distance(b.getLocation()) <= 3) {
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
				if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd.getArena() == null) {
					return;
				}
				if (pd.getArena() == null || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING || pd.getArena().getState() == State.RESET) {
					e.setCancelled(true);
				}
				if (e.getCause() == DamageCause.VOID) {
					if (pd.getArena() == null) {
						pd.getPlayer().teleport(pd.getPlayer().getWorld().getSpawnLocation());
					} else {
						e.setDamage(20.0d);
					}
				}

				if (pd.getArena() != null && pd.getArena().getSpectators().contains(p)) {
					e.setCancelled(true);
				}

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
		if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd != null && pd.getArena() == null) {
			return;
		}
		
		if (pd != null) {
			if (pd.getArena() == null) {
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			} else if (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd != null && pd.getArena() == null) {
			return;
		}
		
		if (pd != null) {
			if (pd.getArena() == null) {
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			} else if (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd != null && pd.getArena() == null) {
			return;
		}
		
		if (pd != null) {
			if (pd.getArena() == null) {
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			} else if (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onPick(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = PlayerData.get(p);
		if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd != null && pd.getArena() == null) {
			return;
		}
		
		if (pd != null) {
			if (pd.getArena() == null) {
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			} else if (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onFood(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			PlayerData pd = PlayerData.get(p);
			if (!Options.getWorlds_allowed().contains(p.getWorld().getName()) && pd != null && pd.getArena() == null) {
				return;
			}
			
			if (pd != null) {
				if (pd.getArena() == null) {
					e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
				} else if (pd.getArena().getSpectators().contains(p) || pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
					e.setCancelled(true);
				}
			}
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
		if (e.getEntityType() == EntityType.ARMOR_STAND || e.getSpawnReason() == SpawnReason.CUSTOM) {
			e.setCancelled(false);
		}
	}
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("§6Lobby Para Player");
			return true;
		}

		if(cmd.getName().equalsIgnoreCase("lobby")) {
		final Player p = (Player)sender;
		PlayerData pd = PlayerData.get(p);
		if (pd != null && pd.getArena() != null) {
			p.teleport(pd.getArena().getCuboId().getRandomLocation());
			p.setFireTicks(0);
			p.setLevel(0);
			p.setExp(0.0f);
			p.setAllowFlight(true);
			p.setFlying(true);
				p.setHealth(20.0);
				p.setFoodLevel(20);
				p.setExp((float) ((double) (pd.getProfile().getXp()) / (Level.getNext(pd.getProfile().getLevel()).getXp() < pd.getProfile().getXp() ? pd.getProfile().getXp() : Level.getNext(pd.getProfile().getLevel()).getXp())));
				p.setLevel(pd.getProfile().getLevel());
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				if (Options.isReceive_profile())
					p.getInventory().setItem(Options.getSlot_profile(), ItemUtils.createSkull("3 : name=" + Options.getDisplay_profile() + " : owner=" + p.getName()));
				if (Options.isReceive_shop())
					p.getInventory().setItem(Options.getSlot_shop(), ItemUtils.createItem(Options.getMaterial_shop() + " : 1 : name=" + Options.getDisplay_shop()));
				if (Options.isReceive_players())
					p.getInventory().setItem(Options.getSlot_players(), ItemUtils.createItem("INK_SACK:" + (pd.isView() ? 10 : 8) + " : 1 : name=" + (pd.isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
				if (Options.isReceive_lobbies())
					p.getInventory().setItem(Options.getSlot_lobbies(), ItemUtils.createItem(Options.getMaterial_lobbies() + " : 1 : name=" + Options.getDisplay_lobbies()));
				p.updateInventory();
			new BukkitRunnable() {
				public void run() {
					p.setGameMode(GameMode.CREATIVE);
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 3));
				}
			}.runTaskLater(Main.getPlugin(), 5);
		}
	}
		return false;

}

	@EventHandler
	private void onWeather(WeatherChangeEvent e) {
		if (e.toWeatherState()) {
			e.setCancelled(true);
			
		}
	}
}
