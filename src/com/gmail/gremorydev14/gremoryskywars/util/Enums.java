package com.gmail.gremorydev14.gremoryskywars.util;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Enums {

	@Getter
	@AllArgsConstructor
	public static enum Rarity {
		COMMON("Common", "§a"), RARE("Rare", "§9"), LEGENDARY("Legendary", "§6");

		private String name;
		private String color;
	}

	@Getter
	@AllArgsConstructor
	public static enum Found {
		SOUL_WELL(), RANK(), PERMISSION(), ALL();

		public String getDesc() {
			if (this == ALL)
				return "";
			if (this == RANK)
				return Language.messages$menu$only_can_unlocked_rank;
			if (this == PERMISSION)
				return Language.messages$menu$only_can_unlocked_permission;
			return Language.messages$menu$only_can_unlocked_soulwell;
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum Type {
		INSANE("Insane"), NORMAL("Normal"), MEGA("Mega");
		private String name;

		public String getColor() {
			if (this == NORMAL)
				return "§9";
			if (this == MEGA)
				return "§5";
			return "§c";
		}

		public static Type get(String name) {
			for (Type type : values())
				if (type.name().toLowerCase().equals(name.toLowerCase()))
					return type;
			return null;
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum Mode {
		SOLO("Solo", 1), TEAM("Team", 2), MEGA("Mega", 5);
		private String name;
		private int size;

		public static Mode get(String name) {
			for (Mode mode : values())
				if (mode.name().toLowerCase().equals(name.toLowerCase()))
					return mode;
			return null;
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum State {

		WAITING("Waiting", true), STARTING("Starting", true), GAME("InGame", false), RESET("Reseting", false), DISABLED("Disabled", false);

		private String name;
		private boolean avaible;

		public String getColor() {
			if (this == WAITING)
				return "§9";
			if (this == STARTING)
				return "§a";
			if (this == GAME)
				return "§e";
			return "§6";
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum Stats {

		KILLS_SOLO(0), KILLS_TEAM(1), KILLS_MEGA(2), WINS_SOLO(3), WINS_TEAM(4), WINS_MEGA(5), SOULS(6);

		private int id;

		public int getInt(String[] split) {
			return Integer.parseInt(split[id]);
		}

		public static String parseFrom(PlayerData pd) {
			return pd.getKillsSolo() + " : " + pd.getKillsTeam() + " : " + pd.getKillsMega() + " : " + pd.getWinsSolo() + " : " + pd.getWinsTeam() + " : " + pd.getWinsMega() + " : " + pd.getSouls();
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum Options {

		KIT(0), CAGE(1), PERK(2), PLAYERS(3), TELL(4);

		private int id;

		public String getString(String[] split) {
			return split[id];
		}

		public boolean getBoolean(String[] split) {
			return Boolean.valueOf(split[id]);
		}

		public static String parseFrom(PlayerData pd) {
			return ((pd.getSKit() != null ? pd.getSKit().getName() : "Default") + ";" + (pd.getTKit() != null ? pd.getTKit().getName() : "Default") + ";" + (pd.getMKit() != null ? pd.getMKit().getName() : "Default")) + " : " + (pd.getCage() != null ? pd.getCage().getName() : "normal") + " : " + (pd.getSPerk() != null ? pd.getSPerk().getName() : "normal") + ";"
					+ (pd.getTPerk() != null ? pd.getTPerk().getName() : "normal") + ";" + (pd.getMPerk() != null ? pd.getMPerk().getName() : "normal") + " : " + pd.isView() + " : " + pd.isTell();
		}
	}

	@Getter
	@AllArgsConstructor
	public static enum FontInfo {

		A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5), K('K',
				5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5), P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4), U('U',
						5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5), Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6',
								5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6), NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&',
										5), ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}',
												4), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4), RIGHT_ARROW('>',
														4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5), LINE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

		private char character;
		private int length;

		public int getBoldLength() {
			if (this == SPACE)
				return getLength();
			return length + 1;
		}

		public static FontInfo getFontInfo(char c) {
			for (FontInfo fi : values())
				if (fi.getCharacter() == c)
					return fi;
			return DEFAULT;
		}
	}
}
