package com.gmail.gremorydev14.gremoryskywars.cmd;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.AddCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.ChestCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.CreateCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.DeliveryCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.LeaderBoardCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.LoadCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.LobbyCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.NPCCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.SchematicCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.SpawnCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.StartCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.TeleportCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.UnloadCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.WandCommand;
import com.gmail.gremorydev14.gremoryskywars.cmd.subs.WellCommand;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand;

public class SkywarsCommand extends MainCommand {

	public SkywarsCommand() {
		super(Main.getPlugin(), "sw", "gremoryskywars.cmd.skywars", false,
				Utils.getCitizens() != null
						? new String[] { "§a/sw start §7- Start arena", "§a/sw create <mode> <type> §7- Create arena", "§a/sw spawn §7- Enter spawn mode", "§a/sw add §7- Add coins or souls", "§a/sw boards §7- Add leaderboards", "§a/sw well §7- Manage soulwells",
								"§a/sw npc §7- Manage npcs", "§a/sw dman §7- Manage deliverymans", "§a/sw schematic §7- Lobby schematic (TEAM and MEGA)", "§a/sw tp <world> §7- Teleport to world", "§a/sw load <name> §7- Load an world", "§a/sw unload <name> §7- Unload an world", "§a/sw chest <name> §7- Chest editor",
								"§a/sw wand <name> §7- Enter chest mode", "§a/sw setlobby §7- Set main lobby of server" }
						: new String[] { "§a/sw start §7- Start arena", "§a/sw create <mode> <type> §7- Create arena", "§a/sw spawn §7- Enter spawn mode", "§a/sw add §7- Add coins or souls", "§a/sw boards §7- Add leaderboards", "§a/sw well §7- Manage soulwells", "§a/sw schematic §7- Lobby schematic (TEAM and MEGA)",
								"§a/sw tp <world> §7- Teleport to world", "§a/sw load <name> §7- Load an world", "§a/sw unload <name> §7- Unload an world", "§a/sw chest <name> §7- Chest editor", "§a/sw wand <name> §7- Enter chest mode",
								"§a/sw setlobby §7- Set main lobby of server" });
		addCommand(new StartCommand());
		addCommand(new CreateCommand());
		addCommand(new SpawnCommand());
		addCommand(new WellCommand());
		if (Utils.getCitizens() != null) {
			addCommand(new NPCCommand());
			addCommand(new DeliveryCommand());
		}
		addCommand(new SchematicCommand());
		addCommand(new AddCommand());
		addCommand(new LeaderBoardCommand());
		addCommand(new TeleportCommand());
		addCommand(new LoadCommand());
		addCommand(new UnloadCommand());
		addCommand(new ChestCommand());
		addCommand(new WandCommand());
		addCommand(new LobbyCommand());
	}
}
