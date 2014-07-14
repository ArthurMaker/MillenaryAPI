package MillenaryAPI.Utils;

import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PropertyManager;
import net.minecraft.server.v1_7_R3.ServerConnection;

public class ServerUtils {
	
	public static void setConfigOption(String option, Object value) {
		PropertyManager propertyManager = MinecraftServer.getServer().getPropertyManager();
		propertyManager.a(option, value);
		propertyManager.savePropertiesFile();
	}
	
	public static String[] getWhitelistedPlayers() {
		return MinecraftServer.getServer().getPlayerList().getWhitelisted();
	}
	
	public static boolean isWhitelisted() {
		return MinecraftServer.getServer().getPlayerList().getHasWhitelist();
	}
	
	public static void enableWhitelist(boolean enabled) {
		MinecraftServer.getServer().getPlayerList().setHasWhitelist(enabled);
	}
	
	public static String getServerVersion() {
		return MinecraftServer.getServer().getVersion();
	}
	
	public static MinecraftServer getMinecraftServer() {
		return MinecraftServer.getServer();
	}
	
	public static ServerConnection getServerConnection(){
		return MinecraftServer.getServer().ai();
	}
	
}