package com.gmail.gremorydev14.gremoryskywars.player.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;

import lombok.Getter;

public class SQLite implements Sql {

	@Getter
	private File file;
	private Connection connection;

	public SQLite(File file) {
		this.file = file;
		if (!this.file.exists())
			try {
				this.file.getParentFile().mkdir();
				this.file.createNewFile();
			} catch (Exception e) {
			}
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Failed to get sqlite driver");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.print("Failed to create sqlite connection");
		}
		this.createTable();
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed())
				try {
					this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
				} catch (SQLException e) {
					e.printStackTrace();
					Logger.warn("Failed to create sqlite connection");
				}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.warn("Failed to get sqlite connection");
		}
		return connection;
	}

	public void createTable() {
		try {
			this.connection.createStatement()
					.execute("CREATE TABLE IF NOT EXISTS g_sw (uuid VARCHAR(36), name VARCHAR(32), kSolo INTEGER, kTeam INTEGER, kMega INTEGER, wSolo INTEGER, wTeam INTEGER, wMega INTEGER, souls INTEGER, options VARCHAR(150));");
			this.connection.createStatement()
					.execute("CREATE TABLE IF NOT EXISTS g_profile (uuid VARCHAR(36), name VARCHAR(32), achievements_points INTEGER, xp INTEGER, level INTEGER, booster VARCHAR(255), time VARCHAR(100), levels VARCHAR(5));");
			this.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS g_inventory (uuid VARCHAR(36), name VARCHAR(32), skywars TEXT(1000));");
			this.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS g_delivery (uuid VARCHAR(36), name VARCHAR(32), deliveries TEXT(1000));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute(String query, boolean flag, Object... objects) {
		try {
			PreparedStatement ps = this.connection.prepareStatement(query);
			for (int i = 0; i < objects.length; i++)
				ps.setObject(i + 1, objects[i]);
			if (flag)
				queue(ps);
			else {
				ps.execute();
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void queue(PreparedStatement ps) {
		new BukkitRunnable() {
			public void run() {
				try {
					ps.execute();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}
}
