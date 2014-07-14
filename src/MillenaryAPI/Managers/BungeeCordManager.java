package MillenaryAPI.Managers;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import MillenaryAPI.MillenaryAPI;

public class BungeeCordManager {
	
	private Plugin api;
	
	public BungeeCordManager(MillenaryAPI plugin){
		this.api = plugin;
	}
	
	public void connect(org.bukkit.entity.Player player, String server_name){
		if(!Bukkit.getMessenger().getOutgoingChannels(this.api).contains("BungeeCord")) Bukkit.getMessenger().registerOutgoingPluginChannel(this.api, "BungeeCord");
		ByteArrayDataOutput outstream = ByteStreams.newDataOutput(); 
		outstream.writeUTF("Connect");
		outstream.writeUTF(server_name);
		player.sendPluginMessage(this.api, "BungeeCord", outstream.toByteArray());
	}
	
	public MillenaryServer getServerInfo(String address, int port) throws IOException {
		MillenaryServerOptions options = new MillenaryServerOptions(address, port);
		PingUtils.v(options.getIp(), "Ping can't be null."); // O ping não pode ser inválida.
		PingUtils.v(options.getPort(), "Port can't be null."); // A porta não pode ser inválida.
		final Socket socket = new Socket();
		socket.connect(new InetSocketAddress(options.getIp(), options.getPort()), options.getTimeout());
		final DataInputStream in = new DataInputStream(socket.getInputStream());
		final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
		DataOutputStream handshake = new DataOutputStream(handshake_bytes);
		handshake.writeByte(PingUtils.PACKET_HANDSHAKE);
		PingUtils.w(handshake, PingUtils.PROTOCOL_VERSION);
		PingUtils.w(handshake, options.getIp().length());
		handshake.writeBytes(options.getIp());
		handshake.writeShort(options.getPort());
		PingUtils.w(handshake, PingUtils.STATUS_HANDSHAKE);
		PingUtils.w(out, handshake_bytes.size());
		out.write(handshake_bytes.toByteArray());
		out.writeByte(0x01);
		out.writeByte(PingUtils.PACKET_STATUSREQUEST);
		PingUtils.r(in);
		int id = PingUtils.r(in);
		PingUtils.i(id == -1, "The server closed the connection unexpectedly."); // O servidor encerrou a conexão inesperadamente.
		PingUtils.i(id != PingUtils.PACKET_STATUSREQUEST, "The server returned an invalid packet."); // O servidor retornou um packet inválido.
		int length = PingUtils.r(in);
		PingUtils.i(length == -1, "The server closed the connection unexpectedly."); // O servidor encerrou a conexão inesperadamente.
		PingUtils.i(length == 0, "The server returned an invalid value."); // O servidor retornou um valor inválido.
		byte[] data = new byte[length];
		in.readFully(data);
		String json = new String(data, "UTF-8");
		out.writeByte(0x09);
		out.writeByte(PingUtils.PACKET_PING);
		out.writeLong(System.currentTimeMillis());
		PingUtils.r(in);
		id = PingUtils.r(in);
		PingUtils.i(id == -1, "The server closed the connection unexpectedly."); // O servidor encerrou a conexão inesperadamente.
		PingUtils.i(id != PingUtils.PACKET_PING, "The server returned an invalid packet."); // O servidor retornou um packet inválido.
		handshake.close();
		handshake_bytes.close();
		out.close();
		in.close();
		socket.close();
		return new Gson().fromJson(json, MillenaryServer.class);
	}
	
	public static class MillenaryServer {
		private String description;
		private BungeePlayers players;
	    private Version version;
	    private String favicon;
	    public String getDescription() {
	    	return this.description;
	    }
	    public BungeePlayers getPlayers() {
	    	return this.players;
	    }
	    public Version getVersion() {
	    	return this.version;
	    }
	    public String getFavicon() {
	    	return this.favicon;
	    }
	    public class BungeePlayers {
	    	private int max;
			private int online;
			private List<BungeePlayer> sample;
			public int getMax() {
				return this.max;
			}
			public int getOnline() {
				return this.online;
			}
			public List<BungeePlayer> getSample() {
				return this.sample;
			}
			public List<String> toStringList(){
				List<String> list = new ArrayList<String>();
				for(BungeePlayer p : this.sample){
					list.add(p.getName());
				}
				return list;
			}
	    }
	    public class BungeePlayer {
	    	private String name;
			private String id;
			public String getName() {
				return this.name;
			}
			public String getId() {
				return this.id;
			}
	    }
	    public class Version {
	    	private String name;
			private int protocol;
			public String getName() {
				return this.name;
			}
			public int getProtocol() {
				return this.protocol;
			}
	    }
	}
	
	public static class MillenaryServerOptions {
		private String a = "your.server.com"; // coloque aqui o ip do seu servidor // insert here the ip of your server //
		private int p = 25565; // coloque aqui a porta do seu servidor (você pode encontrá-la no server.properties) //
							   // insert here the port of your server (you can locate it at server.properties) //
							   // Padrão do servidor: 25565 // Server default: 25565 //
	    private int t = 2000; // timeout
		public MillenaryServerOptions(String address, int port){
			this.a = address;
			this.p = port;
		}
		public MillenaryServerOptions(String address){
			this.a = address;
		}
		public String getServerAddress(){
			return (this.a + ":" + this.p);
		}
		public String getIp(){
			return this.a;
		}
		public int getPort(){
			return this.p;
		}
		public int getTimeout(){
			return this.t;
		}
	}
	
	private static class PingUtils {
		public static byte PACKET_HANDSHAKE = 0x00, PACKET_STATUSREQUEST = 0x00, PACKET_PING = 0x01;
	    public static int PROTOCOL_VERSION = 4, STATUS_HANDSHAKE = 1;
	    public static void v(final Object o, final String s){
			if(o == null) throw new RuntimeException(s);
	    }
	    public static void i(final boolean b, final String s) throws IOException {
			if(b) throw new IOException(s);
	    }
	    public static int r(DataInputStream input) throws IOException{
			int out = 0; int bytes = 0;
			while(true){
				int k = input.readByte();
				out |= (k & 0x7F) << bytes++ * 7;
			  //if(bytes > 32)
				if(bytes > 5) throw new RuntimeException("VarInt is too big!"); // A VarInt é muito grande!
				if((k & 0x80) != 128) break;
			}
			return out;
	    }
	    public static void w(DataOutputStream out, int paramInt) throws IOException {
			while(true){
			    if ((paramInt & 0xFFFFFF80) == 0){
					out.writeByte(paramInt);
					return;
			    }
			    out.writeByte(paramInt & 0x7F | 0x80);
			    paramInt >>>= 7;
			}
	    }
	    @SuppressWarnings("unused")
		public static String readString(DataInputStream input) throws IOException {
	        int len = r(input);
	        byte[] b = new byte[len];
	        input.readFully(b);
	        return new String(b, "UTF-8");
	    }
	}
}