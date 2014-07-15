package MillenaryAPI.Factories;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.IMerchant;
import net.minecraft.server.v1_7_R3.MerchantRecipe;
import net.minecraft.server.v1_7_R3.MerchantRecipeList;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.World;

public class Trader implements IMerchant {
	
	private String name = ""; // Você pode colocar um nome padrão aqui para seus traders! :D // You may can use a default name for your traders! :D //
	private MerchantRecipeList list = new MerchantRecipeList();
	private Player p;
	private TraderVillager e;
	private boolean customSounds = false;
	
	public Trader() {}
	public Trader (String name){
		this.name = name;
	}
	
	@Override
	public void a(MerchantRecipe arg0) {
		if(this.p != null) if(this.customSounds) this.p.playSound(this.p.getEyeLocation(), Sound.LEVEL_UP, 0.3F, 0.85F);
	}

	@Override
	public void a_(EntityHuman e) {}

	@Override //item slot (may be buggy)
	public void a_(net.minecraft.server.v1_7_R3.ItemStack arg0){}

	@Override
	public EntityHuman b() {
		return ((CraftPlayer)this.p).getHandle();
	}
	
	@Override
	public MerchantRecipeList getOffers(EntityHuman arg0) {
		return this.list;
	}
	
	public MerchantRecipeList getMerchantRecipeList(){
		return this.list;
	}
	
	public Trader setName(String s){
		this.name = s;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Trader addOffer(Offer offer){
		this.list.add(offer.toMerchantRecipe());
		return this;
	}
	
	public Trader customSounds(boolean b){
		this.customSounds = b;
		return this;
	}
	
	public boolean customSounds(){
		return this.customSounds;
	}
	
	@SuppressWarnings("unchecked")
	public Trader addOffers(Offer[] offers){
		for(Offer o : offers){
			this.list.add(o.toMerchantRecipe());
		}
		return this;
	}
	
	public void openGUI(Player player){
		this.p = player;
		((CraftPlayer)player).getHandle().openTrade(this, this.name);
	}
	
	public TraderVillager spawn(Location l, boolean visibleName){
		TraderVillager v = new TraderVillager((((CraftWorld) l.getWorld()).getHandle()));
		v.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		((CraftWorld) l.getWorld()).getHandle().addEntity(v);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.set("Offers", this.list.a());
		v.a(nbt);
		if(this.name != null || !this.name.equalsIgnoreCase("")) v.setCustomName(this.name);
		if(this.name != null || !this.name.equalsIgnoreCase("")) if(visibleName) v.setCustomNameVisible(true);
		this.e = v;
		return this.e;
	}
	
	public static class Offer {
		private ItemStack i1;
		private ItemStack i2;
		private ItemStack i3;
		public Offer(ItemStack price, ItemStack product){
			this.i1 = price;
			this.i2 = null;
			this.i3 = product;
		}
		public Offer(ItemStack price1, ItemStack price2, ItemStack product){
			this.i1 = price1;
			this.i2 = price2;
			this.i3 = product;
		}
		public MerchantRecipe toMerchantRecipe(){
			if(this.i2 == null) return new MerchantRecipe(CraftItemStack.asNMSCopy(this.i1), CraftItemStack.asNMSCopy(this.i3));
			return new MerchantRecipe(CraftItemStack.asNMSCopy(this.i1), CraftItemStack.asNMSCopy(this.i2), CraftItemStack.asNMSCopy(this.i3));
		}
	}
	
	public static class TraderVillager extends EntityVillager {
		private boolean bw;
		private boolean freeze = false;
		public TraderVillager(World w) {
			super(w);
		}
		public void a(MerchantRecipe merchantrecipe) {
			this.bw(false);
		}
		public boolean bw() {
			return bw;
		}
		public void bw(boolean bw) {
			this.bw = bw;
		}
		@SuppressWarnings("deprecation")
		public void setProfession(Profession p){
			this.setProfession(p.getId());
		}
		@SuppressWarnings("deprecation")
		public Profession getVillagerProfession(){
			return Profession.getProfession(this.getProfession());
		}
		public void g(double x, double y, double z){
			if(this.freeze) return;
		}
	}
	
}