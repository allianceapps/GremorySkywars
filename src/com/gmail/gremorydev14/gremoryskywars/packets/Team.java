package com.gmail.gremorydev14.gremoryskywars.packets;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Team {

	private static int ID = 0;

	private final ArrayList<String> members = new ArrayList<>();
	private String name;
	private String prefix = "";
	private String suffix = "";

	public Team(String prefix, String suffix, String name) {
		this.name = name == null ? "[TEAM:" + (++ID) + "]" : name + (++ID);
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public void addMember(String player) {
		if (!members.contains(player))
			members.add(player);
	}

	public boolean isSimilar(String prefix, String suffix) {
		return this.prefix.equals(prefix) && this.suffix.equals(suffix);
	}
}