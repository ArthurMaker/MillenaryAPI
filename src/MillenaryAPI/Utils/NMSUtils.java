package MillenaryAPI.Utils;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;

public class NMSUtils {
	
	private static Field gf;
	static{ gf = getField(PathfinderGoalSelector.class, "a"); }
	
	public static void clearGoals(PathfinderGoalSelector... g){
		if(gf == null || g == null) return;
		for (PathfinderGoalSelector selector : g) {
			try{
				List<?> list = (List<?>) gf.get(selector);
				list.clear();
			}catch (Exception e){
				//:c
			}
		}
	}
	
	public static Field getField(Class<?> c, String field){
		Field f = null;
		try{
			f = c.getDeclaredField(field);
			f.setAccessible(true);
	    }catch (Exception e){
	    	//:c
	    }
		return f;
	}
	
}