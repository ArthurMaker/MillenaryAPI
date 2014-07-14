package MillenaryAPI.Factories.PacketFactory;

import java.util.Arrays;
import java.util.List;

import MillenaryAPI.Utils.ServerUtils;
import net.minecraft.server.v1_7_R3.ChatComponentText;
import net.minecraft.server.v1_7_R3.ServerPing;
import net.minecraft.server.v1_7_R3.ServerPingPlayerSample;
import net.minecraft.server.v1_7_R3.ServerPingServerData;
import net.minecraft.util.com.mojang.authlib.GameProfile;

/**
 * @author ArthurMaker
 */
public class Wrappers {
	
	//PacketWrapperStatusOutServerInfo
	public static class PacketWrapperStatusOutServerInfo extends PacketWrapper {
		private ServerPing ping;
		private String version_name;
		private int version_protocol;
		private int min_players = 0;
		private int max_players = 0;
		private boolean players_visible = true;
		public PacketWrapperStatusOutServerInfo(Object packet) {
			super(packet);
			try{
				this.ping = (ServerPing) this.getValue("b");
				this.version_name = this.ping.c().a();
				this.version_protocol = this.ping.c().b();
				this.min_players = ServerUtils.getMinecraftServer().C();
				this.max_players = ServerUtils.getMinecraftServer().D();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public void setMOTD(String s){
			this.ping.setMOTD(new ChatComponentText(s));
		}
		public String getMOTD(){
			return this.ping.a().c();
		}
		public void setOnlinePlayers(GameProfile[] p){
			this.ping.b().a(p);
		}
		public GameProfile[] getOnlinePlayers(){
			return this.ping.b().c();
		}
		public void resetOnlinePlayers(){
			this.ping.b().a(new GameProfile[0]);
		}
		public void setFavicon(String f){
			this.ping.setFavicon(f);
		}
		public String getFavicon(){
			return this.ping.d();
		}
		public void setVersionName(String s){
			this.version_name = s;
			this.ping.setServerInfo(new ServerPingServerData(this.version_name, this.version_protocol));
		}
		public String getVersionName(){
			return this.ping.c().a();
		}
		public void setVersionProtocol(int i){
			this.version_protocol = i;
			this.ping.setServerInfo(new ServerPingServerData(this.version_name, this.version_protocol));
		}
		public int getVersionProtocol(){
			return this.ping.c().b();
		}
		public void maintenanceMode(String message){
			this.version_name = message;
			this.version_protocol = -1;
			this.ping.setServerInfo(new ServerPingServerData(this.version_name, this.version_protocol));
		}
		public void setMinPlayers(int min){
			this.min_players = min;
			this.ping.setPlayerSample(new ServerPingPlayerSample(this.min_players, this.max_players));
		}
		public void setMaxPlayers(int max){
			this.max_players = max;
			this.ping.setPlayerSample(new ServerPingPlayerSample(this.min_players, this.max_players));
		}
		public void setPlayersVisible(boolean b){
			this.players_visible = b;
			if(b){
				this.ping.setPlayerSample(new ServerPingPlayerSample(this.min_players, this.max_players));
			}else{
				this.ping.setPlayerSample(null);
			}
		}
		public boolean isPlayersVisible(){
			return this.players_visible;
		}
	}
	
	//PacketWrapperLoginInEncryptionBegin
	public static class PacketWrapperLoginInEncryptionBegin extends PacketWrapper {
		private byte[] shared_secret = new byte[0];
		private byte[] token_response = new byte[0];
		public PacketWrapperLoginInEncryptionBegin(Object packet) {
			super(packet);
			try{
				this.shared_secret = (byte[]) this.getValue("a");
				this.token_response = (byte[]) this.getValue("b");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		public void setSharedSecret(byte[] b){
			this.shared_secret = b;
			this.setValue("a", this.shared_secret);
		}
		public byte[] getSharedSecret(){
			return this.shared_secret;
		}
		public void setVerifyTokenResponse(byte[] b){
			this.token_response = b;
			this.setValue("b", this.token_response);
		}
		public byte[] getVerifyTokenResponse(){
			return this.token_response;
		}
	}
	
	//PacketWrapperPlayOutTabComplete
	public static class PacketWrapperPlayOutTabComplete extends PacketWrapper {
		private String[] s = new String[0];
		public PacketWrapperPlayOutTabComplete(Object packet) {
			super(packet);
			this.s = (String[]) this.getValue("a");
		}
		public void setText(String... s){
			this.s = s.clone();
			this.setValue("a", this.s);
		}
		public String[] getText(){
			return this.s;
		}
		public List<String> getTextAsList(){
			return Arrays.asList(this.s);
		}
	}
	
}