package com.gmail.gremorydev14.gremoryskywars.util.file;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

import lombok.Getter;

public class SettingsManager {

	@Getter
	private FileConfiguration config;
	@Getter
	private File file;

	public SettingsManager(String name) {
		this.file = new File(Main.getPlugin().getDataFolder(), name + ".yml");
		if (!this.file.exists()) {
			try {
				this.file.getParentFile().mkdir();
				this.file.createNewFile();
				Utils.copyFile(Main.getPlugin().getResource(name + ".yml"), file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public SettingsManager(String name, String path) {
		this.file = new File(path + "/" + name + ".yml");
		if (!this.file.exists()) {
			try {
				this.file.getParentFile().mkdir();
				this.file.createNewFile();
				Utils.copyFile(Main.getPlugin().getResource(name + ".yml"), file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public boolean createSection(String path) {
		this.config.createSection(path);
		return save();
	}

	public boolean set(String path, Object obj) {
		this.config.set(path, obj);
		return save();
	}

	public boolean contains(String path) {
		return this.config.contains(path);
	}

	public Object get(String path) {
		return this.config.get(path);
	}

	public int getInt(String path) {
		return this.config.getInt(path);
	}

	public String getString(String path) {
		return this.config.getString(path);
	}

	public boolean getBoolean(String path) {
		return this.config.getBoolean(path);
	}

	public List<String> getStringList(String path) {
		return this.config.getStringList(path);
	}

	public ConfigurationSection getSection(String path) {
		return this.config.getConfigurationSection(path);
	}

	public boolean save() {
		try {
			this.config.save(this.file);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected static Map<String, SettingsManager> configs = new HashMap<>(), configs2 = new HashMap<>();

	public static SettingsManager getConfig(String name) {
		if (configs.containsKey(name))
			return configs.get(name);
		configs.put(name, new SettingsManager(name));
		return configs.get(name);
	}
	
	public static SettingsManager getConfig(String name, String path) {
		if (configs.containsKey(name))
			return configs.get(name);
		configs.put(name, new SettingsManager(name, path));
		return configs.get(name);
	}
	
	public static SettingsManager getConfig2(String name, String path) {
		if (configs2.containsKey(name))
			return configs2.get(name);
		configs2.put(name, new SettingsManager(name, path));
		return configs2.get(name);
	}
}
