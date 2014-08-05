package Millenary.Utils;

import net.minecraft.server.v1_7_R3.PacketPlayOutEntityStatus;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import Millenary.MillenaryAPI;

public class FireworkPlayer {
	
	public static void playToPlayer(Location l, FireworkEffect fe, Player... players){
		Firework fw = createFirework(l, fe);
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftEntity)fw).getHandle(), (byte)17);
		fw.remove();
		MillenaryAPI.getPacketFactory().sendPacket(packet, players);
	}
	
	public static void playToLocation(Location l, FireworkEffect fe){
		Firework fw = createFirework(l, fe);
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftEntity)fw).getHandle(), (byte)17);
		fw.remove();
		MillenaryAPI.getPacketFactory().sendPacket(packet, l.getWorld().getPlayers());
	}
	
	protected static Firework createFirework(Location l, FireworkEffect fe){
		Firework fw = l.getWorld().spawn(l, Firework.class);
		FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
		data.clearEffects();
		data.setPower(1);
		data.addEffect(fe);
		fw.setFireworkMeta(data);
		return fw;
	}
	
}