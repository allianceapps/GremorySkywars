package com.gmail.gremorydev14;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

@SuppressWarnings("all")
public class Language {

	public static String sign$global_1 = "§a[ENTRAR]";
	public static String sign$global_2 = "§l%mode%";
	public static String sign$global_3 = "In Queue %queue%";
	public static String sign$global_4 = "%color%%typeName% Mode";
	public static String sign$selector_1 = "§a[ENTRAR]";
	public static String sign$selector_2 = "§l%mode%";
	public static String sign$selector_3 = "§b§lMAP SELECTOR";
	public static String sign$selector_4 = "%color%%typeName% Mode";

	public static String messages$arena$not_avaible = "§cEssa partida já está em jogo!";
	public static String messages$arena$full = "§cEssa partida já está cheia!";
	public static String messages$arena$already_ingame = "§cVocê já está em partida!";
	public static String messages$arena$join = "%pDName% §aentrou na partida (§b%on%§7/§b%max%§a)!";
	public static String messages$arena$leave = "%pDName% §asaiu da partida (§b%on%§7/§b%max%§a)!";
	public static String messages$arena$title_starting = "§c§l%count%\n§aPrepare-se para lutar!";
	public static String messages$arena$title_skywars = "§a§lSkyWars\n%color%%type% Mode";
	public static String messages$arena$broadcast_starting = "§aO jogo começa em §f%count% §asegundo%S%.";
	public static String messages$arena$broadcast_full = "§6O temporizador foi reduzido para §f10 §6segundos pois a sala lotou!";
	public static String messages$arena$broadcast_cancel = "§cO temporizador foi cancelado pois falta jogadores!";
	public static String messages$arena$kill_action_bar = "§aAinda restam §c%players% §ajogadores vivos!";
	public static String messages$arena$spectator_title = "§c§lVOCE MORREU\n§7Você agora é um espectador!";
	public static List<String> messages$arena$start = Arrays.asList("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "%center%§f§lSkyWars       ", "", "%center%§a§lObtenha recursos e equipamentos na sua ilha",
			"%center%§a§lpara eliminar os outros jogadores.", "%center%§a§lVá para a ilha central para melhores baús,", "%center%§a§lcom itens especiais!", "", "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
	public static String messages$arena$team_notallowed_solo = "§c§lAliar-se não é permitido no Modo Solo!";
	public static String messages$arena$team_notallowed_team = "§c§lAliar-se entre Times não é permitido no Modo Time!";
	public static String messages$arena$cages_opened = "§aCages abertas! §cLUTEM!";
	public static List<String> messages$arena$end = Arrays.asList("§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "%center%§f§lSkyWars       ", "", "%center%§aVencedor - §7%winner%", "",
			"%center%§a§l1st Killer §7- %p1Name% - %p1Kills%", "%center%§6§l2nd Killer §7- %p2Name% - %p2Kills%", "%center%§c§l3rd Killer §7- %p3Name% - %p3Kills%", "");
	public static List<String> messages$arena$rewards = Arrays.asList("%center%§f§lSumario de Prêmios       ", "", "§6Total de Coins Ganhos: %tCoins%", "§3Total de Experiência Ganha: %tExperience%", "", "§7Você conseguiu §b%souls% Souls",
			"§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
	public static String messages$player$title_lose = "§c§lFIM DE JOGO\n§7Você não foi vitorioso desta vez";
	public static String messages$player$title_win = "§c§lFIM DE JOGO\n§7Você foi vitorioso parabéns";
	public static String messages$arena$title_5min = "§cRestam apenas 5 minutos!";
	public static String messages$arena$vote_register = "§aVoto registrado, agradecemos o feedback.";
	public static String messages$arena$refill_title = "§aOs baús foram reabastecidos!";
	public static String messages$player$play_again_null = "§cNenhuma partida encontrada no momento!";
	public static String messages$player$compass_null = "§cNenhum jogador encontrado!";
	public static String messages$player$compass_track = "§aJogador encontrado: %pDName%";
	public static String messages$arena$player_suicide = "%pDName% §amorreu sozinho!";
	public static String messages$arena$player_killed = "%pDName% §afoi morto pelo %kDName%";
	public static String messages$player$kill_reward = "§aVocê ganhou §6%coins% Coins";
	public static String messages$player$arrow_hp = "§aHP de %pDName% §aé §c%health%";
	public static String messages$player$vote_format = "§aClick to vote!";
	public static String messages$player$vote_message = "§bVote for this map:";
	public static String messages$player$want_play_again = "§aWant to play again? ";
	public static String messages$player$want_play_again_died = "§cYou died! §aWant to play again? ";
	public static String messages$player$want_play_again_click = "§b§lClick here!";
	public static String messages$player$want_play_again_hover = "Click to play other SkyWars game";
	public static String messages$chat$format = "§7%pDName%§a: §7%message%";
	public static String messages$chat$format_team = "§7%pDName%§a: §7%message%";
	public static String messages$chat$format_spectator = "§8[SPECTATOR] §7%pDName%§a: §7%message%";
	public static String messages$chat$format_g = "%team% %pDName%§a: §7%message%";
	public static String messages$player$tell_disabled = "§cPlayer tell disabled!";
	public static String messages$player$no_permission = "§cYou not have permission!";
	public static String messages$player$players_disable = "§cPlayers disabled!";
	public static String messages$player$players_enable = "§aPlayers enabled!";
	public static String messages$player$in_development = "§cIn development, coming soon";
	public static String messages$player$players_toggle_delay = "§cWait more %time% to use again";
	public static String messages$player$select_kit = "§6You selected §a%kit% Kit§6!";
	public static String messages$player$select_perk = "§6You selected §a%perk% Perk§6!";
	public static String messages$player$select_cage = "§6You selected §a%cage% Cage§6!";
	public static String messages$player$no_have_enough_coins = "§cYou don't have enough coins.";
	public static String messages$player$unlock_kit = "§6You have unlocked §a%kit% Kit§6!";
	public static String messages$player$unlock_perk = "§6You have unlocked §a%perk% Perk§6!";
	public static String messages$player$kit_soulwell = "§cThis kit can be found in the §bSoul Well§c!";
	public static String messages$player$kit_rank = "§cThis kit is only avaible for §bRanked Player§c!";
	public static String messages$player$kit_shop = "§cThis kit is avaible in the shop!";
	public static String messages$player$perk_soulwell = "§cThis kit can be found in the §bSoul Well§c!";
	public static String messages$player$perk_rank = "§cThis kit is only avaible for §bRanked Player§c!";
	public static String messages$player$perk_shop = "§cThis kit is avaible in the shop!";
	public static String messages$player$already_have_kit = "§cYou already have %kit% Kit!";
	public static String messages$player$already_have_perk = "§cYou already have %perk% Perk!";
	public static String messages$player$unlock_achievement = "§aAchievement §9%name% §aunlocked!";
	public static String messages$player$already_claimed_level = "§cYou've already claimed that reward!";
	public static String messages$player$no_level_to_claim = "§cYou can't claim that reward!";
	public static String messages$player$offline = "§cPlayer %player% offline.";
	public static String messages$player$level_reward_coins = "§eYou has claimed §6%coins% Coins§e!";
	public static String messages$player$level_reward_booster = "§eYou has claimed §6%modifier% §7Coin booster - §6%time% %type%§e!";
	public static String messages$player$already_using_booster = "§cYou already have an booster!";
	public static String messages$player$already_use_booster = "§eYou have enabled §6%modifier% §7Coin booster - §6%time% %type%§e!";
	public static String messages$player$level_up = "§aCongratulions! reached the §3Level Skywars #%level%";
	public static String messages$party$invite_null = "§d[PARTY] §cYou have not received any invitation";
	public static String messages$party$already_in_party = "§d[PARTY] §cYou're already in a party";
	public static String messages$party$party_full = "§d[PARTY] §cThe party is full.";
	public static String messages$party$without_party = "§d[PARTY] §cYou're not in a party.";
	public static String messages$party$join_player = "§d[PARTY] §aYou joined %owner%'s party.";
	public static String messages$party$join_broadcast = "§d[PARTY] §a%player% joined the party.";
	public static String messages$party$leave_player = "§d[PARTY] §aYou left %player%'s party.";
	public static String messages$party$leave_broadcast = "§d[PARTY] §a%player% left the party.";
	public static String messages$party$info = " \n    §d[PARTY]\n \n§eOwner: §a%owner%\n§eMembers: %members%\n§eSlots: %slot%/%maxSlots%\n ";
	public static String messages$party$pending_invite = "§d[PARTY] §cThis player has a pending invite.";
	public static String messages$party$invite = "§d[PARTY] §aInvitation sent successfully.";
	public static String messages$party$invite_msg = "§d[PARTY] §a%player% invited him to his party ";
	public static String messages$party$invite_click = "§c§lClick Here";
	public static String messages$party$invite_msg2 = " §ato §aaccept.";
	public static String messages$party$invite_expires = "§d[PARTY] §a%player%'s party invitation expired.";
	public static String messages$party$wrong_party = "§d[PARTY] §aThe player is not in your party.";
	public static String messages$party$kick_broadcast = "§d[PARTY] §a%player% got kicked out of the party.";
	public static String messages$party$chat_format = "§d[PARTY] §7%player%§a: §7%message%";
	public static String messages$party$members_in_arena = "§d[PARTY] §cNot all party players are available.";
	public static String messages$party$leader_cmds = "§d[PARTY] §cOnly the party leader can do this.";
	public static String messages$party$leader_match = "§d[PARTY] §cOnly the party leader can find the match.";
	public static String messages$party$leader_change = "§d[PARTY] §a%player% is the new party leader.";
	public static String messages$player$broadcast_login = "%player% §6joined the lobby!";
	public static String messages$menu$only_can_unlocked_rank = "§bUnlocked for Ranked Player's!";
	public static String messages$menu$only_can_unlocked_soulwell = "§bAlso be found in Soul Well!";
	public static String messages$menu$only_can_unlocked_permission = "§cNeeds permission!";
	public static String messages$soulwell$already_have_all_items = "§cYou already have all items available on soulwell";
	public static String messages$soulwell$won_kit = "§eYou won the §6%kit% Kit§e!";
	public static String messages$soulwell$won_perk = "§eYou won the §6%perk% Perk§e!";
	public static String messages$soulwell$won_cage = "§eYou won the §6%cage% Cage§e!";
	public static String messages$soulwell$won_bag = "§eYou won a %bag% §6(%coins%)§e!";
	public static String messages$soulwell$title = "§bSoul Well";
	public static String messages$soulwell$subtitle = "§e§lRIGHT CLICK";
	public static String messages$npc$title = "§e§lCLICK TO PLAY!";
	public static String messages$npc$subtitle = "§b%mode% %type%";
	public static String messages$npc$players_format = "§a§l%players% Players";
	public static String messages$soulwell$no_have_enough_souls = "§cYou don't have enough coins.";
	public static String messages$soulwell$already_in_use = "§cThis soulwell is already in use.";
	public static List<String> messages$party$help = Arrays.asList("§a/p join §7- Join an party", "§a/p leave §7- Leave of the party", "§a/p info §7- Party info", "§a/p invite <player> §7- Invite an player to you party",
			"§a/p kick <player> §7- Kick an player of your party", "§a/p chat <message> §7- Broadcast message to party members");
	public static String messages$votes$removed = "§cYour vote has been removed.";
	public static String messages$votes$added = "§aYour vote has been added.";
	public static String messages$votes$vote_double = "§7%player% §avotes for §cDouble Health %size%§7/§c%max%§a.";
	public static String messages$votes$vote_day = "§7%player% §avotes for §cDay time§a.";
	public static String messages$votes$vote_afternoon = "§7%player% §avotes for §cAfternoon Time§a.";
	public static String messages$votes$vote_night = "§7%player% §avotes for §cNight Time§a.";
	public static String messages$votes$vote_midnight = "§7%player% §avotes for §cMidnight Time§a.";
	public static String messages$player$insane_permission = "§cYou don't have rank to use the insane options.";

	public static void setup() {
		SettingsManager config = Utils.getLanguage();
		try {
			for (Field f : Language.class.getFields()) {
				if (Modifier.isPublic(f.getModifiers())) {
					String name = f.getName().replace("$", ".").replace("_", "-");
					if (!config.contains(name)) {
						Object value = f.get(f.getModifiers());
						if (value instanceof String)
							config.set(name, ((String) value).replace("§", "&").replace("\n", "\\n"));
						else if (value instanceof List) {
							List<String> list = (List<String>) value;
							List<String> newList = new ArrayList<>();
							for (String str : list)
								newList.add(str.replace("§", "&").replace("\n", "\\n"));
							config.set(name, newList);
						} else
							config.set(name, value);
					} else {
						if (config.get(name) instanceof String)
							f.set(Language.class, config.getString(name).replace("&", "§").replace("\\n", "\n"));
						else if (config.get(name) instanceof List) {
							List<String> list = config.getStringList(name);
							List<String> newList = new ArrayList<>();
							for (String str : list)
								newList.add(str.replace("&", "§").replace("\\n", "\n"));
							f.set(Language.class, newList);
						} else
							f.set(Language.class, config.get(name));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.warn("Failed to load language!");
		}
	}
}
