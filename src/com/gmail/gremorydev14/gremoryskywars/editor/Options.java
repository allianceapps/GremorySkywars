package com.gmail.gremorydev14.gremoryskywars.editor;

import java.util.ArrayList;
import java.util.List;

import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;

import lombok.Getter;

public class Options {

	@Getter
	private static ServerMode mode;

	// TODO: Booleans
	@Getter
	private static boolean on_join_items = true;
	@Getter
	private static boolean on_world_change_items = true;
	@Getter
	private static boolean receive_compass = true;
	@Getter
	private static boolean receive_profile = true;
	@Getter
	private static boolean receive_players = true;
	@Getter
	private static boolean receive_shop = true;
	@Getter
	private static boolean receive_lobbies = true;
	@Getter
	private static boolean join_teleport = true;
	@Getter
	private static boolean use_mysql = true;

	// TODO: Lists
	@Getter
	private static List<String> worlds_allowed = new ArrayList<>();
	@Getter
	private static List<String> rooms_bungee = new ArrayList<>();

	// TODO: Items material
	@Getter
	private static String material_compass;
	@Getter
	private static String material_shop;
	@Getter
	private static String material_lobbies;
	@Getter
	private static String lobby_bungee;

	// TODO: Items display
	@Getter
	private static String display_compass;
	@Getter
	private static String display_profile;
	@Getter
	private static String display_shop;
	@Getter
	private static String display_lobbies;
	@Getter
	private static String display_players_on;
	@Getter
	private static String display_players_off;

	// TODO: ItemStack
	@Getter
	private static MenuItem wait_kits;
	@Getter
	private static MenuItem wait_leave;
	@Getter
	private static MenuItem wait_insane;
	@Getter
	private static MenuItem spec_compass;
	@Getter
	private static MenuItem spec_settings;
	@Getter
	private static MenuItem spec_again;
	@Getter
	private static MenuItem spec_leave;
	
	@Getter
	private static String megaNPCSkin, nSoloNPCSkin, iSoloNPCSkin, nTeamNPCSkin, iTeamNPCSkin;

	@Getter
	private static Integer[] refillTimes;
	@Getter
	private static int coins_kill, coins_win, countdown_start, countdown_full, countdown_game;
	@Getter
	private static int slot_compass, slot_profile, slot_shop, slot_lobbies, slot_players;
	@Getter
	private static int vote_dh_solo, vote_dh_team, vote_dh_mega;
	@Getter
	private static String schematic;

	public static void setup() {
		mode = Utils.getOptions().getBoolean("preferences.bungee-mode") ? ServerMode.BUNGEE : ServerMode.MULTI_ARENA;
		if (mode == ServerMode.BUNGEE && Utils.getOptions().getBoolean("preferences.lobby-mode"))
			mode = ServerMode.LOBBY;
		lobby_bungee = Utils.getOptions().getString("preferences.sw-lobby-bungee");

		use_mysql = Utils.getOptions().getBoolean("preferences.database.mysql");

		join_teleport = Utils.getOptions().getBoolean("preferences.join-teleport");
		on_join_items = Utils.getOptions().getBoolean("preferences.on-join-items");
		on_world_change_items = Utils.getOptions().getBoolean("preferences.on-world-change-items");
		worlds_allowed = Utils.getOptions().getStringList("preferences.worlds-lobby");
		rooms_bungee = Utils.getOptions().getStringList("preferences.rooms");

		receive_compass = Utils.getOptions().getBoolean("hot_bar.compass.receive");
		receive_profile = Utils.getOptions().getBoolean("hot_bar.skull_profile.receive");
		receive_shop = Utils.getOptions().getBoolean("hot_bar.shop.receive");
		receive_lobbies = Utils.getOptions().getBoolean("hot_bar.lobbies.receive");
		receive_players = Utils.getOptions().getBoolean("hot_bar.players.receive");

		material_compass = Utils.getOptions().getString("hot_bar.compass.material");
		material_shop = Utils.getOptions().getString("hot_bar.shop.material");
		material_lobbies = Utils.getOptions().getString("hot_bar.lobbies.material");

		display_compass = Utils.getOptions().getString("hot_bar.compass.display_name").replace("&", "§");
		display_profile = Utils.getOptions().getString("hot_bar.skull_profile.display_name").replace("&", "§");
		display_shop = Utils.getOptions().getString("hot_bar.shop.display_name").replace("&", "§");
		display_lobbies = Utils.getOptions().getString("hot_bar.lobbies.display_name").replace("&", "§");
		display_players_on = Utils.getOptions().getString("hot_bar.players.display_name_on").replace("&", "§");
		display_players_off = Utils.getOptions().getString("hot_bar.players.display_name_off").replace("&", "§");
		
		if (!Utils.getOptions().contains("hot_bar-wait.insane")) {
			Utils.getOptions().set("hot_bar-wait.insane.slot", 1);
			Utils.getOptions().set("hot_bar-wait.insane.item", "BLAZE_POWDER : 1 : name=&6Insane Options &7(Right Click)");
		}
		
		wait_kits = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-wait.kits.item")), Utils.getOptions().getInt("hot_bar-wait.kits.slot"));
		wait_insane = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-wait.insane.item")), Utils.getOptions().getInt("hot_bar-wait.insane.slot"));
		wait_leave = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-wait.leave.item")), Utils.getOptions().getInt("hot_bar-wait.leave.slot"));

		spec_compass = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-spec.compass.item")), Utils.getOptions().getInt("hot_bar-spec.compass.slot"));
		spec_settings = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-spec.settings.item")), Utils.getOptions().getInt("hot_bar-spec.settings.slot"));
		spec_again = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-spec.again.item")), Utils.getOptions().getInt("hot_bar-spec.again.slot"));
		spec_leave = new MenuItem(ItemUtils.createItem(Utils.getOptions().getString("hot_bar-spec.leave.item")), Utils.getOptions().getInt("hot_bar-spec.leave.slot"));

		coins_win = Utils.getOptions().getConfig().getInt("preferences.coins.win", 10);
		coins_kill = Utils.getOptions().getConfig().getInt("preferences.coins.kill", 2);

		countdown_start = Utils.getOptions().getConfig().getInt("preferences.countdown.start", 45);
		countdown_full = Utils.getOptions().getConfig().getInt("preferences.countdown.full", 10);
		countdown_game = Utils.getOptions().getConfig().getInt("preferences.countdown.game", 45);
		refillTimes = Utils.getOptions().getConfig().getIntegerList("preferences.countdown.refills").toArray(new Integer[Utils.getOptions().getConfig().getIntegerList("preferences.countdown.refillTimes").size()]);
		
		Utils.getOptions().getConfig().addDefault("preferences.npc-skin.solo.normal", "_Nemo");
		Utils.getOptions().getConfig().addDefault("preferences.npc-skin.solo.insane", "_Nemo");
		Utils.getOptions().getConfig().addDefault("preferences.npc-skin.team.normal", "_Nemo");
		Utils.getOptions().getConfig().addDefault("preferences.npc-skin.team.insane", "_Nemo");
		Utils.getOptions().getConfig().addDefault("preferences.npc-skin.mega", "_Nemo");
		
		nSoloNPCSkin = Utils.getOptions().getString("preferences.npc-skin.solo.normal");
		iSoloNPCSkin = Utils.getOptions().getString("preferences.npc-skin.solo.insane");
		nTeamNPCSkin = Utils.getOptions().getString("preferences.npc-skin.team.normal");
		iTeamNPCSkin = Utils.getOptions().getString("preferences.npc-skin.team.insane");
		megaNPCSkin = Utils.getOptions().getString("preferences.npc-skin.mega");
		
		if (!Utils.getOptions().contains("hot_bar.compass.slot")) {
			Utils.getOptions().set("hot_bar.compass.slot", 0);
		}
		if (!Utils.getOptions().contains("hot_bar.skull_profile.slot")) {
			Utils.getOptions().set("hot_bar.skull_profile.slot", 1);
		}
		if (!Utils.getOptions().contains("hot_bar.shop.slot")) {
			Utils.getOptions().set("hot_bar.shop.slot", 2);
		}
		if (!Utils.getOptions().contains("hot_bar.players.slot")) {
			Utils.getOptions().set("hot_bar.players.slot", 7);
		}
		if (!Utils.getOptions().contains("hot_bar.lobbies.slot")) {
			Utils.getOptions().set("hot_bar.lobbies.slot", 8);
		}
		
		slot_compass = Utils.getOptions().getConfig().getInt("hot_bar.compass.slot");
		slot_profile = Utils.getOptions().getConfig().getInt("hot_bar.skull_profile.slot");
		slot_shop = Utils.getOptions().getConfig().getInt("hot_bar.shop.slot");
		slot_players = Utils.getOptions().getConfig().getInt("hot_bar.players.slot");
		slot_lobbies = Utils.getOptions().getConfig().getInt("hot_bar.lobbies.slot");
		
		if (!Utils.getOptions().contains("preferences.votes.double_health.solo")) {
			Utils.getOptions().set("preferences.votes.double_health.solo", 3);
		}
		if (!Utils.getOptions().contains("preferences.votes.double_health.team")) {
			Utils.getOptions().set("preferences.votes.double_health.team", 6);
		}
		if (!Utils.getOptions().contains("preferences.votes.double_health.mega")) {
			Utils.getOptions().set("preferences.votes.double_health.mega", 9);
		}
		
		vote_dh_solo = Utils.getOptions().getInt("preferences.votes.double_health.solo");
		vote_dh_team = Utils.getOptions().getInt("preferences.votes.double_health.team");
		vote_dh_mega = Utils.getOptions().getInt("preferences.votes.double_health.mega");
		
		if (!Utils.getOptions().contains("preferences.schematic")) {
			Utils.getOptions().set("preferences.schematic", "plugins/WorldEdit/schematics/sw-prelobby.schematic");
		}
		
		schematic = Utils.getOptions().getString("preferences.schematic");
	}
}
