package Millenary.Factories.PacketFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.ServerConnection;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;

import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import Millenary.MillenaryAPI;

public class ProtocolManager implements Listener {
	
	private AtomicInteger channelid = new AtomicInteger(0);
	private Field NETWORK_MANAGER_CHANNEL;
	private Field MINECRAFT_SERVER_CONSOLE;
	private Field SERVER_CONNECTION;
	private Field SERVER_CONNECTIONS;
	private String handlername;
	
	private volatile MillenaryAPI api;
	private volatile Map<UUID, Channel> channels = new HashMap<UUID, Channel>();
	private volatile List<Channel> injectedChannels = new ArrayList<Channel>();
	private volatile boolean disabled = false;
	
	public ProtocolManager(MillenaryAPI api) {
		this.api = api;
		this.handlername = getHandlerName();
		try{
			NETWORK_MANAGER_CHANNEL = NetworkManager.class.getDeclaredField("m"); // Keep an eye on
																				  // https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/NetworkManager.java#L39
																				  // to see if the field's name changed
			NETWORK_MANAGER_CHANNEL.setAccessible(true);
			MINECRAFT_SERVER_CONSOLE = CraftServer.class.getDeclaredField("console"); // Keep an eye on
																					  // https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/org/bukkit/craftbukkit/CraftServer.java#L206
																					  // to see if the field's name changed
			MINECRAFT_SERVER_CONSOLE.setAccessible(true);
			SERVER_CONNECTION = MinecraftServer.class.getDeclaredField("p"); // Keep an eye on
																			 // https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/MinecraftServer.java#L44
																			 // to see if the field's name changed
			SERVER_CONNECTION.setAccessible(true);
			SERVER_CONNECTIONS = ServerConnection.class.getDeclaredField("f"); // Keep an eye on
																			   // https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/ServerConnection.java#L26
																			   // to see if the field's name changed
			SERVER_CONNECTIONS.setAccessible(true);
		}catch (Exception e){
			e.printStackTrace();
			this.disabled = true;
		}
		this.api.getServer().getPluginManager().registerEvents(this, this.api);
		this.injectAll();
	}
	
	private void injectAll(){
		if(this.disabled) return;
		for(Player p : this.api.getServer().getOnlinePlayers()) injectPlayer(p);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		injectPlayer(event.getPlayer());
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPing(ServerListPingEvent event) throws IllegalArgumentException, IllegalAccessException {
		MinecraftServer server = (MinecraftServer) this.MINECRAFT_SERVER_CONSOLE.get(this.api.getServer());
		ServerConnection connection = (ServerConnection) this.SERVER_CONNECTION.get(server);
		List<NetworkManager> list = (List<NetworkManager>) this.SERVER_CONNECTIONS.get(connection);
		for(NetworkManager manager : list) injectConnection(manager);
	}
	
	protected String getHandlerName(){
		return "connection-" + this.api.getName() + "-" + this.channelid.incrementAndGet();
	}
	
	public void sendPacket(Player player, Packet packet){
		getChannel(player).pipeline().writeAndFlush(packet);
	}
	
	public void sendPacket(Player player, PacketWrapper packetwrapper){
		getChannel(player).pipeline().writeAndFlush(packetwrapper.getPacket());
	}
	
	public boolean hasInjected(Player p){
		return hasInjected(getChannel(p));
	}
	
	public boolean hasInjected(Channel c){
		return (c.pipeline().get(this.handlername) != null);
	}
	
	public Channel getChannel(Player p){
		Channel c = this.channels.get(p.getUniqueId());
		if(c == null){
			try{
				this.channels.put(p.getUniqueId(), c = (Channel) NETWORK_MANAGER_CHANNEL.get(((CraftPlayer)p).getHandle().playerConnection.networkManager));
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return c;
	}
	
	public void uninjectPlayer(Player p){
		uninjectChannel(getChannel(p));
	}
	
	public void uninjectChannel(final Channel c){
		if(this.disabled) return;
		if(!hasInjected(c)) return;
		c.eventLoop().execute(new Runnable(){
			@Override
			public void run(){
				c.pipeline().remove(ProtocolManager.this.handlername);
			}
		});
	}
	
	public void injectPlayer(final Player p) {
		if(p == null) return;
		if(this.disabled) return;
		if(hasInjected(p)) return;
		try{
			Channel channel = (Channel) NETWORK_MANAGER_CHANNEL.get(((CraftPlayer)p).getHandle().playerConnection.networkManager);
			channels.put(p.getUniqueId(), channel);
			channel.pipeline().addBefore("packet_handler", this.handlername, new ChannelDuplexHandler(){
				@Override
				public void channelRead(ChannelHandlerContext handler, Object msg) throws Exception {
					PacketWrapper packet = new PacketWrapper((Packet)msg);
					MillenaryAPI.getPacketFactory().receiveWrapperFromServer(packet, p);
					if(packet.isCancelled()) return;
					super.channelRead(handler, msg);
				}
				@Override
				public void write(ChannelHandlerContext handler, Object msg, ChannelPromise cp) throws Exception {
					PacketWrapper packet = new PacketWrapper((Packet)msg);
					MillenaryAPI.getPacketFactory().sendWrapperToServer(packet, p);
					if(packet.isCancelled()) return;
					super.write(handler, msg, cp);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void injectConnection(NetworkManager manager) throws IllegalArgumentException, IllegalAccessException {
		if(this.disabled) return;
		Channel channel = (Channel) this.NETWORK_MANAGER_CHANNEL.get(manager);
		if(this.injectedChannels.contains(channel)) return;
		this.injectedChannels.add(channel);
		channel.pipeline().addBefore("packet_handler", this.handlername, new ChannelDuplexHandler(){
			@Override
			public void channelRead(ChannelHandlerContext handler, Object msg) throws Exception {
				if(!msg.getClass().getSimpleName().startsWith("PacketStatus")){
					super.channelRead(handler, msg);
					return;
				}
				PacketWrapper packet = new PacketWrapper((Packet)msg);
				MillenaryAPI.getPacketFactory().receiveWrapperFromServer(packet, null);
				if(packet.isCancelled()) return;
				super.channelRead(handler, msg);
			}
			@Override
			public void write(ChannelHandlerContext handler, Object msg, ChannelPromise cp) throws Exception {
				if(!msg.getClass().getSimpleName().startsWith("PacketStatus")){
					super.write(handler, msg, cp);
					return;
				}
				PacketWrapper packet = new PacketWrapper((Packet)msg);
				MillenaryAPI.getPacketFactory().sendWrapperToServer(packet, null);
				if(packet.isCancelled()) return;
				super.write(handler, msg, cp);
			}
		});
	}
	
	public void disable() {
		if(this.disabled) return;
		HandlerList.unregisterAll(this);
		for(Entry<UUID, Channel> c : this.channels.entrySet()) if(this.api.getServer().getPlayer(c.getKey()) != null) c.getValue().pipeline().remove(this.handlername);
		this.disabled = true;
	}
	
}