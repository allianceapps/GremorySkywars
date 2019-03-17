package com.gmail.gremorydev14.gremoryskywars.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.State;

@SuppressWarnings("all")
public class RollBack {
	
	private static BukkitTask task;
	private static List<Arena> queue = new ArrayList<>();
	private static ArrayList<Arena> rollbacking = new ArrayList<>();
	
	public static void rollBack(Arena arena) {
		if (queue.contains(arena) || rollbacking.contains(arena)) {
			return;
		}
		
		if (rollbacking.size() == 1) {
			queue.add(arena);
			return;
		}
		
		arena.rollBack();
		rollbacking.add(arena);
		if (task == null) {
			task = new BukkitRunnable() {
				
				@Override
				public void run() {
					List<Arena> list = (List<Arena>) rollbacking.clone();
					for (Arena arena : list) {
						if (arena.getState() != State.RESET) {
							rollbacking.remove(arena);
							
							if (rollbacking.isEmpty() && queue.isEmpty()) {
								cancel();
								task = null;
								return;
							}
							
							if (!queue.isEmpty()) {
								rollbacking.add(queue.get(0));
								queue.get(0).rollBack();
								queue.remove(0);
							}
						}
					}
				}
			}.runTaskTimer(Main.getPlugin(), 40, 40);
		}
	}
}
