package Millenary.Factories;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.ChunkCoordinates;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityBat;
import net.minecraft.server.v1_7_R3.EntityBlaze;
import net.minecraft.server.v1_7_R3.EntityCaveSpider;
import net.minecraft.server.v1_7_R3.EntityChicken;
import net.minecraft.server.v1_7_R3.EntityCow;
import net.minecraft.server.v1_7_R3.EntityCreeper;
import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import net.minecraft.server.v1_7_R3.EntityEnderman;
import net.minecraft.server.v1_7_R3.EntityFallingBlock;
import net.minecraft.server.v1_7_R3.EntityGhast;
import net.minecraft.server.v1_7_R3.EntityHorse;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityIronGolem;
import net.minecraft.server.v1_7_R3.EntityMagmaCube;
import net.minecraft.server.v1_7_R3.EntityMushroomCow;
import net.minecraft.server.v1_7_R3.EntityOcelot;
import net.minecraft.server.v1_7_R3.EntityPig;
import net.minecraft.server.v1_7_R3.EntityPigZombie;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EntitySheep;
import net.minecraft.server.v1_7_R3.EntitySilverfish;
import net.minecraft.server.v1_7_R3.EntitySkeleton;
import net.minecraft.server.v1_7_R3.EntitySlime;
import net.minecraft.server.v1_7_R3.EntitySnowman;
import net.minecraft.server.v1_7_R3.EntitySpider;
import net.minecraft.server.v1_7_R3.EntitySquid;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.EntityWitch;
import net.minecraft.server.v1_7_R3.EntityWither;
import net.minecraft.server.v1_7_R3.EntityWolf;
import net.minecraft.server.v1_7_R3.EntityZombie;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.World;
import net.minecraft.util.com.mojang.authlib.*;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Millenary.MillenaryAPI;
import Millenary.Factories.PacketFactory.PacketWrapper;

/**
 * @author ArthurMaker
 * NOTE: EnderDragon - YAW issue
 */
public class DisguiseFactory{
	
	private Plugin api;
	private static Map<UUID, String> disguises = Collections.synchronizedMap(new HashMap<UUID, String>());
	private Scoreboard board;
	private Team team;
	private List<UUID> ghosts = new ArrayList<UUID>();
	
	public static enum DisguiseType {
		BAT, BLAZE, CAVE_SPIDER, CHICKEN, COW, CREEPER, ENDER_DRAGON, ENDERMAN, GHAST, HORSE, IRON_GOLEM, MAGMA_CUBE, MOOSHROOM, OCELOT, PIG, SHEEP, SKELETON, SLIME, SILVERFISH,
		SPIDER, SNOW_GOLEM, SQUID, VILLAGER, WITCH, WITHER, WOLF, ZOMBIE, ZOMBIE_PIGMAN, WITHER_SKELETON;
	}
	
	public DisguiseFactory (MillenaryAPI plugin){
		this.api = plugin;
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.team = this.board.registerNewTeam("ghost_team");
		this.team.setCanSeeFriendlyInvisibles(true);
	}
	
	public void toggleGhost(Player p){
		if(this.ghosts.contains(p.getUniqueId())){
			this.ghosts.remove(p.getUniqueId());
			if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) p.removePotionEffect(PotionEffectType.INVISIBILITY);
			if(this.team.hasPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()))) this.team.removePlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
		}else{
			this.ghosts.add(p.getUniqueId());
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false));
			if(!this.team.hasPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()))) this.team.addPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
		}
	}
	
	public void changeSkin(final Player p, final String playername){
		disguises.put(p.getUniqueId(), playername);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PacketWrapper packet = new PacketWrapper(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)p).getHandle()));
					Field gameProfileField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
					gameProfileField.setAccessible(true);
					GameProfile profile = new GameProfile(p.getUniqueId(), p.getName());
					fixSkin(profile, playername);
					gameProfileField.set(packet, profile);
					packet.broadcast(p.getWorld());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.api);
	}
	
	/**
	 * You can use it to disguise NPCs as well! :D
	 */
	public void changeSkin(final EntityPlayer p, final String playername){
		disguises.put(p.getUniqueID(), playername);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PacketWrapper packet = new PacketWrapper(new PacketPlayOutNamedEntitySpawn(p));
					Field gameProfileField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
					gameProfileField.setAccessible(true);
					GameProfile profile = new GameProfile(p.getUniqueID(), p.getName());
					fixSkin(profile, playername);
					gameProfileField.set(packet, profile);
					packet.broadcast(Bukkit.getWorld(p.world.worldData.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.api);
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	private void fixSkin(GameProfile profile, String skinOwner) {
		try{
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Bukkit.getOfflinePlayer(skinOwner).getUniqueId().toString().replace("-", "") + "?unsigned=false");
			URLConnection connection = url.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A");
			String json = scanner.next();
			JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");
			for (int i = 0; i < properties.size(); i++) {
				JSONObject property = (JSONObject) properties.get(i);
				String n = (String) property.get("name");
				String v = (String) property.get("value");
				String s = property.containsKey("signature") ? (String) property.get("signature") : null;
				if (s != null) {
					profile.getProperties().put(n, new Property(n, v, s));
				} else {
					profile.getProperties().put(n, new Property(v, n));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void disguiseAsMob(Player p, DisguiseType disguisetype, boolean withName){
		if(disguisetype == null) return;
		if(disguises.containsKey(p.getUniqueId())) disguises.remove(p.getUniqueId());
		disguises.put(p.getUniqueId(), disguisetype.toString());
		EntityInsentient e = getPacketEntity(((CraftPlayer)p).getHandle().world, disguisetype);
		e.locX = p.getLocation().getX();
		e.locY = p.getLocation().getY();
		e.locZ = p.getLocation().getZ();
		e.d(((CraftPlayer)p).getHandle().getId());
		e.pitch = p.getLocation().getPitch();
		e.yaw = p.getEyeLocation().getYaw();
		if(withName){
			e.setCustomName(ChatColor.YELLOW + p.getName());
			e.setCustomNameVisible(true);
		}
		sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)p).getHandle().getId()), p, p.getWorld().getPlayers());
		sendPacket(new PacketPlayOutSpawnEntityLiving(e), p, p.getWorld().getPlayers());
	}
	
	@SuppressWarnings("deprecation")
	public void disguiseAsBlock(Player p, Material blocktype, int data){
		Entity e = new EntityFallingBlock(((CraftPlayer)p).getHandle().world, 0D, 0D, 0D, net.minecraft.server.v1_7_R3.Block.e(blocktype.getId()), data);
		e.locX = p.getLocation().getX();
		e.locY = p.getLocation().getY();
		e.locZ = p.getLocation().getZ();
		e.d(((CraftPlayer)p).getHandle().getId());
		sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)p).getHandle().getId()), p, p.getWorld().getPlayers());
		sendPacket(new PacketPlayOutSpawnEntity(e, 70), p, p.getWorld().getPlayers());
	}
	
	public void disguiseAsPlayer(Player p, String playername){
		if(disguises.containsKey(p.getUniqueId())) disguises.remove(p.getUniqueId());
		disguises.put(p.getUniqueId(), playername);
		GameProfile profile = new GameProfile(UUID.fromString("disguisedplayer-" + UUID.randomUUID().toString()), playername);
		this.fixSkin(profile, playername);
		EntityHuman e = new EntityHuman(((CraftPlayer)p).getHandle().world, profile){
			@Override
			public boolean a(int arg0, String arg1) {return false;}
			@Override
			public ChunkCoordinates getChunkCoordinates() {return null;}
			@Override
			public void sendMessage(IChatBaseComponent arg0) {}
		};
		e.getBukkitEntity().setItemInHand(p.getItemInHand());
		e.getBukkitEntity().getInventory().setHelmet(p.getInventory().getHelmet());
		e.getBukkitEntity().getInventory().setChestplate(p.getInventory().getChestplate());
		e.getBukkitEntity().getInventory().setLeggings(p.getInventory().getLeggings());
		e.getBukkitEntity().getInventory().setBoots(p.getInventory().getBoots());
		e.locX = p.getLocation().getX();
		e.locY = p.getLocation().getY();
		e.locZ = p.getLocation().getZ();
		e.d(((CraftPlayer)p).getHandle().getId());
		e.pitch = p.getLocation().getPitch();
		e.yaw = p.getEyeLocation().getYaw();
		sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)p).getHandle().getId()), p, p.getWorld().getPlayers());
		sendPacket(new PacketPlayOutNamedEntitySpawn(e), p, p.getWorld().getPlayers());
	}
	
	public void undisguisePlayer(Player p){
		if(!disguises.containsKey(p.getUniqueId())) return;
		disguises.remove(p.getUniqueId());
		sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)p).getHandle().getId()), p, api.getServer().getOnlinePlayers());
		sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)p).getHandle()), p, api.getServer().getOnlinePlayers());
	}
	
	public boolean isDisguised(Player p){
		if(disguises.containsKey(p.getUniqueId())) return true;
		return false;
	}
	
	public boolean isDisguised(int id){
		for(UUID i : disguises.keySet()){
			if(Bukkit.getPlayer(i).getEntityId() == id) return true;
		}
		return false;
	}
	
	public String getDisguise(Player p){
		if(disguises.containsKey(p.getUniqueId())) return disguises.get(p.getUniqueId());
		return "null";
	}
	
	public DisguiseType getDisguiseType(String s){
		for(DisguiseType t : DisguiseType.values()){
			if(t.toString().equalsIgnoreCase(s)) return t;
		}
		return null;
	}
	
	protected static void sendPacket(Packet packet, Player except, Collection<Player> players){
		for(Player p : players) if(!p.equals(except)) MillenaryAPI.getPacketFactory().sendPacket(packet, p);
	}
	
	protected static void sendPacket(Packet packet, Player except, Player... players){
		for(Player p : players) if(!p.equals(except)) MillenaryAPI.getPacketFactory().sendPacket(packet, p);
	}
	
	@SuppressWarnings("deprecation")
	protected EntityInsentient getPacketEntity(World w, DisguiseType t){
		EntityInsentient e;
		switch(t){
		case BAT: e = new EntityBat(w); break;
		case BLAZE: e = new EntityBlaze(w); break;
		case CAVE_SPIDER: e = new EntityCaveSpider(w); break;
		case CHICKEN: e = new EntityChicken(w); break;
		case COW: e = new EntityCow(w); break;
		case CREEPER: e = new EntityCreeper(w); break;
		case ENDER_DRAGON: e = new EntityEnderDragon(w); break;
		case ENDERMAN: e = new EntityEnderman(w); break;
		case GHAST: e = new EntityGhast(w); break;
		case HORSE: e = new EntityHorse(w); break;
		case IRON_GOLEM: e = new EntityIronGolem(w); break;
		case MAGMA_CUBE: e = new EntityMagmaCube(w); break;
		case MOOSHROOM: e = new EntityMushroomCow(w); break;
		case OCELOT: e = new EntityOcelot(w); break;
		case PIG: e = new EntityPig(w); break;
		case SHEEP: e = new EntitySheep(w); break;
		case SKELETON: e = new EntitySkeleton(w); ((EntitySkeleton)e).setSkeletonType(SkeletonType.NORMAL.getId()); break;
		case SLIME: e = new EntitySlime(w); break;
		case SILVERFISH: e = new EntitySilverfish(w); break;
		case SPIDER: e = new EntitySpider(w); break;
		case SNOW_GOLEM: e = new EntitySnowman(w); break;
		case SQUID: e = new EntitySquid(w); break;
		case VILLAGER: e = new EntityVillager(w); break;
		case WITCH: e = new EntityWitch(w); break;
		case WITHER: e = new EntityWither(w); break;
		case WOLF: e = new EntityWolf(w); break;
		case ZOMBIE_PIGMAN: e = new EntityPigZombie(w); break;
		case WITHER_SKELETON: e = new EntitySkeleton(w); ((EntitySkeleton)e).setSkeletonType(SkeletonType.WITHER.getId()); break;
		default: e = new EntityZombie(w); break;
		}
		return e;
	}
	
}