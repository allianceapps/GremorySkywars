package com.gmail.gremorydev14.gremoryskywars.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.gremorydev14.delivery.Delivery;
import com.gmail.gremorydev14.delivery.Delivery.DeliveryMessages;
import com.gmail.gremorydev14.delivery.DeliveryMan;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.player.PlayerData;
import com.gmail.gremorydev14.mystery.SoulWell;

public class InventoryUtils {

	public static void wellConfirm(Player p, SoulWell sw) {
		Inventory inv = Bukkit.createInventory(null, 27, "Soul Well");
		inv.setItem(11, ItemUtils.createItem("STAINED_CLAY:5 : 1 : name=&aOpen"));
		inv.setItem(15, ItemUtils.createItem("STAINED_CLAY:14 : 1 : name=&cCancel"));
		p.openInventory(inv);
		p.setMetadata("SOUL_WELL", new FixedMetadataValue(Main.getPlugin(), sw));
	}

	public static void deliveryMan(Player p, DeliveryMan dman) {
		PlayerData pd = PlayerData.get(p);
		if (pd == null) {
			return;
		}
		Map<Integer, Delivery> map = new HashMap<>();
		Inventory inv = Bukkit.createInventory(null, 54, "The Delivery Man");
		for (Entry<Integer, Delivery> entry : Delivery.getDeliveries().entrySet()) {
			if (TimeUnit.MILLISECONDS.toSeconds(pd
					.getDelivery()
					.getCountdowns()
					.get(entry.getKey()) - System.currentTimeMillis()) > 0) {
				
				String iconInit = entry.getValue().getItemString(false, entry.getValue().has(p));
				String icon = iconInit.split("lore=")[0] + "lore=" + DeliveryMessages.claim_lore.replace("%next%", DeliveryMessages.next_delivery.replace("%time%", Utils.getTimeUntil(pd.getDelivery().getCountdowns().get(entry.getKey()))));
				inv.setItem(entry.getValue().getSlot(), ItemUtils.createItem(icon));
			} else {
				inv.setItem(entry.getValue().getSlot(), entry.getValue().getItem(true, entry.getValue().has(p)));
			}
			map.put(entry.getValue().getSlot(), entry.getValue());
		}
		p.openInventory(inv);
		if (p.hasMetadata("DELIVERY_MAP")) {
			p.removeMetadata("DELIVERY_MAP", Main.getPlugin());
		}
		p.setMetadata("DELIVERY_MAP", new FixedMetadataValue(Main.getPlugin(), map));
	}
}
