package com.gmail.gremorydev14.gremoryskywars.cmd.subs;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.gremoryskywars.util.command.MainCommand.SubCommand;
import com.gmail.gremorydev14.profile.Booster;

public class AddCommand extends SubCommand {

	public AddCommand() {
		super("add");
	}

	@Override
	public void onSend(Player p, String[] args){
		if (args.length < 3) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw add coins <player> <amount> §7- Add coins");
			p.sendMessage("§a/sw add souls <player> <amount> §7- Add souls");
			p.sendMessage("§a/sw add booster <player> <id> §7- Add booster");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
		} else {
			String id = args[0];
			Player target = Bukkit.getPlayerExact(args[1]);
			int amount = 0;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				p.sendMessage("§cYou need to use numbers.");
				return;
			}

			if (id.equalsIgnoreCase("coins")) {
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).addCoins(amount);
					p.sendMessage("§aCoins added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else if (id.equalsIgnoreCase("souls")) {
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).addSouls(amount);
					p.sendMessage("§aSouls added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else if (id.equalsIgnoreCase("booster")) {
				Booster booster = Booster.get(amount);
				if (booster == null) {
					p.sendMessage("§cBooster with id \"" + amount + "\" not found");
					return;
				}
				
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).getProfile().getBoosters().add(booster);
					p.sendMessage("§aSouls added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw add coins <player> <amount> §7- Add coins");
				p.sendMessage("§a/sw add souls <player> <amount> §7- Add souls");
				p.sendMessage("§a/sw add booster <player> <id> §7- Add booster");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
			}
		}
	}
	
	public void onSend(CommandSender p, String[] args) {
		if (args.length < 3) {
			p.sendMessage("§7§m----------------------------------------");
			p.sendMessage(new String[] { "", "§c§lHELP:" });
			p.sendMessage("§a/sw add coins <player> <amount> §7- Add coins");
			p.sendMessage("§a/sw add souls <player> <amount> §7- Add souls");
			p.sendMessage("§a/sw add booster <player> <id> §7- Add booster");
			p.sendMessage("");
			p.sendMessage("§7§m----------------------------------------");
		} else {
			String id = args[0];
			Player target = Bukkit.getPlayerExact(args[1]);
			int amount = 0;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				p.sendMessage("§cYou need to use numbers.");
				return;
			}

			if (id.equalsIgnoreCase("coins")) {
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).addCoins(amount);
					p.sendMessage("§aCoins added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else if (id.equalsIgnoreCase("souls")) {
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).addSouls(amount);
					p.sendMessage("§aSouls added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else if (id.equalsIgnoreCase("booster")) {
				Booster booster = Booster.get(amount);
				if (booster == null) {
					p.sendMessage("§cBooster with id \"" + amount + "\" not found");
					return;
				}
				
				if (target != null && PlayerData.get(target) != null) {
					PlayerData.get(target).getProfile().getBoosters().add(booster);
					p.sendMessage("§aSouls added sucessfully.");
				} else {
					p.sendMessage(Language.messages$player$offline);
				}
			} else {
				p.sendMessage("§7§m----------------------------------------");
				p.sendMessage(new String[] { "", "§c§lHELP:" });
				p.sendMessage("§a/sw add coins <player> <amount> §7- Add coins");
				p.sendMessage("§a/sw add souls <player> <amount> §7- Add souls");
				p.sendMessage("§a/sw add booster <player> <id> §7- Add booster");
				p.sendMessage("");
				p.sendMessage("§7§m----------------------------------------");
			}
		}
	}
}
