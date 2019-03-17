package com.gmail.gremorydev14.party.bukkit.cmd;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class LeaveCommand extends SubCommand {
	
	public LeaveCommand() {
		super("leave");
	}
	
	@Override
	public void onSend(Player p, String[] args) {
		PlayerData pd = PlayerData.get(p);
		PartyBukkit party = PartyBukkit.get(p);
		if (pd != null) {
			if (party == null) {
				p.sendMessage(Language.messages$party$without_party);
				return;
			}
			
			p.sendMessage(Language.messages$party$leave_player.replace("%player%", party.getOwner().getName()));
			party.removePlayer(p);
			if (party.getMembers().isEmpty()) {
				PartyBukkit.remove(party);
			} else {
				party.broadcastMessage(Language.messages$party$leave_broadcast.replace("%player%", p.getName()));
			}
		}
	}
}
