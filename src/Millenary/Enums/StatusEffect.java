package Millenary.Enums;

public enum StatusEffect {
	SPEED("Speed", 1), SLOWNESS("Slowness", 2), HASTE("Haste", 3), MINING_FATIGUE("Mining Fatigue", 4), STRENGTH("Strength", 5), INSTANT_HEALTH("Instant Health", 6),
	INSTANT_DAMAGE("Instant Damage", 7), JUMP_BOOST("Jump Boost", 8), NAUSEA("Nausea", 9), REGENERATION("Regeneration", 10), RESISTANCE("Resistance", 11),
	FIRE_RESISTANCE("Fire Resistance", 12), WATER_BREATHING("Water Breathing", 13), INVISIBILITY("Invisibility", 14), BLINDNESS("Blindness", 15),
	NIGHT_VISION("Night Vision", 16), HUNGER("Hunger", 17), WEAKNESS("Weakness", 18), POISON("Poison", 19), WITHER("Wither", 20), HEALTH_BOOST("Health Boost", 21),
	ABSORPTION("Absorption", 22), SATURATION("Saturation", 23);
	private String s;
	private int i;
	private StatusEffect(String s, int i){
		this.s = s;
		this.i = i;
	}
	public String getName(){
		return this.s;
	}
	public int getId(){
		return this.i;
	}
}