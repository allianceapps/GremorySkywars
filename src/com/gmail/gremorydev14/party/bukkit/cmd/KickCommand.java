package com.gmail.gremorydev14.party.bukkit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class KickCommand extends SubCommand {

	public KickCommand() {
		super("kick");
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

			if (args.length == 0) {
				p.sendMessage("§cUse: /p kick <player>");
				return;
			}

			Player p2 = Bukkit.getPlayerExact(args[0]);
			if (p2 != null && p2.isOnline() && PlayerData.get(p2) != null) {
				if (p != p2) {
					if (party.getOwner().equals(p)) {
						PartyBukkit party2 = PartyBukkit.get(p2);
						if (party2 == null || party.equals(party2)) {
							party.broadcastMessage(Language.messages$party$kick_broadcast.replace("%player%", p2.getName()));
							party.removePlayer(p2);
						} else {
							p.sendMessage(Language.messages$party$wrong_party);
						}
					} else {
						p.sendMessage(Language.messages$party$leader_cmds);
					}
				}
			} else {
				p.sendMessage(Language.messages$player$offline.replace("%player%", args[0]));
			}
		}
	}
}
