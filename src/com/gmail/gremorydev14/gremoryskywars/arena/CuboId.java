package com.gmail.gremorydev14.gremoryskywars.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import lombok.Getter;

public class CuboId implements Iterable<Block> {

	@Getter
	private String world;
	@Getter
	private int xmax, xmin, ymax, ymin, zmax, zmin;

	public CuboId(Location l1, Location l2) {
		this.world = l1.getWorld().getName();
		this.xmax = Math.max(l1.getBlockX(), l2.getBlockX());
		this.xmin = Math.min(l1.getBlockX(), l2.getBlockX());
		this.ymax = Math.max(l1.getBlockY(), l2.getBlockY());
		this.ymin = Math.min(l1.getBlockY(), l2.getBlockY());
		this.zmax = Math.max(l1.getBlockZ(), l2.getBlockZ());
		this.zmin = Math.min(l1.getBlockZ(), l2.getBlockZ());
	}

	public CuboId(String serializedCuboId) {
		String[] split = serializedCuboId.split(" : ");
		this.world = split[0];
		this.xmax = Math.max(i(split[1]), i(split[2]));
		this.xmin = Math.min(i(split[1]), i(split[2]));
		this.ymax = Math.max(i(split[3]), i(split[4]));
		this.ymin = Math.min(i(split[3]), i(split[4]));
		this.zmax = Math.max(i(split[5]), i(split[6]));
		this.zmin = Math.min(i(split[5]), i(split[6]));
	}

	public Iterator<Block> iterator() {
		return new CuboidIterator(this);
	}

	public Location getRandomLocation() {
		int x = new Random().nextInt(xmax - xmin) + 1;
		int y = new Random().nextInt(xmax - xmin) + 1;
		int z = new Random().nextInt(xmax - xmin) + 1;
		return new Location(Bukkit.getWorld(world), xmin + x, ymin + y, zmin + z);
	}

	public boolean contains(Location loc) {
		return loc != null && loc.getWorld().getName().equals(world) && loc.getBlockX() >= xmin && loc.getBlockX() <= xmax && loc.getBlockY() >= ymin && loc.getBlockY() <= ymax && loc.getBlockZ() >= zmin && loc.getBlockZ() <= zmax;
	}

	@Override
	public String toString() {
		return world + " : " + xmax + " : " + xmin + " : " + ymax + " : " + ymin + " : " + zmax + " : " + zmin;
	}

	private int i(String string) {
		return Integer.parseInt(string);
	}

	public class CuboidIterator implements Iterator<Block> {
		String world;
		CuboId cuboId;
		int baseX, baseY, baseZ, sizeX, sizeY, sizeZ, x, y, z;
		List<Block> blocks;
		List<Location> blocks2;
		Map<Location, Material> blocks3;

		public CuboidIterator(CuboId cuboId) {
			x = y = z = 0;
			baseX = getXmin();
			baseY = getYmin();
			baseZ = getZmin();
			this.cuboId = cuboId;
			this.world = cuboId.getWorld();
			sizeX = Math.abs(getXmax() - getXmin()) + 1;
			sizeY = Math.abs(getYmax() - getYmin()) + 1;
			sizeZ = Math.abs(getZmax() - getZmin()) + 1;
		}

		public boolean hasNext() {
			return x < sizeX && y < sizeY && z < sizeZ;
		}

		public Block next() {
			Block b = Bukkit.getWorld(world).getBlockAt(baseX + x, baseY + y, baseZ + z);
			if (++x >= sizeX) {
				x = 0;
				if (++y >= sizeY) {
					y = 0;
					++z;
				}
			}
			return b;
		}

		public void remove() {
		}

		public Map<Location, Material> getBlockAtLocations() {
			if (blocks3 == null) {
				blocks3 = new HashMap<>();
				for (int x = cuboId.getXmin(); x <= cuboId.getXmax(); x++)
					for (int y = cuboId.getYmin(); y <= cuboId.getYmax(); y++)
						for (int z = cuboId.getZmin(); z <= cuboId.getZmax(); z++)
							blocks3.put(new Location(Bukkit.getWorld(world), x, y, z), Bukkit.getWorld(world).getBlockAt(x, y, z).getType());
			}
			return blocks3;
		}

		public Collection<Location> getLocations() {
			if (blocks2 == null) {
				blocks2 = new ArrayList<>();
				for (int x = cuboId.getXmin(); x <= cuboId.getXmax(); x++)
					for (int y = cuboId.getYmin(); y <= cuboId.getYmax(); y++)
						for (int z = cuboId.getZmin(); z <= cuboId.getZmax(); z++)
							blocks2.add(new Location(Bukkit.getWorld(world), x, y, z));
			}
			return blocks2;
		}

		public Collection<Block> iterateBlocks() {
			if (blocks == null) {
				blocks = new ArrayList<>();
				for (int x = cuboId.getXmin(); x <= cuboId.getXmax(); x++)
					for (int y = cuboId.getYmin(); y <= cuboId.getYmax(); y++)
						for (int z = cuboId.getZmin(); z <= cuboId.getZmax(); z++)
							blocks.add(new Location(Bukkit.getWorld(world), x, y, z).getBlock());
			}
			return blocks;
		}
	}
}