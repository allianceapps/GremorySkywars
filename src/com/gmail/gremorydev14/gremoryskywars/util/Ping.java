package com.gmail.gremorydev14.gremoryskywars.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public class Ping {

	private InetSocketAddress host;
	private JSONObject json;
	private int timeoutInt = 5;

	public Ping(String ip, int porta, int tempo) {
		try {
			host = new InetSocketAddress(ip, porta);

			this.timeoutInt = tempo;

			fetchData();
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("resource")
	public void fetchData() {
		try {
			Socket socket = new Socket();
			OutputStream outputStream;
			DataOutputStream dataOutputStream;
			InputStream inputStream;
			InputStreamReader inputStreamReader;

			socket.setSoTimeout(timeoutInt);

			socket.connect(host, timeoutInt);

			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);

			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);

			ByteArrayOutputStream b = new ByteArrayOutputStream();

			DataOutputStream handshake = new DataOutputStream(b);
			handshake.writeByte(0x00);
			writeVarInt(handshake, 47);
			writeVarInt(handshake, host.getHostString().length());

			handshake.writeBytes(host.getHostString());
			handshake.writeShort(host.getPort());
			writeVarInt(handshake, 1);

			writeVarInt(dataOutputStream, b.size());
			dataOutputStream.write(b.toByteArray());

			dataOutputStream.writeByte(0x01);
			dataOutputStream.writeByte(0x00);
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			readVarInt(dataInputStream);

			int id = readVarInt(dataInputStream);
			if (id == -1)
				throw new IOException("ERRO");

			int length = readVarInt(dataInputStream);
			if (length == -1)
				throw new IOException("ERRO");

			byte[] in = new byte[length];

			dataInputStream.readFully(in);
			String json = new String(in);
			long now = System.currentTimeMillis();
			dataOutputStream.writeByte(0x09);
			dataOutputStream.writeByte(0x01);
			dataOutputStream.writeLong(now);

			readVarInt(dataInputStream);
			id = readVarInt(dataInputStream);
			if (id == -1)
				throw new IOException("ERRO");

			this.json = (JSONObject) new JSONParser().parse(json);
			dataOutputStream.close();
			outputStream.close();
			inputStreamReader.close();
			inputStream.close();
			socket.close();
			process();
		} catch (Exception e) {
		}
	}

	private String motd = null;
	private int max = 0;
	private int online = 0;

	public void process() {
		this.motd = String.valueOf(getJson().get("description")).replace("§", "&");
		JSONArray array = new JSONArray();
		JSONObject site = null;
		array.add(getJson().get("players"));
		max = Integer.parseInt(((JSONObject) array.get(0)).toString().split("max\":")[1].substring(0, 1));
		online = Integer.parseInt(((JSONObject) array.get(0)).toString().split("online\":")[1].substring(0, 1));
	}

	private int readVarInt(DataInputStream in) throws IOException {
		int i = 0, j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5)
				throw new RuntimeException("Int be higher");
			if ((k & 0x80) != 128)
				break;
		}
		return i;
	}

	private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}

			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}
}