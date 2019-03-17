package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;

public class StartCommand extends SubCommand {

	public StartCommand() {
		super("start");
	}

	@Override
	public void onSend(Player p, String[] args) {
		PlayerData pd = PlayerData.get(p);
		if (pd != null && pd.getArena() != null) {
			if (pd.getArena().getState() == State.WAITING || pd.getArena().getState() == State.STARTING) {
				pd.getArena().start();
				p.sendMessage("§aYou started the arena");
			} else {
				p.sendMessage("§cThe arena is already in-game.");
			}
		} else {
			p.sendMessage("§cYou are not in an arena");
		}
	}
}
