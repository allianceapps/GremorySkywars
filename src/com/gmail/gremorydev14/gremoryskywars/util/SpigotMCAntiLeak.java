package com.gmail.gremorydev14.gremoryskywars.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import lombok.Getter;

public class SpigotMCAntiLeak {

	private String so_cachaca = "%%__USER__%%";
	@Getter
	private boolean leaked = false;

	public SpigotMCAntiLeak() {
		try {
			String str = createUrl(Logger.get());
			URLConnection url = new URL(str).openConnection();
			url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			url.connect();

			BufferedReader br = new BufferedReader(new InputStreamReader(url.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder builder = new StringBuilder();
			String append;
			while ((append = br.readLine()) != null)
				builder.append(append);
			if (builder.toString().contains(so_cachaca)) {
				leaked = true;
				return;
			}
		} catch (Exception e) {
		}
	}

	private String createUrl(byte[] bytes) {
		return new String(bytes);
	}
}
