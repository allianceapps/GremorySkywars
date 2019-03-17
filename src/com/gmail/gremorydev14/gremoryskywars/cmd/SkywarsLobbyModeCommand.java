package com.gmail.gremorydev14.gremoryskywars.cmd;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.AddCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.DeliveryCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.LeaderBoardCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.LobbyCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.NPCCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.WellCommand;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand;

public class SkywarsLobbyModeCommand extends MainCommand {

	public SkywarsLobbyModeCommand() {
		super(Main.getPlugin(), "sw", "gremoryskywars.cmd.skywars", false, Utils.getCitizens() == null ? new String[] { "§a/sw setlobby §7- Set main lobby of server", "§a/sw well §7- Manage soulwells", "§a/sw add §7- Add coins or souls", "§a/sw boards §7- Add leaderboards" }
				: new String[] { "§a/sw setlobby §7- Set main lobby of server", "§a/sw well §7- Manage soulwells", "§a/sw npc §7- Manage npcs", "§a/sw dman §7- Manage deliverymans", "§a/sw add §7- Add coins or souls", "§a/sw boards §7- Add leaderboards" });
		addCommand(new LobbyCommand());
		addCommand(new WellCommand());
		if (Utils.getCitizens() != null) {
			addCommand(new NPCCommand());
			addCommand(new DeliveryCommand());
		}
		addCommand(new AddCommand());
		addCommand(new LeaderBoardCommand());
	}
}
