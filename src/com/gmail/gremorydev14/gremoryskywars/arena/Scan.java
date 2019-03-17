package com.gmail.gremorydev14.gremoryskywars.arena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.Logger;
import com.gmail.gremorydev14.gremoryskywars.util.Utils;
import com.gmail.gremorydev14.gremoryskywars.util.file.SettingsManager;

@SuppressWarnings("all")
public class Scan {
	
	public static void generateData(Arena arena, SettingsManager config) {
		long init = System.currentTimeMillis();
		
		new BukkitRunnable() {
			int count = 0;
			List<String> blocks = new ArrayList<>();
			Iterator<Block> iterator = arena.getCuboId().iterator();
			
			@Override
			public void run() {
				while (iterator.hasNext() && count < 50000) {
					Block block = iterator.next();
					if (block.getType() != Material.AIR) {
						blocks.add(Utils.serializeLocation(block.getLocation()) + " : " + block.getTypeId() + " : " + block.getData());
					}
					count++;
				}
				
				count = 0;
				if (!iterator.hasNext()) {
					cancel();
					config.set(arena.getCuboId().toString(), blocks.toString());
					Logger.info("Generated arena blocks from \"" + arena.getWorld().getName() + "\" world after	" + (System.currentTimeMillis() - init) + "ms");	
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}
}
