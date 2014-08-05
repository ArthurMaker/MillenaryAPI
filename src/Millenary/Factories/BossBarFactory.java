package Millenary.Factories;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Millenary.MillenaryAPI;
 
public class BossBarFactory {
	
	private HashMap<UUID, DragonBar> players = new HashMap<UUID, DragonBar>();
	private Plugin plugin;
	
	public BossBarFactory (Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void sendBossBar(String message, int health, Player player){
		if(this.players.containsKey(player.getUniqueId())) removeBossBar(player);
		DragonBar dragon = new DragonBar(message, health, player.getLocation().clone().add(0D, -350D, 0D));
		this.players.put(player.getUniqueId(), dragon);
		sendDragonBar(dragon, player);
	}
	
	public void sendDelayedBossBar(String message, int health, final Player player, int seconds){
		if(players.containsKey(player.getUniqueId())) removeBossBar(player);
		DragonBar dragon = new DragonBar(message, health, player.getLocation().clone().add(0D, -350D, 0D));
		this.players.put(player.getUniqueId(), dragon);
		sendDragonBar(dragon, player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable(){
			public void run(){
				destroyDragonBar(getBossBar(player), player);
			}
		}, Long.valueOf((long)(seconds*20)));
	}
	
	public void sendAnimatedDelayedBossBar(String message, Player player, int seconds){
		if(this.players.containsKey(player.getUniqueId())) removeBossBar(player);
		DragonBar dragon = new DragonBar(message, 100, player.getLocation().clone().add(0D, -350D, 0D));
		this.players.put(player.getUniqueId(), dragon);
		sendDragonBar(dragon, player);
		DragonTimer dT = new DragonTimer(dragon, player);
		Long l = Long.valueOf(seconds);
		dT.start(Bukkit.getScheduler().runTaskTimer(this.plugin, dT, l, l));
	}
	
	public void updateBossBar(Player player){
		sendDragonBar(getBossBar(player), player);
	}
	
	public void removeBossBar(Player player){
		destroyDragonBar(getBossBar(player), player);
		if(this.players.containsKey(player.getUniqueId())) this.players.remove(player.getUniqueId());
	}
	
	public DragonBar getBossBar(Player player){
		if(this.players.containsKey(player.getUniqueId())) return this.players.get(player.getUniqueId());
		return null;
	}
	
	protected void sendDragonBar(DragonBar d, Player p){
		if(d != null) MillenaryAPI.getPacketFactory().sendPacket(d.getSpawnPacket(), p);
	}
	
	protected void destroyDragonBar(DragonBar d, Player p){
		if(d != null) MillenaryAPI.getPacketFactory().sendPacket(d.getDestroyPacket(), p);
	}
	
	protected static class DragonBar {
		private EntityEnderDragon dragon;
		private Float health = 200F;
		private World nms_world;
		private String message = "---";
		private int id = 10;
		
		public DragonBar (String s, Location l){
			this.nms_world = ((CraftWorld) l.getWorld()).getHandle();
			this.dragon = new EntityEnderDragon(this.nms_world);
			this.dragon.d(this.id);
			this.dragon.setLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ(), 0F, 0F);
			this.health = 200F;
			this.message = s;
			this.dragon.setCustomName(s);
			this.dragon.setCustomNameVisible(false);
			this.dragon.setHealth(this.health);
		}
		
		public DragonBar (String s, int i, Location l){
			this.nms_world = ((CraftWorld) l.getWorld()).getHandle();
			this.dragon = new EntityEnderDragon(this.nms_world);
			this.dragon.d(this.id);
			this.dragon.setLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ(), 0F, 0F);
			this.health = (i / 100.0F * 200.0F);
			this.message = s;
			this.dragon.setCustomName(s);
			this.dragon.setCustomNameVisible(false);
			this.dragon.setHealth(this.health);
		}
		
		public void setHealth(int i){
			this.health = (i / 100.0F * 200.0F);
			this.dragon.setHealth(this.health);
		}
		
		public int getHealth(){
			return ((int)(this.health / 200.0F * 100.0F));
		}
		
		public String getMessage(){
			return this.message;
		}
		
		public void setMessage(String s){
			this.message = s;
			this.dragon.setCustomName(s);
			this.dragon.setCustomNameVisible(false);
		}
		
		public Packet getSpawnPacket(){
		    return new PacketPlayOutSpawnEntityLiving(this.dragon);
		}
		
		public Packet getDestroyPacket(){
			return new PacketPlayOutEntityDestroy(this.dragon.getId());
		}
	}
	
	private static class DragonTimer extends BukkitRunnable implements Runnable {
		private DragonBar d;
		private Player p;
		private BukkitTask t;
		private int i = 100;
		public DragonTimer(DragonBar dragon, Player player){
			this.i = 100;
			this.d = dragon;
			this.p = player;
		}
		
		public void start(BukkitTask task){
			this.t = task;
		}
		@Override
		public void run(){
			if(this.p == null){
				this.t.cancel();
				return;
			}
			if(!this.p.isOnline()){
				this.t.cancel();
				return;
			}
			if(!this.d.equals(MillenaryAPI.getBossBarFactory().getBossBar(this.p))){
				this.t.cancel();
				return;
			}
			if(this.i <= 1){
				MillenaryAPI.getBossBarFactory().removeBossBar(this.p);
				this.t.cancel();
				return;
			}
			this.i--;
			this.d.setHealth(this.i);
			MillenaryAPI.getBossBarFactory().updateBossBar(this.p);
		}
	}
	
}