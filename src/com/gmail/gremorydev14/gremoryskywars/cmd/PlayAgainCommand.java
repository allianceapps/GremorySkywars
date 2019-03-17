package com.gmail.gremorydev14.gremoryskywars.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.Utils.ServerMode;

public class PlayAgainCommand implements CommandExecutor {

	public PlayAgainCommand() {
		if (Options.getMode() == ServerMode.LOBBY)
			return;
		Main.getPlugin().getCommand("swplayagain").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		PlayerData pd = PlayerData.get(p);

		if (pd != null && pd.getArena() != null) {
			if (Options.getMode() == ServerMode.MULTI_ARENA) {
				Arena a = Arena.getFrom(pd.getArena().getMode(), pd.getArena().getType());
				if (a != null) {
					pd.getArena().remove(p, true, true);
					a.add(p);
				} else {
					p.sendMessage(Language.messages$player$play_again_null);
				}
			} else {
				String server = Arena.getBungee(pd.getArena().getMode(), pd.getArena().getType());
				if (server != null)
					Utils.sendToServer(p, server);
				else
					pd.getPlayer().sendMessage(Language.messages$player$play_again_null);
			}
		}
		return false;
	}
}
