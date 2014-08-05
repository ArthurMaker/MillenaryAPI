package Millenary.Factories;

import net.minecraft.server.v1_7_R3.EntityBlaze;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.MerchantRecipeList;
import net.minecraft.server.v1_7_R3.NBTTagCompound;

import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftVillager;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;

import Millenary.MillenaryAPI;
import Millenary.Factories.Trader.Offer;

public class MobFactory {
	
	@SuppressWarnings("unused")
	private Plugin api;
	
	public MobFactory (MillenaryAPI api) {
		this.api = api;
	}
	
	public boolean isWitherSkeleton(Entity e){
		if(e instanceof Skeleton) return ((Skeleton)e).getSkeletonType().equals(SkeletonType.WITHER);
		return false;
	}
	
	public void setSkeletonType(Entity e, SkeletonType t){
		if(e instanceof Skeleton) ((Skeleton)e).setSkeletonType(t);
	}
	
	public boolean isBaby(Entity e){
		if(e instanceof Ageable) if(!((Ageable)e).isAdult()) return true;
		return false;
	}
	
	public void setBaby(Entity e){
		if(e instanceof Ageable) if(((Ageable)e).isAdult()) ((Ageable)e).setBaby();
	}
	
	public void setAdult(Entity e){
		if(e instanceof Ageable) if(!((Ageable)e).isAdult()) ((Ageable)e).setAdult();
	}
	
	public void setVillagerProfession(Entity e, Profession p){
		if(e instanceof Villager) if(!((Villager)e).getProfession().equals(p)) ((Villager)e).setProfession(p);
	}
	
	public boolean isVillager(Entity e){
		if(e instanceof Zombie){
			return ((Zombie)e).isVillager();
		}else if(e instanceof Villager){
			return true;
		}
		return false;
	}
	
	public void setVillagerItems(Entity e, Offer[] offers){
		if(e instanceof Villager){
			NBTTagCompound nbt = new NBTTagCompound();
			MerchantRecipeList offerlist = new MerchantRecipeList();
			for(Offer o : offers) offerlist.a(o.toMerchantRecipe());
	        nbt.set("Offers", offerlist.a());
	        ((CraftVillager)e).getHandle().a(nbt);
		}
	}
	
	public void setEntitySpeed(LivingEntity e, double speed){
		((CraftLivingEntity)e).getHandle().getAttributeInstance(GenericAttributes.d).setValue(speed);
	}
	
	public void setBlazeBurning(Blaze blaze, boolean b){
		((EntityBlaze)blaze).a(b);
	}
	
	public void toggleBlazeBurning(Blaze blaze){
		if(((EntityBlaze)blaze).isBurning()){
			((EntityBlaze)blaze).a(false);
		}else{
			((EntityBlaze)blaze).a(true);
		}
	}
	
}