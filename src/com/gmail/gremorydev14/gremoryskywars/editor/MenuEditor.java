package com.gmail.gremorydev14.gremoryskywars.editor;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.menus.ArenaVotesMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.CosmeticsMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.InsaneMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.ShopMenu;
import com.gmail.gremorydev14.gremoryskywars.menus.SkywarsAchievementsMenu;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;
import com.gmail.gremorydev14.profile.menus.AchievementMenu;
import com.gmail.gremorydev14.profile.menus.BoostersMenu;
import com.gmail.gremorydev14.profile.menus.LevelingMenu;
import com.gmail.gremorydev14.profile.menus.PreferencesMenu;
import com.gmail.gremorydev14.profile.menus.PrivateBoosters;
import com.gmail.gremorydev14.profile.menus.ProfileMenu;
import com.gmail.gremorydev14.profile.menus.StatisticsMenu;

import lombok.Getter;

public class MenuEditor {

	@Getter
	private static String desc_coins, desc_booster;
	@Getter
	private static String reward_coins, reward_booster;
	@Getter
	private static Map<String, MenuItem> items = new HashMap<>();

	public static void setup() {
		SettingsManager sm = Utils.getMenus();

		// ALL
		items.put("back", new MenuItem(ItemUtils.createItem(sm.getString("menus.back")), 0));
		items.put("page", new MenuItem(sm.getString("menus.page"), 0));
		// PROFILE
		items.put("p_preferences", new MenuItem(ItemUtils.createItem(sm.getString("menus.profile.preferences.item")), sm.getInt("menus.profile.preferences.slot")));
		items.put("p_informations", new MenuItem(sm.getString("menus.profile.informations.item"), sm.getInt("menus.profile.informations.slot")));
		items.put("p_statistics", new MenuItem(ItemUtils.createItem(sm.getString("menus.profile.statistics.item")), sm.getInt("menus.profile.statistics.slot")));
		items.put("p_delievery", new MenuItem(ItemUtils.createItem(sm.getString("menus.profile.delievery.item")), sm.getInt("menus.profile.delievery.slot")));
		items.put("p_achievements", new MenuItem(ItemUtils.createItem(sm.getString("menus.profile.achievements.item")), sm.getInt("menus.profile.achievements.slot")));
		items.put("p_booster", new MenuItem(ItemUtils.createItem(sm.getString("menus.profile.booster.item")), sm.getInt("menus.profile.booster.slot")));
		items.put("p_leveling", new MenuItem(sm.getString("menus.profile.leveling.item"), sm.getInt("menus.profile.leveling.slot")));
		// PREFERENCES
		items.put("pr_on", new MenuItem(ItemUtils.createItem(sm.getString("menus.preferences.on_item")), 0));
		items.put("pr_off", new MenuItem(ItemUtils.createItem(sm.getString("menus.preferences.off_item")), 0));
		items.put("pr_players", new MenuItem(ItemUtils.createItem(sm.getString("menus.preferences.players_view")), 0));
		items.put("pr_tell", new MenuItem(ItemUtils.createItem(sm.getString("menus.preferences.tell")), 0));
		// BOOSTER
		items.put("b_pbooster", new MenuItem(ItemUtils.createItem(sm.getString("menus.booster.personal_booster.item")), sm.getInt("menus.booster.personal_booster.slot")));
		items.put("b_nbooster", new MenuItem(ItemUtils.createItem(sm.getString("menus.booster.network_booster.item")), sm.getInt("menus.booster.network_booster.slot")));
		items.put("b_booster", new MenuItem(sm.getString("menus.booster.booster_item"), 0));
		// STATISTICS
		items.put("s_total", new MenuItem(sm.getString("menus.statistics.total"), 0));
		items.put("s_player", new MenuItem(sm.getString("menus.statistics.player"), 0));
		// ACHIEVEMENT
		items.put("a_points", new MenuItem(sm.getString("menus.achievements.points"), 0));
		items.put("a_skywars", new MenuItem(ItemUtils.createItem(sm.getString("menus.achievements.skywars")), 0));
		items.put("a_locked", new MenuItem(sm.getString("menus.achievements.locked"), 0));
		items.put("a_unlocked", new MenuItem(sm.getString("menus.achievements.unlocked"), 0));
		// LEVELING
		items.put("l_level_reward_on", new MenuItem(sm.getString("menus.leveling.level_reward_on"), 0));
		items.put("l_level_reward_off", new MenuItem(sm.getString("menus.leveling.level_reward_off"), 0));
		items.put("l_level_reward_level_off", new MenuItem(sm.getString("menus.leveling.level_reward_level_off"), 0));
		// SHOP
		items.put("sh_sKits", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.kits_solo.item")), sm.getInt("menus.shop.kits_solo.slot")));
		items.put("sh_tKits", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.kits_team.item")), sm.getInt("menus.shop.kits_team.slot")));
		items.put("sh_mKits", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.kits_mega.item")), sm.getInt("menus.shop.kits_mega.slot")));
		items.put("sh_sPerks", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.perks_solo.item")), sm.getInt("menus.shop.perks_solo.slot")));
		items.put("sh_tPerks", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.perks_team.item")), sm.getInt("menus.shop.perks_team.slot")));
		items.put("sh_mPerks", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.perks_mega.item")), sm.getInt("menus.shop.perks_mega.slot")));
		items.put("sh_Cosmetics", new MenuItem(ItemUtils.createItem(sm.getString("menus.shop.cosmetics.item")), sm.getInt("menus.shop.cosmetics.slot")));
		items.put("sh_Coins", new MenuItem(sm.getString("menus.shop.coins"), 0));
		// COSMETICS
		items.put("c_Cages", new MenuItem(sm.getString("menus.cosmetics.cages.item"), sm.getInt("menus.cosmetics.cages.slot")));
		items.put("c_Perks", new MenuItem(sm.getString("menus.cosmetics.perks.item"), sm.getInt("menus.cosmetics.perks.slot")));
		// INSANE
		items.put("in_votes", new MenuItem(ItemUtils.createItem(sm.getString("menus.insane.votes.item")), sm.getInt("menus.insane.votes.slot")));
		// VOTES
		items.put("v_double", new MenuItem(ItemUtils.createItem(sm.getString("menus.votes.double_health.item")), sm.getInt("menus.votes.double_health.slot")));
		
		// SETUP PROFILE
		ProfileMenu.setup();
		PreferencesMenu.setup();
		BoostersMenu.setup();
		PrivateBoosters.setup();
		StatisticsMenu.setup();
		AchievementMenu.setup();
		LevelingMenu.setup();
		SkywarsAchievementsMenu.setup();
		InsaneMenu.setup();
		ArenaVotesMenu.setup();
		ShopMenu.setup();
		CosmeticsMenu.setup();

		// LEVELING STRING
		desc_coins = sm.getString("menus.leveling.desc.coins");
		desc_booster = sm.getString("menus.leveling.desc.booster");
		reward_coins = sm.getString("menus.leveling.reward.coins");
		reward_booster = sm.getString("menus.leveling.reward.booster");
	}

	@Getter
	public static class MenuItem {
		private int slot;
		private ItemStack item;
		private String display, build;

		public MenuItem(ItemStack item, int slot) {
			this.slot = slot;
			this.item = item;
			this.display = item.getItemMeta().getDisplayName();
		}

		public MenuItem(String serialized, int slot) {
			this.slot = slot;
			this.build = serialized;
			this.display = serialized.split("name=")[1].replace("&", "§").replace("%page%", "").split(" : ")[0];
		}
	}
}
