package MillenaryAPI;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;

import MillenaryAPI.Events.SignClickEvent;

public class MainListener implements Listener {
	
	private Plugin api;
	
	public MainListener (MillenaryAPI plugin){
		this.api = plugin;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		if(this.api.getServer().hasWhitelist()){
			if((!event.getPlayer().isOp()) && (!this.api.getServer().getWhitelistedPlayers().contains(event.getPlayer().getName().toLowerCase().trim()))){
				event.disallow(Result.KICK_OTHER, "§cThis server is closed at the moment.\n§rWe will come back as soon as we can!\n\n\n\n\n\n\n§6www.yourwebsitehere.com");
										    	//"§cEste servidor está fechado para o público no momento.\n§rVoltaremos assim que pudermos!\n\n\n\n\n\n\n§6www.seusiteaqui.com"
				return;
			}
		}
		//MillenaryAPI.registerPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onSignInteract(PlayerInteractEvent event){
		if(event.isCancelled()) return;
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			if(event.getClickedBlock().getState() instanceof Sign){
				SignClickEvent e = new SignClickEvent((Sign)event.getClickedBlock().getState(), event.getPlayer(), event.getAction());
				Bukkit.getServer().getPluginManager().callEvent(e);
				if(e.isCancelled()) event.setCancelled(true);
			}
		}
	}
	
	//private String getIp(Player player){
	//	return player.getAddress().getAddress().getHostName().trim();
	//}
	
}