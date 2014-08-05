package Millenary.Factories;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import Millenary.MillenaryAPI;
import Millenary.NMS.HNEntity;

public class RandomFactory {
	
	private Map<UUID, HNEntity> hnbats = new HashMap<UUID, HNEntity> ();
	
	@SuppressWarnings("unused")
	private MillenaryAPI api;
	public RandomFactory(MillenaryAPI api){
		this.api = api;
	}
	
	public void hidePlayerName(Player p, boolean b){
		this.getHNBat(p).setOnPlayer(b);
	}
	
	public void togglePlayerName(Player p){
		this.getHNBat(p).toggle();
	}
	
	private HNEntity getHNBat(Player p){
		HNEntity e = hnbats.get(p.getUniqueId());
		if(e == null){
			e = new HNEntity(p);
			hnbats.put(p.getUniqueId(), e);
		}
		return e;
	}
	
}
