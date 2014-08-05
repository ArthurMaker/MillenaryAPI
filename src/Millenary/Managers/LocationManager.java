package Millenary.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationManager {
	
	public static String locationToString(Location location){
		return (location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + (double)location.getYaw() + ", " + (double)location.getPitch());
	}
	
	public static Location stringToLocation(String location){
		String[] string = location.split(", ");
		try{
			return new Location(Bukkit.getWorld(string[0]), Double.parseDouble(string[1]), Double.parseDouble(string[2]), Double.parseDouble(string[3]), Float.parseFloat(string[4]), Float.parseFloat(string[5]));
		}catch (Exception e){
			return null;
		}
	}
	
}