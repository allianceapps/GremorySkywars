package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class LobbyCommand extends SubCommand {

	public LobbyCommand() {
		super("setlobby");
	}

	@Override
	public void onSend(Player p, String[] args) {
		Utils.setLobby(p.getLocation());
		Utils.getLocations().set("lobby", Utils.serializeLocation(p.getLocation()));
		p.sendMessage("§aLobby setted sucessfully!");
	}
}
