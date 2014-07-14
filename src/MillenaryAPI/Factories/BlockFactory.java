package MillenaryAPI.Factories;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.TileEntityBeacon;
import net.minecraft.server.v1_7_R3.TileEntityChest;
import net.minecraft.server.v1_7_R3.TileEntitySkull;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R3.block.CraftBeacon;
import org.bukkit.craftbukkit.v1_7_R3.block.CraftChest;
import org.bukkit.craftbukkit.v1_7_R3.block.CraftSkull;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MillenaryAPI.MillenaryAPI;
import MillenaryAPI.Enums.StatusEffect;

public class BlockFactory {
	
	private Plugin api;
	public BlockFactory(MillenaryAPI api){
		this.api = api;
	}
	
	public void setChestName(Location chestlocation, String chestname){
		if(!chestlocation.getBlock().getType().equals(Material.CHEST)) return;
		try{
			Field f = CraftChest.class.getDeclaredField("chest");
			f.setAccessible(true);
			TileEntityChest c = ((TileEntityChest) f.get((CraftChest)chestlocation.getBlock().getState()));
			c.a(chestname);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	public void setChestName(Chest chest, String chestname){
		if(chest == null) return;
		try{
			Field f = CraftChest.class.getDeclaredField("chest");
			f.setAccessible(true);
			TileEntityChest c = ((TileEntityChest) f.get((CraftChest)chest));
			c.a(chestname);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	public void setBeaconEffects(Location beaconlocation, StatusEffect effect1, StatusEffect effect2){
		if(!beaconlocation.getBlock().getType().equals(Material.BEACON)) return;
		try{
			Field f = CraftChest.class.getDeclaredField("beacon");
			f.setAccessible(true);
			TileEntityBeacon b = ((TileEntityBeacon) f.get((CraftBeacon)beaconlocation.getBlock().getState()));
			NBTTagCompound nbt = new NBTTagCompound();
			b.b(nbt);
			nbt.setInt("Primary", effect1.getId());
			nbt.setInt("Secondary", effect2.getId());
			b.a(nbt);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	public void setBeaconEffects(Location beaconlocation, StatusEffect effect1){
		if(!beaconlocation.getBlock().getType().equals(Material.BEACON)) return;
		try{
			Field f = CraftChest.class.getDeclaredField("beacon");
			f.setAccessible(true);
			TileEntityBeacon b = ((TileEntityBeacon) f.get((CraftBeacon)beaconlocation.getBlock().getState()));
			NBTTagCompound nbt = new NBTTagCompound();
			b.b(nbt);
			nbt.setInt("Primary", effect1.getId());
			nbt.setInt("Secondary", 0);
			b.a(nbt);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	public void setBeaconEffects(Beacon beacon, StatusEffect effect1, StatusEffect effect2){
		if(beacon == null) return;
		try{
			Field f = CraftChest.class.getDeclaredField("beacon");
			f.setAccessible(true);
			TileEntityBeacon b = ((TileEntityBeacon) f.get((CraftBeacon)beacon));
			NBTTagCompound nbt = new NBTTagCompound();
			b.b(nbt);
			nbt.setInt("Primary", effect1.getId());
			nbt.setInt("Secondary", effect2.getId());
			b.a(nbt);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	public void setBeaconEffects(Beacon beacon, StatusEffect effect1){
		if(beacon == null) return;
		try{
			Field f = CraftChest.class.getDeclaredField("beacon");
			f.setAccessible(true);
			TileEntityBeacon b = ((TileEntityBeacon) f.get((CraftBeacon)beacon));
			NBTTagCompound nbt = new NBTTagCompound();
			b.b(nbt);
			nbt.setInt("Primary", effect1.getId());
			nbt.setInt("Secondary", 0);
			b.a(nbt);
			f.setAccessible(false);
		}catch (Exception e){
			return;
		}
	}
	
	/**
	 * UNTESTED
	 */
	@SuppressWarnings("deprecation")
	public void changeSkull(Block head, String playername){
		if(!head.getType().equals(Material.SKULL)) return;
		CraftSkull craftskull = (CraftSkull)head.getState();
		try{
			Field f = CraftSkull.class.getDeclaredField("skull");
			f.setAccessible(true);
			TileEntitySkull skull =((TileEntitySkull) f.get(craftskull));
			if(skull.getSkullType() != 3){
				this.api.getLogger().info("There is no way to set a skin to this skull!"); // Não há como definir uma skin para esta cabeça!
				return;
			}
			skull.setGameProfile(fixProfile(new GameProfile(Bukkit.getOfflinePlayer(playername).getUniqueId(), playername), playername));
			craftskull.update(true, true);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * UNTESTED
	 */
	@SuppressWarnings({ "deprecation", "resource" })
	private GameProfile fixProfile(GameProfile profile, String playername){
		try{
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Bukkit.getOfflinePlayer(playername).getUniqueId().toString().replace("-", ""));
			URLConnection connection = url.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A");
			String json = scanner.next();
			JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");
			for (int i = 0; i < properties.size(); i++) {
				JSONObject property = (JSONObject) properties.get(i);
				String name = (String) property.get("name");
				String value = (String) property.get("value");
				String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
				if (signature != null) {
					profile.getProperties().put(name, new Property(name, value, signature));
				} else {
					profile.getProperties().put(name, new Property(value, name));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}