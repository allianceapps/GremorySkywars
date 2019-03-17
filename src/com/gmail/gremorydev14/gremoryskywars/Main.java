package com.gmail.gremorydev14.gremoryskywars;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.gremorydev14.gremoryskywars.listeners.MultiArenaListeners;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.leaderboards.LeaderBoard;
import lombok.Getter;

public class Main extends JavaPlugin {

	@Getter
	private static Sound sound_orb, enderman, level, chest;

	public void onEnable() {
		Utils.setup();
		
		getCommand("lobby").setExecutor(new MultiArenaListeners());
		String ver = Reflection.getVersion();
		if (ver.startsWith("v1_8")) {
			sound_orb = Sound.valueOf("ORB_PICKUP");
			enderman = Sound.valueOf("ENDERMAN_TELEPORT");
			level = Sound.valueOf("LEVEL_UP");
			chest = Sound.valueOf("CHEST_OPEN");
		}

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	public void onDisable() {
		for (PlayerData pd : PlayerData.values()) {
			pd.save();
		}
		for (LeaderBoard b : LeaderBoard.values()) {
			b.destroy();
		}
		Logger.info("Plugin disabled");
	}

	public static Main getPlugin() {
		return getPlugin(Main.class);
	}
}
