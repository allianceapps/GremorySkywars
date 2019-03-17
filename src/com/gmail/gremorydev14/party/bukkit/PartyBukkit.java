package com.gmail.gremorydev14.party.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;

public class PartyBukkit {
	
	private Player owner;
	private int maxSlots;
	private List<Player> members = new ArrayList<>();
	
	public PartyBukkit(Player owner) {
		this.owner = owner;
		if (owner.hasPermission("gremoryskywars.party.slot3")) {
			maxSlots = 3;
		} else if (owner.hasPermission("gremoryskywars.party.slot4")) {
			maxSlots = 4;
		} else if (owner.hasPermission("gremoryskywars.party.slot5")) {
			maxSlots = 5;
		} else {
			maxSlots = 2;
		}
	}
	
	public void invitePlayer(Player p) {
		invites.put(p, this);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
			String name = owner.getName();
			
			@Override
			public void run() {
				if (p.isOnline() && invites.get(p) != null && invites.get(p).equals(PartyBukkit.this)) {
					p.sendMessage(Language.messages$party$invite_expires.replace("%player%", name));
					invites.remove(p);
				}
			}
		}, 400);
	}
	
	public void addPlayer(Player p) {
		members.add(p);
	}
	
	public void removePlayer(Player p) {
		members.remove(p);
		if (members.size() > 0) {
			broadcastMessage(Language.messages$party$leave_broadcast.replace("%player%", p.getName()));
			if (owner.equals(p)) {
				owner = members.get(0);
				broadcastMessage(Language.messages$party$leader_change.replace("%player%", owner.getName()));
			}
		}
	}
	
	public void broadcastMessage(String message) {
		for (Player ps : members) {
			if (!owner.equals(ps)) {
				ps.sendMessage(message);
			}
		}
		owner.sendMessage(message);
	}
	
	public boolean hasPlayer(Player p) {
		return members.contains(p) || owner.equals(p);
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public List<Player> getMembers() {
		return members;
	}
	
	public List<String> getList() {
		List<String> list = new ArrayList<>();
		for (Player ps : members) {
			if (!owner.equals(ps)) {
				list.add(ps.getName());
			}
		}
		
		return list;
	}
	
	public int getSlots() {
		return members.contains(owner) ? members.size() : members.size() + 1;
	}
	
	public int getMaxSlots() {
		return maxSlots;
	}
	
	private static List<PartyBukkit> parties = new ArrayList<>();
	private static Map<Player, PartyBukkit> invites = new HashMap<>();
	
	public static void add(Player p) {
		parties.add(new PartyBukkit(p));
	}
	
	public static void remove(PartyBukkit party) {
		parties.remove(party);
	}
	
	public static PartyBukkit get(Player p) {
		for (PartyBukkit party : parties) {
			if (party.hasPlayer(p)) {
				return party;
			}
		}
		
		return null;
	}
	
	public static List<PartyBukkit> values() {
		return parties;
	}
	
	public static Map<Player, PartyBukkit> getInvitesMap() {
		return invites;
	}
}
