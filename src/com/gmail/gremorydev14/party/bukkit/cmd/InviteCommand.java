package com.gmail.gremorydev14.party.bukkit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage.ChatExtra;
import com.gmail.gremorydev14.gremoryskywars.util.Reflection.JSONMessage.ClickEventType;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class InviteCommand extends SubCommand {

	public InviteCommand() {
		super("invite");
	}

	@Override
	public void onSend(Player p, String[] args) {
		PlayerData pd = PlayerData.get(p);
		PartyBukkit party = PartyBukkit.get(p);
		if (pd != null) {
			if (args.length == 0) {
				p.sendMessage("§cUse: /p invite <player>");
				return;
			}

			Player p2 = Bukkit.getPlayerExact(args[0]);
			if (p2 != null && p2.isOnline() && PlayerData.get(p2) != null) {
				if (p2 != p) {
					if (party == null) {
						PartyBukkit.add(p);
						party = PartyBukkit.get(p);
					}
					
					if (party.getOwner().equals(p)) {
						if (PartyBukkit.getInvitesMap().get(p2) == null) {
							party.invitePlayer(p2);
							p.sendMessage(Language.messages$party$invite);
							JSONMessage json = new JSONMessage(Language.messages$party$invite_msg.replace("%player%", p.getName()));
							ChatExtra extra = new ChatExtra(Language.messages$party$invite_click);
							extra.addClickEvent(ClickEventType.RUN_COMMAND, "/party join");
							json.addExtra(extra);
							json.addExtra(new ChatExtra(Language.messages$party$invite_msg2));
							Reflection.sendChatPacket(p2, json.toString());
						} else {
							p.sendMessage(Language.messages$party$pending_invite);
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
