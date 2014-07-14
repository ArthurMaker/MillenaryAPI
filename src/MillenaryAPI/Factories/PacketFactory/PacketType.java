package MillenaryAPI.Factories.PacketFactory;

/**
* Thanks to bigteddy98 for the most lines!
*/
public enum PacketType {
	
	ALL("ALL"),	PacketPlayOutAbilities("PacketPlayOutAbilities"), PacketPlayOutAnimation("PacketPlayOutAnimation"), PacketPlayOutAttachEntity("PacketPlayOutAttachEntity"),
	PacketPlayOutBed("PacketPlayOutBed"), PacketPlayOutBlockAction("PacketPlayOutBlockAction"), PacketPlayOutBlockBreakAnimation("PacketPlayOutBlockBreakAnimation"),
	PacketPlayOutBlockChange("PacketPlayOutBlockChange"), PacketPlayOutChat("PacketPlayOutChat"), PacketPlayOutCloseWindow("PacketPlayOutCloseWindow"),
	PacketPlayOutCollect("PacketPlayOutCollect"), PacketPlayOutCraftProgressBar("PacketPlayOutCraftProgressBar"), PacketPlayOutCustomPayload("PacketPlayOutCustomPayload"),
	PacketPlayOutEntity("PacketPlayOutEntity"),	PacketPlayOutEntityDestroy("PacketPlayOutEntityDestroy"), PacketPlayOutEntityEffect("PacketPlayOutEntityEffect"),
	PacketPlayOutEntityEquipment("PacketPlayOutEntityEquipment"), PacketPlayOutEntityHeadRotation("PacketPlayOutEntityHeadRotation"),
	PacketPlayOutEntityLook("PacketPlayOutEntityLook"), PacketPlayOutEntityMetadata("PacketPlayOutEntityMetadata"), PacketPlayOutEntityStatus("PacketPlayOutEntityStatus"),
	PacketPlayOutEntityTeleport("PacketPlayOutEntityTeleport"), PacketPlayOutEntityVelocity("PacketPlayOutEntityVelocity"), PacketPlayOutExperience("PacketPlayOutExperience"),
	PacketPlayOutExplosion("PacketPlayOutExplosion"), PacketPlayOutGameStateChange("PacketPlayOutGameStateChange"), PacketPlayOutHeldItemSlot("PacketPlayOutHeldItemSlot"),
	PacketPlayOutKeepAlive("PacketPlayOutKeepAlive"), PacketPlayOutKickDisconnect("PacketPlayOutKickDisconnect"), PacketPlayOutLogin("PacketPlayOutLogin"),
	PacketPlayOutMap("PacketPlayOutMap"), PacketPlayOutMapChunk("PacketPlayOutMapChunk"), PacketPlayOutMapChunkBulk("PacketPlayOutMapChunkBulk"),
	PacketPlayOutMultiBlockChange("PacketPlayOutMultiBlockChange"), PacketPlayOutNamedEntitySpawn("PacketPlayOutNamedEntitySpawn"),	PacketPlayOutNamedSoundEffect("PacketPlayOutNamedSoundEffect"),
	PacketPlayOutOpenSignEditor("PacketPlayOutOpenSignEditor"), PacketPlayOutOpenWindow("PacketPlayOutOpenWindow"), PacketPlayOutPlayerInfo("PacketPlayOutPlayerInfo"),
	PacketPlayOutPosition("PacketPlayOutPosition"), PacketPlayOutRelEntityMove("PacketPlayOutRelEntityMove"), PacketPlayOutRelEntityMoveLook("PacketPlayOutRelEntityMoveLook"),
	PacketPlayOutRemoveEntityEffect("PacketPlayOutRemoveEntityEffect"),	PacketPlayOutRespawn("PacketPlayOutRespawn"),
	PacketPlayOutScoreboardDisplayObjective("PacketPlayOutScoreboardDisplayObjective"),	PacketPlayOutScoreboardObjective("PacketPlayOutScoreboardObjective"),
	PacketPlayOutScoreboardScore("PacketPlayOutScoreboardScore"), PacketPlayOutScoreboardTeam("PacketPlayOutScoreboardTeam"), PacketPlayOutSetSlot("PacketPlayOutSetSlot"),
	PacketPlayOutSpawnEntity("PacketPlayOutSpawnEntity"), PacketPlayOutSpawnEntityExperienceOrb("PacketPlayOutSpawnEntityExperienceOrb"),
	PacketPlayOutSpawnEntityLiving("PacketPlayOutSpawnEntityLiving"), PacketPlayOutSpawnEntityPainting("PacketPlayOutSpawnEntityPainting"),
	PacketPlayOutSpawnEntityWeather("PacketPlayOutSpawnEntityWeather"),	PacketPlayOutSpawnPosition("PacketPlayOutSpawnPosition"), PacketPlayOutStatistic("PacketPlayOutStatistic"),
	PacketPlayOutTabComplete("PacketPlayOutTabComplete"), PacketPlayOutTileEntityData("PacketPlayOutTileEntityData"), PacketPlayOutTransaction("PacketPlayOutTransaction"),
	PacketPlayOutUpdateAttributes("PacketPlayOutUpdateAttributes"), PacketPlayOutUpdateHealth("PacketPlayOutUpdateHealth"), PacketPlayOutUpdateSign("PacketPlayOutUpdateSign"),
	PacketPlayOutUpdateTime("PacketPlayOutUpdateTime"), PacketPlayOutWindowItems("PacketPlayOutWindowItems"), PacketPlayOutWorldEvent("PacketPlayOutWorldEvent"),
	PacketPlayOutWorldParticles("PacketPlayOutWorldParticles"),	PacketPlayInAbilities("PacketPlayInAbilities"), PacketPlayInArmAnimation("PacketPlayInArmAnimation"),
	PacketPlayInBlockDig("PacketPlayInBlockDig"), PacketPlayInBlockPlace("PacketPlayInBlockPlace"), PacketPlayInChat("PacketPlayInChat"), PacketPlayInClientCommand("PacketPlayInClientCommand"),
	PacketPlayInCloseWindow("PacketPlayInCloseWindow"), PacketPlayInCustomPayload("PacketPlayInCustomPayload"), PacketPlayInEnchantItem("PacketPlayInEnchantItem"),
	PacketPlayInEntityAction("PacketPlayInEntityAction"), PacketPlayInFlying("PacketPlayInFlying"), PacketPlayInHeldItemSlot("PacketPlayInHeldItemSlot"),
	PacketPlayInKeepAlive("PacketPlayInKeepAlive"),	PacketPlayInLook("PacketPlayInLook"), PacketPlayInPosition("PacketPlayInPosition"), PacketPlayInPositionLook("PacketPlayInPositionLook"),
	PacketPlayInSetCreativeSlot("PacketPlayInSetCreativeSlot"), PacketPlayInSettings("PacketPlayInSettings"), PacketPlayInSteerVehicle("PacketPlayInSteerVehicle"),
	PacketPlayInTabComplete("PacketPlayInTabComplete"), PacketPlayInTransaction("PacketPlayInTransaction"),	PacketPlayInUpdateSign("PacketPlayInUpdateSign"),
	PacketPlayInUseEntity("PacketPlayInUseEntity"), PacketPlayInWindowClick("PacketPlayInWindowClick"), PacketStatusInPing("PacketStatusInPing"), PacketStatusInStart("PacketStatusInStart"),
	PacketStatusOutPong("PacketStatusOutPong"),	PacketStatusOutServerInfo("PacketStatusOutServerInfo"), PacketLoginInEncryptionBegin("PacketLoginInEncryptionBegin");
		
	private String name;
	private PacketType(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}