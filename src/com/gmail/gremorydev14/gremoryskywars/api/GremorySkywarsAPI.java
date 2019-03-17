package com.gmail.gremorydev14.gremoryskywars.api;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.Utils;

@SuppressWarnings("all")
public class GremorySkywarsAPI {

	public static void addCoins(Player p, double d) {
		Utils.getEcon().depositPlayer(p.getName(), d);
	}

	public static void removeCoins(Player p, double coins) {
		Utils.getEcon().withdrawPlayer(p.getName(), coins);
	}

	public static double getCoins(Player p) {
		return Utils.getEcon().getBalance(p.getName());
	}
}
