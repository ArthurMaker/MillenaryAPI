package Millenary.Factories.PacketFactory;

import java.util.Random;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import Millenary.Events.PacketReceiveEvent;
import Millenary.Factories.PacketFactory.PacketFactory.PacketHandler;
import Millenary.Factories.PacketFactory.Wrappers.PacketWrapperStatusOutServerInfo;

public class MySexyPacketListener implements PacketListener {
	
	private String[] motds = {"MOTD 1", "MOTD 2", "Free diamonds NOW, bro! ...not."};
	
	@PacketHandler (PacketType = PacketType.PacketStatusOutServerInfo)
	public void onPacketReceive(PacketReceiveEvent event){
		PacketWrapperStatusOutServerInfo w = new PacketWrapperStatusOutServerInfo(event.getPacket());
		//A random MOTD using it will be pretty cool, hu? So let's do it!
		w.setMOTD(this.motds[new Random().nextInt(this.motds.length-1)]);
		//We also can also hide or even modify the quantity of players in our servers!
		w.setMinPlayers(9001); // omg, it is over nine thousand!
		w.setMaxPlayers(69); // why not? :9
		String[] msg = {"Hey, this is a modified", "playercount for your", "server! :D", "", "§cYou can also use §ecolors §btoo!"};
		GameProfile[] gp = new GameProfile[msg.length];
		for(int j = 0; j < msg.length; j++) gp[j] = new GameProfile(UUID.randomUUID(), msg[j]);
		w.setOnlinePlayers(gp);
	}
	
}