package com.gmail.gremorydev14.profile.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor;
import com.gmail.gremorydev14.gremoryskywars.editor.MenuEditor.MenuItem;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.ItemUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.inventory.MenuPaged;
import com.gmail.gremorydev14.profile.Booster;
import com.gmail.gremorydev14.profile.Level;
import com.gmail.gremorydev14.profile.Level.LevelReward.BoosterReward;
import com.gmail.gremorydev14.profile.Level.LevelReward.CoinsReward;
import com.gmail.gremorydev14.profile.Profile;

public class LevelingMenu extends MenuPaged {

	private static MenuItem back, on, off, l_off, leveling;

	private Map<ItemStack, Level> level = new HashMap<>();

	public LevelingMenu(Player p) {
		super(p, "SkyWars Leveling", 54);
		this.setSlotNext(26);
		this.setSlotPrevious(18);
		this.addNoItems(null, 18);

		Profile pf = Profile.get(p);
		List<ItemStack> all = new ArrayList<>();
		for (Level level : Level.getLevels()) {
			String string = level.getLevel() < pf.getLevel() ? off.getBuild() : level.getLevel() > pf.getLevel() ? l_off.getBuild() : pf.isUsed() ? off.getBuild() : on.getBuild();
			if (level.getReward() instanceof CoinsReward)
				string = string.replace("%level%", "" + level.getLevel()).replace("%reward%", MenuEditor.getReward_coins().replace("%coins%", "" + ((CoinsReward) level.getReward()).getCoins())).replace("%rewarddesc%",
						MenuEditor.getDesc_coins());
			else {
				Booster b = Booster.get(((BoosterReward) level.getReward()).getId());
				string = string.replace("%level%", "" + level.getLevel()).replace("%reward%", MenuEditor.getReward_booster().replace("%multiplier%", "" + b.getType().getModifier()).replace("%time%", "" + b.getTime()).replace("%timeType%",
						"" + b.getTimeType().getName().replace(b.getTime() > 1 ? "" : "s", ""))).replace("%rewarddesc%", MenuEditor.getDesc_booster());
			}
			ItemStack item = ItemUtils.createItem(string);
			this.level.put(item, level);
			all.add(item);
		}
		this.addNoItems(ItemUtils.createItem(leveling.getBuild().replace("%untilxp%", Level.getNext(pf.getLevel()).getXp() - pf.getXp() <= 0 ? "§cMax" : Utils.decimal(Level.getNext(pf.getLevel()).getXp() - pf.getXp()))
				.replace("%progress_bar%", progressBar(pf)).replace("%progress%", progress(pf)).replace("%level%", "" + pf.getLevel()).replace("%xp%", Utils.decimal(pf.getXp()))), 49);
		this.addItems(all, true, 0, 26);
		this.openPage(p, 1);
	}

	public static void setup() {
		Map<String, MenuItem> items = MenuEditor.getItems();
		back = items.get("back");
		leveling = items.get("p_leveling");
		on = items.get("l_level_reward_on");
		off = items.get("l_level_reward_off");
		l_off = items.get("l_level_reward_level_off");
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

	public void onClick(Player p, ItemStack item, int slot) {
		Profile pf = Profile.get(p);
		PlayerData pd = PlayerData.get(p);
		if (pf == null || pd == null) {
			p.closeInventory();
			return;
		}
		if (item.getItemMeta().getDisplayName().contains(MenuEditor.getItems().get("page").getDisplay())) {
			if (slot == 26)
				this.openPage(p, currentPage + 1);
			else if (slot == 18)
				this.openPage(p, currentPage - 1);
		} else if (item.equals(back.getItem())) {
			if (Main.getSound_orb() != null)
				p.playSound(p.getLocation(), Main.getSound_orb(), 1.0f, 1.0f);
			new ProfileMenu(p);
		} else {
			Level level = this.level.get(item);
			if (level != null) {
				if (level.getLevel() == pf.getLevel()) {
					if (pf.isUsed()) {
						if (Main.getEnderman() != null) {
							p.playSound(p.getLocation(), Main.getEnderman(), 1.0f, 1.0f);
						}
						p.sendMessage(Language.messages$player$already_claimed_level);
					} else {
						p.closeInventory();
						pf.setUsed(true);
						if (Main.getLevel() != null) {
							p.playSound(p.getLocation(), Main.getLevel(), 1.0f, 1.0f);
						}
						level.getReward().give(pf);
					}
				} else {
					if (Main.getEnderman() != null) {
						p.playSound(p.getLocation(), Main.getEnderman(), 1.0f, 1.0f);
					}
					
					p.sendMessage(Language.messages$player$no_level_to_claim);
				}
			}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (inventoryEquals(e.getInventory())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getWhoClicked() instanceof Player && getPlayer() == (Player) e.getWhoClicked()) {
				onClick((Player) e.getWhoClicked(), e.getCurrentItem(), e.getSlot());
			}
		}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if (inventoryEquals(e.getInventory()) && e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer() == getPlayer())
			HandlerList.unregisterAll(this);
	}
}
