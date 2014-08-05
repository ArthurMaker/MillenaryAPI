package Millenary.Factories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import Millenary.MillenaryAPI;

public class InventoryFactory {
	
	@SuppressWarnings("unused")
	private MillenaryAPI api;
	public InventoryFactory (MillenaryAPI api) {
		this.api = api;
	}
	
	public String inventoryToString(Inventory inv){
		YamlConfiguration yc = new YamlConfiguration();
		yc.set("nome", inv.getTitle());
		yc.set("tamanho", inv.getSize());
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item != null) yc.set("conteudo." + i, item);
		}
		return Base64Coder.encodeString(yc.saveToString());
	}
	
	@SuppressWarnings("deprecation")
	public String playerInventoryToString(Player p, boolean fullInfo){
		YamlConfiguration yc = new YamlConfiguration();
		for(int i = 0; i < p.getInventory().getSize(); i++){
			ItemStack item = p.getInventory().getItem(i);
			if(item != null) yc.set("conteudo." + i, item);
		}
		for(int i = 0; i < p.getInventory().getArmorContents().length; i++){
			ItemStack item = p.getInventory().getArmorContents()[i];
			if(item != null) yc.set("armadura." + i, item);
		}
		if(fullInfo){
			if(p.getActivePotionEffects() != null){
				int potions = 0;
				for(PotionEffect pe : p.getActivePotionEffects()){
					if(pe != null){
						yc.set("effects." + potions + ".type", pe.getType().getId());
						yc.set("effects." + potions + ".amplifier", pe.getAmplifier());
						yc.set("effects." + potions + ".duration", pe.getDuration());
						potions++;
					}
				}
			}
			yc.set("maxhealth", Double.valueOf(p.getMaxHealth()));
			yc.set("health", Double.valueOf(p.getHealth()));
			yc.set("foodlevel", Integer.valueOf(p.getFoodLevel()));
			yc.set("exhaustion", Double.valueOf((double)p.getExhaustion()));
			yc.set("saturation", Double.valueOf((double)p.getSaturation()));
			yc.set("fireticks", Integer.valueOf(p.getFireTicks()));
			yc.set("falldistance", Double.valueOf((double)p.getFallDistance()));
			yc.set("level", Integer.valueOf(p.getLevel()));
			yc.set("expbar", Double.valueOf((double)p.getExp()));
			yc.set("remainingair", Integer.valueOf(p.getRemainingAir()));
		}
		return Base64Coder.encodeString(yc.saveToString());
	}
	
	public Inventory stringToInventory(String s){
		YamlConfiguration yc = new YamlConfiguration();
		try{
			yc.loadFromString(Base64Coder.decodeString(s));
			Inventory inv = Bukkit.createInventory(null, yc.getInt("tamanho"), yc.getString("nome"));
			ConfigurationSection contents = yc.getConfigurationSection("conteudo");
			for(String i : contents.getKeys(false)){
				inv.setItem(Integer.parseInt(i), contents.getItemStack(i));
			}
			return inv;
		}catch (InvalidConfigurationException e){
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stringToPlayerInventory(String s, Player p, boolean fullInfo){
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		if(fullInfo){
			for(PotionEffect pe : p.getActivePotionEffects()){
				p.removePotionEffect(pe.getType());
			}
			p.setFoodLevel(20);
			p.setFallDistance(0F);
			p.setExhaustion(0F);
			p.setFireTicks(0);
			p.setLevel(0);
			p.setExp(0F);
			p.setSaturation(0F);
			p.setMaxHealth(20D);
			p.setHealth(20D);
			p.setRemainingAir(300);
		}
		YamlConfiguration yc = new YamlConfiguration();
		try{
			yc.loadFromString(Base64Coder.decodeString(s));
			ConfigurationSection contents = yc.getConfigurationSection("conteudo");
			for(String i : contents.getKeys(false)){
				p.getInventory().setItem(Integer.parseInt(i), contents.getItemStack(i));
			}
			ConfigurationSection armor = yc.getConfigurationSection("armadura");
			for(String i : contents.getKeys(false)){
				int slot = Integer.parseInt(i);
				if(slot == 0){
					p.getInventory().setBoots(armor.getItemStack(i));
				}else if(slot == 1){
					p.getInventory().setLeggings(armor.getItemStack(i));
				}else if(slot == 2){
					p.getInventory().setChestplate(armor.getItemStack(i));
				}else if(slot == 3){
					p.getInventory().setHelmet(armor.getItemStack(i));
				}
			}
			if(fullInfo){
				if(yc.contains("effects")){
					ConfigurationSection potions = yc.getConfigurationSection("effects");
					for(String i : potions.getKeys(false)){
						int id = Integer.valueOf(potions.getInt(i + ".type"));
						int amplifier = Integer.valueOf(potions.getInt(i + ".amplifier"));
						int duration = Integer.valueOf(potions.getInt(i + ".duration"));
						p.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), duration, amplifier));
					}
				}
				if(yc.contains("maxhealth")) p.setMaxHealth(yc.getDouble("maxhealth"));
				if(yc.contains("health")) p.setHealth(yc.getDouble("health"));
				if(yc.contains("foodlevel")) p.setFoodLevel(yc.getInt("foodlevel"));
				if(yc.contains("exhaustion")) p.setExhaustion(Float.valueOf((float)yc.getDouble("exhaustion")));
				if(yc.contains("saturation")) p.setSaturation(Float.valueOf((float)yc.getDouble("saturation")));
				if(yc.contains("fireticks")) p.setFireTicks(yc.getInt("fireticks"));
				if(yc.contains("falldistance")) p.setFallDistance(Float.valueOf((float)yc.getDouble("falldistance")));
				if(yc.contains("level")) p.setLevel(yc.getInt("level"));
				if(yc.contains("expbar")) p.setExp(Float.valueOf((float)yc.getDouble("expbar")));
				if(yc.contains("remainingair")) p.setRemainingAir(yc.getInt("remainingair"));
			}
		}catch (InvalidConfigurationException e){
			p.sendMessage("§cOcorreu um erro ao carregar o inventário.");
		}
	}
	
	public int getItemCount(Inventory inv, Material material){
		int count = 0;
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item != null) if(item.getType().equals(material)) count+=item.getAmount();
		}
		return count;
	}
	
	public boolean hasFreeSlot(Inventory inv){
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item == null) return true;
		}
		return false;
	}
	
	public int getFreeSlots(Inventory inv){
		int count = 0;
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item == null) count++;
		}
		return count;
	}
	
	public int getItemCount(Inventory inv, ItemStack itemstack){
		int count = 0;
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item != null) if(item.getType().equals(itemstack.getType()) && item.getItemMeta().equals(itemstack.getItemMeta())) count+=item.getAmount();
		}
		return count;
	}
	
	public boolean containsAtLeast(Inventory inv, Material material, int min){
		if(getItemCount(inv, material) >= min) return true;
		return false;
	}
	
	public boolean containsAtLeast(Inventory inv, ItemStack itemstack, int min){
		if(getItemCount(inv, itemstack) >= min) return true;
		return false;
	}
	
	public void closeInventory(Inventory inv){
		for(HumanEntity e : inv.getViewers()){
			e.closeInventory();
		}
	}
	
}