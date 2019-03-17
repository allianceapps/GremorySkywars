package com.gmail.gremorydev14.jnbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gmail.gremorydev14.gremoryskywars.Main;
import com.gmail.gremorydev14.gremoryskywars.editor.Options;

@SuppressWarnings("all")
public class Schematic {

	public short[] blocks;
	public byte[] data;
	public short width;
	public short lenght;
	public short height;

	private Schematic(short[] blocks2, byte[] data, short width, short lenght, short height) {
		this.blocks = blocks2;
		this.data = data;
		this.width = width;
		this.lenght = lenght;
		this.height = height;
	}

	public static void generateAsync(Location location, List<Block> schematicBlocks) {
		Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable() {

			@Override
			public void run() {
				try {
					Schematic.generateSchematic(location.getWorld(), location, Schematic.loadSchematic(new File(Options.getSchematic())), schematicBlocks);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void generateSchematic(World world, Location loc2, Schematic schematic, List<Block> coliseu) {
		short[] blocks = schematic.blocks;
		byte[] blockData = schematic.data;

		short length = schematic.lenght;
		short width = schematic.width;
		short height = schematic.height;
		
		Location loc = new Location(loc2.getWorld(), loc2.getX() - width / 2, loc2.getY() - 2, loc2.getZ() - length / 2);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					int index = y * width * length + z * width + x;
					Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
					coliseu.add(block);
					block.setTypeIdAndData(blocks[index], blockData[index], true);
				}
			}
		}
	}

	public static Schematic loadSchematic(File file) throws IOException, DataException {
		FileInputStream stream = new FileInputStream(file);
		NBTInputStream nbtStream = new NBTInputStream((stream));
		CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		nbtStream.close();
		if (!schematicTag.getName().equalsIgnoreCase("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		Map schematic;
		if (!(schematic = schematicTag.getValue()).containsKey("Blocks")) {
			throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
		}

		short width = ((ShortTag) getChildTag(schematic, "Width", ShortTag.class)).getValue().shortValue();
		short length = ((ShortTag) getChildTag(schematic, "Length", ShortTag.class)).getValue().shortValue();
		short height = ((ShortTag) getChildTag(schematic, "Height", ShortTag.class)).getValue().shortValue();

		byte[] blockId = ((ByteArrayTag) getChildTag(schematic, "Blocks", ByteArrayTag.class)).getValue();
		byte[] blockData = ((ByteArrayTag) getChildTag(schematic, "Data", ByteArrayTag.class)).getValue();
		byte[] addId = new byte[0];
		short[] blocks = new short[blockId.length];

		if (schematic.containsKey("AddBlocks")) {
			addId = ((ByteArrayTag) getChildTag(schematic, "AddBlocks", ByteArrayTag.class)).getValue();
		}

		for (int index = 0; index < blockId.length; index++) {
			if (index >> 1 >= addId.length) {
				blocks[index] = ((short) (blockId[index] & 0xFF));
			} else if ((index & 0x1) == 0)
				blocks[index] = ((short) (((addId[(index >> 1)] & 0xF) << 8) + (blockId[index] & 0xFF)));
			else {
				blocks[index] = ((short) (((addId[(index >> 1)] & 0xF0) << 4) + (blockId[index] & 0xFF)));
			}

		}

		return new Schematic(blocks, blockData, width, length, height);
	}

	private static <T extends Tag> Tag getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws DataException {
		if (!items.containsKey(key)) {
			throw new DataException("Schematic file is missing a \"" + key + "\" tag");
		}
		Tag tag = (Tag) items.get(key);
		if (!expected.isInstance(tag)) {
			throw new DataException(key + " tag is not of tag type " + expected.getName());
		}

		return (Tag) expected.cast(tag);
	}
}
