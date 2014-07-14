package MillenaryAPI;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import MillenaryAPI.Factories.BlockFactory;
import MillenaryAPI.Factories.BossBarFactory;
import MillenaryAPI.Factories.DisguiseFactory;
import MillenaryAPI.Factories.HologramFactory;
import MillenaryAPI.Factories.InventoryFactory;
import MillenaryAPI.Factories.MobFactory;
import MillenaryAPI.Factories.Trader.TraderVillager;
import MillenaryAPI.Factories.PacketFactory.PacketFactory;
import MillenaryAPI.Managers.BungeeCordManager;
import MillenaryAPI.MySQL.MakerSQL;

/**
 * WARNING: It is not recommed to use "/reload" because of the ProtocolManager! If you do it, you will get many errors.
 * For this and other reasons, like a insane consuming of RAM memory (which is extremely necessary for a fluid gameplay on a minecraft server),
 * it is healthy to RESTART your server, instead of that.
 * I did a RESTART METHOD that is built-in this class. You may can use it, but it is not tested. Let me know if it worked for you! :D
 */
public class MillenaryAPI extends JavaPlugin {
	
	private static MillenaryAPI instance;
	private static MakerSQL mcdatabase;
	private static BossBarFactory bossbarfactory;
	private static BlockFactory blockfactory;
	private static InventoryFactory inventoryfactory;
	private static MobFactory mobfactory;
	private static PacketFactory packetfactory;
	private static DisguiseFactory disguisefactory;
	private static BungeeCordManager bungeecord;
	private static HologramFactory hologramfactory;
	//private static HashMap<UUID, String> players = new HashMap<UUID, String>();
	
	public static Plugin getInstance(){
		return instance;
	}
	
	public File getFolder(){
		return this.getFolder();
	}
	
	//protected static void registerPlayer(Player player){
	//	if(!players.containsKey(player.getUniqueId())) players.put(player.getUniqueId(), player.getName());
	//}
	
	public static MakerSQL getServerDatabase(){
		return mcdatabase;
	}
	
	public static boolean restartServer(){
		System.out.println("Asking for restart..."); // "Requisitando restart..."
		shutdownServer();
		ProcessBuilder pb = new ProcessBuilder();
		String jarname = Bukkit.class.getResource("").getFile();
		jarname = jarname.substring(0, jarname.indexOf(".jar"));
		jarname = new File(jarname).getName()+".jar";
		List<String> a = ManagementFactory.getRuntimeMXBean().getInputArguments();                        
		List<String> e = new ArrayList<String>();
		e.add("java"); e.addAll(a); e.add("-jar"); e.add(jarname);
		pb.command(e);
		try {
			pb.start();
		} catch (IOException exception) {
			exception.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean restartServer(String... custom_arguments){
		System.out.println("Asking for restart..."); // "Requisitando restart..."
		shutdownServer();
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(custom_arguments);
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void shutdownServer(){
		System.out.println("Turning off..."); // "Desligando servidor..."
		Bukkit.getServer().savePlayers();
		for(World w : Bukkit.getWorlds()) w.save();
		Bukkit.getServer().shutdown();
	}
	
	public static void printLog(String s){
		instance.getLogger().info(s);
	}
	
	public static BossBarFactory getBossBarFactory(){
		return bossbarfactory;
	}
	
	public static BlockFactory getBlockFactory(){
		return blockfactory;
	}
	
	public static MobFactory getMobFactory(){
		return mobfactory;
	}
	
	public static InventoryFactory getInventoryFactory(){
		return inventoryfactory;
	}
	
	public static PacketFactory getPacketFactory(){
		return packetfactory;
	}
	
	public static DisguiseFactory getDisguiseFactory(){
		return disguisefactory;
	}
	
	public static BungeeCordManager getBungeeCordManager(){
		return bungeecord;
	}
	
	public static HologramFactory getHologramFactory(){
		return hologramfactory;
	}
	
	@Override
	public void onEnable(){
		instance = this;
		String ip = "192.168.0.1"; // Coloque aqui o IP do servidor MySQL // Insert here the IP that hosts a MySQL server //
		String port = "3306"; // Padrão: 3306 // Default: 3306 //
		String database = "random_database"; // Coloque aqui o nome do database que queira conectar-se // Insert the name of the database that you want to connect to //
		String user_name = "random_user"; // Coloque o nome do usuário do login desejado // Insert here the user that you want access the MySQL database //
		String user_password = "0123456789"; // Coloque a senha do usuário escolhido (precisa ser a senha do usuário que você colocou em "user_name"!) //
											 // Insert the password of the user, that you chose before to use in "user_name" //
		mcdatabase = new MakerSQL(this, ip, port, user_name, user_password, database);
		registerListeners();
		registerOtherStuff();
		initializeStuff();
	}
	
	@Override
	public void onDisable(){
		packetfactory.getProtocolManager().close();
		mcdatabase.closeConnection();
	}
	
	@SuppressWarnings("unchecked")
	public static void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass){
		try{
			List<Map<?,?>> dataMaps = new ArrayList<Map<?,?>>();
			for(Field f : EntityTypes.class.getDeclaredFields()){
				if(f.getType().getSimpleName().equals(Map.class.getSimpleName())){
					f.setAccessible(true);
					dataMaps.add((Map<?,?>)f.get(null));
				}
			}
			((Map<Class<? extends EntityInsentient>, String>)dataMaps.get(1)).put(customClass, name);
			((Map<Class<? extends EntityInsentient>, Integer>)dataMaps.get(3)).put(customClass, id);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void initializeStuff(){
		bossbarfactory = new BossBarFactory(this);
		blockfactory = new BlockFactory(this);
		inventoryfactory = new InventoryFactory(this);
		mobfactory = new MobFactory(this);
		packetfactory = new PacketFactory(this);
		disguisefactory = new DisguiseFactory(this);
		bungeecord = new BungeeCordManager(this);
		hologramfactory = new HologramFactory(this);
	}
	
	private void registerListeners(){
		this.getServer().getPluginManager().registerEvents(new MainListener(this), this);
	}
	
	private void registerOtherStuff(){
		registerEntity("Villager", 120, TraderVillager.class);
	}
	
}