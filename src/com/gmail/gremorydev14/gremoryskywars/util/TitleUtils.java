package com.gmail.gremorydev14.gremoryskywars.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

class TitleUtils {

	private boolean ticks = true;
	private int fadeInTime;
	private int fadeOutTime;
	private int stayTime;
	private ChatColor color;
	private ChatColor subcolor;
	private final HashMap<String, String> json_data = new HashMap<>();

	public TitleUtils() {
		this.color = ChatColor.WHITE;
		this.subcolor = ChatColor.WHITE;
		setTimes(Integer.valueOf(5), Integer.valueOf(5), Integer.valueOf(60));
	}

	public void useTicks(boolean ticks) {
		this.ticks = ticks;
	}

	public void setTimes(Integer in, Integer out, Integer stay) {
		this.fadeInTime = (in == null ? this.fadeInTime : in.intValue());
		this.fadeOutTime = (out == null ? this.fadeOutTime : out.intValue());
		this.stayTime = (stay == null ? this.stayTime : stay.intValue());
	}

	public void setTitleColor(char color) {
		this.color = ChatColor.getByChar(color);
	}

	public void setSubtitleColor(char color) {
		this.subcolor = ChatColor.getByChar(color);
	}

	public void clearTitle(Player player) {
		try {
			Object handle = Reflection.getHandle(player);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);

			Object[] actions = NMS.PacketPlayOutTitle$EnumTitleAction.getEnumConstants();

			Constructor<?> constructor = NMS.PacketPlayOutTitle.getConstructor(new Class[] { NMS.PacketPlayOutTitle$EnumTitleAction, NMS.IChatBaseComponent });
			Object packet = constructor.newInstance(new Object[] { actions[3], null });

			Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
			sendToPlayer.invoke(connection, new Object[] { packet });
		} catch (Exception e) {
			System.out.println("An problem occurred during sending clear title packet (Problema ocorrido ao enviar packet de limpar titulo)");
		}
	}

	public void resetTitle(Player player) {
		try {
			Object handle = Reflection.getHandle(player);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Object[] actions = NMS.PacketPlayOutTitle$EnumTitleAction.getEnumConstants();

			Constructor<?> constructor = NMS.PacketPlayOutTitle.getConstructor(new Class[] { NMS.PacketPlayOutTitle$EnumTitleAction, NMS.IChatBaseComponent });
			Object packet = constructor.newInstance(new Object[] { actions[4], null });

			Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
			sendToPlayer.invoke(connection, new Object[] { packet });
		} catch (Exception e) {
			System.out.println("An problem occurred during sending reset title packet (Problema ocorrido ao enviar packet de reset de titulo)");
		}
	}

	public void send(Player player, String title, String subtitle) {
		resetTitle(player);
		try {
			Object handle = Reflection.getHandle(player);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);

			Object[] actions = NMS.PacketPlayOutTitle$EnumTitleAction.getEnumConstants();

			Constructor<?> constructor = NMS.PacketPlayOutTitle.getConstructor(new Class[] { NMS.PacketPlayOutTitle$EnumTitleAction, NMS.IChatBaseComponent, Integer.TYPE, Integer.TYPE, Integer.TYPE });
			Object packet = constructor.newInstance(
					new Object[] { actions[2], null, Integer.valueOf(this.fadeInTime * (this.ticks ? 1 : 20)), Integer.valueOf(this.stayTime * (this.ticks ? 1 : 20)), Integer.valueOf(this.fadeOutTime * (this.ticks ? 1 : 20)) });

			if ((this.fadeInTime != -1) && (this.fadeOutTime != -1) && (this.stayTime != -1)) {
				Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
				sendToPlayer.invoke(connection, new Object[] { packet });
			}

			addJSON("text", ChatColor.translateAlternateColorCodes('&', title));
			addJSON("color", this.color.name().toLowerCase());

			Object serialized = NMS.IChatBaseComponent$ChatSerializer.getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { toJSON() });

			constructor = NMS.PacketPlayOutTitle.getConstructor(new Class[] { NMS.PacketPlayOutTitle$EnumTitleAction, NMS.IChatBaseComponent });
			packet = constructor.newInstance(new Object[] { actions[0], serialized });

			Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
			sendToPlayer.invoke(connection, new Object[] { packet });

			if (subtitle != null) {
				addJSON("text", ChatColor.translateAlternateColorCodes('&', subtitle));
				addJSON("color", this.subcolor.name().toLowerCase());

				serialized = NMS.IChatBaseComponent$ChatSerializer.getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { toJSON() });

				constructor = NMS.PacketPlayOutTitle.getConstructor(new Class[] { NMS.PacketPlayOutTitle$EnumTitleAction, NMS.IChatBaseComponent });
				packet = constructor.newInstance(new Object[] { actions[1], serialized });

				sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
				sendToPlayer.invoke(connection, new Object[] { packet });
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An problem occurred during sending title packet (Problema ocorrido ao enviar packet de titulo)");
		}
	}

	private String toJSON() {
		JSONObject json = new JSONObject(this.json_data);
		this.json_data.clear();
		return json.toJSONString();
	}

	private void addJSON(String key, String value) {
		this.json_data.put(key, value);
	}

}
