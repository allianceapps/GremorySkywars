package com.gmail.gremorydev14.gremoryskywars.arena.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;

public class ArenaVote {

	private Arena arena;
	private VoteType type;
	private Map<SubVoteType, List<UUID>> votes = new HashMap<>();
	
	public ArenaVote(Arena arena, VoteType type)	{
		this.arena = arena;
		this.type = type;
		if (type == VoteType.HEALTH) {
			votes.put(SubVoteType.DOUBLE_HEALTH, new ArrayList<>());
		} else {
			votes.put(SubVoteType.DAY, new ArrayList<>());
			votes.put(SubVoteType.NIGHT, new ArrayList<>());
			votes.put(SubVoteType.AFTERNOON, new ArrayList<>());
			votes.put(SubVoteType.MIDNIGHT, new ArrayList<>());
		}
	}
	
	public void put(Player p, SubVoteType type) {
		votes.get(type).add(p.getUniqueId());
	}
	
	public void clear(Player p) {
		for (List<UUID> list : votes.values()) {
			if (list.contains(p.getUniqueId()))	{
				list.remove(p.getUniqueId());
			}
		}
	}
	
	public void clear() {
		votes.clear();
	}
	
	public boolean has(Player p, SubVoteType type) {
		return votes.get(type).contains(p.getUniqueId());
	}
	
	public VoteType getType() {
		return type;
	}
	
	public Map<SubVoteType, List<UUID>> getVotesMap() {
		return votes;
	}
	
	public SubVoteType getOrganizedVote() {
		List<String> list = new ArrayList<>();
		for (Entry<SubVoteType, List<UUID>> entry : votes.entrySet()) {
			if (entry.getValue().size() >= (entry.getKey() == SubVoteType.DOUBLE_HEALTH ? arena.getMode() == Mode.SOLO ? Options.getVote_dh_solo() : arena.getMode() == Mode.TEAM ? Options.getVote_dh_team() : Options.getVote_dh_mega() : 0)) {
				list.add(entry.getKey().name() + " " + entry.getValue().size());
			}
		}
		
		Collections.sort(list, new Comparator<String>() {
			public int compare(String a, String b) {
				int one = Integer.parseInt(a.split(" ")[1]);
				int two = Integer.parseInt(b.split(" ")[1]);
				return Integer.compare(one, two);
			}
		});
		
		return list.size() - 1 < 0 ? null : SubVoteType.valueOf(list.get(list.size() - 1).split(" ")[0]);
	}
	
	public static enum VoteType {
		HEALTH, TIME;
	}
	
	public static enum SubVoteType {
		DOUBLE_HEALTH(null), NIGHT(13000), DAY(1000), AFTERNOON(6000), MIDNIGHT(18000);
		
		private Object value;
		
		SubVoteType(Object value) {
			this.value = value;
		}
		
		public void apply(Arena arena) {
			arena.getWorld().setTime((int)value);
		}
		
		public void reset(Arena arena) {
			arena.getWorld().setTime(0);
		}
	}
}
