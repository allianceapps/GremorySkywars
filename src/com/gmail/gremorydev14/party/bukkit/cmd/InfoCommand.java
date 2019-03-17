package com.gmail.gremorydev14.party.bukkit.cmd;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.party.bukkit.PartyBukkit;

public class InfoCommand extends SubCommand {
	
	public InfoCommand() {
		super("info");
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
			
			p.sendMessage(Language.messages$party$info.replace("%owner%", party.getOwner().getName()).replace("%members%", StringUtils.join(party.getList() , ", ")).replace("%slot%", "" + party.getSlots()).replace("%maxSlots%", "" + party.getMaxSlots()));
		}
	}
}
