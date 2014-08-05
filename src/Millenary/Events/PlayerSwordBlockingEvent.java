package Millenary.Events;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;

import Millenary.MillenaryAPI;

/**
 * @author Arthur
 * UNTESTED
 */
public class PlayerSwordBlockingEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private Player player;
	private boolean iscancelled = false;
	private Action action;
	private BlockState block;
	
	public PlayerSwordBlockingEvent (final Player player, final Action action, final BlockState blockstate) {
        this.player = player;
        this.action = action;
        this.block = blockstate;
        Bukkit.getScheduler().scheduleSyncDelayedTask(MillenaryAPI.getInstance(), new Runnable(){
        	public void run(){
        		if(!PlayerSwordBlockingEvent.this.isCancelled()){
        			if(player.isBlocking()){
        				Bukkit.getServer().getPluginManager().callEvent(new PlayerSwordBlockingEvent(player, action, blockstate));
        			}else{
        				PlayerSwordBlockingEvent e = new PlayerSwordBlockingEvent(player, action, blockstate);
        				e.setCancelled(true);
        				Bukkit.getServer().getPluginManager().callEvent(e);
        				return;
        			}
        		}
        	}
        }, 3L);
    }
	
	@Override
	public void setCancelled(boolean b) {
		this.iscancelled = b;
	}
	
	public BlockState getBlockState(){
		return this.block;
	}
	
	@Override
	public boolean isCancelled() {
		return this.iscancelled;
	}
	
	public Action getAction(){
		return this.action;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public boolean isPlayerDefending(){
		return this.player.isBlocking();
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
}