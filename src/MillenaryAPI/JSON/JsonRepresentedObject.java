package MillenaryAPI.JSON;

import java.io.IOException;

import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

interface JsonRepresentedObject {
	
	public void writeJson(JsonWriter writer) throws IOException;

}