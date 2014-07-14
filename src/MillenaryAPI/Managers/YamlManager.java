package MillenaryAPI.Managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlManager {
	
	/**
	 * @param filePath (example: 'plugins/YourPlugin/random_file.yml')
	 * @return
	 */
	public static YamlConfiguration getConfig(String filePath){
		File file = new File(filePath);
		if(!file.exists()){
			try {
				file.mkdir();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public static boolean exists(String filePath){
		return new File(filePath).exists();
	}
	
	public static void delete(String filePath){
		new File(filePath).delete();
	}
	
	//======================================//
	
}