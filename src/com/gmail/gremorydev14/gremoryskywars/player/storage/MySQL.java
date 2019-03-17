package com.gmail.gremorydev14.gremoryskywars.player.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gmail.gremorydev14.gremoryskywars.util.Logger;

public class MySQL implements Sql {

	private Connection connection;
	private String host, database, username, password;
	private int port;

	public MySQL(String host, int port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		try {
			this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			this.createTable();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.warn("Failed to create mysql connection");
		}
	}

	public Connection getConnection() {
		if (!isConnected())
			try {
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
				Logger.warn("Failed to create mysql connection");
			}
		return connection;
	}

	public boolean deconnection() {
		if (!isConnected())
			return false;
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.warn("Failed to deconnect of mysql");
			return false;
		}
	}

	public boolean isConnected() {
		try {
			if (connection == null || connection.isClosed() || !connection.isValid(5))
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.warn("Failed to check if mysql is connected");
			return false;
		}
	}

	public void execute(String query, boolean flag, Object... objects) {
		try {
			PreparedStatement ps = this.connection.prepareStatement(query);
			for (int i = 0; i < objects.length; i++)
				ps.setObject(i + 1, objects[i]);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTable() {
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
}
