package com.gmail.gremorydev14.delivery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.Utils;

public class SQLDatabase {

	private Player player;
	private Connection connection;

	public SQLDatabase(Player p) {
		this.player = p;
		this.connection = Utils.getStorage().getConnection();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT name FROM g_delivery WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				ps.close();
				String sql = "INSERT INTO g_delivery (uuid, name, deliveries)";
				ps = connection.prepareStatement(sql + " VALUES (?, ?, ?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, p.getName());
				ps.setString(3, "0 : 0");
				ps.executeUpdate();
				ps.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Failed to create account using sql!");
		}
	}

	public boolean set(String path, Object value) {
		/** prevents lag */
		return false;
	}

	public Object get(String path) {
		Object obj = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT " + path + " FROM g_delivery WHERE uuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next())
				return obj;
			obj = rs.getObject(path);
			ps.close();
			rs.close();
			return obj;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Failed to get value in sql!");
			return obj;
		}
	}

	public int getInt(String path) {
		int number = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT " + path + " FROM g_delivery WHERE uuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next())
				return number;
			number = rs.getInt(path);
			ps.close();
			rs.close();
			return number;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Failed to get int in sql!");
			return number;
		}
	}

	public double getDouble(String path) {
		double number = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT " + path + " FROM g_delivery WHERE uuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next())
				return number;
			number = rs.getDouble(path);
			ps.close();
			rs.close();
			return number;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("�aFailed to get int in sql!");
			return number;
		}
	}

	public long getLong(String path) {
		long number = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT " + path + " FROM g_delivery WHERE uuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next())
				return number;
			number = rs.getLong(path);
			ps.close();
			rs.close();
			return number;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Failed to get long in sql!");
			return number;
		}
	}

	public String getString(String path) {
		String str = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT " + path + " FROM g_delivery WHERE uuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if (!rs.next())
				return str;
			str = rs.getString(path);
			ps.close();
			rs.close();
			return str;
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Failed to get string in sql:");
			return str;
		}
	}
}
