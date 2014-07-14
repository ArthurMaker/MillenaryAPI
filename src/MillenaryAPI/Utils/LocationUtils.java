package MillenaryAPI.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class LocationUtils{
	
	public static List<Location> getCircle(Location centerLoc, int radius){
		List<Location> circle = new ArrayList<Location>();
		World world = centerLoc.getWorld();
		int x = 0;
		int z = radius;
		int error = 0;
		int d = 2 - 2 * radius;
		while(z >= 0){
			circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() + z));
			circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() + z));
			circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() - z));
			circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() - z));
			error = 2 * (d + z) - 1;
			if((d < 0) && (error <= 0)){
				x++;
				d += 2 * x + 1;
			}else{
				error = 2 * (d - x) - 1;
				if((d > 0) && (error > 0)){
					z--;
					d += 1 - 2 * z;
				}else{
					x++;
					d += 2 * (x - z);
					z--;
				}
			}
		}
		return circle;
	}
	
	public static List<Vector> getCircleVectors(Vector vector, int radius, int height, boolean hollow, boolean sphere){
		List<Vector> vectors = new ArrayList<Vector>();
		int cx = vector.getBlockX();
		int cy = vector.getBlockY();
		int cz = vector.getBlockZ();
		for (int x = cx - radius; x <= cx + radius; x++){
			for (int z = cz - radius; z <= cz + radius; z++){
				for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++){
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) vectors.add(new Vector(x, y, z));
				}
			}
		}
		return vectors;
	}
	
	public static void pullTo(Entity e, Location loc, boolean direto) {
		Location l = e.getLocation();
		if (l.distanceSquared(loc) < 9) {
			if (loc.getY() > l.getY()) {
				e.setVelocity(new Vector(0, 0.25, 0));
				return;
			}
			Vector v = loc.toVector().subtract(l.toVector());
			e.setVelocity(v);
			return;
		}
		l.setY(l.getY() + 0.5);
		e.teleport(l);
		double d = loc.distance(l);
		double g = -0.08;
		double x = (1.0 + 0.07 * d) * (loc.getX() - l.getX()) / d;
		double y = (1.0 + 0.03 * d) * (loc.getY() - l.getY()) / d + (direto ? 0 : -0.5 * g * d);
		double z = (1.0 + 0.07 * d) * (loc.getZ() - l.getZ()) / d;
		Vector v = e.getVelocity();
		v.setX(x);
		v.setY(y);
		v.setZ(z);
		v.multiply(direto ? 1.5 : 1.0);
		e.setVelocity(v);
	}
	
	public static List<Location> getCuboid(Location corner1, Location corner2){
		List<Location> cube = new ArrayList<Location>();
		if(!corner1.getWorld().equals(corner2.getWorld())) return cube;
		int minX = (int) Math.min(corner1.getX(), corner2.getX());
		int maxX = (int) Math.max(corner1.getX(), corner2.getX());
		int minY = (int) Math.min(corner1.getY(), corner2.getY());
		int maxY = (int) Math.max(corner1.getY(), corner2.getY());
		int minZ = (int) Math.min(corner1.getZ(), corner2.getZ());
		int maxZ = (int) Math.max(corner1.getZ(), corner2.getZ());
		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				for(int z = minZ; z <= maxZ; z++){
					cube.add(new Location(corner1.getWorld(), x, y, z));
				}
			}
		}
		return cube;
	  }
	
	public static boolean isInsideCuboid(Location loc, Location corner1, Location corner2){
		if((loc.getWorld().equals(corner1.getWorld())) && (loc.getWorld().equals(corner2.getWorld()))){
    		double[] a = {Math.floor(corner1.getX()), Math.floor(corner1.getY()), Math.floor(corner1.getZ())};
    		double[] b = {Math.floor(corner2.getX()), Math.floor(corner2.getY()), Math.floor(corner2.getZ())};
    		if(a[0] > b[0]){
    			double temp = b[0];
    			b[0] = a[0];
    			a[0] = temp;
    		}
    		if(a[1] > b[1]){
    			double temp = b[1];
    			b[1] = a[1];
    			a[1] = temp;
    		}
    		if(a[2] > b[2]){
    			double temp = b[2];
    			b[2] = a[2];
    			a[2] = temp;
    		}
    		if(loc.getX() >= a[0] && loc.getX() <= b[0] && loc.getY() > a[1] && loc.getY() <= b[1] && loc.getZ() >= a[2] && loc.getZ() <= b[2]) return true;
    	}
	    return false;
	}
	
	public static List<Location> getPlain(Location position1, Location position2){
		List<Location> plain = new ArrayList<Location>();
		if (position1 == null) return plain;
		if (position2 == null) return plain;
		for (int x = Math.min(position1.getBlockX(), position2.getBlockX()); x <= Math.max(position1.getBlockX(), position2.getBlockX()); x++){
			for (int z = Math.min(position1.getBlockZ(), position2.getBlockZ()); z <= Math.max(position1.getBlockZ(), position2.getBlockZ()); z++){
				plain.add(new Location(position1.getWorld(), x, position1.getBlockY(), z));
			}
		}
		return plain;
	}
	
	public static List<Location> getBlocksInCuboid(Location corner1, Location corner2, boolean getOnlyAboveGround){
		List<Location> blocks = new ArrayList<Location>();
		if (corner1 == null) return blocks;
		if (corner2 == null) return blocks;
		for (int x = Math.min(corner1.getBlockX(), corner2.getBlockX()); x <= Math.max(corner1.getBlockX(), corner2.getBlockX()); x++)
			for (int z = Math.min(corner1.getBlockZ(), corner2.getBlockZ()); z <= Math.max(corner1.getBlockZ(), corner2.getBlockZ()); z++){
				for (int y = Math.min(corner1.getBlockY(), corner2.getBlockY()); y <= Math.max(corner1.getBlockY(), corner2.getBlockY()); y++) {
					Block b = corner1.getWorld().getBlockAt(x, y, z);
					if((b.getType() == Material.AIR) && ((!getOnlyAboveGround) || (b.getRelative(BlockFace.DOWN).getType() != Material.AIR))) blocks.add(b.getLocation());
				}
			}
		return blocks;
	}
	
	public static List<Location> getLine(Location position1, Location position2){
		List<Location> line = new ArrayList<Location>();
		int dx = Math.max(position1.getBlockX(), position2.getBlockX()) - Math.min(position1.getBlockX(), position2.getBlockX());
		int dy = Math.max(position1.getBlockY(), position2.getBlockY()) - Math.min(position1.getBlockY(), position2.getBlockY());
		int dz = Math.max(position1.getBlockZ(), position2.getBlockZ()) - Math.min(position1.getBlockZ(), position2.getBlockZ());
		int x1 = position1.getBlockX();
		int x2 = position2.getBlockX();
		int y1 = position1.getBlockY();
		int y2 = position2.getBlockY();
		int z1 = position1.getBlockZ();
		int z2 = position2.getBlockZ();
		int x = 0;
		int y = 0;
		int z = 0;
		int i = 0;
		int d = 1;
	    switch (getHighest(dx, dy, dz)) {
	    case 1:
	    	i = 0;
	    	d = 1;
	    	if (x1 > x2) d = -1;
	    	x = position1.getBlockX();
	    	do{
	    		i++;
	    		y = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
	    		z = z1 + (x - x1) * (z2 - z1) / (x2 - x1);
	    		line.add(new Location(position1.getWorld(), x, y, z));
	    		x += d;
	    	}while(i <= Math.max(x1, x2) - Math.min(x1, x2));
	    	break;
	    case 2:
	    	i = 0;
	    	d = 1;
	    	if (y1 > y2) d = -1;
	    	y = position1.getBlockY();
	    	do{
	    		i++;
	    		x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
	    		z = z1 + (y - y1) * (z2 - z1) / (y2 - y1);
	    		line.add(new Location(position1.getWorld(), x, y, z));
	    		y += d;
	    	}while(i <= Math.max(y1, y2) - Math.min(y1, y2));
	    	break;
	    case 3:
	    	i = 0;
	    	d = 1;
	    	if (z1 > z2) d = -1;
	    	z = position1.getBlockZ();
	    	do{
	    		i++;
	    		y = y1 + (z - z1) * (y2 - y1) / (z2 - z1);
	    		x = x1 + (z - z1) * (x2 - x1) / (z2 - z1);
	    		line.add(new Location(position1.getWorld(), x, y, z));
	    		z += d;
	    	}while(i <= Math.max(z1, z2) - Math.min(z1, z2));
	    }
	    return line;
	}
	
	public static HashSet<Entity> getNearbyEntities(Location location, double radius){
		double chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();
		for (double chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (double chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();
				for (Entity e: new Location(location.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
					if (e.getLocation().distance(location) <= radius && e.getLocation().getBlock() != location.getBlock())
						if (e instanceof Entity) radiusEntities.add((Entity)e);
				}
			}
		}
		return radiusEntities;
	}
	
	public static List<Location> getParticlesCircle(Location center, float radius, float distanceBetweenParticles){
		List<Location> locs = new ArrayList<Location>();
		for(float i = 0F; i < 360F;){
			locs.add(new Location(center.getWorld(), center.getX() + Math.cos((double)i) / radius, center.getY(), center.getZ() + Math.sin((double)i)/radius));
			i = i + distanceBetweenParticles;
		}
		return locs;
	}
	
	public static List<Location> getSpiral(Location center, Float degrees, double centerRadius, float radius, float distanceBetweenParticles){
		List<Location> locs = new ArrayList<Location>();
		for(float i = 0F; i < degrees;){
			locs.add(new Location(center.getWorld(), center.getX() + Math.sin((double)i) / radius, center.getY()+i/centerRadius, center.getZ() + Math.cos((double)i)/radius));
			i = i + distanceBetweenParticles;
		}
		return locs;
	}
	
	public static enum BasicGravity {
		potion(0.115), snowball(0.075);
		private double gravity = 0D;
		private BasicGravity(double g){
			this.gravity = g;
		}
		public double getGravity(){
			return this.gravity;
		}
	}
	
	public static Vector calculateVelocity(Location from, Location to, BasicGravity gravity, int heightGain){
		return calculateVelocity(from.toVector(), to.toVector(), gravity.getGravity(), heightGain);
	}
	
	public static Vector calculateVelocity(Location from, Location to, double gravity, int heightGain){
		return calculateVelocity(from.toVector(), to.toVector(), gravity, heightGain);
	}
	
	public static Vector calculateVelocity(Vector from, Vector to, BasicGravity gravity, int heightGain){
		return calculateVelocity(from, to, gravity.getGravity(), heightGain);
	}
	
	public static Vector calculateVelocity(Vector from, Vector to, double gravity, int heightGain){
		int endGain = to.getBlockY() - from.getBlockY();
		double horizDist = Math.sqrt(distanceSquared(from, to));
		double maxGain = heightGain > (endGain + heightGain) ? heightGain : (endGain + heightGain);
		double a = -horizDist * horizDist / (4 * maxGain);
		double b = horizDist;
		double c = -endGain;
		double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);
		double vy = Math.sqrt(maxGain * gravity);
		double vh = vy / slope;
		int dx = to.getBlockX() - from.getBlockX();
		int dz = to.getBlockZ() - from.getBlockZ();
		double mag = Math.sqrt(dx * dx + dz * dz);
		double dirx = dx / mag;
		double dirz = dz / mag;
		double vx = vh * dirx;
		double vz = vh * dirz;
		return new Vector(vx, vy, vz);
	}
	
	// support method // método de suporte //
	private static int getHighest(int x, int y, int z){
		if ((x >= y) && (x >= z)) return 1;
		if ((y >= x) && (y >= z)) return 2;
		return 3;
	}
	
	// support method // método de suporte //
	private static double distanceSquared(Vector from, Vector to){
		double dx = to.getBlockX() - from.getBlockX();
		double dz = to.getBlockZ() - from.getBlockZ();
		return dx * dx + dz * dz;
	}
	
}