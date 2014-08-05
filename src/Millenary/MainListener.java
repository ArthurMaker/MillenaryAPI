package Millenary;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import Millenary.Events.SignClickEvent;

public class MainListener implements Listener {
	
	@SuppressWarnings("unused")
	private Plugin api;
	public MainListener (MillenaryAPI plugin){
		this.api = plugin;
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