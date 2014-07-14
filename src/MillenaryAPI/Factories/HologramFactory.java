package MillenaryAPI.Factories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityHorse;
import net.minecraft.server.v1_7_R3.EntityWitherSkull;
import net.minecraft.server.v1_7_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MillenaryAPI.MillenaryAPI;
import MillenaryAPI.Factories.PacketFactory.PacketWrapper;
import MillenaryAPI.Managers.LocationManager;
import MillenaryAPI.Managers.YamlManager;

public class HologramFactory implements Listener {
	
	@SuppressWarnings("unused")
	private MillenaryAPI api;
	private List<Hologram> holograms = new ArrayList<Hologram>();
	public HologramFactory (MillenaryAPI plugin){
		this.api = plugin;
	}
	
	public Hologram createHologram(Location l, String... s){
		Hologram h = new Hologram(l, s);
		this.holograms.add(h);
		return h;
	}
	
	public boolean removeHologram(Hologram h){
		if(this.holograms.contains(h)){
			h.destroy();
			this.holograms.remove(h);
			return true;
		}
		return false;
	}
	
	/*public void load(){
		if(this.all_loaded) return;
		YamlConfiguration config = YamlManager.getConfig("plugins/MillenaryAPI/holograms.yml");
		List<String> list = config.getStringList("holograms");
		if(list == null) return;
		for(String s : list){
			try {
				JSONObject h = (JSONObject) new JSONParser().parse(s);
				String location = (String) h.get("location");
				List<String> lines = new ArrayList<String>();
				JSONArray a = (JSONArray) h.get("lines");
				for(Object o : a) lines.add((String)o);
				Hologram hologram = new Hologram(LocationManager.stringToLocation(location), lines);
				hologram.setJSON(s);
				hologram.setPublic(true);
				this.holograms.put(this.LAST_ID, hologram);
				this.LAST_ID++;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		for(Hologram h : this.holograms.values()) h.broadcast();
	}*/
	
	@SuppressWarnings("unchecked")
	public void save(Hologram h){
		String l = LocationManager.locationToString(h.L());
		JSONArray a = new JSONArray();
		for(String s : h.S()) a.add(s.replaceAll("§", "&"));
		JSONObject o = new JSONObject();
		o.put("location", l);
		o.put("lines", a);
		YamlConfiguration config = YamlManager.getConfig("plugins/MillenaryAPI/holograms.yml");
		List<String> list = config.getStringList("holograms");
		if(list == null) list = new ArrayList<String>();
		String json = o.toJSONString();
		list.add(json);
		config.set("holograms", list);
		try {
			config.save(new File("plugins/MillenaryAPI/holograms.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class Hologram {
		private double LINE_BREAK = 0.23;
		private Location l;
		private List<String> lines = new ArrayList<String>();
		private List<Integer> ids = new ArrayList<Integer>();
		public List<UUID> viewers = new ArrayList<UUID>();
		public Hologram (Location location, String... text){
			this.l = location;
			for(String s : text) this.lines.add(ChatColor.translateAlternateColorCodes('&', s));
			//this.lines.addAll(Arrays.asList(text));
		}
		public Hologram (Location location, List<String> text){
			this.l = location;
			for(String s : text) this.lines.add(ChatColor.translateAlternateColorCodes('&', s));
			//this.lines.addAll(Arrays.asList(text));
		}
		public void show(Player... players){
			for(Player p : players){
				if(!this.l.getWorld().equals(p.getWorld())) return;
				Location hlocation = this.l.clone().add(0, (this.lines.size() / 2) * this.LINE_BREAK, 0);
				for(int i = 0; i < this.lines.size(); i++){
					this.ids.addAll(this.showLine(hlocation.clone(), this.lines.get(i), p));
					hlocation.subtract(0, this.LINE_BREAK, 0);
				}
				if(!this.viewers.contains(p.getUniqueId()))	this.viewers.add(p.getUniqueId());
			}
		}
		public void broadcast(){
			for(Player p : this.l.getWorld().getPlayers()){
				Location hlocation = this.l.clone().add(0, (this.lines.size() / 2) * this.LINE_BREAK, 0);
				for(int i = 0; i < this.lines.size(); i++){
					this.ids.addAll(this.showLine(hlocation.clone(), this.lines.get(i), p));
					hlocation.subtract(0, this.LINE_BREAK, 0);
				}
				if(!this.viewers.contains(p.getUniqueId()))	this.viewers.add(p.getUniqueId());
			}
		}
		public void destroy(){
			int[] id_list = new int[this.ids.size()];
			for(int i = 0; i < id_list.length; i++) id_list[i] = this.ids.get(i);
			PacketWrapper packet = new PacketWrapper(new PacketPlayOutEntityDestroy(id_list));
			this.viewers = new ArrayList<UUID>();
			packet.broadcast(this.l.getWorld());
		}
		public void destroy(Player... player){
			for(Player p : player){
				if(!this.l.getWorld().equals(p.getWorld())) return;
				int[] id_list = new int[this.ids.size()];
				for(int i = 0; i < id_list.length; i++) id_list[i] = this.ids.get(i);
				PacketWrapper packet = new PacketWrapper(new PacketPlayOutEntityDestroy(id_list));
				if(this.viewers.contains(p.getUniqueId())) this.viewers.remove(p.getUniqueId());
				packet.send(p);
			}
		}
		private List<Integer> showLine(Location loc, String s, Player p){
			if(!this.l.getWorld().equals(p.getWorld())) return new ArrayList<Integer>();
			WorldServer w = ((CraftWorld)loc.getWorld()).getHandle();
			EntityWitherSkull skull = new EntityWitherSkull(w);
			skull.setLocation(loc.getX(), (loc.getY()+55.25), loc.getZ(), 0F, 0F);
			((CraftWorld)loc.getWorld()).getHandle().addEntity(skull);
			EntityHorse horse = new EntityHorse(w);
			horse.setLocation(loc.getX(), (loc.getY()+55.25), loc.getZ(), 0F, 0F);
			horse.setAge(-1700000);
			horse.setCustomName(s);
			horse.setCustomNameVisible(true);
			PacketWrapper horse_packet = new PacketWrapper(new PacketPlayOutSpawnEntityLiving(horse));
			horse_packet.send(p); //Envio de packet - 1
			PacketWrapper attachment_packet = new PacketWrapper(new PacketPlayOutAttachEntity(0, horse, skull));
			attachment_packet.send(p); //Envio de packet - 2
			return Arrays.asList(skull.getId(), horse.getId());
		}
		public List<String> S(){
			return this.lines;
		}
		public Location L(){
			return this.l;
		}
		public World getWorld(){
			return this.l.getWorld();
		}
		public boolean canSee(Player player){
			return this.viewers.contains(player.getUniqueId());
		}
	}
	
}