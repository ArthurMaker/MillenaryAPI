package Millenary.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import Millenary.MillenaryAPI;

public enum ParticleEffect {
	HUGE_EXPLOSION("hugeexplosion"), LARGE_EXPLODE("largeexplode"), FIREWORKS_SPARK("fireworksSpark"), BUBBLE("bubble"), SUSPEND("suspend"),
	DEPTH_SUSPEND("depthSuspend"), TOWN_AURA("townaura"), CRIT("crit"), MAGIC_CRIT("magicCrit"), MOB_SPELL("mobSpell"), MOB_SPELL_AMBIENT("mobSpellAmbient"), SPELL("spell"),
	INSTANT_SPELL("instantSpell"), WITCH_MAGIC("witchMagic"), NOTE("note"), PORTAL("portal"), ENCHANTMENT_TABLE("enchantmenttable"), EXPLODE("explode"), FLAME("flame"), LAVA("lava"),
	FOOTSTEP("footstep"), SPLASH("splash"), LARGE_SMOKE("largesmoke"), CLOUD("cloud"), RED_DUST("reddust"), SNOWBALL_POOF("snowballpoof"), DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"), SNOW_SHOVEL("snowshovel"), SLIME("slime"), HEART("heart"), ANGRY_VILLAGER("angryVillager"), HAPPY_VILLAGER("happyVillager"), WAKE("wake");//, BARRIER("barrier");
	
	private final String name;
	
	private ParticleEffect(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void display(Location l, float offsetX, float offsetY, float offsetZ, float speed, int amount){
		sendPacket(new PacketPlayOutWorldParticles(this.name, (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, speed, amount), l.getWorld().getPlayers());
	}
	
	public void display(Location l, float offsetX, float offsetY, float offsetZ, float speed, int amount, Player... players){
		sendPacket(new PacketPlayOutWorldParticles(this.name, (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, speed, amount), players);
	}
	
	public void displayInLine(Location l1, Location l2, float offsetX, float offsetY, float offsetZ, float speed, int amount){
		for(Location l : this.getLine(l1, l2)) display(l, offsetX, offsetY, offsetZ, speed, amount);
	}
	
	public static void displayBlockDestroyEffect(int blockid, int blockdata, Location l, float offsetX, float offsetY, float offsetZ, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("blockcrack_" + blockid + "_" + blockdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, 1F, amount),
				l.getWorld().getPlayers());
	}
	
	public static void displayItemDestroyEffect(int itemid, int itemdata, Location l, float offsetX, float offsetY, float offsetZ, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("iconcrack_" + itemid + "_" + itemdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, 0.1F, amount),
				l.getWorld().getPlayers());
	}
	
	public static void displayBlockDustEffect(int blockid, int blockdata, Location l, float offsetX, float offsetY, float offsetZ, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("blockdust_" + blockid + "_" + blockdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, 0.1F, amount),
				l.getWorld().getPlayers());
	}
	
	public static void displayBlockDestroyEffect(int blockid, int blockdata, Location l, float offsetX, float offsetY, float offsetZ, float speed, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("blockcrack_" + blockid + "_" + blockdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, speed, amount),
				l.getWorld().getPlayers());
	}
	
	public static void displayItemDestroyEffect(int itemid, int itemdata, Location l, float offsetX, float offsetY, float offsetZ, float speed, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("iconcrack_" + itemid + "_" + itemdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, speed, amount),
				l.getWorld().getPlayers());
	}
	
	public static void displayBlockDustEffect(int blockid, int blockdata, Location l, float offsetX, float offsetY, float offsetZ, float speed, int amount){
		sendPacket(new PacketPlayOutWorldParticles(("blockdust_" + blockid + "_" + blockdata), (float)l.getX(), (float)l.getY(), (float)l.getZ(), offsetX, offsetY, offsetZ, speed, amount),
				l.getWorld().getPlayers());
	}
	
	private static void sendPacket(Packet packet, Player... players){
		MillenaryAPI.getPacketFactory().sendPacket(packet, players);
	}
	
	private static void sendPacket(Packet packet, Collection<Player> players){
		MillenaryAPI.getPacketFactory().sendPacket(packet, players);
	}
	
	private List<Location> getLine(Location l1, Location l2){
		if(!l1.getWorld().equals(l2.getWorld())) return null;
		double dx = (l2.getX()-l1.getX());
		double dy = (l2.getY()-l1.getY());
		double dz = (l2.getZ()-l1.getZ());
		double to = (Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)))*3.0D);
		double strenght = 0.5D;
		double ds = (strenght/30.0D);
		//double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		//double vx = dx / distance * 0.015D; // may we can use it for motion (vector x) (EntityFX from Spout)
		//double vy = dy / distance * 0.015D; // may we can use it for motion (vector y) (EntityFX from Spout)
		//double vz = dz / distance * 0.015D; // may we can use it for motion (vector z) (EntityFX from Spout)
		List<Location> locs = new ArrayList<Location>();
		for (int i = 0; i < to; i++){
			double x = l1.getX() + dx / to * i;
			double y = l1.getY() + dy / to * i;
			double z = l1.getZ() + dz / to * i;
			strenght -= ds;
			if(strenght < 0.2D) strenght = 0.2D;
			locs.add(new Location(l1.getWorld(), x, y, z));
		}
		return locs;
	}
	
}