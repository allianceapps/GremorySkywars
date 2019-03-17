package com.gmail.gremorydev14.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Cage;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Kit;
import com.gmail.gremorydev14.gremoryskywars.arena.util.Perk;
import com.gmail.gremorydev14.gremoryskywars.player.storage.SQLite;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

import lombok.Getter;

@Getter
public class SwInventory {

	private Player player;
	private SQLDatabase storage;
	private List<Integer> sKits = new ArrayList<>();
	private List<Integer> tKits = new ArrayList<>();
	private List<Integer> mKits = new ArrayList<>();
	private List<Integer> sPerks = new ArrayList<>();
	private List<Integer> tPerks = new ArrayList<>();
	private List<Integer> mPerks = new ArrayList<>();
	private List<Integer> cages = new ArrayList<>();

	public SwInventory(Player p) {
		this.player = p;
		this.storage = new SQLDatabase(p);
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				String[] inventory = storage.getString("skywars").split(" : ");

				String[] sKits = inventory[0].split(";");
				for (String kit : sKits)
					SwInventory.this.sKits.add(Integer.valueOf(kit));
				String[] tKits = inventory[1].split(";");
				for (String kit : tKits)
					SwInventory.this.tKits.add(Integer.valueOf(kit));
				String[] mKits = inventory[2].split(";");
				for (String kit : mKits)
					SwInventory.this.mKits.add(Integer.valueOf(kit));
				String[] cages = inventory[3].split(";");
				for (String cage : cages)
					SwInventory.this.cages.add(Integer.valueOf(cage));
				String[] sPerks = inventory[4].split(";");
				for (String perk : sPerks)
					SwInventory.this.sPerks.add(Integer.valueOf(perk));
				String[] tPerks = inventory[5].split(";");
				for (String perk : tPerks)
					SwInventory.this.tPerks.add(Integer.valueOf(perk));
				String[] mPerks = inventory[6].split(";");
				for (String perk : mPerks)
					SwInventory.this.mPerks.add(Integer.valueOf(perk));
			}
		});
	}

	public void addKit(Kit kit, Mode mode) {
		if (mode == Mode.SOLO)
			sKits.add(kit.getId());
		else if (mode == Mode.TEAM)
			tKits.add(kit.getId());
		else
			mKits.add(kit.getId());
	}

	public void addPerk(Perk perk, Mode mode) {
		if (mode == Mode.SOLO)
			sPerks.add(perk.getId());
		else if (mode == Mode.TEAM)
			tPerks.add(perk.getId());
		else
			mPerks.add(perk.getId());
	}

	public void addCage(Cage cage) {
		cages.add(cage.getId());
	}

	public void save() {
		Utils.getStorage().execute("UPDATE g_inventory SET skywars=? WHERE uuid=?", false, StringUtils.join(sKits, ";") + " : " + StringUtils.join(tKits, ";") + " : " + StringUtils.join(mKits, ";") + " : " + StringUtils.join(cages, ";")
				+ " : " + StringUtils.join(sPerks, ";") + " : " + StringUtils.join(tPerks, ";") + " : " + StringUtils.join(mPerks, ";"), player.getUniqueId().toString());
	}

	public void saveAsync() {
		Utils.getStorage().execute("UPDATE g_inventory SET skywars=? WHERE uuid=?", (Utils.getStorage() instanceof SQLite), StringUtils.join(sKits, ";") + " : " + StringUtils.join(tKits, ";") + " : " + StringUtils.join(mKits, ";") + " : "
				+ StringUtils.join(cages, ";") + " : " + StringUtils.join(sPerks, ";") + " : " + StringUtils.join(tPerks, ";") + " : " + StringUtils.join(mPerks, ";"), player.getUniqueId().toString());
	}

	private static Map<UUID, SwInventory> inventories = new HashMap<>();

	public static void create(Player p) {
		inventories.put(p.getUniqueId(), new SwInventory(p));
	}

	public static void delete(Player p) {
		if (get(p) != null)
			inventories.remove(p.getUniqueId());
	}

	public static SwInventory get(Player p) {
		return inventories.get(p.getUniqueId());
	}

	public static Collection<SwInventory> values() {
		return inventories.values();
	}
}
