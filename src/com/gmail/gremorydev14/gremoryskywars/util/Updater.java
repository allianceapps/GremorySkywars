package com.gmail.gremorydev14.gremoryskywars.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.Plugin;

public class Updater {

	public Updater(Plugin plugin, int resourceId) {
		init(plugin, resourceId);
	}

	public void init(Plugin plugin, int resourceId) {
		Logger.info("§bIniciando PL PRINCIPAL");

		String latest = getVersion(resourceId);
		String currentVersion = plugin.getDescription().getVersion();
		if (latest == null) {
			Logger.info("§bModificado Por DayennCraft e Feito por TalonDev");
		} else {
			int siteVersion = Integer.parseInt(latest.replace(".", ""));
			int current = Integer.parseInt(currentVersion.replace(".", ""));

			if (current >= siteVersion) {
				Logger.info("Voce Pode Checar Atualizacoes no GitHub");
			} else {
				Logger.info("Em Breve Sistema Automatico");
			}
		}
	}

	private String getVersion(int resourceId) {
		String version = null;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://github.com/dc/plugins/naoexisteaindamano.php").openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
		} catch (IOException ex) {
			return null;
		}
		return version;
	}
}
