package com.gmail.gremorydev14.profile.menus;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.Menu;
import com.gmail.gremorydev14.profile.Level;
import com.gmail.gremorydev14.profile.Profile;
import com.gmail.gremorydev14.profile.Rank;

public class ProfileMenu extends Menu {

	private static String leveling_dis;
	private static MenuItem achievements, delievery, leveling, booster, statistics, informations, preferences;

	public ProfileMenu(Player p) {
		super(p, "My Profile", 5);
		Profile pf = Profile.get(p);
		if (achievements.getSlot() > -1)
			getInventory().setItem(achievements.getSlot(), achievements.getItem());
		if (delievery.getSlot() > -1)
			getInventory().setItem(delievery.getSlot(), delievery.getItem());
		if (leveling.getSlot() > -1)
			getInventory().setItem(leveling.getSlot(),
					ItemUtils.createItem(leveling.getBuild().replace("%untilxp%", Level.getNext(pf.getLevel()).getXp() - pf.getXp() <= 0 ? "§cMax" : Utils.decimal(Level.getNext(pf.getLevel()).getXp() - pf.getXp()))
							.replace("%progress_bar%", progressBar(pf)).replace("%progress%", progress(pf)).replace("%level%", "" + pf.getLevel()).replace("%xp%", Utils.decimal(pf.getXp()))));
		if (booster.getSlot() > -1)
			getInventory().setItem(booster.getSlot(), booster.getItem());
		if (statistics.getSlot() > -1)
			getInventory().setItem(statistics.getSlot(), statistics.getItem());
		if (informations.getSlot() > -1)
			getInventory().setItem(informations.getSlot(), ItemUtils.createSkull(informations.getBuild().replace("%player%", p.getName()).replace("%level%", "" + pf.getLevel()).replace("%points%", Utils.decimal(pf.getAchievements_points()))
					.replace("%untilxp%", Level.getNext(pf.getLevel()).getXp() - pf.getXp() <= 0 ? "§cMax" : Utils.decimal(Level.getNext(pf.getLevel()).getXp() - pf.getXp())).replace("%rank%", Rank.getRank(p).getDisplay())));
		if (preferences.getSlot() > -1)
			getInventory().setItem(preferences.getSlot(), preferences.getItem());
		p.openInventory(getInventory());
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		achievements = items.get("p_achievements");
		delievery = items.get("p_delievery");
		leveling = items.get("p_leveling");
		booster = items.get("p_booster");
		statistics = items.get("p_statistics");
		informations = items.get("p_informations");
		preferences = items.get("p_preferences");
		leveling_dis = leveling.getDisplay();
	}

	public String progress(Profile pf) {
		return ((int) (pf.getXp() * 100.0) / Level.getNext(pf.getLevel()).getXp()) + "%";
	}

	public String progressBar(Profile pf) {
		String text = "";
		double percentage = (pf.getXp() * 100.0) / Level.getNext(pf.getLevel()).getXp();
		for (double d = 2.5; d <= 100.0; d += 2.5)
			if (percentage >= d)
				text += "§3|";
			else
				text += "§8|";
		return text;
	}

	@Override
	public void onClick(Player p, ItemStack item) {
		if (item.equals(preferences.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new PreferencesMenu(p);
		} else if (item.equals(statistics.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new StatisticsMenu(p);
		} else if (item.equals(booster.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new BoostersMenu(p);
		} else if (item.equals(achievements.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new AchievementMenu(p);
		} else if (item.getItemMeta().getDisplayName().contains(leveling_dis)) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new LevelingMenu(p);
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getInventory().equals(getInventory())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked() instanceof Player && getPlayer() == (Player) e.getWhoClicked()) {
				onClick((Player) e.getWhoClicked(), e.getCurrentItem());
			}
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (getInventory().equals(e.getInventory()) && e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
