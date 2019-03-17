package com.gmail.gremorydev14.gremoryskywars.arena.util;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.gmail.gremorydev14.Language;
import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.api.CitizensAPI;
import com.gmail.gremorydev14.gremoryskywars.arena.Arena;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Mode;
import com.gmail.gremorydev14.gremoryskywars.util.Enums.Type;

import net.citizensnpcs.api.npc.NPC;

@SuppressWarnings("all")
public class ArenaNPC {

	private Mode mode;
	private Type type;
	private Location location;
	private NPC npc;
	private String id;
	private Hologram hologram;

	public ArenaNPC(String id, Mode mode, Type type, Location location) {
		this.mode = mode;
		this.type = type;
		this.id = id;
		this.location = location;
		npc = net.citizensnpcs.api.CitizensAPI.getNamedNPCRegistry("SkyWars").createNPC(EntityType.PLAYER, "");
		npc.setProtected(true);
		npc.spawn(location);
		npc.getEntity().setMetadata("ARENA_NPC", new FixedMetadataValue(Main.getPlugin(), this));
		new BukkitRunnable() {

			@Override
			public void run() {
				hologram = HolographicDisplaysAPI.createHologram(Main.getPlugin(), location.clone().add(0, 3.0, 0), Language.messages$npc$title, "",
						Language.messages$npc$subtitle.replace("%mode%", mode.getName()).replace("%type%", type.getName()), Language.messages$npc$players_format.replace("%players%", "0"));
			}
		}.runTaskLater(HolographicDisplays.getInstance(), 40);
		CitizensAPI.getNpcs().put(id, this);
	}

	private String getSkinName() {
		if (mode == Mode.SOLO) {
			return type == Type.NORMAL ? Options.getNSoloNPCSkin() : Options.getISoloNPCSkin();
		} else if (mode == Mode.TEAM) {
			return type == Type.NORMAL ? Options.getNTeamNPCSkin() : Options.getITeamNPCSkin();
		}
		return Options.getMegaNPCSkin();
	}

	public void destroy() {
		hologram.delete();
		npc.destroy();
	}

	public void update() {
		if (hologram != null) {
			hologram.removeLine(3);
			hologram.addLine(Language.messages$npc$players_format.replace("%players%", "" + Arena.getCountPlaying(mode, type)));
		}
	}

	public String getId() {
		return id;
	}

	public Mode getMode() {
		return mode;
	}

	public Type getType() {
		return type;
	}

	public Location getNPCLocation() {
		return location;
	}
}
