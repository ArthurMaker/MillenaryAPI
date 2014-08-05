package Millenary.NMS;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityAmbient;

public class HNEntity extends EntityAmbient {
	
	private Player p;
	private boolean onp;
	
	public HNEntity(Player p) {
		super(((CraftPlayer)p).getHandle().world);
		this.p = p;
		this.setInvisible(true);
		this.persistent = true;
		try{
			Field f = Entity.class.getDeclaredField("invulnerable");
			f.setAccessible(true);
			f.setBoolean(this, true);
		}catch (Exception e){
			e.printStackTrace();
		}
		this.setPosition(p.getEyeLocation().getX(), p.getEyeLocation().getY(), p.getEyeLocation().getZ());
		this.world.addEntity(this);
		this.setPassengerOf(((CraftPlayer)p).getHandle());
		this.onp = true;
	}
	
	public void toggle(){
		if(this.onp){
			this.setPassengerOf(null);
			this.onp = false;
		}else{
			this.setPassengerOf(((CraftPlayer)this.p).getHandle());
			this.onp = true;
		}
	}
	
	public void removeFromPlayer(){
		if(!this.onp) return;
		this.setPassengerOf(null);
		this.onp = false;
	}
	
	public void addOnPlayer(){
		if(this.onp) return;
		this.setPassengerOf(((CraftPlayer)this.p).getHandle());
		this.onp = true;
	}
	
	public void setOnPlayer(boolean b){
		if(this.onp){
			this.setPassengerOf(null);
			this.onp = false;
		}else{
			this.setPassengerOf(((CraftPlayer)this.p).getHandle());
			this.onp = true;
		}
	}
	
	public boolean isOnPlayer(){
		return this.onp;
	}
	
}