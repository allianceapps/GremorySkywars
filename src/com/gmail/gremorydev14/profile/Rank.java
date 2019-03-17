package com.gmail.gremorydev14.profile;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.packets.TagUtils;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

import lombok.Getter;

@Getter
public class Rank {

	private int order;
	private String name;
	private String perm;
	private String tag;
	private String display;

	public Rank(String name, String tag, String perm, String display) {
		this.name = name;
		this.perm = perm;
		this.tag = tag.replace("&", "§");
		this.display = display;
		this.order = ranks.size();
		ranks.add(this);
	}

	public void apply(Player p) {
		TagUtils.setTag(p.getName(), tag, "", order);
		p.setDisplayName(tag + p.getName());
	}

	private static List<Rank> ranks = new ArrayList<>();

	public static void register() {
		for (String name : Utils.getRanks().getSection("ranks").getKeys(false)) {
			String tag = Utils.getRanks().getString("ranks." + name + ".tag");
			String perm = Utils.getRanks().getString("ranks." + name + ".perm");
			String display = Utils.getRanks().getString("ranks." + name + ".display");
			new Rank(name, tag, perm, display);
		}
		if (ranks.size() == 0) {
			ranks.add(new Rank("Default", "&7", "none", "&7Default"));
		}
		Logger.info("Loaded " + ranks.size() + " rank(s)!");
	}

	public static Rank getRank(Player p) {
		for (Rank rank : ranks) {
			if (p.hasPermission(rank.getPerm()) || rank.getPerm().equals("none")) {
				return rank;
			}
		}
		return null;
	}
}
