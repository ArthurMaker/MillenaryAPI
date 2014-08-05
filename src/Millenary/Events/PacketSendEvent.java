package Millenary.Events;

import net.minecraft.server.v1_7_R3.Packet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import Millenary.Factories.PacketFactory.PacketWrapper;

public class PacketSendEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private PacketWrapper packet;
	private Player player;
	
	public PacketSendEvent(PacketWrapper packet, Player player){
		this.packet = packet;
		this.player = player;
	}
	
	public Packet getPacket(){
		return this.packet.getPacket();
	}
	
	public PacketWrapper getPacketWrapper(){
		return this.packet;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
		this.packet.setCancelled(b);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}