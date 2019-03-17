package com.gmail.gremorydev14.gremoryskywars.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign.SignType;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.editor.ScoreboardConstructor;
import com.gmail.gremorydev14.gremoryskywars.menus.SelectBungeeMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SelectMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.ShopMenu;
import com.gmail.gremorydev14.gremoryskywars.packets.TagUtils;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;
import com.gmail.gremorydev14.gremoryskywars.util.InventoryUtils;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.mystery.SoulWell;
import com.gmail.gremorydev14.profile.Level;
import com.gmail.gremorydev14.profile.Rank;
import com.gmail.gremorydev14.profile.menus.ProfileMenu;

@SuppressWarnings("all")
public class LobbyBungeeListeners implements Listener {

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
						} else
							p.sendMessage(Language.messages$player$play_again_null);
					} else {
						Arena arena = Arena.getFrom(sign.getMode(), sign.getType());
						if (arena != null)
							arena.add(p);
						else
							p.sendMessage(Language.messages$player$play_again_null);
					}
				} else {
					if (Options.getMode() == ServerMode.LOBBY) {
						if (p.hasPermission("sign.selector"))
							new SelectBungeeMenu(p, sign.getMode(), sign.getType());
						else
							p.sendMessage(Language.messages$player$no_permission);
					} else {
						if (p.hasPermission("sign.selector"))
							new SelectMenu(p, sign.getMode(), sign.getType());
						else
							p.sendMessage(Language.messages$player$no_permission);
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
		if (p.hasPermission("broadcast.name"))
			Bukkit.broadcastMessage(Language.messages$player$broadcast_login.replace("%player%", p.getDisplayName()));

		p.setHealth(20.0);
		p.setFoodLevel(20);
		p.setExp((float) ((double) (pd.getProfile().getXp()) / (Level.getNext(pd.getProfile().getLevel()).getXp() < pd.getProfile().getXp() ? pd.getProfile().getXp() : Level.getNext(pd.getProfile().getLevel()).getXp())));
		p.setLevel(pd.getProfile().getLevel());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		if (Options.isReceive_compass())
			p.getInventory().setItem(Options.getSlot_compass(), ItemUtils.createItem(Options.getMaterial_compass() + " : 1 : name=" + Options.getDisplay_compass()));
		if (Options.isReceive_profile())
			p.getInventory().setItem(Options.getSlot_profile(), ItemUtils.createSkull("3 : name=" + Options.getDisplay_profile() + " : owner=" + p.getName()));
		if (Options.isReceive_shop())
			p.getInventory().setItem(Options.getSlot_shop(), ItemUtils.createItem(Options.getMaterial_shop() + " : 1 : name=" + Options.getDisplay_shop()));
		if (Options.isReceive_players())
			p.getInventory().setItem(Options.getSlot_players(), ItemUtils.createItem("INK_SACK:" + (pd.isView() ? 10 : 8) + " : 1 : name=" + (pd.isView() ? Options.getDisplay_players_on() : Options.getDisplay_players_off())));
		if (Options.isReceive_lobbies())
			p.getInventory().setItem(Options.getSlot_lobbies(), ItemUtils.createItem(Options.getMaterial_lobbies() + " : 1 : name=" + Options.getDisplay_lobbies()));
		p.updateInventory();
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (!ps.getWorld().equals(p.getWorld())) {
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
	private void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(null);
		if (p.getGameMode() != GameMode.ADVENTURE) {
			p.setGameMode(GameMode.ADVENTURE);
		}

		PlayerData pd = PlayerData.get(p);
		if (pd != null) {
			pd.saveAsync();
			PlayerData.remove(p);
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();

		PlayerData pd = PlayerData.get(p);
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
							p.sendMessage(Language.messages$player$in_development);
						} else if (display.equalsIgnoreCase(Options.getDisplay_players_on()) || display.equalsIgnoreCase(Options.getDisplay_players_off())) {
							if (MultiArenaListeners.delay_players.containsKey(pd.getPlayer()) && TimeUnit.MILLISECONDS.toSeconds(MultiArenaListeners.delay_players.get(pd.getPlayer()) - System.currentTimeMillis()) > 0) {
								p.sendMessage(Language.messages$player$players_toggle_delay.replace("%time%", MultiArenaListeners.until(p)));
								return;
							}
							MultiArenaListeners.delay_players.put(pd.getPlayer(), TimeUnit.SECONDS.toMillis(15) + System.currentTimeMillis());
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
								if (!ps.getWorld().equals(p.getWorld())) {
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
			}
		}
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent e) {
		String message = e.getMessage().replace("%", "");
		PlayerData pd = PlayerData.get(e.getPlayer());

		if (pd != null) {
			if (pd.getArena() == null) {
				e.setFormat(Language.messages$chat$format.replace("%message%", message).replace("%pDName%", pd.getPlayer().getDisplayName()));
				e.getRecipients().clear();
				e.getRecipients().addAll(pd.getPlayer().getWorld().getPlayers());
			}
		}
	}

	@EventHandler
	private void onCommand(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().split(" ")[0].toLowerCase().replace("/", "");
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
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			e.setCancelled(true);
			if (e.getCause() == DamageCause.VOID)
				p.teleport(Utils.getLobby());
		}
	}

	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	private void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	private void onDrop(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	private void onFood(FoodLevelChangeEvent e) {
		e.setCancelled(true);
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
