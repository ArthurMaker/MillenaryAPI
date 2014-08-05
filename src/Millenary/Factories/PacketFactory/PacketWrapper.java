package Millenary.Factories.PacketFactory;

import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.entity.Player;

import Millenary.MillenaryAPI;
import net.minecraft.server.v1_7_R3.Packet;

public class PacketWrapper {
	
	protected Object packet;
	private boolean cancelled = false;
	
	public PacketWrapper (Object packet){
		this.packet = packet;
	}
	
	public void setValue(String fieldname, Object value){
		try{
			Field f = this.packet.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			f.set(this.packet, value);
			f.setAccessible(false);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public Object getValue(String fieldname){
		Object o = null;
		try{
			Field f = this.packet.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			o = f.get(this.packet);
			f.setAccessible(false);
		}catch (Exception e){
			e.printStackTrace();
		}
		return o;
	}
	
	public boolean send(Player p){
		if(this.cancelled) return false;
		MillenaryAPI.getPacketFactory().sendPacket((Packet)this.packet, p);
		return true;
	}
	
	public boolean broadcast(World w){
		if(this.cancelled) return false;
		MillenaryAPI.getPacketFactory().sendPacket((Packet)this.packet, w.getPlayers());
		return true;
	}
	
	public Packet getPacket(){
		if(this.packet instanceof Packet) return (Packet) this.packet;
		return null;
	}
	
	public String getPacketName(){
		return this.packet.getClass().getSimpleName();
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
	
}