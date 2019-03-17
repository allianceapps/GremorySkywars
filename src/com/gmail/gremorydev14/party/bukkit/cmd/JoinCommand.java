package com.gmail.gremorydev14.party.bukkit.cmd;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class JoinCommand extends SubCommand {
	
	public JoinCommand() {
		super("join");
	}
	
	@Override
	public void onSend(Player p, String[] args) {
		PlayerData pd = PlayerData.get(p);
		PartyBukkit party = PartyBukkit.get(p);
		if (pd != null) {
			if (party != null) {
				p.sendMessage(Language.messages$party$already_in_party);
				return;
			}
			
			PartyBukkit invited = PartyBukkit.getInvitesMap().get(p);
			if (invited != null && PartyBukkit.values().contains(invited)) {
				if (invited.getSlots() < invited.getMaxSlots()) {
					PartyBukkit.getInvitesMap().remove(p);
					
					p.sendMessage(Language.messages$party$join_player.replace("%owner%", invited.getOwner().getName()));
					invited.addPlayer(p);
					invited.broadcastMessage(Language.messages$party$join_broadcast.replace("%player%", p.getName()));
				} else {
					p.sendMessage(Language.messages$party$party_full);
				}
			} else {
				p.sendMessage(Language.messages$party$invite_null);
			}
		}
	}
}
