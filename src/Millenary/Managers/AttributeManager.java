package Millenary.Managers;
 
import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagList;

import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.google.common.base.Preconditions;
 
public class AttributeManager {
	
	public static enum AttributeType {
		/** Define a quantidade de vida da entidade de acordo com o número dado.
		 * Meio coração (vida) equivale à 0.5D. */
		MAX_HEALTH("generic.maxHealth"),
		/** Define a distância em blocos em que a entidade pode encontrar outras entidades. */
		FOLLOW_RANGE("generic.followRange"),
		/** Define a velocidade da entidade.
		 * Padrão: 0.7D */
		MOVEMENT_SPEED("generic.movementSpeed"),
		/** Define o quão resistente a entidade é em relação à knockbacks(repulsões).
		 * 0D = sem qualquer resistência (mín.)
		 * 1D = resistência completa. (máx.)
		 */
		KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		/** Define a quantidade de vida (em corações) será retirada quando a entidade receber dano de outra entidade do tipo Damageable.
		 */
		ATTACK_DAMAGE("generic.attackDamage"),
		/** Define a força do pulo de um cavalo.
		 * Padrão: 0.7D
		 */
		JUMP_STRENGTH("horse.jumpStrength"),
		/** Define se um zumbi deve "chamar reforços" zumbis ao receber dano.
		 * (isto apenas fará que outro zumbi nasça ao lado da entidade)
		 * 
		 * Valores:
		 * 0D = não há "reforços" para o zumbi.
		 * 1D = há "reforços" para o zumbi.
		 */
		SPAWN_REINFORCEMENTS("zombie.spawnReinforcements");
		private String minecraftID;
		private AttributeType(String minecraftID){
			this.minecraftID = minecraftID;
		}
		public String getMinecraftId(){
			return this.minecraftID;
		}
	}
	
	public static enum AttributeOperation {
		/** Adicionará um <tt>valor</tt> ao <tt>resultado final</tt>.
		 * Será aplicado antes de qualquer outra operação. */
		ADD_NUMBER(0),
		/** Adicionará <tt>valor * base</tt> ao <tt>resultado final</tt>.
		 * Será aplicado apenas depois de todos os {@link #ADD_NUMBER} serem aplicados e antes de qualquer {@link #ADD_PERCENTAGE}. */
		MULTIPLY_PERCENTAGE(1),
		/** Adicionará <tt>resultado final * (1+valor)</tt> ao <tt>resultado final<tt>.
		 * Será aplicado depois de todas as outras operações.
		 */
		ADD_PERCENTAGE(2);
		private int id = 0;
		private AttributeOperation(int id){
			this.id = id;
		}
		public int getId(){
			return this.id;
		}
	}
	
	public static class Attribute {
		private AttributeType type;
		private AttributeOperation operation;
		private double amount;
		private UUID uuid;
		public Attribute (AttributeType type, AttributeOperation operation, double value, UUID uuid){
			this.type = type;
			this.operation = operation;
			this.amount = value;
			this.uuid = uuid;
		}
		public Attribute (AttributeType type, double value, UUID uuid){
			this(type, AttributeOperation.ADD_NUMBER, value, uuid);
		}
		public Attribute(AttributeType type, AttributeOperation operation, double value){
			this(type, operation, value, UUID.randomUUID());
		}
		public Attribute (AttributeType type, double value){
			this(type, AttributeOperation.ADD_NUMBER, value, UUID.randomUUID());
		}
		public Attribute setType(AttributeType type){
			this.type = type;
			return this;
		}
		public Attribute setOperation(AttributeOperation operation){
			this.operation = operation;
			return this;
		}
		public Attribute setValue(double v){
			this.amount = v;
			return this;
		}
		public Attribute setUUID(UUID uuid){
			this.uuid = uuid;
			return this;
		}
		public AttributeType getType(){
			return this.type;
		}
		public AttributeOperation getOperation(){
			return this.operation;
		}
		public double getValue(){
			return this.amount;
		}
		public UUID getUUID(){
			return this.uuid;
		}
		public NBTTagCompound write() throws InstantiationException, IllegalAccessException{
			Preconditions.checkNotNull(this.type, "O tipo de atributo não pode ser null!");
			if(this.operation == null) this.operation = AttributeOperation.ADD_NUMBER;
			if(this.uuid == null) this.uuid = UUID.randomUUID();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("AttributeName", this.type.minecraftID);
			tag.setString("Name", this.type.getMinecraftId());
			tag.setInt("Operation", this.operation.getId());
			tag.setDouble("Amount", this.amount);
			tag.setLong("UUIDMost", this.uuid.getMostSignificantBits());
			tag.setLong("UUIDLeast", this.uuid.getLeastSignificantBits());
			return tag;
		}
	}
	
	public static class AttributeList extends ArrayList<Attribute> {
		private static final long serialVersionUID = 1L;
		public void add(Attribute... attributes){
			for(Attribute a : attributes) this.add(a);
		}
		public ItemStack apply(ItemStack i, boolean replaceAll){
			try{
				net.minecraft.server.v1_7_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(i);
				NBTTagCompound tag = itemstack.getTag();
				NBTTagList list = replaceAll ? new NBTTagList() : tag.getList("AttributeModifiers", 10);
				for(Attribute attribute : this) list.add(attribute.write());
				tag.set("AttributeModifiers", list);
				itemstack.setTag(tag);
				return CraftItemStack.asCraftMirror(itemstack);
			}catch (InstantiationException | IllegalAccessException e){
				//ex.printStackTrace();
				return i;
			}
		}
	}
	
	public static ItemStack addAttribute(ItemStack i, Attribute a, AttributeOperation o, double v, boolean replaceAll){
		try {
			net.minecraft.server.v1_7_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(i);
			NBTTagCompound tag = itemstack.getTag();
			NBTTagList list = replaceAll ? new NBTTagList() : tag.getList("AttributeModifiers", 10);
			list.add(a.write());
			tag.set("AttributeModifiers", list);
			itemstack.setTag(tag);
			return CraftItemStack.asCraftMirror(itemstack);
		} catch (InstantiationException | IllegalAccessException e) {
			return i;
		}
	}
	
	public static ItemStack removeAttributes(ItemStack i){
		try {
			net.minecraft.server.v1_7_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(i);
			NBTTagCompound tag = itemstack.getTag();
			tag.set("AttributeModifiers", new NBTTagList());
			itemstack.setTag(tag);
			return CraftItemStack.asCraftMirror(itemstack);
		} catch (Exception e) {
			return i;
		}
	}
	
	public static ItemStack removePotionAttributes(ItemStack i){
		if(!(i.getItemMeta() instanceof PotionMeta)) return i;
		try {
			net.minecraft.server.v1_7_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(i);
			NBTTagCompound tag = itemstack.getTag();
			tag.set("CustomPotionEffects", new NBTTagList());
			itemstack.setTag(tag);
			return CraftItemStack.asCraftMirror(itemstack);
		} catch (Exception e) {
			return i;
		}
	}
	
}