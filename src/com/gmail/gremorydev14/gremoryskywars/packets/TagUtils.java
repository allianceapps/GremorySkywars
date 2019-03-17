package com.gmail.gremorydev14.gremoryskywars.packets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings("all")
public class TagUtils {

	private static final HashMap<String, Team> TEAMS = new HashMap<>();
	private static final HashMap<String, Team> CACHED_FAKE_TEAMS = new HashMap<>();

	private static String getNameFromInput(int input) {
		if (input < 0)
			return null;
		String letter = String.valueOf((char) ((input / 13) + 65));
		int repeat = input % 13 + 1;
		return StringUtils.repeat(letter, repeat);
	}

	private static Team getTeam(String prefix, String suffix) {
		for (Team fakeTeam : TEAMS.values())
			if (fakeTeam.isSimilar(prefix, suffix))
				return fakeTeam;
		return null;
	}
	
	private static void addPlayerToTeam(String p, String prefix, Player... players) {
		Team joining = new Team(prefix, "", "");
		joining.addMember(p);
		TEAMS.put(joining.getName(), joining);
		addTeamPackets(joining);
		Player adding = Bukkit.getPlayerExact(p);
		if (adding != null) {
			addPlayerToTeamPackets(joining, adding.getName(), players);
			cache(adding.getName(), joining);
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p);
			addPlayerToTeamPackets(joining, offlinePlayer.getName(), players);
			cache(offlinePlayer.getName(), joining);
		}
	}

	private static void addPlayerToTeam(String p, String prefix, String suffix, int sortPriority) {
		reset(p);
		Team joining = getTeam(prefix, suffix);
		if (joining != null) {
			joining.addMember(p);
		} else {
			joining = new Team(prefix, suffix, getNameFromInput(sortPriority));
			joining.addMember(p);
			TEAMS.put(joining.getName(), joining);
			addTeamPackets(joining);
		}
		Player adding = Bukkit.getPlayerExact(p);
		if (adding != null) {
			addPlayerToTeamPackets(joining, adding.getName());
			cache(adding.getName(), joining);
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p);
			addPlayerToTeamPackets(joining, offlinePlayer.getName());
			cache(offlinePlayer.getName(), joining);
		}
	}

	public static Team reset(String p) {
		return reset(p, decache(p));
	}

	private static Team reset(String p, Team fakeTeam) {
		if (fakeTeam != null && fakeTeam.getMembers().remove(p)) {
			boolean delete;
			Player removing = Bukkit.getPlayerExact(p);
			if (removing != null) {
				delete = removePlayerFromTeamPackets(fakeTeam, removing.getName());
			} else {
				OfflinePlayer toRemoveOffline = Bukkit.getOfflinePlayer(p);
				delete = removePlayerFromTeamPackets(fakeTeam, toRemoveOffline.getName());
			}
			if (delete) {
				removeTeamPackets(fakeTeam);
				TEAMS.remove(fakeTeam.getName());
			}
		}
		return fakeTeam;
	}

	private static Team decache(String p) {
		return CACHED_FAKE_TEAMS.remove(p);
	}

	public static Team getFakeTeam(String p) {
		return CACHED_FAKE_TEAMS.get(p);
	}

	private static void cache(String p, Team fakeTeam) {
		CACHED_FAKE_TEAMS.put(p, fakeTeam);
	}

	public static void setTag(String p, String prefix, String suffix) {
		setTag(p, prefix, suffix, -1);
	}

	public static void setTag(String p, String prefix, String suffix, int sortPriority) {
		addPlayerToTeam(p, prefix != null ? prefix : "", suffix != null ? suffix : "", sortPriority);
	}
	
	public static void setPrefixSingle(Player p, String prefix, Player... players) {
		addPlayerToTeam(p.getName(), prefix, players);
	}

	public static void sendTeams(Player p) {
		for (Team fakeTeam : TEAMS.values())
			new Wrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 0, fakeTeam.getMembers()).send(p);
	}

	public static void reset() {
		for (Team fakeTeam : TEAMS.values()) {
			removePlayerFromTeamPackets(fakeTeam, fakeTeam.getMembers());
			removeTeamPackets(fakeTeam);
		}
		CACHED_FAKE_TEAMS.clear();
		TEAMS.clear();
	}

	/**
	 *
	 * Packet controllers and modifiers
	 */

	private static void removeTeamPackets(Team fakeTeam) {
		new Wrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 1, new ArrayList<>()).send();
	}

	private static boolean removePlayerFromTeamPackets(Team fakeTeam, String... players) {
		return removePlayerFromTeamPackets(fakeTeam, Arrays.asList(players));
	}

	private static boolean removePlayerFromTeamPackets(Team fakeTeam, List<String> players) {
		new Wrapper(fakeTeam.getName(), 4, players).send();
		fakeTeam.getMembers().removeAll(players);
		return fakeTeam.getMembers().isEmpty();
	}

	private static void addTeamPackets(Team fakeTeam) {
		new Wrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 0, fakeTeam.getMembers()).send();
	}

	private static void addTeamPackets(Team fakeTeam, Player... players) {
		for (Player p : players) {
			new Wrapper(fakeTeam.getName(), fakeTeam.getPrefix(), fakeTeam.getSuffix(), 0, fakeTeam.getMembers()).send(p);
		}
	}

	private static void addPlayerToTeamPackets(Team fakeTeam, String player) {
		new Wrapper(fakeTeam.getName(), 3, Collections.singletonList(player)).send();
	}

	private static void addPlayerToTeamPackets(Team fakeTeam, String player, Player... players) {
		for (Player p : players) {
			new Wrapper(fakeTeam.getName(), 3, Collections.singletonList(player)).send(p);
		}
	}
}