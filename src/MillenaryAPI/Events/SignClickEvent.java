package MillenaryAPI.Events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

public class SignClickEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Sign sign;
	private Player player;
	private Action action;
	
	public SignClickEvent(Sign sign, Player player, Action action){
		this.sign = sign;
		this.player = player;
		this.action = action;
	}
	
	public Sign getSign(){
		return this.sign;
	}
	
	public Action getAction(){
		return this.action;
	}
	
	public Location getSignLocation(){
		return this.sign.getLocation();
	}
	
	public Block getSignBlock(){
		return this.sign.getBlock();
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public String[] getLines(){
		return this.sign.getLines();
	}
	
	public void setLine(int i, String s){
		this.sign.setLine(i, s);
	}
	
	public String getLine(int i){
		return this.sign.getLine(i);
	}
	
	public void setLines(String... lines){
		for(int i = 0; i < lines.length; i++){
			this.sign.setLine(i, lines[i]);
		}
	}
	
	public void updateSign(){
		this.sign.update();
	}
	
	@SuppressWarnings("deprecation")
	public void setTypeAndData(Material type, int data){
		this.sign.setData(new MaterialData(type, (byte)data));
		this.sign.update(true, false);
	}
	
	public World getWorld(){
		return this.sign.getWorld();
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}