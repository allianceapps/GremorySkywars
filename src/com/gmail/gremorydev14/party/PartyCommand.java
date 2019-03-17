package com.gmail.gremorydev14.party;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.ChatCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.InfoCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.InviteCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.JoinCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.KickCommand;
import com.gmail.gremorydev14.party.bukkit.cmd.LeaveCommand;

public class PartyCommand extends MainCommand {

	public PartyCommand() {
		super(Main.getPlugin(), "party", "none", false, Language.messages$party$help.toArray(new String[Language.messages$party$help.size()]));
		if (Options.getMode() == ServerMode.MULTI_ARENA) {
			addCommand(new JoinCommand());
			addCommand(new LeaveCommand());
			addCommand(new InfoCommand());
			addCommand(new InviteCommand());
			addCommand(new KickCommand());
			addCommand(new ChatCommand());
		}
	}
}
