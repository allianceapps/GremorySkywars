package com.gmail.gremorydev14.gremoryskywars.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;

public class Reflection {

	@Getter
	private static String version = "";
	@Getter
	private static TitleUtils titleUtil = new TitleUtils();

	public static void sendTitle(Player p, String title, String subTitle) {
		if (title == null)
			title = "";
		if (subTitle == null)
			subTitle = "";
		titleUtil.setTimes(0, 40, 30);
		titleUtil.send(p, title.replace("&", "§"), subTitle.replace("&", "§"));
	}

	public static void sendChatPacket(Player p, String message) {
		Class<?> packetClazz = getNMSClass("PacketPlayOutChat");
		Class<?> ichatbasecomponentClazz = getNMSClass("IChatBaseComponent");
		Class<?> serializerClazz = getNMSClass("IChatBaseComponent$ChatSerializer");
		try {
			Object obj = packetClazz.getConstructor(ichatbasecomponentClazz).newInstance(serializerClazz.getMethod("a", String.class).invoke(null, message));
			Object handle = Reflection.getHandle(p);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
			sendToPlayer.invoke(connection, new Object[] { obj });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendChatAction(Player p, String message) {
		Class<?> packetClazz = getNMSClass("PacketPlayOutChat");
		Class<?> ichatbasecomponentClazz = getNMSClass("IChatBaseComponent");
		Class<?> serializerClazz = getNMSClass("IChatBaseComponent$ChatSerializer");
		try {
			Object obj = packetClazz.getConstructor(ichatbasecomponentClazz, byte.class).newInstance(serializerClazz.getMethod("a", String.class).invoke(null, message), (byte)2);
			Object handle = Reflection.getHandle(p);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Method sendToPlayer = connection.getClass().getMethod("sendPacket", new Class[] { NMS.Packet });
			sendToPlayer.invoke(connection, new Object[] { obj });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String className) {
		String fullName = "net.minecraft.server." + getVersion() + "." + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Class<?> getNMSClassException(String className) throws Exception {
		String fullName = "net.minecraft.server." + getVersion() + "." + className;
		Class<?> clazz = Class.forName(fullName);
		return clazz;
	}

	public static Class<?> getOBCClass(String className) {
		String fullName = "org.bukkit.craftbukkit." + getVersion() + "." + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>[] args) {
		for (Method m : clazz.getMethods()) {
			if (!m.getName().equals(name) || (args.length != 0 && !ClassListEqual(args, m.getParameterTypes())))
				continue;
			m.setAccessible(true);
			return m;
		}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equals = true;
		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] == l2[i])
				continue;
			return false;
		}
		return equals;
	}

	static {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		version = name.substring(name.lastIndexOf('.') + 1);
	}

	@SuppressWarnings("all")
	public static class JSONMessage {
		private JSONObject chatObject;

		public JSONMessage(String text) {
			chatObject = new JSONObject();
			chatObject.put("text", text);
		}

		public JSONMessage addExtra(ChatExtra extraObject) {
			if (!chatObject.containsKey("extra"))
				chatObject.put("extra", new JSONArray());
			JSONArray extra = (JSONArray) chatObject.get("extra");
			extra.add(extraObject.toJSON());
			chatObject.put("extra", extra);
			return this;
		}

		public String toString() {
			return chatObject.toJSONString();
		}

		public static class ChatExtra {
			private JSONObject chatExtra;

			public ChatExtra(String text) {
				chatExtra = new JSONObject();
				chatExtra.put("text", text);
			}

			public ChatExtra addClickEvent(ClickEventType action, String value) {
				JSONObject clickEvent = new JSONObject();
				clickEvent.put("action", action.getTypeString());
				clickEvent.put("value", value);
				chatExtra.put("clickEvent", clickEvent);
				return this;
			}

			public ChatExtra addHoverEvent(HoverEventType action, String value) {
				JSONObject hoverEvent = new JSONObject();
				hoverEvent.put("action", action.getTypeString());
				hoverEvent.put("value", value);
				chatExtra.put("hoverEvent", hoverEvent);
				return this;
			}

			public JSONObject toJSON() {
				return chatExtra;
			}

			public ChatExtra build() {
				return this;
			}
		}

		public static enum ClickEventType {
			RUN_COMMAND("run_command"), SUGGEST_COMMAND("suggest_command"), OPEN_URL("open_url"), CHANGE_PAGE("change_page");

			private final String type;

			ClickEventType(String type) {
				this.type = type;
			}

			public String getTypeString() {
				return type;
			}
		}

		public static enum HoverEventType {
			SHOW_TEXT("show_text"), SHOW_ITEM("show_item"), SHOW_ACHIEVEMENT("show_achievement");
			private final String type;

			HoverEventType(String type) {
				this.type = type;
			}

			public String getTypeString() {
				return type;
			}
		}
	}
}
