package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.leaderboards.LeaderBoard;

public class LeaderBoardCommand extends SubCommand {

	public LeaderBoardCommand() {
		super("boards");
	}

	@Override
	public void onSend(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw boards add <id> <Kills/Wins> <1|2|3|4|5> §7- Add a leaderboard in your location");
			p.sendMessage("§a/sw boards remove <id> §7- Remove an leaderboard");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
			return;
		} else {
			String id = args[1];
			String action = args[0];

			if (action.equalsIgnoreCase("add")) {
				if (args.length < 4) {
					p.sendMessage("§a/sw boards add <id> <Kills/Wins> <1|2|3|4|5> §7- Add a leaderboard in your location");
					return;
				}
				String type = args[2];
				int slot = 0;
				try {
					slot = Integer.parseInt(args[3]);
				} catch (Exception e) {
					p.sendMessage("§a/sw boards add <Kills/Wins> <type> <1|2|3|4|5> §7- Add a leaderboard in your location");
					return;
				}
				if (slot < 1 || slot > 5) {
					p.sendMessage("§a/sw boards add <Kills/Wins> <type> <1|2|3|4|5> §7- Add a leaderboard in your location");
					return;
				}
				if (!type.equalsIgnoreCase("Kills") && !type.equalsIgnoreCase("Wins")) {
					p.sendMessage("§a/sw boards add <Kills/Wins> <type> <1|2|3|4|5> §7- Add a leaderboard in your location");
					return;
				}
				if (LeaderBoard.get(id) != null) {
					p.sendMessage("§cThere is already a leaderboard with this id");
					return;
				}
				Location loc = p.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
				loc.setYaw(p.getLocation().getYaw());
				loc.setPitch(p.getLocation().getPitch());
				LeaderBoard ld = new LeaderBoard(id, type, slot, loc);
				LeaderBoard.put(id, ld, type);
				ld.update();
				p.sendMessage("§aYou added an LeaderBoard in your location");
				return;
			} else if (action.equalsIgnoreCase("remove")) {
				LeaderBoard npc = LeaderBoard.get(id);
				if (npc == null) {
					p.sendMessage("§cLeaderBoard \"" + id + "\" was not found");
					return;
				}

				LeaderBoard.remove(id);
				p.sendMessage("§aYou removed the \"" + id + "\" LeaderBoard");
				return;
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw boards add <id> <Kills/Wins> <1|2|3|4|5> §7- Add a leaderboard in your location");
				p.sendMessage("§a/sw boards remove <id> §7- Remove an leaderboard");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
				return;
			}
		}
	}
}
