package Millenary.Factories.PacketFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityFallingBlock;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumDifficulty;
import net.minecraft.server.v1_7_R3.EnumGamemode;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_7_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R3.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_7_R3.WorldType;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import Millenary.MillenaryAPI;
import Millenary.Events.PacketReceiveEvent;
import Millenary.Events.PacketSendEvent;

/**
 * @author Arthur
 * Thanks to bigteddy98 and Comphenix!
 */
public class PacketFactory {
	
	private Plugin api;
	private ProtocolManager manager;
	private Map<PacketListener, List<Method>> packetListeners = new HashMap<PacketListener, List<Method>>();
	
	public PacketFactory(MillenaryAPI plugin){
		this.api = plugin;
		this.manager = new ProtocolManager(plugin);
	}
	
	public ProtocolManager getProtocolManager(){
		return this.manager;
	}
	
	public static enum Animation {
		SWING_ARM(0), DAMAGE(1), LEAVE_BED(2), EAT_FOOD(3), CRITICAL_EFFECT(4), MAGICAL_CRIT_EFFECT(5), UNKNOWN(102), CROUCH(104), UNCROUCH(105);
		private int i;
		private Animation(int i){
			this.i = i;
		}
		public int getId(){
			return this.i;
		}
	}
	
	public void playAnimation(Player p, Animation a){
		sendPacket(new PacketPlayOutAnimation(((CraftPlayer)p).getHandle(), a.getId()), p.getWorld().getPlayers());
	}
	
	public static enum SkyType {
		NORMAL(0), NETHER(-1), THE_END(1);
		private int i;
		private SkyType (int i){
			this.i = i;
		}
		public int getId(){
			return this.i;
		}
	}
	
	public void changePlayerSky(final PlayerRespawnEvent event, final SkyType s){
		new BukkitRunnable() {
			@Override
			public void run() {
				sendPacket(new PacketPlayOutRespawn(s.getId(), getDifficulty(event.getPlayer().getWorld().getDifficulty()), WorldType.NORMAL, getGameMode(event.getPlayer().getGameMode())), event.getPlayer());
			}
		}.runTaskLater(this.api, 1L);
	}
	
	public void changePlayerSky(PlayerJoinEvent event, SkyType s){
		sendPacket(new PacketPlayOutRespawn(s.getId(), getDifficulty(event.getPlayer().getWorld().getDifficulty()), WorldType.NORMAL, getGameMode(event.getPlayer().getGameMode())), event.getPlayer());
    }
	
	public void unsafelyChangePlayerSky(Player player, SkyType s){
		sendPacket(new PacketPlayOutRespawn(s.getId(), getDifficulty(player.getWorld().getDifficulty()), WorldType.NORMAL, getGameMode(player.getGameMode())), player);
    }
	
	public void changePlayerSky(PlayerChangedWorldEvent event, SkyType s){
		sendPacket(new PacketPlayOutRespawn(s.getId(), getDifficulty(event.getPlayer().getWorld().getDifficulty()), WorldType.NORMAL, getGameMode(event.getPlayer().getGameMode())), event.getPlayer());
    }
	
	protected EnumDifficulty getDifficulty(Difficulty diff) {
		for (EnumDifficulty dif : EnumDifficulty.values()) {
			if (dif.toString().equalsIgnoreCase(diff.toString())) {
				return dif;
			}
		}
		return null;
	}
	
	protected EnumGamemode getGameMode(GameMode gamemode) {
		for (EnumGamemode dif : EnumGamemode.values()) {
			if (dif.toString().equalsIgnoreCase(gamemode.toString())) {
				return dif;
			}
		}
		return null;
	}
	
	public static enum EntityStatus {
		ENTITY_HURT(2), ENTITY_DEATH(3), IRON_GOLEM_ARMS(4), WOLF_TAMING_FAILED(6), WOLF_TAMING_SUCCESS(7), WOLF_SHAKING_WATER(8),
		SHEEP_EAT_GRASS(10), IRON_GOLEM_ROSE(11), VILLAGER_HEARTS(12), VILLAGER_ANGRY(13), VILLAGER_HAPPY(14), WITCH_MAGIC(15),
		ZOMBIE_INFECT(16), FIREWORK_EXPLODING(17);
		//UNKNOWN_ID_0(0), UNKNOWN_ID_1(1), ACCEPT_EATING(9), UNKNOWN_ID_18(18);
		private byte i;
		private EntityStatus (int i){
			this.i = (byte) i;
		}
		public byte getId(){
			return this.i;
		}
	}
	
	public void playEntityStatus(Entity e, EntityStatus statustype, Player... players) {
		sendPacket(new PacketPlayOutEntityStatus(((CraftEntity)e).getHandle(), statustype.getId()), players);
	}
	
	public void playEntityStatus(Entity e, EntityStatus statustype, Collection<Player> players) {
		sendPacket(new PacketPlayOutEntityStatus(((CraftEntity)e).getHandle(), statustype.getId()), players);
	}
	
	@SuppressWarnings("deprecation")
	public void sendPotionEffectWithoutParticles(Player e, PotionEffect p){
		PacketPlayOutEntityEffect packet = new PacketPlayOutEntityEffect();
		setValueInPacket(packet, "a", e.getEntityId());
		setValueInPacket(packet, "b", (byte)p.getType().getId());
		setValueInPacket(packet, "c", (byte)p.getAmplifier());
		setValueInPacket(packet, "d", (short)p.getDuration());
		sendPacket(packet, e);
	}
	
	public void openSignEditorToPlayer(Player player, Location signlocation){
		sendPacket(new PacketPlayOutOpenSignEditor(signlocation.getBlockX(), signlocation.getBlockY(), signlocation.getBlockZ()), player);
	}
	
	public void sendSignUpdateToPlayer(Player player, Location signlocation, String... lines){
		sendPacket(new PacketPlayOutUpdateSign(signlocation.getBlockX(), signlocation.getBlockY(), signlocation.getBlockZ(), lines.clone()), player);
	}
	
	public void setPlayerAbilities(Player player, boolean isInvulnerable, boolean isFlying, boolean canFly, boolean canInstantlyBuild, float flySpeed, float walkSpeed){
		EntityPlayer p = ((CraftPlayer)player).getHandle();
		p.abilities.isInvulnerable = isInvulnerable;
		p.abilities.isFlying = isFlying;
		p.abilities.canFly = canFly;
		p.abilities.canInstantlyBuild = canInstantlyBuild;
		p.abilities.flySpeed = flySpeed;
		p.abilities.walkSpeed = walkSpeed;
	}
	
	public void playSound(Player player, String sound, float volume, float pitch){
		sendPacket(new PacketPlayOutNamedSoundEffect(sound, (int)player.getEyeLocation().getX(), (int)player.getEyeLocation().getY(), (int)player.getEyeLocation().getZ(), volume, pitch), player);
	}
	
	public void playSound(Location location, String sound, float volume, float pitch){
		((CraftWorld)location.getWorld()).getHandle().makeSound((int)location.getX(), (int)location.getY(), (int)location.getZ(), sound, volume, pitch);
	}
	
	public void moveEntityToEntityLocation(LivingEntity e, LivingEntity target){
		if(!(e instanceof Creature)) return;
		EntityCreature entity = ((CraftCreature)e).getHandle();
		entity.pathEntity = entity.world.findPath(entity, ((CraftLivingEntity)target).getHandle(), 16.0F, true, false, false, false);
	}
	
	public static enum StateChange {
		INVALID_BED(0,0), END_RAIN(1,0), BEGIN_RAIN(2,0), CHANGE_GAMEMODE_SURVIVAL(3,0), CHANGE_GAMEMODE_CREATIVE(3,1), CHANGE_GAMEMODE_ADVENTURE(3,2),
		SHOW_CREDITS(4,0), DEMO_WELCOME_WINDOW(5,0), DEMO_MOVEMENT_MESSAGE(5,101), DEMO_JUMP_MESSAGE(5,102), DEMO_INVENTORY_MESSAGE(5,103),
		ARROW_PLAYER_HIT_SOUND(6,0), FADE_VALUE(7,1), FADE_TIME(8,20);
		private int i;
		private int v;
		private StateChange(int i, int v) {
			this.i = i;
			this.v = v;
		}
		public int getId(){
			return this.i;
		}
		public int getValue(){
			return this.v;
		}
	}
	
	public void playStateChange(StateChange s, Player p){
		sendPacket(new PacketPlayOutGameStateChange(s.getId(), s.getValue()), p);
	}
	
	public void setFallingBlockDamage(FallingBlock block, float mindamage, int maxdamage){
		EntityFallingBlock efb = ((CraftFallingSand)block).getHandle();
		try{
			Field f = EntityFallingBlock.class.getDeclaredField("hurtEntities");
			f.setAccessible(true);
			f.setBoolean(efb, true);
			f = EntityFallingBlock.class.getDeclaredField("fallHurtAmount");
			f.setAccessible(true);
			f.setFloat(efb, mindamage);
			f = EntityFallingBlock.class.getDeclaredField("fallHurtMax");
			f.setAccessible(true);
			f.setInt(efb, maxdamage);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public PacketWrapper createPacket(Packet packet){
		return new PacketWrapper(packet);
	}
	
	public void sendPacket(Packet packet, Player... players){
		for(Player p : players){
			try{
				PacketSendEvent e = new PacketSendEvent(new PacketWrapper(packet), p);
				Bukkit.getServer().getPluginManager().callEvent(e);
				if(!e.isCancelled()) this.manager.sendPacket(p, e.getPacket());
			}catch (Exception e){
				this.api.getLogger().info("It was not possible to send packets to " + p.getUniqueId().toString() + "."); // "Não foi possível enviar packets para " + p.getUniqueId().toString() + "."
			}
		}
	}
	
	public void sendPacket(Packet packet, Collection<Player> players){
		for(Player p : players){
			try{
				PacketSendEvent e = new PacketSendEvent(new PacketWrapper(packet), p);
				Bukkit.getServer().getPluginManager().callEvent(e);
				if(!e.isCancelled()) this.manager.sendPacket(p, e.getPacket());
			}catch (Exception e){
				this.api.getLogger().info("It was not possible to send packets to " + p.getUniqueId().toString() + "."); // "Não foi possível enviar packets para " + p.getUniqueId().toString() + "."
			}
		}
	}
	
	public Packet setValueInPacket(Packet packet, String fieldName, Object value) {
		try{
			Field f = packet.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(packet, value);
		}catch (Exception e){
			this.api.getLogger().info("It was not possible to set values in a packet."); // "Não foi possível definir valores em um packet."
		}
		return packet;
	}
	
	public Object getValueFromPacket(Packet packet, String fieldName) {
		Object o = null;
		try{
			Field f = packet.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			o = f.get(packet);
		}catch (Exception e){
			this.api.getLogger().info("It was not possible to return values from a packet."); // "Não foi possível retornar valores de um packet."
		}
		return o;
	}
	
	public DataWatcher getDataWatcher(){
		return new DataWatcher(null);
	}
	
	public DataWatcher getDataWatcher(Entity e){
		return ((CraftEntity)e).getHandle().getDataWatcher();
	}
	
	public void registerPacketListener(PacketListener listener) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			PacketHandler handler = method.getAnnotation(PacketHandler.class);
			if (handler != null) {
				try {
					methods.add(method);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.packetListeners.put(listener, methods);
	}
	
	protected void sendWrapperToServer(PacketWrapper packet, Player player) {
		try {
			for (Entry<PacketListener, List<Method>> l : this.packetListeners.entrySet()) {
				for (Method method : l.getValue()) {
					if (!method.getParameterTypes()[0].equals(PacketSendEvent.class)) continue;
					PacketHandler handler = method.getAnnotation(PacketHandler.class);
					if (handler.PacketType().equals(PacketType.ALL) || handler.PacketType().getName().equals(packet.getPacketName())) {
						method.setAccessible(true);
						try {
							method.invoke(l.getKey(), new PacketSendEvent(packet, player));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			this.api.getLogger().info("It was not possible to send a wrapper to " + player.getUniqueId() + "."); // "Não foi possível enviar um wrapper para " + player.getUniqueId() + "."
		}
	}

	protected void receiveWrapperFromServer(PacketWrapper packet, Player p) {
		for (Entry<PacketListener, List<Method>> listener : this.packetListeners.entrySet()) {
			for (Method method : listener.getValue()) {
				if (!method.getParameterTypes()[0].equals(PacketReceiveEvent.class)) continue;
				PacketHandler handler = method.getAnnotation(PacketHandler.class);
				if (handler.PacketType() == PacketType.ALL || handler.PacketType().getName().equals(packet.getPacketName())) {
					method.setAccessible(true);
					try {
						method.invoke(listener.getKey(), new PacketReceiveEvent(packet, p));
					} catch (Exception e) {
						this.api.getLogger().info("It was not possible to receive a wrapper from " + p.getUniqueId() + "."); //"Não foi possível receber um wrapper de " + p.getUniqueId() + "."
					}
				}
			}
		}
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PacketHandler {
		PacketType PacketType() default PacketType.ALL;
	}
	
}