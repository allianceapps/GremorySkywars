package com.gmail.gremorydev14.gremoryskywars.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("deprecation")
public class ItemUtils {

	public static ItemStack createItem(String serialized) {
		serialized = serialized.replace("\\n", "\n");
		String[] split = serialized.split(" : ");
		String mat = split[0].split(":")[0];

		ItemStack item = new ItemStack(StringUtils.isNumeric(mat) ? Material.getMaterial(Integer.parseInt(mat)) : Material.matchMaterial(mat.toUpperCase()));
		if (split[0].split(":").length > 1)
			item.setDurability((short) Integer.parseInt(split[0].split(":")[1]));
		ItemMeta meta = item.getItemMeta();
		if (split.length > 1)
			item.setAmount(Integer.parseInt(split[1]) > 64 ? 64 : Integer.parseInt(split[1]));
		List<String> lore = new ArrayList<>();
		for (int i = 2; i < split.length; i++) {
			String opt = split[i];
			if (opt.startsWith("name="))
				meta.setDisplayName(opt.split("=")[1].replace("&", "§"));
			if (opt.startsWith("lore="))
				for (String lored : opt.split("=")[1].split("\\n"))
					lore.add(lored.replace("&", "§"));
			if (opt.startsWith("enchant="))
				for (String enchanted : opt.split("=")[1].split("\\n"))
					meta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
		}
		if (!lore.isEmpty())
			meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createSkull(String serialized) {
		serialized = serialized.replace("\\n", "\n");
		String[] split = serialized.split(" : ");
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) Integer.parseInt(split[0]));
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>();
		for (int i = 1; i < split.length; i++) {
			String parsed = split[i];
			if (parsed.startsWith("owner="))
				meta.setOwner(parsed.split("=")[1]);
			if (parsed.startsWith("name="))
				meta.setDisplayName(parsed.split("=")[1].replace("&", "§"));
			if (parsed.startsWith("lore="))
				for (String lored : parsed.split("=")[1].split("\\n"))
					lore.add(lored.replace("&", "§"));
			if (parsed.startsWith("enchant="))
				for (String enchanted : parsed.split("=")[1].split("\\n"))
					meta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
		}
		if (!lore.isEmpty())
			meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createFirework(String name, int amount, Color c, String[] lore) {
		ItemStack item = new ItemStack(Material.FIREWORK_CHARGE, amount);
		FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
		meta.setDisplayName(name.replace("&", "§"));
		ArrayList<String> desc = new ArrayList<>();
		for (String s : lore)
			desc.add(s.replace("&", "§"));
		meta.setLore(desc);
		FireworkEffect fe = FireworkEffect.builder().withColor(c).build();
		meta.setEffect(fe);
		item.setItemMeta(meta);
		return item;
	}
}
