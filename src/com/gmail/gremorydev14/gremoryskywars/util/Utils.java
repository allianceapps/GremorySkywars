package com.gmail.gremorydev14.gremoryskywars.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.delivery.Delivery;
import com.gmail.gremorydev14.delivery.Delivery.DeliveryMessages;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.api.CitizensAPI;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.arena.Chest;
import com.gmail.gremorydev14.gremoryskywars.arena.util.ArenaSign;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Cage;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.cmd.AdminCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.PlayAgainCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.SkywarsCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.SkywarsLobbyModeCommand;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.editor.ScoreboardConstructor;
import com.gmail.gremorydev14.gremoryskywars.listeners.BungeeModeListeners;
import com.gmail.gremorydev14.gremoryskywars.listeners.InventoryListeners;
import com.gmail.gremorydev14.gremoryskywars.listeners.LobbyBungeeListeners;
import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.player.SwAchievement;
import com.gmail.gremorydev14.gremoryskywars.player.storage.MySQL;
import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLite;
import com.gmail.gremorydev14.gremoryskywars.player.storage.Sql;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.FontInfo;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.leaderboards.LeaderBoard;
import com.gmail.gremorydev14.mystery.SoulWell;
import com.gmail.gremorydev14.party.PartyCommand;
import com.gmail.gremorydev14.profile.Booster;
import com.gmail.gremorydev14.profile.Level;
import com.gmail.gremorydev14.profile.Rank;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;

public class Utils {

	@Getter
	private static Sql storage;
	@Getter
	private static Economy econ;
	@Getter
	private static CitizensAPI citizens;
	@Getter
	@Setter
	private static Location lobby;
	@Getter
	private static SettingsManager kits, cages, perks, votes, boards, chests, options, locations, achievements, ranks, levels, boosters, language, menus, scoreboards;

	public static void setup() {
		Logger.info("Checking plugin..");
		SpigotMCAntiLeak smcal = new SpigotMCAntiLeak();
		if (smcal.isLeaked()) {
			Logger.warn("Plugin failed to check, disabled!");
			return;
		}
		Logger.info("Plugin checked, starting modules..");
		kits = new SettingsManager("kits");
		cages = new SettingsManager("cages");
		perks = new SettingsManager("perks");
		ranks = new SettingsManager("ranks");
		menus = new SettingsManager("menus");
		chests = new SettingsManager("chests");
		levels = new SettingsManager("levels");
		options = new SettingsManager("options");
		boosters = new SettingsManager("boosters");
		language = new SettingsManager("language");
		locations = new SettingsManager("locations");
		scoreboards = new SettingsManager("scoreboards");
		achievements = new SettingsManager("achievements");
		boards = new SettingsManager("boards");
		votes = new SettingsManager("votes", "plugins/GremorySkywars/votes");
		if (locations.contains("lobby")) {
			lobby = Utils.unserializeLocation(locations.getString("lobby"));
		} else {
			lobby = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		if (!setupEconomy()) {
			Logger.warn("This plugin needs vault and economy plugin!");
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
			return;
		}
		if (!setupHolographic()) {
			Logger.warn("This plugin needs holographicdisplays plugin!");
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
			return;
		}
		Options.setup();
		if (!setupCitizens()) {
			Logger.warn("This plugin needs Citizens to manage NPC's, disabling NPC's!");
		}
		MenuEditor.setup();
		Language.setup();
		Delivery.register();
		DeliveryMessages.register();
		ScoreboardConstructor.setup();
		if (Options.isUse_mysql()) {
			storage = new MySQL(Utils.getOptions().getString("preferences.database.host"), Utils.getOptions().getInt("preferences.database.port"), Utils.getOptions().getString("preferences.database.database"),
					Utils.getOptions().getString("preferences.database.user"), Utils.getOptions().getString("preferences.database.password"));
		} else {
			storage = new SQLite(new File(Main.getPlugin().getDataFolder(), "data.db"));
		}
		new AdminCommand();
		new InventoryListeners();
		if (Options.getMode() != ServerMode.LOBBY) {
			new SkywarsCommand();
			Chest.register();
			Arena.register();
		} else {
			new SkywarsLobbyModeCommand();
		}
		if (Options.getMode().equals(ServerMode.MULTI_ARENA)) {
			Bukkit.getPluginManager().registerEvents(new MultiArenaListeners(), Main.getPlugin());
		} else if (Options.getMode().equals(ServerMode.BUNGEE)) {
			Bukkit.getPluginManager().registerEvents(new BungeeModeListeners(), Main.getPlugin());
		} else {
			Bukkit.getPluginManager().registerEvents(new LobbyBungeeListeners(), Main.getPlugin());
		}
		new PlayAgainCommand();
		new PartyCommand();
		Kit.register();
		Cage.register();
		Perk.register();
		ArenaSign.register();
		Rank.register();
		Level.register();
		Booster.register();
		SoulWell.register();
		SwAchievement.register();
		LeaderBoard.setup();
		new Updater(Main.getPlugin(), 43977);
		Logger.info("Plugin enabled");
	}

	public static String decimal(int i) {
		return "" + i;
	}

	public static String decimal(double i) {
		return "" + ((int) i);
	}

	public static boolean setupCitizens() {
		if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
			return false;
		}
		citizens = new CitizensAPI();
		return citizens != null;
	}
	
	public static boolean setupHolographic() {
		return Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null;
	}

	public static boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Main.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = (Economy) rsp.getProvider();
		return econ != null;
	}

	public static String serializeLocation(Location loc) {
		String toString = loc.getWorld().getName() + " : " + loc.getX() + " : " + loc.getY() + " : " + loc.getZ() + " : " + loc.getPitch() + " : " + loc.getYaw();
		return toString;
	}

	public static Location unserializeLocation(String path) {
		String[] sp = path.split(" : ");
		Location loc = new Location(Bukkit.getWorld(sp[0]), Double.parseDouble(sp[1]), Double.parseDouble(sp[2]), Double.parseDouble(sp[3]));
		loc.setPitch(Float.parseFloat(sp[4]));
		loc.setYaw(Float.parseFloat(sp[5]));
		return loc;
	}

	public static boolean sendToServer(Player p, String name) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(name);
			p.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void spawnFirework(Location loc) {
		if (Reflection.getVersion().startsWith("v1_8")) {
			Firework fire = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
			FireworkMeta meta = fire.getFireworkMeta();
			Color color = Color.AQUA;
			int random = new Random().nextInt(5) + 1;
			if (random == 1) {
				color = Color.BLUE;
			} else if (random == 2) {
				color = Color.RED;
			} else if (random == 3) {
				color = Color.GREEN;
			} else if (random == 4) {
				color = Color.MAROON;
			} else {
				color = Color.ORANGE;
			}
			meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.STAR).build());
			meta.setPower(1);
			fire.setFireworkMeta(meta);
		} else {
			loc.getWorld().playSound(loc, Sound.FIREWORK_LAUNCH, 1.0f, 1.0f);
		}
	}

	public static String center(String message) {
		message = message.replace("&", "§");
		int size = 0;
		boolean color = false;
		boolean bold = false;

		for (char c : message.toCharArray()) {
			if (c == '§') {
				color = true;
			} else if (color) {
				color = false;
				bold = (c == 'l' || c == 'L');
			} else {
				FontInfo font = FontInfo.getFontInfo(c);
				size += (bold ? font.getBoldLength() : font.getLength());
				size++;
			}
		}
		int compensate = 154 - size / 2;
		int space = FontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < compensate) {
			sb.append(" ");
			compensated += space;
		}
		return sb.toString() + message;
	}

	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			if (file.listFiles().length == 0) {
				file.delete();
			} else {
				for (File f : file.listFiles()) {
					deleteFile(f);
				}
			}
		} else {
			file.delete();
		}
	}

	public static void copyDirectory(File from, File to) {
		if (from.isDirectory()) {
			if (!to.exists()) {
				to.mkdir();
			}

			for (String name : from.list()) {
				copyDirectory(new File(from, name), new File(to, name));
			}
		} else {
			if (from.getName().equals("uid.dat") || from.getName().equals("session.dat")) {
				return;
			}

			try {
				copyFile(new FileInputStream(from), to);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Logger.warn("Failed to copy file from " + from.getName() + " to " + to.getName());
			}
		}
	}

	public static void copyFile(InputStream i, File config) {
		try {
			OutputStream out = new FileOutputStream(config);
			byte[] buf = new byte[710];
			int len;
			while ((len = i.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			i.close();
		} catch (Exception ignore) {
		}
	}
	
	public static String getTimeUntil(long epoch) {
		epoch -= System.currentTimeMillis();
		return getTime(epoch);
	}

	public static String getTime(long ms) {
		ms = (long) Math.ceil(ms / 1000.0);
		StringBuilder sb = new StringBuilder(40);

		if (ms / 86400 > 0) {
			long days = ms / 86400;
			sb.append(days + (days == 1 ? "d " : "d "));
			ms -= days * 86400;
		}
		if (ms / 3600 > 0) {
			long hours = ms / 3600;
			sb.append(hours + (hours == 1 ? "h " : "h "));
			ms -= hours * 3600;
		}
		if (ms / 60 > 0) {
			long minutes = ms / 60;
			sb.append(minutes + (minutes == 1 ? "m " : "m "));
			ms -= minutes * 60;
		}
		if (ms > 0) {
			sb.append(ms + (ms == 1 ? "s " : "s "));
		}
		if (sb.length() > 1) {
			sb.replace(sb.length() - 1, sb.length(), "");
		} else {
			sb = new StringBuilder("Can claim");
		}

		return sb.toString();
	}

	public static void cageInventory(Inventory inv, boolean all) {
		ItemStack glass = ItemUtils.createItem("STAINED_GLASS_PANE:11 : 1 : name=&8-");
		if (all) {
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, glass);
				}
			}
		} else {
			for (int i = 0; i < 9; i++) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, glass);
				}
			}
			for (int i = inv.getSize() - 9; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, glass);
				}
			}
			int slot = inv.getSize() / 9 - 2;
			if (slot < 1) {
				return;
			}
			for (int i = 9; i < 9 * slot + 1; i += 9) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, glass);
				}
			}
			for (int i = 17; i < 9 * slot + 9; i += 9) {
				if (inv.getItem(i) == null) {
					inv.setItem(i, glass);
				}
			}
		}
	}

	public static String serializeItemStack(ItemStack content) {
		StringBuilder sb = new StringBuilder(content.getType().name() + ":" + content.getDurability() + " : " + content.getAmount());
		if (content.hasItemMeta()) {
			if (content.getItemMeta().hasDisplayName()) {
				sb.append(" : name=" + content.getItemMeta().getDisplayName().replace("§", "&"));
			}
			if (content.getItemMeta().hasLore() && content.getItemMeta().getLore().size() > 0) {
				sb.append(" : lore=");
				for (int i = 0; i < content.getItemMeta().getLore().size(); i++) {
					if (i + 1 == content.getItemMeta().getLore().size()) {
						sb.append(content.getItemMeta().getLore().get(i).replace("&", "§"));
					} else {
						sb.append(content.getItemMeta().getLore().get(i).replace("&", "§") + "/");
					}
				}
			}
			if (content.getItemMeta().hasEnchants()) {
				for (Entry<Enchantment, Integer> entry : content.getItemMeta().getEnchants().entrySet()) {
					sb.append(" : enchant=" + entry.getKey().getName().toUpperCase() + ":" + entry.getValue());
				}
			}
		}
		return sb.toString();
	}

	public static enum ServerMode {
		BUNGEE, LOBBY, MULTI_ARENA;
	}
}
