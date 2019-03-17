package com.gmail.gremorydev14.gremoryskywars.editor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.player.SmartScoreboard;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

import lombok.Getter;

public class ScoreboardConstructor {

	@Getter
	private static boolean scoreboard_lobby_enabled;
	@Getter
	private static boolean scoreboard_waiting_enabled;
	@Getter
	private static boolean scoreboard_starting_enabled;
	@Getter
	private static boolean scoreboard_game_enabled;
	@Getter
	private static String scoreboard_lobby_title;
	@Getter
	private static String scoreboard_game_title;
	@Getter
	private static List<String> scoreboard_waiting_lines;
	@Getter
	private static List<String> scoreboard_starting_lines;
	@Getter
	private static List<String> scoreboard_gameS_lines;
	@Getter
	private static List<String> scoreboard_gameT_lines;
	@Getter
	private static List<String> scoreboard_lobby_lines;

	public static void setup() {
		SettingsManager sm = Utils.getScoreboards();

		scoreboard_lobby_enabled = sm.getBoolean("scoreboard-lobby.enabled");
		scoreboard_waiting_enabled = sm.getBoolean("scoreboard-waiting.enabled");
		scoreboard_starting_enabled = sm.getBoolean("scoreboard-starting.enabled");
		scoreboard_game_enabled = sm.getBoolean("scoreboard-game.enabled");

		scoreboard_lobby_title = sm.getString("scoreboard-lobby.title");
		scoreboard_game_title = sm.getString("game-title");

		scoreboard_lobby_lines = sm.getStringList("scoreboard-lobby.lines");
		scoreboard_waiting_lines = sm.getStringList("scoreboard-waiting.lines");
		scoreboard_starting_lines = sm.getStringList("scoreboard-starting.lines");
		scoreboard_gameS_lines = sm.getStringList("scoreboard-game.lines_solo");
		scoreboard_gameT_lines = sm.getStringList("scoreboard-game.lines_team");
	}

	public static SmartScoreboard createFromState(Player p, Arena a) {
		if (a.getState() == State.WAITING || a.getState() == State.STARTING) {
			SmartScoreboard sb = new SmartScoreboard(p, ScoreboardConstructor.getScoreboard_game_title()) {

				@Override
				public String placeHolders(String str) {
					return str.replace("%players%", "" + a.getPlayers().size()).replace("%maxPlayers%", "" + a.getSpawns().size() * a.getMode().getSize()).replace("%serverName%", a.getSv()).replace("%time%", a.getCountdown() + "s");
				}
			};
			for (String line : (a.getState() == State.WAITING ? ScoreboardConstructor.getScoreboard_waiting_lines() : ScoreboardConstructor.getScoreboard_starting_lines())) {
				sb.add(line);
			}
			sb.set(p);
			return sb;
		}

		SmartScoreboard sb = new SmartScoreboard(p, ScoreboardConstructor.getScoreboard_game_title()) {

			@Override
			public String placeHolders(String str) {
				return str.replace("%event%", a.getTheEvent() + " " + (a.formatTime(a.getCountdown() - a.getNextEvent()))).replace("%mode%", a.getMode().getName()).replace("%date%", new SimpleDateFormat("dd/MM/yy").format(new Date()))
						.replace("%playersLeft%", "" + a.playersAlive()).replace("%teamsLeft%", "" + a.getAliveTeams()).replace("%kills%", "" + a.getKills(p)).replace("%mapName%", a.getName())
						.replace("%type%", a.getType().getColor() + a.getType().getName());
			}
		};
		sb.createHealth();
		for (String line : (a.getMode() == Mode.SOLO ? ScoreboardConstructor.getScoreboard_gameS_lines() : ScoreboardConstructor.getScoreboard_gameT_lines())) {
			sb.add(line);
		}
		sb.set(p);
		return sb;
	}
}
