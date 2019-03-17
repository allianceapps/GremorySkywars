package com.gmail.gremorydev14.party.bukkit.cmd;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class ChatCommand extends SubCommand {

	public ChatCommand() {
		super("chat");
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
				p.sendMessage("§cUse: /p chat <message>");
				return;
			}

			String message = "";
			for (int i = 0; i < args.length; i++) {
				message += args[i] + (i + 1 == args.length ? "" : " ");
			}
			party.broadcastMessage(Language.messages$party$chat_format.replace("%player%", p.getDisplayName()).replace("%message%", message));
		}
	}
}
