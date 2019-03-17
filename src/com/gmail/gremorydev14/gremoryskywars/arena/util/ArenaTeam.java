package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;

public class ArenaTeam {
	
	private Arena arena;
	private String name;
	private String spawn;
	private List<Player> members = new ArrayList<>();
	
	public ArenaTeam(Arena arena, Location spawn) {
		this.arena = arena;
		this.spawn = Utils.serializeLocation(spawn);
	}
	
	public ArenaTeam(Arena arena, Location spawn, String name) {
		this(arena, spawn);
		this.name = "[" + letters[arena.getSpawns().size()] + "]";
	}
	
	public void addPlayer(Player p) {
		this.members.add(p);
	}
	
	public void removePlayer(Player p) {
		this.members.remove(p);
	}
	
	public boolean hasPlayer(Player p) {
		return members.contains(p);
	}
	
	public void reset() {
		this.members.clear();
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return name;
	}
	
	public Location getSpawnLocation() {
		return Utils.unserializeLocation(spawn);
	}
	
	public List<Player> getMembers() {
		return members;
	}
	
	private String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "X", "W", "Y", "Z" };
}
